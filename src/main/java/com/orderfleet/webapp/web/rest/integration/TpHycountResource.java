package com.orderfleet.webapp.web.rest.integration;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LedgerReportTPRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.BankService;
import com.orderfleet.webapp.service.DivisionService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.FormElementMasterService;
import com.orderfleet.webapp.service.FormElementMasterTypeService;
import com.orderfleet.webapp.service.LedgerReportTPService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.dto.DivisionDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;
import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TourDetails;
import com.orderfleet.webapp.web.rest.dto.TourOrderDTO;
import com.orderfleet.webapp.web.rest.dto.TourPaymentDTO;
import com.orderfleet.webapp.web.rest.dto.TourSummaryDTO;

/**
 * REST controller for managing master data for hycount third party application.
 * 
 * @author Sarath
 * @since Nov 2, 2016
 */
@RestController
@RequestMapping(value = "/api/tp")
public class TpHycountResource {

	private final Logger log = LoggerFactory.getLogger(TpHycountResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private DivisionService divisionService;

	@Inject
	private BankService bankService;

	@Inject
	private LocationService locationService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductProfileService productProfileService;

	// @Inject
	// private ProductGroupService productGroupService;
	//
	// @Inject
	// private ProductGroupProductRepository productGroupProductRepository;
	//
	// @Inject
	// private ProductGroupRepository productGroupRepository;
	//
	// @Inject
	// private ProductProfileRepository productProfileRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private LedgerReportTPService ledgerReportTPService;

	@Inject
	private FormElementMasterService formElementMasterService;

	@Inject
	private FormElementMasterTypeService formElementMasterTypeService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private LedgerReportTPRepository ledgerReportTPRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	private static final String visitDocument = "Remarks";
	private static final String tourSummaryDocument = "Tour Summary Details";

	/**
	 * GET /hycount-ledger-report.json to tab : get ledger report.
	 * 
	 * for return ledger report where ledger report narration is 'Ledger'
	 *
	 * @return the ResponseEntity with status 200 and list of ledger Report
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/hycount-ledger-report.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LedgerReportTPDTO>> getLedgerReports(
			@RequestParam(value = "accountPid") String accountPid) throws URISyntaxException {
		log.debug("REST request to get ledger report ");
		List<LedgerReportTPDTO> ledgerReportTPDTOs = ledgerReportTPService
				.findAllByCompanyIdAndTypeAndAccountProfilePid("Ledger", accountPid);
		return new ResponseEntity<>(ledgerReportTPDTOs, HttpStatus.CREATED);
	}

	/**
	 * GET /hycount-opening-balance.json to tab : get ledger report.
	 * 
	 * for return ledger report where ledger report narration is 'Ledger'
	 *
	 * @return the ResponseEntity with status 200 and list of ledger Report
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/hycount-opening-balance.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LedgerReportTPDTO>> getOpeningBalances(
			@RequestParam(value = "accountPid") String accountPid) throws URISyntaxException {
		log.debug("REST request to get opening balance ");
		List<LedgerReportTPDTO> ledgerReportTPDTOs = ledgerReportTPService
				.findAllByCompanyIdAndTypeAndAccountProfilePid("Opening Balance", accountPid);
		return new ResponseEntity<>(ledgerReportTPDTOs, HttpStatus.CREATED);
	}

	/**
	 * GET /hycount-visits.json : get visits.
	 * 
	 * for return dynamic documents where document name is 'Remarks'
	 *
	 * @return the ResponseEntity with status 200 and list of dynamin documents
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/hycount-visits.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TourDetails>> getVisits() throws URISyntaxException {
		log.debug("REST request to get visits ");
		List<TourDetails> tourDetails = new ArrayList<>();
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = executiveTaskExecutionService
				.findAllByCompanyIdAndStatusFalse();

		for (ExecutiveTaskExecutionDTO executiveTaskExecutionDTO : executiveTaskExecutionDTOs) {
			TourDetails details = new TourDetails();

			// orders
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			
			String id = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by executive task execution Pid ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<InventoryVoucherHeader> inventoryVoucherHeaderDTOs = inventoryVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(executiveTaskExecutionDTO.getPid());
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
			List<TourOrderDTO> orderDTOs = new ArrayList<>();
			if (!inventoryVoucherHeaderDTOs.isEmpty()) {
				for (InventoryVoucherHeader inventoryVoucherHeaderDTO : inventoryVoucherHeaderDTOs) {
					if (inventoryVoucherHeaderDTO.getInventoryVoucherDetails() != null
							&& !inventoryVoucherHeaderDTO.getInventoryVoucherDetails().isEmpty()) {

						for (InventoryVoucherDetail inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
								.getInventoryVoucherDetails()) {
							TourOrderDTO orderDTO = new TourOrderDTO();
							Optional<ProductProfileDTO> productProfile = productProfileService
									.findOneByPid(inventoryVoucherDetailDTO.getProduct().getPid());
							if (productProfile.isPresent()) {
								orderDTO.setDicountPercentage(inventoryVoucherDetailDTO.getDiscountPercentage());
								orderDTO.setItemCode(productProfile.get().getAlias());
								orderDTO.setQuantity(inventoryVoucherDetailDTO.getQuantity());
								orderDTO.setVoucherNo(String.valueOf(inventoryVoucherDetailDTO.getId()));
							}
							orderDTOs.add(orderDTO);
						}
					}
				}
			}
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACC_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 =" get all AccVoucher By ExecutiveTask Execution Pid ";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			// receipts
			List<AccountingVoucherHeader> accountingVoucherHeaderDTOs = accountingVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(executiveTaskExecutionDTO.getPid());
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

			List<TourPaymentDTO> paymentDTOs = new ArrayList<>();
			if (!accountingVoucherHeaderDTOs.isEmpty()) {
				for (AccountingVoucherHeader accountingVoucherHeaderDTO : accountingVoucherHeaderDTOs) {
					if (accountingVoucherHeaderDTO.getAccountingVoucherDetails() != null
							&& !accountingVoucherHeaderDTO.getAccountingVoucherDetails().isEmpty()) {
						for (AccountingVoucherDetail accountingVoucherDetailDTO : accountingVoucherHeaderDTO
								.getAccountingVoucherDetails()) {
							for (AccountingVoucherAllocation accountingVoucherAllocationDTO : accountingVoucherDetailDTO
									.getAccountingVoucherAllocations()) {
								TourPaymentDTO tourPaymentDTO = new TourPaymentDTO();
								Optional<BankDTO> bankDTO = bankService
										.findByName(accountingVoucherDetailDTO.getBankName());
								if (bankDTO.isPresent()) {
									tourPaymentDTO.setBankCode(bankDTO.get().getAlias());
									tourPaymentDTO.setBankName(bankDTO.get().getName());
									tourPaymentDTO.setBankPlace(bankDTO.get().getDescription());
									tourPaymentDTO.setChequeAmount(accountingVoucherAllocationDTO.getAmount());
									tourPaymentDTO.setPaymentType(accountingVoucherAllocationDTO.getMode().toString());
									tourPaymentDTO.setPrDate(accountingVoucherHeaderDTO.getCreatedDate().toString());
									tourPaymentDTO
											.setChequeDate(accountingVoucherDetailDTO.getVoucherDate().toString());
									tourPaymentDTO.setVoucherNumber(accountingVoucherAllocationDTO.getVoucherNumber());
									paymentDTOs.add(tourPaymentDTO);
								}
							}
						}
					}
				}
			}

			// remarks
			List<DynamicDocumentHeaderDTO> dynamicDocumentHeaders = dynamicDocumentHeaderService
					.findAllByCompanyIdAndDocumentNameStatusFalseOrderByCreatedDateDesc(visitDocument);
			for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : dynamicDocumentHeaders) {
				for (FilledFormDTO filledFormDTO : dynamicDocumentHeaderDTO.getFilledForms()) {
					for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {

						if (filledFormDetailDTO.getFormElementName().equals("Select ORDER remark")) {
							details.setOrderRemarkName(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals("Enter Special ORDER remark")) {
							details.setSpecialRemark(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals("Select RECEIPT remark")) {
							details.setPaymentRemarkName(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals("Enter Special RECEIPT remark")) {
							// details.setPaymentRemarkName(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals("Enter VISIT remark")) {
							// details.setPaymentRemarkName(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals("Visit date")) {
							details.setTourDate(filledFormDetailDTO.getValue());
						}
					}
				}
			}

			Optional<EmployeeProfile> employeee = employeeProfileService
					.findByUserPid(executiveTaskExecutionDTO.getUserPid());
			Optional<AccountProfileDTO> accountsDTO = accountProfileService
					.findOneByPid(executiveTaskExecutionDTO.getAccountProfilePid());
			// details.setOrderRemarkCode();
			// details.setPlaceCode();
			details.setPlaceName(accountsDTO.get().getLocation());
			details.setCustomerCode(accountsDTO.get().getAlias());
			details.setTourOrderList(orderDTOs);
			details.setTourPaymentList(paymentDTOs);
			details.setRepCode(employeee.get().getAlias());
			details.setVoucherNo(executiveTaskExecutionDTO.getPid());

			tourDetails.add(details);
		}

		return new ResponseEntity<>(tourDetails, HttpStatus.CREATED);
	}

	/**
	 * GET /hycount-tour-summary.json : get tour summary.
	 * 
	 * for return dynamic documents where document name is 'TourSummary'
	 *
	 * @return the ResponseEntity with status 200 and list of dynamin documents
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/hycount-tour-summary.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TourSummaryDTO>> getTourSummary() throws URISyntaxException {
		log.debug("REST request to get visits ");
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaders = dynamicDocumentHeaderService
				.findAllByCompanyIdAndDocumentNameStatusFalseOrderByCreatedDateDesc(tourSummaryDocument);
		List<TourSummaryDTO> tourSummaryDTOs = new ArrayList<>();
		for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : dynamicDocumentHeaders) {
			Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
					.findOneByPid(dynamicDocumentHeaderDTO.getEmployeePid());
			for (FilledFormDTO filledFormDTO : dynamicDocumentHeaderDTO.getFilledForms()) {
				for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
					TourSummaryDTO tourSummaryDTO = new TourSummaryDTO();

					if (filledFormDetailDTO.getFormElementName().equals("Closing KM")) {
						if (!filledFormDetailDTO.getValue().equals("") && filledFormDetailDTO.getValue() != null) {
							tourSummaryDTO.setClosingKm(Double.valueOf(filledFormDetailDTO.getValue()));
						}
					} else if (filledFormDetailDTO.getFormElementName().equals("Fuel (Rs)")) {
						if (!filledFormDetailDTO.getValue().equals("") && filledFormDetailDTO.getValue() != null) {
							tourSummaryDTO.setFuel(Double.valueOf(filledFormDetailDTO.getValue()));
						}
					} else if (filledFormDetailDTO.getFormElementName().equals("Halting Station")) {
						tourSummaryDTO.setHaltingStation(filledFormDetailDTO.getValue());
					} else if (filledFormDetailDTO.getFormElementName().equals("End Time")) {
						tourSummaryDTO.setHaltingTime(filledFormDetailDTO.getValue());
					} else if (filledFormDetailDTO.getFormElementName().equals("Tour Summary remark")) {
						tourSummaryDTO.setRemarks(filledFormDetailDTO.getValue());
					} else if (filledFormDetailDTO.getFormElementName().equals("Running KM")) {
						if (!filledFormDetailDTO.getValue().equals("") && filledFormDetailDTO.getValue() != null) {
							tourSummaryDTO.setRunningKm(Double.valueOf(filledFormDetailDTO.getValue()));
						}
					} else if (filledFormDetailDTO.getFormElementName().equals("Starting KM")) {
						if (!filledFormDetailDTO.getValue().equals("") && filledFormDetailDTO.getValue() != null) {
							tourSummaryDTO.setStartingKm(Double.valueOf(filledFormDetailDTO.getValue()));
						}
					} else if (filledFormDetailDTO.getFormElementName().equals("Starting Station")) {
						tourSummaryDTO.setStartingStation(filledFormDetailDTO.getValue());
					} else if (filledFormDetailDTO.getFormElementName().equals("Start Time")) {
						tourSummaryDTO.setStartingTime(filledFormDetailDTO.getValue());
					} else if (filledFormDetailDTO.getFormElementName().equals("Station Covered")) {
						tourSummaryDTO.setStationCovered(filledFormDetailDTO.getValue());
					}
					tourSummaryDTO.setRepCode(employeeProfileDTO.get().getAlias());
					tourSummaryDTO.setTourDate(dynamicDocumentHeaderDTO.getDocumentDate().toLocalDate());
					tourSummaryDTO.setDynamicDocumentPid(dynamicDocumentHeaderDTO.getPid());
					tourSummaryDTO.setVoucherNo(dynamicDocumentHeaderDTO.getPid());
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "FORM_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="Get the one filled form by pid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					Optional<FilledForm> filledForm = filledFormRepository.findOneByPid(filledFormDTO.getPid());
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

					tourSummaryDTO.setVoucherId(filledForm.get().getId());
					tourSummaryDTOs.add(tourSummaryDTO);
				}
			}
		}
		return new ResponseEntity<>(tourSummaryDTOs, HttpStatus.CREATED);
	}

	// hycount-update-visits-status

	/**
	 * POST /hycount-update-visits-status .
	 *
	 * @param String
	 *            the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-update-visits-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteVisitStatus(@Valid @RequestBody List<String> executiveTaskExecutionPids)
			throws URISyntaxException {
		log.debug("REST request to update executive task execution status : {}", executiveTaskExecutionPids.size());
		for (String executiveTaskExecutionPid : executiveTaskExecutionPids) {
			Optional<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTO = executiveTaskExecutionService
					.findOneByPid(executiveTaskExecutionPid);
			if (executiveTaskExecutionDTO.isPresent()) {
				executiveTaskExecutionService.updateExecutiveTaskExecutionStatus(executiveTaskExecutionDTO.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-update-tour-summery-status .
	 *
	 * @param String
	 *            the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-update-tour-summery-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteTourSummeryStatus(@Valid @RequestBody List<String> dynamicDocumentPids)
			throws URISyntaxException {
		log.debug("REST request to update Dynamic Document status : {}", dynamicDocumentPids.size());
		for (String dynamicDocumentId : dynamicDocumentPids) {
			Optional<DynamicDocumentHeaderDTO> executiveTaskExecutionDTO = dynamicDocumentHeaderService
					.findOneByPid(dynamicDocumentId);
			if (executiveTaskExecutionDTO.isPresent()) {
				dynamicDocumentHeaderService.updateDynamicDocumentStatus(executiveTaskExecutionDTO.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-bank.json : Create new bank.
	 *
	 * @param banlDTOs
	 *            the List<BankDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-bank.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createBanksJSON(@Valid @RequestBody List<BankDTO> bankDTOs) throws URISyntaxException {
		log.debug("REST request to save banks : {}", bankDTOs.size());
		// if not already exist, save productGroups
		for (BankDTO bankDTO : bankDTOs) {
			Optional<BankDTO> bank = bankService.findByName(bankDTO.getName());
			if (bank.isPresent()) {
				log.debug("Bank Name Alredy Exist: " + bankDTO.getName());
				bankDTO.setPid(bank.get().getPid());
				bankService.update(bankDTO);
			} else {
				bankService.save(bankDTO);
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-division.json : Create new division.
	 *
	 * @param divisionDTOs
	 *            the List<divisionDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-division.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createDivisionJSON(@Valid @RequestBody List<DivisionDTO> divisionDTOs)
			throws URISyntaxException {
		log.debug("REST request to save divisions : {}", divisionDTOs.size());
		// if not already exist, save productGroups
		for (DivisionDTO divisionDTO : divisionDTOs) {
			Optional<DivisionDTO> division = divisionService.findByName(divisionDTO.getName());
			if (division.isPresent()) {
				log.debug("Division Name Alredy Exist: " + divisionDTO.getName());
				divisionDTO.setPid(division.get().getPid());
				divisionService.update(divisionDTO);
			} else {
				divisionService.save(divisionDTO);
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-location.json : Create new division.
	 *
	 * @param locationDTOs
	 *            the List<LocationDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-location.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> creatLocationJSON(@Valid @RequestBody List<LocationDTO> locationDTOs)
			throws URISyntaxException {
		log.debug("REST request to save Locations : {}", locationDTOs.size());
		// if not already exist, save productGroups
		for (LocationDTO locationDTO : locationDTOs) {
			Optional<LocationDTO> location = locationService.findByName(locationDTO.getName());
			if (location.isPresent()) {
				log.debug("Location Name Alredy Exist: " + locationDTO.getName());
				locationDTO.setPid(location.get().getPid());
				locationService.update(locationDTO);
			} else {
				locationDTO.setActivated(true);
				locationService.save(locationDTO);
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-product-profile.json : Create new productProfiles.
	 *
	 * @param productProfileDTOs
	 *            the List<productProfile> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createProductProfilesJSON(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) throws URISyntaxException {
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
		// if not already exist, save productProfiles

		List<DivisionDTO> divisions = divisionService.findAllByCompany();

		for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
			productProfileDTO.setDescription("");
			productProfileDTO.setDivisionPid(divisions.get(0).getPid());

			Optional<ProductCategoryDTO> productCategoryDto = productCategoryService
					.findByName(productProfileDTO.getProductCategoryName());

			if (productCategoryDto.isPresent()) {
				productProfileDTO.setProductCategoryPid(productCategoryDto.get().getPid());
			} else {
				ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
				categoryDTO.setName(productProfileDTO.getProductCategoryName());
				categoryDTO = productCategoryService.save(categoryDTO);
				productProfileDTO.setProductCategoryPid(categoryDTO.getPid());
			}
			Optional<ProductProfileDTO> productProfile = productProfileService.findByName(productProfileDTO.getName());

			if (productProfile.isPresent()) {
				log.debug("Product Profile Name Alredy Exist: " + productProfileDTO.getName());
				productProfileDTO.setPid(productProfile.get().getPid());
				productProfileService.update(productProfileDTO);
			} else {
				productProfileService.save(productProfileDTO);
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-account-profile.json : Create a new accountProfile.
	 *
	 * @param accountProfile
	 *            the accountProfile to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new accountProfile, or with status 400 (Bad Request) if the name
	 *         is already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-account-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createAccountProfileJSON(@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs)
			throws URISyntaxException {
		log.debug("REST request to save AccountProfile : {}", accountProfileDTOs.size());

		List<AccountTypeDTO> accountTypeDTO = accountTypeService.findAllByCompany();
		for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {

			if (accountProfileDTO.getName() != null) {
				Optional<AccountProfileDTO> accountProfileAlias = accountProfileService
						.findByAlias(accountProfileDTO.getAlias());

				if (accountProfileAlias.isPresent()) {
					log.debug("Product Profile Name Alredy Exist: " + accountProfileDTO.getName());
					log.debug("Product Profile Alias Alredy Exist: " + accountProfileDTO.getAlias());

					boolean accountProfileName = accountProfileService.findByName(accountProfileDTO.getName())
							.isPresent();
					if (accountProfileName) {
						accountProfileDTO.setName(accountProfileDTO.getName() + "-" + accountProfileDTO.getAlias());
					}
					accountProfileDTO.setPid(accountProfileAlias.get().getPid());
					accountProfileDTO.setAccountTypePid(accountProfileAlias.get().getAccountTypePid());
					accountProfileService.update(accountProfileDTO);
				} else {

					boolean accountProfileName = accountProfileService.findByName(accountProfileDTO.getName())
							.isPresent();
					if (accountProfileName) {
						accountProfileDTO.setName(accountProfileDTO.getName() + "-" + accountProfileDTO.getAlias());
					}
					accountProfileDTO.setAccountStatus(AccountStatus.Unverified);
					accountProfileDTO.setAccountTypePid(accountTypeDTO.get(0).getPid());
					accountProfileService.save(accountProfileDTO);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-ledger-report-tp.json : Create a new ledgerReportTP.
	 *
	 * @param ledgerReportTP
	 *            the ledgerReportTP to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new accountProfile, or with status 400 (Bad Request) if the name
	 *         is already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-ledger-report-tp.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createLedgerReportTPJSON(@Valid @RequestBody List<LedgerReportTPDTO> ledgerReportTPDTOs)
			throws URISyntaxException {
		log.debug("REST request to save LedgerReportTP : {}", ledgerReportTPDTOs.size());

		if (!ledgerReportTPDTOs.isEmpty()) {
			ledgerReportTPRepository.deleteByCompanyIdAndType(SecurityUtils.getCurrentUsersCompanyId(),
					ledgerReportTPDTOs.get(0).getType());
		}

		for (LedgerReportTPDTO ledgerReportTPDTO : ledgerReportTPDTOs) {
			if (ledgerReportTPDTO.getAccountProfileName() != null && ledgerReportTPDTO.getDivisionName() != null) {
				Optional<AccountProfileDTO> accountProfileDTO = accountProfileService
						.findByAlias(ledgerReportTPDTO.getAccountProfileName());
				Optional<DivisionDTO> divisionDTO = divisionService.findByAlias(ledgerReportTPDTO.getDivisionName());
				if (accountProfileDTO.isPresent()) {
					ledgerReportTPDTO.setAccountProfileName(accountProfileDTO.get().getName());
					ledgerReportTPDTO.setAccountProfilePid(accountProfileDTO.get().getPid());
				}
				if (divisionDTO.isPresent()) {
					ledgerReportTPDTO.setDivisionName(divisionDTO.get().getName());
					ledgerReportTPDTO.setDivisionPid(divisionDTO.get().getPid());
				}
				if (divisionDTO.isPresent() && accountProfileDTO.isPresent()) {
					ledgerReportTPService.save(ledgerReportTPDTO);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /hycount-form-element-master.json : Create a new formElementMaster.
	 *
	 * @param formElementMaster
	 *            the formElementMaster to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new accountProfile, or with status 400 (Bad Request) if the name
	 *         is already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/hycount-form-element-master.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createFormElementMasterJSON(
			@Valid @RequestBody List<FormElementMasterDTO> formElementMasterDTOs) throws URISyntaxException {
		log.debug("REST request to save Form Element Master : {}", formElementMasterDTOs.size());
		for (FormElementMasterDTO formElementMasterDTO : formElementMasterDTOs) {

			if (formElementMasterDTO.getFormElementMasterTypeName() != null) {
				Optional<FormElementMasterTypeDTO> formElementMasterType = formElementMasterTypeService
						.findByName(formElementMasterDTO.getFormElementMasterTypeName());
				if (!formElementMasterType.isPresent()) {
					FormElementMasterTypeDTO formElementMasterTypeDTO = new FormElementMasterTypeDTO();
					formElementMasterTypeDTO.setName(formElementMasterDTO.getFormElementMasterTypeName());
					formElementMasterTypeService.save(formElementMasterTypeDTO);
				}
				if (formElementMasterDTO.getName() != null) {
					boolean formElement = formElementMasterService.findOneByName(formElementMasterDTO.getName())
							.isPresent();
					if (formElement) {
						log.debug("Form Element Master Name Alredy Exist: " + formElementMasterDTO.getName());
					} else {
						formElementMasterService.save(formElementMasterDTO);
					}
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
