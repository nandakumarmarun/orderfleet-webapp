package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
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
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportDetailView;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

@Controller
@RequestMapping("/web")
public class DeliveryReportResource {

	private final Logger log = LoggerFactory.getLogger(InvoiceWiseReportResource.class);

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

	@RequestMapping(value = "/delivery-wise-reports", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDeliveryExecutiveTaskExecutions(Pageable pageable, Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		List<DocumentDTO> documentDTOs = userDocumentService.findDocumentsByUserIsCurrentUser();
		model.addAttribute("documents", documentDTOs);

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
		return "company/deliveryWiseReport";
	}

	@RequestMapping(value = "/delivery-wise-reports/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InvoiceWiseReportView>> filterDeliveryExecutiveTaskExecutions(
			@RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {

		List<InvoiceWiseReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, LocalDate.now(),
					LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, yeasterday,
					yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, weekStartDate,
					LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, monthStartDate,
					LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
					toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
					fromDateTime);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<InvoiceWiseReportView> getFilterData(String employeePid, String documentPid, String activityPid,
			String accountPid, LocalDate fDate, LocalDate tDate) {
		log.info("Get Fileter Data");
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<String> activityPids;
		List<String> accountProfilePids;

		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid);

		log.info("User Ids :" + userIds);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		if (activityPid.equalsIgnoreCase("no") || activityPid.equalsIgnoreCase("planed")
				|| activityPid.equalsIgnoreCase("unPlaned")) {
			activityPids = getActivityPids(activityPid, userIds);
		} else {
			activityPids = Arrays.asList(activityPid);
		}

		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
		log.info("Finding executive Task execution");
		if (accountPid.equalsIgnoreCase("no")) {
			// accountProfilePids = getAccountPids(userIds);
			// if all accounts selected avoid account wise query
			executiveTaskExecutions = executiveTaskExecutionRepository
					.getByCreatedDateBetweenAndActivityPidInAndUserIdIn(fromDate, toDate, activityPids, userIds);
		} else {
			// if a specific account is selected load data based on that particular account
			accountProfilePids = Arrays.asList(accountPid);
			executiveTaskExecutions = executiveTaskExecutionRepository
					.getByCreatedDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids,
							userIds, accountProfilePids);
		}

		Set<Long> exeIds = new HashSet<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			exeIds.add(executiveTaskExecution.getId());
		}

		log.info("Finding executive Task execution Inventory Vouchers...");
		log.info("Documents :"+documentPid);
		List<Object[]> inventoryVouchers = new ArrayList<>();
		if (exeIds.size() > 0) {
			if (documentPid.equals("no")) {
				inventoryVouchers = inventoryVoucherHeaderRepository.findByExecutiveTaskExecutionIdIn(exeIds);

			} else {
				inventoryVouchers = inventoryVoucherHeaderRepository
						.findByExecutiveTaskExecutionIdInAndDocumentPid(exeIds, documentPid);

			}
		}

		log.info("executive task execution looping started :" + executiveTaskExecutions.size());
		List<InvoiceWiseReportView> invoiceWiseReportViews = new ArrayList<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			double totalSalesOrderAmount = 0.0;

			InvoiceWiseReportView invoiceWiseReportView = new InvoiceWiseReportView(executiveTaskExecution);
			EmployeeProfile employeeProfile = employeeProfileRepository
					.findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());

			invoiceWiseReportView
					.setVehicleRegistrationNumber(executiveTaskExecution.getVehicleNumber() == null ? "no vehicle"
							: executiveTaskExecution.getVehicleNumber());
			invoiceWiseReportView.setAccountProfileName(invoiceWiseReportView.getAccountProfileName());
			invoiceWiseReportView.setSendDate(executiveTaskExecution.getDate());
			if (employeeProfile != null) {
				invoiceWiseReportView.setEmployeeName(employeeProfile.getName());

				for (Object[] obj : inventoryVouchers) {

					if (obj[5].toString().equalsIgnoreCase(executiveTaskExecution.getPid())) {

						invoiceWiseReportView.setInvoiceNo(obj[6].toString());
						invoiceWiseReportView.setCreatedDate(LocalDateTime.parse(obj[7].toString()));
						if (obj[3].toString().equalsIgnoreCase("INVENTORY_VOUCHER")) {
							totalSalesOrderAmount += Double.valueOf(obj[2].toString());
							invoiceWiseReportView.setTotalSalesOrderAmount(totalSalesOrderAmount);

							invoiceWiseReportViews.add(invoiceWiseReportView);
						}

					}
				}
			}
		}
		log.info("executive task execution looping ended :" + executiveTaskExecutions.size());
		return invoiceWiseReportViews;
	}

	private List<String> getActivityPids(String activityPid, List<Long> userIds) {
		// TODO Auto-generated method stub
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

	private List<Long> getUserIdsUnderCurrentUser(String employeePid) {
		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
				Set<Long> uniqueIds = new HashSet<>();
				log.info("dashboard user ids empty: " + dashboardUserIds.isEmpty());
				if (!dashboardUserIds.isEmpty()) {
					log.info(" user ids empty: " + userIds.isEmpty());
					log.info("userids :" + userIds.toString());
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid != null && uid.equals(sid)) {
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

					userIds = userRepository.findAllUserIdsByCompanyId();
				}
			}
		} else {

			Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
			if (opEmployee.isPresent()) {
				userIds = Arrays.asList(opEmployee.get().getUser().getId());
			}
			System.out.println("Testing start for Activity Transaction");
			System.out.println("--------------------------------------");
			System.out.println("employeePid:" + employeePid);
			System.out.println("UserIds:" + userIds.toString());
			System.out.println("Testing end for Activity Transaction");
		}

		return userIds;
	}
}
