package com.example.adminservice.Exeptions;

public class ProfileAlreadyActiveException extends RuntimeException{
    public ProfileAlreadyActiveException(String message) {
        super(message);
    }
}
