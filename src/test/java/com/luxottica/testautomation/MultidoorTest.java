package com.luxottica.testautomation;

import com.luxottica.testautomation.components.bucket.BucketComponent;
import com.luxottica.testautomation.components.bucket.dto.DataTestDTO;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.constants.Constants;
import com.luxottica.testautomation.dto.multidoor.*;
import com.luxottica.testautomation.exceptions.MySkipException;
import com.luxottica.testautomation.security.Context;
import com.luxottica.testautomation.security.dto.BFFResponse;
import com.luxottica.testautomation.utils.InjectionUtil;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class MultidoorTest extends BaseTest {

    @BeforeMethod
    @Override
    void createContextAndPage(Method method) {
        super.createContextAndPage(method);

        List<DoorDTO> selectedDoors = getSelectedDoors();
        BucketComponent bucket = InjectionUtil.getBean(BucketComponent.class);

        DataTestDTO data = bucket.getBucketData(method.getAnnotation(Test.class).testName());
        String secondDoor = (String) data.getAdditionalData().get("secondDoor");

        if (selectedDoors.size() == 1) {
            logger.debug("Selecting two doors for user {}", Context.getUser().getUsername());
            List<DoorDTO> availableDoors = getDoorGroup();
            if (availableDoors.size() < 2) {
                throw new MySkipException("Not enough doors available for user " + Context.getUser().getUsername());
            }
            boolean success = selectDoors(List.of(
                    MultidoorRequestDataDTO.builder().customer(availableDoors.get(0).getOrgentityName()).selected(true).build(),
                    MultidoorRequestDataDTO.builder()
                            .customer(availableDoors.stream().filter(a -> a.getOrgentityName().equals(secondDoor)).findFirst().orElse(availableDoors.get(1)).getOrgentityName())
                            .selected(true).build()
            ));

            Assert.assertTrue(success, "Failed to select doors for user " + Context.getUser().getUsername());
        }
    }

    @AfterClass
    @Override
    void closeBrowser() {
        List<DoorDTO> selectedDoors = getSelectedDoors();
        if (selectedDoors.size() > 1) {
            boolean success = selectDoors(List.of(
                    MultidoorRequestDataDTO.builder().customer(selectedDoors.get(0).getOrgentityName()).selected(false).build()
            ));

            Assert.assertTrue(success, "Failed to deselect doors for user " + Context.getUser().getUsername());
        }
        super.closeBrowser();
    }

    protected List<DoorDTO> getSelectedDoors() {
        Config config = InjectionUtil.getBean(Config.class);
        BFFResponse bffResponse = getBffClient().setRequest(
                config.getUserContext()
                    .replace(Constants.ENDPOINTS.STORE_IDENTIFIER, getUser().getStore()),
                HttpMethod.GET
                );

        UserContextResponseDTO response = bffResponse.getData(UserContextResponseDTO.class);
        List<DoorDTO> selectedDoors = response.getMultiDoors();
        logger.info("{} doors selected for user {}", selectedDoors.size(), response.getUsername());

        return selectedDoors;
    }

    protected List<DoorDTO> getDoorGroup() {

        Config config = InjectionUtil.getBean(Config.class);
        BFFResponse bffResponse = getBffClient().setRequest(
                config.getDoors()
                    .replace(Constants.ENDPOINTS.STORE_IDENTIFIER, getUser().getStore())
                    .replace(Constants.ENDPOINTS.LOCALE, getUser().getLocale()),
                HttpMethod.GET,
                new LinkedMultiValueMap<>(Map.of(
                        "size", List.of("10"),
                        "page", List.of("1")))
                );

        MultidoorListResponseDTO response = bffResponse.getData(MultidoorListResponseDTO.class);
        logger.info("{} doors found for user {}", response.getTotalDoors(), getUser().getUsername());
        return response.getDoors();
    }

    protected boolean selectDoors(List<MultidoorRequestDataDTO> selectedDoors) {
        Config config = InjectionUtil.getBean(Config.class);
        BFFResponse bffResponse = getBffClient().setRequest(
                config.getDoors()
                        .replace(Constants.ENDPOINTS.STORE_IDENTIFIER, getUser().getStore())
                        .replace(Constants.ENDPOINTS.LOCALE, getUser().getLocale()),
                HttpMethod.POST,
                MultidoorRequestDTO.builder().doors(selectedDoors).build());

        return bffResponse.getStatus().is2xxSuccessful();
    }
}
