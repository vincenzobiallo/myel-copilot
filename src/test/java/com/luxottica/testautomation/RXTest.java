package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelUtils;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.report.enums.TestStatus;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.luxottica.testautomation.constants.Constants.LABELS.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;

public class RXTest extends BaseTest {

    @Test(testName = "AT016", description = "Process flow Standard warranty")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);

        LabelUtils labelUtils = InjectionUtil.getBean(LabelUtils.class);

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
    }
}
