package com.luxottica.testautomation.components.report.enums;

public enum ReportColumn {

    ID(0),
    MACRO_AREA(1),
    AREA(2),
    SUMMARY(3),
    PRECONDITIONS(4),
    STEP(5),
    ACTIONS(6),
    EXPECTED_RESULTS(7),
    EXECUTION_TIME(8),
    STATUS(9),
    NOTE(10),
    DOOR(11),
    JIRA_ID(12),
    JIRA_STATUS(13),
    UNIQUE_ID(14),
    ;

    public final Integer offset;

    ReportColumn(Integer offset) {
        this.offset = offset;
    }
}