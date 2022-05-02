package jp.co.seattle.library.controller;

import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.commonutil.BookUtil;
import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * Handles requests for the application home page.
 */
@Controller // APIの入り口
public class AddBooksController {
	final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

	@Autowired
	private BooksService booksService;
	@Autowired
	private ThumbnailService thumbnailService;
	@Autowired
	private BookUtil bookUtil;

	@RequestMapping(value = "/addBook", method = RequestMethod.GET) // value＝actionで指定したパラメータ
	// RequestParamでname属性を取得
	public String login(Model model) {
		return "addBook";
	}

	/**
	 * 書籍情報を登録する
	 * 
	 * @param locale      ロケール情報
	 * @param title       書籍名
	 * @param author      著者名
	 * @param publisher   出版社
	 * @param publishDate 出版日
	 * @param file        サムネイルファイル
	 * @param isbn        ISBN
	 * @param description 説明文
	 * @param model       モデル
	 * @return 遷移先画面
	 */
	@Transactional
	@RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
	public String insertBook(Locale locale, @RequestParam("title") String title, @RequestParam("author") String author,
			@RequestParam("publisher") String publisher, @RequestParam("publishDate") String publishDate,
			@RequestParam("isbn") String isbn, @RequestParam("description") String description,
			@RequestParam("thumbnail") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
		logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

		// パラメータで受け取った書籍情報をDtoに格納する。
		BookDetailsInfo bookInfo = new BookDetailsInfo();
		bookInfo.setTitle(title);
		bookInfo.setAuthor(author);
		bookInfo.setPublisher(publisher);
		bookInfo.setPublishDate(publishDate);
		bookInfo.setIsbn(isbn);
		bookInfo.setDescription(description);

		List<String> errorList = bookUtil.checkBookInfo(bookInfo);
		// errorListに一つでもエラーメッセージが入っていたら登録しない
		if (errorList.size() > 0) {
			redirectAttributes.addFlashAttribute("bookInfo", bookInfo);
			redirectAttributes.addFlashAttribute("errorList", errorList);
			return "redirect:/addBook";
		}

		// クライアントのファイルシステムにある元のファイル名を設定する
		String thumbnail = file.getOriginalFilename();

		if (!file.isEmpty()) {
			try {
				// サムネイル画像をアップロード
				String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
				// URLを取得
				String thumbnailUrl = thumbnailService.getURL(fileName);

				bookInfo.setThumbnailName(fileName);
				bookInfo.setThumbnailUrl(thumbnailUrl);

			} catch (Exception e) {
				// 異常終了時の処理
				logger.error("サムネイルアップロードでエラー発生", e);
				redirectAttributes.addFlashAttribute("bookDetailsInfo", bookInfo);
				return "redirect:/addBook";
			}
		}

		// 書籍情報を新規登録する
		int bookId = booksService.registBook(bookInfo);

		// 詳細画面に遷移する
		return "redirect:/details?bookId=" + bookId;
	}
}
