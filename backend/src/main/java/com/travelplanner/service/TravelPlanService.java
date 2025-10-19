package com.travelplanner.service;

import com.travelplanner.param.TravelQueryParam;

public interface TravelPlanService {

    void addTravelPlan(TravelQueryParam travelQueryParam, Long userId);

}
