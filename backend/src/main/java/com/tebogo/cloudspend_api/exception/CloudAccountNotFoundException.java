package com.tebogo.cloudspend_api.exception;

public class CloudAccountNotFoundException extends RuntimeException {

    public CloudAccountNotFoundException(Long id) {
        super("Cloud account not found with id: " + id);
    }
}