package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;

/**
 * 本の貸し出しコントローラー
 */
@Controller
public class LendBookController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 本を貸し出し状態にする
     * @param locale
     * @param bookId
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/redBook", method = RequestMethod.GET)
    public String detailsBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            @RequestParam(name = "isInsertSuccess", required = false) boolean isInsertSuccess,
            @RequestParam(name = "isEditSuccess", required = false) boolean isEditSuccess,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        try {
            //対象の書籍が貸し出し中である場合
            if (!booksService.getBookInfo(bookId).getAbleLend()) {
                //貸し出し中と表示
                model.addAttribute("isRented", true);
                return "details";
            }
            booksService.lendBook(bookId);
        } catch (Exception e) {
            model.addAttribute("unknownError", true);
            return "details";
        }

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));

        return "details";
    }
}
