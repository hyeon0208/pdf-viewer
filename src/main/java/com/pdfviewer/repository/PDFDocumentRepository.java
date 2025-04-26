package com.pdfviewer.repository;

import com.pdfviewer.entity.PDFDocument;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFDocumentRepository extends JpaRepository<PDFDocument, Long> {

    Optional<PDFDocument> findByFilePath(String filePath);
}
