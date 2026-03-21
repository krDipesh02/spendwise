package com.spendwise.controller;

import com.spendwise.model.RecurringFrequency;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.AddExpenseToolRequest;
import com.spendwise.dto.request.AddRecurringExpenseToolRequest;
import com.spendwise.dto.request.MonthlySummaryToolRequest;
import com.spendwise.dto.request.SetBudgetToolRequest;
import com.spendwise.dto.request.TrendToolRequest;
import com.spendwise.dto.request.UploadReceiptToolRequest;
import com.spendwise.dto.response.ExpenseDto;
import com.spendwise.dto.response.ReceiptDto;
import com.spendwise.dto.response.RecurringExpenseDto;
import com.spendwise.dto.response.ReminderDto;
import com.spendwise.service.AnalyticsService;
import com.spendwise.dto.service.BudgetService;
import com.spendwise.service.CurrentUserService;
import com.spendwise.dto.service.ExpenseService;
import com.spendwise.dto.service.ReceiptService;
import com.spendwise.dto.service.RecurringExpenseService;
import com.spendwise.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mcp/tools")
public class McpToolController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final AnalyticsService analyticsService;
    private final ReceiptService receiptService;
    private final RecurringExpenseService recurringExpenseService;
    private final ReminderService reminderService;
    private final CurrentUserService currentUserService;

    public McpToolController(ExpenseService expenseService,
                             BudgetService budgetService,
                             AnalyticsService analyticsService,
                             ReceiptService receiptService,
                             RecurringExpenseService recurringExpenseService,
                             ReminderService reminderService,
                             CurrentUserService currentUserService) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.analyticsService = analyticsService;
        this.receiptService = receiptService;
        this.recurringExpenseService = recurringExpenseService;
        this.reminderService = reminderService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/add_expense")
    public ExpenseDto addExpense(@Valid @RequestBody AddExpenseToolRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        return ExpenseDto.from(expenseService.create(
                user,
                request.getCategoryId(),
                request.getAmount(),
                request.getCurrency(),
                request.getSpentAt(),
                request.getMerchant(),
                request.getDescription(),
                request.getReceiptId()
        ));
    }

    @PostMapping("/set_budget")
    public List<BudgetService.BudgetStatus> setBudget(@Valid @RequestBody SetBudgetToolRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        budgetService.setBudget(user, request.getCategoryId(), request.getMonth(), request.getAmount());
        return budgetService.getBudgetStatus(user, request.getMonth());
    }

    @PostMapping("/upload_receipt")
    public ReceiptDto uploadReceipt(@Valid @RequestBody UploadReceiptToolRequest request) {
        return ReceiptDto.from(receiptService.upload(currentUserService.getCurrentUser(), request.getFileUrl(), request.getRawText()));
    }

    @PostMapping("/add_recurring_expense")
    public RecurringExpenseDto addRecurringExpense(@Valid @RequestBody AddRecurringExpenseToolRequest request) {
        UserProfile user = currentUserService.getCurrentUser();
        return RecurringExpenseDto.from(recurringExpenseService.create(
                user,
                request.getCategoryId(),
                request.getName(),
                request.getAmount(),
                request.getCurrency(),
                request.getFrequency(),
                request.getNextDueDate()
        ));
    }

    @PostMapping("/get_monthly_summary")
    public AnalyticsService.MonthlySummary getMonthlySummary(@Valid @RequestBody MonthlySummaryToolRequest request) {
        return analyticsService.getMonthlySummary(currentUserService.getCurrentUser(), request.getMonth());
    }

    @PostMapping("/get_category_summary")
    public List<AnalyticsService.CategorySpend> getCategorySummary(@Valid @RequestBody MonthlySummaryToolRequest request) {
        return analyticsService.getCategorySummary(currentUserService.getCurrentUser(), request.getMonth());
    }

    @PostMapping("/get_spending_trend")
    public List<AnalyticsService.TrendPoint> getSpendingTrend(@Valid @RequestBody TrendToolRequest request) {
        return analyticsService.getSpendingTrend(currentUserService.getCurrentUser(), request.getFrom(), request.getTo());
    }

    @PostMapping("/generate_reminders")
    public List<ReminderDto> generateReminders() {
        return reminderService.generateReminders(currentUserService.getCurrentUser()).stream().map(ReminderDto::from).toList();
    }
}
