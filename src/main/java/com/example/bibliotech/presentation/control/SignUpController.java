package com.example.bibliotech.presentation.control;

import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    private final UserService userService = new UserService();

    @FXML
    private Button btn_Login;

    @FXML
    private Button btn_Register;

    @FXML
    private Label sloganLabel;

    @FXML
    private TextField txt_Email,txt_UserName;

    @FXML
    private PasswordField txt_password,txt_ComfirmPassword;

    @FXML
    private Text text_ErrorEmail,text_ErrorUserName,text_ErrorPassword,text_ErrorConfirmPassword;

    @FXML
    private CheckBox chk_ShowPassword;

    @FXML
    public void handleLoginButton() {
        // Your login logic here
        changeScene("/com/example/bibliotech/login.fxml");

    }

    @FXML
    public void handleRegisterButton() {
        try {
            String email = txt_Email.getText();
            String username = txt_UserName.getText();
            String password = txt_password.getText();
            String confirmPassword = txt_ComfirmPassword.getText();

            // Reset error messages
            text_ErrorEmail.setText("");
            text_ErrorUserName.setText("");
            text_ErrorConfirmPassword.setText("");
            text_ErrorPassword.setText("");

            // Validation checks
            if (isEmailExists(email)) {
                text_ErrorEmail.setText("Email đã tồn tại!");
                return;
            }
            if (isUsernameExists(username)) {
                text_ErrorUserName.setText("Tên tài khoản đã tồn tại!");
                return;
            }
            if (!password.equals(confirmPassword)) {
                text_ErrorConfirmPassword.setText("Mật khẩu không khớp!");
                return;
            }
            if (!isPasswordStrong(password)) {
                text_ErrorPassword.setText("Mật khẩu yếu, cần mạnh hơn!");
                return;
            }

            // Create new user object
            Users newUser = new Users();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            // Attempt to create user
            userService.createUser(newUser);

            // Only show success message if user creation was successful
            showAlert("Đăng ký thành công!", "Bạn có thể đăng nhập ngay bây giờ.", Alert.AlertType.INFORMATION);
            changeScene("/com/example/bibliotech/login.fxml");

        } catch (DatabaseException e) {
            showAlert("Lỗi đăng ký",
                    "Không thể tạo tài khoản: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Lỗi hệ thống",
                    "Đã xảy ra lỗi không mong muốn: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private boolean isEmailExists(String email) {
        try {
            // Call UserService to check email existence
            return userService.isEmailExists(email);
        } catch (DatabaseException e) {
            showAlert("Lỗi kiểm tra email",
                    "Không thể kiểm tra email: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            return true; // Assume exists to prevent registration
        }
    }

    private boolean isUsernameExists(String username) {
        try {
            // Call UserService to check username existence
            return userService.isUsernameExists(username);
        } catch (DatabaseException e) {
            showAlert("Lỗi kiểm tra tên người dùng",
                    "Không thể kiểm tra tên người dùng: " + e.getMessage(),
                    Alert.AlertType.ERROR);
            return true; // Assume exists to prevent registration
        }
    }

    // Phương thức kiểm tra độ mạnh của mật khẩu
    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*].*");
    }

    // Phương thức tạo người dùng mới
    private void createUser(String email, String username, String password) {
        // Thực hiện lưu thông tin người dùng mới vào cơ sở dữ liệu
        // Đặt registration_status là PENDING
    }

    @FXML
    public void handleShowPassword() {
        // Nếu CheckBox được chọn, hiển thị mật khẩu
        if (chk_ShowPassword.isSelected()) {
            txt_password.setPromptText(""); // Xóa prompt
            txt_password.setText(txt_password.getText()); // Hiển thị mật khẩu
            txt_ComfirmPassword.setPromptText(""); // Xóa prompt
            txt_ComfirmPassword.setText(txt_ComfirmPassword.getText()); // Hiển thị mật khẩu
        } else {
            txt_password.setText(txt_password.getText()); // Giữ nguyên mật khẩu
            txt_ComfirmPassword.setText(txt_ComfirmPassword.getText()); // Giữ nguyên mật khẩu
        }
    }

    private void changeScene(String fxmlPath) {
        try {
            System.out.println("Login button clicked!");

            // Lấy stage hiện tại
            Stage stage = (Stage) btn_Login.getScene().getWindow();

            // Sử dụng SceneCache để lấy scene
            Scene scene = SceneCache.getScene(fxmlPath);

            // Thiết lập Scene mới
            stage.setScene(scene);

            // Áp dụng hiệu ứng chuyển cảnh
            SceneTransitionEffect.applyTransitionEffect((Pane) scene.getRoot());

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypewriterEffect effect = new TypewriterEffect("READ A BOOK, LIVE A\nTHOUSAND LIVES", sloganLabel, 120, true);
        effect.play();
    }
}
