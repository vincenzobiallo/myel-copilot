package com.luxottica.testautomation.components.report.config;

import com.luxottica.testautomation.components.report.ReportComponent;
import com.luxottica.testautomation.components.report.ReportComponentImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {

    @Bean
    public ReportComponent reportComponent() {
        return new ReportComponentImpl();
    }
}
