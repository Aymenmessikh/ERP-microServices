package com.example.adminservice.Exeptions;

public class AuthorityNoteExistsException extends RuntimeException{
    public AuthorityNoteExistsException(String message){
        super(message);
    }
}
