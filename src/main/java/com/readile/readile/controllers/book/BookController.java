package com.readile.readile.controllers.book;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.readile.readile.config.FxController;
import com.readile.readile.controllers.PopupMenuController;
import com.readile.readile.controllers.ToolBar;
import com.readile.readile.controllers.authentication.GoogleSignInController;
import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.models.book.Highlight;
import com.readile.readile.models.book.Status;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.category.BookCategory;
import com.readile.readile.models.book.category.Category;
import com.readile.readile.services.implementation.book.BookCategoryService;
import com.readile.readile.services.implementation.book.CategoryService;
import com.readile.readile.services.implementation.book.HighlightService;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import com.readile.readile.views.Observer;
import com.readile.readile.views.StageManager;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle; // ОБОВʼЯЗКОВО
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@FxmlView("/fxml/Book.fxml")
public class BookController extends ToolBar implements Initializable, FxController, Observer {

    // VIEW VARIABLES
    @FXML
    public Pane bookCover;
    @FXML
    public Label status;
    @FXML
    public Label bookName;
    @FXML
    public Label authors;
    @FXML
    public Label description;
    @FXML
    public Label totalPages;
    @FXML
    public Label startDate;
    @FXML
    public Label error;
    @FXML
    public Rating rating;
    @FXML
    public JFXButton reminder;
    @FXML
    private JFXTextField currentPage;
    @FXML
    public JFXSpinner progress;
    @FXML
    public Pane avatar;
    @FXML
    public HBox toolBar;
    @FXML
    public JFXTextField highlightTextField;
    @FXML
    private StackPane root;
    @FXML
    public CheckComboBox<String> categoriesComboBox;
    @FXML
    public JFXDialog highlightDialog;

    // ✅ тепер список хайлайтів з об’єктами Highlight
    @FXML
    public JFXListView<Highlight> highlightsListView;

    // SERVICES
    @Lazy
    @Autowired
    private StageManager stageManager;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private HighlightService highlightService;
    @Autowired
    private BookCategoryService bookCategoryService;
    @Autowired
    private UserBookService userBookService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Intent.observer = this;
        Intent.toolBar = toolBar;

        boolean darkTheme = Intent.activeUser.getTheme() == 1;
        Intent.toggleTheme(darkTheme, root);
        Intent.currentSceneClass = BookController.class;

        fetchNavAvatar();

        currentPage.setEditable(true);

        UserBook currentBook = userBookService.findById(Intent.bookId);
        CatalogBook catalogBook = currentBook.getBook();

        // Categories
        List<Category> categories = categoryService.findGlobalAndUserCategories(Intent.activeUser);
        categories.forEach(category -> categoriesComboBox.getItems().add(category.getName()));

        List<Category> bookCategories = bookCategoryService.findAllByBook(catalogBook).stream()
                .map(BookCategory::getCategory)
                .toList();

        bookCategories.forEach(bookCategory ->
                categoriesComboBox.checkModelProperty().get().check(bookCategory.getName())
        );

        // Book UI
        String path = "\"" + getClass().getResource(catalogBook.getCoverId()).toExternalForm() + "\"";
        bookCover.setStyle("-fx-background-image: url(" + path + ");");
        bookName.setText(catalogBook.getName());
        authors.setText(catalogBook.getAuthors());
        String desc = catalogBook.getDescription();
        description.setText(desc == null || desc.isBlank() ? "No description" : desc);
        description.setManaged(true);
        description.setVisible(true);
        totalPages.setText(String.valueOf(catalogBook.getLength()));
        status.setText(StringUtils.capitalize(String.valueOf(currentBook.getStatus()).replace('_', ' ').toLowerCase()));
        startDate.setText(currentBook.getStartDate() == null ? "" : currentBook.getStartDate().toString());
        rating.setRating(currentBook.getRating().ordinal() + 1);
        currentPage.setText(String.valueOf(currentBook.getCurrentPage()));

        double p = currentBook.getBook().getLength() == 0 ? 0.0
                : currentBook.getCurrentPage() / (currentBook.getBook().getLength() * 1.0);

        p = Math.max(0.0, Math.min(1.0, p));
        p = Math.round(p * 100.0) / 100.0;
        progress.setProgress(p);

        // ✅ Highlights (завантажуємо об’єкти)
        List<Highlight> highlightList = highlightService.findByUserBook(currentBook);
        highlightsListView.getItems().clear();
        highlightsListView.getItems().addAll(highlightList);

        // ✅ показуємо у списку тільки текст highlight
        highlightsListView.setCellFactory(list -> new JFXListCell<>() {
            @Override
            protected void updateItem(Highlight item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getHighlight());
            }
        });

        // Dialog setup
        highlightDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        highlightDialog.setDialogContainer(root);

        // Update categories on change
        categoriesComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super String>) event -> {
            bookCategoryService.deleteInBatch(bookCategoryService.findAllByBook(catalogBook));

            List<String> checkedCategoriesNames = categoriesComboBox.getCheckModel().getCheckedItems();

            List<Category> categoryList = categoryService.findGlobalAndUserCategories(Intent.activeUser)
                    .stream()
                    .filter(category -> checkedCategoriesNames.stream()
                            .anyMatch(name -> category.getName().equals(name)))
                    .toList();

            categoryList.forEach(category -> bookCategoryService.save(new BookCategory(category, catalogBook)));

            reminder.setDisable(true);
        });
    }

    @FXML
    public void setCurrentPage(Event event) {
        UserBook currentBook = userBookService.findById(Intent.bookId);
        Control selectedControl = ((Control) event.getSource());

        if (selectedControl.getId().equals("page")) {
            changeCurrentPage(Integer.valueOf(currentPage.getText()), selectedControl);
        } else if (selectedControl.getId().equals("minus")) {
            changeCurrentPage(currentBook.getCurrentPage() - 1, selectedControl);
        } else {
            changeCurrentPage(currentBook.getCurrentPage() + 1, selectedControl);
        }
    }

    private void changeCurrentPage(Integer page, Control selectedControl) {
        UserBook currentBook = userBookService.findById(Intent.bookId);
        int bookLength = currentBook.getBook().getLength();

        if (page >= 0 && page <= bookLength) {
            currentPage.setText(String.valueOf(page));
            currentBook.setCurrentPage(page);
            updateBookStatus(currentBook);

            status.setText(StringUtils.capitalize(String.valueOf(currentBook.getStatus()).replace('_', ' ').toLowerCase()));
            double prog = currentBook.getBook().getLength() == 0 ? 0.0
                    : currentBook.getCurrentPage() / (currentBook.getBook().getLength() * 1.0);

            prog = Math.max(0.0, Math.min(1.0, prog));
            prog = Math.round(prog * 100.0) / 100.0;
            progress.setProgress(prog);

        } else {
            if (selectedControl instanceof JFXTextField)
                currentPage.setText(String.valueOf(currentBook.getCurrentPage()));

            error.setVisible(true);

            final FadeTransition inTransition = new FadeTransition(new Duration(200), error);
            inTransition.setFromValue(0.0);
            inTransition.setToValue(1);

            final FadeTransition outTransition = new FadeTransition(new Duration(200), error);
            outTransition.setFromValue(1.0);
            outTransition.setToValue(0);

            final PauseTransition pauseTransition = new PauseTransition(new Duration(2000));

            final SequentialTransition mainTransition = new SequentialTransition(inTransition, pauseTransition, outTransition);
            mainTransition.setOnFinished(ae -> error.setVisible(false));
            mainTransition.play();
        }
    }

    private void updateBookStatus(UserBook currentBook) {
        if (currentBook.getCurrentPage() == currentBook.getBook().getLength()) {
            currentBook.setStatus(Status.READ);
            currentBook.setEndDate(new java.sql.Date(System.currentTimeMillis()));
        } else {
            currentBook.setStatus(Status.CURRENTLY_READING);
            currentBook.setEndDate(null);
        }
        userBookService.update(currentBook);
    }

    @FXML
    void setRating(MouseEvent event) {
        UserBook currentBook = userBookService.findById(Intent.bookId);
        currentBook.setRating(com.readile.readile.models.book.Rating.values()
                [(int) ((Rating) event.getSource()).getRating() - 1]);
        userBookService.save(currentBook);
    }


    private void fetchNavAvatar() {
        String imageUrl = Intent.activeUser.getProfileImage();

        // fallback, якщо нема аватарки
        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = getClass()
                    .getResource("/icons/avatar.png")
                    .toExternalForm();
        }

        avatar.setStyle(
                "-fx-background-image: url(" + imageUrl + ");" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-size: cover;"
        );

        Circle clip = new Circle(35, 35, 35);
        avatar.setClip(clip);
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

    public void deleteBook() {
        UserBook currentBook = userBookService.findById(Intent.bookId);
        highlightService.deleteInBatch(highlightService.findByUserBook(currentBook));
        userBookService.delete(currentBook);
        back();
    }

    @FXML
    public void showHighlights() {
        highlightDialog.show();
    }

    // ✅ Add highlight
    @FXML
    public void addHighlight() {
        String text = highlightTextField.getText().trim();
        if (text.isEmpty()) return;

        UserBook currentBook = userBookService.findById(Intent.bookId);
        Highlight highlight = new Highlight(currentBook, text);

        highlightService.save(highlight);
        highlightsListView.getItems().add(highlight);

        highlightTextField.clear();
    }

    // ✅ Delete selected highlight
    @FXML
    public void deleteHighlight() {
        Highlight selected = highlightsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        highlightService.delete(selected);
        highlightsListView.getItems().remove(selected);
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