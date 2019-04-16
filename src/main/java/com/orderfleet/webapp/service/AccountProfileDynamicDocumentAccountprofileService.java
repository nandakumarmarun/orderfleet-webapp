package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountProfileDynamicDocumentAccountprofileDTO;

/**
 * Service Interface for managing AccountProfileDynamicDocumentAccountprofile.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
public interface AccountProfileDynamicDocumentAccountprofileService {

	List<AccountProfileDynamicDocumentAccountprofileDTO> save(
			List<AccountProfileDynamicDocumentAccountprofileDTO> documentAccountprofileDTOs);

	AccountProfileDynamicDocumentAccountprofileDTO update(
			AccountProfileDynamicDocumentAccountprofileDTO accountProfileDTO);

	List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByCompany();

	List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByCompanyMapped();

	List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByDocumentPidAndFormPid(String documentPid,
			String formPid);
}
