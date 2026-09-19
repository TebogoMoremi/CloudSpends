package com.tebogo.cloudspend_api.dto;

import com.tebogo.cloudspend_api.model.AccountStatus;
import com.tebogo.cloudspend_api.model.CloudProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCloudAccountRequest(

        @NotBlank(message = "Account name is required")
        String accountName,

        @NotNull(message = "Cloud provider is required")
        CloudProvider provider,

        @NotBlank(message = "Provider account ID is required")
        String providerAccountId,

        @NotBlank(message = "Region is required")
        String region,

        @NotNull(message = "Status is required")
        AccountStatus status

) {
}