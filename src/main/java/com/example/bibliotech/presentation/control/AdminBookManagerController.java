package com.example.bibliotech.presentation.control;

import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.model.Books;
import com.example.bibliotech.utils.DataManager;
import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

public class AdminBookManagerController implements Initializable {

    @FXML
    private Button btn_addBook,btn_Refresh,btn_User;

    @FXML
    private TableView<Books> tableView;

    @FXML
    private TableColumn<Books, Integer> idColumn;

    @FXML
    private TableColumn<Books, ImageView> imageColumn;

    @FXML
    private TableColumn<Books, String> titleColumn;

    @FXML
    private TableColumn<Books, String> infoColumn;

    @FXML
    private TableColumn<Books, Void> actionColumn;

    private BooksDao booksDao = new BooksDao();

    @FXML
    public void handleAddBookButton() {
        changeScene("/com/example/bibliotech/AdminAddBook.fxml");
    }

    @FXML
    public void handlAccountManagerButton() {
        changeScene("/com/example/bibliotech/AdminAccountManager.fxml");
    }

    @FXML
    public void handleHomeButton() {
        changeScene("/com/example/bibliotech/AdminDashboard.fxml");
    }

    @FXML
    public void handleRefreshButton() {
        try {
            // Đợi 500ms để đảm bảo file đã được copy xong
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadBooks();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadBooks();
    }

    private void setupColumns() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Cột ID
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getBookId()));
        idColumn.setStyle("-fx-alignment: CENTER;");
        idColumn.setPrefWidth(80);

        // Cột Hình ảnh
        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(loadImage(cellData.getValue().getCoverImageUrl()));
            imageView.setFitHeight(120);
            imageView.setFitWidth(80);
            imageView.setPreserveRatio(true);
            return new SimpleObjectProperty<>(imageView);
        });
        imageColumn.setStyle("-fx-alignment: CENTER;");
        imageColumn.setPrefWidth(120);

        // Cột Tiêu đề
        titleColumn.setCellValueFactory(cellData -> {
            Books book = cellData.getValue();
            return new SimpleStringProperty(
                    book.getTitle() + "\n\nby " + book.getAuthor() +
                            "\n\nLanguage: " + book.getLanguage()
            );
        });
        titleColumn.setCellFactory(tc -> {
            TableCell<Books, String> cell = new TableCell<>() {
                private final VBox vbox = new VBox(5);
                private final Label titleLabel = new Label();
                private final Label authorLabel = new Label();
                private final Label languageLabel = new Label();

                {
                    vbox.setPadding(new Insets(5));
                    vbox.getChildren().addAll(titleLabel, authorLabel, languageLabel);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        String[] parts = item.split("\n\n");
                        titleLabel.setText(parts[0]);
                        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                        authorLabel.setText(parts[1]);
                        languageLabel.setText(parts[2]);
                        setGraphic(vbox);
                    }
                }
            };
            return cell;
        });
        titleColumn.setPrefWidth(300);

        // Cột Thông tin
        infoColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Books book = getTableView().getItems().get(getIndex());
                    VBox vbox = new VBox(5);
                    vbox.setPadding(new Insets(5));

                    Label originalPriceLabel = new Label(String.format("Original Price: $%.2f", book.getOriginalPrice()));
                    vbox.getChildren().add(originalPriceLabel);

                    if (book.getDiscountedPrice() != null && book.getDiscountedPrice() > 0) {
                        Label discountedPriceLabel = new Label(String.format("Sale Price: $%.2f", book.getDiscountedPrice()));
                        discountedPriceLabel.setStyle("-fx-text-fill: red;");
                        vbox.getChildren().add(discountedPriceLabel);
                    }

                    Label quantityLabel = new Label("Stock: " + book.getStockQuantity());
                    vbox.getChildren().add(quantityLabel);

                    setGraphic(vbox);
                }
            }
        });
        infoColumn.setPrefWidth(200);
        infoColumn.setStyle("-fx-alignment: CENTER-LEFT;");

        // Cột Action
        actionColumn.setCellFactory(tc -> new TableCell<>() {
            final Button detailBtn = new Button("Detail");

            {
                detailBtn.getStyleClass().add("detail-button");
                detailBtn.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Books book = getTableView().getItems().get(getIndex());
                    detailBtn.setOnAction(e -> handleDetailClick(book));
                    setGraphic(detailBtn);
                }
            }
        });
        actionColumn.setPrefWidth(100);
        actionColumn.setStyle("-fx-alignment: CENTER;");
    }

    private Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return getPlaceholderImage();
        }

        try {
            String resourcePath = "/book-covers/" + imagePath;
            URL imageUrl = getClass().getResource(resourcePath);

            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm(), true); // true enables background loading
            } else {
                System.err.println("Image not found: " + imagePath);
                return getPlaceholderImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            return getPlaceholderImage();
        }
    }

    private Image getPlaceholderImage() {
        try {
            URL placeholderUrl = getClass().getResource("/book-covers/placeholder.png");
            if (placeholderUrl != null) {
                return new Image(placeholderUrl.toExternalForm());
            } else {
                System.err.println("Placeholder image not found!");
                // Return a 1x1 transparent image as last resort
                return new Image(new ByteArrayInputStream(new byte[0]));
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image");
            e.printStackTrace();
            // Return a 1x1 transparent image as last resort
            return new Image(new ByteArrayInputStream(new byte[0]));
        }
    }

    private void loadBooks() {
        tableView.getItems().clear();
        tableView.getItems().addAll(booksDao.getAllBooksForDisplay());
    }

    private void handleDetailClick(Books book) {
        try {
            // Store the selected book in DataManager
            DataManager.getInstance().setSelectedBook(book);

            // Navigate to detail screen
            Stage stage = (Stage) tableView.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/AdminDetailBook.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading detail scene");
            e.printStackTrace();
        }
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_addBook.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
