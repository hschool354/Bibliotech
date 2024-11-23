package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.PremiumPackageDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.PremiumPackages;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PremiumController implements Initializable {
    @FXML private ToggleButton toggleSwitch;
    @FXML private VBox basicFeaturesBox;
    @FXML private VBox standardFeaturesBox;
    @FXML private VBox premiumFeaturesBox;
    @FXML private Button btn_Home;
    @FXML private Button btn_Category,btn_Wallet;

    @FXML private Button btn_choseBasic;
    @FXML private Button btn_choseStandard;
    @FXML private Button btn_chosePremium;

    @FXML private Button btn_MyPremium;

    private final PremiumPackageDao premiumPackageDao = new PremiumPackageDao();
    private List<PremiumPackages> packages;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadPremiumPackages();
            setupToggleSwitch();
            displayPackages();
        } catch (DatabaseException e) {
            showError("Error loading premium packages", e.getMessage());
        }
    }

    private void loadPremiumPackages() throws DatabaseException {
        String billingCycle = toggleSwitch.isSelected() ? "YEARLY" : "MONTHLY";
        packages = premiumPackageDao.getPackagesByBillingCycle(billingCycle);
    }

    private void setupToggleSwitch() {
        toggleSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            try {
                String billingCycle = newVal ? "YEARLY" : "MONTHLY";
                packages = premiumPackageDao.getPackagesByBillingCycle(billingCycle);
                displayPackages();
            } catch (DatabaseException e) {
                showError("Error updating packages", e.getMessage());
            }
        });
    }

    private void displayPackages() {
        clearAllFeatureBoxes();
        for (int i = 0; i < packages.size(); i++) {
            PremiumPackages pkg = packages.get(i);
            VBox featuresBox = getFeatureBox(i);
            if (featuresBox != null) {
                updatePackageDisplay(featuresBox.getParent(), pkg);
                displayPackageFeatures(featuresBox, pkg);
            }
        }
    }

    private void updatePackageDisplay(Node packagePane, PremiumPackages pkg) {
        if (packagePane instanceof Pane) {
            Pane pane = (Pane) packagePane;
            Label nameLabel = (Label) pane.lookup(".package-name");
            Label priceLabel = (Label) pane.lookup(".price-label");
            Label periodLabel = (Label) pane.lookup(".period-label");

            if (nameLabel != null) {
                nameLabel.setText(pkg.getPackageName());
            }
            if (priceLabel != null) {
                priceLabel.setText(String.format("$ %.2f", pkg.getPrice()));
            }
            if (periodLabel != null) {
                periodLabel.setText("/" + pkg.getBillingCycle().toString().toLowerCase());
            }
        }
    }

    private void displayPackageFeatures(VBox featuresBox, PremiumPackages pkg) {
        featuresBox.getChildren().clear();
        featuresBox.setSpacing(15); // Add spacing between features

        for (String feature : pkg.getFeatures()) {
            HBox featureContainer = new HBox(10); // Container with 10px spacing
            featureContainer.setAlignment(Pos.CENTER_LEFT);

            // Load check icon using getClass().getResource()
            String iconPath = "/icons/icons_check1.png"; // Update this path to match your project structure
            Image checkImage = new Image(getClass().getResourceAsStream(iconPath));
            ImageView checkIcon = new ImageView(checkImage);
            checkIcon.setFitWidth(16);
            checkIcon.setFitHeight(16);

            Label featureLabel = new Label(feature);
            featureLabel.getStyleClass().add("feature-label");
            featureLabel.setWrapText(true);

            featureContainer.getChildren().addAll(checkIcon, featureLabel);
            featuresBox.getChildren().add(featureContainer);
        }
    }

    private VBox getFeatureBox(int index) {
        return switch (index) {
            case 0 -> basicFeaturesBox;
            case 1 -> standardFeaturesBox;
            case 2 -> premiumFeaturesBox;
            default -> null;
        };
    }

    private void clearAllFeatureBoxes() {
        if (basicFeaturesBox != null) basicFeaturesBox.getChildren().clear();
        if (standardFeaturesBox != null) standardFeaturesBox.getChildren().clear();
        if (premiumFeaturesBox != null) premiumFeaturesBox.getChildren().clear();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleChoseBasic() {
        if (!packages.isEmpty()) {
            loadBuyPremiumScene(packages.get(0));
        }
    }

    @FXML
    private void handleChoseStandard() {
        if (packages.size() > 1) {
            loadBuyPremiumScene(packages.get(1));
        }
    }

    @FXML
    private void handleChosePremium() {
        if (packages.size() > 2) {
            loadBuyPremiumScene(packages.get(2));
        }
    }

    private void loadBuyPremiumScene(PremiumPackages selectedPackage) {
        try {
            Stage stage = (Stage) btn_choseBasic.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/BuyPremium.fxml");

            // Get the controller and set the package data
            BuyPremiumController buyPremiumController = SceneCache.getController("/com/example/bibliotech/BuyPremium.fxml");
            if (buyPremiumController != null) {
                // Get user balance from SessionManager
                int currentUserId = SessionManager.getInstance().getCurrentUserId();
                BigDecimal balance = new UserService().getUserBalance(currentUserId);
                buyPremiumController.setPackageData(selectedPackage, balance);
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading buy premium scene" + e);
        }
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
    private void handleWalletButton() {
        changeScene("/com/example/bibliotech/myWallet.fxml");
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

    @FXML
    private void handleMyPremium() {
        try {
            Stage stage = (Stage) btn_MyPremium.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/MyPremium.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error", "Could not load My Premium page");
        }
    }
}