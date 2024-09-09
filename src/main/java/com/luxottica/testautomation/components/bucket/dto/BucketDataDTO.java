package com.luxottica.testautomation.components.bucket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketDataDTO {

    private String id;
    private String weekId;
    private String coreId;
    private String description;
    private Boolean impersonificate;
    private String user;
    private String store;
}
