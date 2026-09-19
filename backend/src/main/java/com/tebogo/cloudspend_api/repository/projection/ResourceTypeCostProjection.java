package com.tebogo.cloudspend_api.repository.projection;

import java.math.BigDecimal;

import com.tebogo.cloudspend_api.model.ResourceType;

public interface ResourceTypeCostProjection {

    ResourceType getResourceType();

    BigDecimal getCost();
}