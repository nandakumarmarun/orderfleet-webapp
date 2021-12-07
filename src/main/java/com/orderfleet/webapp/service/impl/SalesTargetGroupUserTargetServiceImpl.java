package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

/**
 * Service Implementation for managing SalesTargetGroupUserTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Service
@Transactional
public class SalesTargetGroupUserTargetServiceImpl implements SalesTargetGroupUserTargetService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupUserTargetServiceImpl.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetGroupUserTargetMapper salesTargetGroupUserTargetMapper;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	/**
	 * Save a salesTargetGroupUserTarget.
	 * 
	 * @param salesTargetGroupUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetGroupUserTargetDTO save(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) {
		log.debug("Request to save SalesTargetGroupUserTarget : {}", salesTargetGroupUserTargetDTO);

		// set pid
		salesTargetGroupUserTargetDTO.setPid(SalesTargetGroupUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		SalesTargetGroupUserTarget salesTargetGroupUserTarget = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetDTOToSalesTargetGroupUserTarget(salesTargetGroupUserTargetDTO);
		// set company
		salesTargetGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesTargetGroupUserTarget = salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
		SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
		return result;
	}

	@Override
	public SalesTargetGroupUserTargetDTO saveMonthlyTarget(SalesMonthlyTargetDTO monthlyTargetDTO, LocalDate startDate,
			LocalDate endDate) {
		SalesTargetGroupUserTarget salesTargetGroupUserTarget = new SalesTargetGroupUserTarget();
		// set pid
		salesTargetGroupUserTarget.setPid(SalesTargetGroupUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		salesTargetGroupUserTarget.setSalesTargetGroup(
				salesTargetGroupRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupPid()).get());
		// set company
		salesTargetGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesTargetGroupUserTarget.setFromDate(startDate);
		salesTargetGroupUserTarget.setToDate(endDate);
		salesTargetGroupUserTarget.setAmount(monthlyTargetDTO.getAmount());
		salesTargetGroupUserTarget.setVolume(monthlyTargetDTO.getVolume());
		if (monthlyTargetDTO.getUserPid() != null) {
			salesTargetGroupUserTarget.setUser(userRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get());
		}

		if (monthlyTargetDTO.getAccountProfilePid() != null) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
			salesTargetGroupUserTarget.setAccountProfile(
					accountProfileRepository.findOneByPid(monthlyTargetDTO.getAccountProfilePid()).get());
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate1 = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

			salesTargetGroupUserTarget.setAccountWiseTarget(true);
		}

		salesTargetGroupUserTarget = salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
		SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
		return result;
	}

	/**
	 * Update a salesTargetGroupUserTarget.
	 * 
	 * @param salesTargetGroupUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetGroupUserTargetDTO update(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) {
		log.debug("Request to Update SalesTargetGroupUserTarget : {}", salesTargetGroupUserTargetDTO);

		return salesTargetGroupUserTargetRepository.findOneByPid(salesTargetGroupUserTargetDTO.getPid())
				.map(salesTargetGroupUserTarget -> {
					salesTargetGroupUserTarget.setAmount(salesTargetGroupUserTargetDTO.getAmount());
					salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume());
					salesTargetGroupUserTarget.setFromDate(salesTargetGroupUserTargetDTO.getFromDate());
					salesTargetGroupUserTarget.setToDate(salesTargetGroupUserTargetDTO.getToDate());
					salesTargetGroupUserTarget.setSalesTargetGroup(salesTargetGroupRepository
							.findOneByPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid()).get());
					salesTargetGroupUserTarget
							.setUser(userRepository.findOneByPid(salesTargetGroupUserTargetDTO.getUserPid()).get());
					salesTargetGroupUserTarget = salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
					SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
							.salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the salesTargetGroupUserTargets.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<SalesTargetGroupUserTarget> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<SalesTargetGroupUserTarget> result = salesTargetGroupUserTargetRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the salesTargetGroupUserTargets.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupUserTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
				.findAllByCompanyId();
		List<SalesTargetGroupUserTargetDTO> result = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargetList);
		return result;
	}

	/**
	 * Get all the salesTargetGroupUserTargets.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<SalesTargetGroupUserTargetDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<SalesTargetGroupUserTarget> activities = salesTargetGroupUserTargetRepository.findAllByCompanyId(pageable);
		Page<SalesTargetGroupUserTargetDTO> result = new PageImpl<SalesTargetGroupUserTargetDTO>(
				salesTargetGroupUserTargetMapper.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(
						activities.getContent()),
				pageable, activities.getTotalElements());
		return result;
	}

	/**
	 * Get one salesTargetGroupUserTarget by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public SalesTargetGroupUserTargetDTO findOne(Long id) {
		log.debug("Request to get SalesTargetGroupUserTarget : {}", id);
		SalesTargetGroupUserTarget salesTargetGroupUserTarget = salesTargetGroupUserTargetRepository.findOne(id);
		SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
		return salesTargetGroupUserTargetDTO;
	}

	/**
	 * Get one salesTargetGroupUserTarget by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetGroupUserTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get SalesTargetGroupUserTarget by pid : {}", pid);
		return salesTargetGroupUserTargetRepository.findOneByPid(pid).map(salesTargetGroupUserTarget -> {
			SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetMapper
					.salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
			return salesTargetGroupUserTargetDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupUserTargetDTO> findByCurrentUserAndCurrentMonth() {

		LocalDate currentDate = LocalDate.now();

		LocalDate start = currentDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = currentDate.with(TemporalAdjusters.lastDayOfMonth());

		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
				.findByUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						user.get().getPid(), start, end);
		if (!salesTargetGroupUserTargetList.isEmpty()) {
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetMapper
					.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargetList);
			for (SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO : salesTargetGroupUserTargetDTOs) {
				// get sales Target Group documents
				Set<Long> documents = salesTargetGroupDocumentRepository
						.findDocumentIdsBySalesTargetGroupPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid());

				Set<Long> productProfiles = salesTargetGroupProductRepository
						.findProductIdBySalesTargetGroupPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid());

				if (!documents.isEmpty() && !productProfiles.isEmpty()) {
					// get achieved amount
					Double achievedAmount = inventoryVoucherDetailRepository
							.sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(
									salesTargetGroupUserTargetDTO.getUserPid(), documents, productProfiles,
									start.atTime(0, 0), end.atTime(23, 59));
					// set achieved amount
					if (achievedAmount != null) {
						double roundedAchievedAmt = new BigDecimal(achievedAmount.toString())
								.setScale(2, RoundingMode.HALF_UP).doubleValue();
						salesTargetGroupUserTargetDTO.setAchievedAmount(roundedAchievedAmt);
					}
				}
			}
			return salesTargetGroupUserTargetDTOs;
		}
		return null;
	}

	/**
	 * Delete the salesTargetGroupUserTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete SalesTargetGroupUserTarget : {}", pid);
		salesTargetGroupUserTargetRepository.findOneByPid(pid).ifPresent(salesTargetGroupUserTarget -> {
			salesTargetGroupUserTargetRepository.delete(salesTargetGroupUserTarget.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupUserTarget> findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate) {
		return salesTargetGroupUserTargetRepository.findUserAndSalesTargetGroupPidAndDateWiseDuplicate(userPid,
				salesTargetGroupPid, startDate, endDate);
	}

	@Override
	public SalesTargetGroupUserTargetDTO saveUpdateDaylyTarget(
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs) {
		List<SalesTargetGroupUserTarget> groupUserTargets = new ArrayList<>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		Optional<EmployeeProfile> opEmployee = employeeProfileRepository
				.findOneByPid(salesTargetGroupUserTargetDTOs.get(0).getUserPid());
		if (opEmployee.isPresent())

			for (SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO : salesTargetGroupUserTargetDTOs) {

				SalesTargetGroupUserTarget salesTargetGroupUserTarget = new SalesTargetGroupUserTarget();

				Optional<SalesTargetGroupUserTarget> opSalesTargetGroupUserTarget = salesTargetGroupUserTargetRepository
						.findOneByPid(salesTargetGroupUserTargetDTO.getPid());

				if (opSalesTargetGroupUserTarget.isPresent()) {
					salesTargetGroupUserTarget = opSalesTargetGroupUserTarget.get();
					salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume());
				} else {
					// set pid
					salesTargetGroupUserTarget
							.setPid(SalesTargetGroupUserTargetService.PID_PREFIX + RandomUtil.generatePid());
					salesTargetGroupUserTarget.setSalesTargetGroup(salesTargetGroupRepository
							.findOneByPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid()).get());
					// set company
					salesTargetGroupUserTarget.setCompany(company);
					salesTargetGroupUserTarget.setFromDate(salesTargetGroupUserTargetDTO.getFromDate());
					salesTargetGroupUserTarget.setToDate(salesTargetGroupUserTargetDTO.getToDate());
					salesTargetGroupUserTarget.setAmount(0);
					salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume());

					salesTargetGroupUserTarget.setUser(opEmployee.get().getUser());

					salesTargetGroupUserTarget.setAccountWiseTarget(false);
					salesTargetGroupUserTarget.setTargetFrequency(TargetFrequency.DAY);
				}
				groupUserTargets.add(salesTargetGroupUserTarget);
			}
		salesTargetGroupUserTargetRepository.save(groupUserTargets);
		// SalesTargetGroupUserTargetDTO result =
		// salesTargetGroupUserTargetMapper
		// .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(salesTargetGroupUserTarget);
		// return result;
		return null;
	}

	@Override
	public List<SalesTargetGroupUserTargetDTO> findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(
			String userPid, String salesTargetGroupPid, LocalDate startDate, LocalDate endDate,
			TargetFrequency targetFrequency) {
		log.debug(
				"Request to get SalesTargetGroupUserTarget findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise: {}");
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(userPid, salesTargetGroupPid, startDate,
						endDate, targetFrequency);
		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargets.stream()
				.map(SalesTargetGroupUserTargetDTO::new).collect(Collectors.toList());

		Comparator<SalesTargetGroupUserTargetDTO> salaryComparator = (o1, o2) -> o1.getFromDate()
				.compareTo(o2.getFromDate());
		salesTargetGroupUserTargetDTOs.sort(salaryComparator.reversed());

		return salesTargetGroupUserTargetDTOs;
	}

	@Override
	public List<SalesTargetGroupUserTargetDTO> findAllByCompanyIdAndTargetFrequencyAndDateBetween(
			TargetFrequency targetFrequency, LocalDate startDate, LocalDate endDate) {
		log.debug("Request to get SalesTargetGroupUserTarget findAllByCompanyIdAndTargetFrequency: {}");
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findAllByCompanyIdAndTargetFrequencyAndDateBetween(targetFrequency, startDate, endDate);
		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargets.stream()
				.map(SalesTargetGroupUserTargetDTO::new).collect(Collectors.toList());
		Comparator<SalesTargetGroupUserTargetDTO> salaryComparator = (o1, o2) -> o1.getFromDate()
				.compareTo(o2.getFromDate());
		salesTargetGroupUserTargetDTOs.sort(salaryComparator.reversed());
		return salesTargetGroupUserTargetDTOs;
	}

}