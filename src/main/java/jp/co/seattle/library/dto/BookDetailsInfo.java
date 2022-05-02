package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍詳細情報格納DTO
 *
 */
@Configuration
@Data
public class BookDetailsInfo {

	private int bookId;

	private String title;

	private String author;

	private String publisher;

	private String publishDate;

	private String thumbnailUrl;

	private String thumbnailName;

	private String isbn;

	private String description;

	private String lendingStatus;

}