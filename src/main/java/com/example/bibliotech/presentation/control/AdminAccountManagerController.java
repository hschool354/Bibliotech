package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.UserDao;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.DataManager;
import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAccountManagerController implements Initializable {

    @FXML
    private ToggleButton btn_Member, btn_Admin;

    @FXML
    private Button btn_addAccount, btn_Refresh;

    @FXML
    private TableView<Users> tableView;

    @FXML
    private TableColumn<Users, String> idColumn;

    @FXML
    private TableColumn<Users, String> usernameColumn;

    @FXML
    private TableColumn<Users, String> emailColumn;

    @FXML
    private TableColumn<Users, String> statusColumn;

    @FXML
    private TableColumn<Users, String> accountTypeColumn;

    @FXML
    private TableColumn<Users, Void> actionColumn;

    private final UserService userService = new UserService();
    private boolean showingMembers = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupToggleButtons();
        setupColumns();
        loadUsers();
    }

    private void setupToggleButtons() {
        ToggleGroup group = new ToggleGroup();
        btn_Member.setToggleGroup(group);
        btn_Admin.setToggleGroup(group);

        btn_Member.setSelected(true);

        btn_Member.setOnAction(e -> {
            showingMembers = true;
            loadUsers();
        });

        btn_Admin.setOnAction(e -> {
            showingMembers = false;
            loadUsers();
        });
    }

    @FXML
    public void handleRefreshButton() {
        loadUsers();
    }

    @FXML
    public void handleAddAccountButton() {
        try {
            Stage stage = (Stage) btn_addAccount.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/AdminAddAccount.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading add account scene");
            e.printStackTrace();
            showErrorAlert("Error", "Could not load add account screen");
        }
    }

    private void setupColumns() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID Column
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getUserId())));
        idColumn.setStyle("-fx-alignment: CENTER;");
        idColumn.setPrefWidth(100);

        // Username Column
        usernameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));
        usernameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        usernameColumn.setPrefWidth(150);

        // Email Column
        emailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));
        emailColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        emailColumn.setPrefWidth(200);

        // Status Column
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRegistrationStatus()));
        statusColumn.setStyle("-fx-alignment: CENTER;");
        statusColumn.setPrefWidth(100);

        // Account Type Column
        accountTypeColumn.setCellValueFactory(cellData -> {
            boolean isAdmin = cellData.getValue().isAdmin(); // Sử dụng getter boolean
            return new SimpleStringProperty(isAdmin ? "Admin" : "User");
        });
        accountTypeColumn.setStyle("-fx-alignment: CENTER;");
        accountTypeColumn.setPrefWidth(100);

        // Action Column
        actionColumn.setCellFactory(tc -> new TableCell<>() {
            final Button detailBtn = new Button("Detail");

            {
                detailBtn.getStyleClass().add("detail-button");
                detailBtn.setStyle("""
                    -fx-background-color: #4a90e2;
                    -fx-text-fill: white;
                    -fx-padding: 5 15;
                    -fx-cursor: hand;
                """);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Users user = getTableView().getItems().get(getIndex());
                    detailBtn.setOnAction(e -> handleDetailClick(user));
                    setGraphic(detailBtn);
                }
            }
        });
        actionColumn.setPrefWidth(100);
        actionColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void loadUsers() {
        tableView.getItems().clear();
        try {
            List<Users> allUsers = userService.getAllUsers(); // Sử dụng userService đã khai báo
            List<Users> filteredUsers = allUsers.stream()
                    .filter(user -> showingMembers ? !user.isAdmin() : user.isAdmin())
                    .collect(Collectors.toList());

            tableView.getItems().addAll(filteredUsers);
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Error", "Could not load user data");
        }
    }

    private void handleDetailClick(Users user) {
        /*try {
            DataManager.getInstance().setSelectedUser(user);
            Stage stage = (Stage) tableView.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/AdminDetailAccount.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading detail scene");
            e.printStackTrace();
            showErrorAlert("Error", "Could not load user details");
        }*/
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}