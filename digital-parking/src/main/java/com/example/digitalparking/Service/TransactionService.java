package com.example.digitalparking.Service;


import com.example.digitalparking.Entity.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    Transaction findByTxRef(String txRef);
}