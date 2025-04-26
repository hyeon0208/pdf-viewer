package com.pdfviewer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pdf_documents")
@NoArgsConstructor
public class PDFDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pdf_document_seq")
    @SequenceGenerator(
            name = "pdf_document_seq",
            sequenceName = "pdf_document_sequence",
            allocationSize = 50
    )
    private Long id;

    @Column(unique = true, nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    @Column
    private int pageCount;

    @Column
    private LocalDateTime lastOpenedTime;

    @Column
    private String title;

    @Column
    private String author;

    @Column(length = 1000)
    private String description;

    public PDFDocument(
            String filePath,
            String fileName,
            int pageCount,
            LocalDateTime lastOpenedTime,
            String title,
            String author,
            String description
    ) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.pageCount = pageCount;
        this.lastOpenedTime = lastOpenedTime;
        this.title = title;
        this.author = author;
        this.description = description;
    }
}
