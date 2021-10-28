package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardLocationDataDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Web controller for managing Dash board territory wise.
 * 
 * @author Shaheer
 * @since June 05, 2017
 */
@Controller
@RequestMapping(value = "/web/dashboard/territorywise")
public class DashboardTerritoryWiseResource {

	private final Logger log = LoggerFactory.getLogger(DashboardTerritoryWiseResource.class);

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	
	@Inject
	private CompanySettingRepository companySettingRepository;
	
	@Inject
	private DashboardUserRepository dashboardUserRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserRepository userRepository;

	@Timed
	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public String dashboard(Model model) {
		log.info("Web request to get dashboard territory wise");
		//set user ui type
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> model.addAttribute("dashboardType", u.getDashboardUIType()));
		
		locationHierarchyRepository.findRootLocationByCompanyId(SecurityUtils.getCurrentUsersCompanyId())
				.ifPresent(lh -> {
					model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
					model.addAttribute("territory", lh.getLocation());
				});
		model.addAttribute("locations", locationService.findAllByCompany());
		return "company/dashboard-territorywise";
	}
	
	@RequestMapping(value = "/child-territories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LocationDTO>> getChildTerritories(@RequestParam("territoryId") Long territoryId) {
		return new ResponseEntity<>(locationHierarchyService.findChildLocationsByParentId(territoryId), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/parent-territory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationDTO> getParentTerrity(@RequestParam("territoryId") Long territoryId) {
		return new ResponseEntity<>(locationHierarchyService.findParentLocation(territoryId), HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardHeaderSummaryDTO> getDashboardSummaryData(
			@RequestParam(value = "date", required = false) LocalDate date, @RequestParam("territoryId") Long territoryId) {
		log.info("Web request to get dashboard  summary Day/Week/Month wise");
		return new ResponseEntity<>(loadDashboardHeaderSummary(date.atTime(0, 0), date.atTime(23, 59), territoryId), HttpStatus.OK);
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/territorytiles-summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DashboardLocationDataDTO<DashboardSummaryDTO>>> getDashboardAccountWiseData(
			@RequestParam(value = "date", required = false) LocalDate date, @RequestParam("territoryId") Long territoryId) {
		log.info("Web request to get territory tiles sepecific data");
		return new ResponseEntity<>(createTerritoryTilesData(date, territoryId), HttpStatus.OK);
	}
	
	@Timed
	@RequestMapping(value = "/delay-time", method = RequestMethod.GET)
	public @ResponseBody int getDelayTime() {
		CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
		if (companySetting != null && companySetting.getDashboardConfiguration() != null) {
			return companySetting.getDashboardConfiguration().getDelayTimeTerritory();
		}
		return 0;
	}
	
	@Timed
	@RequestMapping(value = "/update-delay-time", method = RequestMethod.POST)
	public @ResponseBody int updateDelayTime(@RequestParam int delayTime) {
		CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
		if (companySetting == null) {
			companySetting = new CompanySetting();
			companySetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		}
		companySetting.getDashboardConfiguration().setDelayTimeTerritory(delayTime);
		companySettingRepository.save(companySetting);
		return 0;
	}

	private DashboardHeaderSummaryDTO loadDashboardHeaderSummary(LocalDateTime from, LocalDateTime to, Long territoryId) {
		List<DashboardItem> dashboardItems = dashboardItemRepository.findAllByCompanyId();
		List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> weekSummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> monthSummaryDatas = new ArrayList<>();

		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDateTime weekStartDate = from.with(fieldISO, 1);
		LocalDateTime monthStartDate = from.withDayOfMonth(1);

		List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(territoryId);
		List<AccountProfile> accountProfiles = locationAccountProfileRepository
				.findAccountProfileByLocationIdIn(locationIds);
		for (DashboardItem dashboardItem : dashboardItems) {
			DashboardSummaryDTO daySummary = new DashboardSummaryDTO();
			daySummary.setDashboardItemPid(dashboardItem.getPid());
			daySummary.setLabel(dashboardItem.getName());
			daySummary.setDashboardItemType(dashboardItem.getDashboardItemType());
			daySummary.setTaskPlanType(dashboardItem.getTaskPlanType());

			DashboardSummaryDTO weekSummary = new DashboardSummaryDTO();
			weekSummary.setDashboardItemPid(dashboardItem.getPid());
			weekSummary.setLabel(dashboardItem.getName());
			weekSummary.setDashboardItemType(dashboardItem.getDashboardItemType());
			weekSummary.setTaskPlanType(dashboardItem.getTaskPlanType());

			DashboardSummaryDTO monthSummary = new DashboardSummaryDTO();
			monthSummary.setDashboardItemPid(dashboardItem.getPid());
			monthSummary.setLabel(dashboardItem.getName());
			monthSummary.setDashboardItemType(dashboardItem.getDashboardItemType());
			monthSummary.setTaskPlanType(dashboardItem.getTaskPlanType());

			if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
				// activities summary-day
				daySummary = findAccountWiseActivityAchievedAndScheduled(dashboardItem, daySummary, from, to, accountProfiles);
				// activities summary-week
				weekSummary = findAccountWiseActivityAchievedAndScheduled(dashboardItem, weekSummary, weekStartDate, to, accountProfiles);
				// activities summary-month
				monthSummary = findAccountWiseActivityAchievedAndScheduled(dashboardItem, monthSummary, monthStartDate, to, accountProfiles);
			} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
				if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
					// inventory document summary-day
					daySummary = findAccountWiseInventoryCountAndAmount(dashboardItem, daySummary, from, to, accountProfiles);
					// inventory document summary-week
					weekSummary = findAccountWiseInventoryCountAndAmount(dashboardItem, weekSummary, weekStartDate, to, accountProfiles);
					// inventory document summary-month
					monthSummary = findAccountWiseInventoryCountAndAmount(dashboardItem, monthSummary, monthStartDate, to, accountProfiles);
				} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
					// accounting document summary-day
					daySummary = findAccountWiseAccountingVoucherCountAndAmount(dashboardItem, daySummary, from, to, accountProfiles);
					// accounting document summary-week
					weekSummary = findAccountWiseAccountingVoucherCountAndAmount(dashboardItem, weekSummary, weekStartDate, to,
							accountProfiles);
					// accounting document summary-month
					monthSummary = findAccountWiseAccountingVoucherCountAndAmount(dashboardItem, monthSummary, monthStartDate, to,
							accountProfiles);
				} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
					// dynamic document summary-day
					daySummary = findAccountWiseDynamicVoucherCountAndAmount(dashboardItem, daySummary, from, to, accountProfiles);
					// dynamic document summary-week
					weekSummary = findAccountWiseDynamicVoucherCountAndAmount(dashboardItem, weekSummary, weekStartDate, to, accountProfiles);
					// dynamic document summary-month
					monthSummary = findAccountWiseDynamicVoucherCountAndAmount(dashboardItem, monthSummary, monthStartDate, to,
							accountProfiles);
				}
			}
			daySummaryDatas.add(daySummary);
			weekSummaryDatas.add(weekSummary);
			monthSummaryDatas.add(monthSummary);
		}
		DashboardHeaderSummaryDTO dashboardDTO = new DashboardHeaderSummaryDTO();
		dashboardDTO.setDaySummaryDatas(daySummaryDatas);
		dashboardDTO.setWeekSummaryDatas(weekSummaryDatas);
		dashboardDTO.setMonthSummaryDatas(monthSummaryDatas);
		return dashboardDTO;
	}
	
	private List<DashboardLocationDataDTO<DashboardSummaryDTO>> createTerritoryTilesData(LocalDate date,
			Long parentLocationId) {
		List<DashboardLocationDataDTO<DashboardSummaryDTO>> dashboardTerritoryDatas = new ArrayList<>();
		List<DashboardItem> dashboardItems = dashboardItemRepository.findAllByCompanyId();
		List<LocationDTO> dashboardTerritories;
		// get children
		List<Long> childLocationIds = locationHierarchyRepository.findIdsByParentIdAndActivatedTrue(parentLocationId);
		if (childLocationIds != null && !childLocationIds.isEmpty()) {
			dashboardTerritories = locationService.findAllByCompanyAndLocationIdIn(childLocationIds);
			dashboardTerritories.forEach(t -> {
				// get account profile in all hierarchical data under this
				// territory
				List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(Long.valueOf(t.getId()));
				if (!locationIds.isEmpty()) {
					List<AccountProfile> accountProfiles = locationAccountProfileRepository
							.findAccountProfileByLocationIdIn(locationIds);
					DashboardLocationDataDTO<DashboardSummaryDTO> dashboardTerritoryData = new DashboardLocationDataDTO<>();
					dashboardTerritoryData.setLocationId(Long.valueOf(t.getId()));
					dashboardTerritoryData.setLocationPid(t.getPid());
					dashboardTerritoryData.setLocationName(t.getName());
					dashboardTerritoryData.setAccountProfilePids(accountProfiles.stream().map(AccountProfile::getPid).collect(Collectors.toList()));
					// set attendance details
					Set<Long> uniqueUserIds = employeeProfileLocationRepository
							.findEmployeeUserIdsByLocationIdIn(locationIds);
					//dash board configured users
					Set<Long> userIds = dashboardUserRepository.findUserIdsByUserIdIn(uniqueUserIds);
					dashboardTerritoryData.setAttendenceTotalUsers(userIds.size());
					if(!userIds.isEmpty()) {
						dashboardTerritoryData.setAttendencePresentUsers(attendanceRepository.countByUserIdInAndDateBetween(new ArrayList<>(userIds), date.atTime(0, 0),
								date.atTime(23, 59)));	
					}
					// set account wise summary
					dashboardTerritoryData.setAccountSummaryData(accountSummaryData(dashboardItems, date.atTime(0, 0),
							date.atTime(23, 59), accountProfiles));
					// set last execution details
					ExecutiveTaskExecution executiveTaskExecution = executiveTaskExecutionRepository
							.findTop1ByAccountProfileInAndDateBetweenOrderByDateDesc(accountProfiles, date.atTime(0, 0),
									date.atTime(23, 59));
					if (executiveTaskExecution != null) {
						dashboardTerritoryData.setLastTime(executiveTaskExecution.getCreatedDate());
					}
					dashboardTerritoryDatas.add(dashboardTerritoryData);
				}
			});
		}
		return dashboardTerritoryDatas;
	}

	private List<DashboardSummaryDTO> accountSummaryData(List<DashboardItem> dashboardItems, LocalDateTime from,
			LocalDateTime to, List<AccountProfile> accountProfiles) {
		List<DashboardSummaryDTO> dashboardSummaryDatas = new ArrayList<>();
		for (DashboardItem dashboardItem : dashboardItems) {
			DashboardSummaryDTO dashboardItemSummary = new DashboardSummaryDTO();
			dashboardItemSummary.setDashboardItemPid(dashboardItem.getPid());
			dashboardItemSummary.setLabel(dashboardItem.getName());
			dashboardItemSummary.setDashboardItemType(dashboardItem.getDashboardItemType());
			dashboardItemSummary.setTaskPlanType(dashboardItem.getTaskPlanType());
			if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
				// activities summary-day
				dashboardItemSummary = findAccountWiseActivityAchievedAndScheduled(dashboardItem, dashboardItemSummary, from, to,
						accountProfiles);
			} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
				if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
					// inventory document summary-day
					dashboardItemSummary = findAccountWiseInventoryCountAndAmount(dashboardItem, dashboardItemSummary, from, to,
							accountProfiles);
				} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
					// accounting document summary-day
					dashboardItemSummary = findAccountWiseAccountingVoucherCountAndAmount(dashboardItem, dashboardItemSummary,
							from, to, accountProfiles);

				} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
					// dynamic document summary-day
					dashboardItemSummary = findAccountWiseDynamicVoucherCountAndAmount(dashboardItem, dashboardItemSummary, from,
							to, accountProfiles);
				}
			}
			dashboardSummaryDatas.add(dashboardItemSummary);
		}
		return dashboardSummaryDatas;
	}

	private DashboardSummaryDTO findAccountWiseActivityAchievedAndScheduled(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDTO, LocalDateTime from, LocalDateTime to,
			List<AccountProfile> accountProfiles) {
		if (!accountProfiles.isEmpty()) {
			List<Activity> activities = new ArrayList<>(dashboardItem.getActivities());
			long achieved = 0;
			long scheduled = 0;
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				// get achieved by both activities
				scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndAccountProfileIn(from,
						to, activities, accountProfiles);
				achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivitiesAndAccountProfileIn(
						from, to, activities, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				// get achieved by planned activities(task plan is not equal to
				// null)
				scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndAccountProfileIn(from,
						to, activities, accountProfiles);
				achieved = executiveTaskExecutionRepository
						.countByDateBetweenAndActivitiesAndAccountProfileInAndTaskPlanIsNotNull(from, to,
								activities, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				// get achieved by un planned activities(task plan is equal to
				// null)
				achieved = executiveTaskExecutionRepository
						.countByDateBetweenAndActivitiesAndAccountProfileInAndTaskPlanIsNull(from, to,
								activities, accountProfiles);
			}
			dashboardSummaryDTO.setAchieved(achieved);
			dashboardSummaryDTO.setScheduled(scheduled);
		}
		return dashboardSummaryDTO;
	}

	private DashboardSummaryDTO findAccountWiseInventoryCountAndAmount(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDTO, LocalDateTime from, LocalDateTime to,
			List<AccountProfile> accountProfiles) {
		Object obj = null;
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		if (!accountProfiles.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				// get both document
				String id="INV_QUERY_132";
				String description="Selecting count of inv_vouc, sum of doc Total,doc vol of inv_vouch and validating compnayId & inv_voucher doc=1& createDateBetween is 2&3 & created by id=4 and receiver account in=4";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileIn(documents, from, to,
								accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				// get documents under planned activities
				String id="INV_QUERY_133";
				String description="Selecting count of inv_vouc, sum of doc Total,doc vol of inv_vouch and validating compnayId & inv_voucher doc=1& createDateBetween is 2&3 & created by id=4 and receiver account in=4 &executiveTaskExecution.executiveTaskPlan IS NOT NULL ";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNotNull(
								documents, from, to, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				// get documents under un planned activities
				String id="INV_QUERY_134";
				String description="Selecting count of inv_vouc, sum of doc Total,doc vol of inv_vouch and validating compnayId & inv_voucher doc=1& createDateBetween is 2&3 & created by id=4 and receiver account in=4 &executiveTaskExecution.executiveTaskPlan IS NULL ";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNull(documents,
								from, to, accountProfiles);
			}
			Object[] countAndAmount = (Object[]) obj;
			if (countAndAmount != null) {
				dashboardSummaryDTO.setCount((long) countAndAmount[0]);
				if (countAndAmount[1] != null) {
					dashboardSummaryDTO.setAmount((double) countAndAmount[1]);
				}
			}
		}

		return dashboardSummaryDTO;
	}

	private DashboardSummaryDTO findAccountWiseAccountingVoucherCountAndAmount(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDTO, LocalDateTime from, LocalDateTime to,
			List<AccountProfile> accountProfiles) {
		Object obj = null;
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		if (!accountProfiles.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				// get both document
				obj = accountingVoucherHeaderRepository.getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileIn(
						documents, from, to, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				// get documents under planned activities
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNotNull(documents,
								from, to, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				// get documents under unplanned activities
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNull(documents, from,
								to, accountProfiles);
			}
			Object[] countAndAmount = (Object[]) obj;
			if (countAndAmount != null) {
				dashboardSummaryDTO.setCount((long) countAndAmount[0]);
				if (countAndAmount[1] != null) {
					dashboardSummaryDTO.setAmount((double) countAndAmount[1]);
				}
			}
		}
		return dashboardSummaryDTO;
	}

	private DashboardSummaryDTO findAccountWiseDynamicVoucherCountAndAmount(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDTO, LocalDateTime from, LocalDateTime to,
			List<AccountProfile> accountProfiles) {
		Long count = 0L;
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		if (!accountProfiles.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				// get both document
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileIn(from, to,
								documents, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				// get documents under planned activities
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
								from, to, documents, accountProfiles);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				// get documents under un planned activities
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
								from, to, documents, accountProfiles);
			}
			dashboardSummaryDTO.setCount(count);
		}

		return dashboardSummaryDTO;
	}

}
