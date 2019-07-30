package com.orderfleet.webapp.service;

import java.time.LocalDate;

import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupMonthlyTargetDTO;

public interface ProductGroupLocationTargetService {

	String PID_PREFIX = "PGLT-";

	ProductGroupLocationTargetDTO saveMonthlyTarget(ProductGroupMonthlyTargetDTO monthlyTargetDTO,
			LocalDate firstDateMonth, LocalDate lastDateMonth);

}
