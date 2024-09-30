package com.luxottica.testautomation.security;

import com.luxottica.testautomation.security.dto.BFFResponse;
import com.microsoft.playwright.Response;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface BFFClient {

    BFFResponse setRequest(final String uri, final HttpMethod method);

    BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers);

    BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers, final MultiValueMap<String, String> params);

    BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers, final Object body);

    BFFResponse setRequest(final String uri, final HttpMethod method, final MultiValueMap<String, String> params);

    BFFResponse setRequest(final String uri, final HttpMethod method, final Object body);

    BFFResponse setRequest(final String uri, final HttpMethod method, final Map<String, String> headers, final MultiValueMap<String, String> params, final Object body);

    BFFResponse toBFFResponse(Response response);

}