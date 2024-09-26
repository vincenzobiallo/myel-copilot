package com.luxottica.testautomation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MyelStore {

    public static final List<MyelStore> STORES = new ArrayList<>();

    private final String countryName;
    private final String storeIdentifier;
    private final String storeCode;
    private final String storeSlug;
    private final String locale;
    private final String langId;

    public static MyelStore fromStoreIdentifier(String storeIdentifier) {
        return STORES.stream()
                .filter(store -> store.getStoreIdentifier().equals(storeIdentifier))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    public static MyelStore fromLocale(String locale) {
        return STORES.stream()
                .filter(store -> store.getLocale().equalsIgnoreCase(locale))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
