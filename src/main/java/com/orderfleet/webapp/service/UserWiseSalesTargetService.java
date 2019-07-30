package com.orderfleet.webapp.service;

import java.time.LocalDate;

import com.orderfleet.webapp.web.rest.dto.UserWiseSalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesTargetDTO;

public interface UserWiseSalesTargetService {

	String PID_PREFIX = "USWST-";

	UserWiseSalesTargetDTO saveMonthlyTarget(UserWiseSalesMonthlyTargetDTO monthlyTargetDTO, LocalDate firstDateMonth,
			LocalDate lastDateMonth);

}
