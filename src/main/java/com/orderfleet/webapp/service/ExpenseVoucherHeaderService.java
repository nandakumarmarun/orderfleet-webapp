package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Interface for managing AccountingVoucherHeader.
 * 
 * @author Prashob Sasidharan
 * @since October 29, 2019
 */
public interface ExpenseVoucherHeaderService {

	String PID_PREFIX = "EXPVH-";

	

}
