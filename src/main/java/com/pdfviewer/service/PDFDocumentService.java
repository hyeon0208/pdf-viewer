package com.pdfviewer.service;

import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.exception.ExistsFileException;
import com.pdfviewer.repository.PDFDocumentRepository;
import java.io.File;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFDocumentService {

    private final PDFDocumentRepository pdfDocumentRepository;

    @Transactional(readOnly = true)
    public PDFDocument findByFilePath(String filePath) {
        PDFDocument pdfDocument = pdfDocumentRepository.findByFilePath(filePath)
                .orElseThrow(() -> new ExistsFileException("There is no PDF document for that path."));
        pdfDocument.setLastOpenedTime(LocalDateTime.now());
        return pdfDocument;
    }

    @Transactional
    public PDFDocument save(File pdfFile, PDDocument pdDocument) {
        PDDocumentInformation info = pdDocument.getDocumentInformation();
        PDFDocument pdfDocument = new PDFDocument(
                pdfFile.getPath(),
                pdfFile.getName(),
                pdDocument.getNumberOfPages(),
                LocalDateTime.now(),
                info.getTitle(),
                info.getAuthor(),
                info.getSubject()
        );
        pdfDocumentRepository.save(pdfDocument);
        log.info("PDF Producer : {} | Creator : {}", info.getProducer(), info.getCreator());
        log.info("Document metadata update complete | page count : {}", pdDocument.getNumberOfPages());
        return pdfDocument;
    }
}
