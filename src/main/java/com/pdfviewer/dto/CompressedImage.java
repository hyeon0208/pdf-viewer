package com.pdfviewer.dto;

import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record CompressedImage(byte[] data, int width, int height) {

    public Image toImage() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            return new Image(bais);
        } catch (Exception exception) {
            log.error("Failed to convert compressed data to Image", exception);
            throw new RuntimeException("Failed to convert compressed data to Image");
        }
    }
}
