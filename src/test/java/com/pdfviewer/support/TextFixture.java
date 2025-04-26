package com.pdfviewer.support;

import com.pdfviewer.entity.PDFBookmark;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.entity.PDFPage;
import java.time.LocalDateTime;

public class TextFixture {

    private TextFixture() {
    }

    public static PDFDocument pdfDocument() {
        return new PDFDocument(
                "/test/path.pdf",
                "test.pdf",
                10,
                LocalDateTime.now(),
                "Test Title",
                "Test Author",
                "Test Description"
        );
    }

    public static PDFBookmark parentBookmark(PDFDocument pdfDocument, int pageNumber) {
        return new PDFBookmark(pdfDocument, "Parent", pageNumber, 1, null);
    }

    public static PDFBookmark childBookmark(PDFDocument pdfDocument, PDFBookmark parent) {
        return new PDFBookmark(pdfDocument, "Child", parent.getPageNumber() + 1, parent.getLevel() + 1, parent);
    }

    public static PDFPage pdfPage(PDFDocument pdfDocument, int page, String content) {
        return new PDFPage(pdfDocument, page, content);
    }
}
