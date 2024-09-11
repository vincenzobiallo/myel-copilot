package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static com.luxottica.testautomation.constants.Label.*;
import static org.testng.AssertJUnit.assertTrue;

public class PLPTest extends BaseTest {

    @Test(testName = "AT014", description = "Check catalogue sorting")
    public void checkCatalogueSorting(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        String plpPage = getURL() + "/plp/frames?PRODUCT_CATEGORY_FILTER=Sunglasses&PRODUCT_CATEGORY_FILTER=Sunglasses+Kids";
        page.navigate(plpPage);

        String sortByRelevanceText = labelComponent.getLabel(PLP_SORT_BY_RELEVANCE);
        Locator sortByRelevance = page.locator(String.format("//div[text()='%s']", sortByRelevanceText)).first();
        String sortByBestsellerText = labelComponent.getLabel(PLP_SORT_BY_BESTSELLER);
        Locator sortByBestseller = page.locator(String.format("//div[text()='%s']", sortByBestsellerText)).first();
        String sortByAlphabeticalText = labelComponent.getLabel(PLP_SORT_BY_ALPHABETICAL);
        Locator sortByAlphabetical = page.locator(String.format("//div[text()='%s']", sortByAlphabeticalText)).first();
        String sortByNewText = labelComponent.getLabel(PLP_SORT_BY_NEW);
        Locator sortByNew = page.locator(String.format("//div[text()='%s']", sortByNewText)).first();

        executeStep(1, testId, () -> {

            Locator dropdown = page.locator(String.format("//div[text()='%s']", sortByRelevanceText)).first();
            dropdown.click();

            List<Locator> options = List.of(sortByRelevance, sortByBestseller, sortByAlphabetical, sortByNew);
            for (Locator option : options) {
                assertThat(option).isVisible();
            }

            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            sortByBestseller.click();
            assertTrue(page.url().contains(String.format("&orderBy=%s", PLP_SORT_BY_BESTSELLER.replace("PLP_SORT_BY_", ""))));

            Locator dropdown = page.locator(String.format("//div[text()='%s']", sortByBestsellerText)).first();
            dropdown.click();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {

            sortByAlphabetical.click();
            assertTrue(page.url().contains(String.format("&orderBy=%s", PLP_SORT_BY_ALPHABETICAL.replace("PLP_SORT_BY_", ""))));

            Locator dropdown = page.locator(String.format("//div[text()='%s']", sortByAlphabeticalText)).first();
            dropdown.click();

            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {

            sortByNew.click();
            assertTrue(page.url().contains(String.format("&orderBy=%s", PLP_SORT_BY_NEW.replace("PLP_SORT_BY_", ""))));

            Locator dropdown = page.locator(String.format("//div[text()='%s']", sortByNewText)).first();
            dropdown.click();

            return TestStatus.PASSED;
        });

        executeStep(5, testId, () -> {

            sortByRelevance.click();
            assertTrue(page.url().contains(String.format("&orderBy=%s", PLP_SORT_BY_RELEVANCE.replace("PLP_SORT_BY_", ""))));

            return TestStatus.PASSED;
        });
    }
}
