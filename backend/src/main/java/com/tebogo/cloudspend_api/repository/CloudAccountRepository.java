
package com.tebogo.cloudspend_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.tebogo.cloudspend_api.model.CloudAccount;

public interface CloudAccountRepository
        extends JpaRepository<CloudAccount, Long> {
}