package com.pdfviewer.dto;

import com.pdfviewer.entity.PDFBookmark;
import java.util.ArrayList;
import java.util.List;

public record PDFBookmarkDTO(Long id, String title, int pageNumber, int level, List<PDFBookmarkDTO> children) {

    public static PDFBookmarkDTO from(PDFBookmark pdfBookmark) {
        return new PDFBookmarkDTO(
                pdfBookmark.getId(),
                pdfBookmark.getTitle(),
                pdfBookmark.getPageNumber(),
                pdfBookmark.getLevel(),
                new ArrayList<>()
        );
    }
}
