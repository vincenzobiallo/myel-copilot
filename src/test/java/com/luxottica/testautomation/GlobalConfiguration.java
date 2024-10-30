package com.luxottica.testautomation;

import com.luxottica.testautomation.components.report.ReportComponent;
import com.luxottica.testautomation.components.sendgrid.SendgridService;
import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class GlobalConfiguration {

    @AfterSuite
    void teardown() throws IOException {
        log.info("Tearing down...");
        ReportComponent reportComponent = InjectionUtil.getBean(ReportComponent.class);
        File report = reportComponent.generateReport();

        SendgridService sendgridService = InjectionUtil.getBean(SendgridService.class);
        File[] attachments = getScreenshot();
        sendgridService.sendMail(report, attachments);
        Arrays.stream(attachments).forEach(File::delete);
    }

    private File[] getScreenshot() {
        File dir = new File("screenshots");
        return dir.listFiles();
    }

}
