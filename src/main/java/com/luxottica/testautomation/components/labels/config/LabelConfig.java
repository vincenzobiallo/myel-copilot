package com.luxottica.testautomation.components.labels.config;

import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "labels")
public class LabelConfig {

    private String endpoint;
    private Header header;

    @Bean
    public LabelComponent labelComponent() {
        return new LabelComponentImpl();
    }

    @Data
    public static class Header {
        private String key;
        private String value;
    }
}
