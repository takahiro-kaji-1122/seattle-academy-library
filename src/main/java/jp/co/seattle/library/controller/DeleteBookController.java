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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.dto.BookStatusInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.BooksStatusService;

/**
 * 削除コントローラー
 */
@Controller //APIの入り口
public class DeleteBookController {
    final static Logger logger = LoggerFactory.getLogger(DeleteBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private BooksStatusService booksStatusService;

    /**
     * 対象書籍を削除する
     *
     * @param locale ロケール情報
     * @param redirectAttributes リダイレクト属性値
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/deleteBook", method = RequestMethod.GET)
    public String deleteBook(
            RedirectAttributes redirectAttributes,
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);
        int deleteBookId = bookId;

        try {
            //書籍の最新状態を取得
            BookStatusInfo latestBookStatusInfo = booksStatusService.getLatestBookStatusInfo(bookId);

            //対象の書籍が貸し出し状態である場合
            if (!latestBookStatusInfo.getAbleLend()) {
                //貸し出し中です。返却後に削除してください。と表示
                model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
                model.addAttribute("latestBookStatusInfo", latestBookStatusInfo);
                model.addAttribute("isDeleteError", true);
                return "details";
            }
            booksStatusService.deleteBookStatus(deleteBookId);
            booksService.deleteBook(deleteBookId);
        } catch (Exception e) {
            //何かしらの例外やエラーが出た場合、エラーを表示
            redirectAttributes.addFlashAttribute("unknownError", true);
            return "details";
        }

        redirectAttributes.addFlashAttribute("isSuccessDelete", true);
        return "redirect:home";

    }

}
