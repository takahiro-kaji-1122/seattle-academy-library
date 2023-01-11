package jp.co.seattle.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookStatusInfo;
import jp.co.seattle.library.rowMapper.BookStatusInfoRowMapper;

/**
 * 書籍ログのサービス
 * 
 *  books_satusテーブルに関する処理を実装する
 */
@Service
public class BooksStatusService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍を削除する際に、books_statusからも対応するレコードを削除する
     * 
     * @param bookId 書籍ID
     */
    public void deleteBookStatus(int bookId) {

        String sql = "DELETE FROM books_status WHERE book_id="
                + bookId
                + ";";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍ログを登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBookStatus(BookStatusInfo bookStatusInfo) {

        String sql = "INSERT INTO books_status (book_id, able_lend, upd_date) VALUES ('"
                + bookStatusInfo.getBookId() + "','"
                + bookStatusInfo.getAbleLend() + "',"
                + "now())";

        //SQLの実行
        jdbcTemplate.update(sql);
    }

    /**
     * 対象書籍の最新ログの取得
     *
     * @param bookInfo 書籍情報
     * @return latestLog 登録した書籍ID
     */
    public BookStatusInfo getLatestBookStatusInfo(int bookId) {

        String sql = "SELECT * FROM books_status "
                + "where book_id =" + bookId + " and upd_date =(select max(upd_date) from books_status WHERE book_id = "
                + bookId + ")";

        //SQLの実行
        BookStatusInfo latestLog = jdbcTemplate.queryForObject(sql, new BookStatusInfoRowMapper());

        return latestLog;
    }

}
