package com.luxottica.testautomation.dto.multidoor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserContextResponseDTO {

    private List<DoorDTO> multiDoors;
    private DoorDTO activeDoor;
    private String username;

}
