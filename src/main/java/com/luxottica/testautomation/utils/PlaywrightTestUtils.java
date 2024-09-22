package com.luxottica.testautomation.utils;

import com.google.gson.JsonElement;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.components.report.ReportComponent;
import com.luxottica.testautomation.components.report.enums.ReportType;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.components.report.models.TestCase;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static com.luxottica.testautomation.constants.Label.COOKIE_POLICY_BANNER_REFUSE;

public class PlaywrightTestUtils {

    public static boolean anyStepFailed(String testId) {
        ReportComponent report = InjectionUtil.getBean(ReportComponent.class);
        TestCase test = Objects.requireNonNull(report.getTests().get(testId), String.format("Test %s not found!", testId));
        return test.getSteps().stream().anyMatch(step -> step.getStatus().equals(TestStatus.FAILED));
    }

    public static BrowserType.LaunchOptions getPlaywrightLaunchOptions() {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
        options.setHeadless(Boolean.parseBoolean(System.getProperty("playwright.browser.headless")));
        options.setTimeout(Double.parseDouble(System.getProperty("playwright.defaultTimeout")));

        return options;
    }

    public static ReportType getReportType() {
        Optional<String> input = Optional.ofNullable(System.getProperty("playwright.reportType"));
        return input.map(ReportType::valueOf).orElseGet(() -> ReportType.valueOf("CORE"));
    }

    public static boolean containClass(Locator locator, String clazz) {
        Set<String> classes = Set.of(locator.getAttribute("class").toLowerCase().split(" "));
        return classes.contains(clazz.toLowerCase());
    }

    public static boolean containClass(Locator locator, List<String> clazz) {
        Set<String> classes = Set.of(locator.getAttribute("class").toLowerCase().split(" "));

        return classes.containsAll(clazz);
    }

    public static Page.ScreenshotOptions getScreenshotOptions(String name) {
        return new Page.ScreenshotOptions().setPath(Paths.get("screenshots", name + ".png"));
    }

    public static void closeAllPopups(Page page, @Nullable Boolean closeCookieBanner) {
        page.waitForURL(page.url(), new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Close popup "Remind me later"
        Locator remindMeLater = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remind me later"));
        if (remindMeLater.isVisible()) {
            remindMeLater.click();
        }

        // Close Overlay_TutorialPills
        Locator overlayTutorialPills = page.locator("div[data-element-id='Overlay_TutorialPills']");
        if (overlayTutorialPills.isVisible()) {
            page.locator("button[data-element-id='Overlay_TutorialPills_Close']").click();
        }

        LabelComponentImpl labelComponent = InjectionUtil.getBean(LabelComponentImpl.class);
        String refuseCookieLabel = labelComponent.getLabel(COOKIE_POLICY_BANNER_REFUSE);
        Locator refuseCookie = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(refuseCookieLabel));
        if (closeCookieBanner == Boolean.TRUE && refuseCookie.isVisible()) {
            refuseCookie.click();
        }
    }

    public static MultiValueMap<String, String> getQueryMap(String url) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        String[] parts = url.split("\\?");
        if (parts.length > 1) {
            String query = parts[1];
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = pair[0];
                String value = pair.length > 1 ? pair[1] : "";
                queryParams.add(key, value);
            }
        }

        return queryParams;
    }
}
