package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.custom.InventoryVoucherDetailCustomRepository;
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
public class ItemWiseSaleResource {

	private final Logger log = LoggerFactory.getLogger(ItemWiseSaleResource.class);

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

	@RequestMapping(value = "/item-wise-sale", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllItemWiseSales(Model model) throws URISyntaxException {
		log.debug("Web request to get All ItemWiseSales");
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);
		// model.addAttribute("employees",)
		return "company/itemWiseSales";
	}

	@RequestMapping(value = "/item-wise-sale/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) throws URISyntaxException {
		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService
				.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
		return documentDTOs;
	}

	@RequestMapping(value = "/item-wise-sale/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterInventoryVoucherHeaders(@RequestParam String sort,
			@RequestParam String order, @RequestParam String categoryPids, @RequestParam String groupPids,
			@RequestParam("voucherType") VoucherType voucherType, @RequestParam("documentPid") String documentPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam String stockLocations, @RequestParam String profilePids,
			@RequestParam("employeePid") String employeePids, @RequestParam boolean inclSubordinate,
			@RequestParam String accountPids) {

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

		if (filterBy.equals("TODAY")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					LocalDate.now(), LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					yeasterday, yeasterday, stockLocations, profilePids, employeePids, inclSubordinate, accountPids);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					weekStartDate, LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					monthStartDate, LocalDate.now(), stockLocations, profilePids, employeePids, inclSubordinate,
					accountPids);
		} else if (filterBy.equals("CUSTOM")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					fromDate, toDate, stockLocations, profilePids, employeePids, inclSubordinate, accountPids);
		}
		return new ResponseEntity<>(inventoryVoucherDetailDTOs, HttpStatus.OK);

	}

	private List<InventoryVoucherDetailDTO> getFilterData(String sort, String order, String categoryPids,
			String groupPids, VoucherType voucherType, String documentPid, LocalDate fDate, LocalDate tDate,
			String stockLocations, String profilePids, String employeePids, boolean inclSubordinate,
			String accountPids) {

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

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
		accountPidList = !accountPids.equals("-1") ? Arrays.asList(accountPids)
				: accountProfileRepository.findAllPidsByCompany();

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

		inventoryVoucherDetailDTOs = inventoryVoucherDetailCustomRepository.getInventoryDetailListBy(
				productCategoryPids, productGroupPids, productProfilePids, stockLocationPids, fromDate, toDate,
				documentPids, productTerritoryPids, employeePidList, status, accountPidList);

		return filterBySortAndOrder(sort, order, inventoryVoucherDetailDTOs);
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
