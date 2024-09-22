package com.luxottica.testautomation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.dto.MultidoorRequestDTO;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.luxottica.testautomation.utils.RequestUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.RequestOptions;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.luxottica.testautomation.constants.Label.ORDER_HISTORY_FULL_DETAILS;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class AccountTest extends BaseTest {

    @Test(testName = "AT022", description = "Order History - Filters")
    public void orderHistoryFilters(Method method) {

        String testId = initTestAndReturnId(method);
        String orderNumber = getAdditionalData("orderNumber", String.class);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/account/order-history");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Filling the Order Number input");
            Locator searchInput = page.locator("#OrderNumber");
            searchInput.fill(orderNumber);

            logger.trace("Clicking on the Search button");
            Locator searchButton = page.locator("//button[@data-element-id='OrderHistory_Search']").last();
            searchButton.click();

            logger.trace("Waiting for the results to load and checking if the order is displayed");
            List<Locator> results = page.locator("//div[@data-element-id='OrderHistory_Results']").locator("div[class^='OrderHistoryOrder__Container']").all();
            assertEquals(results.size(), 1);

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT022", description = "Order Detail - Page structure")
    public void orderHistoryPageStructure(Method method) {

        String testId = initTestAndReturnId(method);
        String orderNumber = getAdditionalData("orderNumber", String.class);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/account/order-history");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Filling the Order Number input");
            Locator searchInput = page.locator("#OrderNumber");
            searchInput.fill(orderNumber);

            Response response = page.waitForResponse("**/history**", () -> {
                logger.trace("Clicking on the Search button");
                Locator searchButton = page.locator("//button[@data-element-id='OrderHistory_Search']").last();
                searchButton.click();
            });

            JsonObject jsonResponse = JsonParser.parseString(new String(response.body())).getAsJsonObject();
            jsonResponse = jsonResponse.getAsJsonObject("data");
            JsonArray results = jsonResponse.getAsJsonArray("histories");
            String orderRef = jsonResponse.getAsJsonArray("histories").get(0).getAsJsonObject().get("reference").getAsString();

            logger.trace("Waiting for the results to load and checking if the order is displayed");
            assertEquals(results.size(), 1);

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);
            final String fullDetailsLabel = labelComponent.getLabel(ORDER_HISTORY_FULL_DETAILS);
            logger.trace("Clicking on the Full Details button");
            Locator fullDetailsButton = page.locator(String.format("//button[contains(text(), '%s')]", fullDetailsLabel)).first();
            fullDetailsButton.click();

            logger.trace("Check if user lands on the order detail page");
            page.waitForURL(Pattern.compile(".*order-history.*"));

            return TestStatus.PASSED;
        });
    }
}
