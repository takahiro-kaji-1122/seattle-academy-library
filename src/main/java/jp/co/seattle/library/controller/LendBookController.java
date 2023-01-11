package jp.co.seattle.library.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    private BooksStatusService booksStatusService;
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
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        //セッションからユーザー名を取得する
        HttpSession session = request.getSession(false);

        //セッションがない場合
        if (session == null) {
            //ログイン画面に遷移する
            return "redirect:/";
        }

        //セッションからユーザ名を取得する
        String username = (String) session.getAttribute("username");

        try {
            //書籍の最新状態を取得
            BookStatusInfo latestBookStatusInfo = booksStatusService.getLatestBookStatusInfo(bookId);

            //対象の書籍が貸し出し中である場合
            if (!latestBookStatusInfo.getAbleLend()) {
                //貸し出し中と表示
                model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
                model.addAttribute("latestBookStatusInfo", latestBookStatusInfo);
                model.addAttribute("isLendError", true);
                return "details";
            }
            //対象書籍を貸し出し状態に変更
            BookStatusInfo bookStatusInfo = new BookStatusInfo(bookId, false);
            booksStatusService.registBookStatus(bookStatusInfo, username);

            model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
            model.addAttribute("latestBookStatusInfo", booksStatusService.getLatestBookStatusInfo(bookId));
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
