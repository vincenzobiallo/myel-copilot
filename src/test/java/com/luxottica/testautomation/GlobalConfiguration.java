package com.luxottica.testautomation;

import com.luxottica.testautomation.components.report.ReportComponentImpl;
import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;

import java.io.IOException;

@Slf4j
public class GlobalConfiguration {

    @AfterSuite
    void teardown() throws IOException {
        log.info("Tearing down...");
        ReportComponentImpl report = InjectionUtil.getBean(ReportComponentImpl.class);
        report.generateReport();
    }
}
