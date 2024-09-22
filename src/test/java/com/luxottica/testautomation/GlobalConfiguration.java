package com.luxottica.testautomation;

import com.luxottica.testautomation.components.report.ReportComponent;
import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;

import java.io.IOException;

@Slf4j
public class GlobalConfiguration {

    @AfterSuite
    void teardown() throws IOException {
        log.info("Tearing down...");
        ReportComponent report = InjectionUtil.getBean(ReportComponent.class);
        report.generateReport();
    }

    private void getAllTests() {
        // get all tests frrom TestNG

    }
}
