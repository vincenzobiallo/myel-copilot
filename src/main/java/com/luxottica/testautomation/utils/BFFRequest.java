package com.luxottica.testautomation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.security.Context;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.HashMap;
import java.util.Map;

public class BFFRequest {

    private final ObjectMapper mapper;
    private final APIRequestContext context;
    private final User user;
    private Map<String, String> extraHeaders;

    public BFFRequest(Map<String, String> extraHeaders) {
        this.user = Context.getUser();
        this.extraHeaders = extraHeaders;
        this.context = buildContext();
        this.mapper = new ObjectMapper();
    }

    public BFFResponse get(String url) {
        APIResponse response = context.get(url);
        return mapResponse(response);
    }

    public BFFResponse post(String url, RequestOptions options) {
        APIResponse response = context.post(url, options);
        return mapResponse(response);
    }

    private BFFResponse mapResponse(APIResponse response) {
        String body = new String(response.body());
        try {
            return mapper.readValue(body, BFFResponse.class);
        } catch (JsonProcessingException e) {
            return new BFFResponse(response.status(), "KO", body);
        }
    }

    private APIRequestContext buildContext() {

        String sessionId = PlaywrightFactory.getSessionId(user.getUsername());

        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put(Constants.COOKIE, String.format("%s=%s", Constants.COOKIE_SESSION_ID, sessionId));
        APIRequest.NewContextOptions options = new APIRequest.NewContextOptions();
        options.setIgnoreHTTPSErrors(true);
        options.setExtraHTTPHeaders(extraHeaders);
        options.setExtraHTTPHeaders(this.extraHeaders);

        Playwright playwright = Context.getPlaywright();
        APIRequest request = playwright.request();

        return request.newContext(options);
    }

}
