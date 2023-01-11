package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍の貸出/返却ログDTO
 */
@Configuration
@Data
public class BookStatusInfo {

    private int id;

    private int bookId;

    private String title;

    private Boolean ableLend;

    private String rentalUser;

    private String updDate;

    public BookStatusInfo() {

    }

    public BookStatusInfo(
            int bookId,
            Boolean ableLend) {
        this.bookId = bookId;
        this.ableLend = ableLend;
    }
}
