package com.luxottica.testautomation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.components.report.enums.TestStatus;

import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.dto.MultidoorRequestDTO;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.luxottica.testautomation.utils.RequestUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class MultidoorTest extends BaseTest {

    @Test(testName = "AT016", description = "Process flow Standard warranty")
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);
        String plpPage = getURL() + "/plp/frames?PRODUCT_CATEGORY_FILTER=Sunglasses&PRODUCT_CATEGORY_FILTER=Sunglasses+Kids";
        page.navigate(plpPage);

        executeStep(1, testId, () -> {

            List<JsonObject> doors = getActiveDoors();
            if (doors.size() < 2) {
                logger.debug("Less than 2 doors are active, executing multidoor request");
                if (!startMultidoor()) {
                    throw new RuntimeException("Failed to start multidoor session");
                }
            }
            Response response = page.waitForResponse("**/menu/**", () -> {

            });

            JsonObject responseBody = JsonParser.parseString(new String(response.body())).getAsJsonObject();
            JsonObject data = responseBody.getAsJsonObject("data");
            JsonObject facets = data.getAsJsonObject("facetsViewsDTO");
            String resourceId = facets.get("resourceId").getAsString();

            MultiValueMap<String, String> params = PlaywrightTestUtils.getQueryMap(resourceId);
            assertTrue(params.containsKey("activeDoorId"));
            assertTrue(params.get("activeDoorId").contains("0001001081"));
            assertTrue(params.get("doorId").size() >= 2);

            return TestStatus.PASSED;
        });
    }

    private List<JsonObject> getActiveDoors() {
        logger.debug("Getting active doors");
        Response response = page.waitForResponse("**/usercontext", () -> logger.trace("Waiting for usercontext response"));
        JsonObject userContext = JsonParser.parseString(new String(response.body())).getAsJsonObject();
        JsonArray doors = userContext.get("data").getAsJsonObject().get("multiDoors").getAsJsonArray();

        logger.debug("Obtained active doors: {}", doors.size());
        return doors.asList().stream().map(JsonElement::getAsJsonObject).toList();
    }

    private boolean startMultidoor() {
        Config config = InjectionUtil.getBean(Config.class);
        String url = config.getMultidoor();

        APIRequestContext context = RequestUtils.buildContext(Context.getPlaywright(), getUser().getUsername());

        url = url.replace("{storeIdentifier}", getUser().getStore()).replace("{locale}", getUser().getLocale());

        MultidoorRequestDTO payload = MultidoorRequestDTO.builder()
                .doors(new ArrayList<>(List.of(
                        MultidoorRequestDTO.MultdoorRequestDataDTO.builder()
                                .customer("0001001081")
                                .selected(true)
                                .build(),
                        MultidoorRequestDTO.MultdoorRequestDataDTO.builder()
                                .customer("0001509900")
                                .selected(true)
                                .build()
                )))
                .build();

        APIResponse response = context.post(url,
                RequestOptions.create()
                        .setData(payload)
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json"));

        logger.debug("Multidoor HTTP Status: {}", response.status());

        return Objects.requireNonNull(HttpStatus.resolve(response.status())).is2xxSuccessful();
    }
}
