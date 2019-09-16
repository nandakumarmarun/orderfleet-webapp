package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PerformanceReportMobileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.LoadServerExeTaskDTO;
import com.orderfleet.webapp.web.rest.api.dto.LoadServerSentItemDTO;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionView;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;

import javassist.expr.NewArray;

@RestController
@RequestMapping("/api")
public class LoadServerItemsToMobileController {

	private final Logger log = LoggerFactory.getLogger(LoadServerItemsToMobileController.class);

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@GetMapping("/load-server-attendence")
	public ResponseEntity<List<AttendanceDTO>> sentAttendenceDownload() {

		log.info("Request to load server attendance...");

		List<AttendanceDTO> attendenceDTOs = new ArrayList<>();

		List<Attendance> attendances = new ArrayList<>();

		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

		long userId;

		if (user.isPresent()) {
			userId = user.get().getId();
			attendances = attendanceRepository.findTop61ByUserIdOrderByPlannedDateDesc(userId);
			// getAttendanceByUserandUptoLimitDesc(userId);
		}

		for (Attendance attendance : attendances) {
			attendenceDTOs.add(new AttendanceDTO(attendance));
		}

		log.info("Attendance Size= " + attendenceDTOs.size());

		return new ResponseEntity<>(attendenceDTOs, HttpStatus.OK);
	}

	@GetMapping("/load-server-sent-items")
	public ResponseEntity<LoadServerExeTaskDTO> sentItemsDownload(@RequestParam String filterBy,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {

		log.info("Request to load server sent items...");

		LoadServerExeTaskDTO loadServerExeTaskDTO = new LoadServerExeTaskDTO();

		if (filterBy.equalsIgnoreCase("TODAY")) {
			log.info("TODAY------");
			loadServerExeTaskDTO = getFilterData(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equalsIgnoreCase("YESTERDAY")) {
			log.info("YESTERDAY------");
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			loadServerExeTaskDTO = getFilterData(yeasterday, yeasterday);
		} else if (filterBy.equalsIgnoreCase("WTD")) {
			log.info("WTD------");
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			loadServerExeTaskDTO = getFilterData(weekStartDate, LocalDate.now());
		} else if (filterBy.equalsIgnoreCase("MTD")) {
			log.info("MTD------");
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			loadServerExeTaskDTO = getFilterData(monthStartDate, LocalDate.now());
		} else if (filterBy.equalsIgnoreCase("CUSTOM")) {
			log.info("CUSTOM------" + fromDate + " to " + toDate + "------");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			loadServerExeTaskDTO = getFilterData(fromDateTime, toFateTime);
		} else if (filterBy.equalsIgnoreCase("SINGLE")) {
			log.info("SINGLE------" + fromDate + "-------");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			loadServerExeTaskDTO = getFilterData(fromDateTime, fromDateTime);
		}

		return new ResponseEntity<>(loadServerExeTaskDTO, HttpStatus.OK);
	}

	@GetMapping("/load-server-sent-items-document")
	public ResponseEntity<LoadServerSentItemDTO> sentDocumentItemDownload(@RequestParam String exeTaskPid) {

		log.info("Request to load server sent items document...");

		LoadServerSentItemDTO loadServerSentItemDTO = new LoadServerSentItemDTO();

		loadServerSentItemDTO.setDocumentType("");
		loadServerSentItemDTO.setInventoryVouchers(getDocumentInventoryItems(exeTaskPid));
		loadServerSentItemDTO.setAccountingVouchers(getDocumentAccountingItems(exeTaskPid));
		loadServerSentItemDTO.setDynamicDocuments(getDocumentDynamicItems(exeTaskPid));

		return new ResponseEntity<>(loadServerSentItemDTO, HttpStatus.OK);

	}

	@GetMapping("/load-server-sent-items-details")
	public ResponseEntity<LoadServerSentItemDTO> sentItemsDetailDownload(@RequestParam String pid,
			@RequestParam String documentPid) {

		log.info("Request to load server sent items details...");

		LoadServerSentItemDTO loadServerSentItemDTO = new LoadServerSentItemDTO();

		Optional<Document> document = documentRepository.findOneByPid(documentPid);

		if (document.isPresent()) {
			loadServerSentItemDTO.setDocumentType(document.get().getDocumentType().toString());
		}
		loadServerSentItemDTO.setInventoryVouchers(getDocumentInventoryItemsDetails(pid));
		loadServerSentItemDTO.setAccountingVouchers(getDocumentAccountingItemsDetails(pid));
		loadServerSentItemDTO.setDynamicDocuments(getDocumentDynamicItemsDetails(pid));

		return new ResponseEntity<>(loadServerSentItemDTO, HttpStatus.OK);

	}

	private LoadServerExeTaskDTO getFilterData(LocalDate fDate, LocalDate tDate) {

		LoadServerExeTaskDTO loadServerExeTaskDTO = new LoadServerExeTaskDTO();

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<ExecutiveTaskExecutionDTO> executiveTaskExecutions = new ArrayList<>();

		List<Object[]> executiveTaskExecutionsObject = new ArrayList<>();

		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

		long userId;

		if (user.isPresent()) {
			userId = user.get().getId();
			executiveTaskExecutionsObject = executiveTaskExecutionRepository.getByDateBetweenAndUser(fromDate, toDate,
					userId);
		}

		Set<Long> exeIds = new HashSet<>();

		for (Object[] obj : executiveTaskExecutionsObject) {

			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO();

			executiveTaskExecutionDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			executiveTaskExecutionDTO.setAccountProfileName(obj[1] != null ? obj[1].toString() : "");
			executiveTaskExecutionDTO.setActivityName(obj[2] != null ? obj[2].toString() : "");

			LocalDateTime date = null;
			if (obj[3] != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
				date = LocalDateTime.parse(obj[3].toString(), formatter);
			}

			executiveTaskExecutionDTO.setDate(date);

			exeIds.add(obj[4] != null ? Long.parseLong(obj[4].toString()) : 0);

			executiveTaskExecutions.add(executiveTaskExecutionDTO);

		}

		if (exeIds.size() != 0) {
			List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTos = getDocumentInventoryItemsByExeIdIn(exeIds);
			List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTos = getDocumentAccountingItemsByExeIdIn(exeIds);
			List<DynamicDocumentHeaderDTO> dynamicHeaderDTos = getDynamicDocumentsByExeIdIn(exeIds);

			int inventoryVoucherCount = inventoryVoucherHeaderDTos.size();
			int accountingVoucherCount = accountingVoucherHeaderDTos.size();
			int dynamicDocumentCount = dynamicHeaderDTos.size();
			double inventoryVocherTotal = inventoryVoucherHeaderDTos.stream().mapToDouble(ivh -> ivh.getDocumentTotal())
					.sum();
			double accountingVoucherTotal = accountingVoucherHeaderDTos.stream()
					.mapToDouble(avh -> avh.getTotalAmount()).sum();

			log.info("Executive Task Execution Size= " + executiveTaskExecutions.size());
			log.info("Inventory Voucher Size= " + inventoryVoucherCount + " & Total= " + inventoryVocherTotal);
			log.info("Accounting Voucher Size= " + accountingVoucherCount + " & Total= " + accountingVoucherTotal);
			log.info("Dynamic Document Size= " + dynamicDocumentCount);

			loadServerExeTaskDTO.setAccountingVoucherCount(accountingVoucherCount);
			loadServerExeTaskDTO.setDynamicDocumentCount(dynamicDocumentCount);
			loadServerExeTaskDTO.setAccountingVoucherTotal(accountingVoucherTotal);
			loadServerExeTaskDTO.setExecutiveTaskExecutionDTOs(executiveTaskExecutions);
			loadServerExeTaskDTO.setInventoryVoucherCount(inventoryVoucherCount);
			loadServerExeTaskDTO.setInventoryVoucherTotal(inventoryVocherTotal);
		}
		return loadServerExeTaskDTO;
	}

	private List<InventoryVoucherHeaderDTO> getDocumentInventoryItems(String exeTasKPid) {

		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();

		List<Object[]> inventoryVouchersObject = inventoryVoucherHeaderRepository
				.findInventoryVoucherHeaderByExecutiveTaskExecutionPid(exeTasKPid);

		for (Object[] obj : inventoryVouchersObject) {
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
			inventoryVoucherHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			inventoryVoucherHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			inventoryVoucherHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			inventoryVoucherHeaderDTOs.add(inventoryVoucherHeaderDTO);
		}
		log.info("Inventory Voucher Size= " + inventoryVoucherHeaderDTOs.size());
		return inventoryVoucherHeaderDTOs;
	}

	private List<AccountingVoucherHeaderDTO> getDocumentAccountingItems(String exeTasKPid) {
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();

		List<Object[]> accountingVouchersObject = accountingVoucherHeaderRepository
				.findAccountingVoucherHeaderByExecutiveTaskExecutionPid(exeTasKPid);

		for (Object[] obj : accountingVouchersObject) {
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
			accountingVoucherHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			accountingVoucherHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			accountingVoucherHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			accountingVoucherHeaderDTOs.add(accountingVoucherHeaderDTO);
		}

		log.info("Accounting Voucher Size= " + accountingVoucherHeaderDTOs.size());
		return accountingVoucherHeaderDTOs;
	}

	private List<InventoryVoucherHeaderDTO> getDocumentInventoryItemsByExeIdIn(Set<Long> exeIds) {

		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();

		List<Object[]> inventoryVouchersObject = inventoryVoucherHeaderRepository
				.findInventoryVoucherHeaderByExecutiveTaskExecutionIdIn(exeIds);

		for (Object[] obj : inventoryVouchersObject) {
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
			inventoryVoucherHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			inventoryVoucherHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			inventoryVoucherHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			inventoryVoucherHeaderDTO.setDocumentTotal(obj[3] != null ? Double.parseDouble(obj[3].toString()) : 0.0);
			inventoryVoucherHeaderDTOs.add(inventoryVoucherHeaderDTO);
		}

		return inventoryVoucherHeaderDTOs;
	}

	private List<AccountingVoucherHeaderDTO> getDocumentAccountingItemsByExeIdIn(Set<Long> exeIds) {
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();

		List<Object[]> accountingVouchersObject = accountingVoucherHeaderRepository
				.findAccountingVoucherHeaderByExecutiveTaskExecutionIdin(exeIds);

		for (Object[] obj : accountingVouchersObject) {
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
			accountingVoucherHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			accountingVoucherHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			accountingVoucherHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			accountingVoucherHeaderDTO.setTotalAmount(obj[3] != null ? Double.parseDouble(obj[3].toString()) : 0.0);
			accountingVoucherHeaderDTOs.add(accountingVoucherHeaderDTO);
		}

		return accountingVoucherHeaderDTOs;
	}

	private List<DynamicDocumentHeaderDTO> getDynamicDocumentsByExeIdIn(Set<Long> exeIds) {

		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs = new ArrayList<>();

		List<Object[]> dynamicDocumentsObject = dynamicDocumentHeaderRepository
				.findDynamicDocumentsHeaderByExecutiveTaskExecutionIdin(exeIds);

		for (Object[] obj : dynamicDocumentsObject) {
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO();
			dynamicDocumentHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			dynamicDocumentHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			dynamicDocumentHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			dynamicDocumentHeaderDTOs.add(dynamicDocumentHeaderDTO);
		}
		return dynamicDocumentHeaderDTOs;
	}

	private List<DynamicDocumentHeaderDTO> getDocumentDynamicItems(String exeTasKPid) {
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs = new ArrayList<>();

		List<Object[]> dynamicDocumentsObject = dynamicDocumentHeaderRepository
				.findDynamicDocumentsHeaderByExecutiveTaskExecutionPid(exeTasKPid);

		for (Object[] obj : dynamicDocumentsObject) {
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO();
			dynamicDocumentHeaderDTO.setPid(obj[0] != null ? obj[0].toString() : "");
			dynamicDocumentHeaderDTO.setDocumentName(obj[1] != null ? obj[1].toString() : "");
			dynamicDocumentHeaderDTO.setDocumentPid(obj[2] != null ? obj[2].toString() : "");
			dynamicDocumentHeaderDTOs.add(dynamicDocumentHeaderDTO);
		}
		log.info("Dynamic Document Size= " + dynamicDocumentHeaderDTOs.size());
		return dynamicDocumentHeaderDTOs;
	}

	private List<InventoryVoucherHeaderDTO> getDocumentInventoryItemsDetails(String inventoryVoucherHeaderPid) {
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();

		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findInventoryVoucherHeaderByPid(inventoryVoucherHeaderPid);

		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
			inventoryVoucherHeaderDTOs.add(new InventoryVoucherHeaderDTO(inventoryVoucherHeader));
		}
		log.info("Inventory Voucher Size= " + inventoryVoucherHeaderDTOs.size());
		return inventoryVoucherHeaderDTOs;
	}

	private List<AccountingVoucherHeaderDTO> getDocumentAccountingItemsDetails(String accountingVoucherHeaderPid) {
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();

		List<AccountingVoucherHeader> inventoryVoucherHeaders = accountingVoucherHeaderRepository
				.findAccountingVoucherHeaderByPid(accountingVoucherHeaderPid);

		for (AccountingVoucherHeader accountinVoucherHeader : inventoryVoucherHeaders) {
			accountingVoucherHeaderDTOs.add(new AccountingVoucherHeaderDTO(accountinVoucherHeader));
		}
		log.info("Accounting Voucher Size= " + accountingVoucherHeaderDTOs.size());
		return accountingVoucherHeaderDTOs;
	}

	private List<DynamicDocumentHeaderDTO> getDocumentDynamicItemsDetails(String dynamicDocumentHeaderPid) {
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs = new ArrayList<>();

		List<DynamicDocumentHeader> dynamicHeaders = dynamicDocumentHeaderRepository
				.findDynamicDocumentHeaderByPid(dynamicDocumentHeaderPid);

		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicHeaders) {
			dynamicDocumentHeaderDTOs.add(new DynamicDocumentHeaderDTO(dynamicDocumentHeader));
		}
		log.info("Dynamic Document Size= " + dynamicDocumentHeaderDTOs.size());
		return dynamicDocumentHeaderDTOs;
	}

}
