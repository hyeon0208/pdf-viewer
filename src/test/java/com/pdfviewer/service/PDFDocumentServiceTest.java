package com.pdfviewer.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pdfviewer.exception.ExistsFileException;
import com.pdfviewer.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PDFDocumentServiceTest extends BaseServiceTest {

    @Autowired
    private PDFDocumentService documentService;

    @Test
    @DisplayName("Exception occurs when trying to find a document with a non-existent path")
    void findByFilePath_WhenPathDoesNotExist_ShouldThrowException() {
        assertThatThrownBy(() -> documentService.findByFilePath("/test/path.pdf"))
                .isInstanceOf(ExistsFileException.class);
    }
}
