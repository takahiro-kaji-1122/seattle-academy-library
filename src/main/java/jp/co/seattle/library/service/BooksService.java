package jp.co.seattle.library.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        //取得すべきカラムを書籍名（title）の昇順で取得
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by title;",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     * @return bookId 登録した書籍ID
     */
    public int registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, author, publisher, publish_date, description, isbn, thumbnail_name, thumbnail_url, reg_date, upd_date) VALUES ('"
                + bookInfo.getTitle() + "','"
                + bookInfo.getAuthor() + "','"
                + bookInfo.getPublisher() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getDescription() + "','"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "',"
                + "now(),"
                + "now()) RETURNING id";

        //sqlの''となっている箇所をnullに置換
        sql = StringUtils.replace(sql, "\'\'", "null");

        //sqlの'null'となっている箇所をnullに置換
        sql = StringUtils.replace(sql, "\'null\'", "null");

        //SQLの実行
        Integer bookId = jdbcTemplate.queryForObject(sql, Integer.class);

        return bookId;
    }

    /**
     * 書籍を更新する
     *
     * @param bookInfo 書籍情報
     * @return bookId 編集した書籍ID
     */
    public void editBook(BookDetailsInfo bookInfo) {

        String sql = "UPDATE books SET "
                + "title = '" + bookInfo.getTitle() + "',"
                + "author = '" + bookInfo.getAuthor() + "',"
                + "publisher = '" + bookInfo.getPublisher() + "',"
                + "publish_date = '" + bookInfo.getPublishDate() + "',"
                + "description = '" + bookInfo.getDescription() + "',"
                + "isbn = '" + bookInfo.getIsbn() + "',"
                + "upd_date = now() "
                + "WHERE id =" + bookInfo.getBookId();

        //sqlの''となっている箇所をnullに置換
        sql = StringUtils.replace(sql, "\'\'", "null");

        //SQLの実行
        jdbcTemplate.update(sql);

    }

    /**
     * 書籍IDに紐づく書籍を削除する
     * 
     * @param bookId 書籍ID
     */
    public void deleteBook(int bookId) {

        String sql = "DELETE FROM books WHERE id="
                + bookId
                + ";";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を貸し出し状態にする
     * 
     * @param bookId 書籍ID
     */
    public void lendBook(int bookId) {

        String sql = "UPDATE books set able_lend = false WHERE id="
                + bookId
                + ";";

        jdbcTemplate.update(sql);
    }
}
