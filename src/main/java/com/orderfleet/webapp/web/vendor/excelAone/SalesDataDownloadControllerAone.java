package com.orderfleet.webapp.web.vendor.excelAone;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
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
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptVendorDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderItemDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderVenderDTO;
import com.orderfleet.webapp.web.rest.dto.VatLedgerDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.NewlyAddedLedgerDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.SalesOrderExcelDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesOrderPid;
import com.orderfleet.webapp.web.vendor.integre.dto.SalesPidDTO;

@RestController
@RequestMapping(value = "/api/excel/aone/v1")
public class SalesDataDownloadControllerAone {

	private final Logger log = LoggerFactory.getLogger(SalesDataDownloadControllerAone.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
	private AccountProfileRepository accountProfileRepository;

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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_184" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="getting primary sales for excel";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getPrimarySalesForExcel(company.getId(), documentIds);
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_183" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="getting primary sales order for excel";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getPrimarySalesOrderForExcel(company.getId(), documentIds);
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
		log.debug("REST request to download primary sales orders : " + inventoryVoucherHeaders.size());
		salesOrderDTOs = getInventoryVoucherList(inventoryVoucherHeaders);

		return salesOrderDTOs;

	}

	@RequestMapping(value = "/get-service-data.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderExcelDTO> getServiceDataJSON(
			@RequestParam(value = "voucherType", required = true) VoucherType voucherType) throws URISyntaxException {
		log.debug("REST request to download primary sales orders : {}");
		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();

		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(voucherType, company.getId());
		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			return salesOrderDTOs;
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_178" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="Getting Primary secondary sales order for excel";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.getPrimarySecondarySalesOrderForExcel(company.getId(), documentIds);
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
		log.debug("REST request to download primary sales orders : " + inventoryVoucherHeaders.size());
		salesOrderDTOs = getServiceDataList(inventoryVoucherHeaders);

		return salesOrderDTOs;

	}

	@RequestMapping(value = "/get-newly-added-ledger.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<NewlyAddedLedgerDTO> getNewlyAddedLedgerJSON() throws URISyntaxException {
		log.debug("REST request to download newly added  sales ledgers : {}");
		List<NewlyAddedLedgerDTO> newlyAddedLedgerDTOs = new ArrayList<>();

		List<AccountProfile> accountProfiles = accountProfileRepository
				.findAllByCompanyAndDataSourceTypeAndCreatedDateAndAccountStatus(DataSourceType.MOBILE,
						AccountStatus.Unverified);

		newlyAddedLedgerDTOs = convertAccountProfilesToNewlyAddedLedgerDTO(accountProfiles);

		return newlyAddedLedgerDTOs;

	}

	private List<NewlyAddedLedgerDTO> convertAccountProfilesToNewlyAddedLedgerDTO(
			List<AccountProfile> accountProfiles) {
		List<NewlyAddedLedgerDTO> newlyAddedLedgerDTOs = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (AccountProfile accountProfile : accountProfiles) {
			NewlyAddedLedgerDTO newlyAddedLedgerDTO = new NewlyAddedLedgerDTO();

			newlyAddedLedgerDTO.setAccountStatus(accountProfile.getAccountStatus().toString());
			newlyAddedLedgerDTO.setAddress(accountProfile.getAddress());
			newlyAddedLedgerDTO.setAlias(accountProfile.getAlias());
			newlyAddedLedgerDTO.setCity(accountProfile.getCity());
			newlyAddedLedgerDTO.setCreatedBy(accountProfile.getUser().getFirstName());
			newlyAddedLedgerDTO.setCreatedDate(accountProfile.getCreatedDate().format(formatter));
			newlyAddedLedgerDTO.setDescription(accountProfile.getDescription());
			newlyAddedLedgerDTO.setEmail1(accountProfile.getEmail1());
			newlyAddedLedgerDTO.setLocation(accountProfile.getLocation());
			newlyAddedLedgerDTO.setName(accountProfile.getName());
			newlyAddedLedgerDTO.setPhone1(accountProfile.getPhone1());
			newlyAddedLedgerDTO.setPhone2(accountProfile.getPhone2());
			newlyAddedLedgerDTO.setPid(accountProfile.getPid());
			newlyAddedLedgerDTO.setPin(accountProfile.getPin());
			newlyAddedLedgerDTO.setTinNo(accountProfile.getTinNo());

			newlyAddedLedgerDTOs.add(newlyAddedLedgerDTO);
		}

		return newlyAddedLedgerDTOs;
	}

	private List<SalesOrderExcelDTO> getInventoryVoucherList(List<Object[]> inventoryVoucherHeaders) {

		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		double taxAmount = 0.0;

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderExcelDTO salesOrderDTO = new SalesOrderExcelDTO();
			boolean unregisteredCustomer = false;

			String pattern = "dd-MMM-yy";
			DateFormat df = new SimpleDateFormat(pattern);
			String dateAsString = df.format(obj[1]);

			salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
			salesOrderDTO.setCustomerCode(obj[2] != null ? obj[2].toString() : "");

			Optional<AccountProfile> apOp = accountProfileRepository
					.findByCompanyIdAndAliasIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), obj[2].toString());

			if (apOp.isPresent()) {
				if (apOp.get().getTinNo() != null) {
					if (apOp.get().getTinNo().equalsIgnoreCase("")) {
						unregisteredCustomer = true;
					}
				} else {
					unregisteredCustomer = true;
				}
			}
			salesOrderDTO.setItemCode(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setQuantity(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
			salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);
			salesOrderDTO.setDiscPer(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setTaxPer(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);

			salesOrderDTO.setEmployeeCode(obj[16] != null ? obj[16].toString() : "");
			salesOrderDTO.setCaseValue(0.0);

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

			double compDisAmount = 0.0;
			if (obj[15] != null) {
				compDisAmount = Double.parseDouble(obj[15].toString());
				salesOrderDTO.setCompDiscAmt(Double.parseDouble(obj[15].toString()));
			} else {
				salesOrderDTO.setCompDiscAmt(0.0);

			}

			double amountValue = qty * rate;
			double discountValue = amountValue * dis / 100;
			double amountWithoutTax = (amountValue - discountValue) - compDisAmount;

			// tax calculation

			double taxValue = amountWithoutTax * taxPer / 100;
			double totalAmount = amountWithoutTax + taxValue;

			// double gstRate = taxValue - discountValue;
			// taxAmount = taxAmount + tax;

			// double taxableAmount = taxableAmount + amountWithoutTax;

			DecimalFormat round = new DecimalFormat(".##");
			if (unregisteredCustomer) {
				if (taxPer > 5) {
					double amount = Double.parseDouble(round.format(amountWithoutTax * 0.01));
					salesOrderDTO.setKfcAmt(amount);
					salesOrderDTO.setKfcPer(1.0);
					totalAmount += amount;
				}
			}
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
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);


			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
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
			log.debug("updated " + updated + " to PROCESSING");
		}
		return salesOrderDTOs;
	}

	private List<SalesOrderExcelDTO> getServiceDataList(List<Object[]> inventoryVoucherHeaders) {

		List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		for (Object[] obj : inventoryVoucherHeaders) {
			SalesOrderExcelDTO salesOrderDTO = new SalesOrderExcelDTO();

			String pattern = "dd-MM-yyyy";
			DateFormat df = new SimpleDateFormat(pattern);

			salesOrderDTO.setBillNo(obj[0].toString());
			String dateAsString = df.format(obj[1]);
			salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
			salesOrderDTO.setCustomerCode(obj[2] != null ? obj[2].toString() : "");
			salesOrderDTO.setItemCode(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setQuantity(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
			salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);
			salesOrderDTO.setDiscPer(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setTaxPer(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
			salesOrderDTO.setTotal(obj[8] != null ? Double.parseDouble(obj[8].toString()) : 0.0);
			salesOrderDTO.setInventoryPid(obj[9] != null ? obj[9].toString() : "");
			salesOrderDTO.setFreeQuantity(Double.parseDouble(obj[12] != null ? obj[12].toString() : "0.0"));
			salesOrderDTO.setCustomerName(obj[13] != null ? obj[13].toString() : "");
			salesOrderDTO.setMrp(Double.parseDouble(obj[14] != null ? obj[14].toString() : "0.0"));
			salesOrderDTO.setDiscPrice(Double.parseDouble(obj[15] != null ? obj[15].toString() : "0.0"));
			salesOrderDTO.setRemarks(obj[17] != null ? obj[17].toString() : "");

			inventoryHeaderPid.add(obj[9] != null ? obj[9].toString() : "");

			salesOrderDTOs.add(salesOrderDTO);

		}

		if (!salesOrderDTOs.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
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
			log.debug("updated " + updated + " to PROCESSING");
		}
		return salesOrderDTOs;
	}

	@PostMapping("/update-order-status.json")
	@Timed
	@Transactional
	public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> inventoryVoucherHeaderPids) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		log.debug("REST request to update Inventory Voucher Header Status (" + company.getLegalName() + ") : {}",
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


			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
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
			log.debug("updated " + updated + " to Completed");
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/update-newly-added-ledger-status")
	@Timed
	@Transactional
	public ResponseEntity<String> updateNewlyAddedLedger(@RequestBody List<String> accountProfilePids) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		log.debug("REST request to update Inventory Voucher Header Status (" + company.getLegalName() + ") : {}",
				accountProfilePids.size());

		if (!accountProfilePids.isEmpty()) {
			int updated = accountProfileRepository.updateAccountProfileStatusUsingPid(AccountStatus.Verified,
					accountProfilePids);
			log.debug("updated " + updated + " to Completed");
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
