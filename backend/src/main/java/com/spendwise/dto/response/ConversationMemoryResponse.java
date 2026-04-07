package com.spendwise.dto.response;

import java.time.Instant;
import java.util.List;

public record ConversationMemoryResponse(String telegramUserId,
                                         List<ConversationMemoryMessageResponse> messages,
                                         Instant expiresAt) {
}
