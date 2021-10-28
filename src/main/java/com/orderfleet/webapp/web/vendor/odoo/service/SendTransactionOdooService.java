package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoiceLine;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseMessageOdooInvoice;

@Service
public class SendTransactionOdooService {

	private final Logger log = LoggerFactory.getLogger(SendTransactionOdooService.class);
	
	private static String SEND_INVOICE_API_URL = "http://edappal.nellara.com:1214/web/api/create_invoice";	

	public static int successCount = 0;
	public static int failedCount = 0;
	public static int unattendedCount = 0;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;
	
	@Inject
	private UserStockLocationRepository userStockLocationRepository;
	
	@Inject
	private UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	
	@Transactional
	public void sendInvoicesToOdoo(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper) {
		List<InventoryVoucherHeader> inventoryVouchers = tsTransactionWrapper.getInventoryVouchers();
		
		if (inventoryVouchers != null && inventoryVouchers.size()>0) {
			InventoryVoucherHeader ivh = inventoryVouchers.get(0);
			
			Long companyId = ivh.getCompany().getId();
			log.info("send invoice to odoo for company id : {}", companyId);
			List<PrimarySecondaryDocument> primarySecDocSales = new ArrayList<>();
			List<PrimarySecondaryDocument> primarySecDocSalesReturn = new ArrayList<>();
			primarySecDocSales = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES, companyId);
			primarySecDocSalesReturn = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_RETURN, companyId);

			if (primarySecDocSales.isEmpty()) {
				log.info("........No PrimarySecondaryDocument configuration Available...........");
				// return salesOrderDTOs;
			}
			List<Long> documentIdListSales = primarySecDocSales.stream().map(psd -> psd.getDocument().getId())
					.collect(Collectors.toList());

			List<Long> documentIdListSalesReturn = primarySecDocSalesReturn.stream().map(psd -> psd.getDocument().getId())
					.collect(Collectors.toList());
			

			if (ivh != null) {
				String id="INV_QUERY_161";
				String description=" Updating invVou Header by Tally download status using pid";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");




				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, Arrays.asList(ivh.getPid()));
				log.debug("updated " + updated + " to PROCESSING");
			}
			
			AccountProfile opRecAccPro = ivh.getReceiverAccount();
			AccountProfile opSupAccPro = ivh.getReceiverAccount();
			String companyPid = ivh.getCompany().getPid();
			
			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);

			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();

			Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
					.filter(us -> us.getUser().getPid().equals(ivh.getCreatedBy().getPid())).findAny();
			

			Set<String> ivhPids = new HashSet<>();
			ivhPids.add(ivh.getPid());
			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			 
			ParamsOneOdooInvoice invoice = new ParamsOneOdooInvoice();
			invoice.setCreate(true);
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			invoice.setInvoice_date(ivh.getDocumentDate() != null ? ivh.getDocumentDate().format(formatter1): "");
			invoice.setLocation_id(Long.parseLong(opUserStockLocation.get().getStockLocation().getAlias()));
			invoice.setRounding_amt(ivh.getRoundedOff());
			invoice.setReference(ivh.getDocumentNumberServer());		
			 
			if (documentIdListSales.contains(ivh.getDocument().getId())) { // Primary Sales
				invoice.setInvoice_type("out_invoice");
				invoice.setJournal_type("sale");
				invoice.setPartner_id(opRecAccPro.getCustomerId() != null && !opRecAccPro.getCustomerId().equals("")
						? Long.parseLong(opRecAccPro.getCustomerId())
						: 0);
				
				invoice.setOrigin(ivh.getDocumentNumberServer());
			} else if (documentIdListSalesReturn.contains(ivh.getDocument().getId())) {  // Primary Sales Return
				invoice.setInvoice_type("out_refund");
				invoice.setJournal_type("sale_refund");
				invoice.setPartner_id(
						opSupAccPro.getCustomerId() != null && !opSupAccPro.getCustomerId().equals("")
								? Long.parseLong(opSupAccPro.getCustomerId())
								: 0);
				invoice.setOrigin(ivh.getReferenceInvoiceNumber() != null ? ivh.getReferenceInvoiceNumber(): ivh.getDocumentNumberServer());
			}
			
			
			List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
					.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(ivh.getId().toString()))
					.collect(Collectors.toList()).stream()
					.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId)).collect(Collectors.toList());

			List<OdooInvoiceLine> odooInvoiceLines = new ArrayList<OdooInvoiceLine>();
			for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {

				OdooInvoiceLine odooInvoiceLine = new OdooInvoiceLine();

				odooInvoiceLine.setIs_foc(
						inventoryVoucherDetail.getFreeQuantity() > 0.0 ? inventoryVoucherDetail.getFreeQuantity()
								: 0.0);

				odooInvoiceLine.setDiscount(inventoryVoucherDetail.getDiscountPercentage());

				odooInvoiceLine.setProduct_id(inventoryVoucherDetail.getProduct().getProductId() != null
						&& !inventoryVoucherDetail.getProduct().getProductId().equals("")
								? Long.parseLong(inventoryVoucherDetail.getProduct().getProductId())
								: 0);
				odooInvoiceLine.setPrice_unit(inventoryVoucherDetail.getSellingRate());
				odooInvoiceLine.setQuantity(inventoryVoucherDetail.getQuantity());

				Optional<UnitOfMeasureProduct> opUnitOfMeasure = unitOfMeasureProducts.stream()
						.filter(us -> us.getProduct().getPid().equals(inventoryVoucherDetail.getProduct().getPid()))
						.findAny();

				if (opUnitOfMeasure.isPresent()) {
					odooInvoiceLine.setUom_id(Long.parseLong(opUnitOfMeasure.get().getUnitOfMeasure().getUomId()));
				}

				odooInvoiceLines.add(odooInvoiceLine);
			}

			invoice.setInvoice_lines(odooInvoiceLines);
			
			log.info("Sending Invoice to Odoo....");

			RequestBodyOneOdooInvoice request = new RequestBodyOneOdooInvoice();

			request.setJsonrpc("2.0");

			request.setParams(invoice);
			
			HttpEntity<RequestBodyOneOdooInvoice> entity = new HttpEntity<>(request,
					RestClientUtil.createTokenAuthHeaders());

			log.info(entity.getBody().toString() + "");
			
			ObjectMapper Obj = new ObjectMapper();

			// get object as a json string
			String jsonStr;
			try {
				jsonStr = Obj.writeValueAsString(request);
				log.info(jsonStr);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get URL: " + SEND_INVOICE_API_URL);

			try {

				ResponseBodyOneOdooInvoice responseBodyOneOdooInvoice = restTemplate.postForObject(SEND_INVOICE_API_URL,
						entity, ResponseBodyOneOdooInvoice.class);
				log.info(responseBodyOneOdooInvoice + "");

				// get object as a json string
				String jsonStr1;
				try {
					jsonStr1 = Obj.writeValueAsString(responseBodyOneOdooInvoice);
					log.info(jsonStr1);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				log.info("Odoo Invoice Created Success Size= " + responseBodyOneOdooInvoice.getResult().getMessage()
						+ "------------");

				changeServerDownloadStatus(responseBodyOneOdooInvoice.getResult().getMessage());

			} catch (HttpClientErrorException exception) {
				if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
					// throw new ServiceException(exception.getResponseBodyAsString());
					log.info(exception.getResponseBodyAsString());
					log.info("-------------------------");
					log.info(exception.getMessage());
					log.info("-------------------------");
					exception.printStackTrace();
				}
				log.info(exception.getMessage());
				// throw new ServiceException(exception.getMessage());
			} catch (Exception exception) {

				log.info(exception.getMessage());
				log.info("-------------------------");
				exception.printStackTrace();
				log.info("-------------------------");

				// throw new ServiceException(exception.getMessage());
			}
		}
	}
	
	private void changeServerDownloadStatus(List<ResponseMessageOdooInvoice> message) {
		if (!message.isEmpty()) {

			List<String> successReferences = new ArrayList<>();
			List<String> failedReferences = new ArrayList<>();
			List<String> unattendedReferences = new ArrayList<>();
			for (ResponseMessageOdooInvoice response : message) {

				if (response.getStatus_code() == 0) {
					successReferences.add(response.getReference());
				} else if (response.getStatus_code() == 1) {
					failedReferences.add(response.getReference());
				} else {
					unattendedReferences.add(response.getReference());
				}
			}

			log.info("Total Responses :- " + message.size() + "-----Success Responses :-" + successReferences.size()
					+ "-----Failed Responses :-" + failedReferences.size() + "-----Unattended Responses :-"
					+ unattendedReferences.size());

//			List<String> inventoryHeaderPid = inventoryVoucherHeaderRepository
//					.findAllByDocumentNumberServer(references);

			List<InventoryVoucherHeader> successInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> successdistinctElements = new ArrayList<>();

			if (successReferences.size() > 0) {
				String id="INV_QUERY_200";
				String description="Find all headers by documnetNumberServer";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

				successInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(successReferences);
				successdistinctElements = successInventoryHeaders.stream().distinct().collect(Collectors.toList());
			}
			List<InventoryVoucherHeader> failedInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> faileddistinctElements = new ArrayList<>();
			if (failedReferences.size() > 0) {
				failedInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(failedReferences);
				faileddistinctElements = failedInventoryHeaders.stream().distinct().collect(Collectors.toList());
			}

			List<InventoryVoucherHeader> unattendedInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> unuttendeddistinctElements = new ArrayList<>();
			if (unattendedReferences.size() > 0) {
				unattendedInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(unattendedReferences);
				unuttendeddistinctElements = unattendedInventoryHeaders.stream().distinct()
						.collect(Collectors.toList());
			}

			List<InventoryVoucherHeader> updatedList = new ArrayList<>();

			successCount = 0;
			for (InventoryVoucherHeader ivh : successdistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpReferenceNumber(a.getId());
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
							updatedList.add(newIvh);
							successCount++;
						});
			}

			log.debug("updated " + successCount + " to Completed");

			failedCount = 0;
			for (InventoryVoucherHeader ivh : faileddistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.FAILED);
							updatedList.add(newIvh);
							failedCount++;
						});
			}

			if (failedCount > 0) {
				log.debug("updated " + failedCount + " to Failed");
			}

			unattendedCount = 0;
			for (InventoryVoucherHeader ivh : unuttendeddistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.PENDING);
							updatedList.add(newIvh);
							unattendedCount++;
						});
			}
			inventoryVoucherHeaderRepository.save(updatedList);

			if (unattendedCount > 0) {
				log.debug("updated " + unattendedCount + " to Pending");
			}
		}
	}
}
