package com.spendwise.dto.service;

import com.spendwise.dto.entity.AuditLog;
import com.spendwise.dto.entity.UserProfile;
import com.spendwise.dto.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(UserProfile user, String action, String resourceType, String resourceId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(user.getId().toString());
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> list(UserProfile user) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId().toString());
    }
}
