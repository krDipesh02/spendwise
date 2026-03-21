package com.spendwise.dto.service;

import com.spendwise.dto.entity.Category;
import com.spendwise.dto.entity.RecurringExpense;
import com.spendwise.model.RecurringFrequency;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.CategoryRepository;
import com.spendwise.dto.repository.RecurringExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringExpenseService {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    public RecurringExpenseService(RecurringExpenseRepository recurringExpenseRepository,
                                   CategoryRepository categoryRepository,
                                   AuditService auditService) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.categoryRepository = categoryRepository;
        this.auditService = auditService;
    }

    @Transactional
    public RecurringExpense create(UserProfile user,
                                   UUID categoryId,
                                   String name,
                                   BigDecimal amount,
                                   String currency,
                                   RecurringFrequency frequency,
                                   LocalDate nextDueDate) {
        RecurringExpense recurringExpense = new RecurringExpense();
        recurringExpense.setUser(user);
        recurringExpense.setCategory(resolveCategory(user, categoryId));
        recurringExpense.setName(name);
        recurringExpense.setAmount(amount);
        recurringExpense.setCurrency(currency);
        recurringExpense.setFrequency(frequency);
        recurringExpense.setNextDueDate(nextDueDate);
        recurringExpense.setActive(true);
        RecurringExpense saved = recurringExpenseRepository.save(recurringExpense);
        auditService.log(user, "CREATE_RECURRING_EXPENSE", "RECURRING_EXPENSE", saved.getId().toString(), name);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<RecurringExpense> list(UserProfile user) {
        return recurringExpenseRepository.findByUserIdOrderByNextDueDateAsc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<RecurringExpense> dueBy(UserProfile user, LocalDate dueDate) {
        return recurringExpenseRepository.findByUserIdAndActiveTrueAndNextDueDateLessThanEqualOrderByNextDueDateAsc(user.getId(), dueDate);
    }

    private Category resolveCategory(UserProfile user, UUID categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
