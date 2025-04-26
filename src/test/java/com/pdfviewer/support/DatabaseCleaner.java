package com.pdfviewer.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    private static final String FOREIGN_KEY_CHECK_OFF = "SET REFERENTIAL_INTEGRITY FALSE";
    private static final String FOREIGN_KEY_CHECK_ON = "SET REFERENTIAL_INTEGRITY TRUE";
    private static final String TRUNCATE_TABLE_FORMAT = "TRUNCATE TABLE %s";
    private static final String ALTER_TABLE_AUTO_INCREMENT_FORMAT = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH %d";

    private List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    void findTablesNames() {
        this.tableNames = entityManager.getMetamodel()
                .getEntities().stream()
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                .map(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    Table tableAnnotation = javaType.getAnnotation(Table.class);
                    if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                        return tableAnnotation.name();
                    }
                    return entityType.getName();
                })
                .toList();
    }


    @Transactional
    public void cleanUp() {
        entityManager.createNativeQuery(FOREIGN_KEY_CHECK_OFF).executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_TABLE_FORMAT, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(ALTER_TABLE_AUTO_INCREMENT_FORMAT, tableName, 1))
                    .executeUpdate();
        }
        entityManager.createNativeQuery(FOREIGN_KEY_CHECK_ON).executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }
}
