package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.model.PremiumPackages;
import com.example.bibliotech.model.TransactionStatus;
import com.example.bibliotech.model.Transactions;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.math.BigDecimal;

public class BuyPremiumController {
    @FXML
    private Label lb_packagename;
    @FXML
    private Label lb_PackagePrice;
    @FXML
    private Label lb_Balance;
    @FXML
    private Label lb_PackagePrice2;
    @FXML
    private Label lb_Transactionfee;
    @FXML
    private Label lb_Total;
    @FXML
    private Button btn_Buy;
    @FXML
    private Button btn_Back;

    private PremiumPackages selectedPackage;
    private BigDecimal userBalance;
    private final TransactionDao transactionDao = new TransactionDao();
    private static final BigDecimal TRANSACTION_FEE = new BigDecimal("0.00"); // Set your transaction fee here

    public void setPackageData(PremiumPackages premiumPackage, BigDecimal balance) {
        this.selectedPackage = premiumPackage;
        this.userBalance = balance;

        // Update UI elements
        lb_packagename.setText(premiumPackage.getPackageName());
        lb_PackagePrice.setText(String.format("$%.2f", premiumPackage.getPrice()));
        lb_PackagePrice2.setText(String.format("$%.2f", premiumPackage.getPrice()));
        lb_Balance.setText(String.format("$%.2f", userBalance));
        lb_Transactionfee.setText(String.format("$%.2f", TRANSACTION_FEE));

        BigDecimal total = premiumPackage.getPrice().add(TRANSACTION_FEE);
        lb_Total.setText(String.format("$%.2f", total));

        // Disable buy button if insufficient balance
        btn_Buy.setDisable(total.compareTo(balance) > 0);
    }

    @FXML
    private void handleBuyButton() {
        try {
            BigDecimal totalAmount = selectedPackage.getPrice().add(TRANSACTION_FEE);

            if (userBalance.compareTo(totalAmount) >= 0) {
                // Create transaction record
                Transactions transaction = new Transactions(
                        SessionManager.getInstance().getCurrentUser().getUserId(),
                        null, // No book ID for subscription
                        "SUBSCRIPTION",
                        totalAmount,
                        TransactionStatus.COMPLETED
                );

                // Add transaction to database
                transactionDao.addTransaction(transaction);

                // Show success scene
                Stage stage = (Stage) btn_Buy.getScene().getWindow();
                Scene scene = SceneCache.getScene("/com/example/bibliotech/SuccessDeposit.fxml");
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            showError("Transaction failed", e.getMessage());
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage stage = (Stage) btn_Back.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/Premium.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showError("Navigation failed", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}