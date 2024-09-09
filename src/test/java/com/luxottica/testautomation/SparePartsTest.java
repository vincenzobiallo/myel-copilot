package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.luxottica.testautomation.constants.Constants.LABELS.AFTERSALES_CONVERSATIONAL_SEARCH_BRAND_PLACEHOLDER;
import static com.luxottica.testautomation.constants.Constants.LABELS.AFTERSALES_CONVERSATIONAL_SEARCH_MODEL_PLACEHOLDER;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertTrue;

public class SparePartsTest extends BaseTest {

    /*@Test(testName = "AT016", description = "Spare parts - Complete flow")
    @Impersonificate(door = "0001001081", store = MyelStoreold.ITALY)
    public void sparePartsCompleteFlow(Method method) {

        String testId = initTestAndReturnId(method);

        LabelComponentImpl labelUtils = InjectionUtil.getBean(LabelComponentImpl.class);
        String findBrandLabel = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_SEARCH_BRAND_PLACEHOLDER);
        String findModelLabel = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_SEARCH_MODEL_PLACEHOLDER);

        executeStep(1, testId, () -> {
            String sparePage = getURL() + "/spare-parts";
            page.navigate(sparePage);

            Locator findBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]");
            findBrand.click();

            Locator brand = page.locator("//div[contains(@class, 'BrandImage')]/img[@class]").first();
            brand.click();

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {
            Locator findModel = page.locator("//div[contains(text(), '" + findModelLabel + "')]");
            assertThat(findModel).isEnabled(Errors.ELEMENTS_DISABLED);
            findModel.click();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            Locator modelDiv = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]");
            assertThat(modelDiv).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            Locator model = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]").first();
            model.click();

            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {
            String ctaLabel = labelUtils.getLabel(getUser(), Constants.LABELS.AFTERSALES_CONVERSATIONAL_VIEW_SPAREPARTS_CTA);

            Locator cta = page.locator("//button[contains(text(), '" + ctaLabel + "')]");
            cta.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(5, testId, () -> {
            String url = page.url();
            assertTrue(url.contains("/spare-parts/"));

            return TestStatus.PASSED;
        });
    }*/
}
