package com.pdfviewer.config;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StageInitializer {

    private final ApplicationContext context;
    private final FXMLLoader fxmlLoader;

    public void initialize(Stage stage) {
        try {
            log.info("Init Start JavaFX Main UI");
            fxmlLoader.setControllerFactory(context::getBean); // Inject Fxml View Controller into The FXMLLoader
            Parent root = fxmlLoader.load();
            setupStage(stage, root);
            log.info("Init Complete JavaFX Main UI");
        } catch (IOException exception) {
            throw new RuntimeException("Error occurred while initializing UI ", exception);
        }
    }

    private void setupStage(Stage stage, Parent root) {
        Scene scene = new Scene(root, 2000, 1200);
        stage.setScene(scene);
        stage.setTitle("PDF Viewer");
        stage.setMinWidth(1000);
        stage.setMinHeight(800);
        stage.show();
    }
}
