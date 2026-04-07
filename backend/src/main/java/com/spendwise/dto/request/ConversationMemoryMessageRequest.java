package com.spendwise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConversationMemoryMessageRequest {

    @NotBlank
    @Size(max = 20)
    private String role;

    @NotBlank
    @Size(max = 4000)
    private String content;
}
