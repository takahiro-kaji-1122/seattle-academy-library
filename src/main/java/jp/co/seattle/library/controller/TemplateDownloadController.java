package jp.co.seattle.library.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * アカウント作成コントローラー
 */
@Controller //APIの入り口
public class TemplateDownloadController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/booksRegisterTemplateDownload", method = RequestMethod.GET)
    public String fileDownload(HttpServletResponse response, Model model) {
        //resources/template/registBooksTemplate.csv
        try (InputStream importFileStream = new ClassPathResource("template/registBooksTemplate.csv").getInputStream();
                OutputStream outputFileStream = response.getOutputStream();) {

            byte[] fileByteArray = IOUtils.toByteArray(importFileStream);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=registBooksTemplate.csv");
            response.setContentLength(fileByteArray.length);

            outputFileStream.write(fileByteArray);
            outputFileStream.flush();
            return "addCollectiveBooks";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "ファイルダウンロードに失敗しました。時間を置いてから再度実施ください。");
            return "addCollectiveBooks";
        }
    }

}
