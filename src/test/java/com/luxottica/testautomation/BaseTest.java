package com.luxottica.testautomation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.authentication.UserUtils;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.exceptions.TestFirmException;
import com.luxottica.testautomation.exceptions.UserAttributeException;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.report.*;
import com.luxottica.testautomation.report.enums.TestStatus;
import com.luxottica.testautomation.report.models.TestCase;
import com.luxottica.testautomation.report.models.TestStep;
import com.luxottica.testautomation.report.BusinessBucket;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.components.labels.LabelUtils;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter(AccessLevel.PROTECTED)
@SpringBootTest(classes = TestAutomationApplication.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Browser browser;
    private BrowserContext context;
    protected Page page;

    private User user;

    @Autowired
    protected LabelUtils labelUtils;

    @BeforeClass
    void launchBrowser() {
        PlaywrightTestUtils.getPlaywright().set(Playwright.create());
        this.browser = PlaywrightTestUtils.getPlaywright().get().chromium().launch(PlaywrightTestUtils.getPlaywrightLaunchOptions());
    }

    @AfterClass
    void closeBrowser() {
        PlaywrightTestUtils.getPlaywright().get().close();
    }

    private Annotation getAnnotation(Method method) {
        if (method.isAnnotationPresent(Impersonificate.class)) {
            return method.getAnnotation(Impersonificate.class);
        }

        return null;
    }

    @BeforeMethod
    void createContextAndPage(Method method) {

        Annotation annotation = getAnnotation(method);

        if (annotation instanceof Impersonificate) {
            PlaywrightTestUtils.getUser().set(UserUtils.getUserByWorker(browser));
        /*} else if (annotation instanceof User) {
            String username = method.getDeclaringClass().getAnnotation(UserClass.class).username();
            user = UserUtils.getUserByUsername(browser, username);*/
        } else {
            throw new TestFirmException("You must specify the type of the class with @BackOfficeClass or @UserClass annotation !");
        }

        this.user = PlaywrightTestUtils.getUser().get();
        getURL(); // Precarico l'URL dell'utente

        Browser.NewContextOptions options = new Browser.NewContextOptions();
        options.setStorageStatePath(Paths.get(PlaywrightFactory.getStorageState(user.getUsername())));
        context = browser.newContext(options);
        page = context.newPage();
        page.onDOMContentLoaded(p -> {
            p.waitForURL(page.url(), new Page.WaitForURLOptions().setTimeout(10000));
            p.waitForLoadState(LoadState.NETWORKIDLE);
            if (p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remind me later")).isVisible()) {
                p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remind me later")).click();
            }
            // Close popup if visible
            if (p.locator(".popup").isVisible()) {
                p.locator("icon-container").click();
            }

            if (p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rifiuta")).isVisible()) {
                p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rifiuta")).click();
            }
        });

        if (user.isBackOfficeUser() && method.isAnnotationPresent(Impersonificate.class)) {
            Impersonificate impersonification = method.getAnnotation(Impersonificate.class);
            String userToImpersonificate = impersonification.door();

            if (!Objects.equals(user.getCurrentImpersonification(), userToImpersonificate)) {
                user.vanish(impersonification);
            }
        }
    }

    @AfterMethod
    void closeContext(Method method) {

        Test test = method.getAnnotation(Test.class);
        String testId = getBusinessTestId(test);
        String testDescription = test.description();

        if (PlaywrightTestUtils.anyStepFailed(testId)) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshots", testDescription + ".png")));
        }

        context.close();
    }

    protected void executeStep(Integer number, String testId, TestStepFunction function) {

        Report report = InjectionUtil.getBean(Report.class);
        TestCase test = report.getTests().get(testId);
        TestStep step = test.getStep(number);

        if (test.isFailed()) {
            logger.debug("Test {} is already failed! Skipping step {}..", test.getId(), number);
            return;
        }

        try {
            logger.info("Executing step {} for test {}", number, test.getId());
            TestStatus result = function.apply();
            step.setStatus(result);
            logger.info("Step {} for test {} executed with status {}", number, test.getId(), result);
        } catch (TimeoutError e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote("Timeout! Is the step waiting for an element that is not present?");
            logger.warn("Timeout executing step {} for test {}!", number, test.getId());
            test.setFailed(Boolean.TRUE);
        } catch (AssertionError e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote(e.getMessage());
            logger.error("Assertion error executing step {} for test {}! {}", number, test.getId(), e.getMessage());
            test.setFailed(Boolean.TRUE);
        } catch (SkipException skipException) {
            logger.info(skipException.getMessage());
            throw skipException;
        } catch (Exception e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote(e.getMessage());
            logger.error("Error executing step {} for test {}! {}", number, test.getId(), e.getMessage());
            test.setFailed(Boolean.TRUE);
        }
    }

    protected String getURL() {

        if (Objects.isNull(user.getAttribute(Constants.USER_ATTRIBUTES.URL)) || user.getAttribute(Constants.USER_ATTRIBUTES.URL).isEmpty()){

            Config config = InjectionUtil.getBean(Config.class);
            String url = config.getBaseUrl().concat(config.getSessionUrl());

            APIRequest.NewContextOptions options = new APIRequest.NewContextOptions();
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Cookie", String.format("%s=%s", Constants.COOKIE_SESSION_ID, PlaywrightFactory.getSessionId(user.getUsername())));
            options.setExtraHTTPHeaders(extraHeaders);

            APIRequest request = PlaywrightTestUtils.getPlaywright().get().request();
            APIRequestContext context = request.newContext(options);

            // Effettua la richiesta GET all'URL
            APIResponse response = context.get(url);
            JsonObject responseBody = JsonParser.parseString(new String(response.body())).getAsJsonObject();
            JsonObject attributes = responseBody.getAsJsonObject("attributes");

            if (attributes.isEmpty()) {
                throw new UserAttributeException("User attributes not found in the response");
            }

            JsonObject data = attributes.getAsJsonObject("user");
            String storeIdentifier = data.get("storeIdentifier").getAsString();
            String locale = MyelStore.fromStoreIdentifier(storeIdentifier).getLocale();
            String door = data
                    .getAsJsonObject("userContext")
                    .getAsJsonArray("multiDoors")
                    .get(0)
                    .getAsJsonObject()
                    .get("orgentityName")
                    .getAsString();

            String userUrl = String.format("%s/%s/%s", config.getBaseUrl(), storeIdentifier, locale);
            this.user.setAttribute(Constants.USER_ATTRIBUTES.URL, userUrl);
            this.user.setAttribute(Constants.USER_ATTRIBUTES.STORE, storeIdentifier);
            this.user.setAttribute(Constants.USER_ATTRIBUTES.LOCALE, locale);
            this.user.setAttribute(Constants.USER_ATTRIBUTES.CURRENT_IMPERSONIFICATION, door);
        }

        return user.getURL();
    }

    private String getBusinessTestId(Test test) {

        BusinessBucket bucket = InjectionUtil.getBean(BusinessBucket.class);
        String businessId = bucket.getBusinessId(test.testName());

        if (businessId == null || businessId.isEmpty()) {
            throw new SkipException(String.format("Test %s is disabled in %s test execution. Skipping..", test.testName(), PlaywrightTestUtils.getReportType().name()));
        }

        return businessId;
    }

    protected String initTestAndReturnId(Method method) {
        Test test = method.getAnnotation(Test.class);
        Report report = InjectionUtil.getBean(Report.class);
        String testId = getBusinessTestId(test);
        TestCase testCase = report.getTests().get(testId);
        testCase.setExecutor(method.getAnnotation(Impersonificate.class).door());
        testCase.setInternalId(test.testName());

        return testId;
    }
}
