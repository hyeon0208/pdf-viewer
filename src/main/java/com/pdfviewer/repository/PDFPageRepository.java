package com.pdfviewer.repository;

import com.pdfviewer.entity.PDFPage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PDFPageRepository extends JpaRepository<PDFPage, Long> {

    @Query("SELECT p FROM PDFPage p WHERE p.document.id = :pdfDocumentId AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PDFPage> findByPDFDocumentIdAndContentContaining(Long pdfDocumentId, String keyword);
}
