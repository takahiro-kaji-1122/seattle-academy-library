package jp.co.seattle.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 書籍の状態管理サービス
 * 
 *  books_satusテーブルに関する処理を実装する
 */
@Service
public class BooksSatusService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍を削除する際に、books_satusからも対応するレコードを削除する
     * 
     * @param bookId 書籍ID
     */
    public void deleteBookStatus(int bookId) {

        String sql = "DELETE FROM books_satus WHERE book_id="
                + bookId
                + ";";

        jdbcTemplate.update(sql);
    }
}
