package com.tebogo.cloudspend_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tebogo.cloudspend_api.service.AwsResourceDiscoveryService;
import com.tebogo.cloudspend_api.service.AwsResourceDiscoveryService.AwsEc2Resource;

@RestController
@RequestMapping("/api/aws")
public class AwsDiscoveryController {

    private final AwsResourceDiscoveryService
            awsResourceDiscoveryService;

    public AwsDiscoveryController(
            AwsResourceDiscoveryService
                    awsResourceDiscoveryService) {

        this.awsResourceDiscoveryService =
                awsResourceDiscoveryService;
    }

    @GetMapping("/ec2")
    public List<AwsEc2Resource>
            discoverEc2Instances() {

        return awsResourceDiscoveryService
                .discoverEc2Instances();
    }
}