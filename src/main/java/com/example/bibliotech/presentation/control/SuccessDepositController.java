package com.example.bibliotech.presentation.control;

import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.model.Transactions;
import com.example.bibliotech.utils.SessionManager;
import javafx.stage.Stage;

import javafx.scene.control.Button;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SuccessDepositController  {
    @FXML
    private Label lb_TransactionId;

    @FXML
    private Label lb_Amount;

    @FXML
    private Label lb_TransactionDate;

    @FXML private Button btn_backtoHome,btn_Home,btn_Category;


    private final TransactionDao transactionDao;
    private final SessionManager sessionManager;

    public SuccessDepositController() {
        this.transactionDao = new TransactionDao();
        this.sessionManager = SessionManager.getInstance();
    }

    @FXML
    public void initialize() {
        loadLatestTransaction();
    }

    private void loadLatestTransaction() {
        try {
            int currentUserId = sessionManager.getCurrentUserId();
            List<Transactions> userTransactions = transactionDao.getAllTransactions()
                    .stream()
                    .filter(t -> t.getUserId() == currentUserId)
                    .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                    .collect(Collectors.toList());

            if (!userTransactions.isEmpty()) {
                Transactions latestTransaction = userTransactions.get(0);

                lb_TransactionId.setText(String.valueOf(latestTransaction.getTransactionId()));
                lb_Amount.setText(String.format("$%.2f", latestTransaction.getAmount()));
                lb_TransactionDate.setText(latestTransaction.getTransactionDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error - maybe show an alert
        }
    }

    @FXML
    private void handleMyWalletButton() {
        changeScene("/com/example/bibliotech/myWallet.fxml");
    }

    @FXML
    private void handleHomeButton() {
        changeScene("/com/example/bibliotech/home_1.fxml");
    }

    @FXML
    private void handleCategoryButton() {
        changeScene("/com/example/bibliotech/Category_1.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Home.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}