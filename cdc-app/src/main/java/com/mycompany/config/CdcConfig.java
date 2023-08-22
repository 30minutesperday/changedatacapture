package com.mycompany.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class CdcConfig {

    @Value("${inventory.datasource.host}")
    private String inventoryDbHost;
    @Value("${inventory.datasource.port}")
    private String inventoryDbPort;
    @Value("${inventory.datasource.username}")
    private String inventoryDbUsername;
    @Value("${inventory.datasource.password}")
    private String inventoryDbPassword;
    @Value("${inventory.datasource.database}")
    private String inventoryDbName;

    @Bean
    public io.debezium.config.Configuration inventoryConnector() throws Exception {
        File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
        File dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");

        return io.debezium.config.Configuration.create()
                .with("name", "inventory-postgresql-connector")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                .with("offset.flush.interval.ms", "60000")
                .with("database.hostname", inventoryDbHost)
                .with("database.port", inventoryDbPort)
                .with("database.user", inventoryDbUsername)
                .with("database.password", inventoryDbPassword)
                .with("database.dbname", inventoryDbName)
//                .with("database.include.list", inventoryDbName)
                .with("include.schema.changes", "false")
                .with("database.allowPublicKeyRetrieval", "true")
                .with("database.server.id", "10181")
                .with("database.server.name", "inventory-postgresql-server")
                .with("topic.prefix", "product_topic")
                .with("plugin.name", "pgoutput")
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", dbHistoryTempFile.getAbsolutePath())
                .build();
    }
}
