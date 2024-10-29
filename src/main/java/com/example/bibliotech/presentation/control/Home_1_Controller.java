package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.TopBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import com.example.bibliotech.utils.ViewLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Home_1_Controller implements Initializable {
    private static final Logger logger = Logger.getLogger(Home_1_Controller.class.getName());
    @FXML
    private Button btn_Search;

    @FXML private Button btn_PopularBook1;
    @FXML private Button btn_PopularBook2;
    @FXML private Button btn_PopularBook3;
    @FXML private Button btn_PopularBook4;

    @FXML private ImageView image_1;
    @FXML private ImageView image_2;
    @FXML private ImageView image_3;
    @FXML private ImageView image_4;

    @FXML private Label lbl_Tittle1;
    @FXML private Label lbl_Tittle2;
    @FXML private Label lbl_Tittle3;
    @FXML private Label lbl_Tittle4;

    @FXML private Label lbl_Price1;
    @FXML private Label lbl_Price2;
    @FXML private Label lbl_Price3;
    @FXML private Label lbl_Price4;

    @FXML private AnchorPane contentArea;

    private BooksDao bookDAO;
    private List<TopBookDTO> popularBooks;
    private ViewName currentView;

    public Home_1_Controller() {
        this.bookDAO = new BooksDao();
    }

    private void loadBookCover(ImageView imageView, String coverImageUrl) {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + coverImageUrl;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    Image image = new Image(imageUrl.toExternalForm());
                    imageView.setImage(image);
                    imageView.setFitHeight(170);
                    imageView.setFitWidth(120);
                    imageView.setPreserveRatio(true);
                } else {
                    loadPlaceholderImage(imageView);
                }
            } catch (Exception e) {
                System.err.println("Error loading book cover: " + coverImageUrl);
                e.printStackTrace();
                loadPlaceholderImage(imageView);
            }
        } else {
            loadPlaceholderImage(imageView);
        }
    }

    private void loadPlaceholderImage(ImageView imageView) {
        try {
            URL placeholderUrl = getClass().getResource("/book-covers/placeholder.png");
            if (placeholderUrl != null) {
                Image placeholder = new Image(placeholderUrl.toExternalForm());
                imageView.setImage(placeholder);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image");
            e.printStackTrace();
        }
    }

    private void loadPopularBooks() {
        try {
            popularBooks = bookDAO.getTopRatedBooks(4);

            ImageView[] images = {image_1, image_2, image_3, image_4};
            Label[] titles = {lbl_Tittle1, lbl_Tittle2, lbl_Tittle3, lbl_Tittle4};
            Label[] prices = {lbl_Price1, lbl_Price2, lbl_Price3, lbl_Price4};
            Button[] buttons = {btn_PopularBook1, btn_PopularBook2, btn_PopularBook3, btn_PopularBook4};

            for (int i = 0; i < popularBooks.size() && i < 4; i++) {
                TopBookDTO book = popularBooks.get(i);

                // Load book cover using the new method
                loadBookCover(images[i], book.getCoverImageUrl());
                images[i].setDisable(false);

                titles[i].setText(book.getTitle());
                titles[i].setDisable(false);

                prices[i].setText(String.format("$%.2f", book.getDisplayPrice()));
                prices[i].setDisable(false);

                buttons[i].setUserData(book.getBookId());
                buttons[i].setDisable(false);

                final int index = i;
                buttons[i].setOnAction(event -> handleBookSelection(popularBooks.get(index)));
            }

        } catch (SQLException e) {
            System.err.println("Error loading popular books");
            e.printStackTrace();
        }
    }

    private void handleBookSelection(TopBookDTO book) {
        try {
            logger.info("Selected book: " + book.getTitle() + " (ID: " + book.getBookId() + ")");

            SessionManager.getInstance().clearSelectedBookId();
            SessionManager.getInstance().setSelectedBookId(book.getBookId());
            contentArea.getChildren().clear();

            try {
                logger.info("Loading fresh view for BriefInfomationBoook...");

                ViewLoader.ViewLoadResult result = ViewLoader.loadView(
                        ViewName.BriefInfomationBoook.getViewName(),
                        true
                );

                AnchorPane view = result.getView();
                result.setAnchors(0.0, 0.0, 0.0, 0.0);
                contentArea.getChildren().setAll(view);

                Object controller = result.getController();
                if (controller instanceof BriefInformationBookController) {
                    BriefInformationBookController briefController =
                            (BriefInformationBookController) controller;
                    briefController.initialize(null, null);
                    briefController.loadSelectedBook();
                }

                this.currentView = ViewName.BriefInfomationBoook;

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reloading view", e);
                showErrorView();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in handleBookSelection", e);
            showErrorView();
        }
    }

    private void showErrorView() {
        Label errorLabel = new Label("Error loading content. Please try again.");
        errorLabel.getStyleClass().add("error-label");
        contentArea.getChildren().setAll(errorLabel);
    }

    public void refreshPopularBooks() {
        loadPopularBooks();
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Search.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadPopularBooks();
            loadView(ViewName.GifHome);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during initialization", e);
            showErrorView();
        }
    }

    private Node loadView(ViewName viewName) {
        try {
            logger.info("Loading view: " + viewName);
            this.currentView = viewName;
            contentArea.getChildren().clear();

            // Using new ViewLoader
            ViewLoader.ViewLoadResult result = ViewLoader.loadView(viewName.getViewName(), true);
            AnchorPane view = result.getView();

            // Set anchors using convenience method
            result.setAnchors(0.0, 0.0, 0.0, 0.0);

            contentArea.getChildren().setAll(view);

            // Initialize controller if needed
            Object controller = result.getController();
            if (controller instanceof Initializable) {
                ((Initializable) controller).initialize(null, null);
            }

            return view;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load view: " + viewName, e);
            showErrorView();
            return null;
        }
    }

    public enum ViewName {
        GifHome("GifHome"),
        BriefInfomationBoook("Brief_Information_Book");

        private final String viewName;

        ViewName(String viewName) {
            this.viewName = viewName;
        }

        public String getViewName() {
            return viewName;
        }
    }
}