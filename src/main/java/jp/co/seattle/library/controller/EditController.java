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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * 詳細表示コントローラー
 */
@Controller
public class EditController {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);

    @Autowired
    private BooksService booksService;

    /**
     * 編集画面に遷移する
     * @param locale
     * @param bookId
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/editBook", method = RequestMethod.GET)
    public String detailsBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        try {
            model.addAttribute("editBookDetailsInfo", booksService.getBookInfo(bookId));
        } catch (Exception e) {
            //何かしらの例外やエラーが出た場合、エラーを表示
            model.addAttribute("unknownError", true);
            return "detail";
        }

        return "editBook";
    }

    /**
     * 書籍情報を編集する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/editBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            RedirectAttributes redirectAttributes,
            @RequestParam("bookId") Integer bookId,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo editBookDetailsInfo = new BookDetailsInfo();
        editBookDetailsInfo.setBookId(bookId);
        editBookDetailsInfo.setTitle(title);
        editBookDetailsInfo.setAuthor(author);
        editBookDetailsInfo.setPublisher(publisher);
        editBookDetailsInfo.setPublishDate(publishDate);
        editBookDetailsInfo.setDescription(description);
        editBookDetailsInfo.setIsbn(isbn);

        /* バリデーションチェック
         * requiredItemCheck 必須項目のチェック結果
         * Boolean publishDateCheck 出版日のチェック結果
         * Boolean isbmCheck ISBNのチェック結果
         */
        Boolean requiredItemCheck = false;
        Boolean publishDateCheck = false;
        Boolean isbmCheck = false;
        //必須項目のチェック（Nullとスペースのみを許容しない）
        if (StringUtils.isNotBlank(editBookDetailsInfo.getTitle()) &&
                StringUtils.isNotBlank(editBookDetailsInfo.getAuthor()) &&
                StringUtils.isNotBlank(editBookDetailsInfo.getPublisher()) &&
                StringUtils.isNotBlank(editBookDetailsInfo.getPublishDate())) {
            requiredItemCheck = true;
        }
        if (editBookDetailsInfo.getPublishDate().matches("^[0-9]{8,8}$")) {
            publishDateCheck = true;
        }
        if (editBookDetailsInfo.getIsbn().matches("^[0-9]{10,10}$") ||
                editBookDetailsInfo.getIsbn().matches("^[0-9]{13,13}$") ||
                StringUtils.isEmpty(editBookDetailsInfo.getIsbn())) {
            isbmCheck = true;
        }
        //　必須項目・出版日・ISBNのチェック結果のうち、いずれかがNGの場合、エラーを表示
        if (!requiredItemCheck ||
                !publishDateCheck ||
                !isbmCheck) {
            model.addAttribute("requiredItemCheckNG", !requiredItemCheck);
            model.addAttribute("publishDateCheckNG", !publishDateCheck);
            model.addAttribute("isbmCheckNG", !isbmCheck);
            model.addAttribute("newBookDetailsInfo", editBookDetailsInfo);
            return "addBook";
        }

        // 書籍情報を更新する

        try {
            booksService.editBook(editBookDetailsInfo);
        } catch (Exception e) {

            //異常終了時の処理
            logger.error("書籍情報登録でエラー発生", e);
            model.addAttribute("unknownError", true);
            model.addAttribute("editBookDetailsInfo", editBookDetailsInfo);
            return "addBook";
        }

        // TODO 登録した書籍の詳細情報を表示するように実装
        redirectAttributes.addAttribute("isEditSuccess", true);
        redirectAttributes.addAttribute("bookId", editBookDetailsInfo.getBookId());
        //  詳細画面に遷移する
        return "redirect:/details";
    }
}
