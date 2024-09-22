package com.luxottica.testautomation.dto;

import com.luxottica.testautomation.models.MyelStore;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopilotDTO {

    @Getter
    @Setter
    private String door;

    @Getter
    @Setter
    private MyelStore store;

    private Boolean isImpersonate;

    public Boolean isImpersonate() {
        return isImpersonate;
    }

    public void setImpersonate(Boolean impersonificate) {
        this.isImpersonate = impersonificate;
    }
}
