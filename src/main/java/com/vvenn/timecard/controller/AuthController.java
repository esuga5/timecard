package com.vvenn.timecard.controller;

import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ログイン認証コントローラー
 */
@Controller
public class AuthController {
    @Autowired
    /**
     * ルートへのGET/POSTアクセスを「/」へリダイレクトします。<br>
     * 非ログインの場合はSecurityConfigの設定により「/login」へリダイレクトします。
     *
     * @return 「/」へのリダイレクト
     */
    @RequestMapping("/")
    public String root() {
        return "redirect:/";
    }

    /**
     * ログインへのGETアクセスを「auth/login.html」へフォワードします。<br>
     * ログインエラー時は「?error」付きのアクセスのため、メッセージを表示します。<br>
     * POSTアクセス時のログイン処理はSpringSecurityのデフォルト処理に任せます。
     *
     * @param model viewへ渡すためのmodel
     * @param error エラー時のクエストリング
     * @return "auth/login.html"
     */
    @GetMapping("/login")
    public String login(Model model, @RequestParam Optional<String> error) {
        model.addAttribute("loginError", error.isPresent());
        return "auth/login";
    }

    /**
     * ログアウトへのGET/POSTアクセスに対してログアウト処理を行い<br>
     * 「/login」へリダイレクトします。
     *
     * @param request 「/logout」へのPOSTを取得します。
     * @return 「/login」へのリダイレクト文字列
     * @throws ServletException エラーを取得します。
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }

}