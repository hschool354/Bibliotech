package com.example.bibliotech.service;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.dao.PremiumTransactionDao;
import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.dao.UserSubscriptionDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PremiumSubscriptionService {
    private final TransactionDao transactionDao;
    private final PremiumTransactionDao premiumTransactionDao;
    private final UserSubscriptionDao subscriptionDao;

    public PremiumSubscriptionService() {
        this.transactionDao = new TransactionDao();
        this.premiumTransactionDao = new PremiumTransactionDao();
        this.subscriptionDao = new UserSubscriptionDao();
    }

//    public void processPremiumPurchase(int userId, PremiumPackages selectedPackage,
//                                       BigDecimal totalAmount) throws DatabaseException {
//        Connection conn = null;
//        try {
//            conn = DatabaseConfig.getConnection();
//            conn.setAutoCommit(false);  // Start transaction
//
//            // 1. Create main transaction
//            Transactions transaction = new Transactions(
//                    userId,
//                    null,
//                    "SUBSCRIPTION",
//                    totalAmount,
//                    TransactionStatus.PENDING
//            );
//
//            int transactionId = transactionDao.createTransactionAndGetId(transaction);
//
//            // 2. Create premium transaction record
//            PremiumTransactions premiumTx = new PremiumTransactions(transactionId, selectedPackage.getPackageId());
//            premiumTransactionDao.createPremiumTransaction(premiumTx);
//
//            // 3. Create subscription
//            UserSubscriptions subscription = new UserSubscriptions(
//                    userId,
//                    selectedPackage.getPackageId(),
//                    LocalDate.now(),
//                    selectedPackage.getDuration(),
//                    true,
//                    selectedPackage.getFreeReads()
//            );
//            subscriptionDao.createSubscription(subscription);
//
//            // 4. Update transaction status to completed
//            transactionDao.updateTransactionStatus(transactionId, TransactionStatus.COMPLETED);
//
//            conn.commit();  // Commit all changes
//
//        } catch (Exception e) {
//            if (conn != null) {
//                try {
//                    conn.rollback();  // Rollback in case of error
//                } catch (SQLException ex) {
//                    throw new DatabaseException("Error rolling back transaction", ex);
//                }
//            }
//            throw new DatabaseException("Failed to process premium purchase", e);
//        } finally {
//            if (conn != null) {
//                try {
//                    conn.setAutoCommit(true);
//                    conn.close();
//                } catch (SQLException e) {
//                    // Log error
//                }
//            }
//        }
}

