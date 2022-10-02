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

import jp.co.seattle.library.dto.BookStatusInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.BooksStatusService;

/**
 * 本の貸し出しコントローラー
 */
@Controller
public class LendBookController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private BooksStatusService booksLogService;
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
    @RequestMapping(value = "/lendBook", method = RequestMethod.GET)
    public String lendBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        try {
            //書籍の最新状態を取得
            BookStatusInfo latestBookLogInfo = booksLogService.getLatestBookStatusInfo(bookId);

            //対象の書籍が貸し出し中である場合
            if (!latestBookLogInfo.getAbleLend()) {
                //貸し出し中と表示
                model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
                model.addAttribute("latestBookLogInfo", latestBookLogInfo);
                model.addAttribute("isLendError", true);
                return "details";
            }
            //対象書籍を貸し出し状態に変更
            BookStatusInfo bookLogInfo = new BookStatusInfo(bookId, false);
            booksLogService.registBookStatus(bookLogInfo);

            model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
            model.addAttribute("latestBookLogInfo", booksLogService.getLatestBookStatusInfo(bookId));
            return "details";
        } catch (Exception e) {
            //エラー、例外が出た場合
            e.printStackTrace();
            //エラーを表示
            model.addAttribute("unknownError", true);
            return "details";
        }

    }
}
