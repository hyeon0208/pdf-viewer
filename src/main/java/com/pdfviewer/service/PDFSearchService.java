package com.pdfviewer.service;

import com.pdfviewer.entity.PDFPage;
import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.repository.PDFDocumentRepository;
import com.pdfviewer.repository.PDFPageRepository;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFSearchService {

    private final PDFPageRepository pageRepository;
    private final PDFDocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<ContentsSearchResult> searchInDocument(Long documentId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String trimmed = keyword.trim();
        log.info("Search for keyword '{}'", trimmed);
        return documentRepository.findById(documentId)
                .map(document -> {
                    List<PDFPage> matchingPages = pageRepository.findByPDFDocumentIdAndContentContaining(documentId, trimmed);
                    String documentTitle = document.getTitle() != null ? document.getTitle() : document.getFileName();
                    List<ContentsSearchResult> results = getSearchResults(matchingPages, keyword, documentTitle, documentId);
                    log.info("Search results: {} items", results.size());
                    return results;
                }).orElse(Collections.emptyList());
    }

    private List<ContentsSearchResult> getSearchResults(List<PDFPage> pages, String keyword, String documentTitle, Long documentId) {
        return pages.stream()
                .map(page -> {
                    String snippet = extractSnippet(page.getContent(), keyword);
                    return new ContentsSearchResult(documentId, documentTitle, page.getPageNumber(), snippet);
                }).collect(Collectors.toList());
    }

    private String extractSnippet(String content, String keyword) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String lowerContent = content.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        int keywordIndex = lowerContent.indexOf(lowerKeyword);

        if (keywordIndex == -1) {
            return "";
        }
        int snippetStart = Math.max(0, keywordIndex - 50);
        int snippetEnd = Math.min(content.length(), keywordIndex + 50);

        String prefix = snippetStart > 0 ? "..." : "";
        String suffix = snippetEnd < content.length() ? "..." : "";
        return prefix + content.substring(snippetStart, snippetEnd) + suffix;
    }
}
