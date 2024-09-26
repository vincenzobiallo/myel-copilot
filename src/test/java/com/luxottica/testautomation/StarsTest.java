package com.luxottica.testautomation;

import com.google.gson.JsonParser;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.options.LoadState;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.*;

public class StarsTest extends BaseTest {

    @Test(testName = "AT024", description = "Stars navigation menu by brand - Stars Brand Pre PLP")
    public void starsBrandPrePLP(Method method) {

        String testId = initTestAndReturnId(method);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/homepage");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

            logger.trace("Opening Brands Menu");
            page.locator("span[data-element-id='MainNav_Brands']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible("Brands menu is not visible");

            logger.trace("Verifying the presence of the Stars container");
            Locator starsContainer = page.locator("div[class^='BrandMenu__StarsContainer-']").first();
            assertThat(starsContainer).isVisible("Stars container is not visible");
            final String starsContainerText = labelComponent.getLabel(MENU_BRAND_STARS);
            Locator starsContainerTitle = page.locator("//p[contains(text(), '" + starsContainerText + "')]").first();
            assertThat(starsContainerTitle).isVisible("Stars container title is not visible");

            logger.trace("Verifying the presence of the Others container");
            final String othersContainerText = labelComponent.getLabel(MENU_BRAND_OTHERS);
            Locator othersContainerTitle = page.locator("//p[contains(text(), '" + othersContainerText + "')]").first();
            assertThat(othersContainerTitle).isVisible("Others container title is not visible");

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            logger.trace("Opening Stars container");
            Locator starsContainer = page.locator("div[class^='BrandMenu__StarsContainer-']").first();
            List<Locator> stars = starsContainer.locator("div[class^='BrandMenu__Container-']").first()
                    .locator("button[class^='BrandButton__BrandButtonEl-']").all();
            assertFalse("No stars found in the Stars container", stars.isEmpty());

            logger.trace("Clicking on the first star");
            stars.get(0).click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertTrue(page.url().contains("/preplp/"));
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT025", description = "PLP Stars - Check Macrofamily Chips")
    public void plpStarsCheckMacrofamilyChips(Method method) {

        String testId = initTestAndReturnId(method);

        executeStep(1, testId, () -> {

            page.navigate(getURL() + "/homepage");

            logger.trace("Opening Brands Menu");
            page.locator("span[data-element-id='MainNav_Brands']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible("Brands menu is not visible");

            Locator starsContainer = page.locator("div[class^='BrandMenu__StarsContainer-']").first();
            List<Locator> stars = starsContainer.locator("div[class^='BrandMenu__Container-']").first()
                    .locator("button[class^='BrandButton__BrandButtonEl-']").all();
            assertFalse("No stars found in the Stars container", stars.isEmpty());

            logger.trace("Clicking on the first star");
            stars.get(0).click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);
            final String starsLabel = labelComponent.getLabel(STARS_LABEL);
            Locator starsContainerTitle = page.locator("//span[contains(text(), '" + starsLabel + "')]").first();
            starsContainerTitle.click();

            final String showAllLabel = labelComponent.getLabel(VIEW_ALL);
            Locator showAllButton = page.locator("//button[contains(text(), '" + showAllLabel + "')]").first();
            showAllButton.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            List<Locator> macrofamilies = page.locator("button[class^='PLPStarsButtons__']").all();
            List<String> allFamilies = List.of("Best Stars", "Newness", "For You", "Sport", "Lifestyle", "Brand Lover", "XXL", "One Shot", "View all");

            List<String> presentFamilies = macrofamilies.stream().map(Locator::textContent).toList();
            // Check sorting ignoring presence of chips
            List<String> filteredFamilies = allFamilies.stream().filter(presentFamilies::contains).toList();

            if (Objects.equals(filteredFamilies, presentFamilies)) {
                return TestStatus.PASSED;
            } else {
                Context.getTest().getStep(2).setNote("Macrofamily chips are not displayed correctly (order or missing)");
                return TestStatus.PASSED_WITH_MINOR;
            }
        });

        executeStep(3, testId, () -> {
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            final String macrofamilyText = getAdditionalData("macrofamily", String.class).toUpperCase();
            logger.trace("Clicking on BEST STARS macrofamily");
            page.waitForSelector("button[class^='PLPStarsButtons__']");
            List<Locator> macrofamilies = page.locator("button[class^='PLPStarsButtons__']").all();
            Locator bestStars = macrofamilies.stream().filter(macrofamily -> macrofamily.textContent().equalsIgnoreCase(macrofamilyText)).findFirst().orElseThrow();
            bestStars.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertTrue(page.url().contains("&MACROFAMILY=" + macrofamilyText.replace(" ", "+")));
            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {
            final String macrofamilyText = "Mostra tutto";
            logger.trace("Clicking on View all macrofamily");
            Locator bestStars = page.locator("//button[text()='" + macrofamilyText + "']").first();
            bestStars.click();

            assertFalse(page.url().contains("&MACROFAMILY="));
            return TestStatus.PASSED;
        });

        executeStep(5, testId, () -> {
            final String macrofamilyText = "ONE SHOT";
            logger.trace("Clicking on One shot macrofamily");
            List<Locator> macrofamilies = page.locator("button[class^='PLPStarsButtons__']").all();
            Optional<Locator> bestStars = macrofamilies.stream().filter(macrofamily -> macrofamily.textContent().equalsIgnoreCase(macrofamilyText)).findFirst();
            if (bestStars.isPresent()) {
                bestStars.get().click();

                assertFalse(page.url().contains("&MACROFAMILY=" + macrofamilyText));
                return TestStatus.PASSED;
            } else {
                Context.getTest().getStep(5).setNote("One Shot macrofamily is not displayed", logger);
                return TestStatus.PASSED_WITH_MINOR;
            }
        });

        executeStep(6, testId, () -> {
            logger.trace("Clicking on first macrofamily");
            List<Locator> macrofamilies = page.locator("button[class^='PLPStarsButtons__']").all();
            Locator firstFamily = macrofamilies.get(0);
            String firstFamilyText = firstFamily.textContent();

            firstFamily.click();
            Locator header = page.locator("//p[contains(text(), '" + firstFamilyText.toUpperCase() + "')]").first();
            assertThat(header).isVisible("Header does not contains the selected macrofamily");

            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT026", description = "PDP - Sku stars")
    public void pdpSkuStars(Method method) {

        String testId = initTestAndReturnId(method);

        executeStep(1, testId, () -> {

            page.navigate(getURL() + "/homepage");

            logger.trace("Opening Brands Menu");
            page.locator("span[data-element-id='MainNav_Brands']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible("Brands menu is not visible");

            Locator starsContainer = page.locator("div[class^='BrandMenu__StarsContainer-']").first();
            List<Locator> stars = starsContainer.locator("div[class^='BrandMenu__Container-']").first()
                    .locator("button[class^='BrandButton__BrandButtonEl-']").all();
            assertFalse("No stars found in the Stars container", stars.isEmpty());
            logger.trace("Clicking on the first star");
            stars.get(0).click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);
            final String starsLabel = labelComponent.getLabel(STARS_LABEL);
            Locator starsContainerTitle = page.locator("//span[contains(text(), '" + starsLabel + "')]").first();
            starsContainerTitle.click();

            logger.trace("Opening the first macrofamily");
            final String showAllLabel = labelComponent.getLabel(VIEW_ALL);
            Locator showAllButton = page.locator("//button[contains(text(), '" + showAllLabel + "')]").first();
            showAllButton.click();

            assertTrue(page.url().contains("/plp-stars/"));

            logger.trace("Opening the first product");
            Locator firstTile = page.locator("//button[contains(@class, 'TileSku')]").first();
            assertThat(firstTile).isVisible("No products found in the PLP");
            firstTile.click();

            assertTrue(page.url().contains("/pdp/"));

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            Locator sizeContainer = page.locator("div[class^='SizeContainer__StyledSizeContainer-']").first();
            assertThat(sizeContainer).isVisible("Size container is not visible");

            List<Locator> sizes = sizeContainer.locator("div[class^='AddSize__SizeContainer-']").all();

            logger.trace("Checking if star sizes have star icons and highlighted background for the first product");
            List<Locator> starSizes = sizes.stream()
                    .filter(size -> PlaywrightTestUtils.containClass(size, "add-size-stars"))
                    .toList();
            for (Locator starSize : starSizes) {
                Locator iconContainer = starSize.locator("div[class^='AddSize__IconContainer-']").first();
                assertTrue(PlaywrightTestUtils.containClass(iconContainer, "stars-icon"));
                assertThat(iconContainer).isVisible("Star icon is not visible");
            }

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            List<Locator> products = page.locator("div[class^='WrapperLayout__Wrapper-']").locator("div[data-element-id='MainBox']").all();

            logger.trace("Ordering the products by stars");
            List<Locator> sortedProducts = products.stream()
                    .sorted((p1, p2) -> {
                        boolean p1HasStars = p1.locator("div[class^='AddSize__SizeContainer-']").all().stream()
                                .anyMatch(size -> PlaywrightTestUtils.containClass(size, "add-size-stars"));
                        boolean p2HasStars = p2.locator("div[class^='AddSize__SizeContainer-']").all().stream()
                                .anyMatch(size -> PlaywrightTestUtils.containClass(size, "add-size-stars"));

                        if (p1HasStars && !p2HasStars) {
                            return -1;
                        } else if (!p1HasStars && p2HasStars) {
                            return 1;
                        } else {
                            return 0;
                        }
                    })
                    .toList();

            assertEquals(products, sortedProducts);
            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {
            logger.trace("Adding first product to the cart");
            Locator firstProduct = page.locator("div[data-element-id='MainBox']").first();
            Locator firstStarSize = firstProduct.locator("div[class^='AddSize__SizeContainer-']").all()
                    .stream().filter(size -> PlaywrightTestUtils.containClass(size, "add-size-stars")).findFirst().orElseThrow();

            Locator input = firstStarSize.locator("input").first();
            input.fill("1");

            logger.trace("Extracting the UPC value from request triggered by adding the product to the cart");
            Request request = page.waitForRequest("**/items", () -> {
                Locator addToCart = page.locator("//button[@data-element-id='AddToCart']");
                addToCart.scrollIntoViewIfNeeded();
                addToCart.click();
            });
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String upc = JsonParser.parseString(request.postData()).getAsJsonObject().get("orderItem").getAsJsonArray()
                    .get(0).getAsJsonObject().get("xitem_upc").getAsString();
            logger.debug("UPC value: {}", upc);

            return TestStatus.PASSED;
        });

    }
}
