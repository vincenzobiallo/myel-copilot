package com.luxottica.testautomation;

import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.constants.Errors;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.LoadState;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;

import static com.luxottica.testautomation.constants.Label.COOKIE_POLICY_BANNER_MESSAGE;
import static com.luxottica.testautomation.constants.Label.SEARCH_SORRY_NO_RESULTS;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.Assert.*;

public class HomepageTest extends BaseTest {

    @BeforeMethod
    public void landInPage() {
        page.navigate(super.getURL() + "/homepage");
        page.waitForURL(super.getURL() + "/homepage", new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test(testName = "AT001", description = "Cookie Acceptance Banner")
    public void cookieAcceptanceBanner(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        executeStep(1, testId, () -> {

            String text = labelComponent.getLabel(COOKIE_POLICY_BANNER_MESSAGE);
            Locator.FilterOptions filterOptions = new Locator.FilterOptions();
            filterOptions.setHasText(text);

            logger.trace("Verifying Cookie Acceptance Banner is visible");
            assertThat(page.locator("div").filter(filterOptions).nth(2)).isVisible("Cookie banner is not visible.");
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT002", description = "HomePage Parts are Visible")
    public void homepagePartsAreVisible(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        logger.trace("Verifying HomePage parts are visible");
        executeStep(2, testId, () -> {
            logger.trace("Verifying Header is visible");
            assertThat(page.locator("header#header>div>div")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Search Bar is visible");
            assertThat(page.locator("input[placeholder='Cerca']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying top banner is visible");
            assertThat(page.locator("div[data-name='hp-top-banner']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Promotions Carousel is visible");
            assertThat(page.locator("div[class^='PromotionsCarousel__SliderContainer']")).hasCount(2);
            logger.trace("Verifying News is visible");
            assertThat(page.locator("div[data-element-id='News']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Highlights are visible");
            assertThat(page.locator("div[data-name='highlights']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Best Sellers are visible");
            assertThat(page.locator("div[data-name='best-sellers']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Leonardo Carousel is visible");
            assertThat(page.locator("div[data-name='leonardo-carousel']")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);
            logger.trace("Verifying Footer is visible");
            assertThat(page.locator("footer#sticky-footer")).isAttached(Errors.ELEMENTS_NOT_VISIBLE);

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT003", description = "Brands Menu")
    public void brandsMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Opening Brands Menu");
            page.locator("span[data-element-id='MainNav_Brands']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible(Errors.ELEMENTS_NOT_VISIBLE);
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            var buttons = page.locator("div[class^='BrandMenu__Container-']").nth(0).locator("button").all();
            var descriptions = buttons.stream().map(button -> button.getAttribute("data-description")).toList();
            var sortedDescriptions = descriptions.stream().sorted().toList();

            logger.trace("Verifying Brands Menu is ordered");
            if (!descriptions.equals(sortedDescriptions)) {
                Context.getTest().getStep(3).setNote("Brands are not ordered", logger);
                return TestStatus.PASSED_WITH_MINOR;
            }

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT004", description = "Categories Menu")
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
            logger.trace("Verifying Categories Menu is ordered");
            var buttons = page.locator("div[class^='CategoriesMenu__Menu-']").nth(0).locator("button").all();
            logger.trace("Categories found: {}", buttons.size());
            var categories = buttons.stream().map(button -> button.getAttribute("data-element-id")).toList();
            var orderedCategories = List.of(
                    "MainNav_Products_Sunglasses",
                    "MainNav_Products_Eyeglasses",
                    "MainNav_Products_Smart_Glasses",
                    "MainNav_Products_GOGGLES_HELMETS",
                    "MainNav_Products_Lenses",
                    "MainNav_Products_ACCESSORIES",
                    "MainNav_Products_AFA"
            );

            logger.trace("Verifying Categories Menu is composed by {} elements", orderedCategories.size());
            assertEquals(categories.size(), orderedCategories.size(), "Categories Menu is not complete");

            logger.trace("Verifying Categories Menu is ordered");
            if (!categories.equals(orderedCategories)) {
                Context.getTest().getStep(3).setNote("Categories are not ordered", logger);
                return TestStatus.PASSED_WITH_MINOR;
            }
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT005", description = "Sunglasses Sub-Menu")
    public void sunglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Sunglasses Sub-Menu");
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
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT006", description = "Eyeglasses Sub-Menu")
    public void eyeglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Eyeglasses Sub-Menu");
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
            List<Locator> columns = submenu.all();
            assertNotEquals(columns.size(), 0, "No columns found in Eyeglasses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT007", description = "Smartglasses Sub-Menu")
    public void smartglassesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Smartglasses Sub-Menu");
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
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            assertThat(page.locator("button[data-element-id='MainNav_Menu_ViewAll']")).isVisible();
            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT008", description = "Goggles&Helmets Sub-Menu")
    public void gogglesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Goggles Sub-Menu");
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
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);

                logger.trace("Verifying column has View All button");
                assertThat(page.locator("button[data-element-id='MainNav_Products_GOGGLES_HELMETS_MENU_CATEGORY_ViewAll']")).isVisible(Errors.VIEW_ALL_NOT_VISIBLE);
            }

            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT009", description = "Lenses Sub-Menu")
    public void lensesSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Lenses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_Lenses";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            logger.trace("Clicking on Lenses Sub-Menu");
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            page.waitForSelector("div[class^='LensesMenu__Container-']>div");
            List<Locator> columns = page.locator("div[class^='LensesMenu__Container-']>div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Lenses Sub-Menu");

            for (int i = 0; i < columns.size(); i++) {
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT010", description = "Eyewear Accessories Sub-Menu")
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
    public void afaSubMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.debug("Open AFA Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            var MENU_ID = "MainNav_Products_AFA";
            var button = page.locator(String.format("button[data-element-id='%s']", MENU_ID));
            logger.trace("Clicking on AFA Sub-Menu");
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
                logger.trace("Verifying links in column {}", i);
                List<Locator> links = columns.get(i).locator("li").locator("span").all();
                assertNotEquals(links.size(), 0, "No links found in column " + i);
            }

            return TestStatus.PASSED;
        });

    }

    @Test(testName = "AT012", description = "Service Menu")
    public void serviceMenu(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Opening Service Menu");
            page.locator("button[data-element-id='MainNav_Services']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();
            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {
            List<Locator> columns = page.locator("div[class^='ServicesMenu__Menu']").locator(">div").all();
            assertNotEquals(columns.size(), 0, "No columns found in Service Menu");

            List<Locator> urls = columns.stream().flatMap(column -> column.locator("button").all().stream()).toList();
            List<String> LINK_MUST_CONTAIN = List.of(
                    "MainNav_Services_AFTERSALES_SECTION_Spareparts",
                    "MainNav_Services_AFTERSALES_SECTION_Warranty"
            );

            List<String> links = urls.stream().map(url -> url.getAttribute("data-element-id")).toList();
            return links.containsAll(LINK_MUST_CONTAIN) ? TestStatus.PASSED : TestStatus.PASSED_WITH_MINOR;
        });
    }

    @Test(testName = "AT020", description = "Leonardo Link")
    public void leonardoLink(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            Locator leonardoBtn = page.locator("//button[@data-element-id='MainNav_Leonardo']");
            logger.trace("Veryfing Leonardo link is visible in header");
            assertThat(leonardoBtn).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            logger.trace("Clicking on Leonardo link");
            Page leonardoPage = page.waitForPopup(new Page.WaitForPopupOptions().setTimeout(30000), leonardoBtn::click);
            leonardoPage.waitForLoadState(LoadState.LOAD);

            logger.trace("Verifying new page is opened");
            assertEquals(page.context().pages().size(), 2);

            Locator profileIcon = leonardoPage.locator("//button[@data-element-id='mainNav_account']");
            logger.trace("Verifyng SSO is working");
            assertThat(profileIcon).isVisible("SSO is not working");

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT021", description = "Search UPC")
    public void searchUPC(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        final String upc = "888392605245";

        executeStep(2, testId, () -> {
            Locator searchBtn = page.locator("button[data-element-id='MainNav_Search_SearchBox']");
            logger.trace("Clicking on Search Button");
            searchBtn.click();

            Locator searchInput = page.locator("input[data-element-id='MainNav_Search']");
            logger.trace("Verifying Search Input is visible");
            assertThat(searchInput).isVisible(Errors.ELEMENTS_NOT_VISIBLE);

            logger.trace("Filling Search Input with UPC {}", upc);
            searchInput.fill(upc);
            searchInput.press(Constants.KEYS.ENTER);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            logger.trace("Verifying Search Results Page is opened");
            page.waitForURL(String.format("**/search-results/%s", upc));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);
            String noResultLabel = labelComponent.getLabel(SEARCH_SORRY_NO_RESULTS);
            Locator noResults = page.locator("//p[contains(text(), '" + noResultLabel + "')]");
            logger.trace("Verifying Search Results are not empty");
            if (noResults.isVisible()) {
                logger.warn("No results found for UPC {}", upc);
                Context.getTest().getStep(2).setNote("No results found for UPC " + upc);
                return TestStatus.PASSED_WITH_MINOR;
            }

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT028", description = "HTML Landing pages contents")
    public void htmlLandingPagesContents(Method method) {

        String testId = initTestAndReturnId(method);
        executeStep(1, testId, () -> TestStatus.PASSED); // First step is always log with BO User (landInPage())

        executeStep(2, testId, () -> {
            logger.trace("Open Lenses Sub-Menu");
            page.locator("span[data-element-id='MainNav_Products']").click();
            assertThat(page.locator("div#menu-container>div")).isVisible();

            Locator button = page.locator("button[data-element-id='MainNav_Products_Lenses']");
            logger.trace("Clicking on Lenses Sub-Menu");
            button.click();
            assertThat(page.locator("div[class^='CategoryItem__Submenu']").first()).isVisible();

            Locator firstColumn = page.locator("div[class^='LensesColumnEssilor_']").first();
            Locator firstLink = firstColumn.locator("a").first();
            firstLink.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertTrue(page.url().contains("/digital-catalog"));

            return TestStatus.PASSED;
        });
    }

}
