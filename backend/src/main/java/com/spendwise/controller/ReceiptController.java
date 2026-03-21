package com.spendwise.controller;

import com.spendwise.dto.request.UploadReceiptRequest;
import com.spendwise.dto.response.ReceiptDto;
import com.spendwise.service.CurrentUserService;
import com.spendwise.dto.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;
    private final CurrentUserService currentUserService;

    public ReceiptController(ReceiptService receiptService, CurrentUserService currentUserService) {
        this.receiptService = receiptService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ReceiptDto upload(@Valid @RequestBody UploadReceiptRequest request) {
        return ReceiptDto.from(receiptService.upload(currentUserService.getCurrentUser(), request.getFileUrl(), request.getRawText()));
    }

    @GetMapping("/{id}")
    public ReceiptDto get(@PathVariable UUID id) {
        return ReceiptDto.from(receiptService.get(id));
    }
}
