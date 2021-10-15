package com.mapr.mgrweb.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Mapper {

    private static ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    public static <T> T read(String json, Class<T> theClass) {
        try {
            return objectMapper.readValue(json, theClass);
        } catch (JsonProcessingException e) {
            log.debug("Error converting to Object", e);
            return null;
        }
    }

    public static String write(Object elem) {
        try {
            return objectMapper.writeValueAsString(elem);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
