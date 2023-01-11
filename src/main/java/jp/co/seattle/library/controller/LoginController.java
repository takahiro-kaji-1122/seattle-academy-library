package jp.co.seattle.library.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.UserInfo;
import jp.co.seattle.library.service.UsersService;

/**
 * ログインコントローラー
 */
@Controller /** APIの入り口 */
public class LoginController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String first(Model model,
            HttpServletRequest request,
            HttpServletResponse response) {

        // セッションが存在しない場合はnullを返却
        HttpSession session = request.getSession(false);

        // セッションがある場合
        if (session != null) {
            //すでにログインされているので、home画面に遷移する
            return "redirect:home";
        }

        return "login"; //jspファイル名
    }

    /**
     * ログイン処理
     *
     * @param email メールアドレス
     * @param password パスワード
     * @param model
     * @return　ホーム画面に遷移
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {

        // ログイン処理を実施する
        UserInfo selectedUserInfo;
        // StringUtilsメールアドレスの組み合わせ存在チェック実装
        try {
            //指定されたアカウントのパスワードを取得
            selectedUserInfo = usersService.selectUserInfo(email, password);
        } catch (EmptyResultDataAccessException e) {
            //入力されたemailが存在しない場合、エラーを表示
            model.addAttribute("isNotExistAccount", true);

            return "login";
        } catch (Exception e) {
            //何かしらの例外やエラーが出た場合、エラーを表示
            model.addAttribute("unknownError", true);
            return "login";
        }
        if (StringUtils.equals(password, selectedUserInfo.getPassword())) {
            //// セッションが存在しない場合はnullを返却
            HttpSession session = request.getSession(false);
            //セッションを開始する
            session = request.getSession(true);
            //セッションにメールアドレスを記録する
            session.setAttribute("username", email);

            // homeへリダイレクトして、transitionHomeを作動させる
            return "redirect:/home";
        }

        //emailとpasswordの組み合わせが一致しないエラーの表示
        model.addAttribute("notMatchPassword", true);
        return "login";

    }
}