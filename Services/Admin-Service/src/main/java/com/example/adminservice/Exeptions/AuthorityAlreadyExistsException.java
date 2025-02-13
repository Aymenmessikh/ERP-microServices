package com.example.adminservice.Exeptions;

public class AuthorityAlreadyExistsException extends RuntimeException{
    public AuthorityAlreadyExistsException(String message) {
        super(message);
    }
}
