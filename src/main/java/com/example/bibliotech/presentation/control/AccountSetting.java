package com.example.bibliotech.presentation.control;

import com.example.bibliotech.utils.ViewLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import java.io.IOException;

public class AccountSetting {
    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button btn_profile, btn_security, btn_notifications, btn_brandedContent;

    @FXML
    public void initialize() {
        // Kiểm tra null cho tất cả các nút
        if (btn_profile == null || btn_security == null || btn_notifications == null || btn_brandedContent == null) {
            System.err.println("One or more buttons are null");
        } else {
            loadView(ViewName.PROFILE);
            setActiveButton(btn_profile);
        }
    }

    @FXML
    public void handleProfileButton() {
        loadView(ViewName.PROFILE);
    }

    @FXML
    public void handleSecurityButton() {
        loadView(ViewName.SECURITY);
    }

    @FXML
    private void handleNotificationsButton() {
        loadView(ViewName.NOTIFICATIONS);
    }

    @FXML
    private void handleBrandedContentButton() {
        loadView(ViewName.BRANDED_CONTENT);
    }

    private void loadView(ViewName viewName) {
        try {
            System.out.println("Loading view: " + viewName);

            // Sử dụng ViewLoader để tải view
            AnchorPane view = ViewLoader.loadView(viewName.getViewName());
            contentArea.getChildren().setAll(view);
            setActiveButton(getButtonByView(viewName));
        } catch (IOException e) {
            System.err.println("Error loading view: " + viewName);
            e.printStackTrace();
        }
    }

    private Button getButtonByView(ViewName viewName) {
        switch (viewName) {
            case PROFILE:
                return btn_profile;
            case SECURITY:
                return btn_security;
            case NOTIFICATIONS:
                return btn_notifications;
            case BRANDED_CONTENT:
                return btn_brandedContent;
            default:
                return null;
        }
    }

    private void setActiveButton(Button activeButton) {
        // Reset tất cả các nút về kiểu mặc định
        btn_profile.getStyleClass().remove("active-button");
        btn_security.getStyleClass().remove("active-button");
        btn_notifications.getStyleClass().remove("active-button");
        btn_brandedContent.getStyleClass().remove("active-button");

        // Đặt kiểu hoạt động cho nút đã nhấn
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-button");
        }
    }

    // Enum để quản lý tên view
    public enum ViewName {
        PROFILE("accountSettingProfile"),
        SECURITY("accountSettingSecurity"),
        NOTIFICATIONS("notifications"),
        BRANDED_CONTENT("branded-content");

        private final String viewName;

        ViewName(String viewName) {
            this.viewName = viewName;
        }

        public String getViewName() {
            return viewName;
        }
    }
}
