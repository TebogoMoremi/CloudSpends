package com.tebogo.cloudspend_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Tag;

@Service
public class AwsResourceDiscoveryService {

    private final Ec2Client ec2Client;

    public AwsResourceDiscoveryService(
            Ec2Client ec2Client) {

        this.ec2Client = ec2Client;
    }

    public List<AwsEc2Resource> discoverEc2Instances() {

        List<AwsEc2Resource> resources =
                new ArrayList<>();

        String nextToken = null;

        do {

            DescribeInstancesRequest request =
                    DescribeInstancesRequest.builder()
                            .nextToken(nextToken)
                            .build();

            DescribeInstancesResponse response =
                    ec2Client.describeInstances(request);

            for (Reservation reservation
                    : response.reservations()) {

                for (Instance instance
                        : reservation.instances()) {

                    resources.add(
                            mapInstance(instance)
                    );
                }
            }

            nextToken = response.nextToken();

        } while (
                nextToken != null &&
                !nextToken.isBlank()
        );

        return resources;
    }

    private AwsEc2Resource mapInstance(
            Instance instance) {

        String name = findName(instance);

        String availabilityZone = null;

        if (instance.placement() != null) {
            availabilityZone =
                    instance.placement()
                            .availabilityZone();
        }

        return new AwsEc2Resource(
                instance.instanceId(),
                name,
                instance.instanceTypeAsString(),
                instance.state() != null
                        ? instance.state().nameAsString()
                        : "UNKNOWN",
                availabilityZone,
                instance.privateIpAddress(),
                instance.publicIpAddress()
        );
    }

    private String findName(
            Instance instance) {

        if (instance.tags() == null) {
            return instance.instanceId();
        }

        return instance.tags()
                .stream()
                .filter(tag ->
                        "Name".equals(tag.key()))
                .map(Tag::value)
                .findFirst()
                .orElse(instance.instanceId());
    }

    public record AwsEc2Resource(
            String instanceId,
            String name,
            String instanceType,
            String state,
            String availabilityZone,
            String privateIpAddress,
            String publicIpAddress
    ) {
    }
}