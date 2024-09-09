package com.luxottica.testautomation.components.bucket.config;

import com.luxottica.testautomation.components.bucket.BucketComponent;
import com.luxottica.testautomation.components.bucket.BucketComponentImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfig {

    @Bean
    public BucketComponent bucketComponent() {
        return new BucketComponentImpl();
    }
}
