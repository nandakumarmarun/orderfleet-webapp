package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PerformanceReportMobile;
import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PerformanceReportMobileRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PerformanceReportMobileService;
import com.orderfleet.webapp.service.SalesTargetReportSettingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PerformanceReportMobileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingSalesTargetBlockDTO;

/**
 * Service Implementation for managing SalesTargetReportSetting.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
@Service
@Transactional
public class SalesTargetReportSettingServiceImpl implements SalesTargetReportSettingService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetReportSettingServiceImpl.class);

	@Inject
	private SalesTargetReportSettingRepository salesTargetReportSettingRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetBlockRepository salesTargetBlockRepository;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	@Inject
	private PerformanceReportMobileRepository performanceReportMobileRepository;

	@Inject
	private PerformanceReportMobileService performanceReportMobileService;

	/**
	 * Save a salesTargetReportSetting.
	 * 
	 * @param salesTargetReportSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetReportSettingDTO save(SalesTargetReportSettingDTO salesTargetReportSettingDTO) {
		log.debug("Request to save SalesTargetReportSetting : {}", salesTargetReportSettingDTO);

		SalesTargetReportSetting salesTargetReportSetting = new SalesTargetReportSetting();
		salesTargetReportSetting.setName(salesTargetReportSettingDTO.getName());
		salesTargetReportSetting.setAccountWiseTarget(salesTargetReportSettingDTO.getAccountWiseTarget());
		salesTargetReportSetting.setTargetPeriod(salesTargetReportSettingDTO.getTargetPeriod());
		salesTargetReportSetting.setTargetType(salesTargetReportSettingDTO.getTargetType());
		salesTargetReportSetting.setMonthlyAverageWise(salesTargetReportSettingDTO.getMonthlyAverageWise());
		salesTargetReportSetting.setTargetSettingType(salesTargetReportSettingDTO.getTargetSettingType());

		// set pid
		salesTargetReportSetting.setPid(SalesTargetReportSettingService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		salesTargetReportSetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesTargetReportSetting = salesTargetReportSettingRepository.save(salesTargetReportSetting);

		log.debug("Request to save SalesTargetReportSetting : {}", salesTargetReportSettingDTO);
		if (salesTargetReportSettingDTO.getMobileUIName() != null) {
			PerformanceReportMobile mobile = new PerformanceReportMobile();
			mobile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
			mobile.setMobileUINames(salesTargetReportSettingDTO.getMobileUIName());
			mobile.setSalesTargetReportSetting(salesTargetReportSetting);
			performanceReportMobileRepository.save(mobile);
		}

		SalesTargetReportSettingDTO result = new SalesTargetReportSettingDTO(salesTargetReportSetting);
		return result;
	}

	/**
	 * Update a salesTargetReportSetting.
	 * 
	 * @param salesTargetReportSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetReportSettingDTO update(SalesTargetReportSettingDTO salesTargetReportSettingDTO) {
		log.debug("Request to Update SalesTargetReportSetting : {}", salesTargetReportSettingDTO);

		return salesTargetReportSettingRepository.findOneByPid(salesTargetReportSettingDTO.getPid())
				.map(salesTargetReportSetting -> {
					salesTargetReportSetting.setName(salesTargetReportSettingDTO.getName());
					salesTargetReportSetting.setAccountWiseTarget(salesTargetReportSettingDTO.getAccountWiseTarget());
					salesTargetReportSetting.setTargetPeriod(salesTargetReportSettingDTO.getTargetPeriod());
					salesTargetReportSetting.setTargetType(salesTargetReportSettingDTO.getTargetType());
					salesTargetReportSetting.setMonthlyAverageWise(salesTargetReportSettingDTO.getMonthlyAverageWise());
					salesTargetReportSetting.setTargetSettingType(salesTargetReportSettingDTO.getTargetSettingType());
					
					salesTargetReportSetting = salesTargetReportSettingRepository.save(salesTargetReportSetting);
					SalesTargetReportSettingDTO result = new SalesTargetReportSettingDTO(salesTargetReportSetting);

					if (salesTargetReportSettingDTO.getMobileUIName() == null) {
						performanceReportMobileRepository.deleteByCompanyIdAndSalesTargetReportSettingPid(
								SecurityUtils.getCurrentUsersCompanyId(), salesTargetReportSetting.getPid());
					} else {
						// saving performance report mobile
						PerformanceReportMobile performanceReportMobile = new PerformanceReportMobile();
						Optional<PerformanceReportMobile> performanceReportMobileOPtinal = performanceReportMobileRepository
								.findOneBySalesTargetReportSettingPid(salesTargetReportSetting.getPid());
						if (performanceReportMobileOPtinal.isPresent()) {
							performanceReportMobile = performanceReportMobileOPtinal.get();
							performanceReportMobile.setMobileUINames(salesTargetReportSettingDTO.getMobileUIName());
							performanceReportMobileRepository.save(performanceReportMobile);
							result.setMobileUIName(performanceReportMobile.getMobileUINames());
						} else {
							performanceReportMobile
									.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
							performanceReportMobile.setMobileUINames(salesTargetReportSettingDTO.getMobileUIName());
							performanceReportMobile.setSalesTargetReportSetting(salesTargetReportSetting);
							performanceReportMobile = performanceReportMobileRepository.save(performanceReportMobile);
							result.setMobileUIName(performanceReportMobile.getMobileUINames());
						}
					}

					return result;
				}).orElse(null);
	}

	/**
	 * Get all the salesTargetReportSettings.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetReportSettingDTO> findAllByCompany() {
		log.debug("Request to get all SalesTargetReportSettings");
		List<SalesTargetReportSetting> salesTargetReportSettingList = salesTargetReportSettingRepository
				.findAllByCompanyId();
		List<SalesTargetReportSettingDTO> result = salesTargetReportSettingList.stream()
				.map(SalesTargetReportSettingDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one salesTargetReportSetting by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetReportSettingDTO> findOneByPid(String pid) {
		log.debug("Request to get SalesTargetReportSetting by pid : {}", pid);
		return salesTargetReportSettingRepository.findOneByPid(pid).map(salesTargetReportSetting -> {
			Optional<PerformanceReportMobileDTO> mobileDTO = performanceReportMobileService
					.findOneBySalesTargetReportSettingPid(salesTargetReportSetting.getPid());
			SalesTargetReportSettingDTO salesTargetReportSettingDTO = new SalesTargetReportSettingDTO(
					salesTargetReportSetting);
			if (mobileDTO.isPresent()) {
				salesTargetReportSettingDTO.setMobileUIName(mobileDTO.get().getMobileUINames());
			}
			return salesTargetReportSettingDTO;
		});
	}

	/**
	 * Get one salesTargetReportSetting by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetReportSettingDTO> findByName(String name) {
		log.debug("Request to get SalesTargetReportSetting by name : {}", name);
		return salesTargetReportSettingRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(salesTargetReportSetting -> {
					SalesTargetReportSettingDTO salesTargetReportSettingDTO = new SalesTargetReportSettingDTO(
							salesTargetReportSetting);
					return salesTargetReportSettingDTO;
				});
	}

	/**
	 * Delete the salesTargetReportSetting by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete SalesTargetReportSetting : {}", pid);
		salesTargetReportSettingRepository.findOneByPid(pid).ifPresent(salesTargetReportSetting -> {
			salesTargetReportSettingRepository.delete(salesTargetReportSetting.getId());
		});
	}

	@Override
	public void saveAssignedTargetBlocks(String pid,
			List<SalesTargetReportSettingSalesTargetBlockDTO> assignedTargetBlocks) {
		List<SalesTargetReportSettingSalesTargetBlock> strsSalesTargetBlocks = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		salesTargetReportSettingRepository.findOneByPid(pid).ifPresent(strs -> {
			for (SalesTargetReportSettingSalesTargetBlockDTO salesTargetReportSettingSalesTargetBlockDTO : assignedTargetBlocks) {
				salesTargetBlockRepository
						.findOneByPid(salesTargetReportSettingSalesTargetBlockDTO.getSalesTargetBlockPid())
						.ifPresent(stb -> {
							SalesTargetReportSettingSalesTargetBlock strsStb = new SalesTargetReportSettingSalesTargetBlock();
							strsStb.setCompany(company);
							strsStb.setSalesTargetReportSetting(strs);
							strsStb.setSalesTargetBlock(stb);
							strsStb.setSortOrder(salesTargetReportSettingSalesTargetBlockDTO.getSortOrder());
							strsSalesTargetBlocks.add(strsStb);
						});
			}
		});
		salesTargetReportSettingSalesTargetBlockRepository.deleteBySalesTargetReportSettingPid(pid);
		salesTargetReportSettingSalesTargetBlockRepository.save(strsSalesTargetBlocks);
	}

	
	/**
	 * Get all the salesTargetReportSettings.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetReportSettingDTO> findAllByCompanyIdAndTargetSettingType(BestPerformanceType targetSettingType) {
		log.debug("Request to get all SalesTargetReportSettings");
		List<SalesTargetReportSetting> salesTargetReportSettingList = salesTargetReportSettingRepository
				.findAllByCompanyIdAndTargetSettingType(targetSettingType);
		List<SalesTargetReportSettingDTO> result = salesTargetReportSettingList.stream()
				.map(SalesTargetReportSettingDTO::new).collect(Collectors.toList());
		return result;
	}
}
