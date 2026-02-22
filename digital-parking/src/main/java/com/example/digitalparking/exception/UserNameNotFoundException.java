package com.example.digitalparking.exception;

public class UserNameNotFoundException extends RuntimeException{
    public UserNameNotFoundException(String msg) {
        super(msg);
    }
}
