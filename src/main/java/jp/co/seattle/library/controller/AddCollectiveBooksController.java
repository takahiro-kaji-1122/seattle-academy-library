package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.FileErrorInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddCollectiveBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddCollectiveBooksController.class);

    @Autowired
    private BooksService booksService;

    @RequestMapping(value = "/addCollectiveBooks", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addCollectiveBooks";
    }

    /**
     * CSVファイルで書籍情報を登録する
     * @param file アップロードファイル
     */
    @Transactional
    @RequestMapping(value = "/insertCollectiveBooks", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("file") MultipartFile uploadFile,
            Model model) {
        logger.info("Welcome addCollectiveBooks.java! The client locale is {}.", locale);

        //アップロードされたファイルがCSVファイルか        
        if (!StringUtils.equals("csv", StringUtils.substringAfterLast(uploadFile.getOriginalFilename(), "."))) {
            model.addAttribute("errorMsg", "CSVファイルをアップロードしてください。");
            return "addCollectiveBooks";
        }

        //ファイルの中身が空じゃないことを確認
        if (uploadFile.isEmpty()) {
            model.addAttribute("errorMsg", "ファイルが空のようです。書籍データを記載したファイルをアップロードしてください。");
            return "addCollectiveBooks";
        }

        //エラー内容を格納するリストを定義
        ArrayList<FileErrorInfo> fileErrorInfoList = new ArrayList<FileErrorInfo>();
        //アップロードされたファイルの書籍情報を格納する
        ArrayList<BookDetailsInfo> bookDtailsInfoList = new ArrayList<BookDetailsInfo>();

        try {
            BufferedReader bufferedReaderUploadFile = new BufferedReader(
                    new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8));

            //ファイルの各行を一時的に格納する変数を定義
            String bookInfoLine;
            //bufferedReaderUploadFileの最終行までループ
            while ((bookInfoLine = bufferedReaderUploadFile.readLine()) != null) {
                //ファイルの形式チェック
                //1行のコンマの数が5個か
                if (StringUtils.countMatches(bookInfoLine, ",") != 5) {
                    //エラーを表示
                    String commaNumError = "入力内容に「,」が含まれている。もしくは、７列目以降に値が入力されています。書籍情報が正しく登録されないため、「,」を削除、もしくは７列目以降の記入を削除してください。";
                    model.addAttribute("errorMsg", commaNumError);
                    return "addCollectiveBooks";
                }

                //Excelなどで編集したCSVファイルの場合、改行コードが最後に残ってしまっているので削除
                bookInfoLine = bookInfoLine.trim();
                //要素６つの書籍情報の格納先を定義
                String[] bookInfoSplit = new String[5];
                //bookInfoLineを,ごとに区切って配列に格納
                bookInfoSplit = bookInfoLine.split(",");
                BookDetailsInfo addBookDetailsInfo = new BookDetailsInfo();
                try {
                    addBookDetailsInfo.setTitle(bookInfoSplit[0]);
                    addBookDetailsInfo.setAuthor(bookInfoSplit[1]);
                    addBookDetailsInfo.setPublisher(bookInfoSplit[2]);
                    addBookDetailsInfo.setPublishDate(bookInfoSplit[3]);
                    addBookDetailsInfo.setDescription(bookInfoSplit[4]);
                    addBookDetailsInfo.setIsbn(bookInfoSplit[5]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //bookInfoSplitに要素が6こない場合
                    //何もしない
                }
                //bookDtailsInfoListにこの行の内容を格納
                bookDtailsInfoList.add(addBookDetailsInfo);
            }

        } catch (IOException e) {
            model.addAttribute("errorMsg", "ファイルの読み込み時にエラーが発生しました。時間を置いてから再度実施ください。");
            return "addCollectiveBooks";
        }
        //入力内容のバリデーションチェック

        fileErrorInfoList = checkFileValidation(bookDtailsInfoList);
        //fileErrorInfoListにエラーの記載があるか
        if (CollectionUtils.isNotEmpty(fileErrorInfoList)) {

            //記載がある場合、エラーを表示
            model.addAttribute("fileErrorInfoList", fileErrorInfoList);
            return "addCollectiveBooks";
        }
        //ファイルの書籍データを登録
        fileErrorInfoList = insertBooks(bookDtailsInfoList);
        //fileErrorInfoListにエラーの記載があるか
        if (CollectionUtils.isNotEmpty(fileErrorInfoList)) {
            //記載がある場合、エラーを表示
            model.addAttribute("fileErrorInfoList", fileErrorInfoList);
            return "addCollectiveBooks";
        }
        return "redirect:/home";
    }

    /**
     * アップロードされたファイルのバリデーションチェック
     * @param bookDtailsInfoList アップロードされたファイルに記載された書籍情報のリスト
     * @return fileErrorInfoList バリデーションチェックに引っかかった箇所のリスト
     */
    public ArrayList<FileErrorInfo> checkFileValidation(ArrayList<BookDetailsInfo> bookDtailsInfoList) {
        //エラー内容を記録するfileErrorInfoを定義
        ArrayList<FileErrorInfo> fileErrorInfoList = new ArrayList<FileErrorInfo>();
        //バリデーションチェックのエラー文言を定義
        String requiredItemError = "必須項目です。入力してください。";
        String publishDateFormatError = "YYYYMMDD形式で入力してください。";
        String isbmFormatError = "半角数字で10桁もしくは13桁で入力してください。";

        //ファイルの行数を格納するlineNumを定義
        long lineNum = 0;

        //ファイルの最終行までループ
        for (BookDetailsInfo bookDtailsInfo : bookDtailsInfoList) {

            //行数に１追加
            lineNum++;

            //１行目のチェックの場合
            if (lineNum == 1) {
                //バリデーションチェックをスキップ   
                continue;
            }

            //書籍名が入力されていない場合
            if (StringUtils.isEmpty(bookDtailsInfo.getTitle())) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "書籍名は", requiredItemError));
            }
            //著者名が入力されていない場合
            if (StringUtils.isEmpty(bookDtailsInfo.getAuthor())) {
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "著者名は", requiredItemError));
            }
            //出版社が入力されていない場合
            if (StringUtils.isEmpty(bookDtailsInfo.getPublisher())) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版社は", requiredItemError));
            }
            //出版日が入力されていない場合
            if (StringUtils.isEmpty(bookDtailsInfo.getPublishDate())) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版日は", requiredItemError));
            }
            //出版日が入浴されているがYYYYMMDD形式で入力されていない場合
            if (StringUtils.isNotEmpty(bookDtailsInfo.getPublishDate())
                    && !bookDtailsInfo.getPublishDate().matches("^[0-9]{8,8}$")) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版日は", publishDateFormatError));
            }
            //ISBNが入力されていて、10桁もしくは13桁ではない場合
            if (StringUtils.isNotEmpty(bookDtailsInfo.getIsbn())
                    && !bookDtailsInfo.getIsbn().matches("^[0-9]{10,10}$")
                    && !bookDtailsInfo.getIsbn().matches("^[0-9]{13,13}$")) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "ISBNは", isbmFormatError));
            }
        }
        return fileErrorInfoList;
    }

    /**
     * アップロードされたファイルの書籍情報をDBに登録
     * @param bookDtailsInfoList アップロードされたファイルに記載された書籍情報のリスト
     * @return fileErrorInfoList エラー箇所のリスト
     */
    public ArrayList<FileErrorInfo> insertBooks(ArrayList<BookDetailsInfo> bookDtailsInfoList) {
        //エラー内容を記録するfileErrorInfoを定義
        ArrayList<FileErrorInfo> fileErrorInfoList = new ArrayList<FileErrorInfo>();

        //DB登録失敗のエラー文言を定義
        String insertError = "この行の書籍の登録ができませんでいた。時間を置いてから、これ以降の行の登録を再度行ってください。";

        //ファイルの行数を格納するlineNumを定義
        long lineNum = 0;

        //最終行までループ
        for (BookDetailsInfo bookDtailsInfo : bookDtailsInfoList) {
            //行数に１追加
            lineNum++;
            //１行目のチェックの場合
            if (lineNum == 1) {
                //登録処理をスキップ
                continue;
            }

            try {
                //処理中の行の書籍情報をDBに登録
                booksService.registBook(bookDtailsInfo);
            } catch (Exception e) {
                //fileErrorInfoListにエラーを追加
                fileErrorInfoList.add(new FileErrorInfo(lineNum, insertError));
                //ループの終了
                break;
            }
        }
        return fileErrorInfoList;
    }
}
