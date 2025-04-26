package com.pdfviewer.view;

import com.pdfviewer.command.PDFCommandFactory;
import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.util.Alerter;
import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PDFViewController {

    // UI Components - PDF Viewer Area
    @FXML
    private ScrollPane pdfScrollPane;
    @FXML
    private VBox pageContainer;
    @FXML
    private Label pageInfoLabel;
    @FXML
    private Label statusLabel;

    // UI Components - File Related Buttons
    @FXML
    private Button openFileButton;
    @FXML
    private Button closeFileButton;

    // UI Components - Page Navigation
    @FXML
    private Button nextPageButton;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button firstPageButton;
    @FXML
    private Button lastPageButton;
    @FXML
    private TextField pageInputField;

    // UI Components - Zoom Button
    @FXML
    private Button zoomInButton;
    @FXML
    private Button zoomOutButton;
    @FXML
    private Button resetZoomButton;

    // UI Components - Search Related
    @FXML
    private TextField searchField;
    @FXML
    private ListView<ContentsSearchResult> searchResultsListView;

    // UI Components - Bookmark Related
    @FXML
    private TreeView<PDFBookmarkDTO> bookmarksTreeView;

    private final PDFCommandFactory commandFactory;
    private final PDFViewerState viewerState;
    private final PDFRender pdfRender;

    @FXML
    public void initialize() {
        pdfRender.initialize(pageContainer);
        setupUIBindings();
        setUpPageInfoLabel();
        setupSearchResultList();
        setupBookmarksTree();
    }

    private void setupUIBindings() {
        closeFileButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        nextPageButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        prevPageButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        firstPageButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        lastPageButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        zoomInButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        zoomOutButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        resetZoomButton.disableProperty().bind(viewerState.getDocumentLoaded().not());
        searchField.disableProperty().bind(viewerState.getDocumentLoaded().not());
        pageInputField.disableProperty().bind(viewerState.getDocumentLoaded().not());
    }

    private void setUpPageInfoLabel() {
        pageInfoLabel.textProperty().bind(Bindings.createStringBinding(
                        () -> String.format("Page : %d / %d",
                                viewerState.getDisplayPageNumber(),
                                viewerState.getTotalPages().get()
                        ), viewerState.getCurrentPage(), viewerState.getTotalPages()
                )
        );
        statusLabel.textProperty().bind(viewerState.getStatusMessage());
    }

    private void setupSearchResultList() {
        searchResultsListView.setItems(viewerState.getSearchResults().get());
        searchResultsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ContentsSearchResult item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("page %d: %s", item.pageNumber(), item.snippet()));
                }
            }
        });

        searchResultsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        commandFactory.createGoToSearchResultCommand(newValue).execute();
                    }
                }
        );
    }

    private void setupBookmarksTree() {
        bookmarksTreeView.rootProperty().bind(viewerState.getBookmarksRoot());
        bookmarksTreeView.setShowRoot(false);

        bookmarksTreeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(PDFBookmarkDTO bookmark, boolean empty) {
                super.updateItem(bookmark, empty);
                if (empty || bookmark == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (p.%d)", bookmark.title(), bookmark.pageNumber()));
                }
            }
        });

        bookmarksTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        commandFactory.createGoToBookmarkCommand(newValue.getValue()).execute();
                    }
                }
        );
    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(pdfScrollPane.getScene().getWindow());
        if (selectedFile != null) {
            commandFactory.createOpenFileCommand(selectedFile).execute();
            commandFactory.createLoadBookmarksCommand().execute();
        }
    }

    @FXML
    private void closeFile() {
        commandFactory.createCloseFileCommand().execute();
    }

    @FXML
    private void goToNextPage() {
        commandFactory.createNextPageCommand().execute();
    }

    @FXML
    private void goToPrevPage() {
        commandFactory.createPrevPageCommand().execute();
    }

    @FXML
    private void goToFirstPage() {
        commandFactory.createFirstPageCommand().execute();
    }

    @FXML
    private void goToLastPage() {
        commandFactory.createLastPageCommand().execute();
    }

    @FXML
    private void handlePageInput() {
        String input = pageInputField.getText().trim();
        int pageNumber = Integer.parseInt(input);
        int totalPages = viewerState.getTotalPages().get();
        if (pageNumber >= 1 && pageNumber <= totalPages) {
            commandFactory.createGoToPageCommand(pageNumber).execute();
            pageInputField.selectAll();
        } else {
            Alerter.showWarnAlert("", "Please enter a valid page number (1-" + totalPages + ")");
        }
    }

    @FXML
    private void handleZoomIn() {
        commandFactory.createZoomInCommand().execute();
    }

    @FXML
    private void handleZoomOut() {
        commandFactory.createZoomOutCommand().execute();
    }

    @FXML
    private void handleResetZoom() {
        commandFactory.createResetZoomCommand().execute();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        commandFactory.createSearchCommand(keyword).execute();
    }
}
