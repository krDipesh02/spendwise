package com.spendwise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramBootstrapResponse {

    private String userId;
    private String telegramId;
    private String displayName;
    private boolean created;
}
