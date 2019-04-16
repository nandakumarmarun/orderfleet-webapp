package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

/**
 * Web controller for managing StockTracking.
 *
 * @author Sarath
 * @since Mar 20, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class StockTrackingResource {

	private final Logger log = LoggerFactory.getLogger(StockTrackingResource.class);

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	// @Inject
	// private PrimarySecondaryDocumentRepository
	// primarySecondaryDocumentRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	/**
	 * GET /stock-tracking-report : get StockTrackingReport.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@GetMapping(value = "/stock-tracking-report")
	@Timed
	@Transactional(readOnly = true)
	public String getStockConsumptionReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stock tracking Report");

		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		return "company/stock-tracking-report";
	}

	@RequestMapping(value = "/stock-tracking-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterProductWiseSales(
			@RequestParam("userPid") String userPid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		List<InventoryVoucherDetailDTO> inventoryVouchers = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			inventoryVouchers = getFilterData(userPid, accountPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVouchers = getFilterData(userPid, accountPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVouchers = getFilterData(userPid, accountPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVouchers = getFilterData(userPid, accountPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			inventoryVouchers = getFilterData(userPid, accountPid, fromDateTime, toDateTime);
		}
		return new ResponseEntity<>(inventoryVouchers, HttpStatus.OK);
	}

	private List<InventoryVoucherDetailDTO> getFilterData(String userPid, String accountPid, LocalDate fDate,
			LocalDate tDate) {

		List<InventoryVoucherDetail> inventoryVouchers = new ArrayList<>();

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<String> accountProfilePids = new ArrayList<>();
		List<String> userPids = new ArrayList<>();

		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userPid.equalsIgnoreCase("no") && accountPid.equalsIgnoreCase("no")) {
			if (userIds.isEmpty()) {
				List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(true);
				List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService.findAllByCompany();

				accountProfilePids = accountProfileDTOs.stream().map(acc -> acc.getPid()).collect(Collectors.toList());
				userPids = employeeProfileDTOs.stream().map(emp -> emp.getUserPid()).collect(Collectors.toList());
			} else {
				List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
						.findAccountProfilesByCurrentUserLocations();
				List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService
						.findAllEmployeeByUserIdsIn(userIds);

				accountProfilePids = accountProfileDTOs.stream().map(acc -> acc.getPid()).collect(Collectors.toList());
				userPids = employeeProfileDTOs.stream().map(emp -> emp.getUserPid()).collect(Collectors.toList());
			}
		} else if (!userPid.equalsIgnoreCase("no") && accountPid.equalsIgnoreCase("no")) {
			if (userIds.isEmpty()) {
				List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(true);
				accountProfilePids = accountProfileDTOs.stream().map(acc -> acc.getPid()).collect(Collectors.toList());
			} else {
				List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
						.findAccountProfilesByCurrentUserLocations();
				accountProfilePids = accountProfileDTOs.stream().map(acc -> acc.getPid()).collect(Collectors.toList());
			}
			userPids = Arrays.asList(userPid);

		} else if (userPid.equalsIgnoreCase("no") && !accountPid.equalsIgnoreCase("no")) {

			if (userIds.isEmpty()) {
				List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService.findAllByCompany();
				userPids = employeeProfileDTOs.stream().map(emp -> emp.getUserPid()).collect(Collectors.toList());
			} else {
				List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService
						.findAllEmployeeByUserIdsIn(userIds);
				userPids = employeeProfileDTOs.stream().map(emp -> emp.getUserPid()).collect(Collectors.toList());
			}
			accountProfilePids = Arrays.asList(accountPid);
		}

		else if (!userPid.equalsIgnoreCase("no") && !accountPid.equalsIgnoreCase("no")) {
			userPids = Arrays.asList(userPid);
			accountProfilePids = Arrays.asList(accountPid);
		}

		if (!accountProfilePids.isEmpty() && !userPids.isEmpty()) {
			inventoryVouchers = inventoryVoucherDetailRepository
					.findAllByCompanyIdAndAccountPidInAndEmployeePidInAndDateBetween(accountProfilePids, userPids,
							fromDate, toDate);
		}
		List<InventoryVoucherDetailDTO> voucherDetailDTOs = inventoryVouchers.stream()
				.map(InventoryVoucherDetailDTO::new).collect(Collectors.toList());
		return voucherDetailDTOs;
	}
}
