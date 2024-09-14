package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.exceptions.MySkipException;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;

public class WarrantyTest extends BaseTest {

    @Test(testName = "AT016", description = "Process flow Standard warranty")
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);

        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        final String selectedBrand = "Oakley";

        executeStep(1, testId, () -> {
            String sparePage = getURL() + "/warranty";
            page.navigate(sparePage);

            final String findBrandLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_SEARCH_BRAND_PLACEHOLDER);
            logger.trace("Click on div with text: {}", findBrandLabel);
            Locator findBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]");
            findBrand.click();

            logger.trace("Fill in the input with text: {}", selectedBrand);
            Locator inputFindBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]//following-sibling::div//input");
            inputFindBrand.fill(selectedBrand);
            logger.trace("Selecting the filtered brand");
            Locator brand = page.locator("//div[contains(@class, 'BrandImage')]/img[@class]").first();
            brand.click();

            final String findModelLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_SEARCH_MODEL_PLACEHOLDER);
            Locator findModel = page.locator("//div[contains(text(), '" + findModelLabel + "')]");
            logger.trace("Check if SEARCH MODEL is now enabled");
            assertThat(findModel).isEnabled(Errors.ELEMENTS_DISABLED);
            logger.trace("Click on div with text: {}", findModelLabel);
            findModel.click();

            logger.trace("Selecting first model in the list");
            Locator model = page.locator("//div[@class='menulist']/div/button/div[contains(@class, 'custom-select')]").first();
            model.click();

            final String findVariantLabel = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_SEARCH_VARIANT_PLACEHOLDER);
            Locator findVariant = page.locator("//div[contains(text(), '" + findVariantLabel + "')]");
            logger.trace("Check if SEARCH VARIANT is now enabled");
            assertThat(findVariant).isEnabled(Errors.ELEMENTS_DISABLED);
            logger.trace("Click on div with text: {}", findVariantLabel);
            findVariant.click();

            logger.trace("Selecting first variant in the list");
            Locator variant = page.locator("//div[@class='menulist']/div/button/div[contains(@class, 'custom-select')]").first();
            variant.click();

            final String cta1Label = labelComponent.getLabel(AFTERSALES_CONVERSATIONAL_PLACE_WARRANTY_CTA);
            logger.trace("Clicking on CTA with text: {}", cta1Label);
            Locator cta1 = page.locator("//button[contains(text(), '" + cta1Label + "')]");
            cta1.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            logger.debug("Warranty Damage Info page opened");

            logger.trace("Selecting defect");
            final String defectLabel = labelComponent.getLabel(WARRANTY_DAMAGE_INFO_DROPDOWN_DEFECT_PLACEHOLDER);
            Locator findDefect = page.locator("//div[contains(text(), '" + defectLabel + "')]");
            findDefect.click();

            final String defectColorLabel = labelComponent.getLabel(WARRANTY_SERVICE_REQUEST_DEFECT_DESCRIPTION_001);
            logger.trace("Selecting defect with text: {}", defectColorLabel);
            Locator defect = page.locator("//div[contains(text(), '" + defectColorLabel + "')]").first();
            defect.click();

            logger.trace("Selecting state of sale");
            final String stateOfSaleLabel = labelComponent.getLabel(WARRANTY_DAMAGE_INFO_DROPDOWN_STATE_OF_SALE_PLACEHOLDER);
            Locator findStateOfSale = page.locator("//div[contains(text(), '" + stateOfSaleLabel + "')]");
            findStateOfSale.click();

            final String stateLabel = labelComponent.getLabel(WARRANTY_LUX_WARR_STATE_OF_SALE_VALUE_SOLD);
            logger.trace("Selecting state of sale with text: {}", stateLabel);
            Locator stateOfSale = page.locator("//div[contains(text(), '" + stateLabel + "')]").first();
            stateOfSale.click();

            logger.trace("Selecting type of request");
            final String requestTypeLabel = labelComponent.getLabel(WARRANTY_DAMAGE_INFO_DROPDOWN_TYPE_OF_REQUEST_PLACEHOLDER);
            Locator findRequestType = page.locator("//div[contains(text(), '" + requestTypeLabel + "')]");
            findRequestType.click();

            final String requestTypeStandardLabel = labelComponent.getLabel(WARRANTY_LUX_WARR_SELECT_TYPE_OF_REQUEST_VALUE_NAME_STANDARD);
            logger.trace("Selecting type of request with text: {}", requestTypeStandardLabel);
            Locator requestType = page.locator("//div[contains(text(), '" + requestTypeStandardLabel+ "')]");
            requestType.click();

            final String notAvailableLabel = labelComponent.getLabel(WARRANTY_LUX_WARR_SELECT_TYPE_OF_REQUEST_VALUE_NAME__DIGITAL_ERROR_NOT_AVAILABLE_DESCRIPTION);
            Locator notAvailable = page.locator("//p[contains(text(), '" + notAvailableLabel + "')]");
            if (notAvailable.isVisible()) {
                throw new MySkipException("Selected product is not available for warranty!", true);
            }

            final String cta2Label = labelComponent.getLabel(WARRANTY_DAMAGE_INFO_STICKY_BAR_BUTTON_LABEL);
            logger.trace("Clicking on CTA with text: {}", cta2Label);
            Locator cta2 = page.locator("//button[contains(text(), '" + cta2Label + "')]");
            cta2.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });
    }
}
