package com.luxottica.testautomation.components.bucket;

import com.luxottica.testautomation.components.bucket.dto.DataDTO;
import com.luxottica.testautomation.components.bucket.dto.DataTestDTO;

public interface BucketComponent {

    String DATA_FILE = "playwright/report/data.json";

    DataTestDTO getBucketData(String internalTestId);
    String getBusinessTestIdFromInternal(String internalTestId);
}
