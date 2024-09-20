package com.luxottica.testautomation.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.exceptions.BackOfficeUserException;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.luxottica.testautomation.utils.RequestUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.luxottica.testautomation.authentication.PlaywrightFactory.areCookiesPresent;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String username;
    private String password;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Map<String, String> attributes = new LinkedHashMap<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private boolean isBackOfficeUser = false;

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void deleteAttribute(String key) {
        attributes.remove(key);
    }

    public String getURL() {
        return attributes.getOrDefault(Constants.USER_ATTRIBUTES.URL, Strings.EMPTY);
    }

    public String getStore() {
        return attributes.getOrDefault(Constants.USER_ATTRIBUTES.STORE, Strings.EMPTY);
    }

    public String getLocale() {
        return attributes.getOrDefault(Constants.USER_ATTRIBUTES.LOCALE, Strings.EMPTY);
    }

    public String getCurrentImpersonification() {

        if (!isBackOfficeUser) {
            return Strings.EMPTY;
        }

        return attributes.getOrDefault(Constants.USER_ATTRIBUTES.CURRENT_IMPERSONIFICATION, Strings.EMPTY);
    }

    public void executeLogin(Browser browser, String baseUrl) {

        if (areCookiesPresent(username)) {
            throw new RuntimeException("User already logged in");
        }

        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate(baseUrl);
        page.getByLabel("Sign in name").hover();
        page.getByLabel("Sign in name").click();
        page.getByLabel("Sign in name").fill(username);
        page.getByLabel("Continue").hover();
        page.getByLabel("Continue").click();

        if (!isBackOfficeUser) {
            page.getByLabel("Password").click();
            page.getByLabel("Password").fill(password);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
        } else {
            Locator passwordInput = page.locator("input[name='passwd']");
            passwordInput.click();
            passwordInput.fill(password);
            page.locator("input[type='submit']").click();
        }

        // Check if user lands on the homepage (whatever the store is)
        page.waitForURL(String.format("%s/**/**/homepage", baseUrl));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Save storage state into a file
        BrowserContext.StorageStateOptions options = new BrowserContext.StorageStateOptions();
        options.setPath(Paths.get(PlaywrightFactory.getStorageState(username)));
        page.context().storageState(options);

        context.close();
    }


}
