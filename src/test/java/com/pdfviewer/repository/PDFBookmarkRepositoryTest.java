package com.pdfviewer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pdfviewer.entity.PDFBookmark;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.support.BaseRepositoryTest;
import com.pdfviewer.support.TextFixture;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PDFBookmarkRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PDFBookmarkRepository pdfBookmarkRepository;

    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    @Test
    @DisplayName("Find all bookmarks for this document")
    void findAllByDocumentId() {
        PDFDocument pdfDocument = TextFixture.pdfDocument();
        pdfDocumentRepository.save(pdfDocument);

        List<PDFBookmark> pdfBookmarks = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            pdfBookmarks.add(TextFixture.parentBookmark(pdfDocument, i));
        }
        pdfBookmarkRepository.saveAll(pdfBookmarks);

        List<PDFBookmark> allByDocumentId = pdfBookmarkRepository.findAllByDocumentId(pdfDocument.getId());

        assertThat(allByDocumentId).hasSize(5);
    }
}
