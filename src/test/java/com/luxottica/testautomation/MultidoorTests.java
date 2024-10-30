package com.luxottica.testautomation;

import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.dto.multidoor.DoorDTO;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.security.dto.BFFResponse;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.groups.Tuple;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

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
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        List<DoorDTO> selectedDoors = getSelectedDoors();
        List<DoorDTO> availableDoors = getDoorGroup();

        logger.trace("Filtering doors in the cart..");
        List<DoorDTO> doorsInCart = availableDoors.stream()
                .filter(availableDoor -> selectedDoors.stream()
                        .anyMatch(selectedDoor -> selectedDoor.getOrgentityId().equals(availableDoor.getOrgentityId())))
                .filter(filteredDoor -> filteredDoor.getItemCount() > 0)
                .toList();

        executeStep(1, testId, () -> {

            page.navigate(getURL() + "/cart");
            List<Locator> cartDoorSection = page.locator("div[class^='CartDoor__Section-']").all();

            logger.trace("Checking if the number of doors in the cart is equal to the number of selected doors..");
            assertEquals("Number of doors in the cart is not equal to the number of selected doors", doorsInCart.size(), cartDoorSection.size());

            logger.trace("Checking if the items for each door are displayed correctly..");
            for (int i = 0; i < doorsInCart.size(); i++) {
                Locator loc = cartDoorSection.get(i);
                Locator itemSection = loc.locator("div[class^='CartSection__Section-']");
                assertThat(itemSection).isVisible(String.format("Items for door %s are not visible", doorsInCart.get(i).getOrgentityName()));
            }

            logger.trace("Checking if the order summary is displayed correctly..");
            Locator orderSummary = page.locator("div[data-element-id='Summary']");
            assertThat(orderSummary).isVisible("Order summary is not visible");

            logger.trace("Checking if the number of price summaries is equal to the number of selected doors..");
            List<Locator> priceSummaries = orderSummary.locator("div[class^='CartPriceSummary__PriceSummary-']").all();
            assertEquals("Number of price summaries is not equal to the number of selected doors", doorsInCart.size(), priceSummaries.size());

            logger.trace("Selecting first address for each door..");
            List<Locator> addressContainers = orderSummary.locator("div[class^='CustomSelect__ReactSelectCustom-']").all();
            for (int i = 0; i < doorsInCart.size(); i++) {
                Locator addressContainer = addressContainers.get(i);
                assertThat(addressContainer).isVisible(String.format("Address container for door %s is not visible", doorsInCart.get(i).getOrgentityName()));
                addressContainer.click();

                Locator addressOption = page.locator("div[class^='CustomSelect__OptionContainer-']").first();
                addressOption.click();
            }

            final String cartSummaryCheckoutLabel = labelComponent.getLabel("CART_SUMMARY_CHECKOUT_BUTTON");
            Locator checkoutButton = orderSummary.locator("//button[contains(text(), '" + cartSummaryCheckoutLabel + "')]");
            assertThat(checkoutButton).isVisible("Checkout button is not visible");
            checkoutButton.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            logger.trace("Checking if order summary is displayed correctly..");
            Locator orderSummary = page.locator("div[class^='OrderConfirmationTable__Container-']").first();
            assertThat(orderSummary).isVisible("Order summary is not visible");
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            List<Locator> customerInfoBoxes = page.locator("div[class^='CustomerInfoBox__Box-']").all();
            logger.trace("Check if information boxes are displayed correctly (BillTo and ShipTo for each door)..");
            assertEquals("Number of customer info boxes is not equal to the number of selected doors", doorsInCart.size() * 2, customerInfoBoxes.size());
            return TestStatus.PASSED;
        });

        executeStep(4,testId, () -> {

            final String customerReferenceLabel = labelComponent.getLabel("CUSTOMERREFERENCE_LABEL");
            List<Locator> customerReference = page.locator("textarea[placeholder='" + customerReferenceLabel + "']").all();

            logger.trace("Writing customer reference for each door..");
            for (int i = 0; i < doorsInCart.size(); i++) {
                Locator customerReferenceField = customerReference.get(i);
                assertThat(customerReferenceField).isVisible(String.format("Customer reference field for door %s is not visible", doorsInCart.get(i).getOrgentityName()));
                customerReferenceField.fill(doorsInCart.get(i).getOrgentityName() +" has been automated.");
            }

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT032", description = "Multidoor Order History")
    public void multidoorOrderHistory(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        List<DoorDTO> selectedDoors = getSelectedDoors();
        List<DoorDTO> availableDoors = getDoorGroup();

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/homepage");
            logger.trace("Verify multidoor banner is displayed..");

            final String multidoorLabel = labelComponent.getLabel("MULTIDOOR_BANNER_YOU_ARE_NOW_IN_MULTIDOOR");
            Locator banner = page.locator("#banner");
            Locator text = banner.locator("p").first();
            assertTrue("Multidoor banner is not visible!", banner.isVisible() && text.innerText().contains(multidoorLabel));

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Navigating to order history page..");
            Locator orderHistoryTitle = page.locator("a[data-element-id='MainNav_Orders']");
            assertThat(orderHistoryTitle).isVisible("Order history title is not visible");
            orderHistoryTitle.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator multidoorDropdown = page.locator("div[class^='MultidoorSelect__Container-']").first();
            assertThat(multidoorDropdown).isVisible("Multidoor dropdown is not visible");
            multidoorDropdown.click();

            List<Locator> doorList = page.locator("div[class^='CustomSelect__CustomOption-']").all();
            List<Tuple> doorListTuples = doorList.stream().map(loc -> {
                String innerText = loc.innerText();
                String[] split = innerText.split(" - ");
                return new Tuple(split[0], split[1]);
            }).toList();

            logger.trace("Checking if the first door in the dropdown is the current door..");
            assertEquals("First door in the dropdown is not the current door", selectedDoors.get(0).getOrgentityName(), doorListTuples.get(0).toList().get(0));

            logger.trace("Checking if the other doors are ordered alphabetically..");
            List<String> doorNames = doorListTuples.stream().map(Tuple::toList).map(list -> list.get(1)).map(String::valueOf).toList();
            List<String> sortedDoorNames = doorNames.stream().sorted().toList();

            if (!doorNames.equals(sortedDoorNames)) {
                Context.getTest().getStep(2).setNote("Doors are not ordered alphabetically", logger);
                return TestStatus.PASSED_WITH_MINOR;
            }

            Locator secondDoor = doorList.get(1);
            secondDoor.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            logger.trace("Checking if the order history is displayed correctly..");
            Locator orderHistoryTooltip = page.locator("div[class^='OrderHistoryFilters__TooltipContainer-']").first()
                            .locator("h2").first();
            assertThat(orderHistoryTooltip).containsText(selectedDoors.get(1).getOrgentityName());
            return TestStatus.PASSED;
        });
    }
}
