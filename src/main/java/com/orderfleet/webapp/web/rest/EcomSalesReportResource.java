package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDetailView;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionView;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Controller
@RequestMapping("/web")
public class EcomSalesReportResource {
	private final Logger log = LoggerFactory.getLogger(EcomSalesReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductsRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private GeoLocationService geoLocationService;

	/**
	 * GET /ecom-sales-reports : get all the Ecom Sales Report.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task execution in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/ecom-sales-reports", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllEcomSalesReports(Pageable pageable, Model model) {
		log.debug("Web request to get a page of Ecom Sales Report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		return "company/ecomSalesReports";
	}

	@RequestMapping(value = "/ecom-sales-reports/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ExecutiveTaskExecutionView> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		log.debug("Web request to get  executive task executions");
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		ExecutiveTaskExecutionView executionView = new ExecutiveTaskExecutionView();
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
			executionView = new ExecutiveTaskExecutionView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	@RequestMapping(value = "/ecom-sales-reports/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ExecutiveTaskExecutionView>> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter executive task executions");
		List<ExecutiveTaskExecutionView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, accountPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, accountPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, accountPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, accountPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, accountPid, fromDateTime, toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, accountPid, fromDateTime, fromDateTime);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<ExecutiveTaskExecutionView> getFilterData(String employeePid, String accountPid, LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Document> documents = primarySecondaryDocumentRepository
				.findDocumentsByVoucherTypeAndCompanyId(VoucherType.ECOM_SALES_ORDER);
		if (documents.isEmpty()) {
			return Collections.emptyList();
		}
		String userPid = "no";
		if (!employeePid.equals("no") && !employeePid.equals("Dashboard Employees")) {
			// find employees user
			Optional<EmployeeProfileDTO> optionalEmp = employeeProfileService.findOneByPid(employeePid);
			if (optionalEmp.isPresent()) {
				userPid = optionalEmp.get().getUserPid();
			}
		}
		// find subordinates
		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (userPid.equals("no") && accountPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				String id = "INV_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by companyId and date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				Long start = System.nanoTime();
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
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
				
				String id = "INV_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by companyIdUserId and DocIns and Date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				Long start = System.nanoTime();
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdAndUserIdInAndDocumentsInAndDateBetweenOrderByCreatedDateDesc(userIds,
								documents, fromDate, toDate);
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
		} else {
			// employee selected
			if (!userPid.equals("no") && accountPid.equals("no")) {
				// user accessible accounts
				List<Location> locations = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
				if (locations.isEmpty()) {
					return Collections.emptyList();
				}
				List<AccountProfile> accountProfiles = locationAccountProfileRepository
						.findAccountProfileByLocationIdIn(
								locations.stream().map(Location::getId).collect(Collectors.toList()));
				if (accountProfiles.isEmpty()) {
					return Collections.emptyList();
				}
				List<String> accountProfilePids = accountProfiles.stream().map(AccountProfile::getPid)
						.collect(Collectors.toList());
				// user accessible products
				inventoryVoucherHeaders = findInventoryVoucherHeaderByUser(userPid, documents, fromDate, toDate,
						accountProfilePids);

			} else if (userPid.equals("no") && !accountPid.equals("no")) {
				// Account selected
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				String id = "INV_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by compIdAccountPid and Date Between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				Long start = System.nanoTime();
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountPid, fromDate, toDate,
								documents);
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
			} else if (!userPid.equals("no") && !accountPid.equals("no")) {
				// both selected
				// user accessible products
				List<String> accountProfilePids = new ArrayList<>();
				accountProfilePids.add(accountPid);
				inventoryVoucherHeaders = findInventoryVoucherHeaderByUser(userPid, documents, fromDate, toDate,
						accountProfilePids);
			}
		}

		if (inventoryVoucherHeaders.isEmpty()) {
			return Collections.emptyList();
		}
		// group by executive task execution pid
		Map<ExecutiveTaskExecution, List<InventoryVoucherHeader>> iVHGroupByExecutiveTaskExecution = inventoryVoucherHeaders
				.parallelStream().collect(Collectors.groupingBy(InventoryVoucherHeader::getExecutiveTaskExecution));
		List<ExecutiveTaskExecutionView> executiveTaskExecutionViews = new ArrayList<>();
		for (Map.Entry<ExecutiveTaskExecution, List<InventoryVoucherHeader>> entry : iVHGroupByExecutiveTaskExecution
				.entrySet()) {
			ExecutiveTaskExecution executiveTaskExecution = entry.getKey();
			ExecutiveTaskExecutionView executiveTaskExecutionView = new ExecutiveTaskExecutionView(
					executiveTaskExecution);
			String timeSpend = findTimeSpend(executiveTaskExecution.getStartTime(),
					executiveTaskExecution.getEndTime());
			executiveTaskExecutionView.setTimeSpend(timeSpend);
			List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews = new ArrayList<>();
			for (InventoryVoucherHeader ivh : entry.getValue()) {
				// Calculate total amount
				double documentTotal = ivh.getInventoryVoucherDetails().stream()
						.mapToDouble(InventoryVoucherDetail::getRowTotal).sum();
				ExecutiveTaskExecutionDetailView executiveTaskExecutionDetailView = new ExecutiveTaskExecutionDetailView(
						ivh.getPid(), ivh.getDocument().getName(), documentTotal,
						ivh.getDocument().getDocumentType().toString());
				executiveTaskExecutionDetailView.setDocumentVolume(documentTotal);
				 Optional<ExecutiveTaskExecutionDetailView> ivdetails = executiveTaskExecutionDetailViews.stream()
						.filter(ivd -> ivd.getDocumentName().equalsIgnoreCase(ivd.getDocumentName())).findAny();
				if (ivdetails.isPresent()) {
					continue;
				}
				executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
			}
			executiveTaskExecutionView.setExecutiveTaskExecutionDetailViews(executiveTaskExecutionDetailViews);
			executiveTaskExecutionViews.add(executiveTaskExecutionView);
		}
		return executiveTaskExecutionViews;
	}

	private List<Long> getUserIdsUnderCurrentUser(String employeePid) {
		List<Long> userIds;
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());
				Set<Long> uniqueIds = new HashSet<>();
				if (!dashboardUserIds.isEmpty()) {
					for (Long uid : userIds) {
						for (Long sid : dashboardUserIds) {
							if (uid.equals(sid)) {
								uniqueIds.add(sid);
							}
						}
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}

			}
		} else {
			userIds = Collections.emptyList();
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

	@RequestMapping(value = "/ecom-sales-reports/loadAccountProfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> loadAccountProfile(@RequestParam("employeePid") String employeePid) {
		Optional<EmployeeProfileDTO> optionalEmployeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		List<Location> locations = new ArrayList<>();
		List<AccountProfile> accountProfiles = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (optionalEmployeeProfileDTO.isPresent()) {
			locations = employeeProfileLocationRepository
					.findLocationsByUserPid(optionalEmployeeProfileDTO.get().getUserPid());
		}
		if (!locations.isEmpty()) {
			accountProfiles = locationAccountProfileRepository.findAccountProfileByLocationIdIn(
					locations.stream().map(Location::getId).collect(Collectors.toList()));
		}
		if (!accountProfiles.isEmpty()) {
			accountProfileDTOs = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfiles);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	private List<InventoryVoucherHeader> findInventoryVoucherHeaderByUser(String userPid, List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<String> accountProfilePids) {
		// product groups
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserPid(userPid);
		if (productGroups.isEmpty()) {
			return Collections.emptyList();
		}
		// ecom-product by product group
		List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductsRepository
				.findByProductGroups(productGroups);
		if (productGroupEcomProducts.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> ecomProductProfilePids = productGroupEcomProducts.stream().map(p -> p.getEcomProduct().getPid())
				.collect(Collectors.toSet());
		List<ProductProfile> productProfiles = ecomProductProfileProductRepository
				.findProductByEcomProductProfilePidIn(ecomProductProfilePids);
		if (productProfiles.isEmpty()) {
			return Collections.emptyList();
		}
		List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
				.findAllByCompanyIdAndAccountPidInAndProductPidInAndDateBetween(accountProfilePids, productProfiles,
						documents, fromDate, toDate);
		// mapped by InventoryVoucherHeader
		Map<InventoryVoucherHeader, List<InventoryVoucherDetail>> mapInventoryVoucherDetail = inventoryVoucherDetails
				.parallelStream().collect(Collectors.groupingBy(InventoryVoucherDetail::getInventoryVoucherHeader));
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		for (Map.Entry<InventoryVoucherHeader, List<InventoryVoucherDetail>> entry : mapInventoryVoucherDetail
				.entrySet()) {
			InventoryVoucherHeader inventoryVoucherHeader = entry.getKey();
			inventoryVoucherHeader.setInventoryVoucherDetails(entry.getValue());
			inventoryVoucherHeaders.add(inventoryVoucherHeader);
		}
		return inventoryVoucherHeaders;

	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/ecom-sales-reports/inventory-voucher/{pid}/{employeePid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid,
			@PathVariable String employeePid) {
		log.debug("Web request to get ecom-sales-reports inventoryVoucherDTO by pid : {}", pid);
		if (employeePid.equals("no") || employeePid.equals("Dashboard Employees")) {
			return inventoryVoucherService.findOneByPid(pid)
					.map(inventoryVoucherDTO -> new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK))
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		}
		// find employees user
		Optional<EmployeeProfileDTO> optionalEmp = employeeProfileService.findOneByPid(employeePid);
		if (!optionalEmp.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		String userPid = optionalEmp.get().getUserPid();
		Optional<InventoryVoucherHeaderDTO> opIV = inventoryVoucherService.findOneByPid(pid);
		if (opIV.isPresent()) {
			InventoryVoucherHeaderDTO iv = opIV.get();
			// product groups
			List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserPid(userPid);
			if (productGroups.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			// ecom-product by product group
			List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductsRepository
					.findByProductGroups(productGroups);
			if (productGroupEcomProducts.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			Set<String> ecomProductProfilePids = productGroupEcomProducts.stream().map(p -> p.getEcomProduct().getPid())
					.collect(Collectors.toSet());
			List<ProductProfile> productProfiles = ecomProductProfileProductRepository
					.findProductByEcomProductProfilePidIn(ecomProductProfilePids);
			if (productProfiles.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = iv.getInventoryVoucherDetails().stream()
					.filter(ivd -> {
						return productProfiles.stream().anyMatch(p -> p.getPid().equals(ivd.getProductPid()));
					}).collect(Collectors.toList());
			iv.setInventoryVoucherDetails(inventoryVoucherDetailDTOs);
			return new ResponseEntity<>(iv, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
