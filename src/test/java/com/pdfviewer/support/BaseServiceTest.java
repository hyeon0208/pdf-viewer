package com.pdfviewer.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class BaseServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void cleanUp() {
        databaseCleaner.cleanUp();
    }
}
