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
@Table(name = "pdf_bookmark")
@ToString(exclude = {"parent", "document"})
@NoArgsConstructor
public class PDFBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pdf_bookmark_seq")
    @SequenceGenerator(
            name = "pdf_bookmark_seq",
            sequenceName = "pdf_bookmark_sequence",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private PDFDocument document;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int pageNumber;

    @Column(nullable = false)
    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PDFBookmark parent;

    public PDFBookmark(PDFDocument document, String title, int pageNumber, int level, PDFBookmark parent) {
        this.document = document;
        this.title = title;
        this.pageNumber = pageNumber;
        this.level = level;
        this.parent = parent;
    }
}
