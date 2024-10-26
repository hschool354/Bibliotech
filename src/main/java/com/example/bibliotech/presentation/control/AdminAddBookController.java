package com.example.bibliotech.presentation.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminAddBookController {
    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private FlowPane selectedCategoriesPane;

    private Set<String> selectedCategories = new HashSet<>();

    @FXML
    public void initialize() {
        // Khởi tạo danh sách categories
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Fiction",
                "Non-fiction",
                "Children's",
                "Young Adult",
                "Classic",
                "Mystery",
                "Science Fiction",
                "Romance",
                "Biography",
                "Academic"
        );

        categoryComboBox.setItems(categories);

        // Xử lý sự kiện khi chọn category
        categoryComboBox.setOnAction(e -> {
            String selectedCategory = categoryComboBox.getValue();
            if (selectedCategory != null && !selectedCategories.contains(selectedCategory)) {
                addCategoryLabel(selectedCategory);
                selectedCategories.add(selectedCategory);
                categoryComboBox.setValue(null); // Reset combobox selection
            }
        });
    }

    private void addCategoryLabel(String category) {
        Label label = new Label(category);
        label.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 3 7 3 7; " +
                "-fx-background-radius: 3; -fx-margin: 2;");

        // Thêm nút xóa
        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666;");

        HBox categoryBox = new HBox(5, label, removeButton);
        categoryBox.setAlignment(Pos.CENTER);

        removeButton.setOnAction(e -> {
            selectedCategoriesPane.getChildren().remove(categoryBox);
            selectedCategories.remove(category);
        });

        selectedCategoriesPane.getChildren().add(categoryBox);
    }

    // Phương thức để lấy danh sách categories đã chọn
    public List<String> getSelectedCategories() {
        return new ArrayList<>(selectedCategories);
    }

}
