package com.luxottica.testautomation;

import com.luxottica.testautomation.components.report.enums.TestStatus;

import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.configuration.SearchConfig;
import com.luxottica.testautomation.dto.MultidoorRequestDTO;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.utils.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class MultidoorTest extends BaseTest {

    //@Test(testName = "AT016", description = "Process flow Standard warranty")
    public void processFlowStandardWarranty(Method method) {

        String testId = initTestAndReturnId(method);
        String plpPage = getURL() + "/plp/frames?PRODUCT_CATEGORY_FILTER=Sunglasses";
        page.navigate(plpPage);

        String categoryId = "3074457345616679685";
        MyelStore myelStore = MyelStore.fromStoreIdentifier(getUser().getStore());
        String langId = myelStore.getLangId();
        Integer pageSize = 100;
        Integer pageNumber = 1;
        Set<String> doors = new LinkedHashSet<>();
        doors.add("0001001081");
        doors.add("0001509900");

        executeStep(1, testId, () -> {

            startMultidoor();
            SearchConfig searchConfig = InjectionUtil.getBean(SearchConfig.class);

            String url = searchConfig.getBaseUrl().concat(searchConfig.getFindProductsByCategoryCarousel())
                    .replace("{storeIdentifier}", myelStore.getStoreCode())
                    .replace("{categoryId}", categoryId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("activeDoorId", "0001001081")
                    .queryParam("langId", langId)
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .queryParam("doorId", doors);

            url = builder.toUriString();
            BFFRequest bffRequest = new BFFRequest(Map.of(
                            "WCToken", Strings.EMPTY,
                            "WCTrustedToken", Strings.EMPTY));
            BFFResponse bffResponse = bffRequest.get(url);

            logger.debug("BFF Status: {}", bffResponse.getStatus());
            logger.debug("BFF Response: {}", bffResponse.toJsonObject());

            return TestStatus.PASSED;
        });
    }

    private boolean startMultidoor() {
        Config config = InjectionUtil.getBean(Config.class);
        String url = config.getBaseUrl().concat(config.getMultidoor());

        APIRequestContext context = RequestUtils.buildContext(Context.getPlaywright(), getUser().getUsername());

        url = url.replace("{storeIdentifier}", getUser().getStore()).replace("{locale}", getUser().getLocale());

        MultidoorRequestDTO payload = MultidoorRequestDTO.builder()
                .doors(new ArrayList<>(List.of(
                        MultidoorRequestDTO.MultdoorRequestDataDTO.builder()
                                .customer("0001001081")
                                .selected(true)
                                .build(),
                        MultidoorRequestDTO.MultdoorRequestDataDTO.builder()
                                .customer("0001509900")
                                .selected(true)
                                .build()
                )))
                .build();

        APIResponse response = context.post(url,
                RequestOptions.create()
                        .setData(payload)
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json"));

        logger.debug("Multidoor HTTP Status: {}", response.status());

        return Objects.requireNonNull(HttpStatus.resolve(response.status())).is2xxSuccessful();
    }
}
