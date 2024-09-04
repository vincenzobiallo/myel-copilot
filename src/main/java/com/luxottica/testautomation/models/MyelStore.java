package com.luxottica.testautomation.models;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum MyelStore {

    ITALY("Italy", "myl-it", "10156", "it", "it-IT"),
    UNITED_STATES("United States", "myl-us", "10152", "us", "en-US")
    ;

    private final String countryName;
    private final String storeIdentifier;
    private final String storeCode;
    private final String storeSlug;
    private final String locale;

    MyelStore(String countryName, String storeIdentifier, String storeCode, String storeSlug, String locale) {
        this.countryName = countryName;
        this.storeIdentifier = storeIdentifier;
        this.storeCode = storeCode;
        this.storeSlug = storeSlug;
        this.locale = locale;
    }

    public static MyelStore fromStoreIdentifier(String storeIdentifier) {
        return Stream.of(MyelStore.values())
            .filter(store -> store.getStoreIdentifier().equals(storeIdentifier))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    public static MyelStore fromLocale(String locale) {
        return Stream.of(MyelStore.values())
            .filter(store -> store.getLocale().equalsIgnoreCase(locale))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

}
