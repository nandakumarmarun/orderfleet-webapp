package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.custom.InventoryVoucherDetailCustomRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

@Controller
@RequestMapping("/web")
public class ItemSummaryEmployeeWiseResource {

	private final Logger log = LoggerFactory.getLogger(ItemSummaryEmployeeWiseResource.class);

	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private LocationService locationService;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private InventoryVoucherDetailCustomRepository inventoryVoucherDetailCustomRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@RequestMapping(value = "/item-summary-employee-wise", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllItemSummaryEmployeeWise(Model model) throws URISyntaxException {
		log.debug("Web request to get All ItemWiseSummary");
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("stockLocations", stockLocationRepository.findAllByCompanyId());
		model.addAttribute("locations", locationService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/item-summary-employee-wise";
	}

	@RequestMapping(value = "/item-summary-employee-wise/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) throws URISyntaxException {
		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService
				.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
		return documentDTOs;
	}

	@RequestMapping(value = "/item-summary-employee-wise/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterInventoryVoucherHeaders(@RequestParam String sort,
			@RequestParam String order, @RequestParam String categoryPids, @RequestParam String groupPids,
			@RequestParam("voucherType") VoucherType voucherType, @RequestParam("documentPid") String documentPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam String stockLocations, @RequestParam String profilePids,
			@RequestParam("territoryPids") String territoryPids, @RequestParam("statusSearch") String status,
			@RequestParam("employeePid") String employeePids, @RequestParam String accountPids) {

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

		if (filterBy.equals("TODAY")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					LocalDate.now(), LocalDate.now(), stockLocations, profilePids, territoryPids, employeePids, status,
					accountPids);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					yeasterday, yeasterday, stockLocations, profilePids, territoryPids, employeePids, status,
					accountPids);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					weekStartDate, LocalDate.now(), stockLocations, profilePids, territoryPids, employeePids, status,
					accountPids);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					monthStartDate, LocalDate.now(), stockLocations, profilePids, territoryPids, employeePids, status,
					accountPids);
		} else if (filterBy.equals("CUSTOM")) {
			inventoryVoucherDetailDTOs = getFilterData(sort, order, categoryPids, groupPids, voucherType, documentPid,
					fromDate, toDate, stockLocations, profilePids, territoryPids, employeePids, status, accountPids);
		}
		return new ResponseEntity<>(inventoryVoucherDetailDTOs, HttpStatus.OK);

	}

	private List<InventoryVoucherDetailDTO> getFilterData(String sort, String order, String categoryPids,
			String groupPids, VoucherType voucherType, String documentPid, LocalDate fDate, LocalDate tDate,
			String stockLocations, String profilePids, String territoryPids, String employeePids, String status,
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

		stockLocationPids = stockLocations != "" ? Arrays.asList(stockLocations.split(",")) : stockLocationPids;
		productCategoryPids = categoryPids != "" ? Arrays.asList(categoryPids.split(",")) : productCategoryPids;
		productGroupPids = groupPids != "" ? Arrays.asList(groupPids.split(",")) : productGroupPids;
		productProfilePids = profilePids != "" ? Arrays.asList(profilePids.split(",")) : productProfilePids;
		productTerritoryPids = territoryPids != "" ? Arrays.asList(territoryPids.split(",")) : productTerritoryPids;
		employeePidList = employeePids != "" ? Arrays.asList(employeePids.split(",")) : employeePidList;
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

		inventoryVoucherDetailDTOs = inventoryVoucherDetailCustomRepository.getInventoryDetailListBy(
				productCategoryPids, productGroupPids, productProfilePids, stockLocationPids, fromDate, toDate,
				documentPids, productTerritoryPids, employeePidList, status, accountPidList);

		return filterByCumulative(filterBySortAndOrder(sort, order, inventoryVoucherDetailDTOs));
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

	private List<InventoryVoucherDetailDTO> filterByCumulative(
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs) {

		Set<InventoryVoucherDetailDTO> ivdSet = new HashSet<>();
		List<InventoryVoucherDetailDTO> ivdDTOs = new ArrayList<>();

		inventoryVoucherDetailDTOs.forEach(ivd -> ivdSet.add(ivd));

		ivdSet.forEach(ivdset -> {
			double cumulative = inventoryVoucherDetailDTOs.stream()
					.filter(ivd -> ivd.getProductName().equals(ivdset.getProductName()))
					.mapToDouble(InventoryVoucherDetailDTO::getQuantity).sum();
			InventoryVoucherDetailDTO ivdDTO = new InventoryVoucherDetailDTO();
			ivdDTO.setProductName(ivdset.getProductName());
			ivdDTO.setQuantity(cumulative);
			ivdDTOs.add(ivdDTO);
		});

		return ivdDTOs;
	}
}
