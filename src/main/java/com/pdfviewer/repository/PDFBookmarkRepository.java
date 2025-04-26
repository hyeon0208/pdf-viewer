package com.pdfviewer.repository;

import com.pdfviewer.entity.PDFBookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFBookmarkRepository extends JpaRepository<PDFBookmark, Long> {

    List<PDFBookmark> findAllByDocumentId(Long documentId);
}
