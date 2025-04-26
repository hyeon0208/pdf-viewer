package com.pdfviewer.view;

import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.entity.PDFDocument;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PDFViewerState {

    private static final double DEFAULT_ZOOM = 0.6;

    private final ObjectProperty<TreeItem<PDFBookmarkDTO>> bookmarksRoot = new SimpleObjectProperty<>(new TreeItem<>());
    private final ObjectProperty<ObservableList<ContentsSearchResult>> searchResults = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Image> currentPageImage = new SimpleObjectProperty<>();
    private final ObjectProperty<PDFDocument> currentDocument = new SimpleObjectProperty<>();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM);
    private final BooleanProperty documentLoaded = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final StringProperty searchKeyword = new SimpleStringProperty("");

    public void setDocument(PDFDocument pdfDocument) {
        currentDocument.set(pdfDocument);
        documentLoaded.set(pdfDocument != null);
        if (pdfDocument == null) {
            resetState();
        }
    }

    public void updatePageInfo(int current, int total) {
        currentPage.set(current);
        totalPages.set(total);
    }

    public void setStatusMessage(String message) {
        statusMessage.set(message);
    }

    public void setZoomFactor(double factor) {
        zoomFactor.set(factor);
    }

    public void setCurrentPageImage(Image image) {
        this.currentPageImage.set(image);
    }

    public void setBookmarksRoot(TreeItem<PDFBookmarkDTO> root) {
        bookmarksRoot.set(root);
    }

    public void clearSearchResults() {
        searchResults.get().clear();
    }

    public void addSearchResults(List<ContentsSearchResult> results) {
        searchResults.get().clear();
        searchResults.get().addAll(results);
    }

    private void resetState() {
        currentPage.set(0);
        totalPages.set(0);
        zoomFactor.set(DEFAULT_ZOOM);
        searchKeyword.set("");
        statusMessage.set("");
        searchResults.get().clear();
        bookmarksRoot.set(new TreeItem<>());
    }

    public int getDisplayPageNumber() {
        return currentPage.get() + 1;
    }
}
