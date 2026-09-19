package com.tebogo.cloudspend_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.CreateCloudAccountRequest;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;

@Service
public class CloudAccountService {

    private final CloudAccountRepository cloudAccountRepository;

    public CloudAccountService(
            CloudAccountRepository cloudAccountRepository) {
        this.cloudAccountRepository = cloudAccountRepository;
    }

    public CloudAccount createAccount(CreateCloudAccountRequest request) {

        CloudAccount account = new CloudAccount(
                request.accountName(),
                request.provider(),
                request.providerAccountId(),
                request.region()
        );

        return cloudAccountRepository.save(account);
    }

    public List<CloudAccount> getAllAccounts() {
        return cloudAccountRepository.findAll();
    }
}