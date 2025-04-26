package com.pdfviewer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "pdf_page")
@ToString(exclude = {"document"})
@NoArgsConstructor
public class PDFPage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pdf_page_seq")
    @SequenceGenerator(
            name = "pdf_page_seq",
            sequenceName = "pdf_page_sequence",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private PDFDocument document;

    @Column(nullable = false)
    private int pageNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    public PDFPage(PDFDocument document, int pageNumber, String content) {
        this.document = document;
        this.pageNumber = pageNumber;
        this.content = content;
    }
}
