package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;

/**
 * Service Interface for managing LedgerReportTPService.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */
public interface LedgerReportTPService {

	LedgerReportTPDTO save(LedgerReportTPDTO ledgerReportTPDTO);

	LedgerReportTPDTO update(LedgerReportTPDTO ledgerReportTPDTO);

	List<LedgerReportTPDTO> findAllByCompany();

	LedgerReportTPDTO findOneById(Long id);

	void delete(Long id);

	public List<LedgerReportTPDTO> findAllByCompanyIdAndTypeAndAccountProfilePid(String narration, String accountPid);


}
