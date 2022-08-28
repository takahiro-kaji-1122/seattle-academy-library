package jp.co.seattle.library.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BooksService booksService;

    /**
     * Homeボタンからホーム画面に戻るページ
     * @param model
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String transitionHome(Model model) {
        List<BookInfo> bookList;
        //書籍情報が0件の場合
        try {
            bookList = booksService.getBookList();
        } catch (Exception e) {
            model.addAttribute("unknownError", true);
            return "home";
        }
        if (CollectionUtils.isEmpty(bookList)) {
            model.addAttribute("notExistBookData", true);
            return "home";
        }
        model.addAttribute("bookList", bookList);
        return "home";
    }

}
