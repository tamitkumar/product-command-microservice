package com.tech.brain.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.brain.exception.ErrorCode;
import com.tech.brain.exception.ErrorSeverity;
import com.tech.brain.exception.PollerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JSONUtils {

    private final ObjectMapper objectMapper;

    public JSONUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String javaToJSON(Object object) {
        log.debug("Converting Java to JSON: {}", object);
        String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonGenerationException jsonEx) {
            log.error("Error Occurred while conversion of object to JSON: {}", jsonEx.getMessage());
            throw new PollerException(ErrorCode.ERR005.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR005.getErrorMessage(), jsonEx);
        } catch (JsonMappingException jsonEx) {
            log.error("Error Occurred while mapping of object into JSON: {}", jsonEx.getMessage());
            throw new PollerException(ErrorCode.ERR005.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR005.getErrorMessage(), jsonEx);
        } catch (IOException ioEx) {
            log.error("Error Occurred while reading of object: {}", ioEx.getMessage());
            throw new PollerException(ErrorCode.ERR005.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR005.getErrorMessage(), ioEx);
        }
        return json;
    }

    public <T> T jsonToJava(String json, Class<T> clazz) {
        log.info("Converting JSON to Java: {}", json);
        T result;
        try {
            result = objectMapper.readValue(json, clazz);
        } catch (JsonParseException jsonEx) {
            log.error("Error Occurred while parsing json: {}", jsonEx.getMessage());
            throw new PollerException(ErrorCode.ERR006.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR006.getErrorMessage(), jsonEx);
        } catch (JsonMappingException jsonEx) {
            log.error("Error Occurred while mapping of json: {}", jsonEx.getMessage());
            throw new PollerException(ErrorCode.ERR006.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR006.getErrorMessage(), jsonEx);
        } catch (IOException ioEx) {
            log.error("Error Occurred while reading of json: {}", ioEx.getMessage());
            throw new PollerException(ErrorCode.ERR006.getErrorCode(), ErrorSeverity.ERROR,
                    ErrorCode.ERR006.getErrorMessage(), ioEx);
        }
        return result;
    }
}
