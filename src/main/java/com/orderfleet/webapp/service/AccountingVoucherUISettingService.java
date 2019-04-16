package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.AccountingVoucherUISettingDTO;

public interface AccountingVoucherUISettingService {

		/**
		 * Save a accountingVoucherUISettings.
		 * 
		 * @param accountingVoucherUISettingsDTO
		 *            the entity to save
		 * @return the persisted entity
		 */
		AccountingVoucherUISettingDTO save(AccountingVoucherUISettingDTO accountingVoucherUISettingsDTO);
		
		/**
		 * Update a accountingVoucherUISettings.
		 * 
		 * @param accountingVoucherUISettingsDTO
		 *            the entity to update
		 * @return the persisted entity
		 */
		AccountingVoucherUISettingDTO update(AccountingVoucherUISettingDTO accountingVoucherUISettingsDTO);
		
		/**
		 * Get all the accountingVoucherUISettingss of a company.
		 * 
		 * @return the list of entities
		 */
		List<AccountingVoucherUISettingDTO> findAllByCompany();
		
		/**
		 * Get the "id" accountingVoucherUISettings.
		 * 
		 * @param id
		 *            the id of the entity
		 * @return the entity
		 */
		AccountingVoucherUISettingDTO findOne(Long id);
		
		/**
		 * Delete the "id" accountingVoucherUISettings.
		 * 
		 * @param id
		 *            the id of the entity
		 */
		void delete(Long id);
		
		/**
		 * Get the AccountingVoucherUISetting by "name".
		 * 
		 * @param name
		 *            the name of the entity
		 * @return the entity
		 */
		Optional<AccountingVoucherUISettingDTO> findByName(String name);
}
