package com.orderfleet.webapp.web.rest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.hibernate.service.spi.ServiceException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;
import com.itextpdf.text.Chunk;
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
import com.itextpdf.text.pdf.codec.Base64.OutputStream;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.FilledFormService;
import com.orderfleet.webapp.service.InventoryVoucherDetailService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.FormFileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherXlsDownloadDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooAccountProfile;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderItemDetailsSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderMasterSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderResponseDataSap;

/**
 * Web controller for managing InventoryVoucher.
 * 
 * @author Muhammed Riyas T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class ProcessFlowStage1Resource {

	private final Logger log = LoggerFactory.getLogger(ProcessFlowStage1Resource.class);

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
	private UserMenuItemService userMenuItemService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private DocumentRepository documentRepository;

	/**
	 * GET /primary-sales-performance : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/process-flow-stage-1", method = RequestMethod.GET)
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
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("menuItemLabel", userMenuItemService.findMenuItemLabelView("/web/process-flow-stage-1"));

		return "company/processFlowStage1";
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/process-flow-stage-1/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		Optional<InventoryVoucherHeaderDTO> optionalInventoryVoucherHeaderDTO = inventoryVoucherService
				.findOneByPid(pid);
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
					ivd.setItemtype(ivd.getItemtype() != null ? " (" + ivd.getItemtype() + ")" : " (MT)");
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

			inventoryVoucherDTO.setDocumentVolume(ivTotalVolume);
			return new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/process-flow-stage-1/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<SalesPerformanceDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("processFlowStatus") String processFlowStatus, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam("documentPids") List<String> documentPids,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		if (documentPids.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(ProcessFlowStage1Resource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(ProcessFlowStage1Resource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(ProcessFlowStage1Resource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(ProcessFlowStage1Resource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = fDate;
		} else if (filterBy.equals("UPTO90DAYS")) {
			tDate = LocalDate.now();
			Period days_90 = Period.ofDays(90);
			fDate = tDate.minus(days_90);
		}
		List<SalesPerformanceDTO> salesPerformanceDTOs = getFilterData(employeePids, documentPids, processFlowStatus,
				accountPid, fDate, tDate);
		return new ResponseEntity<>(salesPerformanceDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/process-flow-stage-1/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getDynamicDocumentImages(@PathVariable String pid) {
		log.debug("Web request to get DynamicDocument images by pid : {}", pid);
		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderPid(pid);
		List<FormFileDTO> formFileDTOs = new ArrayList<>();
		if (filledForms.size() > 0) {
			for (FilledForm filledForm : filledForms)
				if (filledForm.getFiles().size() > 0) {
					FormFileDTO formFileDTO = new FormFileDTO();
					formFileDTO.setFormName(filledForm.getForm().getName());
					formFileDTO.setFiles(new ArrayList<>());
					Set<File> files = filledForm.getFiles();
					for (File file : files) {
						FileDTO fileDTO = new FileDTO();
						fileDTO.setFileName(file.getFileName());
						fileDTO.setMimeType(file.getMimeType());
						java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
						if (physicalFile.exists()) {
							try {
								fileDTO.setContent(Files.toByteArray(physicalFile));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						formFileDTO.getFiles().add(fileDTO);
					}
					formFileDTOs.add(formFileDTO);
				}
		}
		return new ResponseEntity<>(formFileDTOs, HttpStatus.OK);
	}

	private List<SalesPerformanceDTO> getFilterData(List<String> employeePids, List<String> documentPids,
			String processFlowStatus, String accountPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		if (documentPids != null && documentPids.size() >0) {
			if (documentPids.get(0).equals("all")) { // check docType all
				List<VoucherType> docTypes = primarySecondaryDocumentService.findAllVoucherTypesByCompanyId();
				documentPids = primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherTypeIn(docTypes)
						.stream().map(vo -> vo.getPid()).collect(Collectors.toList());
			}
		}

		List<ProcessFlowStatus> processStatus = null;

		switch (processFlowStatus) {
		case "DEFAULT":
			processStatus = Arrays.asList(ProcessFlowStatus.DEFAULT);
			break;
		case "PO_PLACED":
			processStatus = Arrays.asList(ProcessFlowStatus.PO_PLACED);
			break;
		case "IN_STOCK":
			processStatus = Arrays.asList(ProcessFlowStatus.IN_STOCK);
			break;
		case "PO_ACCEPTED_AT_TSL":
			processStatus = Arrays.asList(ProcessFlowStatus.PO_ACCEPTED_AT_TSL);
			break;
		case "UNDER_PRODUCTION":
			processStatus = Arrays.asList(ProcessFlowStatus.UNDER_PRODUCTION);
			break;
		case "READY_TO_DISPATCH_AT_TSL":
			processStatus = Arrays.asList(ProcessFlowStatus.READY_TO_DISPATCH_AT_TSL);
			break;
		case "READY_TO_DISPATCH_AT_PS":
			processStatus = Arrays.asList(ProcessFlowStatus.READY_TO_DISPATCH_AT_PS);
			break;
		case "INSTOCK_READYATTSL":
			processStatus = Arrays.asList(ProcessFlowStatus.IN_STOCK, ProcessFlowStatus.READY_TO_DISPATCH_AT_TSL);
			break;
		case "READYATPS_NOTDELIVERED":
			processStatus = Arrays.asList(ProcessFlowStatus.READY_TO_DISPATCH_AT_PS, ProcessFlowStatus.NOT_DELIVERED);
			break;
		case "DELIVERED_INSTALLATIONPLANED":
			processStatus = Arrays.asList(ProcessFlowStatus.DELIVERED, ProcessFlowStatus.INSTALLATION_PLANNED);
			break;
		case "INSTALLED":
			processStatus = Arrays.asList(ProcessFlowStatus.INSTALLED);
			break;
		case "ALL":
			processStatus = Arrays.asList(ProcessFlowStatus.DEFAULT, ProcessFlowStatus.PO_PLACED,
					ProcessFlowStatus.IN_STOCK, ProcessFlowStatus.PO_ACCEPTED_AT_TSL,
					ProcessFlowStatus.UNDER_PRODUCTION, ProcessFlowStatus.READY_TO_DISPATCH_AT_TSL,
					ProcessFlowStatus.READY_TO_DISPATCH_AT_PS, ProcessFlowStatus.DELIVERED,
					ProcessFlowStatus.NOT_DELIVERED, ProcessFlowStatus.INSTALLATION_PLANNED,
					ProcessFlowStatus.INSTALLED);
			break;
		}

		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndProcessFlowStatusStatusAndDateBetweenAndRejectedStatusOrderByCreatedDateDesc(
							userIds, documentPids, processStatus, fromDate, toDate, false);
		} else {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndProcessFlowStatusAndDateBetweenAndRejectedStatusOrderByCreatedDateDesc(
							userIds, accountPid, documentPids, processStatus, fromDate, toDate, false);
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

		Set<Long> executiveTaskIds = inventoryVouchers.parallelStream().map(obj -> Long.valueOf(obj[30].toString()))
				.collect(Collectors.toSet());

		List<Object[]> objeDynamicDocuments = dynamicDocumentHeaderRepository
				.findAllByExecutiveTaskExecutionIdsIn(executiveTaskIds);
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
			salesPerformanceDTO.setEditOrder(orderEdit);
			salesPerformanceDTO.setSendSalesOrderSapButtonStatus(sendSalesOrderSapButtonStatus);
			salesPerformanceDTO.setProcessFlowStatus(ProcessFlowStatus.valueOf(ivData[26].toString()));
			salesPerformanceDTO.setPaymentReceived((double) ivData[27]);

			salesPerformanceDTO.setBookingId(ivData[28] != null ? ivData[28].toString() : "");

			if (ivData[29] != null) {
				LocalDate currentdate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = ivData[29].toString();
				LocalDate deliveryDate = LocalDate.parse(date, formatter);
				salesPerformanceDTO.setDeliveryDate(deliveryDate.toString());

				long noOfDaysBetween = ChronoUnit.DAYS.between(currentdate, deliveryDate);

				salesPerformanceDTO.setDeliveryDateDifference(noOfDaysBetween);
			} else {
				salesPerformanceDTO.setDeliveryDate("");
			}

			if (ivData[32] != null) {
				salesPerformanceDTO.setRemarks(ivData[32].toString().equals("") ? "" : ivData[32].toString());
			} else {
				salesPerformanceDTO.setRemarks("");
			}

			if (ivData[33] != null) {
				salesPerformanceDTO
						.setReceiverAccountPhone(ivData[33].toString().equals("") ? "" : ivData[33].toString());
			} else {
				salesPerformanceDTO.setReceiverAccountPhone("");
			}

			if (objeDynamicDocuments.size() > 0) {
				Optional<Object[]> dynamicDoc = objeDynamicDocuments.stream()
						.filter(o -> o[1].toString().equals(ivData[30].toString())).findAny();

				if (dynamicDoc.isPresent()) {
					salesPerformanceDTO.setDynamicDocumentPid(dynamicDoc.get()[0].toString());
					salesPerformanceDTO.setImageButtonVisible(true);
				}
			} else {
				salesPerformanceDTO.setImageButtonVisible(false);
			}
			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}

	@RequestMapping(value = "/process-flow-stage-1/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/process-flow-stage-1/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
		String[] headerColumns = { "Order No", "Salesman", "Order Date", "Customer", "Supplier", "Product Name",
				"Quantity", "Unit Quantity", "Total Quantity", "Free Quantity", "Selling Rate", "Discount Amount",
				"Discount Percentage", "Tax Amount", "Row Total" };
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
				row.createCell(4).setCellValue(ivh.getSupplierAccountName());

				row.createCell(5).setCellValue(ivd.getProductName());
				row.createCell(6).setCellValue(ivd.getQuantity());
				row.createCell(7).setCellValue(ivd.getProductUnitQty());
				row.createCell(8).setCellValue((ivd.getQuantity() * ivd.getProductUnitQty()));
				row.createCell(9).setCellValue(ivd.getFreeQuantity());
				row.createCell(10).setCellValue(ivd.getSellingRate());
				row.createCell(11).setCellValue(ivd.getDiscountAmount());
				row.createCell(12).setCellValue(ivd.getDiscountPercentage());
				row.createCell(13).setCellValue(ivd.getTaxAmount());
				row.createCell(14).setCellValue(ivd.getRowTotal());
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

	@RequestMapping(value = "/process-flow-stage-1/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		log.info("Sales Tally Download Status " + tallyDownloadStatus);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/process-flow-stage-1/updateInventory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> updateInventoryDetail(@RequestParam long id,
			@RequestParam long quantity, @RequestParam String ivhPid) {
		System.out.println("------------------------------------------------------------------------");
		System.out.println("quantity : " + quantity + "\n Id :" + id + " \n IVHPid : " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
		List<InventoryVoucherDetailDTO> ivdList = inventoryVoucherHeaderDTO.getInventoryVoucherDetails();
		Optional<InventoryVoucherDetailDTO> opIvDetail = ivdList.stream().filter(ivd -> ivd.getDetailId() == id)
				.findAny();

		if (opIvDetail.isPresent()) {
			InventoryVoucherDetailDTO ivdDto = opIvDetail.get();
			// ivdDto.setUpdatedQty(quantity);
			double discAmt = ivdDto.getDiscountAmount();
			double discPer = ivdDto.getDiscountPercentage();
			double sellingRate = ivdDto.getSellingRate();
			double taxPer = ivdDto.getTaxPercentage();
			sellingRate = sellingRate - discAmt;
			sellingRate = sellingRate - (sellingRate * (discPer * 0.01));
			double rowTotal = sellingRate * quantity;
			double totalTax = rowTotal * (taxPer * 0.01);
			double updatedRowTotal = totalTax + rowTotal;

			double rowTotalDiff = 0.0;
			double quantityDiff = 0.0;
			if (ivdDto.getUpdatedStatus()) {
				rowTotalDiff = Math.abs(ivdDto.getUpdatedRowTotal() - updatedRowTotal);

				quantityDiff = Math.abs(ivdDto.getUpdatedQty() - quantity);

				System.out.println("updated : " + ivdDto.getUpdatedStatus());
				System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
			} else {
				rowTotalDiff = Math.abs(ivdDto.getRowTotal() - updatedRowTotal);

				quantityDiff = Math.abs(ivdDto.getQuantity() - quantity);

				System.out.println("updated : " + ivdDto.getUpdatedStatus());
				System.out.println("rowTotatlDiff : " + rowTotalDiff + "\n quantityDiff : " + quantityDiff);
			}

			double updatedDocTotal = 0.0;
			double updatedDocVol = 0.0;
			if (inventoryVoucherHeaderDTO.getUpdatedStatus()) {
				if (ivdDto.getUpdatedStatus()) {
					if (ivdDto.getUpdatedQty() <= quantity) {
						updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + quantityDiff;
					} else {
						updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
					}

					if (ivdDto.getUpdatedRowTotal() <= updatedRowTotal) {
						updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotalUpdated() + rowTotalDiff;
					} else {
						updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
					}
				} else {
					if (ivdDto.getQuantity() <= quantity) {
						updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + quantityDiff;
					} else {
						updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
					}

					if (ivdDto.getRowTotal() <= updatedRowTotal) {
						updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotalUpdated() + rowTotalDiff;
					} else {
						updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
					}
				}

				System.out.println("updated... : TRUE");
				System.out.println("updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
			} else {

				if (ivdDto.getQuantity() <= quantity) {
					updatedDocVol = inventoryVoucherHeaderDTO.getDocumentVolume() + quantityDiff;
				} else {
					updatedDocVol = Math.abs(quantityDiff - inventoryVoucherHeaderDTO.getDocumentVolume());
				}

				if (ivdDto.getRowTotal() <= updatedRowTotal) {
					updatedDocTotal = inventoryVoucherHeaderDTO.getDocumentTotal() + rowTotalDiff;
				} else {
					updatedDocTotal = Math.abs(rowTotalDiff - inventoryVoucherHeaderDTO.getDocumentTotal());
				}

				System.out.println("updated... : FALSE");
				System.out.println("updatedDocTotal : " + updatedDocTotal + "\n updatedDocVol : " + updatedDocVol);
			}

			ivdDto.setUpdatedStatus(true);
			ivdDto.setUpdatedRowTotal(updatedRowTotal);
			ivdDto.setUpdatedQty(quantity);

			inventoryVoucherHeaderDTO.setDocumentTotalUpdated(updatedDocTotal);
			inventoryVoucherHeaderDTO.setDocumentVolumeUpdated(updatedDocVol);
			inventoryVoucherHeaderDTO.setUpdatedStatus(true);
			System.out.println("IVH : Volume : " + inventoryVoucherHeaderDTO.getDocumentVolume() + "\n");
			System.out.println("IVH : Volume Updated : " + inventoryVoucherHeaderDTO.getDocumentVolumeUpdated() + "\n");
			System.out.println("------------------------------------------------------------------------");
			inventoryVoucherDetailService.updateInventoryVoucherDetail(ivdDto);
			inventoryVoucherService.updateInventoryVoucherHeader(inventoryVoucherHeaderDTO);

		}

		// inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(inventoryVoucherHeaderDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/process-flow-stage-1/changeProcessFlowStatus", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> changeProcessFlowStatus(@RequestParam String pid,
			@RequestParam ProcessFlowStatus processFlowStatus) {
		log.info("Process Flow Status " + processFlowStatus);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setProcessFlowStatus(processFlowStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderProcessFlowStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/updatePaymentReceived", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updatePaymentReceived(@RequestParam String ivhPid,
			@RequestParam long paymentReceived) {
		log.info("update Payment Received " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
		inventoryVoucherHeaderDTO.setPaymentReceived((double) paymentReceived);
		inventoryVoucherService.updateInventoryVoucherHeaderPaymentReceived(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/updateBookingId", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updateBookingId(@RequestParam String ivhPid, @RequestParam String bookingId) {
		log.info("update Booking Id " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
		inventoryVoucherHeaderDTO.setBookingId(bookingId);
		inventoryVoucherService.updateInventoryVoucherHeaderBookingId(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/updateRemarks", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updateRemarks(@RequestParam String ivhPid, @RequestParam String remarks) {
		log.info("update remarks " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
		inventoryVoucherHeaderDTO.setRemarks(remarks);
		inventoryVoucherService.updateInventoryVoucherHeaderRemarks(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/reject", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> reject(@RequestParam String ivhPid) {
		log.info("update reject status " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();
		inventoryVoucherHeaderDTO.setRejectedStatus(true);
		inventoryVoucherService.updateInventoryVoucherHeaderRejectedStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/updateDeliveryDate", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updateDeliveryDate(@RequestParam String ivhPid, @RequestParam String deliveryDate) {
		log.info("update Delivery Date " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();

		// LocalDate dDate = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dDate = LocalDate.parse(deliveryDate, formatter);
		inventoryVoucherHeaderDTO.setDeliveryDate(dDate);
		inventoryVoucherService.updateInventoryVoucherHeaderDeliveryDate(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/updateAll", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> updateAll(@RequestParam String ivhPid, @RequestParam String bookingId,
			@RequestParam long paymentReceived, @RequestParam String deliveryDate) {
		log.info("update Booking Id,deliveryDate,paymentReceived " + ivhPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid).get();

		if (bookingId.equals("")) {
			Optional<com.orderfleet.webapp.domain.Document> document = documentRepository
					.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(),
							"Sales Order Questions");

			if (document.isPresent()) {
				long executiveTaskExectionId = inventoryVoucherHeaderRepository
						.findExecutiveTaskExecutionIdByPId(ivhPid);
				List<Object[]> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
						.findByExecutiveTaskExecutionIdAndDocumentPid(executiveTaskExectionId, document.get().getPid());

				if (dynamicDocumentHeaders.size() > 0) {
					Object[] obj = dynamicDocumentHeaders.get(0);

					List<FilledForm> filledForms = filledFormRepository
							.findByDynamicDocumentHeaderPid(obj[0].toString());

					for (FilledForm filledForm : filledForms) {

						List<FilledFormDetail> filledFormDetails = filledForm.getFilledFormDetails();

						for (FilledFormDetail filledFormDetail : filledFormDetails) {

							if (filledFormDetail.getFormElement().getName().equals("Booking ID")) {
								bookingId = filledFormDetail.getValue();
							}
						}
					}
				}
			}
		}
		inventoryVoucherHeaderDTO.setBookingId(bookingId);

		if (!deliveryDate.equals("")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dDate = LocalDate.parse(deliveryDate, formatter);
			inventoryVoucherHeaderDTO.setDeliveryDate(dDate);
		}
		inventoryVoucherHeaderDTO.setPaymentReceived((double) paymentReceived);
		inventoryVoucherService
				.updateInventoryVoucherHeaderBookingIdDeliveryDateAndPaymentReceived(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@RequestMapping(value = "/process-flow-stage-1/downloadSalesOrderSap", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> downloadSalesOrderSap(@RequestParam String inventoryPid)
			throws IOException {

		log.info("Download to Sap with pid " + inventoryPid);

		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(inventoryPid).get();

		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus().equals(TallyDownloadStatus.PENDING)
				&& inventoryVoucherHeaderDTO.getSalesManagementStatus().equals(SalesManagementStatus.APPROVE)) {
			log.info("Downloading to sap.prabhu..............");

			SalesOrderResponseDataSap salesOrderResponseDataSap = sendSalesOrdertoSap(inventoryVoucherHeaderDTO);

			log.info("Response Data: " + salesOrderResponseDataSap);

			inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.PROCESSING);

			if (salesOrderResponseDataSap.getStatusCode() == 0) {
				inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
			}

			if (salesOrderResponseDataSap.getStatusCode() == 1) {
				inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.PENDING);
			}

			inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);

		}

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
					: "MT";
			if (opPiecesToQuantity.isPresent()) {
				if (opPiecesToQuantity.get().getValue().equals("true")) {

					if (inventoryVoucherDetailDTO.getProductSKU() != null
							&& inventoryVoucherDetailDTO.getProductSKU().equalsIgnoreCase("MT")) {
						quantity = (quantity * inventoryVoucherDetailDTO.getProductUnitQty()) / 1000; // Quantity into
																										// MT;
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
			salesOrderItemDetailsSap.setItemtype("MT");
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

	@RequestMapping(value = "/process-flow-stage-1/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
					inventoryVoucherHeaderRepository.updatePdfDownlodStatusByPid(inventoryVoucherHeaderDTO.getPid());

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

}
