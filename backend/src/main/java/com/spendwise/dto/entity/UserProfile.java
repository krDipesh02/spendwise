package com.spendwise.dto.entity;

import com.spendwise.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserProfile extends BaseEntity {

    @Column(unique = true)
    private String telegramId;

    @Column(unique = true)
    private String googleSubject;

    @Column(unique = true)
    private String username;

    @Column(length = 200)
    private String passwordHash;

    @Column
    private String email;

    @Column
    private boolean emailVerified;

    @Column(length = 1000)
    private String pictureUrl;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String baseCurrency;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyLimit;



    public boolean isGoogleLinked() {
        return googleSubject != null && !googleSubject.isBlank();
    }
}
