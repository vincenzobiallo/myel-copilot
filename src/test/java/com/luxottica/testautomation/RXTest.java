package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.labels.LabelComponentImpl;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.models.RXPrescriptionAttribute;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.Assert.*;

public class RXTest extends BaseTest {

    @Test(testName = "AT019", description = "Verify complete job process - non multifocal (single vision)")
    public void verifyCompleteJobProcessNonMultifocal(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        final String selectedBrand = "Chanel";
        final String selectedSubBrand = "Sun";
        final String noBrandOption = labelComponent.getLabel(RX_SELECT_ERROR_NO_OPTS);

        final String model = "0CH4189TQ";
        final String color = "C11287-Brown";

        executeStep(1, testId, () -> {
            String authentics = getURL() + "/rx-prescription?rxFlow=Authentics";
            page.navigate(authentics);

            final String findBrandLabel = labelComponent.getLabel(RX_SEARCH_A_BRAND);
            logger.trace("Click on div with text: {}", findBrandLabel);
            page.locator("//div[contains(text(), '" + findBrandLabel + "')]").click();
            logger.trace("Fill in the input with text: {}", selectedBrand);
            Locator inputFindBrand = page.locator("//div[contains(text(), '" + findBrandLabel + "')]//following-sibling::div//input");
            inputFindBrand.fill(selectedBrand);

            logger.trace("Selecting the filtered brand");
            Locator brand = page.locator("//div[@class='menulist menulist-one-line']/div/button/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]").first();
            brand.click();

            final String subBrandLabel = labelComponent.getLabel(RX_CHOOSE_BRAND);
            Locator subBrand = page.locator("//div[contains(text(), '" + subBrandLabel + "')]");
            logger.trace("Check if sub brand is now enabled");
            assertThat(subBrand).isEnabled(Errors.ELEMENTS_DISABLED);
            logger.trace("Click on div with text: {}", subBrandLabel);
            subBrand.click();

            page.waitForLoadState(LoadState.NETWORKIDLE);

            logger.trace("Selecting the filtered sub brand");
            page.locator("//div[contains(text(), '" + selectedSubBrand + "')]").first().click();

            final String rxCompleteJobLabel = labelComponent.getLabel(JOB_TYPE_RX_COMPLETE_JOB);
            logger.trace("Checking if JOB CHIP are visible");
            Locator chipFull = page.locator("//p[contains(text(), '" + rxCompleteJobLabel.toUpperCase() + "')]");
            assertThat(chipFull).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Check if RX_COMPLETE_JOB is active");
            assertTrue(PlaywrightTestUtils.containClass(chipFull, "active", Errors.CLASS_NOT_FOUND));

            logger.trace("Check if boxes upc search and model search are shown");
            assertThat(page.locator("input[id='upc']")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[class='prescription-container']")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            final String displayLensLabel = labelComponent.getLabel(RX_DISPLAY_LENS);
            logger.trace("Check if CTA with label {} is disabled", displayLensLabel);
            Locator displayLens = page.locator("//button[contains(text(), '" + displayLensLabel.toUpperCase() + "')]");
            displayLens.scrollIntoViewIfNeeded();
            assertThat(displayLens).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            assertTrue(PlaywrightTestUtils.containClass(displayLens, "button-disabled", Errors.CLASS_NOT_FOUND));

            // div class error-icon
            // div error-text > p class CustomText__Text
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            Locator findModel = page.locator("//div[@class='model-form-custom d-flex']/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'custom-select')]/div[contains(@class, 'custom-select')]").first();
            findModel.click();
            Locator modelInput = findModel.locator("input").first();
            modelInput.fill(model);
            Locator modelOption = page.locator("//div[@class='menulist menulist-one-line']/div/button/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]");
            modelOption.click();

            Locator findColor = page.locator("//div[contains(@class, 'CustomSelect')][2]/div[contains(@class, 'custom-select')][1]").first();
            findColor.click();
            Locator colorOption = findColor.locator("input").first();
            colorOption.fill(color);
            Locator colorOptionValue = page.locator("//div[@class='menulist menulist-one-line']/div/button/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]");
            colorOptionValue.click();

            Locator findSize = page.locator("//div[contains(@class, 'CustomSelect')][3]/div[contains(@class, 'custom-select')][1]/div[contains(@class, 'custom-select')][1]").first();
            findSize.click();
            Locator firstOption = page.locator("//div[@class='menulist menulist-one-line']/div/button/div[contains(@class, 'custom-select')]/div[contains(@class, 'CustomSelect')]/div[contains(@class, 'CustomSelect')]/div/div[contains(@class, 'CustomSelect')]");
            firstOption.click();

            Locator prescriptionTable = getPrescriptionTable();
            List<RXPrescriptionAttribute> prescriptionColumns = getPrescriptionColumns(prescriptionTable);

            final String sphereLabel = labelComponent.getLabel(ESSILOR_PRESCRIPTION_SPHERE);
            RXPrescriptionAttribute sphere = getPrescriptionAttribute(sphereLabel, prescriptionColumns);
            sphere.getInputRight().fill("+0.00");
            sphere.getInputLeft().fill("+0.00");

            final String pdLabel = labelComponent.getLabel(ESSILOR_PRESCRIPTION_PD);
            RXPrescriptionAttribute pd = getPrescriptionAttribute(pdLabel, prescriptionColumns);
            pd.getInputRight().fill("33");
            pd.getInputLeft().fill("33");

            final String heightLabel = labelComponent.getLabel(ESSILOR_PRESCRIPTION_HEIGHT);
            RXPrescriptionAttribute height = getPrescriptionAttribute(heightLabel, prescriptionColumns);
            height.getInputRight().fill("17");
            height.getInputLeft().fill("17");

            return TestStatus.PASSED;
        });
    }

    private Locator getPrescriptionTable() {
        return page.locator("div[class^='PrescriptionTable__Table']").first();
    }

    private List<RXPrescriptionAttribute> getPrescriptionColumns(Locator prescriptionTable) {

        List<RXPrescriptionAttribute> attributes = new LinkedList<>();

        List<Locator> columns = prescriptionTable.locator("div[class^='PrescriptionTable__Column-']").all()
                .stream().filter(column -> !PlaywrightTestUtils.containClass(column, "column-int", Errors.CLASS_NOT_FOUND)).toList();

        for (Locator column : columns) {
            Locator header = column.locator("div[class^='PrescriptionTable__ColumnHeader']").first();
            String columnName = header.locator("p").innerHTML();
            logger.debug("Extracting the text from the column header: {}", columnName);

            List<Locator> content = column.locator("div[class^='PrescriptionTable__ColumnContent-']").all();
            assertEquals(content.size(), 2, "The column should have 2 elements: right eye and left one");

            Locator rightEye = content.get(0);
            Locator leftEye = content.get(1);

            Locator inputRightEye = rightEye.locator("input").first();
            Locator inputLeftEye = leftEye.locator("input").first();

            attributes.add(RXPrescriptionAttribute.builder()
                    .identifier(columnName)
                    .inputRight(inputRightEye)
                    .inputLeft(inputLeftEye)
                    .build());
        }

        return attributes;
    }

    private RXPrescriptionAttribute getPrescriptionAttribute(String identifier, List<RXPrescriptionAttribute> attributes) {
        Optional<RXPrescriptionAttribute> attr =  attributes.stream().filter(attribute -> attribute.getIdentifier().equals(identifier)).findFirst();
        if (attr.isEmpty()) {
            throw new NullPointerException(String.format("Attribute with identifier %s not found", identifier));
        }

        return attr.get();
    }

    /*@Test(testName = "AT016", description = "Process flow Standard warranty")
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
