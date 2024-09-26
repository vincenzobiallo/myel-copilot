package com.luxottica.testautomation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public JsonObject toJsonObject() {
        String dataString = stringifyData();
        return JsonParser.parseString(dataString).getAsJsonObject();
    }

    public JsonArray toJsonArray() {
        String dataString = stringifyData();
        return JsonParser.parseString(dataString).getAsJsonArray();
    }

}
