package com.luxottica.testautomation.components.bucket.enums;

public enum BucketColumn {

    ID(0),
    WEEK_ID(1),
    CORE_ID(2),
    DESCRIPTION(3),
    IMPERSONIFICATE(4),
    USER(5),
    STORE(6),
    ;

    public final Integer offset;

    BucketColumn(Integer offset) {
        this.offset = offset;
    }
}