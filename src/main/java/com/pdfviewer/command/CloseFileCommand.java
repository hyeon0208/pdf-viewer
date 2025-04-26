package com.pdfviewer.command;

import com.pdfviewer.view.PDFRender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloseFileCommand implements PDFCommand {

    private final PDFRender pdfRender;

    @Override
    public void execute() {
        pdfRender.close();
    }
}
