package com.spendwise.controller;

import com.spendwise.dto.entity.RecurringExpense;
import com.spendwise.model.RecurringFrequency;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.request.SaveRecurringExpenseRequest;
import com.spendwise.dto.response.RecurringExpenseDto;
import com.spendwise.service.CurrentUserService;
import com.spendwise.dto.service.RecurringExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-expenses")
public class RecurringExpenseController {

    private final RecurringExpenseService recurringExpenseService;
    private final CurrentUserService currentUserService;

    public RecurringExpenseController(RecurringExpenseService recurringExpenseService, CurrentUserService currentUserService) {
        this.recurringExpenseService = recurringExpenseService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public RecurringExpenseDto create(@Valid @RequestBody SaveRecurringExpenseRequest request) {
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

    @GetMapping
    public List<RecurringExpenseDto> list(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBy) {
        UserProfile user = currentUserService.getCurrentUser();
        List<RecurringExpense> recurringExpenses = dueBy == null
                ? recurringExpenseService.list(user)
                : recurringExpenseService.dueBy(user, dueBy);
        return recurringExpenses.stream().map(RecurringExpenseDto::from).toList();
    }
}
