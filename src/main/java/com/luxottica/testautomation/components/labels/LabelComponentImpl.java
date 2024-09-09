package com.luxottica.testautomation.components.labels;

import com.luxottica.testautomation.components.labels.config.LabelConfig;
import com.luxottica.testautomation.components.labels.dto.LabelResponseDTO;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.security.Context;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class LabelComponentImpl implements LabelComponent {

    private static final Logger logger = LoggerFactory.getLogger(LabelComponent.class);
    private final Map<MyelStore, Map<String, String>> labels = new HashMap<>();

    @Autowired
    private LabelConfig config;

    private void loadLanguage(String locale) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");
            headers.add(config.getHeader().getKey(), config.getHeader().getValue());

            HttpEntity<LabelResponseDTO> entity = new HttpEntity<>(headers);

            String url = config.getEndpoint().replace("{locale}", locale.replace("-", "_"));
            LabelResponseDTO response = restTemplate.exchange(url, HttpMethod.GET, entity, LabelResponseDTO.class).getBody();

            if (response == null) {
                throw new RuntimeException("Failed to fetch labels!");
            }

            if (response.getData() == null) {
                throw new RuntimeException("No data found in response!");
            }

            labels.put(MyelStore.fromLocale(locale), response.getData());
            logger.debug("Fetched {} labels for locale {}", response.getData().size(), locale);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch labels", e);
        }
    }

    @Override
    public String getLabel(String key) {

        String locale = Context.getUser().getLocale();
        if (!labels.containsKey(MyelStore.fromLocale(locale))) {
            loadLanguage(locale);
        }

        Map<String, String> labels = this.labels.get(MyelStore.fromLocale(locale));

        if (!labels.containsKey(key)) {
            throw new RuntimeException("Label not found: " + key);
        }

        // Removing placeholder(s) from the label
        String label = labels.get(key);
        if (label.contains("{")) {
            label = label.substring(0, label.indexOf('{'));
        }

        return label;
    }

}
