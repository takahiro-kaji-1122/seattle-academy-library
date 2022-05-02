package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.service.LendingService;

@Controller
public class LendingController {
	final static Logger logger = LoggerFactory.getLogger(LendingController.class);

	@Autowired
	private LendingService lendingService;

	/**
	 * 本を借りる
	 * 
	 * @param locale
	 * @param bookId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/lendBook", method = RequestMethod.POST)
	public String lendBook(Locale locale, int bookId, RedirectAttributes redirectAttributes) {
		logger.info("Welcome lendBook! The client locale is {}.", locale);

		// 貸出し状態を確認する
		boolean isLend = lendingService.checkLendingStatus(bookId);

		if (isLend) {
			redirectAttributes.addFlashAttribute("error", "貸出し済みです。");
		} else {
			// 本を借りる
			lendingService.lendBook(bookId);
		}
		// 詳細画面に遷移する
		return "redirect:/details?bookId=" + bookId;
	}

	@RequestMapping(value = "/returnBook", method = RequestMethod.POST)
	public String returnBook(Locale locale, int bookId, RedirectAttributes redirectAttributes) {
		logger.info("Welcome returnBook! The client locale is {}.", locale);

		// 貸出し状態を確認する
		boolean isLend = lendingService.checkLendingStatus(bookId);

		if (isLend) {
			lendingService.returnBook(bookId);
		} else {
			redirectAttributes.addFlashAttribute("error", "貸出しされていません。");
		}

		// 詳細画面に遷移する
		return "redirect:/details?bookId=" + bookId;
	}
}
