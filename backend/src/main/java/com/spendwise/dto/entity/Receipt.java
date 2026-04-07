package com.spendwise.dto.entity;

import com.spendwise.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "receipts")
public class Receipt extends BaseEntity {

    @Column(nullable = false)
    private String fileUrl;

    @Column(length = 4000)
    private String rawText;

    private String extractedMerchant;

    private LocalDate extractedDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal extractedTotal;
}
