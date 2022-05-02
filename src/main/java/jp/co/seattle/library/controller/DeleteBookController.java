package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.LendingService;

/**
 * 削除コントローラー
 */
@Controller // APIの入り口
public class DeleteBookController {
	final static Logger logger = LoggerFactory.getLogger(DeleteBookController.class);

	@Autowired
	private BooksService booksService;
	@Autowired
	private LendingService lendingService;

	/**
	 * 対象書籍を削除する
	 *
	 * @param locale ロケール情報
	 * @param bookId 書籍ID
	 * @param model  モデル情報
	 * @return 遷移先画面名
	 */
	@Transactional
	@RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
	public String deleteBook(Locale locale, int bookId, RedirectAttributes redirectAttributes) {
		logger.info("Welcome delete! The client locale is {}.", locale);

		// 書籍の貸出し状態を取得する
		boolean isLend = lendingService.checkLendingStatus(bookId);

		if (isLend) {
			redirectAttributes.addFlashAttribute("error", "貸し出し中のため削除できません。");
			return "redirect:/details?bookId=" + bookId;
		} else {
			// 書籍の削除
			booksService.deleteBook(bookId);
			return "redirect:/home";
		}
	}
}
