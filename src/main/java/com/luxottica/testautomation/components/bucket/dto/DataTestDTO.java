package com.luxottica.testautomation.components.bucket.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataTestDTO {

    private String id;
    private String weekId;
    private String coreId;
    private String testName;
    private Boolean impersonificate;
    @JsonAlias({"door", "username"})
    private String door;
    private String storeIdentifier;
    private Map<String, Object> additionalData;

}
