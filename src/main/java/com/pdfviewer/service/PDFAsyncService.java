package com.pdfviewer.service;

import com.pdfviewer.view.PDFViewerState;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFAsyncService {

    private final PDFImageCacheManager imageCacheManager;

    @Async
    public void preloadAdjacentPages(PDFViewerState viewerState, PDFRenderer pdfRenderer) {
        int currentPage = viewerState.getCurrentPage().get();
        int totalPages = viewerState.getTotalPages().get();
        double zoomFactor = viewerState.getZoomFactor().get();
        try {
            if (currentPage + 1 < totalPages) {
                renderAndCacheImage(pdfRenderer, currentPage + 1, zoomFactor);
            }
            if (currentPage > 0) {
                renderAndCacheImage(pdfRenderer, currentPage - 1, zoomFactor);
            }
        } catch (Exception exception) {
            log.warn("Adjacent page preload Error", exception);
        }
    }

    public void renderAndCacheImage(PDFRenderer pdfRenderer, int pageIndex, double zoomFactor) throws IOException {
        Image cachedImage = imageCacheManager.getCacheImageFrom(pageIndex);
        if (cachedImage != null) {
            return;
        }

        float scale = 2.0f * (float) zoomFactor;
        BufferedImage bufferedImage = pdfRenderer.renderImage(pageIndex, scale, ImageType.RGB);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        imageCacheManager.addCacheOf(pageIndex, image);
    }
}
