package com.mycompany.listener;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.transform.Source;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class CdcListener {

    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final Executor executor;

    public CdcListener(Configuration inventoryConnector) {
        this.executor = Executors.newSingleThreadExecutor();

        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(inventoryConnector.asProperties())
                .notifying(this::handleChangeEvent).build();

    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> recordChangeEvent) {

        SourceRecord sourceRecord = recordChangeEvent.record();

        log.info("Key = '"+sourceRecord.key()+"' value = '"+sourceRecord.value()+"'");

    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws Exception {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }

}
