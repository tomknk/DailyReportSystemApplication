package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import com.techacademy.entity.Employee;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public void save(Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    // 日報削除
    @Transactional
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }

    // 日報新規登録処理 日報の重複チェック
    public boolean isDuplicateReportDate(LocalDate reportDate, Employee employee) {
        // ログインユーザーの日報リストを取得
        List<Report> reports = reportRepository.findByEmployeeAndReportDate(employee, reportDate);

        // レポートが存在する場合は重複とみなす
        if (!reports.isEmpty()) {
            // レポートが存在する場合、その従業員情報を取得して使用する
            Report existingReport = reports.get(0); // 重複した最初のレポートを取得
            Employee existingEmployee = existingReport.getEmployee(); // 既存のレポートの従業員情報を取得
            if (existingEmployee != null) {
                // 既存のレポートの従業員情報がnullでない場合、その従業員情報を使用する
                employee = existingEmployee;
            } else {
                // 既存のレポートの従業員情報がnullの場合、エラー処理を行う
                return false; // 重複していないとみなす
            }
        }

        return !reports.isEmpty();
    }

    // 全ての日報取得
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報取得
    public List<Report> findAllByEmployeeRole(Employee employee) {
        if (employee.getRole() == Employee.Role.ADMIN) {
            // 管理者の場合はすべての日報を取得
            return reportRepository.findAll();
        } else {
            // 一般ユーザーの場合は自分が作成した日報のみを取得
            return reportRepository.findByEmployee(employee);
        }
    }

    // 1件を検索
    public Report findById(Long id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報の更新
    @Transactional
    public void update(Report report) {
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    // 日報の重複チェック
    public boolean isDuplicateReportDate(LocalDate reportDate, Employee employee, Long id) {
        // ログインユーザーの日報リストを取得
        List<Report> reports = reportRepository.findByEmployeeAndReportDate(employee, reportDate);

        // 更新対象の日報を除外
        reports = reports.stream().filter(r -> !r.getId().equals(id)).collect(Collectors.toList());

        return !reports.isEmpty();
    }

    // 同じ日付の日報が存在するかチェック
    public boolean checkDuplicateDate(Report report) {
        LocalDate reportDate = report.getReportDate();
        Employee employee = report.getEmployee();

        // ログインユーザーの日報リストを取得
        List<Report> reports = reportRepository.findByEmployeeAndReportDate(employee, reportDate);

        // 既存の日報リストに該当の日報以外のものがあれば重複していると判断
        for (Report r : reports) {
            if (!r.getId().equals(report.getId())) {
                return true;
            }
        }

        return false;
    }

}