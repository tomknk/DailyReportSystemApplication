package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
//import com.techacademy.entity.Reports;
import com.techacademy.service.ReportsService;

//import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportsService.findAll().size());
        model.addAttribute("reportList", reportsService.findAll());
        return "reports/list";
    }

    /*作成途中なのでコメントアウトする
    // 日報詳細画面
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("report", reportsService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面を表示
    @GetMapping("/add")
    public String create(@ModelAttribute Reports reports, Model model, HttpServletRequest request) {
        String loggedInUserName = (String) request.getSession().getAttribute("loggedInUserName");
        model.addAttribute("loggedInUserName", loggedInUserName);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping("/add")
    public String add(@Validated @ModelAttribute Reports reports, BindingResult result) {
        if (result.hasErrors()) {
            return "reports/new";
        }
        reportsService.save(reports);
        return "redirect:/reports";
    }

    // 日報更新画面を表示
    @GetMapping("/{id}/update")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("report", reportsService.findById(id));
        return "reports/edit";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Validated @ModelAttribute Reports reports, BindingResult result) {
        if (result.hasErrors()) {
            return "reports/edit";
        }
        reportsService.save(reports);
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reportsService.deleteById(id);
        return "redirect:/reports";
    }*/
}
