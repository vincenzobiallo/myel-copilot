package com.luxottica.testautomation.components.bucket;

import com.luxottica.testautomation.components.bucket.dto.BucketDataDTO;
import com.luxottica.testautomation.components.bucket.enums.StoresColumn;
import com.luxottica.testautomation.components.report.enums.ReportType;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.utils.PlaywrightTestUtils;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.luxottica.testautomation.utils.ExcelUtils.*;
import static com.luxottica.testautomation.components.bucket.enums.BucketColumn.*;

public class BucketComponentImpl implements BucketComponent {

    private static final Logger logger = LoggerFactory.getLogger(BucketComponent.class);

    private Map<String, BucketDataDTO> references;

    @Override
    public BucketDataDTO getBucketData(String internalTestId) {
        return references.get(internalTestId);
    }

    @Override
    public String getBusinessTestIdFromInternal(String internalTestId) {

        BucketDataDTO dto = getBucketData(internalTestId);
        if (dto == null) {
            throw new IllegalArgumentException(String.format("Test %s not found in mapping file!", internalTestId));
        }

        ReportType reportType = PlaywrightTestUtils.getReportType();
        return switch (reportType) {
            case WEEK -> dto.getWeekId();
            case CORE -> dto.getCoreId();
        };
    }

    @PostConstruct
    private void loadTestTuples() {

        logger.info("Mapping internal reference to business ones...");

        this.references = new HashMap<>();
        try {
            File template = new File(MAPPER);
            FileInputStream stream = new FileInputStream(template);
            Workbook workbook = new XSSFWorkbook(stream);

            Sheet mappingSheet = workbook.getSheet(SHEET_MAPPING);
            logger.debug("Loading {} sheet", mappingSheet.getSheetName());
            loadMapping(mappingSheet);

            Sheet storeSheet = workbook.getSheet(SHEET_STORES);
            logger.debug("Loading {} sheet", storeSheet.getSheetName());
            loadStores(storeSheet);

            workbook.close();
            stream.close();
        } catch (IOException e) {
            logger.error("Failed to load business test references!", e);
            throw new RuntimeException("Failed to load business test references!", e);
        } finally {
            logger.info("{} test references loaded!", references.size());
        }
    }

    private void loadMapping(Sheet sheet) {
        for (Row row : sheet) {

            Pattern idPattern = Pattern.compile("AT\\d{3}"); // ID followed by 3 digits

            if (isCellNull(row, ID) || !idPattern.matcher(getCell(row, ID).getStringCellValue()).matches()) {
                continue;
            }

            BucketDataDTO dto = new BucketDataDTO();
            dto.setId(getCell(row, ID).getStringCellValue());

            if (isCellNotNull(row, WEEK_ID)) {
                dto.setWeekId(getCell(row, WEEK_ID).getStringCellValue());
            }
            if (isCellNotNull(row, CORE_ID)) {
                dto.setCoreId(getCell(row, CORE_ID).getStringCellValue());
            }

            dto.setDescription(getCell(row, DESCRIPTION).getStringCellValue());

            String impersonificateValue = getCell(row, IMPERSONIFICATE).getStringCellValue();
            dto.setImpersonificate(impersonificateValue.equalsIgnoreCase(Y));

            dto.setUser(getCell(row, USER).getStringCellValue());

            dto.setStore(getCell(row, STORE).getStringCellValue());

            references.putIfAbsent(getCell(row, ID).getStringCellValue(), dto);
        }
    }

    private void loadStores(Sheet sheet) {
        boolean header = true;
        for (Row row : sheet) {

            if (header) {
                header = false;
                continue;
            }

            if (isCellNull(row, StoresColumn.COUNTRY)) {
                continue;
            }

            MyelStore store = new MyelStore(
                    getCell(row, StoresColumn.COUNTRY).getStringCellValue(),
                    getCell(row, StoresColumn.STORE_IDENTIFIER).getStringCellValue(),
                    String.valueOf(getCell(row, StoresColumn.STORE_CODE).getNumericCellValue()).split("\\.")[0],
                    getCell(row, StoresColumn.STORE_SLUG).getStringCellValue(),
                    getCell(row, StoresColumn.LOCALE).getStringCellValue()
            );

            MyelStore.STORES.add(store);
        }
    }
}
