package com.luxottica.testautomation.extensions;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.APIResponseAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.impl.APIResponseAssertionsImpl;
import com.microsoft.playwright.impl.AssertionsTimeout;
import com.microsoft.playwright.impl.PageAssertionsImpl;

public interface MyPlaywrightAssertions {

    static APIResponseAssertions assertThat(APIResponse response) {
        return new APIResponseAssertionsImpl(response);
    }

    static MyLocatorAssertions assertThat(Locator locator) {
        return new MyLocatorAssertionsImpl(locator);
    }

    static PageAssertions assertThat(Page page) {
        return new PageAssertionsImpl(page);
    }

    static void setDefaultAssertionTimeout(double timeout) {
        AssertionsTimeout.setDefaultTimeout(timeout);
    }
}
