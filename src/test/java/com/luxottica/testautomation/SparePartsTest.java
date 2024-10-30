package com.luxottica.testautomation;

import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;

public class SparePartsTest extends BaseTest {

    @Test(testName = "AT017", description = "Spare parts - Complete flow")
    public void sparePartsCompleteFlow(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        String findBrandLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_SEARCH_BRAND_PLACEHOLDER);
        String findModelLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_SEARCH_MODEL_PLACEHOLDER);

        executeStep(1, testId, () -> {
            String sparePage = getURL() + "/spare-parts";
            page.navigate(sparePage);

            logger.trace("Opening brand dropdown");
            Locator findBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]");
            findBrand.click();

            logger.trace("Selecting a brand");
            Locator brand = page.locator("//div[contains(@class, 'BrandImage')]/img[@class]").first();
            brand.click();

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            Locator findModel = page.locator("//div[contains(text(), '" + findModelLabel + "')]");
            logger.trace("Checking if the model dropdown is enabled");
            assertThat(findModel).isEnabled(Errors.ELEMENTS_DISABLED);
            logger.trace("Opening model dropdown");
            findModel.click();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            logger.trace("Selecting a model");
            Locator modelDiv = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]");
            assertThat(modelDiv).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            Locator model = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]").first();
            model.click();

            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {
            String ctaLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_VIEW_SPAREPARTS_CTA);

            logger.trace("Clicking on the CTA");
            Locator cta = page.locator("//button[contains(text(), '" + ctaLabel + "')]");
            cta.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(5, testId, () -> {
            logger.trace("Checking if user is redirected to the spare parts page");
            page.waitForURL("**/spare-parts/**");
            return TestStatus.PASSED;
        });
    }
}
