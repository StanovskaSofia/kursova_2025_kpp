package com.readile.readile.controllers.book;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXSpinner;
import com.readile.readile.config.FxController;
import com.readile.readile.controllers.PopupMenuController;
import com.readile.readile.controllers.ToolBar;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.category.BookCategory;
import com.readile.readile.models.book.category.Category;
import com.readile.readile.models.book.Rating;
import com.readile.readile.services.implementation.book.BookCategoryService;
import com.readile.readile.services.implementation.book.CategoryService;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import com.readile.readile.views.Observer;
import com.readile.readile.views.StageManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@FxmlView("/fxml/Category.fxml")
public class CategoryController extends ToolBar implements FxController, Initializable, Observer {
    // VIEW VARIABLES --- <
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView categoryImage;
    @FXML
    private Label categoryName;
    @FXML
    private Label numberOfBooks;
    @FXML
    private FlowPane booksCardView;
    @FXML
    private Pane avatar;
    @FXML
    private HBox toolBar;
    // VIEW VARIABLES --- >

    // SERVICES --- <
    @Lazy
    @Autowired
    StageManager stageManager;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BookCategoryService bookCategoryService;
    @Autowired
    UserBookService userBookService;
    // SERVICES --- >

    // current category
    private Category currentCategory;

    @FXML
    public void back() {
        stageManager.rebuildStage(Intent.popClosedScene());
    }

    @FXML
    void showPopupMenu(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Popup popup = new Popup();
        popup.setAutoHide(true);
        Node popupContent = stageManager.loadView(PopupMenuController.class);
        popupContent.setOnMouseClicked(mouseEvent -> popup.hide());
        popup.getContent().addAll(popupContent);
        new FadeIn(popupContent).setSpeed(1.6).play();
        popup.show(stage, stage.getX() + 726, stage.getY() + 84);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Intent.observer = this;
        Intent.toolBar = toolBar;

        boolean darkTheme = Intent.activeUser.getTheme() == 1;
        Intent.toggleTheme(darkTheme, root);
        Intent.currentSceneClass = CategoryController.class;

        fetchNavAvatar();

        // ---- load category ----
        currentCategory = categoryService.findById(Intent.categoryId);

        String imagePath = currentCategory.getCategoryImage().startsWith("/") ?
                getClass().getResource(currentCategory.getCategoryImage()).toExternalForm() :
                currentCategory.getCategoryImage();

        categoryImage.setImage(new Image(imagePath));
        categoryName.setText(currentCategory.getName());

        // ---- load books in this category ----
        booksCardView.getChildren().clear();
        List<UserBook> userBooks = userBookService.findAllByUser(Intent.activeUser);

        List<UserBook> categoryUserBooks =
                userBooks.stream()
                        .filter(userBook ->
                                bookCategoryService.findAllByBook(userBook.getBook()).stream()
                                        .map(BookCategory::getCategory)
                                        .anyMatch(category -> category.getName().equals(currentCategory.getName()))
                        )
                        .toList();

        numberOfBooks.setText(String.valueOf(categoryUserBooks.size()));

        categoryUserBooks.forEach(categoryUserBook -> {
            try {
                booksCardView.getChildren().add(getBookCard(categoryUserBook));
            } catch (IOException ignored) {
            }
        });
    }

    private void fetchNavAvatar() {
        String path = "\"" + Intent.activeUser.getProfileImage() + "\"";
        avatar.setStyle("-fx-background-image: url(" + path + ");");
        Rectangle mask = new Rectangle(70, 70);
        mask.setArcHeight(100);
        mask.setArcWidth(100);
        avatar.setClip(mask);
    }

    public Pane getBookCard(UserBook userBook) throws IOException {
        Pane root = stageManager.loadView(BookCardController.class);
        root.setUserData(userBook.getId());

        String path = "\"" + getClass().getResource(userBook.getBook().getCoverId()).toExternalForm() + "\"";
        ((StackPane) root.getChildren().get(0)).getChildren().get(0)
                .setStyle("-fx-background-image: url(" + path + ");");

        String statue = String.valueOf(userBook.getStatus()).replace('_', ' ').toLowerCase();
        ((Label) ((HBox) ((Pane) (((StackPane) root.getChildren().get(0)).getChildren().get(0)))
                .getChildren().get(0)).getChildren().get(0))
                .setText(StringUtils.capitalize(statue));

        ((Label) root.getChildren().get(1)).setText(userBook.getBook().getName());

        double progress = userBook.getBook().getLength() == 0
                ? 0.0
                : (userBook.getCurrentPage() / (userBook.getBook().getLength() * 1.0));

        ((JFXSpinner) root.getChildren().get(2)).setProgress(progress);

        ObservableList<Node> stars = ((GridPane) root.getChildren().get(3)).getChildren();
        setRating(userBook.getRating(), stars);

        return root;
    }

    private void setRating(Rating rating, ObservableList<Node> stars) {
        String path = String.valueOf(getClass().getResource("/icons/on.png"));
        switch (rating) {
            case ONE_STAR -> stars.get(0).setStyle("-fx-image: url(" + path + ")");
            case TWO_STARS -> {
                for (int i = 0; i < 2; i++) stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case THREE_STARS -> {
                for (int i = 0; i < 3; i++) stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case FOUR_STARS -> {
                for (int i = 0; i < 4; i++) stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case FIVE_STARS -> {
                for (int i = 0; i < 5; i++) stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
        }
    }

    // ✅ Button handler: onAction="#changeCategoryImage"
    @FXML
    public void changeCategoryImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose category image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) root.getScene().getWindow();
        File selected = chooser.showOpenDialog(stage);
        if (selected == null) return;

        try {
            // ✅ зберігаємо в папку користувача (працює після збірки)
            Path dir = Path.of(System.getProperty("user.home"), ".readile", "category-images");
            Files.createDirectories(dir);

            String safeName = selected.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = "cat_" + currentCategory.getId() + "_" + System.currentTimeMillis() + "_" + safeName;
            Path dest = dir.resolve(fileName);

            Files.copy(selected.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // ✅ зберігаємо file URI в БД
            String uri = dest.toUri().toString(); // file:/C:/Users/.../cat_...
            currentCategory.setCategoryImage(uri);
            categoryService.save(currentCategory);

            // ✅ оновлюємо картинку на екрані категорії
            categoryImage.setImage(new Image(uri));

            // ✅ щоб на сторінці Categories теж оновилось після повернення
            // (ти вже маєш observer)
            if (Intent.observer != null) {
                Intent.observer.notification(Intent.activeUser.getTheme() == 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void deleteCategory() {
        Category current = categoryService.findById(Intent.categoryId);

        bookCategoryService.deleteInBatch(
                bookCategoryService.findAllByCategory(current)
        );

        categoryService.delete(current);
        back();
    }

    @Override
    public void notification(boolean isDarkTheme) {
        Intent.toggleTheme(isDarkTheme, root);
    }
}
