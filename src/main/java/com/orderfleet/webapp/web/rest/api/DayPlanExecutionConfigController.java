package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DayPlanExecutionConfigService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.dto.AccountTargetDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionConfigDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryDTO;

/**
 * REST controller for serve DayPlan Execution Configurations.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@RestController
@RequestMapping("/api")
public class DayPlanExecutionConfigController {

	private final Logger log = LoggerFactory.getLogger(MasterDataController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private DayPlanExecutionConfigService dayPlanExecutionConfigService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private FormFormElementService formFormElementService;

	/**
	 * GET /account-types : get all accountTypes.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         accountTypes
	 * @throws URISyntaxException
	 *             if the pagination headers couldn't be generated
	 */
	@Timed
	@GetMapping("/day-plan-execution-config")
	public ResponseEntity<List<DayPlanExecutionConfigDTO>> getDayPlanExecutionConfig() throws URISyntaxException {
		log.debug("REST request to get  Day PlanExecutionConfigs");
		List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigDTOs = dayPlanExecutionConfigService
				.findAllByCompanyIdAndEnabledTrueCurrentUser();
		return new ResponseEntity<>(dayPlanExecutionConfigDTOs, HttpStatus.OK);
	}

	@Timed
	@GetMapping("/account-purchase-history")
	public ResponseEntity<List<PurchaseHistoryDTO>> getPurchaseHistory(
			@RequestParam(value = "accountPid") String accountPid) throws URISyntaxException {
		log.debug("REST request to get  account Purchase History");

		// primary sales documents
		List<Document> psDocuments = primarySecondaryDocumentRepository
				.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);

		int duration = 3;
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository.findByCompanyIdAndName(
				SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.ACCOUNT_PURCHASE_HISTORY_DURATION);
		if (optCompanyConfiguration.isPresent()) {
			duration = Integer.valueOf(optCompanyConfiguration.get().getValue());
		}
		LocalDateTime fromDate = LocalDate.now().minusMonths(duration).atTime(0, 0);
		LocalDateTime toDate = LocalDate.now().atTime(23, 59);
		List<PurchaseHistoryDTO> historyDTOs = new ArrayList<>();
		if (!psDocuments.isEmpty()) {
			List<Object[]> objects = inventoryVoucherDetailRepository
					.findPurchaseByAccountDocumentsAndDateBetween(accountPid, psDocuments, fromDate, toDate);
			for (Object[] obj : objects) {
				historyDTOs.add(new PurchaseHistoryDTO(obj[0].toString(), obj[1].toString(),
						Double.valueOf(obj[2].toString())));
			}
			// Sorting
			Collections.sort(historyDTOs, new Comparator<PurchaseHistoryDTO>() {
				@Override
				public int compare(PurchaseHistoryDTO arg0, PurchaseHistoryDTO arg1) {
					return Double.compare(arg1.getQuantity(), arg0.getQuantity());
				}
			});
		}
		return new ResponseEntity<>(historyDTOs, HttpStatus.OK);
	}

	@Timed
	@GetMapping("/account-traget-details")
	public ResponseEntity<List<AccountTargetDTO>> getAccountTargetAchievedDetails(
			@RequestParam(value = "accountPid") String accountPid) throws URISyntaxException {
		log.debug("REST request to get  account-traget-details");
		List<AccountTargetDTO> accountTargetDTOs = new ArrayList<>();

		// primary sales documents
		List<Document> psDocuments = primarySecondaryDocumentRepository
				.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);

		// find last year Target and Achieved
		LocalDate lastYearStartDate = LocalDate.now().minusYears(1).withDayOfYear(1);
		LocalDate lastYearEndDate = LocalDate.of(lastYearStartDate.getYear(), 12, 31);
		AccountTargetDTO lastYear = new AccountTargetDTO();
		lastYear.setLabel("last_year");
		// set targets
		Object lastYearSumOfAccountWiseTarget = salesTargetGroupUserTargetRepository
				.findSumOfAccountWiseTarget(accountPid, lastYearStartDate, lastYearEndDate);
		Object[] lastYearTarget = (Object[]) lastYearSumOfAccountWiseTarget;
		if (lastYearTarget[0] != null) {
			lastYear.setTargetVolume(Double.valueOf(lastYearTarget[0].toString()));
		}
		if (lastYearTarget[1] != null) {
			lastYear.setTargetAmount(Double.valueOf(lastYearTarget[1].toString()));
		}
		// set acheived
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String id = "INV_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get amount cunt and volume by accountPid and datec between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Object lastYearSumOfAccountWiseAchieved = inventoryVoucherHeaderRepository
				.getAmountAndVolumeByAccountPidAndDateBetween(accountPid, lastYearStartDate.atTime(0, 0),
						lastYearEndDate.atTime(23, 59), psDocuments);
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
		Object[] lastYearAchieved = (Object[]) lastYearSumOfAccountWiseAchieved;
		if (lastYearAchieved[0] != null) {
			lastYear.setAchievedAmount(Double.valueOf(lastYearAchieved[0].toString()));
		}
		if (lastYearAchieved[1] != null) {
			lastYear.setAchievedVolume(Double.valueOf(lastYearAchieved[1].toString()));
		}
		accountTargetDTOs.add(lastYear);
		// find current year Target and Achieved
		LocalDate currentYearDate = LocalDate.now().withDayOfYear(1);
		LocalDate toDate = LocalDate.now();
		AccountTargetDTO currentYear = new AccountTargetDTO();
		currentYear.setLabel("current_year");
		// set targets
		Object currentYearSumOfAccountWiseTarget = salesTargetGroupUserTargetRepository
				.findSumOfAccountWiseTarget(accountPid, currentYearDate, toDate);
		Object[] currentYearTarget = (Object[]) currentYearSumOfAccountWiseTarget;
		if (currentYearTarget[0] != null) {
			currentYear.setTargetVolume(Double.valueOf(currentYearTarget[0].toString()));
		}
		if (currentYearTarget[1] != null) {
			currentYear.setTargetAmount(Double.valueOf(currentYearTarget[1].toString()));
		}
		// set acheived
		Object currentYearSumOfAccountWiseAchieved = inventoryVoucherHeaderRepository
				.getAmountAndVolumeByAccountPidAndDateBetween(accountPid, currentYearDate.atTime(0, 0),
						toDate.atTime(23, 59), psDocuments);
		Object[] currentYearAchieved = (Object[]) currentYearSumOfAccountWiseAchieved;
		if (currentYearAchieved[0] != null) {
			currentYear.setAchievedAmount(Double.valueOf(currentYearAchieved[0].toString()));
		}
		if (currentYearAchieved[1] != null) {
			currentYear.setAchievedVolume(Double.valueOf(currentYearAchieved[1].toString()));
		}
		accountTargetDTOs.add(currentYear);

		// find current month Target and Achieved
		LocalDate currentMonthDate = LocalDate.now().withDayOfMonth(1);
		AccountTargetDTO currentMonth = new AccountTargetDTO();
		currentMonth.setLabel("current_month");
		// set targets
		Object currentMonthSumOfAccountWiseTarget = salesTargetGroupUserTargetRepository
				.findSumOfAccountWiseTarget(accountPid, currentMonthDate, toDate);
		Object[] currentMonthTarget = (Object[]) currentMonthSumOfAccountWiseTarget;
		if (currentMonthTarget[0] != null) {
			currentMonth.setTargetVolume(Double.valueOf(currentMonthTarget[0].toString()));
		}
		if (currentMonthTarget[1] != null) {
			currentMonth.setTargetAmount(Double.valueOf(currentMonthTarget[1].toString()));
		}
		// set acheived
		Object currentMonthSumOfAccountWiseAchieved = inventoryVoucherHeaderRepository
				.getAmountAndVolumeByAccountPidAndDateBetween(accountPid, currentMonthDate.atTime(0, 0),
						toDate.atTime(23, 59), psDocuments);
		Object[] currentMonthAchieved = (Object[]) currentMonthSumOfAccountWiseAchieved;
		if (currentMonthAchieved[0] != null) {
			currentMonth.setAchievedAmount(Double.valueOf(currentMonthAchieved[0].toString()));
		}
		if (currentMonthAchieved[1] != null) {
			currentMonth.setAchievedVolume(Double.valueOf(currentMonthAchieved[1].toString()));
		}
		accountTargetDTOs.add(currentMonth);

		return new ResponseEntity<>(accountTargetDTOs, HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@GetMapping("/guided_selling_info_document_details")
	public ResponseEntity<DynamicDocumentHeaderDTO> guidedSellingInfoDocumentDetails(
			@RequestParam(value = "accountPid") String accountPid,
			@RequestParam(value = "documentPid") String documentPid) throws URISyntaxException {
		log.debug("REST request to get  guided_selling_info_document_details");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "DYN_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="getting top1 document by executive task execution accountPid and DocPid  ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findTop1ByExecutiveTaskExecutionAccountProfilePidAndDocumentPidOrderByCreatedDateDesc(accountPid,
						documentPid);
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

		DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = null;
		if (dynamicDocumentHeader != null) {
			dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
					.map(FilledFormDTO::new).collect(Collectors.toList()));
			// assign sort order
			for (FilledFormDTO ffdto : dynamicDocumentHeaderDTO.getFilledForms()) {
				List<FormFormElement> formFormElements = formFormElementService.findByFormPid(ffdto.getFormPid());
				formFormElements.sort((f1, f2) -> f1.getSortOrder() - f2.getSortOrder());
				List<FilledFormDetailDTO> filledFormDetails = new ArrayList<>();
				for (FormFormElement ffe : formFormElements) {
					ffdto.getFilledFormDetails().stream()
							.filter(ffd -> ffd.getFormElementPid().equals(ffe.getFormElement().getPid())).findAny()
							.ifPresent(ffd -> {
								filledFormDetails.add(ffd);
							});
				}
				ffdto.setFilledFormDetails(filledFormDetails);
			}
		}
		return new ResponseEntity<>(dynamicDocumentHeaderDTO, HttpStatus.OK);
	}

}
