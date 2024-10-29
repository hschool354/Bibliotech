//AccountSetting
package com.example.bibliotech.presentation.control;

import com.example.bibliotech.utils.ViewLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountSetting {
    private static final Logger logger = Logger.getLogger(AccountSetting.class.getName());

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button btn_profile, btn_security, btn_notifications, btn_brandedContent;

    private ViewName currentView;

    @FXML
    public void initialize() {
        if (btn_profile == null || btn_security == null || btn_notifications == null || btn_brandedContent == null) {
            logger.severe("One or more buttons are null");
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
    public void handleNotificationsButton() {
        loadView(ViewName.NOTIFICATIONS);
    }

    @FXML
    public void handleBrandedContentButton() {
        loadView(ViewName.BRANDED_CONTENT);
    }

    private void loadView(ViewName viewName) {
        try {
            logger.info("Loading view: " + viewName);
            currentView = viewName;
            contentArea.getChildren().clear();

            ViewLoader.ViewLoadResult loadResult = ViewLoader.loadView(viewName.getViewName(), true);

            AnchorPane view = loadResult.getView();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            contentArea.getChildren().setAll(view);

            setActiveButton(getButtonByView(viewName));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading view: " + viewName, e);
            showErrorView();
        }
    }

    private void showErrorView() {
        logger.severe("Error loading content. Please try again.");
        contentArea.getChildren().clear();
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
        btn_profile.getStyleClass().remove("active-button");
        btn_security.getStyleClass().remove("active-button");
        btn_notifications.getStyleClass().remove("active-button");
        btn_brandedContent.getStyleClass().remove("active-button");

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-button");
        }
    }

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
