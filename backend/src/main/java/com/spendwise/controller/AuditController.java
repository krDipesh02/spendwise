package com.spendwise.controller;

import com.spendwise.dto.service.AuditService;
import com.spendwise.dto.response.AuditLogResponse;
import com.spendwise.service.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;
    private final CurrentUserService currentUserService;

    public AuditController(AuditService auditService, CurrentUserService currentUserService) {
        this.auditService = auditService;
        this.currentUserService = currentUserService;
    }

    /**
     * Returns the audit log entries recorded for the authenticated user.
     *
     * @return the user's audit history in reverse chronological order as provided by the service layer
     */
    @GetMapping
    public List<AuditLogResponse> list() {
        return auditService.list(currentUserService.getCurrentUser()).stream().map(AuditLogResponse::from).toList();
    }
}
