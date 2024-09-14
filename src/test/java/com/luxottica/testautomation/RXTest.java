package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.microsoft.playwright.Locator;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;

public class RXTest extends BaseTest {

    @Test(testName = "AT019", description = "Verify complete job process - non multifocal (single vision)")
    public void verifyCompleteJobProcessNonMultifocal(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        final String selectedBrand = "Oakley";

        executeStep(1, testId, () -> {
            String authentics = getURL() + "/rx-prescription?rxFlow=Authentics";
            page.navigate(authentics);

            final String findBrandLabel = labelComponent.getLabel(RX_CHOOSE_BRAND);
            logger.trace("Click on div with text: {}", RX_CHOOSE_BRAND);
            page.locator("//div[contains(text(), '" + "Cerca brand" + "')]").click();

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT016", description = "Process flow Standard warranty")
    @Impersonificate(door = "0001001081", store = MyelStoreold.ITALY)
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);

        LabelComponentImpl labelUtils = InjectionUtil.getBean(LabelComponentImpl.class);

        final String rxChooseLabel = labelUtils.getLabel(getUser(), RX_CHOOSE_BRAND);

        executeStep(1, testId, () -> {
            String microFE = getURL() + "/rx-prescription?rxFlow=Authentics&brands=RB";
            page.navigate(microFE);

            Locator findBrand = page.locator("//div[contains(text(), '" + rxChooseLabel + "')]");
            findBrand.click();
            Locator brand = page.locator("//div[@class='menulist']/div/button[1]/div[contains(@class, 'custom-select')]").first();
            brand.click();

            final String completeJobLabel = labelUtils.getLabel(getUser(), "RX_COMPLETE_JOB");
            Locator CHIP_COMPLETE_JOB = page.locator("//p[contains(text(), '" + completeJobLabel.toUpperCase() + "')]");
            PlaywrightTestUtils.containClass(CHIP_COMPLETE_JOB, "active", Errors.CLASS_NOT_FOUND);

            final String findModelLabel = labelUtils.getLabel(getUser(), "RX_CHOOSE_MODEL");
            Locator findModel = page.locator("//div[contains(text(), '" + findModelLabel + "')]");
            final String findColorLabel = labelUtils.getLabel(getUser(), "RX_CHOOSE_COLOR");
            Locator findColor = page.locator("//div[contains(text(), '" + findColorLabel + "')]");
            final String findSizeLabel = labelUtils.getLabel(getUser(), "RX_CHOOSE_SIZE");
            Locator findSize = page.locator("//div[contains(text(), '" + findSizeLabel + "')]");

            assertThat(page.locator("input[id='upc']")).isVisible();
            assertThat(findModel).isEnabled();
            assertThat(findColor).isDisabled();
            assertThat(findSize).isDisabled();
            assertThat(page.locator("div[class^='PrescriptionTable_']").first());

            return TestStatus.PASSED;
        });
    }*/
}
