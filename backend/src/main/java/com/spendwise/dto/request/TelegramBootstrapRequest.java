package com.spendwise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramBootstrapRequest {

    @NotBlank
    private String telegramUserId;

    private String telegramUsername;
    private String firstName;
    private String lastName;
}
