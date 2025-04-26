package com.pdfviewer.command;

import com.pdfviewer.dto.ContentsSearchResult;
import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.view.PDFRender;
import com.pdfviewer.service.PDFSearchService;
import com.pdfviewer.view.PDFViewerState;
import com.pdfviewer.service.PDFFileService;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PDFCommandFactory {

    private final PDFViewerState viewerState;
    private final PDFRender pdfRender;
    private final PDFFileService fileService;
    private final PDFSearchService searchService;

    public PDFCommand createOpenFileCommand(File pdfFile) {
        return new OpenFileCommand(pdfFile, fileService, pdfRender, viewerState);
    }

    public PDFCommand createLoadBookmarksCommand() {
        return new LoadBookmarksCommand(viewerState, fileService);
    }

    public PDFCommand createCloseFileCommand() {
        return new CloseFileCommand(pdfRender);
    }

    public PDFCommand createSearchCommand(String keyword) {
        return new SearchCommand(keyword, viewerState, searchService);
    }

    public PDFCommand createNextPageCommand() {
        return pdfRender::nextPage;
    }

    public PDFCommand createPrevPageCommand() {
        return pdfRender::previousPage;
    }

    public PDFCommand createFirstPageCommand() {
        return pdfRender::firstPage;
    }

    public PDFCommand createLastPageCommand() {
        return pdfRender::lastPage;
    }

    public PDFCommand createGoToPageCommand(int pageNumber) {
        return () -> pdfRender.goToPage(pageNumber - 1); // UI : 1-based, application : 0-based
    }

    public PDFCommand createZoomInCommand() {
        return pdfRender::zoomIn;
    }

    public PDFCommand createZoomOutCommand() {
        return pdfRender::zoomOut;
    }

    public PDFCommand createResetZoomCommand() {
        return pdfRender::resetZoom;
    }

    public PDFCommand createGoToSearchResultCommand(ContentsSearchResult result) {
        return () -> pdfRender.goToSearchResult(result);
    }

    public PDFCommand createGoToBookmarkCommand(PDFBookmarkDTO bookmark) {
        return () -> pdfRender.goToBookmark(bookmark);
    }
}
