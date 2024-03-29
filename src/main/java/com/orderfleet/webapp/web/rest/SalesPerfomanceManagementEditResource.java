package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.crypto.Data;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.InventoryVoucherDetailService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.UpdateIvhdetailQnty;
import com.orderfleet.webapp.web.rest.dto.UpdateQntyIvhdDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderItemDetailsSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderMasterSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderResponseDataSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.service.SendSalesOrderSapService;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.SendTransactionSapPraveshService;

import net.bytebuddy.asm.Advice.Return;

@Controller
@RequestMapping("/web")
public class SalesPerfomanceManagementEditResource {

	private final Logger log = LoggerFactory.getLogger(SalesPerfomanceManagementEditResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private InventoryVoucherDetailService inventoryVoucherDetailService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private SendSalesOrderSapService sendSalesOrderSapService;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private SendTransactionSapPraveshService sendTransactionSapPraveshService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	/**
	 * GET /primary-sales-performance : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/sales-order-draft", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllInventoryVouchers(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of inventory vouchers");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
			userIds.add(currentUserId);
			Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserIdIn(userIds);
//			List<Object[]> accountPidNames = locationAccountProfileRepository
//			.findAccountProfilesByLocationIdIn(locationIds);
//	int size = accountPidNames.size();
//	List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>(size);
//	for (int i = 0; i < size; i++) {
//		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();
//		accountProfileDTO.setPid(accountPidNames.get(i)[0].toString());
//		accountProfileDTO.setName(accountPidNames.get(i)[1].toString());
//		accountProfileDTOs.add(accountProfileDTO);
//	}

			Set<BigInteger> apIds = locationAccountProfileRepository
					.findAccountProfileIdsByUserLocationsOrderByAccountProfilesName(locationIds);

			Set<Long> accountProfileIds = new HashSet<>();

			for (BigInteger apId : apIds) {
				accountProfileIds.add(apId.longValue());
			}

			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);

			// remove duplicates
			List<AccountProfile> result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());

			List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
					.accountProfilesToAccountProfileDTOs(result);
			model.addAttribute("accounts", accountProfileDTOs);
		}
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyIdandOne());

		boolean pdfDownloadStatus = false;
		Optional<CompanyConfiguration> opCompanyConfigurationPdf = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		if (opCompanyConfigurationPdf.isPresent()) {

			if (opCompanyConfigurationPdf.get().getValue().equals("true")) {
				pdfDownloadStatus = true;
			} else {
				pdfDownloadStatus = false;
			}
		}
		model.addAttribute("pdfDownloadStatus", pdfDownloadStatus);

		boolean sendTransactionsSapPravesh = false;
		Optional<CompanyConfiguration> opCompanyConfigurationSapPravesh = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(),
						CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);
		if (opCompanyConfigurationSapPravesh.isPresent()) {

			if (opCompanyConfigurationSapPravesh.get().getValue().equals("true")) {
				sendTransactionsSapPravesh = true;
			} else {
				sendTransactionsSapPravesh = false;
			}
		}
		model.addAttribute("sendTransactionsSapPravesh", sendTransactionsSapPravesh);

		return "company/salesPerfomanceManagementEdit";
	}

	@RequestMapping(value = "/sales-order-draft/sendTransactionsSapPravesh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> sendTransactionsSapPravesh() throws MessagingException {

		log.info("sendSalesOrderSap()-----");

		sendTransactionSapPraveshService.sendSalesOrder();

		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/sales-order-draft/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		Optional<InventoryVoucherHeaderDTO> optionalInventoryVoucherHeaderDTO = inventoryVoucherService
				.findOneByPid(pid);

		List<InventoryVoucherHeader> ivhIsalesOrderCormirm = inventoryVoucherHeaderRepository
				.findInventoryVoucherHeaderByDocumennumber(
						optionalInventoryVoucherHeaderDTO.get().getDocumentNumberLocal());
		List<InventoryVoucherHeader> ivhINsalesOrderCormirm = new ArrayList<>();
		List<InventoryVoucherDetail> ivhdetails = new ArrayList<>();
		if (!ivhIsalesOrderCormirm.isEmpty()) {
			Set<String> invhIds = new HashSet<>();
			ivhIsalesOrderCormirm.forEach(data -> invhIds.add(data.getPid()));
			ivhdetails = inventoryVoucherDetailRepository.findAllByInventoryVoucherHeaderPidIn(invhIds);
			ivhINsalesOrderCormirm = getIvhConfirm(ivhIsalesOrderCormirm, ivhdetails);
		}
		Optional<CompanyConfiguration> opCompanyConfigurationSendSalesOrderSap = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_SAP);

		if (optionalInventoryVoucherHeaderDTO.isPresent()) {
			InventoryVoucherHeaderDTO inventoryVoucherDTO = optionalInventoryVoucherHeaderDTO.get();

			Optional<CompanyConfiguration> opCompanyConfigurationSalesEdit = companyConfigurationRepository
					.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_EDIT_ENABLED);

			Double ivTotalVolume = inventoryVoucherDTO.getInventoryVoucherDetails().stream()
					.collect(Collectors.summingDouble(ivd -> {
						if (ivd.getProductUnitQty() != null) {
							return (ivd.getProductUnitQty() * ivd.getQuantity());
						} else {
							return 0;
						}
					}));

			// checking tax rate in product group if product does not have tax rate
			for (InventoryVoucherDetailDTO ivd : inventoryVoucherDTO.getInventoryVoucherDetails()) {

				if (opCompanyConfigurationSendSalesOrderSap.isPresent()) {
					ivd.setItemtype(ivd.getItemtype() != null ? " (" + ivd.getItemtype() + ")" : " (MTS)");
				} else {
					ivd.setItemtype(ivd.getItemtype() != null ? " (" + ivd.getItemtype() + ")" : "");
				}
				if (opCompanyConfigurationSalesEdit.isPresent()) {
					if (opCompanyConfigurationSalesEdit.get().getValue().equals("true")) {
						ivd.setEditOrder(true);
					}
				}
				if (ivd.getTaxPercentage() == 0) {
					ProductGroup pg = productGroupProductRepository.findProductGroupByProductPid(ivd.getProductPid())
							.get(0);
					if (pg != null) {
						if (pg.getTaxRate() != 0) {
							ivd.setTaxPercentage(pg.getTaxRate());
						}
					}
				}
			}

			if (!ivhINsalesOrderCormirm.isEmpty()) {

				List<InventoryVoucherDetailDTO> totalQntyDif = new ArrayList<>();

				for (InventoryVoucherDetailDTO ivhd : inventoryVoucherDTO.getInventoryVoucherDetails()) {
					double sum = ivhdetails.stream()
							.filter(data1 -> ivhd.getProductPid().equals(data1.getProduct().getPid()))
//									&& data1.getQuantity() != ivhd.getQuantity()
							.mapToDouble(data2 -> data2.getQuantity()).sum();
					ivhd.setUpdatedQty(sum >= ivhd.getQuantity() ? ivhd.getQuantity() : sum);
					totalQntyDif.add(ivhd);
					totalQntyDif.forEach(data -> {
						if (ivhd.getProductPid().equals(data.getProductPid())) {
							double quantityDiff = Math.abs(ivhd.getQuantity() - data.getUpdatedQty());
							ivhd.setQntyDiff(quantityDiff);
						}
					});	
				}
			} else {
				inventoryVoucherDTO.getInventoryVoucherDetails().forEach(ivhd -> {
					ivhd.setQntyDiff(ivhd.getQuantity());
				});
			}

			inventoryVoucherDTO.setDocumentVolume(ivTotalVolume);

			return new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/sales-order-draft/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<SalesPerformanceDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("tallyDownloadStatus") String tallyDownloadStatus,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam("documentPids") List<String> documentPids, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		if (documentPids.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(SalesPerfomanceManagementEditResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(SalesPerfomanceManagementEditResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(SalesPerfomanceManagementEditResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(SalesPerfomanceManagementEditResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		}
		List<SalesPerformanceDTO> salesPerformanceDTOs = getFilterData(employeePids, documentPids, tallyDownloadStatus,
				accountPid, fDate, tDate);
		return new ResponseEntity<>(salesPerformanceDTOs, HttpStatus.OK);
	}

	private List<SalesPerformanceDTO> getFilterData(List<String> employeePids, List<String> documentPids,
			String tallyDownloadStatus, String accountPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<TallyDownloadStatus> tallyStatus = null;

		switch (tallyDownloadStatus) {
		case "PENDING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PENDING);
			break;
		case "PROCESSING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PROCESSING);
			break;
		case "COMPLETED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED);
			break;
		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING);
			break;
		}

		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_157" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all ByUserIdIn And DocumentPidIn AndTallyDownloadStatusDateBetween";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(userIds,
							documentPids, tallyStatus, fromDate, toDate);
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
		} else {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_160" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all By UserIdIn and AccountPidIn and DocumentPidIn and TallyDownloadStatusDateBetween";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
							userIds, accountPid, documentPids, tallyStatus, fromDate, toDate);
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
		if (inventoryVouchers.isEmpty()) {
			return Collections.emptyList();
		} else {
			return createSalesPerformanceDTO(inventoryVouchers);
		}
	}

	private List<SalesPerformanceDTO> createSalesPerformanceDTO(List<Object[]> inventoryVouchers) {
		// fetch voucher details
		Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		List<Object[]> ivDetails = inventoryVoucherDetailRepository.findByInventoryVoucherHeaderPidIn(ivHeaderPids);
//		Map<String, Double> ivTotalVolume = ivDetails.stream().collect(Collectors.groupingBy(obj -> obj[0].toString(),
//				Collectors.summingDouble(obj -> ((Double) (obj[3] == null ? 1.0d : obj[3]) * (Double) obj[4]))));

		// nested ternary operator
		Map<String, Double> ivTotalVolume = ivDetails.stream()
				.collect(Collectors.groupingBy(obj -> obj[0].toString(),
						Collectors.summingDouble(
								obj -> ((Double) (obj[3] == null ? 1.0d : ((Boolean) obj[6] ? obj[5] : obj[4]))
										* (Double) obj[3]))));

		boolean pdfDownloadButtonStatus = false;
		boolean orderEdit = false;
		boolean sendSalesOrderSapButtonStatus = false;
		Optional<CompanyConfiguration> opCompanyConfigurationPdfDownload = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		Optional<CompanyConfiguration> opCompanyConfigurationSalesEdit = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_EDIT_ENABLED);
		Optional<CompanyConfiguration> opCompanyConfigurationSendSalesOrderSap = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_SAP);

		if (opCompanyConfigurationPdfDownload.isPresent()) {

			if (opCompanyConfigurationPdfDownload.get().getValue().equals("true")) {
				pdfDownloadButtonStatus = true;
			} else {
				pdfDownloadButtonStatus = false;
			}
		}

		if (opCompanyConfigurationSalesEdit.isPresent()) {

			if (opCompanyConfigurationSalesEdit.get().getValue().equals("true")) {
				orderEdit = true;
			} else {
				orderEdit = false;
			}
		}

		if (opCompanyConfigurationSendSalesOrderSap.isPresent()) {

			if (opCompanyConfigurationSendSalesOrderSap.get().getValue().equals("true")) {
				sendSalesOrderSapButtonStatus = true;
			} else {
				sendSalesOrderSapButtonStatus = false;
			}
		}

		DecimalFormat df = new DecimalFormat("0.00");
		int size = inventoryVouchers.size();
		List<SalesPerformanceDTO> salesPerformanceDTOs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			SalesPerformanceDTO salesPerformanceDTO = new SalesPerformanceDTO();
			Object[] ivData = inventoryVouchers.get(i);
			salesPerformanceDTO.setPid(ivData[0].toString());
			salesPerformanceDTO.setDocumentNumberLocal(ivData[1].toString());
			salesPerformanceDTO.setDocumentNumberServer(ivData[2].toString());
			salesPerformanceDTO.setDocumentPid(ivData[3].toString());
			salesPerformanceDTO.setDocumentName(ivData[4].toString());
			salesPerformanceDTO.setCreatedDate((LocalDateTime) ivData[5]);
			salesPerformanceDTO.setDocumentDate((LocalDateTime) ivData[6]);
			salesPerformanceDTO.setReceiverAccountPid(ivData[7].toString());
			salesPerformanceDTO.setReceiverAccountName(ivData[8].toString());
			salesPerformanceDTO.setSupplierAccountPid(ivData[9].toString());
			salesPerformanceDTO.setSupplierAccountName(ivData[10].toString());
			salesPerformanceDTO.setEmployeePid(ivData[11].toString());
			salesPerformanceDTO.setEmployeeName(ivData[12].toString());
			salesPerformanceDTO.setUserName(ivData[13].toString());
			salesPerformanceDTO.setDocumentTotal((double) ivData[14]);
			salesPerformanceDTO.setDocumentVolume((double) ivData[15]);
			salesPerformanceDTO.setTotalVolume(ivTotalVolume.get(salesPerformanceDTO.getPid()));
			salesPerformanceDTO.setPdfDownloadButtonStatus(pdfDownloadButtonStatus);

			salesPerformanceDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			salesPerformanceDTO.setVisitRemarks(ivData[17] == null ? null : ivData[17].toString());

			salesPerformanceDTO.setOrderNumber(ivData[18] == null ? 0 : Long.parseLong(ivData[18].toString()));

			salesPerformanceDTO.setPdfDownloadStatus(Boolean.valueOf(ivData[19].toString()));

			salesPerformanceDTO.setSalesManagementStatus(SalesManagementStatus.valueOf(ivData[20].toString()));

			salesPerformanceDTO.setDocumentTotalUpdated(
					ivData[21] != null ? Double.parseDouble(df.format(Double.parseDouble(ivData[21].toString())))
							: 0.0);
			salesPerformanceDTO
					.setDocumentVolumeUpdated(ivData[22] != null ? Double.parseDouble(ivData[22].toString()) : 0.0);
			salesPerformanceDTO.setUpdatedStatus(ivData[23] != null ? Boolean.valueOf(ivData[23].toString()) : false);
			salesPerformanceDTO.setReceiverAccountLocation(ivData[26] != null ? ivData[26].toString() : "");
			salesPerformanceDTO.setEditOrder(orderEdit);
			salesPerformanceDTO.setSendSalesOrderSapButtonStatus(sendSalesOrderSapButtonStatus);
			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}

	@RequestMapping(value = "/sales-order-draft/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/sales-order-draft/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("inventoryVoucherHeaderPids") String[] inventoryVoucherHeaderPids,
			HttpServletResponse response) {
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = inventoryVoucherService
				.findAllByCompanyIdAndInventoryPidIn(Arrays.asList(inventoryVoucherHeaderPids));
		if (inventoryVoucherHeaderDTOs.isEmpty()) {
			return;
		}
		buildExcelDocument(inventoryVoucherHeaderDTOs, response);
	}

	private void buildExcelDocument(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "SalesOrder" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Order No", "Salesman", "Order Date", "Customer", "Customer Location", "Supplier",
				"Product Name", "Quantity", "Unit Quantity", "Total Quantity", "Free Quantity", "Selling Rate",
				"Discount Amount", "Discount Percentage", "Tax Amount", "Row Total" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, inventoryVoucherHeaderDTOs);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			// Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Sales Order {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs) {
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		for (InventoryVoucherHeaderDTO ivh : inventoryVoucherHeaderDTOs) {
			for (InventoryVoucherDetailDTO ivd : ivh.getInventoryVoucherDetails()) {
				HSSFRow row = worksheet.createRow(rowNum++);
				row.createCell(0).setCellValue(ivh.getDocumentNumberLocal());
				row.createCell(1).setCellValue(ivh.getUserName());
				HSSFCell docDateCell = row.createCell(2);
				docDateCell.setCellValue(ivh.getDocumentDate().toString());
				docDateCell.setCellStyle(dateCellStyle);
				row.createCell(3).setCellValue(ivh.getReceiverAccountName());
				row.createCell(4).setCellValue(ivh.getReceiverAccountLocation());
				row.createCell(5).setCellValue(ivh.getSupplierAccountName());

				row.createCell(6).setCellValue(ivd.getProductName());
				row.createCell(7).setCellValue(ivd.getQuantity());
				row.createCell(8).setCellValue(ivd.getProductUnitQty());
				row.createCell(9).setCellValue((ivd.getQuantity() * ivd.getProductUnitQty()));
				row.createCell(10).setCellValue(ivd.getFreeQuantity());
				row.createCell(11).setCellValue(ivd.getSellingRate());
				row.createCell(12).setCellValue(ivd.getDiscountAmount());
				row.createCell(13).setCellValue(ivd.getDiscountPercentage());
				row.createCell(14).setCellValue(ivd.getTaxAmount());
				row.createCell(15).setCellValue(ivd.getRowTotal());
			}
		}
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// Create a Font for styling header cells
		Font headerFont = worksheet.getWorkbook().createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a CellStyle with the font
		HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		HSSFRow headerRow = worksheet.createRow(0);
		// Create cells
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	}

//	public static void fillReport(HSSFSheet worksheet, List<InventoryVoucherXlsDownloadDTO> xlsDownloadDTOs) {
//		// Row offset
//		int startRowIndex = 1;
//		int startColIndex = 0;
//
//		// Create cell style for the body
//		HSSFCellStyle bodyCellStyle = worksheet.getWorkbook().createCellStyle();
//		bodyCellStyle.setWrapText(true);
//
//		// Create body
//		for (int i = 0; i < xlsDownloadDTOs.size(); i++) {
//			// Create a new row
//			startRowIndex = startRowIndex + 1;
//			HSSFRow row = worksheet.createRow((short) startRowIndex);
//
//			HSSFCell cell1 = row.createCell(startColIndex + 0);
//			cell1.setCellValue(xlsDownloadDTOs.get(i).getDocumentNumberLocal());
//			cell1.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell2 = row.createCell(startColIndex + 1);
//			cell2.setCellValue(xlsDownloadDTOs.get(i).getDocumentName());
//			cell2.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell3 = row.createCell(startColIndex + 2);
//			cell3.setCellValue(xlsDownloadDTOs.get(i).getUserName());
//			cell3.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell4 = row.createCell(startColIndex + 3);
//			if (xlsDownloadDTOs.get(i).getDocumentDate() != null) {
//				cell4.setCellValue(xlsDownloadDTOs.get(i).getDocumentDate().toLocalDate().toString());
//			} else {
//				cell4.setCellValue("");
//			}
//			cell4.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell5 = row.createCell(startColIndex + 4);
//			cell5.setCellValue(xlsDownloadDTOs.get(i).getReceiverAccountName());
//			cell5.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell6 = row.createCell(startColIndex + 5);
//			cell6.setCellValue(xlsDownloadDTOs.get(i).getSupplierAccountName());
//			cell6.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell7 = row.createCell(startColIndex + 6);
//			cell7.setCellValue(xlsDownloadDTOs.get(i).getDocumentTotal());
//			cell7.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell8 = row.createCell(startColIndex + 7);
//			cell8.setCellValue(xlsDownloadDTOs.get(i).getDocumentVolume());
//			cell8.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell9 = row.createCell(startColIndex + 8);
//			cell9.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountAmount());
//			cell9.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell10 = row.createCell(startColIndex + 9);
//			cell10.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountPercentage());
//			cell10.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell11 = row.createCell(startColIndex + 10);
//			cell11.setCellValue(xlsDownloadDTOs.get(i).getProductName());
//			cell11.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell12 = row.createCell(startColIndex + 11);
//			cell12.setCellValue(xlsDownloadDTOs.get(i).getQuantity());
//			cell12.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell13 = row.createCell(startColIndex + 12);
//			cell13.setCellValue(xlsDownloadDTOs.get(i).getFreeQuantity());
//			cell13.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell14 = row.createCell(startColIndex + 13);
//			cell14.setCellValue(xlsDownloadDTOs.get(i).getSellingRate());
//			cell14.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell15 = row.createCell(startColIndex + 14);
//			cell15.setCellValue(xlsDownloadDTOs.get(i).getTaxAmount());
//			cell15.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell16 = row.createCell(startColIndex + 15);
//			cell16.setCellValue(xlsDownloadDTOs.get(i).getDiscountAmount());
//			cell16.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell17 = row.createCell(startColIndex + 16);
//			cell17.setCellValue(xlsDownloadDTOs.get(i).getRowTotal());
//			cell17.setCellStyle(bodyCellStyle);
//		}
//	}

	@RequestMapping(value = "/sales-order-draft/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		log.info("Sales Tally Download Status " + tallyDownloadStatus);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/sales-order-draft/updateInventory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<InventoryVoucherHeader> updateInventoryDetail(@RequestBody UpdateQntyIvhdDTO ivhDTO) {
		System.out.println("UPDATE------------------------------------------------------------------------");
		System.out.println(ivhDTO.getPid());
		ivhDTO.getIvhd().forEach(data -> System.out.println("ivhdetails---------" + data));
		double updatedDocTotal = 0.0;
		double updatedDocVol = 0.0;
		InventoryVoucherHeader ivh = null;
		InventoryVoucherHeader inventoryVoucherHeaderOrg = new InventoryVoucherHeader();
		List<InventoryVoucherDetail> details = new ArrayList<>();
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository.findByPid(ivhDTO.getPid())
				.get();
		List<InventoryVoucherDetail> ivdList = inventoryVoucherHeader.getInventoryVoucherDetails();
		ExecutiveTaskExecution ete = inventoryVoucherHeader.getExecutiveTaskExecution();
//		 ete.setId(null);
//		 ete.setPid(ExecutiveTaskExecutionService.PID_PREFIX + RandomUtil.generatePid());
		for (InventoryVoucherDetail ivhd : ivdList) {
			InventoryVoucherDetail inventoryVoucherDetail = new InventoryVoucherDetail();
			inventoryVoucherDetail.setProduct(ivhd.getProduct());
			inventoryVoucherDetail.setQuantity(ivhd.getQuantity());
			inventoryVoucherDetail.setUpdatedQuantity(ivhd.getUpdatedQuantity());
			inventoryVoucherDetail.setFreeQuantity(ivhd.getFreeQuantity());
			inventoryVoucherDetail.setSellingRate(ivhd.getSellingRate());
			inventoryVoucherDetail.setMrp(ivhd.getMrp());
			inventoryVoucherDetail.setPurchaseRate(ivhd.getPurchaseRate());
			inventoryVoucherDetail.setTaxPercentage(ivhd.getTaxPercentage());
			inventoryVoucherDetail.setDiscountPercentage(ivhd.getDiscountPercentage());
			inventoryVoucherDetail.setBatchNumber(ivhd.getBatchNumber());
			inventoryVoucherDetail.setBatchDate(ivhd.getBatchDate());
			inventoryVoucherDetail.setRowTotal(ivhd.getRowTotal());
			inventoryVoucherDetail.setUpdatedRowTotal(ivhd.getUpdatedRowTotal());
			inventoryVoucherDetail.setDiscountAmount(ivhd.getDiscountAmount());
			inventoryVoucherDetail.setTaxAmount(ivhd.getTaxAmount());
			inventoryVoucherDetail.setLength(ivhd.getLength());
			inventoryVoucherDetail.setWidth(ivhd.getWidth());
			inventoryVoucherDetail.setThickness(ivhd.getThickness());
			inventoryVoucherDetail.setSize(ivhd.getSize());
			inventoryVoucherDetail.setColor(ivhd.getColor());
			inventoryVoucherDetail.setItemtype(ivhd.getItemtype());
			for (UpdateIvhdetailQnty updateIVD : ivhDTO.getIvhd()) {
				if (ivhd.getId().equals(updateIVD.getId())) {
					double discAmt = ivhd.getDiscountAmount();
					double discPer = ivhd.getDiscountPercentage();
					double sellingRate = ivhd.getSellingRate();
					double taxPer = ivhd.getTaxPercentage();
					sellingRate = sellingRate - discAmt;
					sellingRate = sellingRate - (sellingRate * (discPer * 0.01));
					double rowTotal = sellingRate * updateIVD.getQnty();
					double totalTax = rowTotal * (taxPer * 0.01);
					double updatedRowTotal = totalTax + rowTotal;

					double rowTotalDiff = 0.0;
					double quantityDiff = 0.0;
					if (ivhd.getUpdatedStatus()) {
						rowTotalDiff = Math.abs(ivhd.getUpdatedRowTotal() - updatedRowTotal);

						quantityDiff = Math.abs(ivhd.getUpdatedQuantity() - updateIVD.getQnty());

						System.out.println("updated : " + ivhd.getUpdatedStatus());
						System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
					} else {
						rowTotalDiff = Math.abs(ivhd.getRowTotal() - updatedRowTotal);

						quantityDiff = Math.abs(ivhd.getQuantity() - updateIVD.getQnty());

						System.out.println("updated : " + ivhd.getUpdatedStatus());
						System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
					}

					if (inventoryVoucherHeader.getUpdatedStatus()) {
						if (ivhd.getUpdatedStatus()) {
							if (ivhd.getUpdatedQuantity() <= updateIVD.getQnty()) {
								updatedDocVol = inventoryVoucherHeader.getDocumentVolumeUpdated() + quantityDiff;
							} else {
								updatedDocVol = Math
										.abs(quantityDiff - inventoryVoucherHeader.getDocumentVolumeUpdated());
							}

							if (ivhd.getUpdatedRowTotal() <= updatedRowTotal) {
								updatedDocTotal = inventoryVoucherHeader.getDocumentTotalUpdated() + rowTotalDiff;
							} else {
								updatedDocTotal = Math
										.abs(rowTotalDiff - inventoryVoucherHeader.getDocumentTotalUpdated());
							}
						} else {
							if (ivhd.getQuantity() <= updateIVD.getQnty()) {
								updatedDocVol = inventoryVoucherHeader.getDocumentVolumeUpdated() + quantityDiff;
							} else {
								updatedDocVol = Math
										.abs(quantityDiff - inventoryVoucherHeader.getDocumentVolumeUpdated());
							}

							if (ivhd.getRowTotal() <= updatedRowTotal) {
								updatedDocTotal = inventoryVoucherHeader.getDocumentTotalUpdated() + rowTotalDiff;
							} else {
								updatedDocTotal = Math
										.abs(rowTotalDiff - inventoryVoucherHeader.getDocumentTotalUpdated());
							}
						}

						System.out.println("updated... : TRUE");
						System.out.println(
								"updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
					} else {

						if (ivhd.getQuantity() <= updateIVD.getQnty()) {
							updatedDocVol = inventoryVoucherHeader.getDocumentVolume() + quantityDiff;
						} else {
							updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeader.getDocumentVolume());
						}

						if (ivhd.getRowTotal() <= updatedRowTotal) {
							updatedDocTotal = inventoryVoucherHeader.getDocumentTotal() + rowTotalDiff;
						} else {
							updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeader.getDocumentTotal());
						}

						System.out.println("updated... : FALSE");
						System.out.println(
								"updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
					}
					inventoryVoucherDetail.setRowTotal(updatedRowTotal);
					inventoryVoucherDetail.setQuantity(updateIVD.getQnty());
				}
			}
			details.add(inventoryVoucherDetail);
		}
		Optional<com.orderfleet.webapp.domain.Document> optionalDoc = documentRepository.findOneByDocumentPrefix("SSC");
		if (optionalDoc.isPresent()) {
			inventoryVoucherHeaderOrg.setDocument(optionalDoc.get());
			inventoryVoucherHeaderOrg.setDocumentDate(LocalDateTime.now());
		}
		inventoryVoucherHeaderOrg.setProcessStatus(inventoryVoucherHeader.getProcessStatus());
		inventoryVoucherHeaderOrg.setReceiverAccount(inventoryVoucherHeader.getReceiverAccount());
		inventoryVoucherHeaderOrg.setSupplierAccount(inventoryVoucherHeader.getSupplierAccount());
		inventoryVoucherHeaderOrg.setCreatedBy(inventoryVoucherHeader.getCreatedBy());
		inventoryVoucherHeaderOrg.setEmployee(inventoryVoucherHeader.getEmployee());
		inventoryVoucherHeaderOrg.setDocumentVolumeUpdated(inventoryVoucherHeader.getDocumentVolumeUpdated());
		inventoryVoucherHeaderOrg.setDocDiscountPercentage(inventoryVoucherHeader.getDocDiscountPercentage());
		inventoryVoucherHeaderOrg.setDocDiscountAmount(inventoryVoucherHeader.getDocDiscountAmount());
		inventoryVoucherHeaderOrg.setCompany(inventoryVoucherHeader.getCompany());
		inventoryVoucherHeaderOrg.setStatus(inventoryVoucherHeader.getStatus());
		inventoryVoucherHeaderOrg.setPriceLevel(inventoryVoucherHeader.getPriceLevel());
		inventoryVoucherHeaderOrg.setSourceModule(inventoryVoucherHeader.getSourceModule());
		inventoryVoucherHeaderOrg.setOrderNumber(inventoryVoucherHeader.getOrderNumber());
		inventoryVoucherHeaderOrg.setReferenceDocumentType(inventoryVoucherHeader.getReferenceDocumentType());
		inventoryVoucherHeaderOrg.setOrderStatus(inventoryVoucherHeader.getOrderStatus());
		inventoryVoucherHeaderOrg.setUpdatedStatus(inventoryVoucherHeader.getUpdatedStatus());
		inventoryVoucherHeaderOrg.setBookingId(inventoryVoucherHeader.getBookingId());
		inventoryVoucherHeaderOrg.setDeliveryDate(inventoryVoucherHeader.getDeliveryDate());
		inventoryVoucherHeaderOrg.setBookingDate(inventoryVoucherHeader.getBookingDate());
		inventoryVoucherHeaderOrg.setValidationDays(inventoryVoucherHeader.getValidationDays());
		inventoryVoucherHeaderOrg.setUpdatedBy(inventoryVoucherHeader.getUpdatedBy());
		inventoryVoucherHeaderOrg.setSalesLedger(inventoryVoucherHeader.getSalesLedger());
		inventoryVoucherHeaderOrg.setTallyDownloadStatus(inventoryVoucherHeader.getTallyDownloadStatus());
		inventoryVoucherHeaderOrg.setSalesManagementStatus(inventoryVoucherHeader.getSalesManagementStatus());
		inventoryVoucherHeaderOrg.setProcessFlowStatus(inventoryVoucherHeader.getProcessFlowStatus());
		inventoryVoucherHeaderOrg.setPdfDownloadStatus(inventoryVoucherHeader.getPdfDownloadStatus());
		inventoryVoucherHeaderOrg.setUpdatedStatus(inventoryVoucherHeader.getUpdatedStatus());
		inventoryVoucherHeaderOrg.setSendSalesOrderEmailStatus(inventoryVoucherHeader.getSendSalesOrderEmailStatus());
		inventoryVoucherHeaderOrg.setPaymentReceived(inventoryVoucherHeader.getPaymentReceived());
		inventoryVoucherHeaderOrg.setReferenceInvoiceNumber(inventoryVoucherHeader.getReferenceInvoiceNumber());
		inventoryVoucherHeaderOrg.setRoundedOff(inventoryVoucherHeader.getRoundedOff());
		inventoryVoucherHeaderOrg.setErpReferenceNumber(inventoryVoucherHeader.getErpReferenceNumber());
		inventoryVoucherHeaderOrg.setErpStatus(inventoryVoucherHeader.getErpStatus());
		inventoryVoucherHeaderOrg.setRejectedStatus(inventoryVoucherHeader.getRejectedStatus());
		inventoryVoucherHeaderOrg.setRemarks(inventoryVoucherHeader.getRemarks());
		inventoryVoucherHeaderOrg.setCreatedDate(LocalDateTime.now());
		inventoryVoucherHeaderOrg.setExecutiveTaskExecution(ete);
		inventoryVoucherHeaderOrg.setReferenceDocumentNumber(inventoryVoucherHeader.getDocumentNumberLocal());
		inventoryVoucherHeaderOrg
				.setDocumentNumberLocal(LocalDateTime.now() + "_" + SecurityUtils.getCurrentUserLogin() + "_" + "SSC");
		inventoryVoucherHeaderOrg
				.setDocumentNumberServer(LocalDateTime.now() + "_" + SecurityUtils.getCurrentUserLogin() + "_" + "SSC");
		inventoryVoucherHeaderOrg.setDocumentTotal(updatedDocTotal);
		inventoryVoucherHeaderOrg.setDocumentVolume(updatedDocVol);
		inventoryVoucherHeaderOrg.setInventoryVoucherDetails(details);
		inventoryVoucherHeaderOrg.setPid(InventoryVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
	
		
		// inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
//		if(!eteResult.getId().equals(null)) {
		
		ivh = inventoryVoucherHeaderRepository.save(inventoryVoucherHeaderOrg);
	
		return new ResponseEntity<>(ivh, HttpStatus.OK);

	}

//	@RequestMapping(value = "/sales-performance-management-edit/updateInventory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<InventoryVoucherHeaderDTO> updateInventoryDetail(@RequestParam long id,
//			@RequestParam long quantity, @RequestParam String ivhPid) {
//		System.out.println("------------------------------------------------------------------------");
//		System.out.println("quantity : " + quantity + "\n Id :" + id + " \n IVHPid : " + ivhPid);
//		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
//		List<InventoryVoucherDetailDTO> ivdList = inventoryVoucherHeaderDTO.getInventoryVoucherDetails();
//		Optional<InventoryVoucherDetailDTO> opIvDetail = ivdList.stream().filter(ivd -> ivd.getDetailId() == id)
//				.findAny();
//
//		if (opIvDetail.isPresent()) {
//			InventoryVoucherDetailDTO ivdDto = opIvDetail.get();
//			// ivdDto.setUpdatedQty(quantity);
//			double discAmt = ivdDto.getDiscountAmount();
//			double discPer = ivdDto.getDiscountPercentage();
//			double sellingRate = ivdDto.getSellingRate();
//			double taxPer = ivdDto.getTaxPercentage();
//			sellingRate = sellingRate - discAmt;
//			sellingRate = sellingRate - (sellingRate * (discPer * 0.01));
//			double rowTotal = sellingRate * quantity;
//			double totalTax = rowTotal * (taxPer * 0.01);
//			double updatedRowTotal = totalTax + rowTotal;
//
//			double rowTotalDiff = 0.0;
//			double quantityDiff = 0.0;
//			if (ivdDto.getUpdatedStatus()) {
//				rowTotalDiff = Math.abs(ivdDto.getUpdatedRowTotal() - updatedRowTotal);
//
//				quantityDiff = Math.abs(ivdDto.getUpdatedQty() - quantity);
//
//				System.out.println("updated : " + ivdDto.getUpdatedStatus());
//				System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
//			} else {
//				rowTotalDiff = Math.abs(ivdDto.getRowTotal() - updatedRowTotal);
//
//				quantityDiff = Math.abs(ivdDto.getQuantity() - quantity);
//
//				System.out.println("updated : " + ivdDto.getUpdatedStatus());
//				System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
//			}
//
//			double updatedDocTotal = 0.0;
//			double updatedDocVol = 0.0;
//			if (inventoryVoucherHeaderDTO.getUpdatedStatus()) {
//				if (ivdDto.getUpdatedStatus()) {
//					if (ivdDto.getUpdatedQty() <= quantity) {
//						updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + quantityDiff;
//					} else {
//						updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
//					}
//
//					if (ivdDto.getUpdatedRowTotal() <= updatedRowTotal) {
//						updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotalUpdated() + rowTotalDiff;
//					} else {
//						updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
//					}
//				} else {
//					if (ivdDto.getQuantity() <= quantity) {
//						updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + quantityDiff;
//					} else {
//						updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
//					}
//
//					if (ivdDto.getRowTotal() <= updatedRowTotal) {
//						updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotalUpdated() + rowTotalDiff;
//					} else {
//						updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
//					}
//				}
//
//				System.out.println("updated... : TRUE");
//				System.out.println("updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
//			} else {
//
//				if (ivdDto.getQuantity() <= quantity) {
//					updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolume() + quantityDiff;
//				} else {
//					updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolume());
//				}
//
//				if (ivdDto.getRowTotal() <= updatedRowTotal) {
//					updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotal() + rowTotalDiff;
//				} else {
//					updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotal());
//				}
//
//				System.out.println("updated... : FALSE");
//				System.out.println("updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
//			}
//
//			ivdDto.setUpdatedStatus(true);
//			ivdDto.setUpdatedRowTotal(updatedRowTotal);
//			ivdDto.setUpdatedQty(quantity);
//
//			inventoryVoucherHeaderDTO.setDocumentTotalUpdated(updatedDocTotal);
//			inventoryVoucherHeaderDTO.setDocumentVolumeUpdated(updatedDocVol);
//			inventoryVoucherHeaderDTO.setUpdatedStatus(true);
//			System.out.println("IVH : Volume : " + inventoryVoucherHeaderDTO.getDocumentVolume() + "\n");
//			System.out.println("IVH : Volume Updated : " + inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + "\n");
//			System.out.println("------------------------------------------------------------------------");
//			inventoryVoucherDetailService.updateInventoryVoucherDetail(ivdDto);
//			inventoryVoucherService.updateInventoryVoucherHeader(inventoryVoucherHeaderDTO);
//
//		}
//
//		// inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
//		return new ResponseEntity<>(inventoryVoucherHeaderDTO, HttpStatus.OK);
//
//	}

	@RequestMapping(value = "/sales-order-draft/changeSalesManagementStatus", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> changeSalesManagementStatus(@RequestParam String pid,
			@RequestParam SalesManagementStatus salesManagementStatus) {
		log.info("Sales Sales Management Status " + salesManagementStatus);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus() != TallyDownloadStatus.COMPLETED) {
			inventoryVoucherHeaderDTO.setSalesManagementStatus(salesManagementStatus);
			inventoryVoucherService.updateInventoryVoucherHeaderSalesManagementStatus(inventoryVoucherHeaderDTO);
			return new ResponseEntity<>("success", HttpStatus.OK);
		}

		return new ResponseEntity<>("failed", HttpStatus.OK);

	}

	@RequestMapping(value = "/sales-order-draft/downloadSalesOrderSap", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> downloadSalesOrderSap(@RequestParam String inventoryPid)
			throws IOException {

		log.info("Download to Sap with pid " + inventoryPid);

		sendSalesOrderSapService.sendSalesOrder(inventoryPid);

//		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(inventoryPid).get();
//
//		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus().equals(TallyDownloadStatus.PENDING)
//				&& inventoryVoucherHeaderDTO.getSalesManagementStatus().equals(SalesManagementStatus.APPROVE)) {
//			log.info("Downloading to sap prabhu.............");
//			
//			
//
//			SalesOrderResponseDataSap salesOrderResponseDataSap = sendSalesOrdertoSap(inventoryVoucherHeaderDTO);
//
//			log.info("Response Data: " + salesOrderResponseDataSap);
//
//			inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.PROCESSING);
//
//			if (salesOrderResponseDataSap.getStatusCode() == 0) {
//				inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
//			}
//
//			if (salesOrderResponseDataSap.getStatusCode() == 1) {
//				inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.PENDING);
//			}
//
//			inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
//
//		}

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	private SalesOrderResponseDataSap sendSalesOrdertoSap(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		log.debug("Web request to send to sap ...");

		SalesOrderMasterSap requestBody = getRequestBody(inventoryVoucherHeaderDTO);

		log.info("" + requestBody);

		HttpEntity<SalesOrderMasterSap> entity = new HttpEntity<>(requestBody, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

//		String API_URL = "http://117.247.186.223:81/Service1.svc/AddOrder"; //old ip address

		String API_URL = "http://59.94.176.87:81/Service1.svc/AddOrder";

//		String API_URL = "http://192.168.10.36:130/Service1.svc/AddOrder";

		log.info("POST URL: " + API_URL);

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			// Convert object to JSON string
			String json = mapper.writeValueAsString(requestBody);
			log.info("Sales Order Json:- " + json);

			SalesOrderResponseDataSap salesOrderResponseDataSap = restTemplate.postForObject(API_URL, entity,
					SalesOrderResponseDataSap.class);

//			SalesOrderResponseDataSap salesOrderResponseDataSap = new SalesOrderResponseDataSap();
//			salesOrderResponseDataSap.setStatusCode(1);
//			salesOrderResponseDataSap.setStatusMessage("Successfully Posted");

			return salesOrderResponseDataSap;

		} catch (HttpClientErrorException exception) {
			SalesOrderResponseDataSap salesOrderResponseDataSap = new SalesOrderResponseDataSap();
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {

				log.info("HttpClientError Exception-BadRequest........." + exception.getMessage());
				// throw new ServiceException(exception.getResponseBodyAsString());
				salesOrderResponseDataSap.setStatusCode(2);
				salesOrderResponseDataSap.setStatusMessage("Could not able to connect the server");
			}
			log.info("HttpClientError Exception........." + exception.getMessage());
			salesOrderResponseDataSap.setStatusCode(2);
			salesOrderResponseDataSap.setStatusMessage("Could not able to connect the server");
			// throw new ServiceException(exception.getMessage());
			return salesOrderResponseDataSap;
		} catch (Exception exception) {

			log.info("Exception........." + exception.getMessage());
			// throw new ServiceException(exception.getMessage());
			SalesOrderResponseDataSap salesOrderResponseDataSap = new SalesOrderResponseDataSap();
			salesOrderResponseDataSap.setStatusCode(2);
			salesOrderResponseDataSap.setStatusMessage("Could not able to connect the server");
			return salesOrderResponseDataSap;
		}
	}

	private SalesOrderMasterSap getRequestBody(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<CompanyConfiguration> opPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PIECES_TO_QUANTITY);

		SalesOrderMasterSap salesOrderMasterSap = new SalesOrderMasterSap();

		salesOrderMasterSap.setDbKey(1);
		salesOrderMasterSap.setLocation("");

		salesOrderMasterSap.setCustomerCode(inventoryVoucherHeaderDTO.getReceiverAccountAlias());
		salesOrderMasterSap.setCustomerName(inventoryVoucherHeaderDTO.getReceiverAccountName());

		salesOrderMasterSap.setCustomerRef("");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime localDateTime = inventoryVoucherHeaderDTO.getClientDate();
		String date = localDateTime.format(formatter);

		salesOrderMasterSap.setPostingDate(date);
		salesOrderMasterSap.setDocDate(date);

		salesOrderMasterSap.setValidUntil(date);
		salesOrderMasterSap.setSalesCommitDate(date);

		salesOrderMasterSap.setRemarks(inventoryVoucherHeaderDTO.getVisitRemarks());
		salesOrderMasterSap.setPriority("4");

		List<SalesOrderItemDetailsSap> salesOrderItems = new ArrayList<>();

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {
			SalesOrderItemDetailsSap salesOrderItemDetailsSap = new SalesOrderItemDetailsSap();
			double quantity = inventoryVoucherDetailDTO.getQuantity();
			String itemType = inventoryVoucherDetailDTO.getItemtype() != null ? inventoryVoucherDetailDTO.getItemtype()
					: "MTS";
			if (opPiecesToQuantity.isPresent()) {
				if (opPiecesToQuantity.get().getValue().equals("true")) {

					if (inventoryVoucherDetailDTO.getProductSKU() != null
							&& inventoryVoucherDetailDTO.getProductSKU().equalsIgnoreCase("MTS")) {
						quantity = (quantity * inventoryVoucherDetailDTO.getProductUnitQty()) / 1000; // Quantity into
																										// MTS;
						itemType = inventoryVoucherDetailDTO.getProductSKU();
					} else if (inventoryVoucherDetailDTO.getProductSKU() != null
							&& inventoryVoucherDetailDTO.getProductSKU().equalsIgnoreCase("Pcs")) {
						itemType = inventoryVoucherDetailDTO.getProductSKU();
					}
				}
			}

			salesOrderItemDetailsSap.setItemCode(inventoryVoucherDetailDTO.getProductName());
			salesOrderItemDetailsSap.setItemName(inventoryVoucherDetailDTO.getProductAlias());
			salesOrderItemDetailsSap.setQuantity(String.valueOf(quantity));
			salesOrderItemDetailsSap.setuPrice(String.valueOf("0.0"));
			salesOrderItemDetailsSap.setTaxCode("");
			salesOrderItemDetailsSap.setWareHouseCode("PSO2");
			salesOrderItemDetailsSap.setItemtype("MTS");
			// salesOrderItemDetailsSap.setItemtype(itemType);
			salesOrderItemDetailsSap.setArecieved("");

			salesOrderItems.add(salesOrderItemDetailsSap);
		}

		salesOrderMasterSap.setItemDetails(salesOrderItems);

		int Scode = 0;

		if (inventoryVoucherHeaderDTO.getEmployeeAlias() != null) {
			try {
				Scode = Integer.parseInt(inventoryVoucherHeaderDTO.getEmployeeAlias());
			} catch (NumberFormatException e) {
				Scode = 0;
			}
		}
		salesOrderMasterSap.setsCode(Scode);
		salesOrderMasterSap.setOrderType("O");
		salesOrderMasterSap.setDiscount(0.0);
		salesOrderMasterSap.setShipTo("");
		salesOrderMasterSap.setBillTo("");

		return salesOrderMasterSap;
	}

	@RequestMapping(value = "/sales-order-draft/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadStatus(@RequestParam String inventoryPid, HttpServletResponse response) throws IOException {

		log.info("Download pdf with pid " + inventoryPid);
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDtos = new ArrayList<>();

		String[] inventoryPidArray = inventoryPid.split(",");

		if (inventoryPidArray.length > 0) {

			for (String ivhPid : inventoryPidArray) {

				InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid)
						.get();

				inventoryVoucherHeaderDtos.add(inventoryVoucherHeaderDTO);
			}
		}

		buildPdf(inventoryVoucherHeaderDtos, response);

		if (inventoryVoucherHeaderDtos.size() > 0) {

			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDtos)
				if (!inventoryVoucherHeaderDTO.getPdfDownloadStatus()) {
					DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_187" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description = "Updating pdf download status by pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					inventoryVoucherHeaderRepository.updatePdfDownlodStatusByPid(inventoryVoucherHeaderDTO.getPid());
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
					logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag
							+ "," + description);
				}
		}

	}

	private void buildPdf(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");
		if (inventoryVoucherHeaderDTOs.size() > 1) {

			response.setHeader("Content-Disposition", "inline;filename=\"" + "Packing Slip_"
					+ inventoryVoucherHeaderDTOs.get(0).getReceiverAccountName() + ".pdf\"");

		} else {
			response.setHeader("Content-Disposition", "inline;filename=\"" + "Packing Slip_"
					+ inventoryVoucherHeaderDTOs.get(0).getOrderNumber() + ".pdf\"");
		}
		// Get the output stream for writing PDF object
		ServletOutputStream out = response.getOutputStream();
		try {
			Document document = new Document();
			/* Basic PDF Creation inside servlet */
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();

			// writer.addJavaScript("this.print(false);", false);

			com.itextpdf.text.Font fontSize_22 = FontFactory.getFont(FontFactory.TIMES, 20f,
					com.itextpdf.text.Font.BOLD);
			com.itextpdf.text.Font fontSize_16 = FontFactory.getFont(FontFactory.TIMES, 16f,
					com.itextpdf.text.Font.BOLD);

			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDTOs) {
				Paragraph companyName = new Paragraph();
				Paragraph line = new Paragraph();
				companyName.setAlignment(Element.ALIGN_CENTER);
				line.setAlignment(Element.ALIGN_CENTER);
				companyName.setFont(fontSize_22);
				companyName.add(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getLegalName());
				line.add(new Paragraph("_______________________________________________________"));

				String customerAddress = "";
				String customerEmail = "";
				String customerPhone = "";

				LocalDateTime date = inventoryVoucherHeaderDTO.getCreatedDate();

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

				String orderDate = date.format(formatter);

				if (!inventoryVoucherHeaderDTO.getCustomeraddress().equalsIgnoreCase("No Address")
						&& inventoryVoucherHeaderDTO.getCustomeraddress() != null)
					customerAddress = inventoryVoucherHeaderDTO.getCustomeraddress();

				if (inventoryVoucherHeaderDTO.getCustomerEmail() != null)
					customerEmail = inventoryVoucherHeaderDTO.getCustomerEmail();

				if (inventoryVoucherHeaderDTO.getCustomerPhone() != null)
					customerPhone = inventoryVoucherHeaderDTO.getCustomerPhone();

				document.add(companyName);
				document.add(line);
				document.add(new Paragraph("Sales Order No :" + inventoryVoucherHeaderDTO.getOrderNumber()));
				document.add(new Paragraph("Date :" + orderDate));
				document.add(new Paragraph("Executive : " + inventoryVoucherHeaderDTO.getEmployeeName()));
				document.add(new Paragraph("\n"));
				document.add(new Paragraph("Customer :" + inventoryVoucherHeaderDTO.getReceiverAccountName()));
				document.add(new Paragraph("Address :" + customerAddress));
				document.add(new Paragraph("Email :" + customerEmail));
				document.add(new Paragraph("Phone :" + customerPhone));
				document.add(new Paragraph("\n"));
				PdfPTable table = createPdfTable(inventoryVoucherHeaderDTO);
				table.setWidthPercentage(100);
				document.add(table);
				document.add(new Paragraph("\n\n"));
				document.add(new Paragraph("Remarks :" + inventoryVoucherHeaderDTO.getVisitRemarks()));
				document.add(new Paragraph("\n\n"));
				PdfPTable tableTotal = createTotalTable(inventoryVoucherHeaderDTO);
				// tableTotal.setWidthPercentage(50);
				tableTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
				document.add(tableTotal);

				document.newPage();
			}

			/*
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream ps =
			 * new PrintStream(baos); PdfWriter.getInstance(document, ps);
			 */

			PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
			writer.setOpenAction(action);

			document.close();
		} catch (DocumentException exc) {
			throw new IOException(exc.getMessage());
		} finally {
			out.close();
		}

	}

	private PdfPTable createTotalTable(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		DecimalFormat df = new DecimalFormat("0.00");

		double grandTotal = 0.0;

		if (inventoryVoucherHeaderDTO.getUpdatedStatus()) {
			grandTotal = inventoryVoucherHeaderDTO.getDocumentTotalUpdated();
		} else {
			grandTotal = inventoryVoucherHeaderDTO.getDocumentTotal();
		}

		double totalTaxAmount = 0.0;
		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {

			/*
			 * double amount = (inventoryVoucherDetailDTO.getSellingRate() *
			 * inventoryVoucherDetailDTO.getQuantity()); double taxAmount = amount *
			 * inventoryVoucherDetailDTO.getTaxPercentage() / 100;
			 */
			double quantity = 0.0;
			if (inventoryVoucherDetailDTO.getUpdatedStatus()) {
				quantity = inventoryVoucherDetailDTO.getUpdatedQty();
			} else {
				quantity = inventoryVoucherDetailDTO.getQuantity();
			}

			double amount = (inventoryVoucherDetailDTO.getSellingRate() * quantity);
			double discountedAmount = amount - (amount * inventoryVoucherDetailDTO.getDiscountPercentage() / 100);
			double taxAmnt = (discountedAmount * inventoryVoucherDetailDTO.getTaxPercentage() / 100);

			totalTaxAmount += taxAmnt;
		}

		PdfPTable table = new PdfPTable(new float[] { 10f, 10f });

		PdfPCell cell1 = new PdfPCell(new Paragraph("Grand Total :"));
		cell1.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(df.format(grandTotal))));
		cell2.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Tax Total :"));
		cell3.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell4 = new PdfPCell(new Paragraph(df.format(totalTaxAmount)));
		cell4.setBorder(Rectangle.NO_BORDER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);

		return table;
	}

	private PdfPTable createPdfTable(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		com.itextpdf.text.Font fontWeight = FontFactory.getFont(FontFactory.TIMES, 12f, com.itextpdf.text.Font.BOLD);

		PdfPTable table = new PdfPTable(new float[] { 225f, 100f, 100f, 100f, 100f, 100f });
		table.setTotalWidth(350f);

		PdfPCell cell1 = new PdfPCell(new Paragraph("Item Name", fontWeight));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell2 = new PdfPCell(new Paragraph("Price", fontWeight));
		cell2.setBorder(Rectangle.NO_BORDER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Quantity", fontWeight));
		cell3.setBorder(Rectangle.NO_BORDER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell4 = new PdfPCell(new Paragraph("Discount %", fontWeight));
		cell4.setBorder(Rectangle.NO_BORDER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell5 = new PdfPCell(new Paragraph("Tax Amount", fontWeight));
		cell5.setBorder(Rectangle.NO_BORDER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell6 = new PdfPCell(new Paragraph("Total", fontWeight));
		cell6.setBorder(Rectangle.NO_BORDER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell7 = new PdfPCell(new Paragraph(""));
		cell7.setBorder(Rectangle.NO_BORDER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);
		table.addCell(cell5);
		table.addCell(cell6);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);

		DecimalFormat df = new DecimalFormat("0.00");

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {

			double quantity = 0.0;
			double rowTotal = 0.00;

			if (inventoryVoucherDetailDTO.getUpdatedStatus()) {
				quantity = inventoryVoucherDetailDTO.getUpdatedQty();
				rowTotal = inventoryVoucherDetailDTO.getUpdatedRowTotal();
			} else {
				quantity = inventoryVoucherDetailDTO.getQuantity();
				rowTotal = inventoryVoucherDetailDTO.getRowTotal();
			}

			double amount = (inventoryVoucherDetailDTO.getSellingRate() * quantity);
			double discountedAmount = amount - (amount * inventoryVoucherDetailDTO.getDiscountPercentage() / 100);
			double taxAmnt = (discountedAmount * inventoryVoucherDetailDTO.getTaxPercentage() / 100);
			// double taxAmount = Math.round(taxAmt * 100.0) / 100.0;
			String taxAmount = df.format(taxAmnt);

			PdfPCell col1 = new PdfPCell(new Paragraph(inventoryVoucherDetailDTO.getProductName()));
			col1.setBorder(Rectangle.NO_BORDER);

			PdfPCell col2 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getSellingRate())));
			col2.setBorder(Rectangle.NO_BORDER);
			col2.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col3 = new PdfPCell(new Paragraph(String.valueOf(quantity)));
			col3.setBorder(Rectangle.NO_BORDER);
			col3.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col4 = new PdfPCell(
					new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getDiscountPercentage())));
			col4.setBorder(Rectangle.NO_BORDER);
			col4.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col5 = new PdfPCell(new Paragraph(String.valueOf(taxAmount)));
			col5.setBorder(Rectangle.NO_BORDER);
			col5.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col6 = new PdfPCell(new Paragraph(String.valueOf(df.format(rowTotal))));
			col6.setBorder(Rectangle.NO_BORDER);
			col6.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col7 = new PdfPCell(new Paragraph(""));
			col7.setBorder(Rectangle.NO_BORDER);
			col7.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(col1);
			table.addCell(col2);
			table.addCell(col3);
			table.addCell(col4);
			table.addCell(col5);
			table.addCell(col6);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
		}

		return table;
	}

	public List<InventoryVoucherHeader> getIvhConfirm(List<InventoryVoucherHeader> ivhINsalesOrderCormirm,
			List<InventoryVoucherDetail> ivhdetails) {

		for (InventoryVoucherHeader ivh : ivhINsalesOrderCormirm) {
			List<InventoryVoucherDetail> ivhdetails1 = new ArrayList<>();
			for (InventoryVoucherDetail ivhd : ivhdetails) {
				if (ivh.getId().equals(ivhd.getInventoryVoucherHeader().getId())) {
					ivhdetails1.add(ivhd);
					ivh.setInventoryVoucherDetails(ivhdetails1);
				}
			}
		}

		return ivhINsalesOrderCormirm;
	}

}
