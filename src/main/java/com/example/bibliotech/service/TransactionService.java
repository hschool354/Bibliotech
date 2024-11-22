package com.example.bibliotech.service;

import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Transactions;

import java.util.List;

public class TransactionService {
    private TransactionDao transactionDao = new TransactionDao();

    // Lấy tất cả giao dịch
    public List<Transactions> getAllTransactions() throws DatabaseException {
        return transactionDao.getAllTransactions();
    }

    // Thêm giao dịch mới
    public void addTransaction(Transactions transaction) throws DatabaseException {
        transactionDao.addTransaction(transaction);
    }

    // Lấy giao dịch theo ID
    public Transactions getTransactionById(int transactionId) throws DatabaseException {
        return transactionDao.getTransactionById(transactionId);
    }
}
