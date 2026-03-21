package com.spendwise.controller;

import com.spendwise.service.AnalyticsService;
import com.spendwise.service.CurrentUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CurrentUserService currentUserService;

    public AnalyticsController(AnalyticsService analyticsService, CurrentUserService currentUserService) {
        this.analyticsService = analyticsService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/monthly-summary")
    public AnalyticsService.MonthlySummary monthlySummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getMonthlySummary(currentUserService.getCurrentUser(), month);
    }

    @GetMapping("/category-summary")
    public List<AnalyticsService.CategorySpend> categorySummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getCategorySummary(currentUserService.getCurrentUser(), month);
    }

    @GetMapping("/trend")
    public List<AnalyticsService.TrendPoint> trend(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return analyticsService.getSpendingTrend(currentUserService.getCurrentUser(), from, to);
    }

    @GetMapping("/outliers")
    public List<AnalyticsService.ExpenseOutlier> outliers(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.findUnusualSpend(currentUserService.getCurrentUser(), month);
    }
}
