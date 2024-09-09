package com.luxottica.testautomation.components.bucket.enums;

public enum StoresColumn {

    COUNTRY(0),
    STORE_IDENTIFIER(1),
    STORE_CODE(2),
    STORE_SLUG(3),
    LOCALE(4),
    ;

    public final Integer offset;

    StoresColumn(Integer offset) {
        this.offset = offset;
    }
}