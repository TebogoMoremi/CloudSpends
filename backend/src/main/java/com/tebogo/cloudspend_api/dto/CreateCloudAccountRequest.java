package com.tebogo.cloudspend_api.dto;

import com.tebogo.cloudspend_api.model.CloudProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateCloudAccountRequest(

        @NotBlank(message = "Account name is required")
        String accountName,

        @NotNull(message = "Cloud provider is required")
        CloudProvider provider,

        @NotBlank(message = "Provider account ID is required")
        @Pattern(
            regexp = "\\d{12}",
            message = "AWS account ID must contain exactly 12 digits"
        )
        String providerAccountId,

        @NotBlank(message = "Region is required")
        String region

) {
}