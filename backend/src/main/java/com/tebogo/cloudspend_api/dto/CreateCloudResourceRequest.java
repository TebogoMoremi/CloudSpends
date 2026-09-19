package com.tebogo.cloudspend_api.dto;

import com.tebogo.cloudspend_api.model.ResourceStatus;
import com.tebogo.cloudspend_api.model.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCloudResourceRequest(

        @NotBlank(message = "Resource name is required")
        String resourceName,

        @NotBlank(message = "Resource ID is required")
        String resourceId,

        @NotNull(message = "Resource type is required")
        ResourceType resourceType,

        @NotBlank(message = "Region is required")
        String region,

        @NotNull(message = "Resource status is required")
        ResourceStatus status

) {
}