package com.tebogo.cloudspend_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.CreateCloudResourceRequest;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;

@Service
public class CloudResourceService {

    private final CloudResourceRepository cloudResourceRepository;
    private final CloudAccountRepository cloudAccountRepository;

    public CloudResourceService(
            CloudResourceRepository cloudResourceRepository,
            CloudAccountRepository cloudAccountRepository) {

        this.cloudResourceRepository = cloudResourceRepository;
        this.cloudAccountRepository = cloudAccountRepository;
    }

    public CloudResource createResource(
            Long cloudAccountId,
            CreateCloudResourceRequest request) {

        CloudAccount account = cloudAccountRepository
                .findById(cloudAccountId)
                .orElseThrow(() ->
                        new CloudAccountNotFoundException(cloudAccountId));

        CloudResource resource = new CloudResource(
                request.resourceName(),
                request.resourceId(),
                request.resourceType(),
                request.region(),
                request.status(),
                account
        );

        return cloudResourceRepository.save(resource);
    }

    public List<CloudResource> getResourcesByAccount(
            Long cloudAccountId) {

        if (!cloudAccountRepository.existsById(cloudAccountId)) {
            throw new CloudAccountNotFoundException(cloudAccountId);
        }

        return cloudResourceRepository
                .findByCloudAccountId(cloudAccountId);
    }
}