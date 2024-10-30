package com.luxottica.testautomation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.components.labels.LabelComponent;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.LoadState;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.luxottica.testautomation.constants.Label.*;
import static com.luxottica.testautomation.extensions.MyPlaywrightAssertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class AccountTest extends BaseTest {

    @Test(testName = "AT022", description = "Order History - Filters")
    public void orderHistoryFilters(Method method) {

        String testId = initTestAndReturnId(method);
        String orderNumber = getAdditionalData("orderNumber", String.class);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/account/order-history");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Filling the Order Number input");
            Locator searchInput = page.locator("#OrderNumber");
            searchInput.fill(orderNumber);

            logger.trace("Clicking on the Search button");
            Locator searchButton = page.locator("//button[@data-element-id='OrderHistory_Search']").last();
            page.waitForResponse("**/history**", () -> {
                logger.trace("Clicking on the Search button");
                searchButton.click();
            });

            logger.trace("Waiting for the results to load and checking if the order is displayed");
            List<Locator> results = page.locator("//div[@data-element-id='OrderHistory_Results']").locator("div[class^='OrderHistoryOrder__Container']").all();
            assertEquals(1, results.size());

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT023", description = "Order Detail - Page structure")
    public void orderHistoryPageStructure(Method method) {

        String testId = initTestAndReturnId(method);
        String orderNumber = getAdditionalData("orderNumber", String.class);

        executeStep(1, testId, () -> {
            page.navigate(getURL() + "/account/order-history");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Filling the Order Number input");
            Locator searchInput = page.locator("#OrderNumber");
            searchInput.fill(orderNumber);

            Response response = page.waitForResponse("**/history**", () -> {
                logger.trace("Clicking on the Search button");
                Locator searchButton = page.locator("//button[@data-element-id='OrderHistory_Search']").last();
                searchButton.click();
            });

            JsonObject jsonResponse = JsonParser.parseString(new String(response.body())).getAsJsonObject();
            jsonResponse = jsonResponse.getAsJsonObject("data");
            JsonArray results = jsonResponse.getAsJsonArray("histories");

            logger.trace("Waiting for the results to load and checking if the order is displayed");
            assertEquals(results.size(), 1);

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);
            final String fullDetailsLabel = labelComponent.getLabel(ORDER_HISTORY_FULL_DETAILS);
            logger.trace("Clicking on the Full Details button");
            Locator fullDetailsButton = page.locator(String.format("//button[contains(text(), '%s')]", fullDetailsLabel)).first();
            fullDetailsButton.click();

            logger.trace("Check if user lands on the order detail page");
            page.waitForURL(Pattern.compile(".*order-history.*"));

            return TestStatus.PASSED;
        });

        executeStep(3, testId, () -> {

            logger.trace("Verify that header is displayed");
            Locator header = page.locator("#header");
            assertThat(header).isVisible("Header is not displayed");

            logger.trace("Verify that breadcrumb is displayed");
            Locator breadcrumb = page.locator("div[class^='BreadcrumbSection__Section-']").first();
            assertThat(breadcrumb).isVisible("Breadcrumb is not displayed");

            logger.trace("Verify that page title is displayed");
            Locator pageTitle = page.locator("div[class^='MyAccountPage__Header-']").first();
            assertThat(pageTitle).isVisible("Page title is not displayed");

            logger.trace("Verify that left shoulder navigation menu is displayed");
            Locator leftShoulderNav = page.locator("div[class^='OrderDetailsSummary__BackCta-']").first();
            assertThat(leftShoulderNav).isVisible("Left shoulder navigation menu is not displayed");

            LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

            logger.trace("Verify that Export CSV button is displayed");
            final String exportCsvLabel = labelComponent.getLabel(ORDER_DETAILS_EXPORT_CSV);
            Locator exportCsvButton = page.locator(String.format("//button[contains(text(), '%s')]", exportCsvLabel)).first();
            assertThat(exportCsvButton).isVisible("Export CSV button is not displayed");

            logger.trace("Verify that Print button is displayed");
            final String printLabel = labelComponent.getLabel(ORDER_DETAILS_PRINT);
            Locator printButton = page.locator(String.format("//button[contains(text(), '%s')]", printLabel)).first();
            assertThat(printButton).isVisible("Print button is not displayed");

            logger.trace("Verify order number title is displayed");
            Locator orderNumberTitle = page.locator("div[class^='OrderDetailsSummary__Title-']").first();
            assertThat(orderNumberTitle).isVisible("Order number title is not displayed");

            logger.trace("Verify that \"Your order\" section is displayed");
            Locator yourOrderSection = page.locator("div[class^='OrderSummaryHeader__BoxSection-']").first();
            assertThat(yourOrderSection).isVisible("Your order section is not displayed");

            logger.trace("Verify that \"Order details\" section is displayed");
            Locator orderDetailsSection = page.locator("div[class^='OrderDetailsTable__Section-']").first();
            assertThat(orderDetailsSection).isVisible("Order details section is not displayed");

            logger.trace("Verify that footer is displayed");
            Locator footer = page.locator("#sticky-footer");
            assertThat(footer).isVisible("Footer is not displayed");

            return TestStatus.PASSED;
        });
    }

    @Test(testName = "AT029", description = "Massive order upload")
    public void massiveOrderUpload(Method method) {

        String testId = initTestAndReturnId(method);
        LabelComponent labelComponent = InjectionUtil.getBean(LabelComponent.class);

        Map<String, String> fileStruct = (Map<String, String>) getAdditionalData("file", Map.class);

        AtomicReference<File> fileToUpload = new AtomicReference<>();

        executeStep(1, testId, () -> {

            String extension = fileStruct.get("extension");
            String data = fileStruct.get("data");

            byte[] fileBased64 = Base64.getDecoder().decode(data);

            File file = new File("massiveOrderUpload." + extension);
            file.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBased64);
            }

            if (file.getTotalSpace() == 0) {
                throw new IllegalArgumentException("File is empty");
            }

            fileToUpload.set(file);
            assertNotNull("File to upload is not set", fileToUpload.get());

            page.navigate(getURL() + "/account/order-upload");
            return TestStatus.PASSED;
        });

        executeStep(2, testId, () -> {

            logger.trace("Uploading the file");
            Locator fileInput = page.locator("input[id='hidden-upload']");
            fileInput.setInputFiles(fileToUpload.get().toPath());

            logger.trace("Checking if the file is uploaded");
            Locator uploadedFile = page.locator("//div[@direction='column']/div[contains(@class, 'Chip')]").first();
            assertThat(uploadedFile).isVisible("File is not uploaded");

            Locator uploadButtonContainer = page.locator("div[class^='OrderUploadStep1__UploadButtonContainer-']").first();

            final String confirmLabel = labelComponent.getLabel(FILE_INPUT_UPLOAD_BUTTON);
            Locator confirmButton = uploadButtonContainer.locator("//button[contains(text(), '" + confirmLabel + "')]").first();

            Response response = page.waitForResponse("**/upload", () -> {
                logger.trace("Confirming the upload");
                confirmButton.click();
            });

            Locator progressRefresh = page.locator("div[class='progress-container']").locator("svg").first();

            logger.trace("Waiting for the upload to finish");
            int MAX_ITERATE = 60;
            int currentProgress = 0;
            for (int i = 0; i < MAX_ITERATE; i++) {

                if (currentProgress == 100) {
                    break;
                }

                logger.debug("Current progress: {}", currentProgress);
                Response massive = page.waitForResponse("**/massive", () -> {
                    logger.trace("Refreshing the progress");
                    progressRefresh.click();
                });
                HttpStatus status = HttpStatus.valueOf(massive.status());
                if (status.isError()) {
                    Context.getTest().getStep(2).setNote("Error during the upload: " + status.getReasonPhrase(), logger);
                    return TestStatus.FAILED;
                }
                JsonObject jsonResponse = JsonParser.parseString(new String(massive.body())).getAsJsonObject();
                String valueString= jsonResponse.get("data").getAsJsonObject().get("massiveOrders").getAsJsonArray().get(0).getAsJsonObject().get("progress").getAsString();
                currentProgress = Integer.parseInt(valueString);
                Thread.sleep(1000);
            }

            if (currentProgress != 100) {
                Context.getTest().getStep(2).setNote("Upload did not finish in " + MAX_ITERATE + " check", logger);
                Context.getTest().setFailed(true);
                return TestStatus.TO_BE_RETESTED;
            }

            return TestStatus.PASSED;
        });

        final String step2TitleLabel = labelComponent.getLabel("ORDER_UPLOAD_STEP2_TITLE");
        Locator step2Title = page.locator(String.format("//button[contains(text(), '%s')]", step2TitleLabel)).first();

        executeStep(3, testId, () -> {
            logger.trace("Checking if next step is displayed");
            assertThat(step2Title).isVisible("Step 2 title is not displayed");
            return TestStatus.PASSED;
        });

        executeStep(4, testId, () -> {
            logger.trace("Clicking on the next step");
            step2Title.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return TestStatus.PASSED;
        });

        executeStep(5, testId, () -> {
            logger.trace("Checking if the upload is successful");
            Locator message = page.locator("h2[class^='CustomText__Text-']").first();
            final String refusedLabel = labelComponent.getLabel("CHECK_UPLOADS_ERRORROWS_TITLE");
            assertThat(message).not().containsText(refusedLabel);

            return TestStatus.PASSED;
        });

        fileToUpload.get().delete();
    }
}
