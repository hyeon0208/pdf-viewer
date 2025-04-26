package com.pdfviewer.command;

import com.pdfviewer.dto.PDFBookmarkDTO;
import com.pdfviewer.entity.PDFDocument;
import com.pdfviewer.service.PDFFileService;
import com.pdfviewer.view.PDFViewerState;
import java.util.List;
import javafx.scene.control.TreeItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoadBookmarksCommand implements PDFCommand {

    private final PDFViewerState viewerState;
    private final PDFFileService fileService;

    @Override
    public void execute() {
        try {
            TreeItem<PDFBookmarkDTO> root = new TreeItem<>();
            PDFDocument document = viewerState.getCurrentDocument().get();
            List<PDFBookmarkDTO> bookmarks = fileService.findAllBookmarks(document);
            if (bookmarks.isEmpty()) {
                viewerState.setStatusMessage("This PDF has no bookmarks");
            } else {
                buildBookmarksTree(root, bookmarks);
                viewerState.setStatusMessage("Bookmarks loaded : " + bookmarks.size());
            }
            viewerState.setBookmarksRoot(root);
        } catch (Exception exception) {
            log.error("Error loading bookmarks", exception);
            viewerState.setStatusMessage("Error loading bookmarks : " + exception.getMessage());
        }
    }

    private void buildBookmarksTree(TreeItem<PDFBookmarkDTO> rootItem, List<PDFBookmarkDTO> bookmarks) {
        for (PDFBookmarkDTO bookmark : bookmarks) {
            rootItem.getChildren().add(createBookmarkTreeItemFromHierarchy(bookmark));
        }
    }

    private TreeItem<PDFBookmarkDTO> createBookmarkTreeItemFromHierarchy(PDFBookmarkDTO bookmark) {
        TreeItem<PDFBookmarkDTO> parentBookmark = new TreeItem<>(bookmark);
        for (PDFBookmarkDTO child : bookmark.children()) {
            parentBookmark.getChildren().add(createBookmarkTreeItemFromHierarchy(child));
        }
        return parentBookmark;
    }
}
