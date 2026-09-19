package com.tebogo.cloudspend_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.CloudResource;

public interface CloudResourceRepository
        extends JpaRepository<CloudResource, Long> {

    List<CloudResource> findByCloudAccountId(Long cloudAccountId);
    long countByCloudAccountId(Long cloudAccountId);
}