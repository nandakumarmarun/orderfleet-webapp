package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportDetailView;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since July 12, 2016
 */
@Controller
@RequestMapping("/web")
public class InvoiceWiseReportResource {

	private final Logger log = LoggerFactory.getLogger(InvoiceWiseReportResource.class);

	private static final String INTERIM_SAVE = "interimSave";

	private static final String DISTANCE_TRAVELLED = "distanceTarvel";

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private UserActivityRepository userActivityRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private DocumentService documentService;

	@Inject
	private DocumentFormsService documentFormsService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private UserDocumentService userDocumentService;

	@Inject
	private LocationService locationService;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private UserRepository userRepository;

	/**
	 * GET /invoice-wise-reports : get all the executive task executions.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task execution in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/invoice-wise-reports", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		model.addAttribute("dynamicdocuments", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
		List<DocumentDTO> documentDTOs = userDocumentService.findDocumentsByUserIsCurrentUser();
		model.addAttribute("documents", documentDTOs);

		List<LocationDTO> locationDTOs = locationService.findAllByCompanyAndLocationActivated(true);
		model.addAttribute("territories", locationDTOs);

		if (userIds.isEmpty()) {
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
			model.addAttribute("activities", userActivityService.findAllDistinctByUserActivityByCompany());
		} else {
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
			List<UserActivity> activities = userActivityRepository.findByUserIsCurrentUser();
			List<Activity> activitys = new ArrayList<>();
			for (UserActivity userActivity : activities) {
				activitys.add(userActivity.getActivity());
			}
			model.addAttribute("activities", activitys);
		}
		Optional<CompanyConfiguration> optionalInterim = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.INTERIM_SAVE);

		Optional<CompanyConfiguration> optionaldistanceTarvel = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.LOCATION_VARIANCE);
		if (optionalInterim.isPresent()) {
			if (optionalInterim.get().getValue().equals("true")) {
				model.addAttribute(INTERIM_SAVE, true);
			} else {
				model.addAttribute(INTERIM_SAVE, false);
			}
		} else {
			model.addAttribute(INTERIM_SAVE, false);
		}

		if (optionaldistanceTarvel.isPresent()) {
			if (optionaldistanceTarvel.get().getValue().equals("true")) {
				model.addAttribute(DISTANCE_TRAVELLED, true);
			} else {
				model.addAttribute(DISTANCE_TRAVELLED, false);
			}
		} else {
			model.addAttribute(DISTANCE_TRAVELLED, false);
		}

		return "company/invoiceWiseReports";
	}

	@RequestMapping(value = "/invoice-wise-reports/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();
			if (execution.getLocationType() == LocationType.GpsLocation) {
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				execution.setLocation(location);
			} else if (execution.getLocationType() == LocationType.TowerLocation) {
				TowerLocation location = geoLocationService.findAddressFromCellTower(execution.getMcc(),
						execution.getMnc(), execution.getCellId(), execution.getLac());
				execution.setLatitude(location.getLat());
				execution.setLongitude(location.getLan());
				execution.setLocation(location.getLocation());
			}
			execution = executiveTaskExecutionRepository.save(execution);
			executionView = new InvoiceWiseReportView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InvoiceWiseReportView>> filterExecutiveTaskExecutions(
			@RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam boolean inclSubordinate) {
		List<InvoiceWiseReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, LocalDate.now(),
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, yeasterday,
					yeasterday, inclSubordinate);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, weekStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, monthStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
					toFateTime, inclSubordinate);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
					fromDateTime, inclSubordinate);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<InvoiceWiseReportView> getFilterData(String employeePid, String documentPid, String activityPid,
			String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<String> activityPids;
		List<String> accountProfilePids;

		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		if (activityPid.equalsIgnoreCase("no") || activityPid.equalsIgnoreCase("planed")
				|| activityPid.equalsIgnoreCase("unPlaned")) {
			activityPids = getActivityPids(activityPid, userIds);
		} else {
			activityPids = Arrays.asList(activityPid);
		}
		if (accountPid.equalsIgnoreCase("no")) {
			accountProfilePids = getAccountPids(userIds);
		} else {
			accountProfilePids = Arrays.asList(accountPid);
		}
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids, userIds,
						accountProfilePids);
		List<InvoiceWiseReportView> invoiceWiseReportViews = new ArrayList<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			double totalSalesOrderAmount = 0.0;
			double totalRecieptAmount = 0.0;
			InvoiceWiseReportView invoiceWiseReportView = new InvoiceWiseReportView(executiveTaskExecution);
			EmployeeProfile employeeProfile = employeeProfileRepository
					.findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());
			if (employeeProfile != null) {
				invoiceWiseReportView.setEmployeeName(employeeProfile.getName());
				String timeSpend = findTimeSpend(executiveTaskExecution.getStartTime(),
						executiveTaskExecution.getEndTime());
				invoiceWiseReportView.setTimeSpend(timeSpend);
				List<InvoiceWiseReportDetailView> executiveTaskExecutionDetailViews = new ArrayList<>();
				List<Object[]> inventoryVouchers;
				if (documentPid.equals("no")) {
					inventoryVouchers = inventoryVoucherHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
				} else {
					inventoryVouchers = inventoryVoucherHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
				}
				for (Object[] obj : inventoryVouchers) {
					InvoiceWiseReportDetailView executiveTaskExecutionDetailView = new InvoiceWiseReportDetailView(
							obj[0].toString(), obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString());
					executiveTaskExecutionDetailView.setDocumentVolume(Double.valueOf(obj[4].toString()));
					if (obj[3].toString().equalsIgnoreCase("INVENTORY_VOUCHER")) {
						totalSalesOrderAmount += Double.valueOf(obj[2].toString());
					}

					executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
				}

				List<Object[]> accountingVouchers = new ArrayList<>();
				if (documentPid.equals("no")) {
					accountingVouchers = accountingVoucherHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
				} else {
					accountingVouchers = accountingVoucherHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
				}
				for (Object[] obj : accountingVouchers) {
					executiveTaskExecutionDetailViews.add(new InvoiceWiseReportDetailView(obj[0].toString(),
							obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString()));
					if (obj[3].toString().equalsIgnoreCase("ACCOUNTING_VOUCHER")) {
						totalRecieptAmount += Double.valueOf(obj[2].toString());
					}
				}

				List<Object[]> dynamicDocuments;
				if (documentPid.equals("no")) {
					dynamicDocuments = dynamicDocumentHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
				} else {
					dynamicDocuments = dynamicDocumentHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
				}
				for (Object[] obj : dynamicDocuments) {
					boolean imageFound = false;
					// check image saved
					if (filledFormRepository.existsByHeaderPidIfFiles(obj[0].toString())) {
						imageFound = true;
					}
					executiveTaskExecutionDetailViews.add(new InvoiceWiseReportDetailView(obj[0].toString(),
							obj[1].toString(), obj[2].toString(), imageFound));
				}
				// if condition for document wise filter
				if (!documentPid.equals("no") && executiveTaskExecutionDetailViews.isEmpty()) {
				} else {
					invoiceWiseReportView.setInvoiceWiseReportDetailViews(executiveTaskExecutionDetailViews);
					invoiceWiseReportView.setTotalSalesOrderAmount(totalSalesOrderAmount);
					invoiceWiseReportView.setTotalRecieptAmount(totalRecieptAmount);
					invoiceWiseReportViews.add(invoiceWiseReportView);
				}
			}
		}
		return invoiceWiseReportViews;

	}

	private List<String> getAccountPids(List<Long> userIds) {
		List<AccountProfileDTO> allAccountDtos;
		if (userIds.isEmpty()) {
			allAccountDtos = accountProfileService.findAllByCompanyAndActivated(true);
		} else {
			allAccountDtos = locationAccountProfileService.findAccountProfilesByUsers(userIds);
		}
		return allAccountDtos.stream().map(AccountProfileDTO::getPid).collect(Collectors.toList());
	}

	/**
	 * used to get activityPids by filter
	 * 
	 * @param activityPid
	 * @param userIds
	 * @return
	 */
	private List<String> getActivityPids(String activityPid, List<Long> userIds) {
		List<String> activityPids;
		List<ActivityDTO> allActivityDTOs = new ArrayList<>();
		if (userIds.isEmpty()) {
			allActivityDTOs.addAll(userActivityService.findAllDistinctByUserActivityByCompany());
		} else {
			Set<UserActivity> userActivities = userActivityService
					.findUserActivitiesByActivatedTrueAndUserIdIn(userIds);
			allActivityDTOs.addAll(userActivities.stream()
					.map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
							usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
							usrActvity.getInterimSave()))
					.collect(Collectors.toList()));
		}
		List<ActivityDTO> activityDTOs = new ArrayList<>();
		if (activityPid.equalsIgnoreCase("no")) {
			activityDTOs.addAll(allActivityDTOs);
		} else if (activityPid.equalsIgnoreCase("planed")) {
			allActivityDTOs.forEach(act -> {
				if (act.getPlanThrouchOnly()) {
					activityDTOs.add(act);
				}
			});
		} else if (activityPid.equalsIgnoreCase("unPlaned")) {
			allActivityDTOs.forEach(act -> {
				if (!act.getPlanThrouchOnly()) {
					activityDTOs.add(act);
				}
			});
		}
		activityPids = activityDTOs.stream().map(act -> act.getPid()).collect(Collectors.toList());
		return activityPids;

	}

	// private List<InvoiceWiseReportView> getFilterData1(String
	// employeePid,
	// String documentPid, String activityPid,
	// String accountPid, LocalDate fDate, LocalDate tDate) {
	//
	// String userPid = "no";
	// if (!employeePid.equals("no") && !employeePid.equals("Dashboard
	// Employee")) {
	// Optional<EmployeeProfileDTO> opEmployee =
	// employeeProfileService.findOneByPid(employeePid);
	// if (opEmployee.isPresent()) {
	// userPid = opEmployee.get().getUserPid();
	// }
	// }
	//
	// List<Long> userIds = getUserIdsUnderCurrentUser(employeePid);
	//
	// LocalDateTime fromDate = fDate.atTime(0, 0);
	// LocalDateTime toDate = tDate.atTime(23, 59);
	// List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
	// if (userPid.equals("no") && activityPid.equals("no") &&
	// accountPid.equals("no")) {
	//
	// // user under current user
	// if (userIds.isEmpty()) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);
	// } else {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByUserIdInAndDateBetweenOrderByDateDesc(userIds, fromDate,
	// toDate);
	// }
	// } else if (!userPid.equals("no") && !activityPid.equals("no") &&
	// !accountPid.equals("no")) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdUserPidActivityPidAccountPidAndDateBetweenOrderByDateDesc(userPid,
	// activityPid,
	// accountPid, fromDate, toDate);
	// } else if (!userPid.equals("no") && activityPid.equals("no") &&
	// accountPid.equals("no")) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(userPid,
	// fromDate,
	// toDate);
	//
	// } else if (!activityPid.equals("no") && userPid.equals("no") &&
	// accountPid.equals("no")) {
	// // user under current user
	// if (userIds.isEmpty()) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(activityPid,
	// fromDate, toDate);
	// } else {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByUserIdInActivityPidAndDateBetweenOrderByDateDesc(userIds,
	// activityPid, fromDate,
	// toDate);
	// }
	// } else if (!accountPid.equals("no") && userPid.equals("no") &&
	// activityPid.equals("no")) {
	// // user under current user
	// if (userIds.isEmpty()) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(accountPid,
	// fromDate, toDate);
	// } else {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByUserIdInAccountPidAndDateBetweenOrderByDateDesc(userIds,
	// accountPid, fromDate,
	// toDate);
	// }
	// } else if (!userPid.equals("no") && !activityPid.equals("no") &&
	// accountPid.equals("no")) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(userPid,
	// activityPid, fromDate,
	// toDate);
	// } else if (!userPid.equals("no") && !accountPid.equals("no") &&
	// activityPid.equals("no")) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByDateDesc(userPid,
	// accountPid, fromDate,
	// toDate);
	// } else if (!accountPid.equals("no") && !activityPid.equals("no") &&
	// userPid.equals("no")) {
	// // user under current user
	// if (userIds.isEmpty()) {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByCompanyIdActivityPidAccountPidAndDateBetweenOrderByDateDesc(activityPid,
	// accountPid,
	// fromDate, toDate);
	// } else {
	// executiveTaskExecutions = executiveTaskExecutionRepository
	// .findAllByUserIdInActivityPidAccountPidAndDateBetweenOrderByDateDesc(userIds,
	// activityPid,
	// accountPid, fromDate, toDate);
	// }
	// }
	// List<InvoiceWiseReportView> invoiceWiseReportViews = new
	// ArrayList<>();
	// for (ExecutiveTaskExecution executiveTaskExecution :
	// executiveTaskExecutions)
	// {
	// InvoiceWiseReportView invoiceWiseReportView = new
	// InvoiceWiseReportView(
	// executiveTaskExecution);
	// EmployeeProfileDTO employeeProfileDTO = employeeProfileService
	// .findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());
	// if (employeeProfileDTO != null) {
	// invoiceWiseReportView.setEmployeeName(employeeProfileDTO.getName());
	// String timeSpend = findTimeSpend(executiveTaskExecution.getStartTime(),
	// executiveTaskExecution.getEndTime());
	// invoiceWiseReportView.setTimeSpend(timeSpend);
	// List<InvoiceWiseReportDetailView> executiveTaskExecutionDetailViews
	// =
	// new ArrayList<InvoiceWiseReportDetailView>();
	// List<Object[]> inventoryVouchers = new ArrayList<>();
	// if (documentPid.equals("no")) {
	// inventoryVouchers = inventoryVoucherHeaderRepository
	// .findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
	// } else {
	// inventoryVouchers = inventoryVoucherHeaderRepository
	// .findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(),
	// documentPid);
	// }
	// for (Object[] obj : inventoryVouchers) {
	// InvoiceWiseReportDetailView executiveTaskExecutionDetailView = new
	// InvoiceWiseReportDetailView(
	// obj[0].toString(), obj[1].toString(), Double.valueOf(obj[2].toString()),
	// obj[3].toString());
	// executiveTaskExecutionDetailView.setDocumentVolume(Double.valueOf(obj[4].toString()));
	// executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
	// }
	//
	// List<Object[]> accountingVouchers = new ArrayList<>();
	// if (documentPid.equals("no")) {
	// accountingVouchers = accountingVoucherHeaderRepository
	// .findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
	// } else {
	// accountingVouchers = accountingVoucherHeaderRepository
	// .findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(),
	// documentPid);
	// }
	// for (Object[] obj : accountingVouchers) {
	// executiveTaskExecutionDetailViews.add(new
	// InvoiceWiseReportDetailView(obj[0].toString(),
	// obj[1].toString(), Double.valueOf(obj[2].toString()),
	// obj[3].toString()));
	// }
	//
	// List<Object[]> dynamicDocuments = new ArrayList<>();
	// if (documentPid.equals("no")) {
	// dynamicDocuments = dynamicDocumentHeaderRepository
	// .findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
	// } else {
	// dynamicDocuments = dynamicDocumentHeaderRepository
	// .findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(),
	// documentPid);
	// }
	// for (Object[] obj : dynamicDocuments) {
	// boolean imageFound = false;
	// // check image saved
	// if (filledFormRepository.existsByHeaderPidIfFiles(obj[0].toString())) {
	// imageFound = true;
	// }
	// executiveTaskExecutionDetailViews.add(new
	// InvoiceWiseReportDetailView(obj[0].toString(),
	// obj[1].toString(), obj[2].toString(), imageFound));
	// }
	// // if condition for document wise filter
	// if (!documentPid.equals("no") &&
	// executiveTaskExecutionDetailViews.isEmpty())
	// {
	// } else {
	// invoiceWiseReportView.setInvoiceWiseReportDetailViews(executiveTaskExecutionDetailViews);
	// invoiceWiseReportViews.add(invoiceWiseReportView);
	// }
	// }
	// }
	// return invoiceWiseReportViews;
	// }

	private List<Long> getUserIdsUnderCurrentUser(String employeePid, boolean inclSubordinate) {
		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> uniqueIds = new HashSet<>();
				if (!dashboardUserIds.isEmpty()) {
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid.equals(sid)) {
									uniqueIds.add(sid);
								}
							}
						}
					} else {
						userIds = new ArrayList<>(dashboardUserIds);
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}
			} else {
				if (userIds.isEmpty()) {
					List<User> users = userRepository.findAllByCompanyId();
					userIds = users.stream().map(User::getId).collect(Collectors.toList());
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
			}
		}
		return userIds;
	}

	public String findTimeSpend(LocalDateTime startTime, LocalDateTime endTime) {
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		if (startTime != null && endTime != null) {
			long years = startTime.until(endTime, ChronoUnit.YEARS);
			startTime = startTime.plusYears(years);

			long months = startTime.until(endTime, ChronoUnit.MONTHS);
			startTime = startTime.plusMonths(months);

			long days = startTime.until(endTime, ChronoUnit.DAYS);
			startTime = startTime.plusDays(days);
			hours = startTime.until(endTime, ChronoUnit.HOURS);
			startTime = startTime.plusHours(hours);

			minutes = startTime.until(endTime, ChronoUnit.MINUTES);
			startTime = startTime.plusMinutes(minutes);

			seconds = startTime.until(endTime, ChronoUnit.SECONDS);
		}
		return hours + " : " + minutes + " : " + seconds;

	}

	@RequestMapping(value = "/invoice-wise-reports/changeAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> changeAccountProfile(@RequestParam String accountProfilePid,
			@RequestParam String exeTaskPid) {
		log.debug("Web request to change Account Profile");
		Optional<ExecutiveTaskExecution> optionalExecutiveTaskExecution = executiveTaskExecutionRepository
				.findOneByPid(exeTaskPid);
		if (optionalExecutiveTaskExecution.isPresent()) {
			Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
			if (optionalAccountProfile.isPresent()) {
				optionalExecutiveTaskExecution.get().setAccountProfile(optionalAccountProfile.get());
				executiveTaskExecutionRepository.save(optionalExecutiveTaskExecution.get());
				// save InventoryVoucherHeader
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				List<InventoryVoucherHeader> newInventoryVoucherHeaders = new ArrayList<>();
				for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
					inventoryVoucherHeader.setReceiverAccount(optionalAccountProfile.get());
					newInventoryVoucherHeaders.add(inventoryVoucherHeader);
				}
				inventoryVoucherHeaderRepository.save(newInventoryVoucherHeaders);

				// save AccountingVoucherHeader
				List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				List<AccountingVoucherHeader> newAccountingVoucherHeaders = new ArrayList<>();
				for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
					accountingVoucherHeader.setAccountProfile(optionalAccountProfile.get());
					newAccountingVoucherHeaders.add(accountingVoucherHeader);
				}
				accountingVoucherHeaderRepository.save(newAccountingVoucherHeaders);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/invoice-wise-reports/createAndChangeAccountProfile/{pid}/{territoryPid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@ResponseBody
	public ResponseEntity<Void> createAndChangeAccountProfile(@RequestBody AccountProfileDTO accountProfileDTO,
			@PathVariable String pid, @PathVariable String territoryPid) throws URISyntaxException {
		log.debug("Web request to create and change Account Profile");
		if (accountProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile", "idexists",
					"A new account profile cannot already have an ID")).body(null);
		}
		if (accountProfileService.findByName(accountProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		accountProfileDTO.setAccountStatus(AccountStatus.Verified);
		AccountProfileDTO result = accountProfileService.save(accountProfileDTO);
		locationAccountProfileService.saveLocationAccountProfileSingle(territoryPid, result.getPid());

		Optional<ExecutiveTaskExecution> optionalExecutiveTaskExecution = executiveTaskExecutionRepository
				.findOneByPid(pid);
		if (optionalExecutiveTaskExecution.isPresent()) {
			Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(result.getPid());
			if (optionalAccountProfile.isPresent()) {
				optionalExecutiveTaskExecution.get().setAccountProfile(optionalAccountProfile.get());
				executiveTaskExecutionRepository.save(optionalExecutiveTaskExecution.get());

				// save InventoryVoucherHeader
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				List<InventoryVoucherHeader> newInventoryVoucherHeaders = new ArrayList<>();
				for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
					inventoryVoucherHeader.setReceiverAccount(optionalAccountProfile.get());
					newInventoryVoucherHeaders.add(inventoryVoucherHeader);
				}
				inventoryVoucherHeaderRepository.save(newInventoryVoucherHeaders);

				// save AccountingVoucherHeader
				List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				List<AccountingVoucherHeader> newAccountingVoucherHeaders = new ArrayList<>();
				for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
					accountingVoucherHeader.setAccountProfile(optionalAccountProfile.get());
					newAccountingVoucherHeaders.add(accountingVoucherHeader);
				}
				accountingVoucherHeaderRepository.save(newAccountingVoucherHeaders);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/getForms/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getForms(@PathVariable String pid) {
		log.debug("Web request to get Form by dynamic document pid : {}", pid);
		return new ResponseEntity<>(documentFormsService.findByDocumentPid(pid), HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/getAccountFromForm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, String>> getAccountFromForm(@RequestParam String formPid,
			@RequestParam String dynamicDocumentPid, @RequestParam String exePid) {
		log.debug("Web request to get Form by dynamic document pid : {}", formPid);
		Map<String, String> map = executiveTaskExecutionService.findAccountFromForm(formPid, dynamicDocumentPid,
				exePid);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/getDynamicDocument/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DynamicDocumentHeaderDTO>> getDynamicDocument(@PathVariable String pid) {
		log.debug("Web request to get Dynamic Document by Executive Task Execution pid : {}", pid);

		return new ResponseEntity<>(dynamicDocumentHeaderService.findAllDynamicDocumentByExecutiveTaskExecutionPid(pid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/updateGeoTag/{pid}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updateGeoTagLocation(@PathVariable String pid) {
		log.debug("Web request to save accountProfile geo tag");
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		if (opExecutiveeExecution.isPresent()) {
			AccountProfileDTO accountProfileDTO = accountProfileService
					.findOneByPid(opExecutiveeExecution.get().getAccountProfile().getPid()).get();
			accountProfileDTO.setLatitude(opExecutiveeExecution.get().getLatitude());
			accountProfileDTO.setLongitude(opExecutiveeExecution.get().getLongitude());
			accountProfileDTO.setLocation(opExecutiveeExecution.get().getLocation());
			accountProfileDTO = accountProfileService.update(accountProfileDTO);
			return new ResponseEntity<>(opExecutiveeExecution.get().getLocation(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-reports/getActivities/{typeName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityDTO>> getActivities(@PathVariable String typeName) {
		log.debug("Web request to get activities by typeName : {}", typeName);

		List<ActivityDTO> allActivityDTOs = new ArrayList<>();
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			allActivityDTOs.addAll(userActivityService.findAllDistinctByUserActivityByCompany());
		} else {
			List<UserActivity> userActivities = userActivityRepository.findByUserIsCurrentUser();
			allActivityDTOs.addAll(userActivities.stream()
					.map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
							usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
							usrActvity.getInterimSave()))
					.collect(Collectors.toList()));
		}
		List<ActivityDTO> result = new ArrayList<>();
		if (typeName.equalsIgnoreCase("all")) {
			result.addAll(allActivityDTOs);
		} else if (typeName.equalsIgnoreCase("planed")) {
			allActivityDTOs.forEach(act -> {
				if (act.getPlanThrouchOnly()) {
					result.add(act);
				}
			});
		} else if (typeName.equalsIgnoreCase("unPlaned")) {
			allActivityDTOs.forEach(act -> {
				if (!act.getPlanThrouchOnly()) {
					result.add(act);
				}
			});
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
