package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.api.dto.DayPlanResponse;
import com.orderfleet.webapp.web.rest.dto.ExecutiveDayPlanDTO;

/**
 * Service To handle ExecutiveDayPlan
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface ExecutiveDayPlanService {

	DayPlanResponse saveExecutiveDayPlan(ExecutiveDayPlanDTO executiveDayPlan);

}
