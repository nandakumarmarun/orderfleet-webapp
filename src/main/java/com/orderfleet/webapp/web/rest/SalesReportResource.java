package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.custom.InventoryVoucherDetailCustomRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.report.dto.SalesReportDTO;

@Controller
@RequestMapping("/web")
public class SalesReportResource {
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;
	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;
	
	@Inject
	private InventoryVoucherDetailCustomRepository inventoryVoucherDetailCustomRepository;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ProductProfileService productProfileService;
	@Inject
	private StockLocationRepository stockLocationRepository;

	@RequestMapping(value = "/sales-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String salesReport(Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);

		return "company/sales-report/sales-report";
	}
	
	@RequestMapping(value = "/not-ordered-products", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String notOrderedProductReport(Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);

		return "company/sales-report/notOrderedProducts";
	}
	@RequestMapping(value = "/userwise-product-group-summary", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String userwiseProductGroupSummary(Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);

		return "company/sales-report/userwiseProductGroupSummary";
	}

	@RequestMapping(value = "/sales-report/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) throws URISyntaxException {

		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService
				.findAllDocumentsByCompanyIdAndVoucherType(voucherType);

		return documentDTOs;
	}

	@RequestMapping(value = "/sales-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterInventoryVouchers(@RequestParam String pGroupPid,

			@RequestParam("employeePid") String employeePid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
			@RequestParam boolean inclSubordinate, @RequestParam("voucherType") VoucherType voucherType) {
		if (filterBy.equals(SalesReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;

		} else if (filterBy.equals(SalesReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(SalesReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			fromDate = weekStartDate;
			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.MTD)) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			fromDate = monthStartDate;

			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.CUSTOM)) {

		}

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDtos = filterByEmployeeAccountAndDate(employeePid,
				accountPid, fromDate, toDate, inclSubordinate, voucherType);

		if (!"no".equalsIgnoreCase(pGroupPid)) {
			Set<String> productPids = productGroupProductRepository.findProductPidByProductGroupPid(pGroupPid);
			inventoryVoucherDetailDtos = inventoryVoucherDetailDtos.stream()
					.filter(ivd -> productPids.stream().anyMatch(pid -> pid.equals(ivd.getProductPid())))
					.collect(Collectors.toList());
		}
		return new ResponseEntity<>(inventoryVoucherDetailDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/sales-report/filter/not-ordered", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<String>> filterNotOrderedProducts(@RequestParam String pGroupPid,
			@RequestParam("employeePid") String employeePid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
			@RequestParam boolean inclSubordinate, @RequestParam("voucherType") VoucherType voucherType) {
		if (filterBy.equals(SalesReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(SalesReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		}
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDtos = filterByEmployeeAccountAndDate(employeePid,
				accountPid, fromDate, toDate, inclSubordinate, voucherType);
		List<String> productNames;
		if ("no".equals(pGroupPid)) {
			productNames = productProfileRepository
					.findProductNamesByCompanyIdAndActivatedTrueAndCreatedLessThan(toDate.atTime(23, 59));
		} else {
			productNames = productGroupProductRepository
					.findProductNameByProductGroupPidAndActivatedTrueAndCreatedLessThan(pGroupPid,
							toDate.atTime(23, 59));
		}
		Set<String> ivList = inventoryVoucherDetailDtos.stream().map(InventoryVoucherDetailDTO::getProductName)
				.collect(Collectors.toSet());
		return new ResponseEntity<>(filterNotOrderedProduct(ivList, productNames), HttpStatus.OK);
	}

	@RequestMapping(value = "/sales-report/filter/userwise-group-summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<SalesReportDTO> filterInventoryVouchersUserwiseGroupSummary(@RequestParam String pGroupNames,
			@RequestParam("employeePid") String employeePid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
			@RequestParam boolean inclSubordinate, @RequestParam("voucherType") VoucherType voucherType) {
		if (filterBy.equals(SalesReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(SalesReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(SalesReportResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		}
		List<String> reportHeaderNames = Arrays.asList(pGroupNames.split("\\s*,\\s*"));
		if (reportHeaderNames.isEmpty()) {
			return null;
		}
		List<List<String>> reportValues = getEmployeeWiseGroupSummary(reportHeaderNames, employeePid, accountPid,
				fromDate, toDate, inclSubordinate, voucherType);
		return new ResponseEntity<>(new SalesReportDTO(reportHeaderNames, reportValues), HttpStatus.OK);
	}

	private List<InventoryVoucherDetailDTO> filterByEmployeeAccountAndDate(String employeePid, String accountPid,
			LocalDate fDate, LocalDate tDate, boolean inclSubordinate, VoucherType voucherType) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<String> accountProfilePids;
		List<Long> userIds = getUserIdsUnderEmployee(employeePid, inclSubordinate);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		if (accountPid.equalsIgnoreCase("no")) {
			accountProfilePids = getAccountPids(userIds);
		} else {
			accountProfilePids = Arrays.asList(accountPid);
		}
		List<Document> documents = primarySecondaryDocumentRepository
				.findDocumentsByVoucherTypeAndCompanyId(voucherType);
		List<Long> documentIds = documents.stream().map(Document::getId).collect(Collectors.toList());

		List<Object[]> ivDetailDtos = inventoryVoucherDetailRepository
				.findByAccountPidInAndEmployeeIdInAndDocumentIdInDateBetween(accountProfilePids, userIds, documentIds,
						fromDate, toDate);
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		if (!ivDetailDtos.isEmpty()) {
			for (Object[] object : ivDetailDtos) {
				InventoryVoucherDetailDTO ivDetailDto = new InventoryVoucherDetailDTO();
				ivDetailDto.setEmployeeName(object[0].toString());
				ivDetailDto.setProductPid(object[1].toString());
				ivDetailDto.setProductName(object[2].toString());
				ivDetailDto.setProductUnitQty(object[3] == null ? 1d : (Double) object[3]);
				ivDetailDto.setQuantity((Double) object[4]);
				ivDetailDto.setAccountName(object[5].toString());
				ivDetailDto.setCreatedDate((LocalDateTime) object[6]);
				ivDetailDto.setVisitRemarks(object[7] == null ? null : object[7].toString());
				inventoryVoucherDetailDTOs.add(ivDetailDto);
			}
		}
		return inventoryVoucherDetailDTOs;

	}

	private List<String> filterNotOrderedProduct(Set<String> orderedProductNames, List<String> productNames) {
		if (orderedProductNames.isEmpty()) {
			return productNames;
		} else {
			return productNames.stream().filter(p -> !orderedProductNames.contains(p)).collect(Collectors.toList());
		}
	}

	private List<List<String>> getEmployeeWiseGroupSummary(List<String> reportHeaderNames, String employeePid,
			String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate, VoucherType voucherType) {
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDtos = filterByEmployeeAccountAndDate(employeePid,
				accountPid, fDate, tDate, inclSubordinate, voucherType);
		// group employee wise
		Map<String, List<InventoryVoucherDetailDTO>> employeeWiseGrouped = inventoryVoucherDetailDtos.stream()
				.collect(Collectors.groupingBy(InventoryVoucherDetailDTO::getEmployeeName, TreeMap::new,
						Collectors.mapping(ivd -> ivd, Collectors.toList())));
		// get products by productGroup
		List<ProductGroupProduct> productGroupsProducts = productGroupProductRepository
				.findByProductGroupProductActivatedAndCompanyId();
		Map<String, List<String>> productGroupByProducts = productGroupsProducts.stream()
				.collect(Collectors.groupingBy(pgp -> pgp.getProductGroup().getName(),
						Collectors.mapping(pgp -> pgp.getProduct().getPid(), Collectors.toList())));

		List<List<String>> reportValues = new ArrayList<>();
		for (Map.Entry<String, List<InventoryVoucherDetailDTO>> entry : employeeWiseGrouped.entrySet()) {
			List<String> values = new ArrayList<>();
			values.add(entry.getKey());
			for (String headerName : reportHeaderNames) {
				double volume = 0;
				for (InventoryVoucherDetailDTO ivd : entry.getValue()) {
					boolean idExists = productGroupByProducts.getOrDefault(headerName, new ArrayList<>())
							.contains(ivd.getProductPid());
					if (idExists) {
						volume += ivd.getQuantity() * (ivd.getProductUnitQty() == null ? 1 : ivd.getProductUnitQty());
					}
				}
				values.add(volume + "");
			}
			reportValues.add(values);
		}
		return reportValues;
	}

	private List<Long> getUserIdsUnderEmployee(String employeePid, boolean inclSubordinate) {
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

	private List<String> getAccountPids(List<Long> userIds) {
		List<String> accountPids = new ArrayList<>();
		if (userIds.isEmpty()) {
			  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get accPids by activated and company";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountPids = accountProfileRepository.findAccountPidsByActivatedAndCompany();
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
			Set<String> pids = locationAccountProfileService.findAccountProfilePidsByUsers(userIds);
			accountPids.addAll(pids);
		}
		return accountPids;
	}

}
