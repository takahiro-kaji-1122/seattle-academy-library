package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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

import jp.co.seattle.library.commonutil.BookUtil;
import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;

@Controller
public class BulkRegistBooksController {
	final static Logger logger = LoggerFactory.getLogger(BulkRegistBooksController.class);

	@Autowired
	private BookUtil bookUtil;
	@Autowired
	private BooksService booksService;

	private static final String COMMA = ",";
	private static final int SPLIT_LIMIT = -1; // -1にしておくとカンマ区切りの空文字も分割対象にする

	@RequestMapping(value = "/bulkRegistBooks", method = RequestMethod.GET) // value＝actionで指定したパラメータ
	public String transitionBulkRegist(Model model) {
		return "bulkRegistBooks";
	}

	@Transactional
	@RequestMapping(value = "/bulkRegistBooks", method = RequestMethod.POST)
	public String bulkRegist(Locale locale, @RequestParam("file") MultipartFile file, Model model) {
		// 書籍格納用リスト
		List<BookDetailsInfo> books = new ArrayList<BookDetailsInfo>();
		// csv一行取得用の文字列
		String line;
		// エラー文言格納用のリスト
		List<String> errorList = new ArrayList<>();
		try {
			// csvファイルの読み込み
			InputStream stream = file.getInputStream();
			Reader reader = new InputStreamReader(stream);
			BufferedReader buf = new BufferedReader(reader);
			// 行数カウント用の変数
			int count = 0;
			// 次の行が読み込めなくなるまで繰り返す
			while ((line = buf.readLine()) != null) {
				// 行数カウント
				count++;
				// 一行をカンマ区切りで配列を作る
				String[] book = line.split(COMMA, SPLIT_LIMIT);
				// bookDetailsInfoにセットする
				BookDetailsInfo bookDetailsInfo = mapData(book);
				// bookDetailsInfoでバリデーションチェックを行いエラーがあればエラーリストにメッセージをセットする
				if (bookUtil.checkBookInfo(bookDetailsInfo).size() > 0) {
					errorList.add(count + "行目の書籍登録でエラーが起きました。");
				} else {
					// 書籍リストに書籍情報を追加
					books.add(bookDetailsInfo);
				}
			}
		} catch (IOException e) {
			line = "Can't read contents.";
			e.printStackTrace();
		}

		// 書籍リストが空の場合は一括登録画面に返す
		if (books.size() == 0) {
			errorList.add("csvに書籍情報がありません。");
			model.addAttribute("errorList", errorList);
			return "bulkRegistBooks";
		}

		// バリデーションチェックに引っかかった場合は一括登録画面に返す
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
			return "bulkRegistBooks";
		}

		// 書籍リストに格納されている書籍を一冊ずつ登録
		for (BookDetailsInfo book : books) {
			booksService.registBook(book);
		}
		return "redirect:/home";
	}

	/**
	 * BookDetailsInfoのインスタンスにセットする
	 * 
	 * @param book splitされた書籍情報
	 * @return bookDetailsInfo
	 */
	private BookDetailsInfo mapData(String[] book) {
		BookDetailsInfo bookDetailsInfo = new BookDetailsInfo();
		bookDetailsInfo.setTitle(book[0]);
		bookDetailsInfo.setAuthor(book[1]);
		bookDetailsInfo.setPublisher(book[2]);
		bookDetailsInfo.setPublishDate(book[3]);
		bookDetailsInfo.setIsbn(book[4]);
		return bookDetailsInfo;
	}
}
