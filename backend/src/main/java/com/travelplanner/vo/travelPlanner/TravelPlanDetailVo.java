package com.travelplanner.vo.travelPlanner;

import com.travelplanner.dto.TravelPlanContentDto;
import lombok.Data;

import java.util.List;

@Data
public class TravelPlanDetailVo {

    Long id;

    String title;

    String query;

    List<TravelPlanContentDto> content;

}
