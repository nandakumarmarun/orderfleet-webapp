package com.orderfleet.webapp.service;

import java.time.LocalDate;

import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptTargetDTO;

public interface UserWiseReceiptTargetService {

	String PID_PREFIX = "USWRT-";

	UserWiseReceiptTargetDTO saveMonthlyTarget(UserWiseReceiptMonthlyTargetDTO monthlyTargetDTO,
			LocalDate firstDateMonth, LocalDate lastDateMonth);

}
