package jp.co.seattle.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LendingService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 書籍を借りる
	 * 
	 * @param bookId
	 */
	public void lendBook(int bookId) {
		String sql = "INSERT INTO lending_manages (book_id) values (?)";
		jdbcTemplate.update(sql, bookId);
	}

	/**
	 * 書籍が貸出し済みか確認する
	 * 
	 * @param bookId
	 * @return isLend
	 */
	public boolean checkLendingStatus(int bookId) {
		String sql = "SELECT EXISTS(SELECT * FROM lending_manages WHERE book_id = ?) AS is_lend;";
		boolean isLend = jdbcTemplate.queryForObject(sql, boolean.class, bookId);
		return isLend;
	}

	/**
	 * 書籍を返す
	 * 
	 * @param bookId
	 */
	public void returnBook(int bookId) {
		String sql = "DELETE FROM lending_manages WHERE book_id = ?;";
		jdbcTemplate.update(sql, bookId);
	}
}
