package com.pdfviewer.view;

import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.service.PDFAsyncService;
import com.pdfviewer.service.PDFImageCacheManager;
import com.pdfviewer.util.Alerter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFRender {

    private static final float RENDER_SCALE = 2.0f;
    private static final double ZOOM_STEP = 0.2;
    private static final double DEFAULT_ZOOM = 0.6;

    private final PDFImageCacheManager cacheManager;
    private final PDFAsyncService pdfAsyncService;
    private final PDFViewerState viewerState;

    private PDDocument pdDocument;
    private PDFRenderer pdfRenderer;
    private VBox pagesContainer;

    public void initialize(VBox pagesContainer) {
        this.pagesContainer = pagesContainer;
        viewerState.setZoomFactor(DEFAULT_ZOOM);
    }

    public void loadPDF(File pdfFile) throws IOException {
        close();
        try {
            this.pdDocument = Loader.loadPDF(pdfFile);
            this.pdfRenderer = new PDFRenderer(pdDocument);
            viewerState.updatePageInfo(0, pdDocument.getNumberOfPages());
            cacheManager.clear();
            renderCurrentPage();
        } catch (Exception exception) {
            close();
            log.error("PDF Load Error", exception);
            throw new RuntimeException(exception);
        }
    }

    public Image getPageImage(int pageIndex) throws IOException {
        if (pageIndex < 0 || pageIndex >= viewerState.getTotalPages().get()) {
            throw new IllegalArgumentException("Invalid page index : " + pageIndex);
        }

        Image cachedImage = cacheManager.getCacheImageFrom(pageIndex);
        if (cachedImage != null) {
            return cachedImage;
        }

        float scale = RENDER_SCALE * (float) viewerState.getZoomFactor().get();
        BufferedImage bufferedImage = pdfRenderer.renderImage(pageIndex, scale, ImageType.RGB);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        cacheManager.addCacheOf(pageIndex, image);
        return image;
    }

    public void goToPage(int pageNumber) {
        int totalPages = viewerState.getTotalPages().get();
        if (pageNumber >= 0 && pageNumber < totalPages) {
            viewerState.getCurrentPage().set(pageNumber);
            renderCurrentPage();
        }
    }

    public void nextPage() {
        int currentPage = viewerState.getCurrentPage().get();
        int totalPages = viewerState.getTotalPages().get();

        if (currentPage < totalPages - 1) {
            viewerState.getCurrentPage().set(currentPage + 1);
            renderCurrentPage();
        }
    }

    public void previousPage() {
        int currentPage = viewerState.getCurrentPage().get();
        if (currentPage > 0) {
            viewerState.getCurrentPage().set(currentPage - 1);
            renderCurrentPage();
        }
    }

    public void firstPage() {
        viewerState.getCurrentPage().set(0);
        renderCurrentPage();
    }

    public void lastPage() {
        int totalPages = viewerState.getTotalPages().get();
        viewerState.getCurrentPage().set(totalPages - 1);
        renderCurrentPage();
    }

    public void zoomIn() {
        double currentZoom = viewerState.getZoomFactor().get();
        viewerState.setZoomFactor(currentZoom + ZOOM_STEP);
        renderCurrentPage();
    }

    public void zoomOut() {
        double currentZoom = viewerState.getZoomFactor().get();
        if (currentZoom > ZOOM_STEP) {
            viewerState.setZoomFactor(currentZoom - ZOOM_STEP);
            renderCurrentPage();
        }
    }

    public void resetZoom() {
        viewerState.setZoomFactor(DEFAULT_ZOOM);
        renderCurrentPage();
    }

    public void renderCurrentPage() {
        try {
            pagesContainer.getChildren().clear();

            int currentPageIndex = viewerState.getCurrentPage().get();
            ImageView imageView = new ImageView(getPageImage(currentPageIndex));
            imageView.setPreserveRatio(true);

            double containerWidth = pagesContainer.getWidth();
            double zoomFactor = viewerState.getZoomFactor().get();
            imageView.setFitWidth(containerWidth * zoomFactor);
            pagesContainer.getChildren().add(imageView);
            pdfAsyncService.preloadAdjacentPages(viewerState, pdfRenderer);
        } catch (Exception exception) {
            log.error("Page rendering error", exception);
            Alerter.showErrorAlert("Page rendering error", exception.getMessage());
        }
    }

    public void goToSearchResult(ContentsSearchResult result) {
        goToPage(result.pageNumber() - 1);
    }

    public void goToBookmark(PDFBookmarkDTO bookmark) {
        goToPage(bookmark.pageNumber() - 1);
    }

    public void close() {
        try {
            if (pdDocument != null) {
                pdDocument.close();
            }
        } catch (IOException exception) {
            log.error("Error closing PDF document", exception);
        } finally {
            pdDocument = null;
            pdfRenderer = null;
            viewerState.setDocument(null);
            viewerState.clearSearchResults();
            cacheManager.clear();
            viewerState.setCurrentPageImage(null);
            viewerState.setBookmarksRoot(null);
            pagesContainer.getChildren().clear();
        }
    }
}
