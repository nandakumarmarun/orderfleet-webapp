package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesSummaryAchievmentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesSummaryAchievmentDTO;

/**
 * Service Implementation for managing SalesSummaryAchievment.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
@Service
@Transactional
public class SalesSummaryAchievmentServiceImpl implements SalesSummaryAchievmentService {

	private final Logger log = LoggerFactory.getLogger(SalesSummaryAchievmentServiceImpl.class);

	@Inject
	private SalesSummaryAchievmentRepository salesSummaryAchievmentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Override
	public void saveSalesSummaryAchievments(List<SalesSummaryAchievmentDTO> achievmentDTOs) {
		List<SalesSummaryAchievment> salesSummaryAchievments = new ArrayList<>();
		for (SalesSummaryAchievmentDTO salesSummaryAchievmentDTO : achievmentDTOs) {
			// save new
			SalesSummaryAchievment salesSummaryAchievment = null;
			if (salesSummaryAchievmentDTO.getPid().equals("no")) {
				salesSummaryAchievment = new SalesSummaryAchievment();
				salesSummaryAchievment.setPid(SalesSummaryAchievmentService.PID_PREFIX + RandomUtil.generatePid());
				Optional<EmployeeProfile> optionalEmployeeProfile = employeeProfileRepository
						.findOneByPid(salesSummaryAchievmentDTO.getEmployeePid());
				if (optionalEmployeeProfile.isPresent()) {
					salesSummaryAchievment.setUser(optionalEmployeeProfile.get().getUser());
				}
				salesSummaryAchievment.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
				salesSummaryAchievment.setSalesTargetGroup(salesTargetGroupRepository
						.findOneByPid(salesSummaryAchievmentDTO.getSalesTargetGroupPid()).get());
				salesSummaryAchievment.setAmount(salesSummaryAchievmentDTO.getAmount());
				salesSummaryAchievment.setAchievedDate(salesSummaryAchievmentDTO.getAchievedDate());
				Optional<Location> optionalLocation = locationRepository
						.findOneByPid(salesSummaryAchievmentDTO.getLocationPid());
				if (optionalLocation.isPresent()) {
					salesSummaryAchievment.setLocation(optionalLocation.get());
				}
			} else {
				// update
				salesSummaryAchievment = salesSummaryAchievmentRepository
						.findOneByPid(salesSummaryAchievmentDTO.getPid()).get();
				salesSummaryAchievment.setAmount(salesSummaryAchievmentDTO.getAmount());
				salesSummaryAchievment.setAchievedDate(salesSummaryAchievmentDTO.getAchievedDate());
			}
			salesSummaryAchievments.add(salesSummaryAchievment);
		}
		salesSummaryAchievmentRepository.save(salesSummaryAchievments);
	}

	/**
	 * Get one salesSummaryAchievment by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public SalesSummaryAchievmentDTO findOne(Long id) {
		log.debug("Request to get SalesSummaryAchievment : {}", id);
		SalesSummaryAchievment salesSummaryAchievment = salesSummaryAchievmentRepository.findOne(id);
		SalesSummaryAchievmentDTO categoryDTO = new SalesSummaryAchievmentDTO(salesSummaryAchievment);
		return categoryDTO;
	}

	/**
	 * Get one salesSummaryAchievment by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesSummaryAchievmentDTO> findOneByPid(String pid) {
		log.debug("Request to get SalesSummaryAchievment by pid : {}", pid);
		return salesSummaryAchievmentRepository.findOneByPid(pid).map(salesSummaryAchievment -> {
			SalesSummaryAchievmentDTO categoryDTO = new SalesSummaryAchievmentDTO(salesSummaryAchievment);
			return categoryDTO;
		});
	}

	/**
	 * Delete the salesSummaryAchievment by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete SalesSummaryAchievment : {}", pid);
		salesSummaryAchievmentRepository.findOneByPid(pid).ifPresent(salesSummaryAchievment -> {
			salesSummaryAchievmentRepository.delete(salesSummaryAchievment.getId());
		});
	}

	@Override
	public SalesSummaryAchievment findByUserPidAndSalesTargetGroupPidAndDateBetweenAndLocationPid(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate, String locationPid) {
		return salesSummaryAchievmentRepository.findByUserPidAndSalesTargetGroupPidAndAchievedDateBetweenAndLocationPid(
				userPid, salesTargetGroupPid, startDate, endDate, locationPid);
	}

	@Override
	public List<SalesSummaryAchievment> findByUserPidAndSalesTargetGroupPidAndDateBetween(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate) {
		return salesSummaryAchievmentRepository.findByUserPidAndSalesTargetGroupPidAndAchievedDateBetween(userPid,
				salesTargetGroupPid, startDate, endDate);
	}

}
