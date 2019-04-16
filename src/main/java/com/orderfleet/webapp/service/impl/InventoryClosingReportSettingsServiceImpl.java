package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.InventoryClosingReportSettings;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.InventoryClosingReportSettingGroupRepository;
import com.orderfleet.webapp.repository.InventoryClosingReportSettingsRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryClosingReportSettingsService;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingsDTO;

@Service
@Transactional
public class InventoryClosingReportSettingsServiceImpl implements InventoryClosingReportSettingsService{
	
private final Logger log = LoggerFactory.getLogger(InventoryClosingReportSettingsServiceImpl.class);
	
	@Inject
	private InventoryClosingReportSettingsRepository inventoryClosingReportRepository;
	
	@Inject
	private InventoryClosingReportSettingGroupRepository inventoryClosingReportSettingGroupRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private DocumentRepository documentRepository;

	@Override
	public InventoryClosingReportSettingsDTO save(InventoryClosingReportSettingsDTO inventoryClosingReportDTO) {
		InventoryClosingReportSettings inventoryClosingReport=new InventoryClosingReportSettings();
		inventoryClosingReport.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		inventoryClosingReport.setInventoryClosingReportSettingGroup(inventoryClosingReportSettingGroupRepository.findOneByPid(inventoryClosingReportDTO.getInventoryClosingReportSettingGroupPid()).get());
		inventoryClosingReport.setDocument(documentRepository.findOneByPid(inventoryClosingReportDTO.getDocumentPid()).get());
		inventoryClosingReport=inventoryClosingReportRepository.save(inventoryClosingReport);
		InventoryClosingReportSettingsDTO result=new InventoryClosingReportSettingsDTO(inventoryClosingReport);
		return result;
	}

	@Override
	public InventoryClosingReportSettingsDTO update(InventoryClosingReportSettingsDTO inventoryClosingReportDTO) {
		InventoryClosingReportSettings inventoryClosingReport=inventoryClosingReportRepository.findOne(inventoryClosingReportDTO.getId());
		inventoryClosingReport.setDocument(documentRepository.findOneByPid(inventoryClosingReportDTO.getDocumentPid()).get());
		inventoryClosingReport.setInventoryClosingReportSettingGroup(inventoryClosingReportSettingGroupRepository.findOneByPid(inventoryClosingReportDTO.getInventoryClosingReportSettingGroupPid()).get());
		inventoryClosingReport=inventoryClosingReportRepository.save(inventoryClosingReport);
		InventoryClosingReportSettingsDTO result=new InventoryClosingReportSettingsDTO(inventoryClosingReport);
		return result;
	}

	@Override
	public List<InventoryClosingReportSettingsDTO> findAllByCompany() {
		List<InventoryClosingReportSettings>inventoryClosingReport=inventoryClosingReportRepository.findAllByCompanyId();
		List<InventoryClosingReportSettingsDTO>inventoryClosingReportDTOs=new ArrayList<>();
		for(InventoryClosingReportSettings financialClosingReportSetting:inventoryClosingReport) {
			InventoryClosingReportSettingsDTO inventoryClosingReportDTO=new InventoryClosingReportSettingsDTO(financialClosingReportSetting);
			inventoryClosingReportDTOs.add(inventoryClosingReportDTO);
		}
		return inventoryClosingReportDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryClosingReportSettingsDTO findOne(Long id) {
		log.debug("Request to get InventoryClosingReportDTO by id : {}", id);
		InventoryClosingReportSettings inventoryClosingReport=inventoryClosingReportRepository.findOne(id);
		InventoryClosingReportSettingsDTO inventoryClosingReportDTO=new InventoryClosingReportSettingsDTO(inventoryClosingReport);
		return inventoryClosingReportDTO;
	}

	@Override
	public Optional<InventoryClosingReportSettingsDTO> findByCompanyIdAndDocumentPid(
			String documentPid) {
		log.debug("Request to get InventoryClosingReportDTO  : {}", documentPid);
		return inventoryClosingReportRepository.findByCompanyIdAndDocumentPid(SecurityUtils.getCurrentUsersCompanyId(), documentPid)
				.map(inventoryClosingReport -> {
					InventoryClosingReportSettingsDTO inventoryClosingReportDTO=new InventoryClosingReportSettingsDTO(inventoryClosingReport);
					return inventoryClosingReportDTO;
				});
	}

	
	@Override
	public void delete(Long id) {
		log.debug("Request to delete InventoryClosingReportDTO : {}", id);
		inventoryClosingReportRepository.delete(id);
	}
	

}
