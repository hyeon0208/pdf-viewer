package com.pdfviewer.dto;

public record ContentsSearchResult(Long documentId, String documentTitle, int pageNumber, String snippet) {

}
