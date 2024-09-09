package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.luxottica.testautomation.constants.Constants.LABELS.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertTrue;

public class WarrantyTest extends BaseTest {

    /*@Test(testName = "AT016", description = "Process flow Standard warranty")
    @Impersonificate(door = "0001001081", store = MyelStoreold.ITALY)
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);

        LabelComponentImpl labelUtils = InjectionUtil.getBean(LabelComponentImpl.class);
        final String findBrandLabel = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_SEARCH_BRAND_PLACEHOLDER);
        final String findModelLabel = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_SEARCH_MODEL_PLACEHOLDER);
        final String findVariantLabel = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_SEARCH_VARIANT_PLACEHOLDER);

        executeStep(1, testId, () -> {
            String sparePage = getURL() + "/warranty";
            page.navigate(sparePage);

            Locator findBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]");
            findBrand.click();

            Locator brand = page.locator("//div[contains(@class, 'BrandImage')]/img[@class]").first();
            brand.click();

            Locator findModel = page.locator("//div[contains(text(), '" + findModelLabel + "')]");
            assertThat(findModel).isEnabled(Errors.ELEMENTS_DISABLED);
            findModel.click();

            Locator model = page.locator("//div[@class='menulist']/div/button/div[contains(@class, 'custom-select')]").first();
            model.click();

            Locator findVariant = page.locator("//div[contains(text(), '" + findVariantLabel + "')]");
            assertThat(findVariant).isEnabled(Errors.ELEMENTS_DISABLED);
            findVariant.click();

            Locator variant = page.locator("//div[@class='menulist menulist-one-line']/div/button/div[contains(@class, 'custom-select')]").first();
            variant.click();

            String cta1Label = labelUtils.getLabel(getUser(), AFTERSALES_CONVERSATIONAL_PLACE_WARRANTY_CTA);

            Locator cta1 = page.locator("//button[contains(text(), '" + cta1Label + "')]");
            cta1.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            final String defectLabel = labelUtils.getLabel(getUser(), WARRANTY_DAMAGE_INFO_DROPDOWN_DEFECT_PLACEHOLDER);
            Locator findDefect = page.locator("//div[contains(text(), '" + defectLabel + "')]");
            findDefect.click();

            Locator defect = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]").first();
            defect.click();

            final String stateOfSaleLabel = labelUtils.getLabel(getUser(), WARRANTY_DAMAGE_INFO_DROPDOWN_STATE_OF_SALE_PLACEHOLDER);
            Locator findStateOfSale = page.locator("//div[contains(text(), '" + stateOfSaleLabel + "')]");
            findStateOfSale.click();
            Locator stateOfSale = page.locator("//div[@class='menulist menulist-three-line']/div/button[2]/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]").first();
            stateOfSale.click();

            final String requestTypeLabel = labelUtils.getLabel(getUser(), WARRANTY_DAMAGE_INFO_DROPDOWN_TYPE_OF_REQUEST_PLACEHOLDER);
            Locator findRequestType = page.locator("//div[contains(text(), '" + requestTypeLabel + "')]");
            findRequestType.click();
            final String requestTypeStandardLabel = labelUtils.getLabel(getUser(), WARRANTY_LUX_WARR_SELECT_TYPE_OF_REQUEST_VALUE_NAME_STANDARD);
            Locator requestType = page.locator("//div[contains(text(), '" + requestTypeStandardLabel+ "')]");
            requestType.click();

            final String cta2Label = labelUtils.getLabel(getUser(), WARRANTY_DAMAGE_INFO_STICKY_BAR_BUTTON_LABEL);

            Locator cta2 = page.locator("//button[contains(text(), '" + cta2Label + "')]");
            cta2.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });
    }*/
}
