package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;

public interface InvoiceDetailsDenormalizedService {

    String PID_PREFIX = "IID-";

    void SaveExecutivetaskExecutionWithInventory(ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmissionDTO, User user);
}
