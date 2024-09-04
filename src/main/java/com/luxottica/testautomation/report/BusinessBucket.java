package com.luxottica.testautomation.report;

import com.luxottica.testautomation.report.enums.ReportType;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class BusinessBucket {

    private static final Logger logger = LoggerFactory.getLogger(BusinessBucket.class);
    private static final String MAP_FILE = "playwright/report/test-mapping.xlsx";

    private Map<String, String> businessIds;

    public String getBusinessId(String uniqueId) {
        String businessId = businessIds.getOrDefault(uniqueId, Strings.EMPTY);
        if (businessId.isEmpty()) {
            throw new IllegalArgumentException(String.format("Test %s not found in mapping file!", uniqueId));
        }

        return businessId;
    }

    @PostConstruct
    private void loadTestTuples() {

        logger.info("Loading business test references...");

        this.businessIds = new HashMap<>();
        try {
            File template = new File(MAP_FILE);
            FileInputStream stream = new FileInputStream(template);
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                Pattern idPattern = Pattern.compile("AT\\d{3}"); // ID followed by 3 digits

                Cell uniqueIdCell = row.getCell(0);
                Cell weekIdCell = row.getCell(1);
                Cell coreIdCell = row.getCell(2);

                String uniqueId = uniqueIdCell.getStringCellValue();

                if (uniqueId == null || !idPattern.matcher(uniqueId).matches()) {
                    continue;
                }

                String value = null;
                ReportType reportType = PlaywrightTestUtils.getReportType();
                if (reportType == ReportType.WEEK) {
                    value = weekIdCell.getStringCellValue();
                } else if (reportType == ReportType.CORE) {
                    value = coreIdCell.getStringCellValue();
                }
                businessIds.putIfAbsent(uniqueId, value);
            }

            workbook.close();
            stream.close();
        } catch (IOException e) {
            logger.error("Failed to load business test references!", e);
            throw new RuntimeException("Failed to load business test references!", e);
        }
    }
}
