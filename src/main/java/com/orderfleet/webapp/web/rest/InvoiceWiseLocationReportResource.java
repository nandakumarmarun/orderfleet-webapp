package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.web.rest.dto.*;
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
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
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
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since July 12, 2016
 */
@Controller
@RequestMapping("/web")
public class InvoiceWiseLocationReportResource {

	private final Logger log = LoggerFactory.getLogger(InvoiceWiseLocationReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

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

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private InvoiceDetailsDenormalizedRepository invoiceDetailsDenormalizedRepository;

	/**
	 * GET /invoice-wise-location-reports : get all the executive task executions.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task execution in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/invoice-wise-location-reports", method = RequestMethod.GET)
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
			model.addAttribute("locations", locationDTOs);
			model.addAttribute("activities", userActivityService.findAllDistinctByUserActivityByCompany());
		} else {
			model.addAttribute("locations", locationDTOs);
			// model.addAttribute("accounts",
			// locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
			List<UserActivity> activities = userActivityRepository.findByUserIsCurrentUser();
			List<Activity> activitys = new ArrayList<>();
			for (UserActivity userActivity : activities) {
				activitys.add(userActivity.getActivity());
			}
			model.addAttribute("activities", activitys);
		}
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> optionalInterim = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.INTERIM_SAVE);
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);
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

		return "company/invoiceWiseLocationReports";
	}

	@RequestMapping(value = "/invoice-wise-location-reports/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				execution.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}
			/*
			 * if (execution.getLocationType() == LocationType.GpsLocation) { String
			 * location = geoLocationService .findAddressFromLatLng(execution.getLatitude()
			 * + "," + execution.getLongitude()); execution.setLocation(location); } else if
			 * (execution.getLocationType() == LocationType.TowerLocation) { TowerLocation
			 * location = geoLocationService.findAddressFromCellTower(execution.getMcc(),
			 * execution.getMnc(), execution.getCellId(), execution.getLac());
			 * execution.setLatitude(location.getLat());
			 * execution.setLongitude(location.getLan());
			 * execution.setLocation(location.getLocation()); }
			 */
			execution = executiveTaskExecutionRepository.save(execution);
			executionView = new InvoiceWiseReportView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-location-reports/updateTowerLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateTowerLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			/*
			 * if (execution.getLatitude() != BigDecimal.ZERO) {
			 * System.out.println("-------lat != 0"); String location = geoLocationService
			 * .findAddressFromLatLng(execution.getLatitude() + "," +
			 * execution.getLongitude()); System.out.println("-------" + location);
			 * execution.setLocation(location);
			 * 
			 * } else { System.out.println("-------No Location");
			 * execution.setLocation("No Location"); }
			 */

			TowerLocation location = geoLocationService.findAddressFromCellTower(execution.getMcc(), execution.getMnc(),
					execution.getCellId(), execution.getLac());

			if (location.getLat() != null && location.getLat() != BigDecimal.ZERO) {
				execution.setTowerLatitude(location.getLat());
				execution.setTowerLongitude(location.getLan());

			}

			execution.setTowerLocation(location.getLocation());

			execution = executiveTaskExecutionRepository.save(execution);
			executionView = new InvoiceWiseReportView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-location-reports/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InvoiceWiseReportView>> filterExecutiveTaskExecutions(
			@RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
			@RequestParam("activityPid") String activityPid, @RequestParam("locationPid") String locationPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam boolean inclSubordinate) {
		List<InvoiceWiseReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, LocalDate.now(),
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, yeasterday,
					yeasterday, inclSubordinate);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, weekStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, monthStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, fromDateTime,
					toFateTime, inclSubordinate);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, locationPid, fromDateTime,
					fromDateTime, inclSubordinate);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<InvoiceWiseReportView> getFilterData(String employeePid, String documentPid, String activityPid,
			String locationPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<String> activityPids;
		List<String> locationPids;
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

		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();

		if (locationPid.equalsIgnoreCase("no")) {
			// accountProfilePids = getAccountPids(userIds);
			// if all accounts selected avoid account wise query
			executiveTaskExecutions = executiveTaskExecutionRepository
					.getByDateBetweenAndActivityPidInAndUserIdIn(fromDate, toDate, activityPids, userIds);
		} else {
			// if a specific account is selected load data based on that particular account
			locationPids = Arrays.asList(locationPid);

			/*
			 * List<Location> locations=
			 * locationRepository.findAllByCompanyIdAndActivatedLocationPidIn(locationPids);
			 * 
			 * List<Long>
			 * locationsIds=locations.stream().mapToLong(Location::getId).boxed().collect(
			 * Collectors.toList());
			 */

			List<AccountProfileDTO> accountProfiles = locationAccountProfileService
					.findAccountProfileByLocationPidInAndActivated(locationPids, true);

			accountProfilePids = accountProfiles.stream().map(AccountProfileDTO::getPid).collect(Collectors.toList());
       
			if (accountProfilePids.size() > 0) {

				executiveTaskExecutions = executiveTaskExecutionRepository
						.getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids,
								userIds, accountProfilePids);
			}
      
		}
      boolean cmp=getCompanyCofig();
		List<InvoiceWiseReportView> invoiceWiseReportViews = new ArrayList<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			double totalSalesOrderAmount = 0.0;
			double totalRecieptAmount = 0.0;
			InvoiceWiseReportView invoiceWiseReportView = new InvoiceWiseReportView(executiveTaskExecution);
			if(cmp)
			{
				invoiceWiseReportView.setAccountProfileName(invoiceWiseReportView.getDescription());
			}
			
			else
			{
				invoiceWiseReportView.setAccountProfileName(invoiceWiseReportView.getAccountProfileName());
			}
			
			EmployeeProfile employeeProfile = employeeProfileRepository
					.findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());
			if (employeeProfile != null) {
				invoiceWiseReportView.setEmployeeName(employeeProfile.getName());
				String timeSpend = findTimeSpend(executiveTaskExecution.getPunchInDate(),
						executiveTaskExecution.getSendDate());
				invoiceWiseReportView.setTimeSpend(timeSpend);
				List<InvoiceWiseReportDetailView> executiveTaskExecutionDetailViews = new ArrayList<>();
				List<Object[]> inventoryVouchers;
				if (documentPid.equals("no")) {
					DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					
					String id = "INV_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description = "get by executive task execution Id";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					inventoryVouchers = inventoryVoucherHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
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
					String id = "INV_QUERY_153" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description = "get by executive taskExecutionId and DocPid ";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					inventoryVouchers = inventoryVoucherHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
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
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "ACC_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get AccVoucher By ExecutiveTaskExecutionId";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					accountingVouchers = accountingVoucherHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
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
						String id = "ACC_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get AccVoucher By ExecutiveTaskExecutionId And DocumentPid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					accountingVouchers = accountingVoucherHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
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
				for (Object[] obj : accountingVouchers) {
					executiveTaskExecutionDetailViews.add(new InvoiceWiseReportDetailView(obj[0].toString(),
							obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString()));
					if (obj[3].toString().equalsIgnoreCase("ACCOUNTING_VOUCHER")) {
						totalRecieptAmount += Double.valueOf(obj[2].toString());
					}
				}

				List<Object[]> dynamicDocuments;
				if (documentPid.equals("no")) {
					DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id11 = "DYN_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description11 ="get all documents by Executive Task executionId";
					LocalDateTime startLCTime11 = LocalDateTime.now();
					String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
					String startDate11 = startLCTime11.format(DATE_FORMAT11);
					logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
					dynamicDocuments = dynamicDocumentHeaderRepository
							.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
					String flag11 = "Normal";
					LocalDateTime endLCTime11 = LocalDateTime.now();
					String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
					String endDate11 = startLCTime11.format(DATE_FORMAT11);
					Duration duration11 = Duration.between(startLCTime11, endLCTime11);
					long minutes11 = duration11.toMinutes();
					if (minutes11 <= 1 && minutes11 >= 0) {
						flag11 = "Fast";
					}
					if (minutes11 > 1 && minutes11 <= 2) {
						flag11 = "Normal";
					}
					if (minutes11 > 2 && minutes11 <= 10) {
						flag11 = "Slow";
					}
					if (minutes11 > 10) {
						flag11 = "Dead Slow";
					}
			                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
							+ description11);
				} else {
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "DYN_QUERY_134" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get all documents by executive task execution id and doc pid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					dynamicDocuments = dynamicDocumentHeaderRepository
							.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExecution.getId(), documentPid);
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
				for (Object[] obj : dynamicDocuments) {
					boolean imageFound = false;
					// check image saved
					DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "FORM_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get the form by checking exists by header pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					if (filledFormRepository.existsByHeaderPidIfFiles(obj[0].toString())) {
						imageFound = true;
					}
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
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
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
				System.out.println("Testing start for Activity Transaction");
				System.out.println("employeePid:" + employeePid);
				System.out.println("userIds:" + userIds.toString());
				System.out.println("Testing end for Activity Transaction");
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
		}

		return userIds;
	}

	public String findTimeSpend(LocalDateTime startTime, LocalDateTime endTime) {
		long hours = 00;
		long minutes = 00;
		long seconds = 00;
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

	@RequestMapping(value = "/invoice-wise-location-reports/changeAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> changeAccountProfile(@RequestParam String accountProfilePid,
			@RequestParam String exeTaskPid) {
		log.debug("Web request to change Account Profile");
		Optional<ExecutiveTaskExecution> optionalExecutiveTaskExecution = executiveTaskExecutionRepository
				.findOneByPid(exeTaskPid);
		if (optionalExecutiveTaskExecution.isPresent()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
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

			if (optionalAccountProfile.isPresent()) {
				optionalExecutiveTaskExecution.get().setAccountProfile(optionalAccountProfile.get());
				executiveTaskExecutionRepository.save(optionalExecutiveTaskExecution.get());
				// save InventoryVoucherHeader
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				String id1 = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 = "get all by executive task execution Pid ";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		      logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);
				List<InventoryVoucherHeader> newInventoryVoucherHeaders = new ArrayList<>();
				for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
					inventoryVoucherHeader.setReceiverAccount(optionalAccountProfile.get());
					newInventoryVoucherHeaders.add(inventoryVoucherHeader);
				}
				inventoryVoucherHeaderRepository.save(newInventoryVoucherHeaders);

				 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id11 = "ACC_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description11 =" get all AccVoucher By ExecutiveTask Execution Pid ";
					LocalDateTime startLCTime11 = LocalDateTime.now();
					String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
					String startDate11 = startLCTime11.format(DATE_FORMAT11);
					logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				// save AccountingVoucherHeader
				List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				 String flag11 = "Normal";
					LocalDateTime endLCTime11 = LocalDateTime.now();
					String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
					String endDate11 = startLCTime11.format(DATE_FORMAT11);
					Duration duration11 = Duration.between(startLCTime11, endLCTime11);
					long minutes11 = duration11.toMinutes();
					if (minutes11 <= 1 && minutes11 >= 0) {
						flag11 = "Fast";
					}
					if (minutes11 > 1 && minutes11 <= 2) {
						flag11 = "Normal";
					}
					if (minutes11 > 2 && minutes11 <= 10) {
						flag11 = "Slow";
					}
					if (minutes11 > 10) {
						flag11 = "Dead Slow";
					}
			                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
							+ description11);
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

	@RequestMapping(value = "/invoice-wise-location-reports/createAndChangeAccountProfile/{pid}/{territoryPid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(result.getPid());
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

			if (optionalAccountProfile.isPresent()) {
				optionalExecutiveTaskExecution.get().setAccountProfile(optionalAccountProfile.get());
				executiveTaskExecutionRepository.save(optionalExecutiveTaskExecution.get());

				// save InventoryVoucherHeader
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				String id1 = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 = "get all by executive task execution Pid ";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		      logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);
				List<InventoryVoucherHeader> newInventoryVoucherHeaders = new ArrayList<>();
				for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
					inventoryVoucherHeader.setReceiverAccount(optionalAccountProfile.get());
					newInventoryVoucherHeaders.add(inventoryVoucherHeader);
				}
				inventoryVoucherHeaderRepository.save(newInventoryVoucherHeaders);
				 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id11 = "ACC_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description11 =" get all AccVoucher By ExecutiveTask Execution Pid ";
					LocalDateTime startLCTime11 = LocalDateTime.now();
					String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
					String startDate11 = startLCTime11.format(DATE_FORMAT11);
					logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				// save AccountingVoucherHeader
				List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(optionalExecutiveTaskExecution.get().getPid());
				 String flag11 = "Normal";
					LocalDateTime endLCTime11 = LocalDateTime.now();
					String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
					String endDate11 = startLCTime11.format(DATE_FORMAT11);
					Duration duration11 = Duration.between(startLCTime11, endLCTime11);
					long minutes11 = duration11.toMinutes();
					if (minutes11 <= 1 && minutes11 >= 0) {
						flag11 = "Fast";
					}
					if (minutes11 > 1 && minutes11 <= 2) {
						flag11 = "Normal";
					}
					if (minutes11 > 2 && minutes11 <= 10) {
						flag11 = "Slow";
					}
					if (minutes11 > 10) {
						flag11 = "Dead Slow";
					}
			                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
							+ description11);
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

	@RequestMapping(value = "/invoice-wise-location-reports/getForms/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getForms(@PathVariable String pid) {
		log.debug("Web request to get Form by dynamic document pid : {}", pid);
		return new ResponseEntity<>(documentFormsService.findByDocumentPid(pid), HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-location-reports/getAccountFromForm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, String>> getAccountFromForm(@RequestParam String formPid,
			@RequestParam String dynamicDocumentPid, @RequestParam String exePid) {
		log.debug("Web request to get Form by dynamic document pid : {}", formPid);
		Map<String, String> map = executiveTaskExecutionService.findAccountFromForm(formPid, dynamicDocumentPid,
				exePid);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-location-reports/getDynamicDocument/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DynamicDocumentHeaderDTO>> getDynamicDocument(@PathVariable String pid) {
		log.debug("Web request to get Dynamic Document by Executive Task Execution pid : {}", pid);

		return new ResponseEntity<>(dynamicDocumentHeaderService.findAllDynamicDocumentByExecutiveTaskExecutionPid(pid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/invoice-wise-location-reports/updateGeoTag/{pid}", method = RequestMethod.GET)
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

	@RequestMapping(value = "/invoice-wise-location-reports/getActivities/{typeName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@RequestMapping(value = "/invoice-wise-location-reports/filterSkippedCustomers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<SkippedAccountDTO>> filterSkippedCustomers(
			@RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
			@RequestParam("activityPid") String activityPid, @RequestParam("locationPid") String locationPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam boolean inclSubordinate) {
		List<SkippedAccountDTO> accountProfile = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, LocalDate.now(),
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, yeasterday,
					yeasterday, inclSubordinate);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, weekStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, monthStartDate,
					LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, fromDateTime,
					toFateTime, inclSubordinate);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			accountProfile = getSkippedData(employeePid, documentPid, activityPid, locationPid, fromDateTime,
					fromDateTime, inclSubordinate);
		}
		return new ResponseEntity<>(accountProfile, HttpStatus.OK);
	}

	private List<SkippedAccountDTO> getSkippedData(String employeePid, String documentPid, String activityPid,
			String locationPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
		// TODO Auto-generated method stub
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		Optional<Location> location = locationRepository.findOneByPid(locationPid);
		List<String> activityPids;
		List<String> locationPids;
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

		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();

		
			// if a specific account is selected load data based on that particular account
			locationPids = Arrays.asList(locationPid);


			List<AccountProfileDTO> accountProfiles = locationAccountProfileService
					.findAccountProfileByLocationPidInAndActivated(locationPids, true);

			accountProfilePids = accountProfiles.stream().map(AccountProfileDTO::getPid).collect(Collectors.toList());
			
			List<InvoiceDetailsDenormalized> invoiceDetails = new ArrayList<>();

//			if (accountProfilePids.size() > 0) {
//
//				invoiceDetails = invoiceDetailsDenormalizedRepository
//						.getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids,
//								userIds, accountProfilePids);
//			}
//			List<String> accountPid = invoiceDetails.stream().map(data ->data.getAccountProfilePid()).collect(Collectors.toList());
		if (accountProfilePids.size() > 0) {

			executiveTaskExecutions = executiveTaskExecutionRepository
					.getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids,
							userIds, accountProfilePids);
		}
		List<String> accountPid = executiveTaskExecutions.stream().map(data ->data.getAccountProfile().getPid()).collect(Collectors.toList());



		List<String> skippedList = new ArrayList<String>(accountProfilePids);
			skippedList.removeAll(accountPid);
		
			
			
			List<AccountProfile> accounts =accountProfileRepository.findAllByAccountProfilePids(skippedList);
			List<SkippedAccountDTO> skippedAccountDTOList =  new ArrayList<>();
			for(AccountProfile account :accounts)
			{
				SkippedAccountDTO accountDTO= new SkippedAccountDTO();
				accountDTO.setAccountType(account.getAccountType().getName());
				accountDTO.setLocation(location.get().getName());
				accountDTO.setAccountProfileName(account.getName());
				accountDTO.setAddress(account.getAddress());
				accountDTO.setGeotagLocation(account.getLocation());
				accountDTO.setClosingBalance(account.getClosingBalance());
				accountDTO.setDistrict(account.getDistrictName());
				accountDTO.setPhoneNo(account.getPhone1());
				accountDTO.setEmail(account.getEmail1());
				skippedAccountDTOList.add(accountDTO);
			}
			
		
			return skippedAccountDTOList;
	}
}
