package com.pdfviewer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.entity.PDFBookmark;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.repository.PDFBookmarkRepository;
import com.pdfviewer.repository.PDFDocumentRepository;
import com.pdfviewer.support.BaseServiceTest;
import com.pdfviewer.support.TextFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PDFBookmarkServiceTest extends BaseServiceTest {

    @Autowired
    private PDFBookmarkService bookmarkService;

    @Autowired
    private PDFBookmarkRepository bookmarkRepository;

    @Autowired
    private PDFDocumentRepository documentRepository;

    @Test
    @DisplayName("Find hierarchical bookmarks for a document")
    void findAllByDocumentId() {
        PDFDocument pdfDocument = TextFixture.pdfDocument();
        documentRepository.save(pdfDocument);

        PDFBookmark parent = TextFixture.parentBookmark(pdfDocument, 1);
        bookmarkRepository.save(parent);

        PDFBookmark child = TextFixture.childBookmark(pdfDocument, parent);
        bookmarkRepository.save(child);

        List<PDFBookmarkDTO> result = bookmarkService.findAllByDocumentId(pdfDocument);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).children()).hasSize(1);
    }
}
