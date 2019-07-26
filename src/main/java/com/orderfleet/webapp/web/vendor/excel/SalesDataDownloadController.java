package com.orderfleet.webapp.web.vendor.excel;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptVendorDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderItemDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderVenderDTO;
import com.orderfleet.webapp.web.rest.dto.VatLedgerDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.SalesOrderExcelDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesOrderPid;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesPidDTO;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class SalesDataDownloadController {

	private final Logger log = LoggerFactory.getLogger(SalesDataDownloadController.class);

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@RequestMapping(value = "/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderExcelDTO> getSalesOrderJSON() throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");
		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getSalesOrderForExcel(company.getId());

		log.debug("REST request to download sales orders : " + inventoryVoucherHeaders.size());

		salesOrderDTOs = getInventoryVoucherList(inventoryVoucherHeaders);

		return salesOrderDTOs;
	}

	@RequestMapping(value = "/get-primary-sales.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderExcelDTO> getPrimarySalesJSON() throws URISyntaxException {
		log.debug("REST request to download primary sales  : {}");
		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return salesOrderDTOs;
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getPrimarySalesForExcel(company.getId(), documentIds);

		log.debug("REST request to download primary sales : " + inventoryVoucherHeaders.size());

		salesOrderDTOs = getInventoryVoucherList(inventoryVoucherHeaders);

		return salesOrderDTOs;
	}

	@RequestMapping(value = "/get-primary-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderExcelDTO> getPrimarySalesOrderJSON() throws URISyntaxException {
		log.debug("REST request to download primary sales orders : {}");
		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return salesOrderDTOs;
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getPrimarySalesOrderForExcel(company.getId(), documentIds);

		log.debug("REST request to download primary sales orders : " + inventoryVoucherHeaders.size());
		salesOrderDTOs = getInventoryVoucherList(inventoryVoucherHeaders);

		return salesOrderDTOs;

	}

	private List<SalesOrderExcelDTO> getInventoryVoucherList(List<Object[]> inventoryVoucherHeaders) {

		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		double taxAmount = 0.0;

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderExcelDTO salesOrderDTO = new SalesOrderExcelDTO();

			String pattern = "dd-MMM-yy";
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

			double qty = Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.0");
			double rate = Double.parseDouble(obj[5] != null ? obj[5].toString() : "0.0");
			double dis = Double.parseDouble(obj[6] != null ? obj[6].toString() : "0.0");
			double taxPer = Double.parseDouble(obj[7] != null ? obj[7].toString() : "0.0");
			double freeQty = Double.parseDouble(obj[12] != null ? obj[12].toString() : "0.0");
			double mrp = Double.parseDouble(obj[14] != null ? obj[14].toString() : "0.0");

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

			String uniquString = RandomUtil.generateServerDocumentNo().substring(0,
					RandomUtil.generateServerDocumentNo().length() - 10);

			salesOrderDTO.setBillNo(obj[0].toString());
			salesOrderDTO.setFreeQuantity(freeQty);
			salesOrderDTO.setMrp(mrp);
			salesOrderDTO.setCustomerName(obj[13] != null ? obj[13].toString() : "");
			inventoryHeaderPid.add(obj[9] != null ? obj[9].toString() : "");
			System.out.println("================================================================");
			System.out.println(salesOrderDTO.toString());
			System.out.println("================================================================");
			salesOrderDTOs.add(salesOrderDTO);

		}

		if (!salesOrderDTOs.isEmpty()) {
			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
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
			inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.COMPLETED, inventoryVoucherHeaderPids);
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
		accountingVoucherHeaderRepository.updateAccountingVoucherHeaderStatusUsingPid(company.getId(),
				accountingVoucherHeaderPids);
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
		List<SalesPidDTO> inventoryVoucherHeaderPids = inventoryVoucherHeaderRepository.findPidByStatus(company.getId())
				.stream().map(pid -> new SalesPidDTO(pid)).collect(Collectors.toList());

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
		salesOrderPid.setSalesPid(inventoryVoucherHeaderRepository.findPidByStatus(company.getId()));

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
		List<SalesPidDTO> inventoryVoucherHeaderPids = inventoryVoucherHeaderRepository.findPidByStatus(company.getId())
				.stream().map(pid -> new SalesPidDTO(pid)).collect(Collectors.toList());

		log.debug("REST request to download sales order pids : " + inventoryVoucherHeaderPids.size());
		if (inventoryVoucherHeaderPids.size() == 0) {
			return new SalesPidDTO("TestPid-nosalesOrderPresent");
		} else {
			return inventoryVoucherHeaderPids.get(0);
		}

	}
}
