package com.luxottica.testautomation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.authentication.PlaywrightFactory;
import com.luxottica.testautomation.authentication.UserUtils;
import com.luxottica.testautomation.components.bucket.BucketComponent;
import com.luxottica.testautomation.components.bucket.dto.DataTestDTO;
import com.luxottica.testautomation.components.report.ReportComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.components.report.models.TestCase;
import com.luxottica.testautomation.components.report.models.TestStep;
import com.luxottica.testautomation.components.report.models.TestStepFunction;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.dto.CopilotDTO;
import com.luxottica.testautomation.exceptions.BackOfficeUserException;
import com.luxottica.testautomation.exceptions.MySkipException;
import com.luxottica.testautomation.exceptions.UserAttributeException;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.security.BFFClient;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.luxottica.testautomation.utils.RequestUtils;
import com.microsoft.playwright.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.SkipException;
import org.testng.annotations.*;

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
    private BFFClient bffClient;

    @BeforeClass
    void launchBrowser() {
        Context.setPlaywright(Playwright.create());
        this.browser = Context.getPlaywright().chromium().launch(PlaywrightTestUtils.getPlaywrightLaunchOptions());
    }

    @AfterClass
    void closeBrowser() {
        Context.getPlaywright().close();
    }

    @BeforeMethod
    void createContextAndPage(Method method) {

        Test test = method.getAnnotation(Test.class);
        CopilotDTO pilot = getDoorToUse(method);

        try {
            if (pilot.isImpersonate()) {
                Context.setUser(UserUtils.getUserByWorker(browser));
            } else {
                Context.setUser(UserUtils.getUserByUsername(browser, pilot.getDoor()));
            }
        } catch (Exception e) {
            ReportComponent report = InjectionUtil.getBean(ReportComponent.class);
            TestStep firstStep = report.getTests().get(getBusinessTestId(test)).getStep(1);
            firstStep.setNote("Error while authenticating user: " + pilot.getDoor(), logger);
            firstStep.setStatus(TestStatus.FAILED);
            throw new AssertionError("Error while authenticating user: " + pilot.getDoor(), e);
        }

        this.user = Context.getUser();
        getURL(); // Precarico l'URL dell'utente

        Browser.NewContextOptions options = new Browser.NewContextOptions();
        options.setStorageStatePath(Paths.get(PlaywrightFactory.getStorageState(user.getUsername())));
        context = browser.newContext(options);
        page = context.newPage();
        page.onDOMContentLoaded(p -> PlaywrightTestUtils.closeAllPopups(p, !Objects.equals(test.testName(), "AT001")));

        if (user.isBackOfficeUser() && pilot.isImpersonate()) {
            String userToImpersonificate = pilot.getDoor();

            if (!Objects.equals(user.getCurrentImpersonification(), userToImpersonificate)) {
                vanish(pilot);
            }
        }
    }

    @AfterMethod
    void closeContext(Method method) {

        Test test = method.getAnnotation(Test.class);
        String testId = getBusinessTestId(test);
        String testDescription = test.description();

        if (PlaywrightTestUtils.anyStepFailed(testId)) {
            page.screenshot(PlaywrightTestUtils.getScreenshotOptions(testDescription));
        }

        context.close();
    }

    /**
     * Restituisce il door da utilizzare per l'esecuzione del test
     *
     * @param method Metodo di test
     * @return Door da utilizzare
     */
    private CopilotDTO getDoorToUse(Method method) {

        Test test = method.getAnnotation(Test.class);

        if (method.isAnnotationPresent(Impersonificate.class)) {
            Impersonificate impersonification = method.getAnnotation(Impersonificate.class);
            logger.debug("Overriding impersonification for test {} with door {} and store {}", test.testName(), impersonification.door(), impersonification.store());
            return new CopilotDTO(impersonification.door(), MyelStore.fromStoreIdentifier(impersonification.store()), Boolean.TRUE);
        }

        BucketComponent bucketComponent = InjectionUtil.getBean(BucketComponent.class);
        DataTestDTO bucket = bucketComponent.getBucketData(test.testName());
        if (bucket == null) {
            logger.warn("Test {} is not present in {} test execution. Skipping..", test.testName(), PlaywrightTestUtils.getReportType().name());
            throw new SkipException(String.format("Test %s is not present in %s test execution. Skipping..", test.testName(), PlaywrightTestUtils.getReportType().name()));
        }

        return CopilotDTO.builder()
                .door(bucket.getDoor())
                .store(MyelStore.fromStoreIdentifier(bucket.getStoreIdentifier()))
                .isImpersonate(bucket.getImpersonificate())
                .build();
    }

    /**
     * Esegue uno step di test
     *
     * @param number   Numero dello step
     * @param testId   ID del test
     * @param function Funzione da eseguire
     */
    protected void executeStep(Integer number, String testId, TestStepFunction function) {

        ReportComponent report = InjectionUtil.getBean(ReportComponent.class);
        TestCase test = report.getTests().get(testId);
        TestStep step = test.getStep(number);

        if (test.isFailed()) {
            logger.debug("Test {} is already failed! Skipping step {}..", test.getId(), number);
            return;
        }

        try {
            logger.info("\tExecuting step {} for test {}", number, test.getId());
            TestStatus result = function.apply();
            step.setStatus(result);
            logger.info("\tStep {} for test {} executed with status {}", number, test.getId(), result);
        } catch (TimeoutError e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote("Timeout! This step failed because it wanted to interact with an element that was not still present on the page");
            logger.warn("Timeout executing step {} for test {}!", number, test.getId());
            test.setFailed(Boolean.TRUE);
            throw new Error(e);
        } catch (AssertionError e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote(e.getMessage());
            logger.error("Assertion error executing step {} for test {}! {}", number, test.getId(), e.getMessage());
            test.setFailed(Boolean.TRUE);
            throw e;
        } catch (SkipException skipException) {
            step.setNote(skipException.getMessage(), logger);
            step.setStatus(TestStatus.TO_BE_RETESTED);
            if (skipException instanceof MySkipException mySkipException) {
                if (mySkipException.makeScreenshot()) {
                    page.screenshot(PlaywrightTestUtils.getScreenshotOptions(String.format("skip_%s", test.getInternalId())));
                }
            }
            throw skipException;
        } catch (Exception e) {
            step.setStatus(TestStatus.FAILED);
            step.setNote(e.getMessage());
            logger.error("Error executing step {} for test {}! {}", number, test.getId(), e.getMessage());
            test.setFailed(Boolean.TRUE);
            throw new Error(e);
        }
    }

    /**
     * Esegue l'operazione di impersonificazione
     * @param copilot DTO contenente i dati dell'utente da impersonificare
     */
    private void vanish(CopilotDTO copilot) {

        if (!user.isBackOfficeUser()) {
            throw new BackOfficeUserException(String.format("User %s is not a backoffice user!", user.getUsername()));
        }

        logger.debug("Impersonificating user {}..", copilot.getDoor());
        Config config = InjectionUtil.getBean(Config.class);

        String url = config.getBaseUrl().concat(Constants.ENDPOINTS.PERSONIFICATION_URL);
        APIRequestContext context = RequestUtils.buildContext(Context.getPlaywright(), user.getUsername());

        String userToImpersonificate = copilot.getDoor().trim();
        MyelStore storeToImpersonificate = copilot.getStore();

        String urlGet = url.replace("{storeIdentifier}", user.getStore()).replace("{locale}", user.getLocale());
        urlGet = urlGet.concat(String.format("?storeSelected=%s&searchCustomer=%s", storeToImpersonificate.getStoreCode(), userToImpersonificate));

        APIResponse responseGet = context.get(urlGet);
        JsonObject responseBody = JsonParser.parseString(new String(responseGet.body())).getAsJsonObject();
        JsonArray data = responseBody.getAsJsonArray("data");
        if (data.isEmpty()) {
            throw new BackOfficeUserException("Door search returned no results!");
        }
        Long orgentityId = data.get(0).getAsJsonObject().get("orgentityId").getAsLong();

        String urlPost = url.replace("{storeIdentifier}", storeToImpersonificate.getStoreIdentifier()).replace("{locale}", storeToImpersonificate.getLocale());

        APIResponse responsePost = context.post(String.format("%s/%s?storeSelected=%s", urlPost, orgentityId, storeToImpersonificate.getStoreCode()));

        if (!responsePost.ok()) {
            throw new RuntimeException("Error while impersonificating user: " + userToImpersonificate);
        }

        user.setAttribute(Constants.USER_ATTRIBUTES.CURRENT_IMPERSONIFICATION, userToImpersonificate);
        user.setAttribute(Constants.USER_ATTRIBUTES.STORE, storeToImpersonificate.getStoreIdentifier());
        user.setAttribute(Constants.USER_ATTRIBUTES.LOCALE, storeToImpersonificate.getLocale());
        user.deleteAttribute(Constants.USER_ATTRIBUTES.URL);
        context.dispose();
        logger.debug("Impersonification completed!");
    }

    /**
     * Restituisce l'URL dell'utente
     * Se l'URL non è presente, effettua una richiesta GET per recuperarlo
     *
     * @return URL dell'utente
     */
    protected String getURL() {

        if (Objects.isNull(user.getAttribute(Constants.USER_ATTRIBUTES.URL)) || user.getAttribute(Constants.USER_ATTRIBUTES.URL).isEmpty()){

            Config config = InjectionUtil.getBean(Config.class);
            String url = config.getBaseUrl().concat(Constants.ENDPOINTS.SESSION_URL);

            APIRequest.NewContextOptions options = new APIRequest.NewContextOptions();
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Cookie", String.format("%s=%s", Constants.COOKIE_SESSION_ID, PlaywrightFactory.getSessionId(user.getUsername())));
            options.setExtraHTTPHeaders(extraHeaders);

            APIRequest request = Context.getPlaywright().request();
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

    protected String initTestAndReturnId(Method method) {

        ReportComponent reportComponent = InjectionUtil.getBean(ReportComponent.class);
        BucketComponent bucketComponent = InjectionUtil.getBean(BucketComponent.class);

        Test test = method.getAnnotation(Test.class);
        String testId = getBusinessTestId(test);

        DataTestDTO bucket = bucketComponent.getBucketData(test.testName());
        String executor = bucket.getDoor();

        TestCase testCase = reportComponent.getTests().get(testId);
        testCase.setExecutor(executor);
        testCase.setInternalId(test.testName());
        Context.setTest(testCase);

        return testId;
    }

    private String getBusinessTestId(Test test) {

        BucketComponent bucket = InjectionUtil.getBean(BucketComponent.class);
        String businessId = bucket.getBusinessTestIdFromInternal(test.testName());

        if (businessId == null || businessId.isEmpty()) {
            throw new SkipException(String.format("Test %s is disabled in %s test execution. Skipping..", test.testName(), PlaywrightTestUtils.getReportType().name()));
        }

        return businessId;
    }

    protected <T> T getAdditionalData(String key, Class<T> clazz) {
        BucketComponent bucket = InjectionUtil.getBean(BucketComponent.class);

        DataTestDTO data = bucket.getBucketData(Context.getTest().getInternalId());

        if (data.getAdditionalData() == null) {
            throw new IllegalArgumentException("Additional data not found!");
        }

        if (!data.getAdditionalData().containsKey(key)) {
            throw new IllegalArgumentException(String.format("Key %s not found in additional data!", key));
        }

        return clazz.cast(data.getAdditionalData().get(key));
    }
}
