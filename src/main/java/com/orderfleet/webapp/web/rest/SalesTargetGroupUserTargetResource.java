package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesTargetGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesTargetGroupUserTargetResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupUserTargetResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private SalesTargetGroupUserTargetService salesTargetGroupUserTargetService;

	@Inject
	private SalesTargetGroupService salesTargetGroupService;
	
	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	/**
	 * POST /sales-target-group-userTargets : Create a new
	 * salesTargetGroupUserTarget.
	 *
	 * @param salesTargetGroupUserTargetDTO
	 *            the salesTargetGroupUserTargetDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new salesTargetGroupUserTargetDTO, or with status 400 (Bad
	 *         Request) if the salesTargetGroupUserTarget has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/sales-target-group-userTargets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesTargetGroupUserTargetDTO> createSalesTargetGroupUserTarget(
			@Valid @RequestBody SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to save SalesTargetGroupUserTarget : {}", salesTargetGroupUserTargetDTO);
		if (salesTargetGroupUserTargetDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroupUserTarget",
					"idexists", "A new product profile cannot already have an ID")).body(null);
		}
		List<SalesTargetGroupUserTarget> overlappingRecords = salesTargetGroupUserTargetService
				.findUserAndSalesTargetGroupPidAndDateWiseDuplicate(salesTargetGroupUserTargetDTO.getUserPid(),
						salesTargetGroupUserTargetDTO.getSalesTargetGroupPid(),
						salesTargetGroupUserTargetDTO.getFromDate(), salesTargetGroupUserTargetDTO.getToDate());
		if (!overlappingRecords.isEmpty()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroupUserTarget",
					"dateAlreadyExists", "Date range already exist")).body(null);
		}
		SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService.save(salesTargetGroupUserTargetDTO);
		return ResponseEntity.created(new URI("/web/sales-target-group-userTargets/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("salesTargetGroupUserTarget", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /sales-target-group-userTargets : Updates an existing
	 * salesTargetGroupUserTarget.
	 *
	 * @param salesTargetGroupUserTargetDTO
	 *            the salesTargetGroupUserTargetDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         salesTargetGroupUserTargetDTO, or with status 400 (Bad Request)
	 *         if the salesTargetGroupUserTargetDTO is not valid, or with status
	 *         500 (Internal Server Error) if the salesTargetGroupUserTargetDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/sales-target-group-userTargets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetGroupUserTargetDTO> updateSalesTargetGroupUserTarget(
			@Valid @RequestBody SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to update SalesTargetGroupUserTarget : {}", salesTargetGroupUserTargetDTO);
		if (salesTargetGroupUserTargetDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroupUserTarget",
					"idNotexists", "activityUserTarget must have an ID")).body(null);
		}
		List<SalesTargetGroupUserTarget> overlappingRecords = salesTargetGroupUserTargetService
				.findUserAndSalesTargetGroupPidAndDateWiseDuplicate(salesTargetGroupUserTargetDTO.getUserPid(),
						salesTargetGroupUserTargetDTO.getSalesTargetGroupPid(),
						salesTargetGroupUserTargetDTO.getFromDate(), salesTargetGroupUserTargetDTO.getToDate());
		if (!overlappingRecords.isEmpty()
				&& (!overlappingRecords.get(0).getPid().equals(salesTargetGroupUserTargetDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroupUserTarget",
					"dateAlreadyExists", "Date range already exist")).body(null);
		}
		SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService.update(salesTargetGroupUserTargetDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroupUserTarget",
					"idNotexists", "Invalid activityUserTarget ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("salesTargetGroupUserTarget",
				salesTargetGroupUserTargetDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /sales-target-group-userTargets : get all the
	 * salesTargetGroupUserTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesTargetGroupUserTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/sales-target-group-userTargets", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesTargetGroupUserTargets(Model model) throws URISyntaxException {

		log.debug("Web request to get a page of SalesTargetGroupUserTargets");

		model.addAttribute("salesTargetGroupUserTargets", salesTargetGroupUserTargetService.findAllByCompany());

		model.addAttribute("salesTargetGroups", salesTargetGroupService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER));
		return "company/salesTargetGroupUserTargets";
	}

	/**
	 * GET /sales-target-group-userTargets/:id : get the "id"
	 * salesTargetGroupUserTarget.
	 *
	 * @param id
	 *            the id of the salesTargetGroupUserTargetDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         salesTargetGroupUserTargetDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/sales-target-group-userTargets/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetGroupUserTargetDTO> getSalesTargetGroupUserTarget(@PathVariable String pid) {
		log.debug("Web request to get SalesTargetGroupUserTarget by pid : {}", pid);
		return salesTargetGroupUserTargetService.findOneByPid(pid).map(
				salesTargetGroupUserTargetDTO -> new ResponseEntity<>(salesTargetGroupUserTargetDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /sales-target-group-userTargets/:pid : delete the "pid"
	 * salesTargetGroupUserTarget.
	 *
	 * @param pid
	 *            the pid of the salesTargetGroupUserTargetDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/sales-target-group-userTargets/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteSalesTargetGroupUserTarget(@PathVariable String pid) {
		log.debug("REST request to delete SalesTargetGroupUserTarget : {}", pid);
		salesTargetGroupUserTargetService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("salesTargetGroupUserTarget", pid.toString())).build();
	}

	/**
	 * GET /user-monthly-sales-targets : get all the salesUserTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesUserTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/user-monthly-sales-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Sales Target");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));
		return "company/set-monthly-sales-target";
	}

	@RequestMapping(value = "/user-monthly-sales-targets/monthly-sales-targets", method = RequestMethod.GET)
	public @ResponseBody List<SalesMonthlyTargetDTO> monthlySalesTargets(@RequestParam String employeePid,
			@RequestParam String monthAndYear, @RequestParam String productGroupPid) {
		log.debug("Web request to get monthly Sales Targets");
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}

		List<SalesTargetGroup> salesTargetGroups = new ArrayList<>();
		if (productGroupPid.equals("no")) {
			salesTargetGroups = salesTargetGroupRepository.findAllByCompanyId();
		} else {
			salesTargetGroups = productGroupSalesTargetGrouprepository
					.findSalesTargetGroupByProductGroupPid(productGroupPid);

		}

		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		if (salesTargetGroups.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<SalesMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
				SalesMonthlyTargetDTO monthlyTargetDTO = new SalesMonthlyTargetDTO();
				monthlyTargetDTO.setSalesTargetGroupPid(salesTargetGroup.getPid());
				monthlyTargetDTO.setSalesTargetGroupName(salesTargetGroup.getName());
				monthlyTargetDTO.setUserPid(userPid);

				List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
						.findByUserPidAndSalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
								userPid, salesTargetGroup.getPid(), firstDateMonth, lastDateMonth);
				if (salesTargetGroupUserTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesTargetGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargets) {
						totalAmount = totalAmount + salesTargetGroupUserTarget.getAmount();
						totalVolume = totalVolume + salesTargetGroupUserTarget.getVolume();
					}
					monthlyTargetDTO.setAmount(totalAmount);
					monthlyTargetDTO.setVolume(totalVolume);
					monthlyTargetDTO.setSalesTargetGroupUserTragetPid(salesTargetGroupUserTargets.get(0).getPid());
				} else {
					monthlyTargetDTO.setAmount(0);
					monthlyTargetDTO.setVolume(0);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/user-monthly-sales-targets/monthly-sales-targets", method = RequestMethod.POST)
	public @ResponseBody SalesMonthlyTargetDTO saveMonthlyActivityTargets(
			@RequestBody SalesMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Targets");

		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
				.findOneByPid(monthlyTargetDTO.getUserPid());
		if (employeeProfileDTO.isPresent()) {
			monthlyTargetDTO.setUserPid(employeeProfileDTO.get().getUserPid());
			if (monthlyTargetDTO.getSalesTargetGroupUserTragetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService
						.saveMonthlyTarget(monthlyTargetDTO, firstDateMonth, lastDateMonth);
				monthlyTargetDTO.setSalesTargetGroupUserTragetPid(result.getPid());
			} else {
				salesTargetGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupUserTragetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
							salesTargetGroupUserTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

	/** account wise monthly sales targets */
	@Timed
	@RequestMapping(value = "/account-wise-monthly-sales-targets", method = RequestMethod.GET)
	public String setAccountMonthlySalesTargets(Model model) {
		log.debug("Web request to get a page of  set account wise Sales Target");
		model.addAttribute("users", userService.findAllByCompany());
		List<Object[]> sTargetBlockObjects = salesTargetGroupRepository.findSalesTargetGroupPropertyByCompanyId();
		List<SalesTargetBlockDTO> sTargetBlockDtos = new ArrayList<>();
		for (Object[] sTargetBlockObj : sTargetBlockObjects) {
			SalesTargetBlockDTO salesTargetBlockDTO = new SalesTargetBlockDTO();
			salesTargetBlockDTO.setPid(sTargetBlockObj[0].toString());
			salesTargetBlockDTO.setName(sTargetBlockObj[1].toString());
			sTargetBlockDtos.add(salesTargetBlockDTO);
		}
		model.addAttribute("salesTargetGroups", sTargetBlockDtos);
		return "company/set-account-wise-monthly-sales-target";
	}

	@RequestMapping(value = "/account-wise-monthly-sales-targets/load", method = RequestMethod.GET)
	public @ResponseBody List<SalesMonthlyTargetDTO> loadAccountWiseMonthlySalesTargets(@RequestParam(required = false) String page, @RequestParam String userPid,
			@RequestParam String salesTargetGroupPid, @RequestParam String monthAndYear) {
		log.debug("Web request to get monthly Sales Targets");
		List<Object[]> accountProfileObjects;
		if("NON-ALPHABETICAL".equals(page)) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_131" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by comp and accPid and name start with non alphabet";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfileObjects = accountProfileRepository.findByCompanyAndAccountPidAndNameStartWithNonAlphabetic();	
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
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
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		} else {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_130" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by comp and accPid and name start letter";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfileObjects = accountProfileRepository.findByCompanyAndAccountPidAndNameStartWith(page);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
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
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		}
		if(!accountProfileObjects.isEmpty()) {
			int[] mnthYr = Arrays.stream(monthAndYear.split("/")).mapToInt(Integer::valueOf).toArray();
			YearMonth yearMonth = YearMonth.of(mnthYr[1], mnthYr[0]);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<SalesMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();
			for (Object[] apObj : accountProfileObjects) {
				SalesMonthlyTargetDTO monthlyTargetDTO = new SalesMonthlyTargetDTO();
				monthlyTargetDTO.setAccountProfilePid(apObj[0].toString());
				monthlyTargetDTO.setAccountProfileName(apObj[1].toString());
				monthlyTargetDTO.setUserPid(userPid);
				
				List<Object[]> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository.findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndDateBetween(
								userPid, salesTargetGroupPid, apObj[0].toString(), firstDateMonth, lastDateMonth);
				if (salesTargetGroupUserTargets.isEmpty()) {
					monthlyTargetDTO.setAmount(0);
					monthlyTargetDTO.setVolume(0);
				} else {
					Object[] salesTargetGroupUserTarget = salesTargetGroupUserTargets.get(0);
					monthlyTargetDTO.setSalesTargetGroupUserTragetPid(salesTargetGroupUserTarget[0].toString());
					monthlyTargetDTO.setAmount((Double)salesTargetGroupUserTarget[1]);
					monthlyTargetDTO.setVolume((Double)salesTargetGroupUserTarget[2]);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/account-wise-monthly-sales-targets", method = RequestMethod.POST)
	public @ResponseBody SalesMonthlyTargetDTO saveAccountWiseMonthlyActivityTargets(
			@RequestBody SalesMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save Account Wise monthly Sales Targets");
		if (monthlyTargetDTO.getSalesTargetGroupUserTragetPid().equals("null")) {
			String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService.saveMonthlyTarget(monthlyTargetDTO,
					firstDateMonth, lastDateMonth);
			monthlyTargetDTO.setSalesTargetGroupUserTragetPid(result.getPid());
		} else {
			salesTargetGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupUserTragetPid())
					.ifPresent(activityUserTarget -> {
						activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
						activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
						salesTargetGroupUserTargetRepository.save(activityUserTarget);
					});
		}
		return monthlyTargetDTO;
	}
}