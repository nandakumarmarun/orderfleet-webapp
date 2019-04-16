package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;

public interface AccountingVoucherUIService {

	List<AccountProfileDTO> findByAccountProfilesByDocument(String documentPid);
	
	List<AccountProfileDTO> findToAccountProfilesByDocument(String documentPid);
	
	void saveAccountingVoucher(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO);
}
