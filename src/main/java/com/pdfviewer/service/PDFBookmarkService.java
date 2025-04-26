package com.pdfviewer.service;

import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.entity.PDFBookmark;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.infra.LogPerformance;
import com.pdfviewer.repository.PDFBookmarkRepository;
import com.pdfviewer.util.Alerter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFBookmarkService {

    private final PDFBookmarkRepository bookmarkRepository;

    @LogPerformance
    @Transactional(readOnly = true)
    public List<PDFBookmarkDTO> findAllByDocumentId(PDFDocument pdfDocument) {
        List<PDFBookmark> bookmarks = bookmarkRepository.findAllByDocumentId(pdfDocument.getId());
        return toHierarchicalBookmarkDTO(bookmarks);
    }

    private List<PDFBookmarkDTO> toHierarchicalBookmarkDTO(List<PDFBookmark> bookmarks) {
        Map<Long, PDFBookmarkDTO> bookmarkDTOMap = new HashMap<>();
        for (PDFBookmark bookmark : bookmarks) {
            bookmarkDTOMap.put(bookmark.getId(), PDFBookmarkDTO.from(bookmark));
        }

        List<PDFBookmarkDTO> rootBookmarks = new ArrayList<>();
        for (PDFBookmark bookmark : bookmarks) {
            PDFBookmarkDTO bookmarkDTO = bookmarkDTOMap.get(bookmark.getId());
            if (bookmark.getParent() == null) {
                rootBookmarks.add(bookmarkDTO);
            } else {
                Long parentId = bookmark.getParent().getId();
                PDFBookmarkDTO parentDTO = bookmarkDTOMap.get(parentId);
                if (parentDTO != null) {
                    parentDTO.children().add(bookmarkDTO);
                } else {
                    rootBookmarks.add(bookmarkDTO);
                }
            }
        }
        return rootBookmarks;
    }

    @Transactional
    public void saveAll(PDFDocument pdfDocument, PDDocument pdDocument) {
        try {
            List<PDFBookmark> bookmarks = extractBookmarks(pdfDocument, pdDocument);
            if (bookmarks.isEmpty()) {
                log.warn("Failed to save bookmarks, but continuing");
                Alerter.showWarnAlert("Bookmarks Extract Error", "Bookmarks cannot be created because there are no built-in bookmarks");
                return;
            }
            bookmarkRepository.saveAll(bookmarks);
            log.info("{} bookmarks have been saved", bookmarks.size());
        } catch (IOException exception) {
            log.error("Error occurred while creating a bookmark : ", exception);
            throw new RuntimeException("Error occurred while creating a bookmark" , exception);
        }
    }

    private List<PDFBookmark> extractBookmarks(PDFDocument pdfDocument, PDDocument pdDocument) throws IOException {
        List<PDFBookmark> bookmarks = new ArrayList<>();
        PDDocumentOutline outline = pdDocument.getDocumentCatalog().getDocumentOutline();
        if (hasBuiltInBookmarks(outline)) {
            bookmarks = extractBuiltInBookmarks(pdfDocument, outline, null);
        }
        return bookmarks;
    }

    private boolean hasBuiltInBookmarks(PDOutlineNode outline) {
        return outline != null && outline.hasChildren();
    }

    private List<PDFBookmark> extractBuiltInBookmarks(PDFDocument document, PDOutlineNode node, PDFBookmark parent)
            throws IOException {
        List<PDFBookmark> bookmarks = new ArrayList<>();
        PDOutlineItem currentItem = node.getFirstChild();
        while (currentItem != null) {
            String title = currentItem.getTitle();
            if (title != null && !title.isEmpty()) {
                int pageNumber = extractPageNumber(currentItem);
                pageNumber = Math.min(Math.max(1, pageNumber), document.getPageCount());
                int level = (parent == null) ? 1 : parent.getLevel() + 1;
                PDFBookmark bookmark = new PDFBookmark(document, title, pageNumber, level, parent);
                bookmarks.add(bookmark);
                if (currentItem.hasChildren()) {
                    List<PDFBookmark> childBookmarks = extractBuiltInBookmarks(document, currentItem, bookmark);
                    bookmarks.addAll(childBookmarks);
                }
            }
            currentItem = currentItem.getNextSibling();
        }
        return bookmarks;
    }

    private int extractPageNumber(PDOutlineItem item) throws IOException {
        PDPageDestination destination = (PDPageDestination) item.getDestination();
        return destination.retrievePageNumber() + 1;
    }
}
