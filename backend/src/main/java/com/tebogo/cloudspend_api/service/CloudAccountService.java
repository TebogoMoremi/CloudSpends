package com.tebogo.cloudspend_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tebogo.cloudspend_api.dto.CreateCloudAccountRequest;
import com.tebogo.cloudspend_api.dto.UpdateCloudAccountRequest;
import com.tebogo.cloudspend_api.model.CloudAccount;
import com.tebogo.cloudspend_api.repository.CloudAccountRepository;
import com.tebogo.cloudspend_api.exception.CloudAccountNotFoundException;

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
                request.region());

        return cloudAccountRepository.save(account);
    }

    public List<CloudAccount> getAllAccounts() {
        return cloudAccountRepository.findAll();
    }

    public CloudAccount getAccountById(Long id) {
        return cloudAccountRepository.findById(id)
                .orElseThrow(() -> new CloudAccountNotFoundException(id));
    }

    public CloudAccount updateAccount(
            Long id,
            UpdateCloudAccountRequest request) {

        CloudAccount account = getAccountById(id);

        account.setAccountName(request.accountName());
        account.setProvider(request.provider());
        account.setProviderAccountId(request.providerAccountId());
        account.setRegion(request.region());
        account.setStatus(request.status());

        return cloudAccountRepository.save(account);
    }

    public void deleteAccount(Long id) {

        CloudAccount account = getAccountById(id);

        cloudAccountRepository.delete(account);
    }
}