package com.techacademy.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // ログインユーザーのユーザー名を取得
        String loggedInUserName = userDetails.getUsername();
        // ユーザー名を使って該当する Employee オブジェクトを取得
        Employee employee = employeeService.findByCode(loggedInUserName);
        // ログインユーザーの権限を取得
        Role userRole = employee.getRole();
        List<Report> reportList;

        // 氏名を取得してモデルに追加
        String fullName = employee.getName();
        model.addAttribute("fullName", fullName);

        if (Role.ADMIN.equals(userRole)) {
            // 管理者の場合は全ての日報情報を表示
            reportList = reportService.findAll();
        } else {
            // 一般ユーザーの場合は自分が登録した日報情報のみ表示
            reportList = reportService.findAllByEmployeeRole(employee);
        }

        model.addAttribute("listSize", reportList.size());
        model.addAttribute("reportList", reportList);
        model.addAttribute("loggedInUserName", loggedInUserName);
        model.addAttribute("fullName", fullName);
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping("/{id}/")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面を表示
    @GetMapping("/add")
    public String create(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // ログインユーザーのユーザー名を取得
        String loggedInUserName = userDetails.getUsername();
        // ユーザー名を使って該当する Employee オブジェクトを取得
        Employee employee = employeeService.findByCode(loggedInUserName);

        // 新しい日報オブジェクトを作成して、従業員オブジェクトを設定
        Report report = new Report();
        report.setEmployee(employee);

        // モデルに日報オブジェクトを追加
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping("/add")
    public String add(@Validated @ModelAttribute Report report, BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (result.hasErrors()) {
            return "reports/new";
        }

        // ログインユーザーのユーザー名を取得
        String loggedInUserName = userDetails.getUsername();
        // ユーザー名を使って該当する Employee オブジェクトを取得
        Employee employee = employeeService.findByCode(loggedInUserName);

        // 日報の重複チェック
        if (reportService.isDuplicateReportDate(report.getReportDate(), employee)) {
            model.addAttribute("duplicateDateError", true);
            return "reports/new";
        }

        // 従業員オブジェクトをレポートに設定
        report.setEmployee(employee);

        // レポートを保存
        reportService.save(report);

        return "redirect:/reports";
    }

    // 日報更新画面を表示
    @GetMapping("/{id}/update")
    public String edit(@PathVariable("id") Long id, Model model) {
        Report report = reportService.findById(id);
        model.addAttribute("report", report);

        boolean duplicateDateError = reportService.checkDuplicateDate(report);
        model.addAttribute("duplicateDateError", duplicateDateError);

        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, @Validated @ModelAttribute Report report, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reports/update";
        }

        // 更新対象の日報データ取得
        Report existingReport = reportService.findById(id);
        if (existingReport == null) {
            return "redirect:/reports";
        }

        // 既存の日報内容を更新
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setReportDate(report.getReportDate());

        // 更新日時を更新
        existingReport.setUpdatedAt(LocalDateTime.now());

        // 同じ日付の日報が存在するかチェック
        if (reportService.checkDuplicateDate(existingReport)) {
            // エラーメッセージを設定して更新画面にリダイレクト
            redirectAttributes.addFlashAttribute("duplicateDateError", "既に登録されている日付です");
            return "redirect:/reports/{id}/update";
        }

        // 日報保存
        reportService.update(existingReport);

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reportService.deleteById(id);
        return "redirect:/reports";
    }

}
