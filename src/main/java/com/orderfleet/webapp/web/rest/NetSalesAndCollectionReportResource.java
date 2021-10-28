package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.NetSalesAndCollectionDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Controller
@RequestMapping("/web")
public class NetSalesAndCollectionReportResource {

	private final Logger log = LoggerFactory.getLogger(NetSalesAndCollectionReportResource.class);

	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherdetailRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private StockDetailsService netSalesAndCollectionReportService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@RequestMapping(value = "/netSalesAndCollectionReport", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of netSalesAndCollectionReport");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			String user = SecurityUtils.getCurrentUserLogin();
			System.out.println("if.." + userIds.size());
			long userId = userRepository.getIdByLogin(user);
			userIds = new ArrayList<>();
			userIds.add(userId);
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		} else {
			System.out.println("else .." + userIds.size());
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/netSalesAndCollectionReport";
	}

	@RequestMapping(value = "/netSalesAndCollectionReport/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<NetSalesAndCollectionDTO> filter(@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter Net Sales And Collection Report");

		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(NetSalesAndCollectionReportResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(NetSalesAndCollectionReportResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(NetSalesAndCollectionReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(NetSalesAndCollectionReportResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = fDate;

		}
		NetSalesAndCollectionDTO netSalesAndCollectionDTO = getFilterData(employeePids, fDate, tDate);

		return new ResponseEntity<>(netSalesAndCollectionDTO, HttpStatus.OK);

	}

	private NetSalesAndCollectionDTO getFilterData(List<String> employeePids, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		NetSalesAndCollectionDTO netSalesAndCollectionDTO = new NetSalesAndCollectionDTO();
		if (userIds.isEmpty()) {
			netSalesAndCollectionDTO.setNetCollectionAmount(0.0);
			netSalesAndCollectionDTO.setNetCollectionAmountCash(0.0);
			netSalesAndCollectionDTO.setNetCollectionAmountCheque(0.0);
			netSalesAndCollectionDTO.setNetCollectionAmountRtgs(0.0);
			netSalesAndCollectionDTO.setNetSaleAmount(0.0);
			return netSalesAndCollectionDTO;
		}
		String id="INV_QUERY_199";
		String description="Finding net sales amount by UserId";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		Object[] netSaleAmount = inventoryVoucherHeaderRepository.findnetSaleAmountByUserIdandDateBetween(userIds,
				fromDate, toDate);
		Object[] netCollectionAmount = accountingVoucherHeaderRepository
				.findnetCollectionAmountByUserIdandDateBetween(userIds, fromDate, toDate);

		Object[] netCollectionAmountCash = accountingVoucherdetailRepository
				.findnetCollectionAmountByUserIdandDateBetweenAndPaymentMode(userIds, fromDate, toDate,
						PaymentMode.Cash);
		Object[] netCollectionAmountCheque = accountingVoucherdetailRepository
				.findnetCollectionAmountByUserIdandDateBetweenAndPaymentMode(userIds, fromDate, toDate,
						PaymentMode.Cheque);
		Object[] netCollectionAmountRtgs = accountingVoucherdetailRepository
				.findnetCollectionAmountByUserIdandDateBetweenAndPaymentMode(userIds, fromDate, toDate,
						PaymentMode.RTGS);

		netSalesAndCollectionDTO.setNetCollectionAmount(
				netCollectionAmount[0] != null ? Double.parseDouble(netCollectionAmount[0].toString()) : 0.0);
		netSalesAndCollectionDTO.setNetCollectionAmountCash(
				netCollectionAmountCash[0] != null ? Double.parseDouble(netCollectionAmountCash[0].toString()) : 0.0);
		netSalesAndCollectionDTO.setNetCollectionAmountCheque(
				netCollectionAmountCheque[0] != null ? Double.parseDouble(netCollectionAmountCheque[0].toString())
						: 0.0);
		netSalesAndCollectionDTO.setNetCollectionAmountRtgs(
				netCollectionAmountRtgs[0] != null ? Double.parseDouble(netCollectionAmountRtgs[0].toString()) : 0.0);
		netSalesAndCollectionDTO
				.setNetSaleAmount(netSaleAmount[0] != null ? Double.parseDouble(netSaleAmount[0].toString()) : 0.0);

		return netSalesAndCollectionDTO;
	}
}
