package com.example.digitalparking.ServiceImpl.ServiceImpl;


import com.example.digitalparking.Entity.Transaction;
import com.example.digitalparking.Repository.TransactionRepository;
import com.example.digitalparking.Service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Transaction findByTxRef(String txRef) {
        return transactionRepository.findByTxRef(txRef);
    }
}
