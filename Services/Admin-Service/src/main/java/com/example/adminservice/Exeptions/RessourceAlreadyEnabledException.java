package com.example.adminservice.Exeptions;

public class RessourceAlreadyEnabledException extends RuntimeException {
    public RessourceAlreadyEnabledException(String message) {
        super(message);
    }
}
