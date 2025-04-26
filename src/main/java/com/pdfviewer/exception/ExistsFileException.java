package com.pdfviewer.exception;

public class ExistsFileException extends RuntimeException {

    public ExistsFileException(String message) {
        super(message);
    }

    public ExistsFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
