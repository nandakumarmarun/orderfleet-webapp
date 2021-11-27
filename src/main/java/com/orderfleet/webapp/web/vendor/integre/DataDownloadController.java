package com.orderfleet.webapp.web.vendor.integre;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ReceiptVendorDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderVenderDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesOrderPid;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesPidDTO;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
public class DataDownloadController {

	private final Logger log = LoggerFactory.getLogger(DataDownloadController.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_141" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by companyId ,status and order by created date";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyIdAndStatusOrderByCreatedDateDesc(company.getId());
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
		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACC_QUERY_147" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="updating the accVoucher tally download status using pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(
				TallyDownloadStatus.PROCESSING, company.getId(),
				receiptDTOs.stream().map(so -> so.getAccountingVoucherHeaderPid()).collect(Collectors.toList()));
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_170" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get sales order for vendor";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForVendor(company.getId(), documentIds);
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
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "INV_QUERY_181" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="update Iv Header TallyDownload Status Using Pid AndCompanyId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
				TallyDownloadStatus.PROCESSING, company.getId(),
				salesOrderDTOs.stream().map(so -> so.getInventoryPid()).collect(Collectors.toList()));
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
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_170" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get sales order for vendor";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForVendor(company.getId(), documentIds);
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
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "INV_QUERY_181" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="update Iv Header TallyDownload Status Using Pid AndCompanyId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
				TallyDownloadStatus.PROCESSING, company.getId(),
				salesOrderDTOs.stream().map(so -> so.getInventoryPid()).collect(Collectors.toList()));
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_147" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="updating the accVoucher tally download status using pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(
				TallyDownloadStatus.COMPLETED, company.getId(), accountingVoucherHeaderPids);
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
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}


}
