package com.orderfleet.webapp.web.rest.integration;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderItemDTO;
import com.orderfleet.webapp.web.rest.dto.VatLedgerDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;

/**
 * REST controller for managing order data for third party application.
 * 
 * @author Sarath
 * @since Oct 28, 2016
 */
@RestController
@RequestMapping(value = "/api/tp")
public class TransactionResource {

	private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountingVoucherHeaderService accountingVoucherHeaderService;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentService documentService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;
	
	@Inject
	private GstLedgerRepository gstLedgerRepository;

	// @Inject
	// private DocumentStockLocationSourceRepository
	// documentStockLocationSourceRepository;

	private static final String dynamicDocumentAditional = "Sales Order Addl Info";
	private static final String dynamicDocumentDespach = "Despatch Info";

	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesOrderDTO> getSalesOrderJSON() throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");
		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");

		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndStatusOrderByCreatedDateDesc();
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {

			String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

			// seting inventory heder to salesOrderDTO
			SalesOrderDTO salesOrderDTO = new SalesOrderDTO(inventoryVoucherHeader);
			salesOrderDTO.setAccountProfileDTO(accountProfileMapper
					.accountProfileToAccountProfileDTO(inventoryVoucherHeader.getReceiverAccount()));

			List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();

			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
				SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
				if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
					rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
							.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
				}
				List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

				List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
				for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
						.getInventoryVoucherBatchDetails()) {
					openingStockDTOs = openingStockRepository
							.findByCompanyIdAndProductProfilePidAndBatchNumber(
									inventoryVoucherBatchDetail.getProductProfile().getPid(),
									inventoryVoucherBatchDetail.getBatchNumber())
							.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
				}

				// if (openingStockDTOs.isEmpty()) {
				// List<DocumentStockLocationSource>
				// documentStockLocationSources =
				// documentStockLocationSourceRepository
				// .findByDocumentPid(inventoryVoucherHeader.getDocument().getPid());
				// List<StockLocation> stockLocations =
				// documentStockLocationSources.stream()
				// .map(docstoksou ->
				// docstoksou.getStockLocation()).collect(Collectors.toList());
				// if (!stockLocations.isEmpty()) {
				// List<OpeningStock> openingStocks = openingStockRepository
				// .findAllOpeningStockByProductPidAndStockLocations(
				// inventoryVoucherDetail.getProduct().getPid(),
				// stockLocations);
				// openingStockDTOs =
				// openingStocks.stream().map(OpeningStockDTO::new)
				// .collect(Collectors.toList());
				// }
				// }

				// .......................................................................................

				salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
				salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
				salesOrderItemDTOs.add(salesOrderItemDTO);
			}
			List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
				vatLedgerDTO.setName(accountProfileDTO.getName());
				String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
				vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
				vatLedgerDTO.setVatClass(vatledgerArray[0]);
				vatLedgerDTOs.add(vatLedgerDTO);
			}
			salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
			salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
			List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

			if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

				DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
				DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								inventoryVoucherHeader.getExecutiveTaskExecution().getPid(), dynamicDocumentDespach);

				if (documentHeaderDTOAditonal.getDocumentPid() != null) {
					documentHeaderDTOs.add(documentHeaderDTOAditonal);
				}
				if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
					documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
				}
			}
			salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
			salesOrderDTOs.add(salesOrderDTO);
		}
		return salesOrderDTOs;
	}

	
	
	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	//Method used for getting sales order (aquatech)
	@RequestMapping(value = "/v2/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderDTO> getSalesOrderJsonData() throws URISyntaxException {
		log.debug("REST request to download sales orders (aquatech) : {}");
		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		List<String> inventoryHeaderPid = new ArrayList<String>();
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndTallyStatusOrderByCreatedDateDesc();
		log.debug("IVH size : {}",inventoryVoucherHeaders.size());
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
			
//			if(inventoryVoucherHeader.getTallyDownloadStatus() == TallyDownloadStatus.PROCESSING || 
//					inventoryVoucherHeader.getTallyDownloadStatus() == TallyDownloadStatus.COMPLETED) {
//				continue;
//			}
			
			String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

			// seting inventory heder to salesOrderDTO
			SalesOrderDTO salesOrderDTO = new SalesOrderDTO(inventoryVoucherHeader);
			salesOrderDTO.setAccountProfileDTO(accountProfileMapper
					.accountProfileToAccountProfileDTO(inventoryVoucherHeader.getReceiverAccount()));

			List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();

			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
				SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
				if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
					rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
							.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
				}
				List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

				List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
				for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
						.getInventoryVoucherBatchDetails()) {
					openingStockDTOs = openingStockRepository
							.findByCompanyIdAndProductProfilePidAndBatchNumber(
									inventoryVoucherBatchDetail.getProductProfile().getPid(),
									inventoryVoucherBatchDetail.getBatchNumber())
							.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
				}

				salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
				salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
				salesOrderItemDTOs.add(salesOrderItemDTO);
			}
			List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
				vatLedgerDTO.setName(accountProfileDTO.getName());
				String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
				vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
				vatLedgerDTO.setVatClass(vatledgerArray[0]);
				vatLedgerDTOs.add(vatLedgerDTO);
			}
			salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
			salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
			List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

			if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

				DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
				DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								inventoryVoucherHeader.getExecutiveTaskExecution().getPid(), dynamicDocumentDespach);

				if (documentHeaderDTOAditonal.getDocumentPid() != null) {
					documentHeaderDTOs.add(documentHeaderDTOAditonal);
				}
				if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
					documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
				}
			}
			salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
			List<GstLedger> gstLedgerList = new ArrayList<>();
			gstLedgerList = gstLedgerRepository.findAllByCompanyIdAndActivated(inventoryVoucherHeader.getCompany().getId(),true);
			if(gstLedgerList != null && gstLedgerList.size()!=0) {
				List<GstLedgerDTO> gstLedgerDtos = gstLedgerList.stream().map(gst -> new GstLedgerDTO(gst)).collect(Collectors.toList());
				salesOrderDTO.setGstLedgerDtos(gstLedgerDtos);
			}
			inventoryHeaderPid.add(inventoryVoucherHeader.getPid());
			
			salesOrderDTOs.add(salesOrderDTO);
		}
		if(!salesOrderDTOs.isEmpty()) {
			int updated = inventoryVoucherHeaderRepository.
								updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
			log.debug("updated "+updated+" to PROCESSING");
		}
		
		return salesOrderDTOs;
	}
	
	
	
	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-doc-wise-sales-orders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesOrderDTO> getAllSalesOrdersUnderDocuments(
			@Valid @RequestParam(required = true, name = "documentpids") String documentpids)
			throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");

		String[] allDocumentPids = documentpids.split(",");

		List<String> Pids = new ArrayList<String>(Arrays.asList(allDocumentPids));

		List<Document> documentDTOs = documentRepository.findOneByPidIn(Pids);

		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");

		// List<DocumentDTO>
		// primarySecondaryDocument=primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER);

		// PRIMARY_SALES_ORDER

		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndStatusAndDocumentsOrderByCreatedDateDesc(documentDTOs);
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {

			String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";
			SalesOrderDTO salesOrderDTO = new SalesOrderDTO(inventoryVoucherHeader);
			List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
				SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
				if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
					rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
							.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
				}
				List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();
				List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
				for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
						.getInventoryVoucherBatchDetails()) {
					InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO = new InventoryVoucherBatchDetailDTO(
							inventoryVoucherBatchDetail);
					inventoryVoucherBatchDetailsDTOs.add(inventoryVoucherBatchDetailDTO);
					List<OpeningStock> openingStocks = openingStockRepository
							.findByCompanyIdAndProductProfilePidAndBatchNumber(
									inventoryVoucherBatchDetailDTO.getProductProfilePid(),
									inventoryVoucherBatchDetailDTO.getBatchNumber());
					for (OpeningStock openingStock : openingStocks) {
						OpeningStockDTO openingStockDTO = new OpeningStockDTO(openingStock);
						openingStockDTOs.add(openingStockDTO);
					}
				}
				salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
				salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
				salesOrderItemDTOs.add(salesOrderItemDTO);
			}
			List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
				vatLedgerDTO.setName(accountProfileDTO.getName());
				String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
				vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
				vatLedgerDTO.setVatClass(vatledgerArray[0]);
				vatLedgerDTOs.add(vatLedgerDTO);
			}
			salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
			salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
			List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

			if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

				DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
				DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								inventoryVoucherHeader.getExecutiveTaskExecution().getPid(), dynamicDocumentDespach);

				if (documentHeaderDTOAditonal.getDocumentPid() != null) {
					documentHeaderDTOs.add(documentHeaderDTOAditonal);
				}
				if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
					documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
				}
			}
			salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
			salesOrderDTOs.add(salesOrderDTO);
		}
		return salesOrderDTOs;
	}

	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptDTO> getReceiptsJson() throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		List<ReceiptDTO> receiptDTOs = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAndStatusOrderByCreatedDateDesc();
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
					ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
					receiptDTOs.add(receiptDTO);
				} else {
					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
							.getAccountingVoucherAllocations()) {
						ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherAllocation);
						receiptDTOs.add(receiptDTO);
					}
				}
			}
		}
		
		return receiptDTOs;
	}
	
	
	@RequestMapping(value = "/v2/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptDTO> downloadReceiptsJson() throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		List<ReceiptDTO> receiptDTOs = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyAndStatusOrderByCreatedDateDesc();
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
					ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
					receiptDTOs.add(receiptDTO);
				} else {
					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
							.getAccountingVoucherAllocations()) {
						ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherAllocation);
						receiptDTOs.add(receiptDTO);
					}
				}
			}
		}
		
		if(!receiptDTOs.isEmpty()) {
			int updated = accountingVoucherHeaderRepository.
					updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING, receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid()).collect(Collectors.toList()));
			log.debug("updated "+updated+" to PROCESSING");
		}
		return receiptDTOs;
	}

	/**
	 * POST /update-receipt-status .
	 *
	 * @param String
	 *            the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteReceiptStatus(@Valid @RequestBody List<String> AccountingVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Accounting Voucher Header Status : {}", AccountingVoucherHeaderPids.size());
		for (String accountingVoucherHeaderPid : AccountingVoucherHeaderPids) {
			Optional<AccountingVoucherHeaderDTO> accountingVoucherHeader = accountingVoucherHeaderService
					.findOneByPid(accountingVoucherHeaderPid);
			if (accountingVoucherHeader.isPresent()) {
				accountingVoucherHeaderService.updateAccountingVoucherHeaderStatus(accountingVoucherHeader.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/v2/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody List<String> accountingVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Accounting Voucher Header Status : {}", accountingVoucherHeaderPids.size());
			if (!accountingVoucherHeaderPids.isEmpty()) {
				accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.COMPLETED,  accountingVoucherHeaderPids);
			}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /update-order-status .
	 *
	 * @param String
	 *            the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-order-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteOrderStatus(@Valid @RequestBody List<String> inventoryVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Inventory Voucher Header Status : {}", inventoryVoucherHeaderPids.size());

		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders = inventoryVoucherHeaderService
				.findAllByCompanyIdAndInventoryPidIn(inventoryVoucherHeaderPids);
		if (!inventoryVoucherHeaders.isEmpty()) {
			inventoryVoucherHeaderService.updateInventoryVoucherHeadersStatus(inventoryVoucherHeaders);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	//method used for update order status based on tally response : updating variable TallyDownloadStatus enum
	@RequestMapping(value = "/v2/update-order-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Void> UpdarteOrderStatusWithTallyStatus(@Valid @RequestBody List<String> inventoryVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Inventory Voucher Header Status (aquatech) : {}", inventoryVoucherHeaderPids.size());

		if (!inventoryVoucherHeaderPids.isEmpty()) {
			inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.COMPLETED,inventoryVoucherHeaderPids);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /get-receipts.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-ledgers.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<AccountProfileDTO> getLedgersJson() throws URISyntaxException {
		log.debug("REST request to download ledgers : {}");
		List<AccountProfileDTO> allAccountProfileDTOs = accountProfileService
				.findAllByCompanyAndAccountImportStatus(false);

		// seting location name
		allAccountProfileDTOs.forEach(acc -> {
			List<LocationAccountProfile> locationAccountProfileDTOs = locationAccountProfileService
					.findAllByCompanyAndAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(), acc.getPid());
			if (locationAccountProfileDTOs != null && locationAccountProfileDTOs.size() > 0) {
				acc.setLocation(locationAccountProfileDTOs.get(0).getLocation().getName());
			}
		});

		return allAccountProfileDTOs;
	}

	/**
	 * POST /update-ledgers-status .
	 *
	 * @param String
	 *            the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-ledgers-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteLedgersStatus(@Valid @RequestBody List<String> AccountProfilePids)
			throws URISyntaxException {
		log.debug("REST request to update Account Profile Status : {}", AccountProfilePids.size());
		for (String AccountProfilePid : AccountProfilePids) {
			Optional<AccountProfileDTO> AccountProfileDTO = accountProfileService.findOneByPid(AccountProfilePid);
			if (AccountProfileDTO.isPresent()) {
				accountProfileService.updateAccountProfileImportStatus(AccountProfileDTO.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /send sales from tally .
	 *
	 * @param String
	 *            the List<inventoryVoucherHeaderDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/tally-accounting-vouchers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> sendReceiptVouchersFromTally(
			@Valid @RequestBody List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs) {
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (!accountingVoucherHeaderDTOs.isEmpty() && opUser.isPresent()) {
			ActivityDTO activityDto = this.getActivityByName("Receipt Data Transfer");
			DocumentDTO documentDto = this.getDocumentByName("receipts from tally");
			LocalDateTime documentDate = accountingVoucherHeaderDTOs.get(0).getDocumentDate();
			// delete InventoryVoucherHeaders for avoidind duplicates and for new or updated
			// sales vouchers
			List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(
							documentDate, activityDto.getPid(), documentDto.getPid());
			if (!accountingVoucherHeaders.isEmpty()) {
				deleteAccountingVoucherHeaders(accountingVoucherHeaders);
				//return ResponseEntity.ok().body("Receipt already uploaded in this date : " + documentDate);
			}

			int documentUniqueNoToPref = 1;
			for (AccountingVoucherHeaderDTO accountingVoucherHeaderDTO : accountingVoucherHeaderDTOs) {
				documentUniqueNoToPref = documentUniqueNoToPref + 1;
				ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();
				// get account profile
				Optional<AccountProfileDTO> accountProfileDTO = accountProfileService
						.findByName(accountingVoucherHeaderDTO.getAccountProfileName());
				if (accountProfileDTO.isPresent()) {
					AccountProfileDTO accpDTO = accountProfileDTO.get();
					eteDTO.setAccountProfileName(accpDTO.getName());
					eteDTO.setAccountProfilePid(accpDTO.getPid());
					eteDTO.setAccountTypeName(accpDTO.getAccountTypeName());
					eteDTO.setAccountTypePid(accpDTO.getAccountTypePid());
					eteDTO.setDate(accountingVoucherHeaderDTO.getDocumentDate());
					eteDTO.setActivityName(activityDto.getName());
					eteDTO.setActivityPid(activityDto.getPid());
					eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
					eteDTO.setDate(accountingVoucherHeaderDTO.getDocumentDate());
					eteDTO.setLocationType(LocationType.NoLocation);
					eteDTO.setUserName(opUser.get().getFirstName());
					eteDTO.setUserPid(opUser.get().getPid());

					accountingVoucherHeaderDTO.setAccountProfilePid(accpDTO.getPid());
					accountingVoucherHeaderDTO
							.setDocumentNumberLocal(accountingVoucherHeaderDTO.getDocumentNumberLocal());
					accountingVoucherHeaderDTO
							.setDocumentNumberServer(accountingVoucherHeaderDTO.getDocumentNumberServer());
					accountingVoucherHeaderDTO.setDocumentPid(documentDto.getPid());
					accountingVoucherHeaderDTO.setDocumentName(documentDto.getName());
					accountingVoucherHeaderDTO.setStatus(true);

					accountingVoucherHeaderDTO.setAccountingVoucherDetails(new ArrayList<>());

					List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
					accountingVouchers.add(accountingVoucherHeaderDTO);
					ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

					executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
					executiveTaskSubmissionDTO.setAccountingVouchers(accountingVouchers);
					executiveTaskSubmissionService.saveTPExecutiveTaskSubmission(executiveTaskSubmissionDTO, opUser.get());
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /send sales from tally .
	 *
	 * @param String
	 *            the List<inventoryVoucherHeaderDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/tally-inventory-vouchers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> sendSalesVouchersFromTally(
			@Valid @RequestBody List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs) throws URISyntaxException {
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (!inventoryVoucherHeaderDTOs.isEmpty() && opUser.isPresent()) {
			ActivityDTO activityDto = this.getActivityByName("Sales Data Transfer");
			DocumentDTO documentDto = this.getDocumentByName("sales");
			LocalDateTime documentDate = inventoryVoucherHeaderDTOs.get(0).getDocumentDate();

			// delete InventoryVoucherHeaders for avoidind duplicates and for
			// new or updated sales vouchers
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(
							documentDate, activityDto.getPid(), documentDto.getPid());
			if (!inventoryVoucherHeaders.isEmpty()) {
				deleteInventoryVoucherHeaders(inventoryVoucherHeaders);
				//return ResponseEntity.ok().body("Sales already uploaded in this date : " + documentDate);
			}
			int documentUniqueNoToPref = 1;
			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDTOs) {
				documentUniqueNoToPref = documentUniqueNoToPref + 1;

				ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();

				// get account profile
				Optional<AccountProfileDTO> accountProfileDTO = accountProfileService
						.findByName(inventoryVoucherHeaderDTO.getReceiverAccountName());

				if (accountProfileDTO.isPresent()) {
					AccountProfileDTO accpDTO = accountProfileDTO.get();
					eteDTO.setAccountProfileName(accpDTO.getName());
					eteDTO.setAccountProfilePid(accpDTO.getPid());
					eteDTO.setAccountTypeName(accpDTO.getAccountTypeName());
					eteDTO.setAccountTypePid(accpDTO.getAccountTypePid());
					eteDTO.setDate(inventoryVoucherHeaderDTO.getDocumentDate());
					eteDTO.setActivityName(activityDto.getName());
					eteDTO.setActivityPid(activityDto.getPid());
					eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
					eteDTO.setDate(inventoryVoucherHeaderDTO.getDocumentDate());
					eteDTO.setLocationType(LocationType.NoLocation);
					eteDTO.setUserName(opUser.get().getFirstName());
					eteDTO.setUserPid(opUser.get().getPid());

					inventoryVoucherHeaderDTO.setReceiverAccountPid(accpDTO.getPid());
					inventoryVoucherHeaderDTO
							.setDocumentNumberLocal(inventoryVoucherHeaderDTO.getDocumentNumberLocal());
					inventoryVoucherHeaderDTO
							.setDocumentNumberServer(inventoryVoucherHeaderDTO.getDocumentNumberServer());
					inventoryVoucherHeaderDTO.setDocumentPid(documentDto.getPid());
					inventoryVoucherHeaderDTO.setDocumentName(documentDto.getName());
					inventoryVoucherHeaderDTO.setStatus(true);

					for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
							.getInventoryVoucherDetails()) {
						Optional<ProductProfileDTO> productProfileDTO = productProfileService
								.findByName(inventoryVoucherDetailDTO.getProductName());
						if (productProfileDTO.isPresent()) {
							inventoryVoucherDetailDTO.setProductPid(productProfileDTO.get().getPid());
						}
					}
					List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
					inventoryVouchers.add(inventoryVoucherHeaderDTO);

					ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

					executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
					executiveTaskSubmissionDTO.setInventoryVouchers(inventoryVouchers);

					executiveTaskSubmissionService.saveTPExecutiveTaskSubmission(executiveTaskSubmissionDTO, opUser.get());
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * delete InventoryVoucherHeaders for avoidind duplicates and for new or
	 * updated sales vouchers
	 * 
	 * @param inventoryVoucherHeaders
	 */
	@Transactional
	private void deleteInventoryVoucherHeaders(List<InventoryVoucherHeader> inventoryVoucherHeaders) {
		log.debug("request to delete inventory Voucher Headers : {}", inventoryVoucherHeaders.size());
		List<ExecutiveTaskExecution> executivetaskexecutions = inventoryVoucherHeaders.stream()
				.map(a -> a.getExecutiveTaskExecution()).collect(Collectors.toList());
		inventoryVoucherDetailRepository.deleteByInventoryVoucherHeaderIdIn(
				inventoryVoucherHeaders.stream().map(a -> a.getId()).collect(Collectors.toList()));
		inventoryVoucherHeaderRepository.delete(inventoryVoucherHeaders);
		executiveTaskExecutionRepository.delete(executivetaskexecutions);
	}

	/**
	 * delete InventoryVoucherHeaders for avoidind duplicates and for new or
	 * updated sales vouchers
	 * 
	 * @param inventoryVoucherHeaders
	 */
	@Transactional
	private void deleteAccountingVoucherHeaders(List<AccountingVoucherHeader> accountingVoucherHeaders) {
		log.debug("request to delete accounting Voucher Headers : {}", accountingVoucherHeaders.size());
		List<ExecutiveTaskExecution> executivetaskexecutions = accountingVoucherHeaders.stream()
				.map(a -> a.getExecutiveTaskExecution()).collect(Collectors.toList());
		accountingVoucherHeaderRepository.deleteInBatch(accountingVoucherHeaders);
		executiveTaskExecutionRepository.delete(executivetaskexecutions);
	}

	private ActivityDTO getActivityByName(String name) {
		Optional<ActivityDTO> activityDTO = activityService.findByName(name);
		if (activityDTO.isPresent()) {
			return activityDTO.get();
		}
		ActivityDTO activityDto = new ActivityDTO();
		activityDto.setName(name);
		activityDto.setActivated(true);
		activityDto.setHasDefaultAccount(false);
		activityDto.setDescription("Used to send data "+ name +" from tally");
		return activityService.save(activityDto);
	}

	private DocumentDTO getDocumentByName(String name) {
		Optional<DocumentDTO> optionalDocument = documentService.findByName(name);
		if (optionalDocument.isPresent()) {
			return optionalDocument.get();
		}
		DocumentDTO documentDto = new DocumentDTO();
		documentDto.setName(name);
		documentDto.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		documentDto.setDocumentPrefix("Receipts");
		documentDto.setDescription("used to send data "+ name +" from tally");
		return documentService.save(documentDto);
	}
}
