package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.UserSubscriptionDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.UserSubscriptions;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MyPremiumController implements Initializable {
    @FXML private Label NamePremium;
    @FXML private Pane paneContainer;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label durationLabel;
    @FXML private Label freeReadsLabel;
    @FXML private Label statusLabel;
    @FXML private RadioButton autoRenewOn;
    @FXML private RadioButton autoRenewOff;

    private final UserSubscriptionDao userSubscriptionDao = new UserSubscriptionDao();
    private UserSubscriptions currentSubscription;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupToggleGroup();
        loadUserSubscription();
    }

    private void setupToggleGroup() {
        ToggleGroup autoRenewGroup = new ToggleGroup();
        autoRenewOn.setToggleGroup(autoRenewGroup);
        autoRenewOff.setToggleGroup(autoRenewGroup);

        // Handle auto-renew changes
        autoRenewGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && currentSubscription != null) {
                boolean newAutoRenew = newValue == autoRenewOn;
                updateAutoRenewStatus(newAutoRenew);
            }
        });
    }

    private void loadUserSubscription() {
        try {
            int userId = SessionManager.getInstance().getCurrentUserId();
            currentSubscription = userSubscriptionDao.getUserSubscription(userId);

            if (currentSubscription != null) {
                updateUI();
            } else {
                showNoSubscriptionMessage();
            }
        } catch (DatabaseException e) {
            showError("Error loading subscription", e.getMessage());
        }
    }

    private void updateUI() {
        NamePremium.setText(currentSubscription.getPackageName());
        startDateLabel.setText(currentSubscription.getStartDate().format(dateFormatter));
        endDateLabel.setText(currentSubscription.getEndDate().format(dateFormatter));
        durationLabel.setText(currentSubscription.getDuration() + " months");
        freeReadsLabel.setText(String.valueOf(currentSubscription.getFreeReadsRemaining()));

        // Set status based on subscription end date
        boolean isActive = currentSubscription.getEndDate().isAfter(java.time.LocalDate.now());
        statusLabel.setText(isActive ? "Active" : "Expired");
        statusLabel.setStyle(isActive ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Set auto-renew radio buttons
        if (currentSubscription.isAutoRenew()) {
            autoRenewOn.setSelected(true);
        } else {
            autoRenewOff.setSelected(true);
        }
    }

    private void updateAutoRenewStatus(boolean newStatus) {
        try {
            userSubscriptionDao.updateAutoRenew(
                    currentSubscription.getUserId(),
                    currentSubscription.getPackageId(),
                    newStatus
            );
            currentSubscription.setAutoRenew(newStatus);
        } catch (DatabaseException e) {
            showError("Error updating auto-renew status", e.getMessage());
            // Revert the radio button selection
            if (newStatus) {
                autoRenewOff.setSelected(true);
            } else {
                autoRenewOn.setSelected(true);
            }
        }
    }

    private void showNoSubscriptionMessage() {
        NamePremium.setText("No active subscription");
        startDateLabel.setText("-");
        endDateLabel.setText("-");
        durationLabel.setText("-");
        freeReadsLabel.setText("0");
        statusLabel.setText("Inactive");
        statusLabel.setStyle("-fx-text-fill: red;");
        autoRenewOn.setDisable(true);
        autoRenewOff.setDisable(true);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}