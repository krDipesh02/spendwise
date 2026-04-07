package com.spendwise.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import lombok.Data;

@Data
public class ConversationMemoryUpsertRequest {

    @NotBlank
    private String telegramUserId;

    @NotNull
    @Size(max = 50)
    @Valid
    private List<ConversationMemoryMessageRequest> messages;
}
