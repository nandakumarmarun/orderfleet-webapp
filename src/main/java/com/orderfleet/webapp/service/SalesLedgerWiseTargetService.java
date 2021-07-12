package com.orderfleet.webapp.service;

import java.time.LocalDate;

import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesTargetDTO;

public interface SalesLedgerWiseTargetService {

	String PID_PREFIX = "SLWT-";

	SalesLedgerWiseTargetDTO saveMonthlyTarget(SalesLedgerWiseMonthlyTargetDTO monthlyTargetDTO, LocalDate firstDateMonth,
			LocalDate lastDateMonth);

}
