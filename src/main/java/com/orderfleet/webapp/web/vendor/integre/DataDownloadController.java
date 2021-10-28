package com.orderfleet.webapp.web.vendor.integre;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.web.rest.dto.ReceiptVendorDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderVenderDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesOrderPid;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesPidDTO;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
public class DataDownloadController {

	private final Logger log = LoggerFactory.getLogger(DataDownloadController.class);

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ReceiptVendorDTO>> getReceiptsJson(@RequestHeader("X-COMPANY") String companyId)
			throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		List<ReceiptVendorDTO> receiptDTOs = new ArrayList<>();
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if (snrichPartnerCompany == null) {
			return new ResponseEntity<List<ReceiptVendorDTO>>(receiptDTOs,HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();

		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyIdAndStatusOrderByCreatedDateDesc(company.getId());
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			if (accountingVoucherHeader.getAccountProfile().getAlias() == null
					|| accountingVoucherHeader.getAccountProfile().getAlias().equals("")) {
				return new ResponseEntity<List<ReceiptVendorDTO>>(Collections.emptyList(),HttpStatus.EXPECTATION_FAILED);
			}
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
					ReceiptVendorDTO receiptDTO = new ReceiptVendorDTO(accountingVoucherDetail);
					receiptDTOs.add(receiptDTO);
				} else {
					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
							.getAccountingVoucherAllocations()) {
						ReceiptVendorDTO receiptDTO = new ReceiptVendorDTO(accountingVoucherAllocation);
						receiptDTOs.add(receiptDTO);
					}
				}
			}
		}
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(
				TallyDownloadStatus.PROCESSING, company.getId(),
				receiptDTOs.stream().map(so -> so.getAccountingVoucherHeaderPid()).collect(Collectors.toList()));
		return new ResponseEntity<List<ReceiptVendorDTO>>(receiptDTOs,HttpStatus.OK);
		
	}

	@RequestMapping(value = "/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<List<SalesOrderVenderDTO>> getSalesOrderJSON(@RequestHeader("X-COMPANY") String companyId)
			throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");
		List<SalesOrderVenderDTO> salesOrderDTOs = new ArrayList<>();
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if (snrichPartnerCompany == null) {
			return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.EXPECTATION_FAILED);
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());
		String id="INV_QUERY_170";
		String description="Listing sales orders for vendor";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");




		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForVendor(company.getId(), documentIds);

		log.debug("REST request to download sales orders : " + inventoryVoucherHeaders.size());
		int salesSno = 1;

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderVenderDTO salesOrderDTO = new SalesOrderVenderDTO();
			salesOrderDTO.setDate(obj[0] != null ? obj[0].toString() : "");
			if (obj[1] == null || obj[1].toString().equals("")) {
				return new ResponseEntity<List<SalesOrderVenderDTO>>(Collections.emptyList(),HttpStatus.EXPECTATION_FAILED);
			}
			salesOrderDTO.setCustomerId(obj[1] != null ? obj[1].toString() : "");
			salesOrderDTO.setExecutiveId(obj[2] != null ? obj[2].toString() : "");
			salesOrderDTO.setItemId(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setQty(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
			salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);

			salesOrderDTO.setAmt(salesOrderDTO.getQty() * salesOrderDTO.getRate());

			salesOrderDTO.setDiscPer(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setDiscAmt(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
			salesOrderDTO.setNetAmt(obj[8] != null ? Double.parseDouble(obj[8].toString()) : 0.0);
			salesOrderDTO.setAddnAmount(obj[9] != null ? Double.parseDouble(obj[9].toString()) : 0.0);
			salesOrderDTO.setBillAmount(obj[10] != null ? Double.parseDouble(obj[10].toString()) : 0.0);
			salesOrderDTO.setDednAmount(0.0);
			salesOrderDTO.setAmountRecd(0.0);
			salesOrderDTO.setInventoryPid(obj[11] != null ? obj[11].toString() : "");
			salesOrderDTO.setSoNo(salesSno + "");
			salesOrderDTOs.add(salesOrderDTO);
			salesSno++;

		}
		String id1="INV_QUERY_181";
		String description1="Updating inv Vou header TallydownloadStatus using pid and Companyid";
		log.info("{ Query Id:- "+id1+" Query Description:- "+description1+" }");
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
				TallyDownloadStatus.PROCESSING, company.getId(),
				salesOrderDTOs.stream().map(so -> so.getInventoryPid()).collect(Collectors.toList()));
		return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.OK);
	}

	@RequestMapping(value = "/get-sales.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<List<SalesOrderVenderDTO>> getVanSalesJSON(@RequestHeader("X-COMPANY") String companyId)
			throws URISyntaxException {
		log.debug("REST request to download sales  : {}");
		List<SalesOrderVenderDTO> salesOrderDTOs = new ArrayList<>();
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if (snrichPartnerCompany == null) {
			return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.EXPECTATION_FAILED);
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());
		String id="INV_QUERY_170";
		String description="Listing sales orders for vendor";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForVendor(company.getId(), documentIds);

		log.debug("REST request to download sales  : " + inventoryVoucherHeaders.size());
		int salesSno = 1;

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderVenderDTO salesOrderDTO = new SalesOrderVenderDTO();
			salesOrderDTO.setDate(obj[0] != null ? obj[0].toString() : "");
			if (obj[1] == null || obj[1].toString().equals("")) {
				return new ResponseEntity<List<SalesOrderVenderDTO>>( Collections.emptyList(),HttpStatus.EXPECTATION_FAILED);
			}
			salesOrderDTO.setCustomerId(obj[1] != null ? obj[1].toString() : "");
			salesOrderDTO.setExecutiveId(obj[2] != null ? obj[2].toString() : "");
			salesOrderDTO.setItemId(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setQty(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
			salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);

			salesOrderDTO.setAmt(salesOrderDTO.getQty() * salesOrderDTO.getRate());

			salesOrderDTO.setDiscPer(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setDiscAmt(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
			salesOrderDTO.setNetAmt(obj[8] != null ? Double.parseDouble(obj[8].toString()) : 0.0);
			salesOrderDTO.setAddnAmount(obj[9] != null ? Double.parseDouble(obj[9].toString()) : 0.0);
			salesOrderDTO.setBillAmount(obj[10] != null ? Double.parseDouble(obj[10].toString()) : 0.0);
			salesOrderDTO.setDednAmount(0.0);
			salesOrderDTO.setAmountRecd(0.0);
			salesOrderDTO.setInventoryPid(obj[11] != null ? obj[11].toString() : "");
			salesOrderDTO.setSoNo(salesSno + "");
			salesOrderDTOs.add(salesOrderDTO);
			salesSno++;

		}
		String id1="INV_QUERY_181";
		String description1="Updating inv Vou header TallydownloadStatus using pid and Companyid";
		log.info("{ Query Id:- "+id1+" Query Description:- "+description1+" }");
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
				TallyDownloadStatus.PROCESSING, company.getId(),
				salesOrderDTOs.stream().map(so -> so.getInventoryPid()).collect(Collectors.toList()));
		return new ResponseEntity<List<SalesOrderVenderDTO>>(salesOrderDTOs,HttpStatus.OK);
	}

	@PostMapping("/update-order-status.json")
	public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> inventoryVoucherHeaderPids,
			@RequestHeader("X-COMPANY") String companyId) {
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if (snrichPartnerCompany == null) {
			return new ResponseEntity<String>("Failed - Company doesnot exist", HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();
		String id="INV_QUERY_181";
		String description="Updating inv Vou header TallydownloadStatus using pid and Companyid";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
				TallyDownloadStatus.COMPLETED, company.getId(), inventoryVoucherHeaderPids);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

	@PostMapping("/update-receipt-status.json")
	public ResponseEntity<String> updateReceiptStatus(@RequestBody List<String> accountingVoucherHeaderPids,
			@RequestHeader("X-COMPANY") String companyId) {
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if (snrichPartnerCompany == null) {
			return new ResponseEntity<String>("Failed - Company doesnot exist", HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(
				TallyDownloadStatus.COMPLETED, company.getId(), accountingVoucherHeaderPids);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}


}
