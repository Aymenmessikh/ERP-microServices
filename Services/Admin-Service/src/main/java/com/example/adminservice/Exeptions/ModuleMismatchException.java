package com.example.adminservice.Exeptions;

public class ModuleMismatchException extends RuntimeException{
    public ModuleMismatchException(String message) {
        super(message);
    }
}
