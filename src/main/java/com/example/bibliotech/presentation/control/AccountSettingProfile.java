package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.UserDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class AccountSettingProfile {

    @FXML private TextField txt_profilePictureURL, txt_userID, txt_userName,
            txt_firstName, txt_lastName, txt_phone, txt_address;
    @FXML private TextArea txtArea_bio;
    @FXML private Button btn_saveChanges, btn_ChooseImage;
    @FXML private DatePicker datePicker_dob;
    @FXML private ComboBox<String> comboBox_nationality;
    @FXML private ImageView image_ProfilePicture;

    private UserService userService;


    private void loadCountries() {
        List<String> countries = Arrays.asList(
                "Vietnam", "United States", "United Kingdom", "Japan", "China",
                "South Korea", "Singapore", "Malaysia", "Thailand", "Indonesia",
                "Australia", "Canada", "France", "Germany", "Italy", "Other"
        );
        comboBox_nationality.setItems(FXCollections.observableArrayList(countries));
    }

    @FXML
    public void initialize() {
        userService = new UserService();
        loadCountries();
    }



}
