package com.example.adminservice.Exeptions;

public class RessourceAlreadyDisabledException extends RuntimeException{
    public RessourceAlreadyDisabledException(String message) {
        super(message);
    }

}
