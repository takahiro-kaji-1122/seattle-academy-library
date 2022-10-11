package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.BookStatusInfo;

@Configuration
public class BookStatusInfoRowMapper implements RowMapper<BookStatusInfo> {

    @Override
    public BookStatusInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Query結果（ResultSet rs）を、オブジェクトに格納する実装
        BookStatusInfo bookLogInfo = new BookStatusInfo();

        bookLogInfo.setBookId(rs.getInt("book_id"));
        bookLogInfo.setTitle(rs.getString("title"));
        bookLogInfo.setAbleLend(rs.getBoolean("able_lend"));
        bookLogInfo.setUpdDate(rs.getString("upd_date"));
        return bookLogInfo;
    }

}
