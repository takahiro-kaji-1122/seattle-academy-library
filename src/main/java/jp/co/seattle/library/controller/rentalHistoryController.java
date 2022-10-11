package jp.co.seattle.library.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 書籍の貸出履歴のコントローラー
 */
@Controller //APIの入り口
public class rentalHistoryController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * 書籍の貸出履歴を表示
     * @param model
     * @return 書籍の貸出履歴を表示
     */
    @RequestMapping(value = "/rentalHistory", method = RequestMethod.GET)
    public String transitionHome(Model model) {

        return "rentalHistory";
    }

}
