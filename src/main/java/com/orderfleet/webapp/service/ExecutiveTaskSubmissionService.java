package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;

/**
 * Service Interface for managing ExecutiveTaskSubmission.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
public interface ExecutiveTaskSubmissionService {

	ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmission(
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO);

	List<ExecutiveTaskSubmissionDTO> findExecutiveTaskSubmissions(String activityPid, String accountPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	TaskSubmissionResponse updateInventoryVoucher(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO);

	TaskSubmissionResponse updateAccountingVoucher(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO);

	TaskSubmissionResponse updateDynamicDocument(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO);

	TaskSubmissionResponse updateDynamicDocument(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO);

	ExecutiveTaskSubmissionDTO findExecutiveTaskSubmissionsByReferenceDocuments(String accountPid, String documentPid, LocalDate startDate, LocalDate endDate, String processStatus);
	
	ExecutiveTaskSubmissionDTO findExecutiveTaskSubmissionsByTerritotyAndReferenceDocument(String territoryPid, String refDocumentPid, LocalDate startDate, LocalDate endDate, String processStatus);

	void saveTPExecutiveTaskSubmission(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO, User user);

	ExecutiveTaskSubmissionTransactionWrapper updationExecutiveTaskExecution(
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO);
}
