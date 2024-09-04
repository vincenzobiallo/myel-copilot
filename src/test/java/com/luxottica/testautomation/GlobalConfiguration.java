package com.luxottica.testautomation;

import com.luxottica.testautomation.report.Report;
import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;

import java.io.IOException;

@Slf4j
public class GlobalConfiguration {

    @AfterSuite
    void teardown() throws IOException {
        log.info("Tearing down...");
        Report report = InjectionUtil.getBean(Report.class);
        report.generateReport();
    }
}
