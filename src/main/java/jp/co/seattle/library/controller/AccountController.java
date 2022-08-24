package jp.co.seattle.library.controller;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.UserInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.UsersService;

/**
 * アカウント作成コントローラー
 */
@Controller //APIの入り口
public class AccountController {
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private BooksService booksService;
    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/newAccount", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    public String createAccount(Model model) {
        return "createAccount";
    }

    /**
     * 新規アカウント作成
     *
     * @param email メールアドレス
     * @param password パスワード
     * @param passwordForCheck 確認用パスワード
     * @param model
     * @return　ホーム画面に遷移
     */
    @Transactional
    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public String createAccount(Locale locale,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("passwordForCheck") String passwordForCheck,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome createAccount! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);

        // TODO バリデーションチェック、パスワード一致チェック実装
        if (!password.matches("[A-Za-z0-9]{8,}")) {
            //パスワードは8文字以上かつ半角英数字出ない場合
            model.addAttribute("validationCheck", true);
            return "createAccount";
        }
        //パスワードと確認用パスワードが一致しない場合
        if (!StringUtils.equals(password, passwordForCheck)) {
            model.addAttribute("notMatchPasswordForCheck", true);
            return "createAccount";
        }
        //パスワードに問題ない場合

        userInfo.setPassword(password);
        try {
            usersService.registUser(userInfo);
        } catch (DuplicateKeyException e) {
            //emailがすでに登録されている時
            model.addAttribute("emailDuplicat", true);
            return "createAccount";
        } catch (Exception e) {
            model.addAttribute("unknownError", true);
            return "createAccount";
        }

        model.addAttribute("bookList", booksService.getBookList());
        return "login";

    }
}
