package com.pdfviewer.service;

import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.entity.PDFPage;
import com.pdfviewer.infra.LogPerformance;
import com.pdfviewer.repository.PDFPageRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFPageService {

    private final PDFPageRepository pdfPageRepository;

    @LogPerformance
    @Transactional
    public void saveAll(PDFDocument pdfDocument, PDDocument pdDocument) {
        try {
            List<PDFPage> pages = extractPages(pdfDocument, pdDocument);
            pdfPageRepository.saveAll(pages);
            log.info("Page content save completed: Total {} pages", pdDocument.getNumberOfPages());
        } catch (Exception exception) {
            log.error("Error occurred while processing Page content");
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    private List<PDFPage> extractPages(PDFDocument pdfDocument, PDDocument pdDocument) throws IOException {
        int totalPages = pdDocument.getNumberOfPages();
        List<PDFPage> pages = new ArrayList<>(totalPages);
        PDFTextStripper stripper = new PDFTextStripper();
        for (int i = 1; i <= totalPages; i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String pageText = stripper.getText(pdDocument);
            pages.add(new PDFPage(pdfDocument, i, pageText));
        }
        return pages;
    }
}
