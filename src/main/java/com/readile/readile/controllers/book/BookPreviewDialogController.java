package com.readile.readile.controllers.book;

import com.readile.readile.config.FxController;
import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@FxmlView("/fxml/BookPreviewDialog.fxml")
public class BookPreviewDialogController implements FxController, Initializable {

    @FXML private StackPane dialogRoot;
    @FXML private Pane coverPane;
    @FXML private Label title;
    @FXML private Label author;
    @FXML private Label pages;
    @FXML private Label description;

    @Autowired private UserBookService userBookService;

    private CatalogBook book;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ✅ CLIP (прибирає білі кутики)
        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(36);
        clip.arcHeightProperty().set(36);
        clip.widthProperty().bind(dialogRoot.widthProperty());
        clip.heightProperty().bind(dialogRoot.heightProperty());
        dialogRoot.setClip(clip);

        if (Intent.previewCatalogIndex == null) return;

        book = Intent.tempCatalogResults.get(Intent.previewCatalogIndex);

        title.setText(book.getName());
        author.setText("by: " + book.getAuthors());
        pages.setText("Pages: " + book.getLength());
        description.setText(book.getDescription());

        // ✅ cover
        String coverId = book.getCoverId();
        String imgUrl = null;

        if (coverId != null && !coverId.isBlank()) {
            if (coverId.startsWith("/")) {
                URL res = getClass().getResource(coverId);
                if (res != null) imgUrl = res.toExternalForm();
            } else {
                imgUrl = coverId;
            }
        }

        if (imgUrl != null) {
            coverPane.setStyle(
                    "-fx-background-image: url('" + imgUrl + "');" +
                            "-fx-background-size: cover;" +
                            "-fx-background-position: center;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 12;" +
                            "-fx-background-insets: 0;"
            );
        } else {
            coverPane.setStyle("-fx-background-color: #3a3a3a; -fx-background-radius: 12;");
        }
    }

    @FXML
    public void addBook() {
        if (book == null) return;

        userBookService.addCatalogBookToUser(Intent.activeUser, book);

        close();
        if (Intent.addNewBookDialog != null) Intent.addNewBookDialog.close();
        Intent.clearPreviewCatalogIndex();
    }

    @FXML
    public void close() {
        if (Intent.previewDialog != null) {
            Intent.previewDialog.close();
            Intent.previewDialog = null;
        }
        Intent.clearPreviewCatalogIndex();
    }
}
