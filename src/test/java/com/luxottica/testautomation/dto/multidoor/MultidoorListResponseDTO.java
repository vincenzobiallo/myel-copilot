package com.luxottica.testautomation.dto.multidoor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class MultidoorListResponseDTO {

    private Integer page;
    private Integer size;
    private Integer totalDoors;
    private Integer totalPage;
    private List<DoorDTO> doors;

}
