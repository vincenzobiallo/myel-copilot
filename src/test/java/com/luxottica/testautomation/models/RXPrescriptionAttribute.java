package com.luxottica.testautomation.models;

import com.microsoft.playwright.Locator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RXPrescriptionAttribute {
    private String identifier;
    private Locator inputRight;
    private Locator inputLeft;
}
