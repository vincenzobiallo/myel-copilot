package com.luxottica.testautomation.components.labels;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luxottica.testautomation.components.labels.dto.LabelResponseDTO;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.models.User;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class LabelUtils {

    private final Map<MyelStore, Map<String, String>> labels = new HashMap<>();

    @Autowired
    private Config config;

    public String getLabel(User user, String key) {

        String locale = user.getLocale();
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

    private void loadLanguage(String locale) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");
            headers.add(config.getLabelsHeaderKey(), config.getLabelsHeaderValue());

            HttpEntity<LabelResponseDTO> entity = new HttpEntity<>(headers);

            String url = config.getLabelsEndpoint().replace("{locale}", locale.replace("-", "_"));
            LabelResponseDTO response = restTemplate.exchange(url, HttpMethod.GET, entity, LabelResponseDTO.class).getBody();

            if (response == null) {
                throw new RuntimeException("Failed to fetch labels!");
            }

            if (response.getData() == null) {
                throw new RuntimeException("No data found in response!");
            }

            labels.put(MyelStore.fromLocale(locale), response.getData());
            log.info("Fetched {} labels for locale {}", response.getData().size(), locale);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch labels", e);
        }
    }
}
