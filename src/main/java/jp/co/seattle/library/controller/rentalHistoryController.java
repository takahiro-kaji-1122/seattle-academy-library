package jp.co.seattle.library.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.seattle.library.dto.BookStatusInfo;
import jp.co.seattle.library.service.BooksStatusService;

/**
 * 書籍の貸出履歴のコントローラー
 */
@Controller //APIの入り口
public class rentalHistoryController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BooksStatusService booksStatusService;

    /**
     * 書籍の貸出履歴を表示
     * @param model
     * @return 書籍の貸出履歴を表示
     */
    @RequestMapping(value = "/rentalHistory", method = RequestMethod.GET)
    public String transitionHome(Model model,
            RedirectAttributes redirectAttributes) {
        //書籍ごとの最新ステータスの格納先を定義
        List<BookStatusInfo> latestBooksStatusList;
        try {
            //書籍ごとの最新ステータスを取得
            latestBooksStatusList = booksStatusService.getLatestBooksStatusLis();
        } catch (Exception e) {
            //データの取得時にエラーが発生する場合は、エラーを表示
            logger.error("エラー発生", e);
            redirectAttributes.addAttribute("unknownError", true);
            return "redirect:/home";
        }

        for (BookStatusInfo bookStatusInfo : latestBooksStatusList) {

            // Timestampオブジェクト生成
            Timestamp timestamp = null;
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                timestamp = new Timestamp(simpleDateFormat.parse(bookStatusInfo.getUpdDate()).getTime());
            } catch (ParseException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            System.out.println(timestamp);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            bookStatusInfo.setUpdDate(simpleDateFormat1.format(timestamp));

        }
        //書籍の貸出履歴画面を表示
        model.addAttribute("latestBooksStatusList", latestBooksStatusList);
        return "rentalHistory";
    }

}
