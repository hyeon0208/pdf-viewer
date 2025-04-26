package com.pdfviewer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.repository.PDFDocumentRepository;
import com.pdfviewer.repository.PDFPageRepository;
import com.pdfviewer.support.BaseServiceTest;
import com.pdfviewer.support.TextFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PDFSearchServiceTest extends BaseServiceTest {

    @Autowired
    private PDFSearchService searchService;

    @Autowired
    private PDFDocumentRepository documentRepository;

    @Autowired
    private PDFPageRepository pageRepository;

    private PDFDocument pdfDocument;

    @BeforeEach
    void setUp() {
        pdfDocument = TextFixture.pdfDocument();
        documentRepository.save(pdfDocument);

        pageRepository.save(TextFixture.pdfPage(pdfDocument, 1, "content 1"));
        pageRepository.save(TextFixture.pdfPage(pdfDocument, 2, "content 2"));
        pageRepository.save(TextFixture.pdfPage(pdfDocument, 3, "content 3"));
    }

    @Test
    @DisplayName("When searching by keyword, it returns pages whose content includes the keyword")
    void searchInDocument() {
        List<ContentsSearchResult> results = searchService.searchInDocument(pdfDocument.getId(), "content");

        assertThat(results).hasSize(3);
        assertThat(results.get(0).pageNumber()).isEqualTo(1);
        assertThat(results.get(1).pageNumber()).isEqualTo(2);
        assertThat(results.get(2).pageNumber()).isEqualTo(3);

        assertThat(results.get(0).snippet()).containsIgnoringCase("1");
        assertThat(results.get(1).snippet()).containsIgnoringCase("2");
        assertThat(results.get(2).snippet()).containsIgnoringCase("3");
    }

    @Test
    @DisplayName("Searching with an empty keyword or empty string returns empty results")
    void searchInDocumentWithEmptyKeyword() {
        List<ContentsSearchResult> emptyKeywordResults = searchService.searchInDocument(pdfDocument.getId(), "");
        List<ContentsSearchResult> nullKeywordResults = searchService.searchInDocument(pdfDocument.getId(), null);

        assertThat(emptyKeywordResults).isEmpty();
        assertThat(nullKeywordResults).isEmpty();
    }

    @Test
    @DisplayName("Searching with non-existent keywords returns empty results")
    void searchInDocument_WithNonExistentKeyword_ShouldReturnEmptyResults() {
        List<ContentsSearchResult> results = searchService.searchInDocument(pdfDocument.getId(), "nonexistent");

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Searching for a non-existent document ID should return an empty result")
    void searchInDocument_WithNonExistentDocumentId_ShouldReturnEmptyResults() {
        // When
        List<ContentsSearchResult> results = searchService.searchInDocument(999L, "keywords");

        // Then
        assertThat(results).isEmpty();
    }
}
