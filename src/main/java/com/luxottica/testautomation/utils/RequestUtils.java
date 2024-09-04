package com.luxottica.testautomation.utils;

import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.constants.Constants;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    public static APIRequestContext buildContext(Playwright playwright, String username) {
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Cookie", String.format("%s=%s", Constants.COOKIE_SESSION_ID, PlaywrightFactory.getSessionId(username)));
        APIRequest.NewContextOptions options = new APIRequest.NewContextOptions();
        options.setExtraHTTPHeaders(extraHeaders);

        APIRequest request = playwright.request();
        return request.newContext(options);
    }
}
