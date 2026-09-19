package com.tebogo.cloudspend_api.exception;

public class CloudResourceNotFoundException extends RuntimeException {

    public CloudResourceNotFoundException(Long id) {
        super("Cloud resource not found with id: " + id);
    }
}