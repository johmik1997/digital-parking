package com.example.digitalparking.exception;

public class EmailAlreadyExists extends RuntimeException{

    public EmailAlreadyExists(String message){
        super(message);
    }
}
