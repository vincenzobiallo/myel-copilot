package com.luxottica.testautomation.components.report;

import com.luxottica.testautomation.components.report.models.TestCase;

import java.io.File;
import java.util.Map;

public interface ReportComponent {

    String TEMPLATE_CORE = "playwright/report/template_core.xlsx";

    Map<String, TestCase> getTests();
    File generateReport();
}
