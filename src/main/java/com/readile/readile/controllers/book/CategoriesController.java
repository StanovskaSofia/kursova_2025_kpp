package com.readile.readile.controllers.book;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.readile.readile.config.FxController;
import com.readile.readile.controllers.PopupMenuController;
import com.readile.readile.controllers.ToolBar;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.category.BookCategory;
import com.readile.readile.models.book.category.Category;
import com.readile.readile.services.implementation.book.BookCategoryService;
import com.readile.readile.services.implementation.book.CategoryService;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import com.readile.readile.views.Observer;
import com.readile.readile.views.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@FxmlView("/fxml/Categories.fxml")
public class CategoriesController extends ToolBar
        implements Initializable, FxController, Observer {

    // VIEW VARIABLES
    @FXML private StackPane root;
    @FXML private Pane avatar;
    @FXML private FlowPane categoriesCardView;
    @FXML private JFXTextField newCategoryNameField;
    @FXML private HBox toolBar;
    @FXML private JFXDialog newCategoryDialog;

    // SERVICES
    @Lazy @Autowired private StageManager stageManager;
    @Autowired private CategoryService categoryService;
    @Autowired private BookCategoryService bookCategoryService;
    @Autowired private UserBookService userBookService;

    // ================= INIT =================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Intent.observer = this;
        Intent.toolBar = toolBar;

        boolean darkTheme = Intent.activeUser.getTheme() == 1;
        Intent.toggleTheme(darkTheme, root);
        Intent.currentSceneClass = CategoriesController.class;

        fetchNavAvatar();
        loadCategories();

        newCategoryDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        newCategoryDialog.setDialogContainer(root);
    }

    // ================= LOAD CATEGORIES =================
    private void loadCategories() {
        categoriesCardView.getChildren().clear();

        // ✅ беремо ТІЛЬКИ книжки користувача
        List<UserBook> userBooks =
                userBookService.findAllByUser(Intent.activeUser);

        List<Category> categories =
                categoryService.findGlobalAndUserCategories(Intent.activeUser);

        categories.forEach(category -> {
            try {
                // ✅ рахуємо книжки користувача в цій категорії
                int count = (int) userBooks.stream()
                        .filter(userBook ->
                                bookCategoryService
                                        .findAllByBook(userBook.getBook())
                                        .stream()
                                        .map(BookCategory::getCategory)
                                        .anyMatch(c -> c.getId() == category.getId())
                        )
                        .count();

                categoriesCardView.getChildren().add(
                        getCategoryCard(
                                category.getId(),
                                category.getName(),
                                category.getCategoryImage(),
                                count
                        )
                );
            } catch (IOException ignored) {}
        });
    }

    // ================= CATEGORY CARD =================
    public Pane getCategoryCard(Long id,
                                String categoryName,
                                String categoryImage,
                                int numberOfBooks) throws IOException {

        Pane root = stageManager.loadView(CategoryCardController.class);
        root.setAccessibleText(String.valueOf(id));

        String imagePath;
        if (categoryImage != null && categoryImage.startsWith("/")) {
            URL url = getClass().getResource(categoryImage);
            if (url == null) {
                url = getClass().getResource("/assets/placeholders/categories/user_default.png");
            }
            imagePath = url != null ? url.toExternalForm() : "";
        } else {
            imagePath = categoryImage != null ? categoryImage : "";
        }

        root.getChildren().get(0)
                .setStyle("-fx-background-image: url(" + imagePath + ");");

        ((Label) root.getChildren().get(1)).setText(categoryName);
        ((Label) root.getChildren().get(3)).setText(String.valueOf(numberOfBooks));

        return root;
    }

    // ================= ADD CATEGORY =================
    @FXML
    void openNewCategoryDialog() {
        newCategoryDialog.show();
    }

    @FXML
    void addNewCategory() {
        String name = newCategoryNameField.getText().trim();
        if (name.isEmpty()) return;

        Category newCategory = new Category(
                name,
                "/assets/placeholders/categories/user_default.png",
                Intent.activeUser
        );

        try {
            categoryService.save(newCategory);
            loadCategories(); // 🔁 оновлюємо список
        } catch (Exception ignored) {}

        newCategoryDialog.close();
        newCategoryNameField.setText("");
    }

    // ================= AVATAR =================
    private void fetchNavAvatar() {
        String path = "\"" + Intent.activeUser.getProfileImage() + "\"";
        avatar.setStyle("-fx-background-image: url(" + path + ");");

        Rectangle mask = new Rectangle(70, 70);
        mask.setArcHeight(100);
        mask.setArcWidth(100);
        avatar.setClip(mask);
    }

    // ================= UI =================
    @FXML
    void showPopupMenu(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Popup popup = new Popup();
        popup.setAutoHide(true);

        Node popupContent = stageManager.loadView(PopupMenuController.class);
        popupContent.setOnMouseClicked(e -> popup.hide());
        popup.getContent().add(popupContent);

        new FadeIn(popupContent).setSpeed(1.6).play();
        popup.show(stage, stage.getX() + 726, stage.getY() + 84);
    }

    @FXML
    public void back() {
        stageManager.rebuildStage(Intent.popClosedScene());
    }

    @Override
    public void notification(boolean isDarkTheme) {
        Intent.toggleTheme(isDarkTheme, root);
    }
}