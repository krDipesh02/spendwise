package com.spendwise.dto.repository;

import com.spendwise.dto.entity.ConversationMemory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationMemoryRepository extends JpaRepository<ConversationMemory, UUID> {

    Optional<ConversationMemory> findByUserId(UUID userId);
}
