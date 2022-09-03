package jp.co.seattle.library.controller;

import java.util.Locale;

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
        System.out.println(StringUtils.substringAfterLast(uploadFile.getOriginalFilename(), "."));
        System.out.println(
                StringUtils.equals("csv", StringUtils.substringAfterLast(uploadFile.getOriginalFilename(), ".")));
        if (!StringUtils.equals("csv", StringUtils.substringAfterLast(uploadFile.getOriginalFilename(), "."))) {
            model.addAttribute("errorMsg", "CSVファイルをアップロードしてください。");
            return "addCollectiveBooks";
        }

        if (uploadFile.isEmpty()) {
            model.addAttribute("errorMsg", "ファイルが空のようです。書籍データを記載したファイルをアップロードしてください。");
            return "addCollectiveBooks";
        }

        return "home";
    }

}
