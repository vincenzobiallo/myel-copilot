package com.luxottica.testautomation.report;

import com.luxottica.testautomation.report.enums.ReportType;
import com.luxottica.testautomation.report.enums.TestStatus;
import com.luxottica.testautomation.report.models.TestCase;
import com.luxottica.testautomation.report.models.TestStep;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class Report {

    private static final Logger logger = LoggerFactory.getLogger(Report.class);
    private static final String TEMPLATE_CORE = "playwright/report/template_core.xlsx";

    private final ReportType reportType = PlaywrightTestUtils.getReportType();

    @Getter
    private Map<String, TestCase> tests;

    @PostConstruct
    private void initTests() throws IOException {

        logger.info("Initializing tests...");
        this.tests = new HashMap<>();

        File template = getTemplate();
        FileInputStream stream = new FileInputStream(template);
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheetAt(0);

        Pattern idPattern = Pattern.compile("ID\\d{3}"); // ID followed by 3 digits

        for (Row row : sheet) {
            Cell TEST_ID = row.getCell(0);
            Cell MACRO_AREA = row.getCell(1);
            Cell AREA = row.getCell(2);
            Cell SUMMARY = row.getCell(3);
            Cell STEP = row.getCell(5);
            Cell UNIQUE_ID = row.getCell(14);

            if (TEST_ID != null && idPattern.matcher(TEST_ID.getStringCellValue()).matches()) {

                TestCase test;
                if (Objects.nonNull(tests.get(TEST_ID.getStringCellValue()))) {
                    // Se il test è già stato creato, lo recupero
                    test = tests.get(TEST_ID.getStringCellValue());
                } else {
                    // Altrimenti lo creo
                    test = new TestCase();
                    String id = TEST_ID.getStringCellValue();
                    String macroArea = MACRO_AREA.getStringCellValue();
                    String area = AREA.getStringCellValue();
                    String summary = SUMMARY.getStringCellValue();
                    String internalId = UNIQUE_ID.getStringCellValue();

                    test.setId(id);
                    test.setInternalId(internalId);
                    test.setMacroArea(macroArea);
                    test.setArea(area);
                    test.setSummary(summary);

                    tests.put(id, test);
                }

                if (Objects.nonNull(tests.get(TEST_ID.getStringCellValue())) && Objects.nonNull(STEP)) { // Se la cella non è vuota => è uno step
                    int stepNumber = (int) STEP.getNumericCellValue();
                    if (stepNumber >= 1) { // Se lo step non è valido è un intestazione di test
                        TestStep step = new TestStep(stepNumber);
                        test.addStep(step);
                    }
                }
            }
        }

        workbook.close();
        stream.close();
        logger.info("Tests initialized!");
    }

    public void generateReport() throws IOException {
        // Genero un nome univoco per il report (e.g. Report_NRT_2024-05-06_<timestamp>.xlsx)
        LocalDateTime now = LocalDateTime.now();
        String reportName = "Report_NRT_" + now.toString().replace(":", "-").replace(".", "-") + ".xlsx";
        String reportPath = "playwright/report/results/" + reportName;

        File template = getTemplate();
        File report = new File(reportPath);
        Files.copy(template.toPath(), report.toPath());

        try (FileInputStream in = new FileInputStream(report); Workbook workbook = new XSSFWorkbook(in); FileOutputStream out = new FileOutputStream(reportPath)) {

            Sheet sheet = workbook.getSheetAt(0);
            Pattern idPattern = Pattern.compile("ID\\d{3}"); // ID followed by 3 digits

            for (Row row : sheet) {
                Cell TEST_ID = row.getCell(0);
                Cell STEP = row.getCell(5);
                Cell STATUS = row.getCell(9);
                Cell NOTE = row.getCell(10);
                Cell DOOR_USER = row.getCell(11);
                Cell UNIQUE_ID = row.getCell(14);

                boolean isValidTestIdCell = Objects.nonNull(TEST_ID) && idPattern.matcher(TEST_ID.getStringCellValue()).matches();
                boolean isValidStepCell = Objects.nonNull(STEP) && ((int) STEP.getNumericCellValue()) >= 1;

                if (isValidTestIdCell) {
                    TestCase test = tests.get(TEST_ID.getStringCellValue());

                    if (!isValidStepCell) { // Intestazione del test
                        DOOR_USER.setCellValue(test.getExecutor());
                        if (UNIQUE_ID == null) {
                            UNIQUE_ID = row.createCell(14);
                        }
                        UNIQUE_ID.setCellValue(test.getInternalId());
                    } else { // Steps del test
                        TestStep step = test.getStep((int) STEP.getNumericCellValue());

                        TestStatus status = step.getStatus();
                        STATUS.setCellValue(status.getStatus());

                        if (!step.getNote().equals(Strings.EMPTY)) {
                            NOTE.setCellValue(step.getNote());
                        }
                    }
                }
            }

            workbook.setForceFormulaRecalculation(true);
            workbook.write(out);
        }
    }

    private File getTemplate() {
        return switch (reportType) {
            case CORE -> new File(TEMPLATE_CORE);
            case WEEK -> new File("playwright/report/template_week.xlsx");
        };
    }
}
