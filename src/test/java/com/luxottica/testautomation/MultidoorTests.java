package com.luxottica.testautomation;

import com.luxottica.testautomation.components.report.enums.TestStatus;

import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.dto.multidoor.DoorDTO;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.security.dto.BFFResponse;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;

public class MultidoorTests extends MultidoorTest {

    @Test(testName = "AT030", description = "Multidoor Catalogue resulting from merge")
    public void multidoorCatalogueResultingFromMerge(Method method) {

        String testId = initTestAndReturnId(method);
        Config config = InjectionUtil.getBean(Config.class);

        executeStep(1, testId, () -> {

            String plpPage = getURL() + "/plp/frames?PRODUCT_CATEGORY_FILTER=Sunglasses&PRODUCT_CATEGORY_FILTER=Sunglasses+Kids";
            page.navigate(plpPage);

            logger.trace("Getting selectedDoors..");
            List<DoorDTO> selectedDoors = getSelectedDoors();

            logger.trace("Navigating to merged PLP page..");

            BFFResponse response = getBffClient().setRequest(
                    config.getFacets()
                            .replace(Constants.ENDPOINTS.STORE_IDENTIFIER, Context.getUser().getStore())
                            .replace(Constants.ENDPOINTS.LOCALE, Context.getUser().getLocale()),
                    HttpMethod.GET,
                    new LinkedMultiValueMap<>(Map.of(
                            "facetName", List.of("brandGroup"),
                            "doorId", selectedDoors.stream().map(DoorDTO::getOrgentityId).map(String::valueOf).collect(Collectors.toList())
                    )));
            int mergedRecords = response.toJsonObject().get("recordSetTotal").getAsInt();
            int actualMax = 0;

            for (int i = 0; i < selectedDoors.size(); i++) {
                logger.trace("Selecting door {} of {}", i + 1, selectedDoors.size());
                response = getBffClient().setRequest(
                        config.getFacets()
                                .replace(Constants.ENDPOINTS.STORE_IDENTIFIER, Context.getUser().getStore())
                                .replace(Constants.ENDPOINTS.LOCALE, Context.getUser().getLocale()),
                        HttpMethod.GET,
                        new LinkedMultiValueMap<>(Map.of(
                                "facetName", List.of("brandGroup"),
                                "doorId", List.of(selectedDoors.get(i).getOrgentityId().toString())
                        )));

                int selectedRecords = response.toJsonObject().get("recordSetTotal").getAsInt();
                actualMax = Math.max(actualMax, selectedRecords);
            }

            if (mergedRecords < actualMax) {
                Context.getTest().getStep(1).setNote("Merged records are less than the maximum of selected doors", logger);
                return TestStatus.FAILED;
            }

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT031", description = "Multidoor Cart visualization")
    public void multidoorCartVisualization(Method method) {

        String testId = initTestAndReturnId(method);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/cart");

            List<DoorDTO> selectedDoors = getSelectedDoors();
            List<DoorDTO> availableDoors = getDoorGroup();

            logger.trace("Filtering doors in the cart..");
            List<DoorDTO> doorsInCart = availableDoors.stream()
                    .filter(availableDoor -> selectedDoors.stream()
                            .anyMatch(selectedDoor -> selectedDoor.getOrgentityId().equals(availableDoor.getOrgentityId())))
                    .filter(filteredDoor -> filteredDoor.getItemCount() > 0)
                    .toList();

            List<Locator> cartDoorSection = page.locator("div[class^='CartDoor__Section-']").all();

            logger.trace("Checking if the number of doors in the cart is equal to the number of selected doors..");
            assertEquals("Number of doors in the cart is not equal to the number of selected doors", doorsInCart.size(), cartDoorSection.size());

            Locator itemSection = page.locator("div[class^='CartSection__Section-']");
            assertThat(itemSection).isVisible("Door item's section is not visible");

            Locator orderSummary = page.locator("div[data-element-id='Summary']");
            assertThat(orderSummary).isVisible("Order summary is not visible");

            // TODO - Add assertion to check if multiple door summary is displayed correctly

            return TestStatus.PASSED;
        });
    }
}
