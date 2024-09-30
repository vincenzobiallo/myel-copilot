package com.luxottica.testautomation.dto.multidoor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class DoorDTO {

    private Long orgentityId;
    private String orgentityName;
    private String name;
    private String address1;
    private String city;
    private String state;
    private Integer itemCount;
    private Boolean stars;
    private String organization;

}
