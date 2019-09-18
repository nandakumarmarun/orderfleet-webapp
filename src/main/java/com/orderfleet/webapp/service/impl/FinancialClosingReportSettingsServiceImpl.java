package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.FinancialClosingReportSettings;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.FinancialClosingReportSettingsRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FinancialClosingReportSettingsService;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportSettingsDTO;

@Service
@Transactional
public class FinancialClosingReportSettingsServiceImpl implements FinancialClosingReportSettingsService{

	private final Logger log = LoggerFactory.getLogger(FinancialClosingReportSettingsServiceImpl.class);
	
	@Inject
	private FinancialClosingReportSettingsRepository financialClosingReportSettingsRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Override
	public FinancialClosingReportSettingsDTO save(FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO) {
		FinancialClosingReportSettings financialClosingReportSettings=new FinancialClosingReportSettings();
		financialClosingReportSettings.setDebitCredit(financialClosingReportSettingsDTO.getDebitCredit());
		financialClosingReportSettings.setPaymentMode(financialClosingReportSettingsDTO.getPaymentMode());
		financialClosingReportSettings.setSortOrder(financialClosingReportSettingsDTO.getSortOrder());
		financialClosingReportSettings.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		financialClosingReportSettings.setDocument(documentRepository.findOneByPid(financialClosingReportSettingsDTO.getDocumentPid()).get());
		financialClosingReportSettings=financialClosingReportSettingsRepository.save(financialClosingReportSettings);
		FinancialClosingReportSettingsDTO result=new FinancialClosingReportSettingsDTO(financialClosingReportSettings);
		return result;
	}

	@Override
	public FinancialClosingReportSettingsDTO update(FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO) {
		FinancialClosingReportSettings financialClosingReportSettings=financialClosingReportSettingsRepository.findOne(financialClosingReportSettingsDTO.getId());
		financialClosingReportSettings.setDebitCredit(financialClosingReportSettingsDTO.getDebitCredit());
		financialClosingReportSettings.setPaymentMode(financialClosingReportSettingsDTO.getPaymentMode());
		financialClosingReportSettings.setSortOrder(financialClosingReportSettingsDTO.getSortOrder());
		financialClosingReportSettings.setDocument(documentRepository.findOneByPid(financialClosingReportSettingsDTO.getDocumentPid()).get());
		financialClosingReportSettings=financialClosingReportSettingsRepository.save(financialClosingReportSettings);
		FinancialClosingReportSettingsDTO result=new FinancialClosingReportSettingsDTO(financialClosingReportSettings);
		return result;
	}

	@Override
	public List<FinancialClosingReportSettingsDTO> findAllByCompany() {
		List<FinancialClosingReportSettings>financialClosingReportSettings=financialClosingReportSettingsRepository.findAllByCompanyId();
		List<FinancialClosingReportSettingsDTO>financialClosingReportSettingsDTOs=new ArrayList<>();
		for(FinancialClosingReportSettings financialClosingReportSetting:financialClosingReportSettings) {
			FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=new FinancialClosingReportSettingsDTO(financialClosingReportSetting);
			financialClosingReportSettingsDTOs.add(financialClosingReportSettingsDTO);
		}
		return financialClosingReportSettingsDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public FinancialClosingReportSettingsDTO findOne(Long id) {
		log.debug("Request to get FinancialClosingReportSettingsDTO by id : {}", id);
		FinancialClosingReportSettings financialClosingReportSettings=financialClosingReportSettingsRepository.findOne(id);
		FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=new FinancialClosingReportSettingsDTO(financialClosingReportSettings);
		return financialClosingReportSettingsDTO;
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete FinancialClosingReportSettingsDTO : {}", id);
		financialClosingReportSettingsRepository.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<FinancialClosingReportSettingsDTO> findByCompanyIdAndDocumentPid(
			String documentPid) {
		log.debug("Request to get FinancialClosingReportSettingsDTO  : {}", documentPid);
		return financialClosingReportSettingsRepository.findByCompanyIdAndDocumentPid(SecurityUtils.getCurrentUsersCompanyId(), documentPid)
				.map(financialClosingReportSettings -> {
					FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=new FinancialClosingReportSettingsDTO(financialClosingReportSettings);
					return financialClosingReportSettingsDTO;
				});
	}

	@Override
	public List<FinancialClosingReportSettingsDTO> findAllByPaymentMode(PaymentMode paymentMode) {
		List<FinancialClosingReportSettings>financialClosingReportSettings=financialClosingReportSettingsRepository.findAllByPaymentMode(paymentMode);
		List<FinancialClosingReportSettingsDTO>financialClosingReportSettingsDTOs=new ArrayList<>();
		for(FinancialClosingReportSettings financialClosingReportSetting:financialClosingReportSettings) {
			FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=new FinancialClosingReportSettingsDTO(financialClosingReportSetting);
			financialClosingReportSettingsDTOs.add(financialClosingReportSettingsDTO);
		}
		return financialClosingReportSettingsDTOs;
	}

	@Override
	public List<FinancialClosingReportSettingsDTO> findAllByPaymentModeExcludePettyCash() {
		List<PaymentMode>paymentModes=new ArrayList<>();
		paymentModes.add(PaymentMode.Cheque);
		paymentModes.add(PaymentMode.Bank);
		paymentModes.add(PaymentMode.Cash);
		paymentModes.add(PaymentMode.CREDIT);
		List<FinancialClosingReportSettings>financialClosingReportSettings=financialClosingReportSettingsRepository.findAllByPaymentModeExcludePettyCash(paymentModes);
		List<FinancialClosingReportSettingsDTO>financialClosingReportSettingsDTOs=new ArrayList<>();
		for(FinancialClosingReportSettings financialClosingReportSetting:financialClosingReportSettings) {
			FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=new FinancialClosingReportSettingsDTO(financialClosingReportSetting);
			financialClosingReportSettingsDTOs.add(financialClosingReportSettingsDTO);
		}
		return financialClosingReportSettingsDTOs;
	}
}
