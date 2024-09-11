package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.cart.CartService;
import com.luxottica.testautomation.components.cart.dto.CartDTO;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertTrue;

public class PDPTest extends BaseTest {

    @Test(testName = "AT015", description = "Check Sunglasses PDP and add to cart")
    public void checkSunglassesPDP(Method method) {

        String testId = initTestAndReturnId(method);
        final String CATEGORY = "Sunglasses";

        executeStep(1, testId, () -> {
            logger.trace("Navigating to the Sunglasses PDP");
            String plpPage = getURL() + "/plp/frames?PRODUCT_CATEGORY_FILTER=" + CATEGORY;
            page.navigate(plpPage);
            logger.trace("Clicking on the first tile");
            Locator tile = page.locator("//div[contains(@data-description, 'RB')][1]//img");
            tile.click();

            page.waitForLoadState(LoadState.NETWORKIDLE);
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            String url = page.url();
            assertTrue(url.contains("/pdp/"));

            return TestStatus.PASSED;
        });

        AtomicReference<String> upcValue = new AtomicReference<>();
        executeStep(3, testId, () -> {
            logger.trace("Increasing the quantity of the product");
            Locator increaseQuantity = page.locator("//div[contains(@class, 'AddSizeList')][1]/div[contains(@class, 'AddSize')]/div[contains(@class, 'AddSize')]/div[contains(@class, 'AddSize')][2]//div[contains(@class, 'IconButton')][2]/button[contains(@class, 'IconButton')]").first();
            increaseQuantity.click();

            logger.trace("Getting the UPC value");
            Locator upc = page.locator("//div[contains(@class, 'AddSize')][2]/span[@color='primary']");
            assertThat(upc).isVisible();
            upcValue.set(upc.innerText().replaceAll("[A-Za-z]", "").trim());
            logger.trace("UPC value: {}", upcValue.get());

            logger.trace("Adding the product to the cart");
            Locator addToCart = page.locator("//button[@data-element-id='AddToCart']");
            addToCart.scrollIntoViewIfNeeded();
            addToCart.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        // Check if the product is in the cart in the sunglasses category
        executeStep(4, testId, () -> {
            CartService cartService = InjectionUtil.getBean(CartService.class);
            CartDTO cart = cartService.getCart(Context.getPlaywright(), getUser());
            logger.trace("Checking if the product is in the cart");

            assertTrue(cart.getContent().get(CATEGORY).stream().anyMatch(content -> content.getUpc().equals(upcValue.get())));
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT015", description = "Check Sunglasses PDP and add to cart")
    @Impersonificate(door = "0001026276", store = "myl-it")
    public void checkAFAPDP(Method method) {

        String testId = initTestAndReturnId(method);
        final String CATEGORY = "AFA";

        executeStep(1, testId, () -> {
            String plpPage = getURL() + "/plp/apparel-footwear-and-accessories";
            page.navigate(plpPage);
            Locator tile = page.locator("//div[contains(@data-description, 'FOA')][1]/div[1]//img");
            tile.click();

            page.waitForLoadState(LoadState.NETWORKIDLE);
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            String url = page.url();
            assertTrue(url.contains("/pdp/"));

            return TestStatus.PASSED;
        });

        AtomicReference<String> upcValue = new AtomicReference<>();
        executeStep(3, testId, () -> {
            Locator increaseQuantity = page.locator("//div[contains(@class, 'AddSizeAFA')][0]//div[contains(@class, 'IconButton')][0]/button[contains(@class, 'IconButton')]").first();
            increaseQuantity.click();

            Locator upc = page.locator("//div[contains(@class, 'AddSize')][2]/span[@color='primary']");
            assertThat(upc).isVisible();
            upcValue.set(upc.innerText().replaceAll("[A-Za-z]", "").trim());

            Locator addToCart = page.locator("//button[@data-element-id='AddToCart']");
            addToCart.scrollIntoViewIfNeeded();
            addToCart.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        // Check if the product is in the cart in the sunglasses category
        executeStep(4, testId, () -> {
            CartService cartService = InjectionUtil.getBean(CartService.class);
            CartDTO cart = cartService.getCart(Context.getPlaywright(), getUser());

            assertTrue(cart.getContent().get(CATEGORY).stream().anyMatch(content -> content.getUpc().equals(upcValue.get())));
            return TestStatus.PASSED;
        });
    }
}
