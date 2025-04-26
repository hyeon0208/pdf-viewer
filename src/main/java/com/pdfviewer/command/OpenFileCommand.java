package com.pdfviewer.command;

import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.view.PDFRender;
import com.pdfviewer.view.PDFViewerState;
import com.pdfviewer.service.PDFFileService;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OpenFileCommand implements PDFCommand {

    private final File pdfFile;
    private final PDFFileService fileService;
    private final PDFRender pdfRender;
    private final PDFViewerState viewerState;

    @Override
    public void execute() {
        try {
            PDFDocument pdfDocument = fileService.saveFile(pdfFile);
            pdfRender.loadPDF(pdfFile);
            viewerState.setDocument(pdfDocument);
            viewerState.setStatusMessage("PDF loaded : " + pdfFile.getName());
        } catch (IOException exception) {
            log.error("Error opening PDF file", exception);
            viewerState.setStatusMessage("ERROR: " + exception.getMessage());
            throw new RuntimeException("Cannot open PDF file : " + exception);
        }
    }
}
