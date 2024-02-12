package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    @Autowired
    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    // 日報保存
    @Transactional
    public void save(Reports reports) {
        reports.setCreatedAt(LocalDateTime.now());
        reports.setUpdatedAt(LocalDateTime.now());
        reportsRepository.save(reports);
    }

    //日報削除
    @Transactional
    public void deleteById(Long id) {
        reportsRepository.deleteById(id);
    }

    // 全ての日報取得
    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    // IDを使って日報を検索
    public Reports findById(Long id) {
        Optional<Reports> optionalReport = reportsRepository.findById(id);
        return optionalReport.orElse(null);
    }

    // 日報の更新
    @Transactional
    public void update(Reports reports) {
        reports.setUpdatedAt(LocalDateTime.now());
        reportsRepository.save(reports);
    }
}
