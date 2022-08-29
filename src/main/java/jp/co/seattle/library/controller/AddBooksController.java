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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
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
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            RedirectAttributes redirectAttributes,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            @RequestParam("thumbnail") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo newBookDetailsInfo = new BookDetailsInfo();
        newBookDetailsInfo.setTitle(title);
        newBookDetailsInfo.setAuthor(author);
        newBookDetailsInfo.setPublisher(publisher);
        newBookDetailsInfo.setPublishDate(publishDate);
        newBookDetailsInfo.setDescription(description);
        newBookDetailsInfo.setIsbn(isbn);

        //ThumbnailNameとThumbnailUrlに""代入
        newBookDetailsInfo.setThumbnailName("");
        newBookDetailsInfo.setThumbnailUrl("");

        /* バリデーションチェック
         * requiredItemCheck 必須項目のチェック結果
         * Boolean publishDateCheck 出版日のチェック結果
         * Boolean isbmCheck ISBNのチェック結果
         */
        Boolean requiredItemCheck = false;
        Boolean publishDateCheck = false;
        Boolean isbmCheck = false;
        //必須項目のチェック（Nullとスペースのみを許容しない）
        if (StringUtils.isNotBlank(newBookDetailsInfo.getTitle()) &&
                StringUtils.isNotBlank(newBookDetailsInfo.getAuthor()) &&
                StringUtils.isNotBlank(newBookDetailsInfo.getPublisher()) &&
                StringUtils.isNotBlank(newBookDetailsInfo.getPublishDate())) {
            requiredItemCheck = true;
        }
        if (newBookDetailsInfo.getPublishDate().matches("^[0-9]{8,8}$")) {
            publishDateCheck = true;
        }
        if (newBookDetailsInfo.getIsbn().matches("^[0-9]{10,10}$") ||
                newBookDetailsInfo.getIsbn().matches("^[0-9]{13,13}$") ||
                StringUtils.isEmpty(newBookDetailsInfo.getIsbn())) {
            isbmCheck = true;
        }
        //　必須項目・出版日・ISBNのチェック結果のうち、いずれかがNGの場合、エラーを表示
        if (!requiredItemCheck ||
                !publishDateCheck ||
                !isbmCheck) {
            model.addAttribute("requiredItemCheckNG", !requiredItemCheck);
            model.addAttribute("publishDateCheckNG", !publishDateCheck);
            model.addAttribute("isbmCheckNG", !isbmCheck);
            model.addAttribute("newBookDetailsInfo", newBookDetailsInfo);
            return "addBook";
        }

        //

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                newBookDetailsInfo.setThumbnailName(fileName);
                newBookDetailsInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("newBookDetailsInfo", newBookDetailsInfo);
                return "addBook";
            }
        }

        // 書籍情報を新規登録する

        try {
            newBookDetailsInfo.setBookId(booksService.registBook(newBookDetailsInfo));
        } catch (Exception e) {

            //異常終了時の処理
            logger.error("書籍情報登録でエラー発生", e);
            model.addAttribute("unknownError", true);
            model.addAttribute("newBookDetailsInfo", newBookDetailsInfo);
            return "addBook";
        }

        // TODO 登録した書籍の詳細情報を表示するように実装
        redirectAttributes.addAttribute("isInsertSuccess", true);
        redirectAttributes.addAttribute("bookId", newBookDetailsInfo.getBookId());
        //  詳細画面に遷移する
        return "redirect:/details";
    }

}
