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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.custom.InventoryVoucherDetailCustomRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

@Controller
@RequestMapping("/web")
public class ItemWiseSaleResourceJonarin {

	private final Logger log = LoggerFactory.getLogger(ItemWiseSaleResourceJonarin.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private InventoryVoucherDetailCustomRepository inventoryVoucherDetailCustomRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	@Inject
	ProductGroupProductRepository productGroupProductRepository;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private LocationRepository locationRepository;

	@RequestMapping(value = "/item-wise-sale-jonarin", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllItemWiseSales(Model model) throws URISyntaxException {
		log.debug("Web request to get All ItemWiseSales");
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		model.addAttribute("territories",locationRepository.findAllByCompanyId());
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);
		// model.addAttribute("employees",)
		return "company/itemWiseSalesJonarin";
	}

	@RequestMapping(value = "/item-wise-sale-jonarin/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) throws URISyntaxException {
		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService
				.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
		return documentDTOs;
	}

	@RequestMapping(value = "/item-wise-sale-jonarin/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterInventoryVoucherHeaders(@RequestParam String sort,
			@RequestParam String order, @RequestParam String categoryPids, @RequestParam String groupPids,
			@RequestParam("voucherType") VoucherType voucherType, @RequestParam("documentPid") String documentPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(value = "fromdeliveryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDeliveryDate,
			@RequestParam(value = "	toDeliveryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDeliveryDate,
			@RequestParam String stockLocations, @RequestParam String profilePids,
			@RequestParam("employeePid") String employeePids, @RequestParam boolean inclSubordinate,
			@RequestParam String accountPids, @RequestParam String terittoryPids) {

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

		if (filterBy.equals("TODAY")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					LocalDate.now(), LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids,terittoryPids);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					yeasterday, yeasterday, stockLocations, profilePids, employeePids, inclSubordinate, accountPids,terittoryPids);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					weekStartDate, LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids,terittoryPids);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					monthStartDate, LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids,terittoryPids);
		} else if (filterBy.equals("CUSTOM")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					fromDate, toDate, stockLocations, profilePids, employeePids, inclSubordinate, accountPids,terittoryPids);
		}
		return new ResponseEntity<>(inventoryVoucherDetailDTOs, HttpStatus.OK);

	}

	
	private List<InventoryVoucherDetailDTO> getFilterData(String sort, String order, String categoryPids,
			String groupPids, VoucherType voucherType, String documentPid, LocalDate fDate, LocalDate tDate,
			String stockLocations, String profilePids, String employeePids, boolean inclSubordinate,
			String accountPids, String terittoryPids) {

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOList = new ArrayList<>();
		List<ProductGroupProduct> productProfilesProductGroup = productGroupProductRepository.findAllByCompanyId();
		List<LocationAccountProfile> LocationAccountProfile = locationAccountProfileRepository.findAllByCompanyId();
		System.out.println(productProfilesProductGroup.size());

		List<String> stockLocationPids = new ArrayList<>();
		List<String> productCategoryPids = new ArrayList<>();
		List<String> productGroupPids = new ArrayList<>();
		List<String> productProfilePids = new ArrayList<>();
		List<String> productTerritoryPids = new ArrayList<>();
		List<String> documentPids = new ArrayList<>();
		List<String> employeePidList = new ArrayList<>();
		List<String> accountPidList = new ArrayList<>();
		String status = "";// empty string passed just to match the function arguments

		employeePidList = employeePids != "" ? Arrays.asList(employeePids.split(",")) : employeePidList;
		stockLocationPids = stockLocations != "" ? Arrays.asList(stockLocations.split(",")) : stockLocationPids;
		productCategoryPids = categoryPids != "" ? Arrays.asList(categoryPids.split(",")) : productCategoryPids;
		productGroupPids = groupPids != "" ? Arrays.asList(groupPids.split(",")) : productGroupPids;
		productProfilePids = profilePids != "" ? Arrays.asList(profilePids.split(",")) : productProfilePids;
		productTerritoryPids = terittoryPids != "" ? Arrays.asList(terittoryPids.split(",")) : productTerritoryPids;
		System.out.println(	productTerritoryPids);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all pids by company";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountPidList = !accountPids.equals("-1") ? Arrays.asList(accountPids)
				: accountProfileRepository.findAllPidsByCompany();

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

		if (documentPid.equals("no")) {
			List<DocumentDTO> documentDTOs = primarySecondaryDocumentService
					.findAllDocumentsByCompanyIdAndVoucherType(voucherType);

			for (DocumentDTO documentDTO : documentDTOs) {
				documentPids.add(documentDTO.getPid());
			}
		} else {
			documentPids = Arrays.asList(documentPid.split(","));
		}

		if (inclSubordinate && employeePidList.size() == 1) {
			List<Long> employeeIds = employeeHierarchyService.getEmployeeSubordinateEmployeeIds(employeePidList.get(0));
			employeePidList = employeeProfileRepository.findEmployeeByIdsIn(employeeIds);
		}
		System.out.println("HI......");
		inventoryVoucherDetailDTOs = inventoryVoucherDetailCustomRepository.getInventoryDetailListBy(
				productCategoryPids, productGroupPids, productProfilePids, stockLocationPids, fromDate, toDate,
				documentPids, productTerritoryPids, employeePidList, status, accountPidList);
		
		for(InventoryVoucherDetailDTO inventoryVoucherDTO : inventoryVoucherDetailDTOs) {
			List<String> productGroups = new ArrayList();
			List<ProductGroupProduct> optProductGroupProductList = productProfilesProductGroup.stream().filter(p -> p.getProduct().getPid().equals(inventoryVoucherDTO.getProductPid())).collect(Collectors.toList());
			
			Optional<LocationAccountProfile> LocationAccountProfileOp = LocationAccountProfile.stream().
				    filter(a->a.getAccountProfile().getPid().equals(inventoryVoucherDTO.getAccountPid())).findAny();
		
			if(LocationAccountProfileOp.isPresent()) {
			
				LocationAccountProfile locationAccountProfile = LocationAccountProfileOp.get();
				inventoryVoucherDTO.setTerritory(locationAccountProfile.getLocation().getName());
			}
			
			
			Optional<ProductGroupProduct> optProductGroupProduct = productProfilesProductGroup.stream().filter(p -> p.getProduct().getPid().equals(inventoryVoucherDTO.getProductPid())).findAny();
	         
			if(!optProductGroupProductList.isEmpty()) {
				for(ProductGroupProduct productGroupProduct : optProductGroupProductList) {
					productGroups.add(productGroupProduct.getProductGroup().getName());
				}
				inventoryVoucherDTO.setProductGroups(productGroups);	
			}
			
			if(optProductGroupProduct.isPresent()) {
				
				ProductGroupProduct productGroupProduct = optProductGroupProduct.get();
				inventoryVoucherDTO.setProductGroup(productGroupProduct.getProductGroup().getName());
			}
			inventoryVoucherDetailDTOList.add(inventoryVoucherDTO);
		}

		return filterBySortAndOrder(sort, order, inventoryVoucherDetailDTOList);
	}

	private List<InventoryVoucherDetailDTO> filterBySortAndOrder(String sort, String order,
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs) {

		if (sort.equals("date")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate));
			}
		} else if (sort.equals("item")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName));
			}
		} else if (sort.equals("quantity")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity));
			}
		} else if (sort.equals("category")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory));
			}
		}
		return inventoryVoucherDetailDTOs;
	}

}
