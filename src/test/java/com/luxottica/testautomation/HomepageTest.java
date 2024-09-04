package com.luxottica.testautomation;

import com.luxottica.testautomation.annotations.Impersonificate;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.report.enums.TestStatus;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static com.luxottica.testautomation.constants.Constants.LABELS.COOKIE_POLICY_BANNER_MESSAGE;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.Assert.assertNotEquals;

public class HomepageTest extends BaseTest {

    @BeforeMethod
    public void landInPage() {
        page.navigate(super.getURL() + "/homepage");
        page.waitForURL(super.getURL() + "/homepage", new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        if (page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remind me later")).isVisible()) {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remind me later")).click();
        }
    }

    @Test(testName = "AT001", description = "Cookie Acceptance Banner")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void cookieAcceptanceBanner(Method method) {

        String testId = initTestAndReturnId(method);

        executeStep(1, testId, () -> {

            String text = labelUtils.getLabel(getUser(), COOKIE_POLICY_BANNER_MESSAGE);
            Locator.FilterOptions filterOptions = new Locator.FilterOptions();
            filterOptions.setHasText(text);

            assertThat(page.locator("div").filter(filterOptions).nth(2)).isVisible(Errors.COOKIE_BANNER_NOT_FOUND);
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT002", description = "HomePage Parts are Visible")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void homepagePartsAreVisible(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            assertThat(page.locator("header#header>div>div")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("input[placeholder='Cerca']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[data-name='hp-top-banner']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[class^='PromotionsCarousel__SliderContainer']")).hasCount(2);
            assertThat(page.locator("div[data-element-id='News']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[data-name='highlights']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[data-name='best-sellers']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("div[data-name='leonardo-carousel']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            assertThat(page.locator("footer#sticky-footer")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT003", description = "Brands Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void brandsMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            page.locator("span[data-element-id='MainNav_Brands']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            var buttons = page.locator("div[class^='BrandMenu__Container-']").nth(0).locator("button").all();
            var descriptions = buttons.stream().map(button -> button.getAttribute("data-description")).collect(Collectors.toList());
            var sortedDescriptions = descriptions.stream().sorted().collect(Collectors.toList());

            return descriptions.equals(sortedDescriptions) ? TestStatus.PASSED : TestStatus.PASSED_WITH_MINOR;
        });
    }

    @Test(testName = "AT004", description = "Categories Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void categoriesMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Opening Categories Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            logger.debug("Verifying Categories Menu is ordered");
            var buttons = page.locator("div[class^='CategoriesMenu__Menu-']").nth(0).locator("button").all();
            var categories = buttons.stream().map(button -> button.getAttribute("data-element-id")).collect(Collectors.toList());
            var orderedCategories = List.of(
                    "MainNav_Products_Sunglasses",
                    "MainNav_Products_Eyeglasses",
                    "MainNav_Products_Smart_Glasses",
                    "MainNav_Products_GOGGLES_HELMETS",
                    "MainNav_Products_Lenses",
                    "MainNav_Products_ACCESSORIES",
                    "MainNav_Products_AFA"
            );

            return categories.equals(orderedCategories) ? TestStatus.PASSED : TestStatus.PASSED_WITH_MINOR;
        });
    }

    @Test(testName = "AT005", description = "Sunglasses Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void sunglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Sunglasses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            var MENU_ID = "MainNav_Products_Sunglasses";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            List<Locator> columns = page.locator("div[class^='MenuColumns__Container']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Sunglasses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT006", description = "Eyeglasses Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void eyeglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Eyeglasses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_Eyeglasses";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            Locator submenu = page.locator("div[class^='MenuColumns__Container']").locator(">div");
            List<Locator> columns  = submenu.all();
            assertNotEquals(columns.size(), 0, "No columns found in Eyeglasses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT007", description = "Smartglasses Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void smartglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Smartglasses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_Smart_Glasses";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            List<Locator> columns = page.locator("div[class^='MenuColumns__Container']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Smartglasses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT008", description = "Goggles&Helmets Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void gogglesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Smartglasses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_GOGGLES_HELMETS";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            List<Locator> columns = page.locator("div[class^='MenuColumns__Container']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Goggles&Helmets Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible(Errors.VIEW_ALL_NOT_VISIBLE);
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT009", description = "Lenses Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void lensesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Lenses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_Lenses";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            List<Locator> columns = page.locator("div[class^='MenuColumns__Container']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Lenses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT010", description = "Eyewear Accessories Sub-Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void accessoriesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Eyewear Accessories Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_ACCESSORIES";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            List<Locator> columns = page.locator("div[class^='MenuColumns__Container']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Accessories Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT011", description = "AFA Sub-Menu")
    @Impersonificate(door = "0001026276", store = MyelStore.ITALY)
    public void afaSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open AFA Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_AFA";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='MenuColumns__Container']>div");
            Locator container = page.locator("div[class^='MenuColumns__Container']").locator(">div");
            assertThat(container).hasCount(5, new LocatorAssertions.HasCountOptions().setTimeout(5000));
            List<Locator> columns = container.all();
            assertNotEquals(columns.size(), 0, "No columns found in Sunglasses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT012", description = "Service Menu")
    @Impersonificate(door = "0001001081", store = MyelStore.ITALY)
    public void serviceMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open Service Menu");
            page.locator("button[data-element-id='MainNav_Services']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            List<Locator> columns = page.locator("div[class^='ServicesMenu__Menu']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Service Menu");

            List<Locator> urls = columns.stream().flatMap(column -> column.locator("button").all().stream()).collect(Collectors.toList());
            List<String> LINK_MUST_CONTAIN = List.of(
                    "MainNav_Services_AFTERSALES_SECTION_Spareparts",
                    "MainNav_Services_AFTERSALES_SECTION_Warranty"
            );

            List<String> links = urls.stream().map(url -> url.getAttribute("data-element-id")).collect(Collectors.toList());
            return links.containsAll(LINK_MUST_CONTAIN) ? TestStatus.PASSED : TestStatus.PASSED_WITH_MINOR;
        });
    }

}
