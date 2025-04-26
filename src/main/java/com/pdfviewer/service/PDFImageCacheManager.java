package com.pdfviewer.service;

import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PDFImageCacheManager {

    private static final int MAX_CACHE_SIZE = 20;

    private final Map<Integer, Image> pageCache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Image> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public Image getCacheImageFrom(int pageIndex) {
        return pageCache.get(pageIndex);
    }

    public void addCacheOf(int pageIndex, Image image) {
        pageCache.put(pageIndex, image);
    }

    public void clear() {
        pageCache.clear();
    }

    private void logImageMemoryUsage(int pageIndex, Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int pixelByte = 4; // (32 bits per pixel: R, G, B, A)

        long memorySizeBytes = (long) width * height * pixelByte;
        double memorySizeMB = memorySizeBytes / (1024.0 * 1024.0); // Bytes to MB

        log.info("page {} | size {} x {} | memory usage: {}MB", pageIndex + 1, width, height, String.format("%.2f", memorySizeMB));
    }
}
