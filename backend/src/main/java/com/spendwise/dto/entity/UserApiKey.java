package com.spendwise.dto.entity;

import com.spendwise.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_api_keys")
public class UserApiKey extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String keyPrefix;

    @Column(nullable = false, unique = true, length = 128)
    private String keyHash;

    @Column
    private Instant lastUsedAt;

    @Column
    private Instant revokedAt;
}
