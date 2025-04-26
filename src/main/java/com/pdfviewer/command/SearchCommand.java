package com.pdfviewer.command;

import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.service.PDFSearchService;
import com.pdfviewer.view.PDFViewerState;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SearchCommand implements PDFCommand {

    private final String keyword;
    private final PDFViewerState viewerState;
    private final PDFSearchService searchService;

    @Override
    public void execute() {
        if (keyword == null || keyword.trim().isEmpty()) {
            viewerState.clearSearchResults();
            return;
        }
        try {
            viewerState.clearSearchResults();
            Long documentId = viewerState.getCurrentDocument().get().getId();
            List<ContentsSearchResult> results = searchService.searchInDocument(documentId, keyword);
            if (results.isEmpty()) {
                viewerState.setStatusMessage("No Search Result");
            } else {
                viewerState.addSearchResults(results);
                viewerState.setStatusMessage("Search Result : " + results.size());
            }
        } catch (Exception exception) {
            log.error("Search Fail : ", exception);
            viewerState.setStatusMessage("Search Fail : " + exception.getMessage());
        }
    }
}
