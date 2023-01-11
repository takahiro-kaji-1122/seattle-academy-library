package jp.co.seattle.library.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * ログアウトコントローラー
 */
@Controller /** APIの入り口 */
public class LogoutContller {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(Model model,
            HttpServletRequest request,
            HttpServletResponse response) {

        // セッションが存在しない場合はnullを返却
        HttpSession session = request.getSession(false);

        //セッションがない場合
        if (session == null) {
            //ログイン画面に遷移する
            return "login";
        }

        //セッションを削除
        session.invalidate();

        //ログイン画面に遷移
        return "login";
    }
}
