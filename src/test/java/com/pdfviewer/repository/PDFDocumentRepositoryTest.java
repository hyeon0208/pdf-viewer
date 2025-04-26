package com.pdfviewer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.support.BaseRepositoryTest;
import com.pdfviewer.support.TextFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PDFDocumentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PDFDocumentRepository pdfDocumentRepository;

    @Test
    @DisplayName("Find documents saved in the given path")
    void findByFilePath() {
        PDFDocument pdfDocument = TextFixture.pdfDocument();
        pdfDocumentRepository.save(pdfDocument);

        PDFDocument findDocument = pdfDocumentRepository.findByFilePath(pdfDocument.getFilePath())
                .orElseThrow(() -> new RuntimeException("findByFilePath Test Error"));

        assertThat(findDocument.getId()).isEqualTo(pdfDocument.getId());
    }
}
