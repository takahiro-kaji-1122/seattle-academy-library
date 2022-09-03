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
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
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
            fileErrorInfoList = checkFileFormat(bufferedReaderUploadFile);
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

    public ArrayList<FileErrorInfo> checkFileFormat(BufferedReader bufferedReaderUploadFile) throws IOException {

        //行数カウント用の変数を定義
        long lineNum = 1;

        //エラー内容を格納するリストを定義
        ArrayList<FileErrorInfo> fileErrorInfoList = new ArrayList<FileErrorInfo>();

        //ファイルの各行を一時的に格納する変数を定義
        String line;

        //コンマの数でエラーの場合のエラー文言を定義
        String commaNumError = "入力内容に「,」が含まれている。もしくは、７列目以降に値が入力されています。書籍情報が正しく登録されないため、「,」を削除、もしくは７列目以降の記入を削除してください。";

        //bufferedReaderUploadFileの最終行までループ
        while ((line = bufferedReaderUploadFile.readLine()) != null) {
            System.out.println(StringUtils.countMatches(line, ","));
            //1行のコンマの数が5個か
            if (StringUtils.countMatches(line, ",") != 5) {
                //1行のコンマの数が5個でない場合は、エラーに追加
                fileErrorInfoList.add(new FileErrorInfo(lineNum, commaNumError));
            }
            lineNum++;
        }

        return fileErrorInfoList;
    }

}
