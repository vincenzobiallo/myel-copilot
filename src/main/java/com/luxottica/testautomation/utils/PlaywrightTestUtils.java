package com.luxottica.testautomation.utils;

import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.report.*;
import com.luxottica.testautomation.report.enums.ReportType;
import com.luxottica.testautomation.report.enums.TestStatus;
import com.luxottica.testautomation.report.models.TestCase;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Playwright;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlaywrightTestUtils {

    @Getter
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    @Getter
    private static final ThreadLocal<User> user = new ThreadLocal<>();

    public static boolean anyStepFailed(String testId) {
        Report report = InjectionUtil.getBean(Report.class);
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

    public static boolean containClass(Locator locator, String clazz, String message) {
        List<String> classes = List.of(locator.getAttribute("class").toLowerCase().split(" "));
        return classes.contains(clazz.toLowerCase());
    }
}
