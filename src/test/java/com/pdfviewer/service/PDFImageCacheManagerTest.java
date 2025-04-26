package com.pdfviewer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PDFImageCacheManagerTest {

    @Autowired
    private PDFImageCacheManager cacheManager;

    private Image[] mockImages;

    @BeforeEach
    void setUp() {
        mockImages = new Image[20];
        for (int i = 0; i < 20; i++) {
            mockImages[i] = mock(Image.class);
        }
    }

    @Test
    @DisplayName("Can add images to the cache and cached them.")
    void getCacheImageFrom() {
        cacheManager.addCacheOf(0, mockImages[0]);
        Image cachedImage = cacheManager.getCacheImageFrom(0);

        assertThat(cachedImage).isEqualTo(mockImages[0]);
    }

    @Test
    @DisplayName("Clearing the cache will remove all images")
    void clear() {
        for (int i = 0; i < mockImages.length; i++) {
            cacheManager.addCacheOf(i, mockImages[i]);
        }
        cacheManager.clear();

        assertThat(cacheManager.getCacheImageFrom(0)).isNull();
    }

    @Test
    @DisplayName("When the cache size exceeds MAX_CACHE_SIZE, the least recently used items are evicted")
    void evictLRUImageWhenOverMaxSize() {
        for (int i = 0; i < 20; i++) {
            cacheManager.addCacheOf(i, mockImages[i]);
        }

        cacheManager.getCacheImageFrom(0); // 0 index Image move to last space
        cacheManager.addCacheOf(21, mockImages[0]); // first space is 1 index Image

        assertThat(cacheManager.getCacheImageFrom(0)).isNotNull();
        assertThat(cacheManager.getCacheImageFrom(1)).isNull();
    }
}
