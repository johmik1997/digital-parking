package com.example.digitalparking.exception;

public class InvalidPhoneException extends RuntimeException{
    public InvalidPhoneException(String message) {
        super(message);
    }
}
