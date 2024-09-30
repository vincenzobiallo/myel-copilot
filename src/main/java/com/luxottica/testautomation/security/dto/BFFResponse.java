package com.luxottica.testautomation.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class BFFResponse {

    private Integer status;
    private String message;
    private Object data;

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(status);
    }

    private String stringifyData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return Strings.EMPTY;
        }
    }

    public <T> T getData(Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return mapper.convertValue(data, clazz);
    }

    public JsonObject toJsonObject() {
        String dataString = stringifyData();
        return JsonParser.parseString(dataString).getAsJsonObject();
    }

    public JsonArray toJsonArray() {
        String dataString = stringifyData();
        return JsonParser.parseString(dataString).getAsJsonArray();
    }

}
