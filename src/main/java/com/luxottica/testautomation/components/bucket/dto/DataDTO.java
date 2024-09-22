package com.luxottica.testautomation.components.bucket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO {

    private List<DataTestDTO> tests;
    private List<DataStoreDTO> stores;

}
