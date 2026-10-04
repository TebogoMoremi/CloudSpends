package com.tebogo.cloudspend_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.model.CloudProvider;
import com.tebogo.cloudspend_api.model.CloudResource;
import com.tebogo.cloudspend_api.model.ResourceStatus;
import com.tebogo.cloudspend_api.model.ResourceType;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.repository.CloudResourceRepository;
import com.tebogo.cloudspend_api.service.AwsResourceDiscoveryService.AwsEc2Resource;

@Service
public class AwsResourceSyncService {

    private final AwsResourceDiscoveryService awsResourceDiscoveryService;
    private final CloudAccountRepository cloudAccountRepository;
    private final CloudResourceRepository cloudResourceRepository;

    public AwsResourceSyncService(
            AwsResourceDiscoveryService awsResourceDiscoveryService,
            CloudAccountRepository cloudAccountRepository,
            CloudResourceRepository cloudResourceRepository) {

        this.awsResourceDiscoveryService = awsResourceDiscoveryService;
        this.cloudAccountRepository = cloudAccountRepository;
        this.cloudResourceRepository = cloudResourceRepository;
    }

    @Transactional
    public AwsSyncResult syncEc2Resources(Long cloudAccountId) {

        CloudAccount cloudAccount = cloudAccountRepository
                .findById(cloudAccountId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cloud account not found: " + cloudAccountId
                        )
                );

        if (cloudAccount.getProvider() != CloudProvider.AWS) {
            throw new IllegalArgumentException(
                    "Cloud account is not an AWS account"
            );
        }

        List<AwsEc2Resource> awsResources =
                awsResourceDiscoveryService.discoverEc2Instances();

        int discovered = awsResources.size();
        int created = 0;
        int skipped = 0;

        for (AwsEc2Resource awsResource : awsResources) {

            boolean exists = cloudResourceRepository
                    .findByCloudAccountIdAndResourceId(
                            cloudAccountId,
                            awsResource.instanceId()
                    )
                    .isPresent();

            if (exists) {
                skipped++;
                continue;
            }

            CloudResource resource = new CloudResource(
                    awsResource.name(),
                    awsResource.instanceId(),
                    ResourceType.EC2,
                    cloudAccount.getRegion(),
                    mapStatus(awsResource.state()),
                    cloudAccount
            );

            cloudResourceRepository.save(resource);

            created++;
        }

        return new AwsSyncResult(
                cloudAccountId,
                discovered,
                created,
                skipped
        );
    }

    private ResourceStatus mapStatus(String awsState) {

        if (awsState == null) {
            return ResourceStatus.UNKNOWN;
        }

        return switch (awsState.toUpperCase()) {
            case "RUNNING" -> ResourceStatus.RUNNING;
            case "STOPPED" -> ResourceStatus.STOPPED;
            default -> ResourceStatus.UNKNOWN;
        };
    }

    public record AwsSyncResult(
            Long cloudAccountId,
            int discovered,
            int created,
            int skipped
    ) {
    }
}