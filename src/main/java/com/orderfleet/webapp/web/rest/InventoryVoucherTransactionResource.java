package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderHistoryRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DocumentStockLocationDestinationService;
import com.orderfleet.webapp.service.DocumentStockLocationSourceService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.OrderStatusService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReferenceDocumentService;
import com.orderfleet.webapp.service.SetOrderStatusInventoryService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExevTaskExenDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiverSupplierAccountDTO;
import com.orderfleet.webapp.web.rest.dto.SourceDestinationLocationDTO;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.mapper.OpeningStockMapper;
import com.orderfleet.webapp.web.rest.mapper.StaticFormJSCodeMapper;

/**
 * Web controller for managing Inventory Voucher.
 * 
 * @author Muhammed Riyas T
 * @since December 07, 2016
 */
@Controller
@RequestMapping("/web/inventory-voucher-transaction")
public class InventoryVoucherTransactionResource {

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherTransactionResource.class);

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private DocumentInventoryVoucherColumnService documentInventoryVoucherColumnService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherHeaderHistoryRepository inventoryVoucherHeaderHistoryRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private OpeningStockMapper openingStockMapper;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentAccountTypeRepository documentAccountTypeRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodesRepository;

	@Inject
	private StaticFormJSCodeMapper staticFormJSCodeMapper;

	@Inject
	private ReferenceDocumentService referenceDocumentService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	
	@Inject
	private ActivityService activityService;
	
	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private DocumentService documentService;
	
	@Inject
	private DocumentStockLocationSourceService documentStockLocationSourceService;
	
	@Inject
	private DocumentStockLocationDestinationService documentStockLocationDestinationService;
	
	@Inject
	private TaskSubmissionPostSave taskSubmissionPostSave;
	
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private OrderStatusService orderStatusService;
	
	@Inject
	private SetOrderStatusInventoryService setOrderStatusInventoryService;
	
	/**
	 * GET /inventory-voucher-transaction
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(method = RequestMethod.GET)
	public String inventoryVoucherTransaction(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Inventory Voucher Transaction");
		model.addAttribute("statuses", orderStatusService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER));
		model.addAttribute("activityDocuments", getUserAcivityDocuments());
		model.addAttribute("products", productProfileService.findAllByCompany());
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		EmployeeProfileDTO employeeProfile = employeeProfileService
				.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (employeeProfile != null) {
			model.addAttribute("currentEmployeePid", employeeProfile.getPid());
		} else {
			model.addAttribute("currentEmployeePid", "no");
		}
		return "company/inventoryVoucherTransaction";
	}

	@Timed
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> inventoryVoucher(
			@RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) throws URISyntaxException {
		log.debug("Web request to save dynamic documents");
		TaskSubmissionResponse taskSubmissionResponse = null;
		if (executiveTaskSubmissionDTO.getInventoryVouchers().get(0).getPid() == null) {
			// save
			LocalDateTime localDateTime = LocalDateTime.now();
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setDate(localDateTime);
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setLocationType(LocationType.NoLocation);
			executiveTaskSubmissionDTO.getInventoryVouchers().get(0).setDocumentDate(localDateTime);
			// create and set Document Number Local
			String documentNumberLocal = createDocumentNumber(
					executiveTaskSubmissionDTO.getInventoryVouchers().get(0).getDocumentPid());
			executiveTaskSubmissionDTO.getInventoryVouchers().get(0).setDocumentNumberLocal(documentNumberLocal);
			ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper = executiveTaskSubmissionService.executiveTaskSubmission(executiveTaskSubmissionDTO);
			if(tsTransactionWrapper != null) {
				taskSubmissionResponse = tsTransactionWrapper.getTaskSubmissionResponse();
				taskSubmissionPostSave.doPostSaveExecutivetaskSubmission(tsTransactionWrapper, executiveTaskSubmissionDTO);
			}
		} else {
			// update
			taskSubmissionResponse = executiveTaskSubmissionService.updateInventoryVoucher(executiveTaskSubmissionDTO);
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	private String createDocumentNumber(String documentPid) {
		Optional<Document> document = documentRepository.findOneByPid(documentPid);
		String documentNumber = System.currentTimeMillis() + "_" + SecurityUtils.getCurrentUserLogin() + "_"
				+ document.get().getDocumentPrefix();
		// find previous document number
		String id="INV_QUERY_123";
		String description="finding the doc number by createdLogin and order by created date in desc ";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findTop1ByCreatedByLoginOrderByCreatedDateDesc(SecurityUtils.getCurrentUserLogin());

		if (inventoryVoucherHeader != null) {
			String[] arr = inventoryVoucherHeader.getDocumentNumberLocal().split("_");
			int i = Integer.valueOf(arr[arr.length - 1]) + 1;
			documentNumber += "_" + i;
		} else {
			documentNumber += "_0";
		}
		return documentNumber;
	}

	private List<DocumentDTO> getUserAcivityDocuments() {
		List<DocumentDTO> documents = new ArrayList<>();
		List<ActivityDTO> activityDTOs = userActivityService.findActivitiesByUserIsCurrentUser();
		for (ActivityDTO activityDTO : activityDTOs) {
			for (DocumentDTO documentDTO : activityDTO.getDocuments()) {
				if (documentDTO.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
					documentDTO.setPid(documentDTO.getPid() + "~" + activityDTO.getPid());
					documentDTO.setName(documentDTO.getName() + " --- [" + activityDTO.getName() + "]");
					documents.add(documentDTO);
				}
			}
		}
		return documents;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<InventoryVoucherHeaderDTO> search(@RequestParam("documentPid") String documentPid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid) {
		log.debug("Web request to  search inventory vouchers");
		List<InventoryVoucherHeaderDTO> inventoryVouchers = getInventoryVouchers(accountPid, documentPid);
		return inventoryVouchers;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/{inventoryVoucherPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody InventoryVoucherHeaderDTO getInventoryVoucher(
			@PathVariable("inventoryVoucherPid") String inventoryVoucherPid) {
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherPid).get();
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(inventoryVoucherHeader);
		inventoryVoucherHeaderDTO.setInventoryVoucherDetails(inventoryVoucherHeader.getInventoryVoucherDetails()
				.stream().map(InventoryVoucherDetailDTO::new).collect(Collectors.toList()));
		return inventoryVoucherHeaderDTO;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/product-details/{productPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ProductProfileDTO getProductDetails(@PathVariable("productPid") String productPid) {
		ProductProfileDTO productProfileDTO = productProfileService.findOneByPid(productPid).get();
		return productProfileDTO;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/previous-document-number", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getPreviousDocumentNumber() {
		String id="INV_QUERY_123";
		String description="finding the doc number by createdLogin and order by created date in desc ";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findTop1ByCreatedByLoginOrderByCreatedDateDesc(SecurityUtils.getCurrentUserLogin());
		if (inventoryVoucherHeader != null) {
			return inventoryVoucherHeader.getDocumentNumberLocal();
		}
		return "nothing";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/batch/{productPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<OpeningStockDTO> getBatchs(@PathVariable("productPid") String productPid) {
		log.debug("REST request to get product batch numbers");
		List<OpeningStock> openingStocks = openingStockRepository.findByCompanyIdAndProductProfilePid(productPid);
		// group distinct batch numbers by product profile and location
		// return
		// openingStocks.parallelStream().collect(Collectors.groupingBy(os ->
		// os.getProductProfile().getPid(),
		// Collectors.mapping(OpeningStock::getBatchNumber,
		// Collectors.toSet())));
		return openingStockMapper.openingStocksToOpeningStockDTOs(openingStocks);
	}

	private List<InventoryVoucherHeaderDTO> getInventoryVouchers(String accountPid, String documentPid) {
		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
		String id="INV_QUERY_122";
		String description="listing inv_voucher by using executivetaskexecution profile pid  and docPid";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(accountPid, documentPid);
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(inventoryVoucherHeader);
			// find history
			List<InventoryVoucherHeaderHistory> inventoryVoucherHeaderHistories = inventoryVoucherHeaderHistoryRepository
					.findByPidOrderByIdDesc(inventoryVoucherHeader.getPid());
			if (inventoryVoucherHeaderHistories.size() > 0) {
				List<InventoryVoucherHeaderDTO> history = inventoryVoucherHeaderHistories.stream()
						.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
				inventoryVoucherHeaderDTO.setHistory(history);
			}
			inventoryVouchers.add(inventoryVoucherHeaderDTO);
		}
		return inventoryVouchers;
	}

	/**
	 * GET /inventory-voucher-transaction/documentInventoryVoucherColumns/:
	 * documentPid : get inventoryVoucherColumns.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         inventoryVoucherColumnDTOs, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/documentInventoryVoucherColumns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherColumnDTO>> getDocumentInventoryVoucherColumns(
			@PathVariable String documentPid) {
		log.debug("Web request to get documentInventoryVoucherColumns");
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = documentInventoryVoucherColumnService
				.findByDocumentPid(documentPid);
		List<InventoryVoucherColumnDTO> inventoryVoucherColumnDTOs = documentInventoryVoucherColumns.stream()
				.map(InventoryVoucherColumnDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(inventoryVoucherColumnDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/static-js-code/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StaticFormJSCodeDTO> getStaticJsCode(@PathVariable String documentPid) {
		log.debug("Web request to get static-js-code");
		StaticFormJSCode staticFormJSCode = staticFormJSCodesRepository.findByDocumentPidAndCompany(documentPid);
		StaticFormJSCodeDTO result = staticFormJSCodeMapper.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/reference-documents/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentDTO>> getReferenceDocuments(@PathVariable String documentPid) {
		log.debug("Web request to get reference-documents");
		return new ResponseEntity<>(referenceDocumentService.findReferenceDocumentsByDocumentPid(documentPid),
				HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/load-receiver-supplier-account/{activityPid}/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ReceiverSupplierAccountDTO loadReceiverSupplierAccounts(
			@PathVariable("activityPid") String activityPid, @PathVariable("documentPid") String documentPid) {
		log.debug("Web request to  load receivers and suppliers by activitypid " + activityPid);

		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		List<AccountType> allReceiverAccountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.Receiver);
		List<AccountType> allSupplierAccountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.Supplier);

		List<AccountProfileDTO> receiverAccounts = new ArrayList<>();
		if (allReceiverAccountTypes.size() > 0) {
			receiverAccounts = accountProfileMapper.accountProfilesToAccountProfileDTOs(locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAccountTypesAndAccountProfileActivated(locations,
							allReceiverAccountTypes));
		}
		List<AccountProfileDTO> supplierAccounts = new ArrayList<>();
		if (allSupplierAccountTypes.size() > 0) {
			supplierAccounts = accountProfileMapper.accountProfilesToAccountProfileDTOs(locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAccountTypesAndAccountProfileActivated(locations,
							allSupplierAccountTypes));
		}
		return new ReceiverSupplierAccountDTO(receiverAccounts, supplierAccounts);
	}
	
	@Timed
	@RequestMapping(value = "/load-account-profiles/{documentActivity}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<AccountProfileDTO> loadAccountProfiles(@PathVariable("documentActivity") String documentActivity) {
		log.debug("Web request to  load AccountProfiles by activitypid  {}", documentActivity);
		String [] stringArray=documentActivity.split("~");
		Optional<DocumentDTO>documentDTO= documentService.findOneByPid(stringArray[0]);
		Optional<ActivityDTO> activityDTO = activityService.findOneByPid(stringArray[1]);
		List<AccountProfileDTO> filteredAccounts = null;
		if (activityDTO.isPresent()) {
			Set<AccountTypeDTO> accountTypes = activityDTO.get().getActivityAccountTypes();
			List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
					.findAccountProfilesByCurrentUserLocations();
			filteredAccounts = accountProfileDTOs.stream().filter(acc -> {
				AccountTypeDTO accountTypeDTO = accountTypes.stream()
						.filter(at -> at.getPid().equals(acc.getAccountTypePid())).findAny().orElse(null);
				if (accountTypeDTO == null) {
					return false;
				}
				return true;
			}).map(ap -> ap).collect(Collectors.toList());
			
			if(activityDTO.get().getHasDefaultAccount()) {
				for(AccountProfileDTO accountProfileDTO:filteredAccounts) {
					accountProfileDTO.setHasDefaultAccountInventory(true);
				}
			}
		}
		if (documentDTO.isPresent()) {
			if(documentDTO.get().isPromptStockLocation()) {
				for(AccountProfileDTO accountProfileDTO:filteredAccounts) {
					accountProfileDTO.setPromptStockLocationInventory(true);
				}
			}
		}
		return filteredAccounts;
	}
	@Timed
	@RequestMapping(value = "/load-source-destination-location/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SourceDestinationLocationDTO loadSourceDestinationLocation(@PathVariable("documentPid") String documentPid) {
				
		SourceDestinationLocationDTO sourceDestinationLocationDTO=new SourceDestinationLocationDTO();
		List<StockLocationDTO> sourceStockLocationDTOs=documentStockLocationSourceService.findStockLocationsByDocumentPid(documentPid);
		List<StockLocationDTO> destinationStockLocationDTOs=documentStockLocationDestinationService.findStockLocationsByDocumentPid(documentPid);
		sourceDestinationLocationDTO.setSourceStockLocationDTOs(sourceStockLocationDTOs);
		sourceDestinationLocationDTO.setDestinationStockLocationDTOs(destinationStockLocationDTOs);
		return sourceDestinationLocationDTO;
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/loadExecutiveTaskExecution", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ExevTaskExenDTO loadExecutiveTaskExecution(@RequestParam("pid") String pid,@RequestParam("invPid") String invPid) {
		log.debug("Web request to get ExevTaskExenDTO");
		Optional<ExecutiveTaskExecution>optionalExecutiveTaskExecution=executiveTaskExecutionRepository.findOneByPid(pid); 
		Optional<InventoryVoucherHeader>optionalInventoryVoucherHeader=inventoryVoucherHeaderRepository.findOneByPid(invPid);
		ExevTaskExenDTO exevTaskExenDTO=new ExevTaskExenDTO();
		if(optionalExecutiveTaskExecution.isPresent() && optionalInventoryVoucherHeader.isPresent()) {
			exevTaskExenDTO.setAccountPid(optionalInventoryVoucherHeader.get().getReceiverAccount().getPid());
			exevTaskExenDTO.setActivityPid(optionalExecutiveTaskExecution.get().getActivity().getPid());
			exevTaskExenDTO.setUserPid(optionalExecutiveTaskExecution.get().getUser().getPid());
			exevTaskExenDTO.setDocumentPid(optionalInventoryVoucherHeader.get().getDocument().getPid());
			exevTaskExenDTO.setEmployeePid(optionalInventoryVoucherHeader.get().getEmployee().getPid());
		}
		return exevTaskExenDTO;
	}
	
	@Timed
	@RequestMapping(value = "/changeOrderStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeOrderStatus(@RequestParam("inventoryPid") String inventoryPid,
			@RequestParam("status") String status) throws URISyntaxException {
		setOrderStatusInventoryService.saveSetOrderStatusInventoryHistory(inventoryPid, status);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
