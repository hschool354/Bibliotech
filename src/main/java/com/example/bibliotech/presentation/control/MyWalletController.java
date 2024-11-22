package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.math.BigDecimal;

public class MyWalletController implements Initializable {
    @FXML
    private Label lb_TotalBalance;

    @FXML
    private Button btn_Deposit,btn_Home,btn_Category;

    private final UserService userService;
    private final SessionManager sessionManager;

    public MyWalletController() {
        this.userService = new UserService();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserBalance();
    }

    private void loadUserBalance() {
        int currentUserId = sessionManager.getCurrentUserId();
        BigDecimal balance = userService.getUserBalance(currentUserId);

        // Format the balance with 2 decimal places and currency symbol
        String formattedBalance = String.format("$%.2f", balance);
        lb_TotalBalance.setText(formattedBalance);
    }

    @FXML
    private void handleDepositButton() {
        changeScene("/com/example/bibliotech/Deposit.fxml");
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
            Stage stage = (Stage) btn_Deposit.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}