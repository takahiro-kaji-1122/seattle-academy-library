package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍詳細情報格納DTO
 *
 */
@Configuration
@Data
public class FileErrorInfo {

    long rowNum;
    String colName;
    String errorContent;

    public FileErrorInfo() {

    }

    public FileErrorInfo(
            long rowNum,
            String errorContent) {
        this.setRowNum(rowNum);
        this.setErrorContent(errorContent);
    }

    public FileErrorInfo(
            long rowNum,
            String colName,
            String errorContent) {
        this.setRowNum(rowNum);
        this.setColName(colName);
        this.setErrorContent(errorContent);
    }

}