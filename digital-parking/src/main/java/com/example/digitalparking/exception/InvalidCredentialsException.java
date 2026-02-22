package com.example.digitalparking.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String s){
        super("Invalid UserName or Password");
    }
}
