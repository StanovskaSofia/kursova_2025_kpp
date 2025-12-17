package com.readile.readile.controllers.book;

import com.jfoenix.controls.JFXButton;
import com.readile.readile.config.FxController;
import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Intent;
import com.readile.readile.views.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@FxmlView("/fxml/SearchBookCard.fxml")
public class SearchBookCardController implements FxController {

    @Autowired
    private UserBookService userBookService;

    @Lazy
    @Autowired
    private StageManager stageManager;

    public void addBook(ActionEvent event) throws IOException {
        // Перевірка на null для безпеки
        if (event.getSource() instanceof JFXButton) {
            String text = ((JFXButton) event.getSource()).getAccessibleText();
            if (text != null) {
                int index = Integer.parseInt(text);
                CatalogBook resultBook = Intent.tempCatalogResults.get(index);
                userBookService.addCatalogBookToUser(Intent.activeUser, resultBook);

                // Закриваємо діалог додавання, якщо він існує
                if (Intent.addNewBookDialog != null) {
                    Intent.addNewBookDialog.close();
                }
            }
        }
    }

    @FXML
    public void consumeClick(MouseEvent e) {
        e.consume();
    }

    @FXML
    public void openPreview(MouseEvent event) {
        try {
            Pane card = (Pane) event.getSource();
            JFXButton addBtn = (JFXButton) card.lookup("#add");

            if (addBtn == null || addBtn.getAccessibleText() == null) return;

            int index = Integer.parseInt(addBtn.getAccessibleText());
            Intent.previewCatalogIndex = index;

            // 1. Завантажуємо View через FxWeaver (повертає Parent/Pane)
            Parent content = stageManager.loadView(BookPreviewDialogController.class);

            // 2. Створюємо Сцену з ПРОЗОРИМ фоном
            Scene scene = new Scene(content);
            scene.setFill(Color.TRANSPARENT);

            // 3. Створюємо Stage (Окреме вікно)
            Stage stage = new Stage();
            stage.setScene(scene);

            // 4. Прибираємо стандартні білі рамки Windows/macOS
            stage.initStyle(StageStyle.TRANSPARENT);

            // 5. Робимо вікно модальним (блокує батьківське вікно, поки не закриєш це)
            stage.initModality(Modality.APPLICATION_MODAL);

            // Опціонально: прив'язати до головного вікна, щоб не зникало при Alt-Tab
            // if (Intent.addNewBookDialog != null && Intent.addNewBookDialog.getScene() != null) {
            //     stage.initOwner(Intent.addNewBookDialog.getScene().getWindow());
            // }

            // Зберігаємо посилання на stage, щоб потім закрити його з контролера
            Intent.previewDialog = stage;

            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}