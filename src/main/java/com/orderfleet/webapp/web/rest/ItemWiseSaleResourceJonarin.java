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

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.web.rest.dto.QueryTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.enums.VoucherType;
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

	@Inject
	private CompanyRepository companyRepository;


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

		log.debug("Enter : "+ "getFilterData() : ");

		Company company =
					companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOList = new ArrayList<>();

		log.debug("Enter :  productGroupProductRepository.findAllByCompanyId()");

		List<ProductGroupProduct> productProfilesProductGroup =
				productGroupProductRepository.findAllByCompanyId();

		log.debug("Exit :  productGroupProductRepository.findAllByCompanyId() : " + productProfilesProductGroup.size());

		List<String> stockLocationPids = new ArrayList<>();
		List<String> productCategoryPids = new ArrayList<>();
		List<String> productGroupPids = new ArrayList<>();
		List<String> productProfilePids = new ArrayList<>();
		List<String> productTerritoryPids = new ArrayList<>();
		List<String> documentPids = new ArrayList<>();
		List<String> employeePidList = new ArrayList<>();
		List<String> accountPidList = new ArrayList<>();
		String status = "" ;// empty string passed just to match the function arguments

		employeePidList = employeePids != "" ? Arrays.asList(employeePids.split(",")) : employeePidList;
		stockLocationPids = stockLocations != "" ? Arrays.asList(stockLocations.split(",")) : stockLocationPids;
		productCategoryPids = categoryPids != "" ? Arrays.asList(categoryPids.split(",")) : productCategoryPids;
		productGroupPids = groupPids != "" ? Arrays.asList(groupPids.split(",")) : productGroupPids;
		productProfilePids = profilePids != "" ? Arrays.asList(profilePids.split(",")) : productProfilePids;
		productTerritoryPids = terittoryPids != "" ? Arrays.asList(terittoryPids.split(",")) : productTerritoryPids;

		log.debug("employeePidList 		   : " + employeePidList.size());
		log.debug("stockLocationPids     : " +   stockLocationPids.size());
		log.debug("productCategoryPids   : " +  productCategoryPids.size());
		log.debug("productGroupPids 	   : " +  productGroupPids.size());
		log.debug("productProfilePids  	 : " +  productProfilePids.size());
		log.debug("productTerritoryPids  : " +  productTerritoryPids.size());

		List<LocationAccountProfile> LocationAccountProfiles = getLocationAccounts(accountPids,terittoryPids,company);

		accountPidList = getAccountPids(accountPids, terittoryPids, accountPidList, LocationAccountProfiles);

		log.debug("productTerritoryPids : " + "  " + productTerritoryPids.size());

			 log.debug("accountPidList : " +  accountPidList.size());
			 log.debug("Enter : "+ "documentPid.equals(no) : ");

		if (documentPid.equals("no")) {
			log.debug("Conditon - TRUE : "+ "documentPid.equals(no) : "+ documentPid );

			List<DocumentDTO> documentDTOs =
					primarySecondaryDocumentService
							.findAllDocumentsByCompanyIdAndVoucherType(voucherType);

			for (DocumentDTO documentDTO : documentDTOs) {
				documentPids.add(documentDTO.getPid());
				log.debug("Conditon - FALSE  : " + "documentPid.equals(no) : "+ documentPids );
			}
		} else {
			documentPids = Arrays.asList(documentPid.split(","));
		}

		log.debug("Enter : " + "if(inclSubordinate && employeePidList.size() == 1)" );
		if (inclSubordinate && employeePidList.size() == 1) {
			log.debug("inclSubordinate : " + inclSubordinate +  "employeePidList.size() : " + employeePidList.size() );
			List<Long> employeeIds =
					employeeHierarchyService
							.getEmployeeSubordinateEmployeeIds(
									employeePidList.get(0));

			employeePidList =
					employeeProfileRepository
							.findEmployeeByIdsIn(employeeIds);
		}
		log.debug("Exit : "+ "if (inclSubordinate && employeePidList.size() == 1)" );


		log.debug( "Enter :  getInventoryDetailListByItemWiseSalesJonarinOptmised() " + LocalDateTime.now());
		log.debug("========================================================================================================");
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOsold = new ArrayList<>();
		inventoryVoucherDetailDTOs =
				inventoryVoucherDetailCustomRepository
						.getInventoryDetailListByItemWiseSalesJonarinOptmised(
								company.getId(), productCategoryPids, productGroupPids, productProfilePids,
								stockLocationPids, fromDate, toDate, documentPids,
								productTerritoryPids, employeePidList, status, accountPidList);
		log.debug("Exit : "+ "getInventoryDetailListByItemWiseSalesJonarinOptmised () " + LocalDateTime.now());
		log.debug("========================================================================================================");


		log.debug("Enter : for(InventoryVoucherDetailDTO inventoryVoucherDTO : inventoryVoucherDetailDTOs)");
		for(InventoryVoucherDetailDTO inventoryVoucherDTO : inventoryVoucherDetailDTOs) {
			List<String> productGroups = new ArrayList();

			List<ProductGroupProduct> optProductGroupProductList =
					productProfilesProductGroup
							.stream()
							.filter(p -> p.getProduct().getPid()
									.equals(inventoryVoucherDTO.getProductPid()))
							.collect(Collectors.toList());
			
			Optional<LocationAccountProfile>
					LocationAccountProfileOp =
					LocationAccountProfiles
							.stream()
							.filter(a->a.getAccountProfile().getPid()
									.equals(inventoryVoucherDTO.getAccountPid()))
							.findAny();

			if(LocationAccountProfileOp.isPresent()) {
				LocationAccountProfile locationAccountProfile = LocationAccountProfileOp.get();
				inventoryVoucherDTO.setTerritory(locationAccountProfile.getLocation().getName());
			}


			Optional<ProductGroupProduct> optProductGroupProduct =
					productProfilesProductGroup
							.stream()
							.filter(p -> p.getProduct().getPid()
									.equals(inventoryVoucherDTO.getProductPid()))
							.findAny();

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
		log.debug("Exit : "+ "getFilterData() : ");
		return filterBySortAndOrder(sort, order, inventoryVoucherDetailDTOList);
	}

	private 	List<LocationAccountProfile> getLocationAccounts(String accountPids, String terittoryPids, Company company) {
		log.debug("Enter : Method - getLocationAccounts");
		List<String> productTerritoryPids  = !terittoryPids.equals("-1") ? Arrays.asList(terittoryPids) : Arrays.asList("-1");
		List<String> accountPidList = accountPids != "-1" ? Arrays.asList(accountPids.split(",")) : Arrays.asList("-1");
		List<LocationAccountProfile> LocationAccountProfile = new ArrayList<>();
		if(terittoryPids.equals("-1")){
			log.debug("Enter : Query - locationAccountProfileRepository.findByCompanyID ");
			LocationAccountProfile =
					locationAccountProfileRepository.findByCompanyID(company.getId());
			log.debug("Enter : Query - locationAccountProfileRepository.findByCompanyID  : " +LocationAccountProfile.size() );
		} else if(!terittoryPids.equals("-1") && !accountPids.equals("-1") ){
			log.debug("Enter : Query - locationAccountProfileRepository.findAllByCompanyId : ");
			LocationAccountProfile =
					locationAccountProfileRepository
							.findByAccountPids(accountPidList);
			log.debug("Exit : Query - locationAccountProfileRepository.findAllByCompanyId : " + LocationAccountProfile.size() );
		}else{
			log.debug("Enter : Query - locationAccountProfileRepository.findAllByCompanyId : ");
			LocationAccountProfile =
					locationAccountProfileRepository
							.findByLocationPids(productTerritoryPids);
			log.debug("Exit  : Query - locationAccountProfileRepository.findAllByCompanyId : " + LocationAccountProfile.size() );
		}
		log.debug("Exit : Method - getLocationAccounts");
		return LocationAccountProfile;
	}

	private List<String> getAccountPids(String accountPids, String terittoryPids,
																			List<String> accountPidList, List<LocationAccountProfile> LocationAccountProfiles) {
		if(!terittoryPids.equals("-1") && accountPids.equals("-1")){
			log.debug("Streaming LocationAccountProfiles : " + LocationAccountProfiles.size());
			accountPidList = LocationAccountProfiles.stream().map(data->data.getAccountProfile().getPid()).collect(Collectors.toList());
		}
		else{
			accountPidList = Arrays.asList(accountPids.split(","));
		}
		log.debug("accountPidList  : "+ " Size : " + accountPidList.size());
		return accountPidList;
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

	
	private QueryTime queryRunStatusIntialize(String id, String description) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		QueryTime queryTime = new QueryTime(startLCTime,startTime,startDate);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		return queryTime;
	}


	private void queryRunStatus(String id, String description, LocalDateTime startLCTime, String startTime) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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


}
