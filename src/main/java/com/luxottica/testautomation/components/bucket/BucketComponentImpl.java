package com.luxottica.testautomation.components.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luxottica.testautomation.components.bucket.dto.DataDTO;
import com.luxottica.testautomation.components.bucket.dto.DataTestDTO;
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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.luxottica.testautomation.utils.ExcelUtils.*;
import static com.luxottica.testautomation.components.bucket.enums.BucketColumn.*;

public class BucketComponentImpl implements BucketComponent {

    private static final Logger logger = LoggerFactory.getLogger(BucketComponent.class);

    private Map<String, DataTestDTO> references;

    @Override
    public DataTestDTO getBucketData(String internalTestId) {
        return references.get(internalTestId);
    }

    @Override
    public String getBusinessTestIdFromInternal(String internalTestId) {

        DataTestDTO dto = getBucketData(internalTestId);
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

            File dataFile = new File(DATA_FILE);
            ObjectMapper mapper = new ObjectMapper();
            DataDTO data = mapper.readValue(dataFile, DataDTO.class);

            logger.debug("Loading {} tests", data.getTests().size());
            data.getTests().stream().collect(Collectors.toMap(DataTestDTO::getId, Function.identity())).forEach(references::putIfAbsent);

            logger.debug("Loading {} stores", data.getStores().size());
            data.getStores().forEach(store -> MyelStore.STORES.add(
                    MyelStore.builder()
                            .countryName(store.getCountryName())
                            .storeIdentifier(store.getStoreIdentifier())
                            .storeCode(String.valueOf(store.getStoreCode()))
                            .storeSlug(store.getStoreSlug())
                            .locale(store.getLocale())
                            .langId(String.valueOf(store.getLangId()))
                            .build()));
        } catch (IOException e) {
            logger.error("Failed to load business test references!", e);
            throw new RuntimeException("Failed to load business test references!", e);
        } finally {
            logger.info("{} test references loaded!", references.size());
        }
    }
}
