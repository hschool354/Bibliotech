package com.example.bibliotech.presentation.components;

import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;

public class TagInputField extends FlowPane {
    private final TextField inputField;
    private final List<String> tags;
    private List<String> suggestions;

    public TagInputField() {
        this.tags = new ArrayList<>();
        this.suggestions = new ArrayList<>();

        // Setup main container
        this.setPadding(new Insets(5));
        this.setHgap(5);
        this.setVgap(5);

        // Create and setup input field
        inputField = new TextField();
        inputField.setPromptText("add a tag here");
        inputField.setPrefWidth(150);

        // Setup key listeners for input field
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !inputField.getText().trim().isEmpty()) {
                addTag(inputField.getText().trim());
                inputField.clear();
            } else if (event.getCode() == KeyCode.BACK_SPACE && inputField.getText().isEmpty() && !tags.isEmpty()) {
                removeLastTag();
            }
        });

        // Add input field to container
        this.getChildren().add(inputField);
    }

    private void addTag(String tagText) {
        if (!tags.contains(tagText)) {
            tags.add(tagText);

            // Create tag visual element
            HBox tagContainer = new HBox(5);
            tagContainer.getStyleClass().add("tag-container");

            Label tagLabel = new Label(tagText);
            Button removeButton = new Button("×");
            removeButton.getStyleClass().add("tag-remove-button");

            // Setup remove button action
            removeButton.setOnAction(e -> {
                tags.remove(tagText);
                this.getChildren().remove(tagContainer);
            });

            tagContainer.getChildren().addAll(tagLabel, removeButton);

            // Add new tag before the input field
            this.getChildren().add(this.getChildren().size() - 1, tagContainer);
        }
    }

    private void removeLastTag() {
        if (!tags.isEmpty()) {
            tags.remove(tags.size() - 1);
            this.getChildren().remove(this.getChildren().size() - 2);
        }
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;

        // Create autocomplete popup
        ContextMenu contextMenu = new ContextMenu();

        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                List<MenuItem> menuItems = new ArrayList<>();
                suggestions.stream()
                        .filter(s -> s.toLowerCase().contains(newValue.toLowerCase()))
                        .forEach(s -> {
                            MenuItem item = new MenuItem(s);
                            item.setOnAction(e -> {
                                addTag(s);
                                inputField.clear();
                                contextMenu.hide();
                            });
                            menuItems.add(item);
                        });

                contextMenu.getItems().clear();
                contextMenu.getItems().addAll(menuItems);

                if (!menuItems.isEmpty() && !contextMenu.isShowing()) {
                    contextMenu.show(inputField, Side.BOTTOM, 0, 0);
                }
            } else {
                contextMenu.hide();
            }
        });
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public void clear() {
        tags.clear();
        this.getChildren().clear();
        this.getChildren().add(inputField);
    }

    // Thêm phương thức public này
    public void addPublicTag(String tagText) {
        addTag(tagText);
    }
}