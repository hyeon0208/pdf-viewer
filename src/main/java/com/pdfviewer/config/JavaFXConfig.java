package com.pdfviewer.config;

import java.net.URL;
import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JavaFXConfig {

    @Bean
    public FXMLLoader fxmlLoader() {
        URL fxmlUrl = getClass().getResource("/fxml/main.fxml");
        return new FXMLLoader(fxmlUrl);
    }
}
