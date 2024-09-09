package com.luxottica.testautomation.components.bucket;

import com.luxottica.testautomation.components.bucket.dto.BucketDataDTO;

public interface BucketComponent {

    String MAPPER = "playwright/report/test-mapping.xlsx";
    String SHEET_MAPPING = "Mapping";
    String SHEET_STORES = "Stores";
    String Y = "Y";

    BucketDataDTO getBucketData(String internalTestId);
    String getBusinessTestIdFromInternal(String internalTestId);
}
