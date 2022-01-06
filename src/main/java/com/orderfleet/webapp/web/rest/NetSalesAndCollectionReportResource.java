package com.orderfleet.webapp.web.rest;

import java.net.URI;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_199" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Finding net sales amount by UserId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Object[] netSaleAmount = inventoryVoucherHeaderRepository.findnetSaleAmountByUserIdandDateBetween(userIds,
				fromDate, toDate);
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

		
	                DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id1 = "ACC_QUERY_154" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get net collection amount by user Id and date between";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Object[] netCollectionAmount = accountingVoucherHeaderRepository
				.findnetCollectionAmountByUserIdandDateBetween(userIds, fromDate, toDate);
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
	                DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id11 = "AVD_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description11 ="get net a mount by UserId and dateBetween and PaymentMode";
	        		LocalDateTime startLCTime11 = LocalDateTime.now();
	        		String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
	        		String startDate11 = startLCTime11.format(DATE_FORMAT11);
	        		logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
		Object[] netCollectionAmountCash = accountingVoucherdetailRepository
				.findnetCollectionAmountByUserIdandDateBetweenAndPaymentMode(userIds, fromDate, toDate,
						PaymentMode.Cash);
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
