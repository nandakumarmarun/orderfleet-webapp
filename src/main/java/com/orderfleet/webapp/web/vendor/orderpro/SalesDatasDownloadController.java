package com.orderfleet.webapp.web.vendor.orderpro;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesOrderPid;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesPidDTO;
import com.orderfleet.webapp.web.vendor.orderpro.dto.SalesOrderExcelDTO;

@RestController
@RequestMapping(value = "/api/orderpro/v1")
public class SalesDatasDownloadController {

	private final Logger log = LoggerFactory.getLogger(SalesDatasDownloadController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;

	@Inject
	private CompanyRepository companyRepository;

	@RequestMapping(value = "/get-sales-orders-excel.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderExcelDTO> getSalesOrderJSON() throws URISyntaxException {
		log.debug("REST request to download sales orders : {} ");
		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_177" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="Getting sales order for excel";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForExcel(company.getId());
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

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderExcelDTO salesOrderDTO = new SalesOrderExcelDTO();

			String pattern = "dd-MMM-yyyy";
			DateFormat df = new SimpleDateFormat(pattern);
			String dateAsString = df.format(obj[1]);

			salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
			salesOrderDTO.setCustomerCode(obj[2] != null ? obj[2].toString() : "");
			salesOrderDTO.setItemCode(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setQuantity(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
			salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);
			salesOrderDTO.setDiscPer(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setTaxPer(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
			/*
			 * salesOrderDTO.setTotal(obj[8] != null ? Double.parseDouble(obj[8].toString())
			 * : 0.0);
			 */

			double qty = Double.parseDouble(obj[4].toString());
			double rate = Double.parseDouble(obj[5].toString());
			double dis = Double.parseDouble(obj[6].toString());
			double taxPer = Double.parseDouble(obj[7].toString());

			double amountValue = qty * rate;
			double discountValue = amountValue * dis / 100;
			double amountWithoutTax = amountValue - discountValue;

			// tax calculation

			double taxValue = amountWithoutTax * taxPer / 100;
			double totalAmount = amountWithoutTax + taxValue;
			// double gstRate = taxValue - discountValue;
			// taxAmount = taxAmount + tax;

			// double taxableAmount = taxableAmount + amountWithoutTax;

			DecimalFormat round = new DecimalFormat(".##");

			double cgst = Double.parseDouble(round.format(taxValue / 2));
			double sgst = Double.parseDouble(round.format(taxValue / 2));

			salesOrderDTO.setTotal(Double.parseDouble(round.format(totalAmount)));
			salesOrderDTO.setDiscPrice(Double.parseDouble(round.format(discountValue)));
			salesOrderDTO.setCGSTAmt(cgst);
			salesOrderDTO.setSGSTAmt(sgst);
			salesOrderDTO.setInventoryPid(obj[9] != null ? obj[9].toString() : "");
			salesOrderDTO.setEmployeeName(obj[10] != null ? obj[10].toString() : "");

			salesOrderDTO.setBillNo(obj[0].toString());
			inventoryHeaderPid.add(obj[9] != null ? obj[9].toString() : "");

			salesOrderDTO.setRefDocNo(obj[11] != null ? obj[11].toString() : "0");

			double freeQuantity = Double.parseDouble(obj[12].toString());

			salesOrderDTO.setFreeQuantity(freeQuantity);

			salesOrderDTOs.add(salesOrderDTO);

		}

		if (!salesOrderDTOs.isEmpty()) {
			   DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
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
			log.debug("updated " + updated + " to PROCESSING");
		}
		return salesOrderDTOs;
	}

	@PostMapping("/update-order-status.json")
	@Timed
	@Transactional
	public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> inventoryVoucherHeaderPids) {
		// Company company
		// =companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// inventoryVoucherHeaderRepository
		// .updateAllInventoryVoucherHeaderStatusUsingPid(company.getId(),
		// inventoryVoucherHeaderPids);
		// inventoryVoucherHeaderRepository.
		// updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.PROCESSING,
		// inventoryVoucherHeaderPids);
		// return new ResponseEntity<String>("Success",HttpStatus.OK);
		log.debug("REST request to update Inventory Voucher Header Status (aquatech) : {}",
				inventoryVoucherHeaderPids.size());

		if (!inventoryVoucherHeaderPids.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.COMPLETED, inventoryVoucherHeaderPids);
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

		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/update-receipt-status.json")
	@Timed
	@Transactional
	public ResponseEntity<String> updateReceiptStatus(@RequestBody List<String> accountingVoucherHeaderPids,
			@RequestHeader("X-COMPANY") String companyId) {
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_143" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="updating the account voucher header by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderStatusUsingPid(company.getId(),
				accountingVoucherHeaderPids);
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

	// getSalesOrderPid & getFirstSalesOrderPid
	// Used for testing purpose
	// TDL testing with tally

	@RequestMapping(value = "/get-sales-orders-pid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesPidDTO> getSalesOrderPid(@RequestHeader("X-COMPANY") String companyId) throws URISyntaxException {
		log.debug("REST request to download sales orders pid :");
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_168" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get Pid by status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<SalesPidDTO> inventoryVoucherHeaderPids = inventoryVoucherHeaderRepository.findPidByStatus(company.getId())
				.stream().map(pid -> new SalesPidDTO(pid)).collect(Collectors.toList());
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
		log.debug("REST request to download sales order pids : " + inventoryVoucherHeaderPids.size());
		return inventoryVoucherHeaderPids;
	}

	@RequestMapping(value = "/get-sales-orders-pid-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public SalesOrderPid getSalesOrderPidList(@RequestHeader("X-COMPANY") String companyId) throws URISyntaxException {
		log.debug("REST request to download sales orders pid :");
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		SalesOrderPid salesOrderPid = new SalesOrderPid();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_168" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get Pid by status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		salesOrderPid.setSalesPid(inventoryVoucherHeaderRepository.findPidByStatus(company.getId()));
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
		log.debug("REST request to download sales order pids : " + salesOrderPid);
		return salesOrderPid;
	}

	@RequestMapping(value = "/get-sales-orders-pid-first", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public SalesPidDTO getFirstSalesOrderPid(@RequestHeader("X-COMPANY") String companyId) throws URISyntaxException {
		log.debug("REST request to download sales orders pid :");
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_168" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get Pid by status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<SalesPidDTO> inventoryVoucherHeaderPids = inventoryVoucherHeaderRepository.findPidByStatus(company.getId())
				.stream().map(pid -> new SalesPidDTO(pid)).collect(Collectors.toList());
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
	

		log.debug("REST request to download sales order pids : " + inventoryVoucherHeaderPids.size());
		if (inventoryVoucherHeaderPids.size() == 0) {
			return new SalesPidDTO("TestPid-nosalesOrderPresent");
		} else {
			return inventoryVoucherHeaderPids.get(0);
		}

	}
}
