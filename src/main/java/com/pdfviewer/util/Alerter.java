package com.pdfviewer.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerter {

    private Alerter() {
    }

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    public static void showWarnAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Warning");
        alert.setHeaderText(title);
        alert.showAndWait();
    }
}
