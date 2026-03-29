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
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CurrentUserService currentUserService;

    public AnalyticsController(AnalyticsService analyticsService, CurrentUserService currentUserService) {
        this.analyticsService = analyticsService;
        this.currentUserService = currentUserService;
    }

    /**
     * Returns a monthly spending summary for the authenticated user.
     *
     * @param month the month to summarize, formatted as {@code yyyy-MM}
     * @return aggregated spending totals and budget usage for the requested month
     */
    @GetMapping("/monthly-summary")
    public AnalyticsService.MonthlySummary monthlySummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getMonthlySummary(currentUserService.getCurrentUser(), month);
    }

    /**
     * Returns per-category spending totals for the authenticated user for a given month.
     *
     * @param month the month to summarize, formatted as {@code yyyy-MM}
     * @return category-level spending aggregates for the requested month
     */
    @GetMapping("/category-summary")
    public List<AnalyticsService.CategorySpend> categorySummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getCategorySummary(currentUserService.getCurrentUser(), month);
    }

    /**
     * Returns spending trend points for the authenticated user across a date range.
     *
     * @param from the inclusive start date, formatted as an ISO local date
     * @param to the inclusive end date, formatted as an ISO local date
     * @return trend points between the requested dates
     */
    @GetMapping("/trend")
    public List<AnalyticsService.TrendPoint> trend(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return analyticsService.getSpendingTrend(currentUserService.getCurrentUser(), from, to);
    }

    /**
     * Returns unusual spending entries detected for the authenticated user for a given month.
     *
     * @param month the month to analyze, formatted as {@code yyyy-MM}
     * @return detected spending outliers for the requested month
     */
    @GetMapping("/outliers")
    public List<AnalyticsService.ExpenseOutlier> outliers(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.findUnusualSpend(currentUserService.getCurrentUser(), month);
    }
}
