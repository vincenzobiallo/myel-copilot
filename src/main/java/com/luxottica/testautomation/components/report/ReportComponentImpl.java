package com.luxottica.testautomation.components.report;

import com.luxottica.testautomation.components.report.enums.ReportType;
import com.luxottica.testautomation.components.report.enums.TestStatus;
import com.luxottica.testautomation.components.report.models.TestCase;
import com.luxottica.testautomation.components.report.models.TestStep;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.luxottica.testautomation.components.report.enums.ReportColumn.*;
import static com.luxottica.testautomation.utils.ExcelUtils.*;

public class ReportComponentImpl implements ReportComponent {

    private static final Logger logger = LoggerFactory.getLogger(ReportComponent.class);
    private final ReportType reportType = PlaywrightTestUtils.getReportType();

    @Getter
    private Map<String, TestCase> tests;

    @PostConstruct
    private void loadTestCases() throws IOException {

        logger.info("Loading test cases from template {}...", reportType);
        this.tests = new HashMap<>();

        File template = getTemplate();
        FileInputStream stream = new FileInputStream(template);
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheetAt(0);

        Pattern idPattern = Pattern.compile("ID\\d{3}"); // ID followed by 3 digits

        for (Row row : sheet) {
            if (isCellNotNull(row, ID) && idPattern.matcher(getCell(row, ID).getStringCellValue()).matches()) {
                TestCase test;
                // Se il test è già stato creato lo recupero altrimenti lo creo
                if (tests.get(getCell(row, ID).getStringCellValue()) != null) {
                    test = tests.get(getCell(row, ID).getStringCellValue());
                } else {
                    test = new TestCase();

                    test.setId(getCell(row, ID).getStringCellValue());
                    test.setInternalId(getCell(row, UNIQUE_ID).getStringCellValue());
                    test.setMacroArea(getCell(row, MACRO_AREA).getStringCellValue());
                    test.setArea(getCell(row, AREA).getStringCellValue());
                    test.setSummary(getCell(row, SUMMARY).getStringCellValue());

                    tests.put(getCell(row, ID).getStringCellValue(), test);
                }

                if (tests.get(getCell(row, ID).getStringCellValue()) != null && isCellNotNull(row, STEP)) { // Se la cella non è vuota => è uno step
                    int stepNumber = (int) getCell(row, STEP).getNumericCellValue();
                    if (stepNumber >= 1) { // Se lo step non è valido è un intestazione di test
                        TestStep step = new TestStep(stepNumber);
                        test.addStep(step);
                    }
                }
            }
        }

        workbook.close();
        stream.close();
        logger.info("{} test cases loaded!", tests.size());
    }

    public void generateReport() {
        // Genero un nome univoco per il report (e.g. Report_NRT_2024-05-06_<timestamp>.xlsx)
        LocalDateTime now = LocalDateTime.now();
        String reportName = "Report_NRT_" + now.toString().replace(":", "-").replace(".", "-") + ".xlsx";
        String reportPath = "playwright/report/results/";

        // check if report folder exists
        File reportFolder = new File(reportPath);
        if (!reportFolder.exists()) {
            reportFolder.mkdirs();
        }
        reportPath += reportName;

        try {
            File template = getTemplate();
            File report = new File(reportPath);
            Files.copy(template.toPath(), report.toPath());

            try (FileInputStream in = new FileInputStream(report); Workbook workbook = new XSSFWorkbook(in); FileOutputStream out = new FileOutputStream(reportPath)) {

                Sheet sheet = workbook.getSheetAt(0);
                Pattern idPattern = Pattern.compile("ID\\d{3}"); // ID followed by 3 digits

                for (Row row : sheet) {

                    boolean isValidTestIdCell = isCellNotNull(row, ID) && idPattern.matcher(getCell(row, ID).getStringCellValue()).matches();
                    boolean isValidStepCell = isCellNotNull(row, STEP) && ((int) getCell(row, STEP).getNumericCellValue()) >= 1;

                    if (isValidTestIdCell) {
                        TestCase test = tests.get(getCell(row, ID).getStringCellValue());

                        if (!isValidStepCell) { // Intestazione del test
                            getCell(row, DOOR).setCellValue(test.getExecutor());
                            if (isCellNull(row, UNIQUE_ID)) {
                                row.createCell(UNIQUE_ID.offset);
                            }
                            getCell(row, UNIQUE_ID).setCellValue(test.getInternalId());
                        } else { // Steps del test
                            TestStep step = test.getStep((int) getCell(row, STEP).getNumericCellValue());

                            TestStatus status = step.getStatus();
                            getCell(row, STATUS).setCellValue(status.getStatus());

                            if (!step.getNote().equals(Strings.EMPTY)) {
                                getCell(row, NOTE).setCellValue(step.getNote());
                            }
                        }
                    }
                }

                workbook.setForceFormulaRecalculation(true);
                workbook.write(out);
            }
        } catch (IOException e) {
            logger.error("Error while generating report!", e);
            throw new RuntimeException("Error while generating report!", e);
        }
    }

    private File getTemplate() {
        return switch (reportType) {
            case CORE -> new File(TEMPLATE_CORE);
            case WEEK -> new File("playwright/report/template_week.xlsx");
        };
    }
}
