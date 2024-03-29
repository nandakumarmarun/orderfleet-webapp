package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductGroupLocationTargetRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupLocationTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupMonthlyTargetDTO;

/**
 * Service Implementation for managing SalesTargetGroupUserTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Service
@Transactional
public class ProductGroupLocationTargetServiceImpl implements ProductGroupLocationTargetService {

	private final Logger log = LoggerFactory.getLogger(ProductGroupLocationTargetServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private ProductGroupLocationTargetRepository productGroupLocationTargetRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Override
	public ProductGroupLocationTargetDTO saveMonthlyTarget(ProductGroupMonthlyTargetDTO monthlyTargetDTO,
			LocalDate startDate, LocalDate endDate) {
		log.info("Save monthly target location wise");
		ProductGroupLocationTarget productGroupLocationTarget = new ProductGroupLocationTarget();
		// set pid
		productGroupLocationTarget.setPid(ProductGroupLocationTargetService.PID_PREFIX + RandomUtil.generatePid());
		productGroupLocationTarget
				.setProductGroup(productGroupRepository.findOneByPid(monthlyTargetDTO.getProductGroupPid()).get());
		// set company
		productGroupLocationTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productGroupLocationTarget.setFromDate(startDate);
		productGroupLocationTarget.setToDate(endDate);
		productGroupLocationTarget.setAmount(monthlyTargetDTO.getAmount());
		productGroupLocationTarget.setVolume(monthlyTargetDTO.getVolume());
		if (monthlyTargetDTO.getLocationPid() != null) {
			productGroupLocationTarget
					.setLocation(locationRepository.findOneByPid(monthlyTargetDTO.getLocationPid()).get());
		}

		productGroupLocationTarget = productGroupLocationTargetRepository.save(productGroupLocationTarget);
		ProductGroupLocationTargetDTO result = new ProductGroupLocationTargetDTO(productGroupLocationTarget);
		return result;
	}

	/**
	 * Save a salesTargetGroupUserTarget.
	 * 
	 * @param salesTargetGroupUserTargetDTO the entity to save
	 * @return the persisted entity
	 */
	/*
	 * @Override public SalesTargetGroupUserTargetDTO
	 * save(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) {
	 * log.debug("Request to save SalesTargetGroupUserTarget : {}",
	 * salesTargetGroupUserTargetDTO);
	 * 
	 * // set pid
	 * salesTargetGroupUserTargetDTO.setPid(SalesTargetGroupUserTargetService.
	 * PID_PREFIX + RandomUtil.generatePid()); SalesTargetGroupUserTarget
	 * salesTargetGroupUserTarget = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetDTOToSalesTargetGroupUserTarget(
	 * salesTargetGroupUserTargetDTO); // set company
	 * salesTargetGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils
	 * .getCurrentUsersCompanyId())); salesTargetGroupUserTarget =
	 * salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
	 * SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
	 * salesTargetGroupUserTarget); return result; }
	 * 
	 * @Override public SalesTargetGroupUserTargetDTO
	 * saveMonthlyTarget(SalesMonthlyTargetDTO monthlyTargetDTO, LocalDate
	 * startDate, LocalDate endDate) { SalesTargetGroupUserTarget
	 * salesTargetGroupUserTarget = new SalesTargetGroupUserTarget(); // set pid
	 * salesTargetGroupUserTarget.setPid(SalesTargetGroupUserTargetService.
	 * PID_PREFIX + RandomUtil.generatePid());
	 * salesTargetGroupUserTarget.setSalesTargetGroup(
	 * salesTargetGroupRepository.findOneByPid(monthlyTargetDTO.
	 * getSalesTargetGroupPid()).get()); // set company
	 * salesTargetGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils
	 * .getCurrentUsersCompanyId()));
	 * salesTargetGroupUserTarget.setFromDate(startDate);
	 * salesTargetGroupUserTarget.setToDate(endDate);
	 * salesTargetGroupUserTarget.setAmount(monthlyTargetDTO.getAmount());
	 * salesTargetGroupUserTarget.setVolume(monthlyTargetDTO.getVolume()); if
	 * (monthlyTargetDTO.getUserPid() != null) {
	 * salesTargetGroupUserTarget.setUser(userRepository.findOneByPid(
	 * monthlyTargetDTO.getUserPid()).get()); }
	 * 
	 * if (monthlyTargetDTO.getAccountProfilePid() != null) {
	 * salesTargetGroupUserTarget.setAccountProfile(
	 * accountProfileRepository.findOneByPid(monthlyTargetDTO.getAccountProfilePid()
	 * ).get()); salesTargetGroupUserTarget.setAccountWiseTarget(true); }
	 * 
	 * salesTargetGroupUserTarget =
	 * salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
	 * SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
	 * salesTargetGroupUserTarget); return result; }
	 * 
	 *//**
		 * Update a salesTargetGroupUserTarget.
		 * 
		 * @param salesTargetGroupUserTargetDTO the entity to update
		 * @return the persisted entity
		 */
	/*
	 * @Override public SalesTargetGroupUserTargetDTO
	 * update(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) {
	 * log.debug("Request to Update SalesTargetGroupUserTarget : {}",
	 * salesTargetGroupUserTargetDTO);
	 * 
	 * return salesTargetGroupUserTargetRepository.findOneByPid(
	 * salesTargetGroupUserTargetDTO.getPid()) .map(salesTargetGroupUserTarget -> {
	 * salesTargetGroupUserTarget.setAmount(salesTargetGroupUserTargetDTO.getAmount(
	 * ));
	 * salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume(
	 * )); salesTargetGroupUserTarget.setFromDate(salesTargetGroupUserTargetDTO.
	 * getFromDate());
	 * salesTargetGroupUserTarget.setToDate(salesTargetGroupUserTargetDTO.getToDate(
	 * )); salesTargetGroupUserTarget.setSalesTargetGroup(salesTargetGroupRepository
	 * .findOneByPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid()).get());
	 * salesTargetGroupUserTarget
	 * .setUser(userRepository.findOneByPid(salesTargetGroupUserTargetDTO.getUserPid
	 * ()).get()); salesTargetGroupUserTarget =
	 * salesTargetGroupUserTargetRepository.save(salesTargetGroupUserTarget);
	 * SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
	 * salesTargetGroupUserTarget); return result; }).orElse(null); }
	 * 
	 *//**
		 * Get all the salesTargetGroupUserTargets.
		 * 
		 * @param pageable the pagination information
		 * @return the list of entities
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public Page<SalesTargetGroupUserTarget>
	 * findAll(Pageable pageable) { log.debug("Request to get all Activities");
	 * Page<SalesTargetGroupUserTarget> result =
	 * salesTargetGroupUserTargetRepository.findAll(pageable); return result; }
	 * 
	 *//**
		 * Get all the salesTargetGroupUserTargets.
		 * 
		 * @return the list of entities
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<SalesTargetGroupUserTargetDTO>
	 * findAllByCompany() { log.debug("Request to get all TaskHeaders");
	 * List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList =
	 * salesTargetGroupUserTargetRepository .findAllByCompanyId();
	 * List<SalesTargetGroupUserTargetDTO> result = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(
	 * salesTargetGroupUserTargetList); return result; }
	 * 
	 *//**
		 * Get all the salesTargetGroupUserTargets.
		 * 
		 * @param pageable the pagination information
		 * @return the list of entities
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public Page<SalesTargetGroupUserTargetDTO>
	 * findAllByCompany(Pageable pageable) {
	 * log.debug("Request to get all Activities"); Page<SalesTargetGroupUserTarget>
	 * activities =
	 * salesTargetGroupUserTargetRepository.findAllByCompanyId(pageable);
	 * Page<SalesTargetGroupUserTargetDTO> result = new
	 * PageImpl<SalesTargetGroupUserTargetDTO>( salesTargetGroupUserTargetMapper.
	 * salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(
	 * activities.getContent()), pageable, activities.getTotalElements()); return
	 * result; }
	 * 
	 *//**
		 * Get one salesTargetGroupUserTarget by id.
		 *
		 * @param id the id of the entity
		 * @return the entity
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public SalesTargetGroupUserTargetDTO
	 * findOne(Long id) {
	 * log.debug("Request to get SalesTargetGroupUserTarget : {}", id);
	 * SalesTargetGroupUserTarget salesTargetGroupUserTarget =
	 * salesTargetGroupUserTargetRepository.findOne(id);
	 * SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO =
	 * salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
	 * salesTargetGroupUserTarget); return salesTargetGroupUserTargetDTO; }
	 * 
	 *//**
		 * Get one salesTargetGroupUserTarget by pid.
		 *
		 * @param pid the pid of the entity
		 * @return the entity
		 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public
	 * Optional<SalesTargetGroupUserTargetDTO> findOneByPid(String pid) {
	 * log.debug("Request to get SalesTargetGroupUserTarget by pid : {}", pid);
	 * return salesTargetGroupUserTargetRepository.findOneByPid(pid).map(
	 * salesTargetGroupUserTarget -> { SalesTargetGroupUserTargetDTO
	 * salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
	 * salesTargetGroupUserTarget); return salesTargetGroupUserTargetDTO; }); }
	 * 
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<SalesTargetGroupUserTargetDTO>
	 * findByCurrentUserAndCurrentMonth() {
	 * 
	 * LocalDate currentDate = LocalDate.now();
	 * 
	 * LocalDate start = currentDate.with(TemporalAdjusters.firstDayOfMonth());
	 * LocalDate end = currentDate.with(TemporalAdjusters.lastDayOfMonth());
	 * 
	 * Optional<User> user =
	 * userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
	 * List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList =
	 * salesTargetGroupUserTargetRepository
	 * .findByUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * user.get().getPid(), start, end); if
	 * (!salesTargetGroupUserTargetList.isEmpty()) {
	 * List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs =
	 * salesTargetGroupUserTargetMapper
	 * .salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(
	 * salesTargetGroupUserTargetList); for (SalesTargetGroupUserTargetDTO
	 * salesTargetGroupUserTargetDTO : salesTargetGroupUserTargetDTOs) { // get
	 * sales Target Group documents Set<Long> documents =
	 * salesTargetGroupDocumentRepository
	 * .findDocumentIdsBySalesTargetGroupPid(salesTargetGroupUserTargetDTO.
	 * getSalesTargetGroupPid());
	 * 
	 * Set<Long> productProfiles = salesTargetGroupProductRepository
	 * .findProductIdBySalesTargetGroupPid(salesTargetGroupUserTargetDTO.
	 * getSalesTargetGroupPid());
	 * 
	 * if (!documents.isEmpty() && !productProfiles.isEmpty()) { // get achieved
	 * amount Double achievedAmount = inventoryVoucherDetailRepository
	 * .sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(
	 * salesTargetGroupUserTargetDTO.getUserPid(), documents, productProfiles,
	 * start.atTime(0, 0), end.atTime(23, 59)); // set achieved amount if
	 * (achievedAmount != null) { double roundedAchievedAmt = new
	 * BigDecimal(achievedAmount.toString()) .setScale(2,
	 * RoundingMode.HALF_UP).doubleValue();
	 * salesTargetGroupUserTargetDTO.setAchievedAmount(roundedAchievedAmt); } } }
	 * return salesTargetGroupUserTargetDTOs; } return null; }
	 * 
	 *//**
		 * Delete the salesTargetGroupUserTarget by id.
		 * 
		 * @param id the id of the entity
		 *//*
			 * public void delete(String pid) {
			 * log.debug("Request to delete SalesTargetGroupUserTarget : {}", pid);
			 * salesTargetGroupUserTargetRepository.findOneByPid(pid).ifPresent(
			 * salesTargetGroupUserTarget -> {
			 * salesTargetGroupUserTargetRepository.delete(salesTargetGroupUserTarget.getId(
			 * )); }); }
			 * 
			 * @Override
			 * 
			 * @Transactional(readOnly = true) public List<SalesTargetGroupUserTarget>
			 * findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid, String
			 * salesTargetGroupPid, LocalDate startDate, LocalDate endDate) { return
			 * salesTargetGroupUserTargetRepository.
			 * findUserAndSalesTargetGroupPidAndDateWiseDuplicate(userPid,
			 * salesTargetGroupPid, startDate, endDate); }
			 * 
			 * @Override public SalesTargetGroupUserTargetDTO saveUpdateDaylyTarget(
			 * List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs) {
			 * List<SalesTargetGroupUserTarget> groupUserTargets = new ArrayList<>();
			 * 
			 * Company company =
			 * companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			 * 
			 * Optional<EmployeeProfile> opEmployee = employeeProfileRepository
			 * .findOneByPid(salesTargetGroupUserTargetDTOs.get(0).getUserPid()); if
			 * (opEmployee.isPresent())
			 * 
			 * for (SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO :
			 * salesTargetGroupUserTargetDTOs) {
			 * 
			 * SalesTargetGroupUserTarget salesTargetGroupUserTarget = new
			 * SalesTargetGroupUserTarget();
			 * 
			 * Optional<SalesTargetGroupUserTarget> opSalesTargetGroupUserTarget =
			 * salesTargetGroupUserTargetRepository
			 * .findOneByPid(salesTargetGroupUserTargetDTO.getPid());
			 * 
			 * if (opSalesTargetGroupUserTarget.isPresent()) { salesTargetGroupUserTarget =
			 * opSalesTargetGroupUserTarget.get();
			 * salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume(
			 * )); } else { // set pid salesTargetGroupUserTarget
			 * .setPid(SalesTargetGroupUserTargetService.PID_PREFIX +
			 * RandomUtil.generatePid());
			 * salesTargetGroupUserTarget.setSalesTargetGroup(salesTargetGroupRepository
			 * .findOneByPid(salesTargetGroupUserTargetDTO.getSalesTargetGroupPid()).get());
			 * // set company salesTargetGroupUserTarget.setCompany(company);
			 * salesTargetGroupUserTarget.setFromDate(salesTargetGroupUserTargetDTO.
			 * getFromDate());
			 * salesTargetGroupUserTarget.setToDate(salesTargetGroupUserTargetDTO.getToDate(
			 * )); salesTargetGroupUserTarget.setAmount(0);
			 * salesTargetGroupUserTarget.setVolume(salesTargetGroupUserTargetDTO.getVolume(
			 * ));
			 * 
			 * salesTargetGroupUserTarget.setUser(opEmployee.get().getUser());
			 * 
			 * salesTargetGroupUserTarget.setAccountWiseTarget(false);
			 * salesTargetGroupUserTarget.setTargetFrequency(TargetFrequency.DAY); }
			 * groupUserTargets.add(salesTargetGroupUserTarget); }
			 * salesTargetGroupUserTargetRepository.save(groupUserTargets); //
			 * SalesTargetGroupUserTargetDTO result = // salesTargetGroupUserTargetMapper //
			 * .salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
			 * salesTargetGroupUserTarget); // return result; return null; }
			 * 
			 * @Override public List<SalesTargetGroupUserTargetDTO>
			 * findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise( String userPid,
			 * String salesTargetGroupPid, LocalDate startDate, LocalDate endDate,
			 * TargetFrequency targetFrequency) { log.debug(
			 * "Request to get SalesTargetGroupUserTarget findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise: {}"
			 * ); List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets =
			 * salesTargetGroupUserTargetRepository
			 * .findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(userPid,
			 * salesTargetGroupPid, startDate, endDate, targetFrequency);
			 * List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs =
			 * salesTargetGroupUserTargets.stream()
			 * .map(SalesTargetGroupUserTargetDTO::new).collect(Collectors.toList());
			 * 
			 * Comparator<SalesTargetGroupUserTargetDTO> salaryComparator = (o1, o2) ->
			 * o1.getFromDate() .compareTo(o2.getFromDate());
			 * salesTargetGroupUserTargetDTOs.sort(salaryComparator.reversed());
			 * 
			 * return salesTargetGroupUserTargetDTOs; }
			 * 
			 * @Override public List<SalesTargetGroupUserTargetDTO>
			 * findAllByCompanyIdAndTargetFrequencyAndDateBetween( TargetFrequency
			 * targetFrequency, LocalDate startDate, LocalDate endDate) { log.
			 * debug("Request to get SalesTargetGroupUserTarget findAllByCompanyIdAndTargetFrequency: {}"
			 * ); List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets =
			 * salesTargetGroupUserTargetRepository
			 * .findAllByCompanyIdAndTargetFrequencyAndDateBetween(targetFrequency,
			 * startDate, endDate); List<SalesTargetGroupUserTargetDTO>
			 * salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargets.stream()
			 * .map(SalesTargetGroupUserTargetDTO::new).collect(Collectors.toList());
			 * Comparator<SalesTargetGroupUserTargetDTO> salaryComparator = (o1, o2) ->
			 * o1.getFromDate() .compareTo(o2.getFromDate());
			 * salesTargetGroupUserTargetDTOs.sort(salaryComparator.reversed()); return
			 * salesTargetGroupUserTargetDTOs; }
			 */

}