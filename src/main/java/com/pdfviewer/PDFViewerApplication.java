package com.pdfviewer;

import com.pdfviewer.config.StageInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class PDFViewerApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        log.info("Start Application");
        Application.launch(args);
    }

    @Override
    public void init() {
        log.info("Init Spring Context");
        springContext = SpringApplication.run(PDFViewerApplication.class);
        log.info("Init Complete Spring Context");
    }

    @Override
    public void start(Stage stage) {
        log.info("Init JavaFX");
        try {
            StageInitializer initializer = springContext.getBean(StageInitializer.class);
            initializer.initialize(stage);
            log.info("Init Complete JavaFX");
        } catch (Exception exception) {
            log.error("Error occurred while running JavaFX.", exception);
            stop();
        }
    }

    @Override
    public void stop() {
        Platform.exit();
        springContext.close();
        log.info("Close Application");
    }
}
