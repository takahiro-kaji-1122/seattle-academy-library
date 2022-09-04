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

        try {
            BufferedReader bufferedReaderUploadFile = new BufferedReader(
                    new InputStreamReader(uploadFile.getInputStream(), StandardCharsets.UTF_8));

            //ファイルの形式を確認
            //ファイルの各行を一時的に格納する変数を定義
            String line;
            //bufferedReaderUploadFileの最終行までループ
            while ((line = bufferedReaderUploadFile.readLine()) != null) {
                //1行のコンマの数が5個か
                if (StringUtils.countMatches(line, ",") != 5) {
                    //エラーを表示
                    String commaNumError = "入力内容に「,」が含まれている。もしくは、７列目以降に値が入力されています。書籍情報が正しく登録されないため、「,」を削除、もしくは７列目以降の記入を削除してください。";
                    model.addAttribute("errorMsg", commaNumError);
                    return "addCollectiveBooks";
                }
            }
            fileErrorInfoList = checkFileValidation(bufferedReaderUploadFile);
            //fileErrorInfoListにエラーの記載があるか
            if (CollectionUtils.isNotEmpty(fileErrorInfoList)) {
                //記載がある場合、エラーを表示
                model.addAttribute("errorMsg", fileErrorInfoList);
                return "addCollectiveBooks";
            }
        } catch (IOException e) {
            model.addAttribute("errorMsg", "ファイルの読み込み時にエラーが発生しました。時間を置いてから再度実施ください。");
            return "addCollectiveBooks";
        }
        return "home";
    }

    /**
     * アップロードされたファイルのバリデーションチェック
     * @param bufferedReaderUploadFile アップロードされたファイルのBufferedReader
     * @return fileErrorInfoList バリデーションチェックに引っかかった箇所のリスト
     * @throws IOException 
     */
    public ArrayList<FileErrorInfo> checkFileValidation(BufferedReader bufferedReaderUploadFile) throws IOException {
        ArrayList<FileErrorInfo> fileErrorInfoList = new ArrayList<FileErrorInfo>();
        //バリデーションチェックのエラー文言を定義
        String requiredItemError = "必須項目です。入力してください。";
        String publishDateFormatError = "YYYYMMDD形式で入力してください。";
        String isbmFormatError = "半角数字で10桁もしくは13桁で入力してください。";

        //ファイルの行数を格納するlineNumを定義
        long lineNum = 0;

        //各行を一時的に格納するlineを定義
        String line;
        //ファイルの最終行までループ
        while ((line = bufferedReaderUploadFile.readLine()) != null) {
            //行数に１追加
            lineNum++;

            //１行目のチェックの場合
            if (lineNum == 1) {
                //バリデーションチェックをスキップ
                continue;
            }
            //lineを,ごとに区切って配列に格納
            String[] split = line.split(",");

            //書籍名が入力されていない場合
            if (StringUtils.isEmpty(split[0])) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "書籍名は", requiredItemError));
            }

            //著者名が入力されていない場合
            if (StringUtils.isEmpty(split[1])) {
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "著者名は", requiredItemError));
            }
            //出版社が入力されていない場合
            if (StringUtils.isEmpty(split[2])) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版社は", requiredItemError));
            }
            //出版日が入力されていない場合
            if (StringUtils.isEmpty(split[3])) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版日は", publishDateFormatError));
            }
            //出版日がYYYYMMDD形式で入力されていない場合newBookDetailsInfo.getPublishDate().matches("^[0-9]{8,8}$")
            if (!split[3].matches("^[0-9]{8,8}$")) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "出版日は", publishDateFormatError));
            }
            //ISBNが半角数字10桁もしくは半角数字13桁もしくは空白ではない場合
            if (!split[5].matches("^[0-9]{10,10}$") ||
                    !split[5].matches("^[0-9]{13,13}$") ||
                    StringUtils.isEmpty(split[5])) {
                //fileErrorInfoListにエラー内容を追記
                fileErrorInfoList.add(new FileErrorInfo(lineNum, "ISBNは", isbmFormatError));
            }
        }

        return fileErrorInfoList;
    }

}
