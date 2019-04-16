package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportSettingsDTO;

public interface FinancialClosingReportSettingsService {

	/**
	 * Save a financialClosingReportSettings.
	 * 
	 * @param financialClosingReportSettingsDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	FinancialClosingReportSettingsDTO save(FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO);
	
	/**
	 * Update a financialClosingReportSettings.
	 * 
	 * @param financialClosingReportSettingsDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	FinancialClosingReportSettingsDTO update(FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO);
	
	/**
	 * Get all the financialClosingReportSettingss of a company.
	 * 
	 * @return the list of entities
	 */
	List<FinancialClosingReportSettingsDTO> findAllByCompany();
	
	/**
	 * Get the "id" financialClosingReportSettings.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	FinancialClosingReportSettingsDTO findOne(Long id);
	
	/**
	 * Get the financialClosingReportSettings .
	 * 
	 * @param documentType
	 *            the documentType of the entity
	 * @param documentPid
	 *            the documentPid of the entity
	 * @return the entity
	 */
	Optional<FinancialClosingReportSettingsDTO> findByCompanyIdAndDocumentPid(String documentPid);
	
	/**
	 * Delete the "id" financialClosingReportSettings.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
	
	/**
	 * Get the financialClosingReportSettings by "paymentMode".
	 * 
	 * @param paymentMode
	 *            the paymentMode of the entity
	 * @return the entity
	 */
	List<FinancialClosingReportSettingsDTO>findAllByPaymentMode(PaymentMode paymentMode);
	
	/**
	 * Get the financialClosingReportSettings by "paymentMode".
	 * 
	 * @param paymentMode
	 *            the paymentMode of the entity
	 * @return the entity
	 */
	List<FinancialClosingReportSettingsDTO>findAllByPaymentModeExcludePettyCash();
}
