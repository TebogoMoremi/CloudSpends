package com.tebogo.cloudspend_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.CloudResource;

public interface CloudResourceRepository
        extends JpaRepository<CloudResource, Long> {

    List<CloudResource> findByCloudAccountId(
            Long cloudAccountId
    );

    long countByCloudAccountId(
            Long cloudAccountId
    );

    Optional<CloudResource>
            findByCloudAccountIdAndResourceId(
                    Long cloudAccountId,
                    String resourceId
            );
}