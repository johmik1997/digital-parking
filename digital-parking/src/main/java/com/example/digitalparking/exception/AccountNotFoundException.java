package com.example.digitalparking.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(){
        super("No Account Found With The Credentials Provided!");
    }
}
