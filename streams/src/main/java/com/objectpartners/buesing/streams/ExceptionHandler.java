package com.objectpartners.buesing.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

@Slf4j
public class ExceptionHandler implements DeserializationExceptionHandler {

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext context, ConsumerRecord<byte[], byte[]> record, Exception exception) {
        log.error("deserialization error: topic={}, message={}", record.topic(), exception.getMessage());
        // record.key() record.value() -- to access to raw bytes.
        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }

}
