package com.luxottica.testautomation.components.bucket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataStoreDTO {

    private String countryName;
    private String storeIdentifier;
    private Integer storeCode;
    private String storeSlug;
    private String locale;
    private Integer langId;

}
