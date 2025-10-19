package com.travelplanner.dto;

import lombok.Data;
import java.util.List;

@Data
public class TravelPlanDto {

    private String title;

    private List<TravelPlanContentDto> content;

}
