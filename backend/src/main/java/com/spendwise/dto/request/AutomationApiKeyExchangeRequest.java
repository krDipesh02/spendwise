package com.spendwise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationApiKeyExchangeRequest {

    @NotBlank
    private String telegramUserId;

    private String telegramUsername;
    private String firstName;
    private String lastName;

    /**
     * Display name for the generated key (defaults to {@code mcp} when omitted or blank).
     */
    private String apiKeyName;
}
