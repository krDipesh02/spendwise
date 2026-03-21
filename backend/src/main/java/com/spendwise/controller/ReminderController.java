package com.spendwise.controller;

import com.spendwise.dto.response.ReminderDto;
import com.spendwise.service.CurrentUserService;
import com.spendwise.service.ReminderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final CurrentUserService currentUserService;

    public ReminderController(ReminderService reminderService, CurrentUserService currentUserService) {
        this.reminderService = reminderService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/generate")
    public List<ReminderDto> generate() {
        return reminderService.generateReminders(currentUserService.getCurrentUser()).stream().map(ReminderDto::from).toList();
    }

    @GetMapping
    public List<ReminderDto> list() {
        return reminderService.listUpcoming(currentUserService.getCurrentUser()).stream().map(ReminderDto::from).toList();
    }
}
