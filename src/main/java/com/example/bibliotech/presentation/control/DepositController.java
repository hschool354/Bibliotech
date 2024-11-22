package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.PaymentMethodDao;
import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.model.PaymentMethods;
import com.example.bibliotech.model.TransactionStatus;
import com.example.bibliotech.model.Transactions;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositController {
    @FXML private TextField txt_creditCard;
    @FXML private TextField txt_Name;
    @FXML private DatePicker datePicker_Expirationdate;
    @FXML private TextField txt_CVV;
    @FXML private TextField txt_amout;

    @FXML private Label lb_Recharge;
    @FXML private Label lb_Transactionfee;
    @FXML private Label lb_Total;

    @FXML private Button btn_ConfirmOrder;
    @FXML private Button btn_Cancel,btn_Category,btn_Home;

    private TransactionDao transactionDao = new TransactionDao();
    private PaymentMethodDao paymentMethodDao = new PaymentMethodDao();

    // Tỷ lệ phí giao dịch giả định
    private static final BigDecimal TRANSACTION_FEE_PERCENTAGE = new BigDecimal("0.02");
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("10000");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("100000000");
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";

    private static final String PAYMENT_METHOD_CREDIT_CARD = "CREDIT_CARD";

    @FXML
    private void initialize() {
        btn_ConfirmOrder.setOnAction(event -> handleDeposit());

        // Add input validators
        txt_amout.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txt_amout.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txt_CVV.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txt_CVV.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 4) {
                txt_CVV.setText(oldValue);
            }
        });
    }

    private void handleDeposit() {
        if (!validateInputs()) {
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(txt_amout.getText().trim());

            // Calculate fees
            BigDecimal transactionFee = amount.multiply(TRANSACTION_FEE_PERCENTAGE);
            BigDecimal totalAmount = amount.add(transactionFee);

            lb_Transactionfee.setText(transactionFee.toString());
            lb_Total.setText(totalAmount.toString());

            // Create transaction object
            int userId = SessionManager.getInstance().getCurrentUserId();
            Transactions transaction = new Transactions(
                    userId,
                    null,
                    TRANSACTION_TYPE_DEPOSIT,  // Fixed: Use constant instead of undefined variable
                    amount,
                    TransactionStatus.PENDING
            );

            // Create payment method object
            PaymentMethods paymentMethod = new PaymentMethods(
                    0,
                    0,
                    PAYMENT_METHOD_CREDIT_CARD,
                    txt_creditCard.getText().trim(),
                    txt_Name.getText().trim(),
                    datePicker_Expirationdate.getValue().toString(),
                    txt_CVV.getText().trim(),
                    null
            );

            // Process transaction
            int transactionId = paymentMethodDao.addPaymentMethodWithTransaction(paymentMethod, transaction);

            if (transactionId > 0) {
                Stage stage = (Stage) btn_ConfirmOrder.getScene().getWindow();
                Scene scene = SceneCache.getScene("/com/example/bibliotech/SuccessDeposit.fxml");
                stage.setScene(scene);
                stage.show();
                handleCancel();
            } else {
                lb_Recharge.setText("Có lỗi xảy ra trong quá trình xử lý!");
            }
        } catch (Exception e) {
            lb_Recharge.setText("Lỗi: " + e.getMessage());
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        String creditCard = txt_creditCard.getText().trim();
        String cardHolder = txt_Name.getText().trim();
        String cvv = txt_CVV.getText().trim();
        String amountStr = txt_amout.getText().trim();

        // Basic validation
        if (creditCard.isEmpty() || cardHolder.isEmpty() ||
                datePicker_Expirationdate.getValue() == null ||
                cvv.isEmpty() || amountStr.isEmpty()) {
            lb_Recharge.setText("Vui lòng điền đầy đủ thông tin.");
            return false;
        }

        // Credit card validation (basic)
        if (!creditCard.matches("\\d{16}")) {
            lb_Recharge.setText("Số thẻ tín dụng không hợp lệ.");
            return false;
        }

        // CVV validation
        if (!cvv.matches("\\d{3,4}")) {
            lb_Recharge.setText("Mã CVV không hợp lệ.");
            return false;
        }

        // Expiration date validation
        if (datePicker_Expirationdate.getValue().isBefore(java.time.LocalDate.now())) {
            lb_Recharge.setText("Thẻ đã hết hạn.");
            return false;
        }

        return true;
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


    @FXML
    private void handleCancel() {
        // Xử lý khi người dùng hủy
        txt_creditCard.clear();
        txt_Name.clear();

        txt_CVV.clear();
        txt_amout.clear();
        lb_Recharge.setText("");
        lb_Transactionfee.setText("");
        lb_Total.setText("");
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
