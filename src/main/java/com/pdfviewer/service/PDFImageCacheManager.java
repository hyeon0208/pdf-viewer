package com.pdfviewer.service;

import com.pdfviewer.dto.CompressedImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PDFImageCacheManager {

    private static final int MAX_CACHE_SIZE = 20;
    private static final String FORMAT_TYPE = "png";

    private final Map<Integer, CompressedImage> pageCache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, CompressedImage> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public Image getCacheImageFrom(int pageIndex) {
        CompressedImage compressed = pageCache.get(pageIndex);
        if (compressed == null) {
            return null;
        }
        return compressed.toImage();
    }

    public void addCache(int pageIndex, Image image) {
        try {
            BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);
            byte[] compressedData = compress(buffered);
            CompressedImage compressedImage = new CompressedImage(
                    compressedData,
                    (int) image.getWidth(),
                    (int) image.getHeight()
            );
            pageCache.put(pageIndex, compressedImage);
        } catch (IOException e) {
            log.error("Failed to compress and cache image as PNG for page {}", pageIndex, e);
        }
    }

    private byte[] compress(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_TYPE, baos);
        return baos.toByteArray();
    }

    public void clear() {
        pageCache.clear();
    }

    private void logCompressedImageMemoryUsage(int pageIndex, CompressedImage image) {
        int width = image.width();
        int height = image.height();

        long compressedSizeBytes = image.data().length;
        double compressedSizeMB = compressedSizeBytes / (1024.0 * 1024.0);

        log.info("page {} | size {} x {} | memory usage: {}MB", pageIndex + 1, width, height, String.format("%.2f", compressedSizeMB));
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
