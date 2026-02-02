package com.readile.readile.controllers;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.readile.readile.config.FxController;
import com.readile.readile.controllers.book.BookCardController;
import com.readile.readile.controllers.book.CategoriesController;
import com.readile.readile.controllers.book.SearchBookCardController;
import com.readile.readile.models.book.CatalogBook;
import com.readile.readile.models.book.UserBook;
import com.readile.readile.models.book.Rating;
import com.readile.readile.models.book.Status;
import com.readile.readile.models.book.list.BookList;
import com.readile.readile.models.book.list.BookListEntry;
import com.readile.readile.services.implementation.book.CatalogBookService;
import com.readile.readile.services.implementation.book.BookListEntryService;
import com.readile.readile.services.implementation.book.BookListService;
import com.readile.readile.services.implementation.book.UserBookService;
import com.readile.readile.views.Observer;
import com.readile.readile.views.Intent;
import com.readile.readile.views.StageManager;
import com.readile.readile.views.components.DoughnutChart;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import net.rgielen.fxweaver.core.FxmlView;
import com.jfoenix.controls.JFXListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

import static com.readile.readile.models.book.Status.*;

@Controller
@FxmlView("/fxml/Home.fxml")
public class HomeScreenController extends ToolBar implements Initializable, FxController, Observer {

    // VIEW VARIABLES --- <
    @FXML public JFXDialog addBookDialog;
    @FXML private StackPane root;
    @FXML public JFXTextField search;
    @FXML public StackPane charts;
    @FXML public ImageView chartEmptyImage, noBooksImage;
    @FXML public FlowPane booksCardView;
    @FXML public JFXComboBox<String> ratingComboBox, statusComboBox, listComboBox;
    @FXML public Pane avatar;
    @FXML public ScrollPane bookCards;
    @FXML public JFXDialog listsDialog;
    @FXML public JFXListView<String> listsView;
    @FXML public JFXTextField listNameField;
    @FXML public Label listError;

    // Modal Window attributes
    @FXML public JFXTextField searchField;
    @FXML public ImageView noResultsFound;
    @FXML public ScrollPane searchResults;
    @FXML public FlowPane searchResultsView;
    @FXML public HBox toolBar;
    // VIEW VARIABLES --- >

    // SERVICES --- <
    @Lazy
    @Autowired StageManager stageManager;
    @Autowired UserBookService userBookService;
    @Autowired CatalogBookService catalogBookService;
    @Autowired BookListService bookListService;
    @Autowired BookListEntryService bookListEntryService;
    // SERVICES --- >

    private List<UserBook> bookList;
    private int counter = 0;

    private String statusLabel(Status s) {
        return switch (s) {
            case CURRENTLY_READING -> "Reading";
            case TO_READ -> "To read";
            case READ -> "Read";
        };
    }

    @FXML
    public void searchForBook() {
        applyFilters();
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
    public void addNewBook() {
        try {
            addBookDialog.show();
            addBookDialog.setOnDialogClosed(
                    jfxDialogEvent -> {
                        searchField.setText("");
                        searchResultsView.getChildren().clear();
                        Intent.clearCatalogResults();
                        booksCardView.getChildren().clear();
                        bookList = userBookService.findAllByUser(Intent.activeUser);
                        applyFilters();
                    }
            );
        } catch (Exception ignored) {
        }
    }

    @FXML
    public void browseCategories() {
        Intent.pushClosedScene(HomeScreenController.class);
        stageManager.rebuildStage(CategoriesController.class);
    }

    @FXML
    public void openListsDialog() {
        refreshListsView();
        listNameField.clear();
        listError.setVisible(false);
        listsDialog.show();
    }

    // Modal window API search
    @FXML
    public void modalSearchForBook() {
        searchResultsView.getChildren().clear();
        if (!searchField.getText().equals("")) {
            List<CatalogBook> resultBooks = catalogBookService.search(searchField.getText());
            if (resultBooks.size() == 0) {
                searchResults.setVisible(false);
            } else {
                searchResults.setVisible(true);
                Intent.setCatalogResults(resultBooks);
                for (CatalogBook resultBook : resultBooks) {
                    try {
                        Pane card = getSearchBookCard(resultBook);
                        searchResultsView.getChildren().add(card);
                    } catch (IOException ignored) {
                    }
                }
                counter = 0;
            }
        }
    }

    private Pane getSearchBookCard(CatalogBook resultBook) throws IOException {
        Pane root = stageManager.loadView(SearchBookCardController.class);

        ((Label) root.lookup("#book-name")).setText(resultBook.getName());
        ((Label) root.lookup("#author")).setText(resultBook.getAuthors());
        ((Label) root.lookup("#description")).setText(resultBook.getDescription());

        // ✅ cover
        Pane coverPane = (Pane) root.lookup("#coverPane");
        if (coverPane != null) {
            String coverId = resultBook.getCoverId();
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

        // індекс книги (для Add і Preview)
        ((Pane) root.lookup("#bottom-pane")).getChildren().get(0)
                .setAccessibleText(String.valueOf(counter++));

        return root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Intent.observer = this;
        Intent.toolBar = toolBar;

        boolean darkTheme = Intent.activeUser.getTheme() == 1;
        Intent.toggleTheme(darkTheme, root);
        Intent.currentSceneClass = HomeScreenController.class;

        Platform.runLater(() -> {
            booksCardView.getChildren().clear();
            bookList = userBookService.findAllByUser(Intent.activeUser);
            applyFilters();
        });

        fetchNavAvatar();

        ratingComboBox.getItems().clear();
        ratingComboBox.getItems().addAll(
                "", "One Star", "Two Stars", "Three Stars",
                "Four Stars", "Five Stars"
        );

        statusComboBox.getItems().clear();
        statusComboBox.getItems().addAll(
                "", "To Read", "Currently Reading", "Read"
        );

        refreshListFilter();

        addBookDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        addBookDialog.setDialogContainer(root);
        Intent.addNewBookDialog = addBookDialog;

        listsDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        listsDialog.setDialogContainer(root);

        listsView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                listNameField.setText(newValue);
                listError.setVisible(false);
            }
        });
    }

    private void fetchNavAvatar() {
        String imageUrl = Intent.activeUser.getProfileImage();

        avatar.setStyle(
                "-fx-background-image: url(" + imageUrl + ");" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-size: cover;" +
                        "-fx-background-radius: 35;" +
                        "-fx-border-radius: 35;"
        );

        Circle clip = new Circle(35, 35, 35);
        avatar.setClip(clip);
    }

    private void loadBooksAndChart(List<UserBook> listToShow) {
        List<UserBook> displayList = listToShow == null ? List.of() : listToShow;
        if (bookList.size() == 0) {
            booksCardView.getChildren().clear();
            bookCards.setVisible(false);
            noBooksImage.setVisible(true);
            chartEmptyImage.setVisible(true);
        } else {
            chartEmptyImage.setVisible(displayList.isEmpty());
            noBooksImage.setVisible(displayList.isEmpty());
            bookCards.setVisible(!displayList.isEmpty());

            charts.getChildren().clear();

            if (!displayList.isEmpty()) {
                Map<Status, Integer> statusCount = new HashMap<>();
                for (UserBook book : displayList) {
                    switch (book.getStatus()) {
                        case CURRENTLY_READING -> {
                            if (statusCount.containsKey(CURRENTLY_READING))
                                statusCount.put(CURRENTLY_READING, statusCount.get(CURRENTLY_READING) + 1);
                            else statusCount.put(CURRENTLY_READING, 1);
                        }
                        case TO_READ -> {
                            if (statusCount.containsKey(TO_READ))
                                statusCount.put(TO_READ, statusCount.get(TO_READ) + 1);
                            else statusCount.put(TO_READ, 1);
                        }
                        case READ -> {
                            if (statusCount.containsKey(READ))
                                statusCount.put(READ, statusCount.get(READ) + 1);
                            else statusCount.put(READ, 1);
                        }
                    }
                }

                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                statusCount.forEach((k, v) -> pieChartData.add(new PieChart.Data(statusLabel(k), v)));

                final DoughnutChart chart = new DoughnutChart(pieChartData);
                chart.setPrefSize(220, 220);
                chart.setMinSize(220, 220);
                chart.setMaxSize(220, 220);

                chart.setLegendVisible(false);
                chart.getStylesheets().add(String.valueOf(getClass().getResource("/styles/doughnut-chart.css")));

                charts.getChildren().add(chart);
            }

            booksCardView.getChildren().clear();
            for (UserBook record : displayList) {
                try {
                    booksCardView.getChildren().add(getBookCard(record));
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Pane getBookCard(UserBook book) throws IOException {
        Pane root = stageManager.loadView(BookCardController.class);
        root.setUserData(book.getId());

        String path = "\"" + getClass().getResource(book.getBook().getCoverId()).toExternalForm() + "\"";
        ((StackPane) root.getChildren().get(0)).getChildren().get(0)
                .setStyle("-fx-background-image: url(" + path + ");");

        String statue = String.valueOf(book.getStatus()).replace('_', ' ').toLowerCase();
        ((Label) ((HBox) ((Pane) (((StackPane) root.getChildren().get(0)).getChildren().get(0)))
                .getChildren().get(0)).getChildren().get(0))
                .setText(StringUtils.capitalize(statue));

        ((Label) root.getChildren().get(1)).setText(book.getBook().getName());

        double progress = book.getCurrentPage() / (book.getBook().getLength() * 1.0);
        progress = Math.round(progress * 100.0) / 100.0;
        ((JFXSpinner) root.getChildren().get(2)).setProgress(progress);

        ObservableList<Node> stars = ((GridPane) root.getChildren().get(3)).getChildren();
        setRating(book.getRating(), stars);

        updateListBadges(root, book);

        return root;
    }

    private void setRating(Rating rating, ObservableList<Node> stars) {
        String path = String.valueOf(getClass().getResource("/icons/on.png"));
        switch (rating) {
            case ONE_STAR -> stars.get(0).setStyle("-fx-image: url(" + path + ")");
            case TWO_STARS -> {
                for (int i = 0; i < 2; i++)
                    stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case THREE_STARS -> {
                for (int i = 0; i < 3; i++)
                    stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case FOUR_STARS -> {
                for (int i = 0; i < 4; i++)
                    stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
            case FIVE_STARS -> {
                for (int i = 0; i < 5; i++)
                    stars.get(i).setStyle("-fx-image: url(" + path + ")");
            }
        }
    }

    @Override
    public void notification(boolean isDarkTheme) {
        Intent.toggleTheme(isDarkTheme, root);
        if (Intent.innerCircle != null) {
            Intent.innerCircle.setFill(Color.valueOf(isDarkTheme ? "#36373A" : "#F8F8F8"));
            Intent.innerCircle.setStroke(isDarkTheme ? Color.BLACK : Color.WHITE);
        }
    }

    @FXML
    public void filter() {
        applyFilters();
    }

    @FXML
    public void addList() {
        String name = listNameField.getText().trim();
        if (name.isEmpty()) {
            listError.setVisible(true);
            listError.setText("List name can't be empty");
            return;
        }

        if (bookListService.findByUserAndName(Intent.activeUser, name) != null) {
            listError.setVisible(true);
            listError.setText("List name already exists");
            return;
        }

        bookListService.save(new BookList(Intent.activeUser, name));
        listNameField.clear();
        listError.setVisible(false);
        refreshListFilter();
        refreshListsView();
    }

    @FXML
    public void renameList() {
        String selected = listsView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            listError.setVisible(true);
            listError.setText("Select a list to rename");
            return;
        }

        String newName = listNameField.getText().trim();
        if (newName.isEmpty()) {
            listError.setVisible(true);
            listError.setText("List name can't be empty");
            return;
        }

        BookList existing = bookListService.findByUserAndName(Intent.activeUser, newName);
        if (existing != null && !existing.getName().equals(selected)) {
            listError.setVisible(true);
            listError.setText("List name already exists");
            return;
        }

        BookList list = bookListService.findByUserAndName(Intent.activeUser, selected);
        if (list != null) {
            list.setName(newName);
            bookListService.update(list);
        }

        listError.setVisible(false);
        refreshListFilter();
        refreshListsView();
    }

    @FXML
    public void deleteList() {
        String selected = listsView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            listError.setVisible(true);
            listError.setText("Select a list to delete");
            return;
        }

        BookList list = bookListService.findByUserAndName(Intent.activeUser, selected);
        if (list != null) {
            bookListService.delete(list);
        }

        listError.setVisible(false);
        listNameField.clear();
        refreshListFilter();
        refreshListsView();
    }

    private void refreshListsView() {
        listsView.getItems().clear();
        bookListService.findByUser(Intent.activeUser).stream()
                .map(BookList::getName)
                .sorted()
                .forEach(name -> listsView.getItems().add(name));
    }

    private void refreshListFilter() {
        List<String> names = bookListService.findByUser(Intent.activeUser).stream()
                .map(BookList::getName)
                .sorted()
                .toList();

        listComboBox.getItems().clear();
        listComboBox.getItems().add("Усі");
        listComboBox.getItems().addAll(names);
        listComboBox.getSelectionModel().selectFirst();
        applyFilters();
    }

    private void applyFilters() {
        if (bookList == null) {
            loadBooksAndChart(List.of());
            return;
        }

        int rating = ratingComboBox.getSelectionModel().getSelectedIndex();
        int status = statusComboBox.getSelectionModel().getSelectedIndex();
        String selectedList = listComboBox.getSelectionModel().getSelectedItem();
        String query = search.getText() == null ? "" : search.getText().trim().toLowerCase();

        List<UserBook> filtered = bookList.stream()
                .filter(book -> {
                    if (query.isEmpty()) {
                        return true;
                    }
                    String name = book.getBook().getName() == null ? "" : book.getBook().getName().toLowerCase();
                    String authors = book.getBook().getAuthors() == null ? "" : book.getBook().getAuthors().toLowerCase();
                    return name.contains(query) || authors.contains(query);
                })
                .filter(book -> rating <= 0 || book.getRating().ordinal() + 1 == rating)
                .filter(book -> status <= 0 || book.getStatus().ordinal() + 1 == status)
                .toList();

        if (selectedList != null && !selectedList.equals("Усі")) {
            BookList list = bookListService.findByUserAndName(Intent.activeUser, selectedList);
            if (list != null) {
                Set<Long> allowedIds = new HashSet<>();
                bookListEntryService.findByBookList(list)
                        .forEach(entry -> allowedIds.add(entry.getUserBook().getId()));
                filtered = filtered.stream()
                        .filter(book -> allowedIds.contains(book.getId()))
                        .toList();
            }
        }

        loadBooksAndChart(filtered);
    }

    private void updateListBadges(Pane root, UserBook book) {
        List<BookList> lists = bookListEntryService.findByUserBook(book).stream()
                .map(BookListEntry::getBookList)
                .toList();

        List<String> badgeLabels = lists.stream()
                .map(this::formatListBadge)
                .toList();

        Label badge1 = (Label) root.lookup("#list-badge-1");
        Label badge2 = (Label) root.lookup("#list-badge-2");
        Label badgeExtra = (Label) root.lookup("#list-badge-extra");

        if (badge1 == null || badge2 == null || badgeExtra == null) {
            return;
        }

        badge1.setVisible(false);
        badge2.setVisible(false);
        badgeExtra.setVisible(false);

        if (!badgeLabels.isEmpty()) {
            badge1.setText(badgeLabels.get(0));
            badge1.setVisible(true);
        }
        if (badgeLabels.size() > 1) {
            badge2.setText(badgeLabels.get(1));
            badge2.setVisible(true);
        }
        if (badgeLabels.size() > 2) {
            badgeExtra.setText("+" + (badgeLabels.size() - 2));
            badgeExtra.setVisible(true);
        }
    }

    private String formatListBadge(BookList bookList) {
        String name = bookList.getName();
        return switch (name) {
            case "Улюблені" -> "❤️";
            case "Кинуто" -> "🚫";
            case "Не сподобалося" -> "👎";
            case "Таке собі" -> "😐";
            default -> name.length() > 10 ? name.substring(0, 10) + "…" : name;
        };
    }
}
