package com.luxottica.testautomation.dto;

import com.luxottica.testautomation.models.MyelStore;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultidoorRequestDTO {

    private List<MultdoorRequestDataDTO> doors;

    @Builder
    public static class MultdoorRequestDataDTO {
        private String customer;
        private Boolean selected;
    }
}
