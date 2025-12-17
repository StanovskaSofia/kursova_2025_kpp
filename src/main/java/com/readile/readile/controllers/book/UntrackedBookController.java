package com.readile.readile.controllers.book;

import animatefx.animation.FadeIn;
import com.readile.readile.config.FxController;
import com.readile.readile.controllers.PopupMenuController;
import com.readile.readile.controllers.ToolBar;
import com.readile.readile.models.book.Status;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import com.readile.readile.views.Observer;
import com.readile.readile.views.StageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@FxmlView("/fxml/UntrackedBook.fxml")
public class UntrackedBookController extends ToolBar implements Initializable, FxController, Observer {
    // VIEW VARIABLES --- <
    @FXML
    private StackPane root;
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
    public Label pages;
    @FXML
    public Pane avatar;
    @FXML
    public HBox toolBar;
    // VIEW VARIABLES --- >

    // SERVICES --- <
    @Lazy
    @Autowired
    StageManager stageManager;
    @Autowired
    UserBookService userBookService;
    // SERVICES --- >

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Intent.observer = this;
        Intent.toolBar = toolBar;
        boolean darkTheme = Intent.activeUser.getTheme() == 1;
        Intent.toggleTheme(darkTheme, root);
        Intent.currentSceneClass = UntrackedBookController.class;
        fetchNavAvatar();

        UserBook currentBook = userBookService.findById(Intent.bookId);

        String path = "\"" + getClass().getResource(currentBook.getBook().getCoverId()).toExternalForm() + "\"";
        bookCover.setStyle("-fx-background-image: url("+path+");");
        bookName.setText(currentBook.getBook().getName());
        authors.setText(currentBook.getBook().getAuthors());
        String desc = currentBook.getBook().getDescription();
        description.setText(desc == null || desc.isBlank() ? "No description" : desc);
        description.setVisible(true);
        description.setManaged(true);
        pages.setText(String.valueOf(currentBook.getBook().getLength()));
        authors.setText(authors.getText().substring(0, authors.getText().length()-2));
        pages.setText(String.valueOf(currentBook.getBook().getLength()));
    }

    public void startTracking() {
        UserBook currentUserBook = userBookService.findById(Intent.bookId);
        currentUserBook.setStatus(Status.CURRENTLY_READING);
        currentUserBook.setStartDate(new java.sql.Date(System.currentTimeMillis()));
        userBookService.save(currentUserBook);
        stageManager.rebuildStage(BookController.class);
    }

    @FXML
    void deleteBook() {
        UserBook currentBook = userBookService.findById(Intent.bookId);
        userBookService.delete(currentBook);
        back();
    }

    private void fetchNavAvatar() {
        String path = "\"" + Intent.activeUser.getProfileImage() + "\"";
        avatar.setStyle("-fx-background-image: url("+path+");");
        Rectangle mask = new Rectangle(70, 70);
        mask.setArcHeight(100);
        mask.setArcWidth(100);
        avatar.setClip(mask);
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

    @FXML
    public void back() {
        stageManager.rebuildStage(Intent.popClosedScene());
    }

    public void notification(boolean isDarkTheme) {
        Intent.toggleTheme(isDarkTheme, root);
    }
}