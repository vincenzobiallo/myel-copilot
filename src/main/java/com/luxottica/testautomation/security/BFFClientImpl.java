package com.luxottica.testautomation.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.security.dto.BFFResponse;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BFFClientImpl implements BFFClient {

    private final String baseUrl;
    private APIRequestContext context;

    private static final Logger logger = LoggerFactory.getLogger(BFFClient.class);
    private static final Map<String, String> EMPTY_MAP_HEADERS = new HashMap<>();
    private static final MultiValueMap<String, String> EMPTY_MAP_PARAMS = new LinkedMultiValueMap<>();

    public BFFClientImpl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method) {
        logger.debug("Executing {} request to {}", method, uri);
        return setRequest(uri, method, EMPTY_MAP_HEADERS, EMPTY_MAP_PARAMS, null);
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers) {
        logger.debug("Executing {} request to {} with headers {}", method, uri, headers);
        return setRequest(uri, method, headers, EMPTY_MAP_PARAMS, null);
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers, final MultiValueMap<String, String> params) {
        logger.debug("Executing {} request to {} with headers {} and params {}", method , uri, headers, params);
        return setRequest(uri, method, headers, params, null);
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers, final Object body) {
        logger.debug("Executing {} request to {} with headers {} and body {}", method, uri, headers, body);
        return setRequest(uri, method, headers, EMPTY_MAP_PARAMS, body);
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method, final MultiValueMap<String, String> params) {
        logger.debug("Executing {} request to {} with params {}", method, uri, params);
        return setRequest(uri, method, EMPTY_MAP_HEADERS, params, null);
    }

    public BFFResponse setRequest(final String uri, final HttpMethod method, final Object body) {
        logger.debug("Executing {} request to {} with body {}", method, uri, body);
        return setRequest(uri, method, EMPTY_MAP_HEADERS, EMPTY_MAP_PARAMS, body);
    }

    public BFFResponse setRequest(String uri, final HttpMethod method, final Map<String, String> headers, final MultiValueMap<String, String> params, final Object body) {

        RequestOptions requestOptions = buildRequestOptions(headers, body);

        if (Objects.isNull(context)) {
            context = buildContext();
        }

        APIResponse response;
        if (!params.isEmpty()) {
            // append multi-value params to the uri
            uri = uri.concat("?").concat(
                    params.entrySet().stream()
                            .map(entry -> entry.getValue().stream()
                                    .map(value -> entry.getKey() + "=" + value)
                                    .reduce((a, b) -> a + "&" + b)
                                    .orElse(""))
                            .reduce((a, b) -> a + "&" + b)
                            .orElse("")
            );
        }
        if (method.equals(HttpMethod.GET)) {
            response = context.get(uri, requestOptions);
        } else if (method.equals(HttpMethod.POST)) {
            response = context.post(uri, requestOptions);
        } else if (method.equals(HttpMethod.PUT)) {
            response = context.put(uri, requestOptions);
        } else if (method.equals(HttpMethod.DELETE)) {
            response = context.delete(uri, requestOptions);
        } else if (method.equals(HttpMethod.PATCH)) {
            response = context.patch(uri, requestOptions);
        } else {
            throw new IllegalArgumentException("Method not supported");
        }

        return mapResponse(response);
    }

    protected RequestOptions buildRequestOptions(final Map<String, String> headers, final Object body) {
        RequestOptions requestOptions = RequestOptions.create();
        requestOptions.setTimeout(30000);

        if (headers != null) {
            headers.forEach(requestOptions::setHeader);
        }

        if (body != null) {
            requestOptions.setData(body);
        }

        return requestOptions;
    }

    public BFFResponse toBFFResponse(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        String body = new String(response.body());
        try {
            return mapper.readValue(body, BFFResponse.class);
        } catch (JsonProcessingException e) {
            return new BFFResponse(response.status(), Strings.EMPTY, body);
        }
    }

    protected BFFResponse mapResponse(APIResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        String body = new String(response.body());
        try {
            return mapper.readValue(body, BFFResponse.class);
        } catch (JsonProcessingException e) {
            return new BFFResponse(response.status(), Strings.EMPTY, body);
        }
    }

    protected APIRequestContext buildContext() {

        User user = Context.getUser();
        String sessionId = PlaywrightFactory.getSessionId(user.getUsername());

        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put(Constants.COOKIE, String.format("%s=%s", Constants.COOKIE_SESSION_ID, sessionId));
        APIRequest.NewContextOptions options = new APIRequest.NewContextOptions();
        options.setBaseURL(baseUrl);
        options.setIgnoreHTTPSErrors(true);
        options.setExtraHTTPHeaders(extraHeaders);

        Playwright playwright = Context.getPlaywright();
        APIRequest request = playwright.request();

        return request.newContext(options);
    }

}
