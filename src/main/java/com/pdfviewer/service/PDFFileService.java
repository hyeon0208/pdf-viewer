package com.pdfviewer.service;

import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.exception.ExistsFileException;
import com.pdfviewer.util.Alerter;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFFileService {

    private final PDFDocumentService pdfDocumentService;
    private final PDFBookmarkService pdfBookmarkService;
    private final PDFPageService pdfPageService;

    public PDFDocument saveFile(File pdfFile) {
        String filePath = pdfFile.getAbsolutePath();
        log.info("Open PDF file : {}", filePath);
        try {
            return pdfDocumentService.findByFilePath(filePath);
        } catch (ExistsFileException exception) {
            log.info("PDF document does not exist in that path, so save it");
            return saveDocument(pdfFile);
        }
    }

    private PDFDocument saveDocument(File pdfFile) {
        try {
            PDDocument pdDocument = Loader.loadPDF(pdfFile);
            PDFDocument pdfDocument = pdfDocumentService.save(pdfFile, pdDocument);
            pdfPageService.saveAll(pdfDocument, pdDocument);
            pdfBookmarkService.saveAll(pdfDocument, pdDocument);
            return pdfDocument;
        } catch (Exception exception) {
            log.error("Error occurred while processing the contents of the pdf document : {}",
                    exception.getMessage(),
                    exception
            );
            Alerter.showErrorAlert("PDF Document Error", exception.getMessage());
            throw new RuntimeException("Failed to process PDF document: " + exception.getMessage(), exception);
        }
    }

    @Transactional(readOnly = true)
    public List<PDFBookmarkDTO> findAllBookmarks(PDFDocument pdfDocument) {
        return pdfBookmarkService.findAllByDocumentId(pdfDocument);
    }
}
