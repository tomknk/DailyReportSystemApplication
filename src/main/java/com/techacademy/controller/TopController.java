
package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    // ログイン画面表示
    @GetMapping(value = "/login")
    public String login() {
        return "login/login";
    }

    // ログイン後のトップページ表示
    @GetMapping(value = "/")
    public String top(Model model) {

        // 修正前:従業員一覧画面「/employees」にリダイレクト
        //return "redirect:/employees";

        // 問題なければ日報一覧画面「/reports」にリダイレクトに変更する
        return "redirect:/reports";
    }

}
