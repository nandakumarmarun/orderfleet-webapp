package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.OrderStatusRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderItemDTO;
import com.orderfleet.webapp.web.rest.dto.VatLedgerDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooAccountProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooUser;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResultOdooAccountProfile;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class SendInvoiceOdooService {

	private final Logger log = LoggerFactory.getLogger(SendInvoiceOdooService.class);

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

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private OrderStatusRepository orderStatusRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Transactional
	public void sendSalesOrder() {
		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();

			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(
							documentIdList);
		} else {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusOrderByCreatedDateDesc();

			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateDesc(documentIdList);
		}
		log.debug("IVH size : {}", inventoryVoucherHeaders.size());

		if (inventoryVoucherHeaders.size() > 0) {

			Set<Long> ivhIds = new HashSet<>();
			Set<String> ivhPids = new HashSet<>();
			Set<Long> documentIds = new HashSet<>();
			Set<Long> employeeIds = new HashSet<>();
			Set<Long> receiverAccountProfileIds = new HashSet<>();
			Set<Long> supplierAccountProfileIds = new HashSet<>();
			Set<Long> priceLeveIds = new HashSet<>();
			Set<Long> userIds = new HashSet<>();
			Set<Long> orderStatusIds = new HashSet<>();
			Set<Long> exeIds = new HashSet<>();

			for (Object[] obj : inventoryVoucherHeaders) {

				ivhIds.add(Long.parseLong(obj[0].toString()));
				ivhPids.add(obj[9].toString());
				userIds.add(Long.parseLong(obj[12].toString()));
				documentIds.add(Long.parseLong(obj[13].toString()));
				employeeIds.add(Long.parseLong(obj[14].toString()));
				exeIds.add(Long.parseLong(obj[15].toString()));
				receiverAccountProfileIds.add(Long.parseLong(obj[16].toString()));
				supplierAccountProfileIds.add(Long.parseLong(obj[17].toString()));
				priceLeveIds.add(obj[18] != null ? Long.parseLong(obj[18].toString()) : 0);
				orderStatusIds.add(obj[23] != null ? Long.parseLong(obj[23].toString()) : 0);

			}

			List<Document> documents = documentRepository.findAllByCompanyIdAndIdsIn(documentIds);
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndIdsIn(employeeIds);
			List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndIdsIn(exeIds);
			List<AccountProfile> receiverAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);
			List<AccountProfile> supplierAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(supplierAccountProfileIds);
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdAndIdsIn(priceLeveIds);
			List<OrderStatus> orderStatusList = orderStatusRepository.findAllByCompanyIdAndIdsIn(orderStatusIds);
			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			Object[] errorPrint = null;
			try {
				for (Object[] obj : inventoryVoucherHeaders) {

					Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
							.findAny();

					Optional<Document> opDocument = documents.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[13].toString())).findAny();

					Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
							.filter(emp -> emp.getId() == Long.parseLong(obj[14].toString())).findAny();

					Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[15].toString())).findAny();

					Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

					Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();

					PriceLevel priceLevel = null;
					if (obj[18] != null) {

						Optional<PriceLevel> opPriceLevel = priceLevels.stream()
								.filter(pl -> pl.getId() == Long.parseLong(obj[18].toString())).findAny();
						if (opPriceLevel.isPresent()) {
							priceLevel = opPriceLevel.get();
						}
					}

					Optional<OrderStatus> opOrderStatus = orderStatusList.stream()
							.filter(os -> os.getId() == Long.parseLong(obj[23].toString())).findAny();

					OrderStatus orderStatus = new OrderStatus();
					if (opOrderStatus.isPresent()) {
						orderStatus = opOrderStatus.get();
					}

					SalesOrderDTO salesOrderDTO = ivhObjToSalesOrderDTO(obj, opUser.get(), opDocument.get(),
							opEmployeeProfile.get(), opExe.get(), opRecAccPro.get(), opSupAccPro.get(), priceLevel,
							orderStatus);

					salesOrderDTO.setAccountProfileDTO(
							accountProfileMapper.accountProfileToAccountProfileDTO(opRecAccPro.get()));

					List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
							.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
							.collect(Collectors.toList()).stream()
							.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId))
							.collect(Collectors.toList());

					List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
					for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {
						SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);

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

					// List<SalesOrderItemDTO> sortedSalesOrderItems = new
					// ArrayList<SalesOrderItemDTO>();
					// sortedSalesOrderItems =
					// salesOrderItemDTOs.stream().sorted(Comparator.comparingLong(SalesOrderItemDTO::getSortOrder)).collect(Collectors.toList());
					salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);

					inventoryHeaderPid.add(obj[9].toString());

					salesOrderDTOs.add(salesOrderDTO);
				}
			} catch (NoSuchElementException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				int i = 0;
				log.info("Error Printng");
				for (Object ob : errorPrint) {
					log.info(i + "-");
					if (ob != null) {
						log.info("--" + ob.toString());
					}
					i++;
				}
				e.printStackTrace();
				throw new IllegalArgumentException("Data missing in sales order..");
			}
//			if (!salesOrderDTOs.isEmpty()) {
//				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
//						TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
//				log.debug("updated " + updated + " to PROCESSING");
//			}
		}
		System.out.println(salesOrderDTOs.size() + "-----------Sales Order Size");

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	private SalesOrderDTO ivhObjToSalesOrderDTO(Object[] obj, User user, Document document,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			AccountProfile receiverAccountProfile, AccountProfile supplierAccountProfile, PriceLevel priceLevel,
			OrderStatus orderStatus) {
		log.info("ivh to salesorder starts");
		SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
		salesOrderDTO.setInventoryVoucherHeaderPid(obj[9].toString());
		salesOrderDTO.setId(Long.parseLong(obj[0].toString()));
		salesOrderDTO.setLedgerName(receiverAccountProfile.getName());
		salesOrderDTO.setTrimChar(receiverAccountProfile.getTrimChar());
		salesOrderDTO.setLedgerAddress(receiverAccountProfile.getAddress());
		LocalDateTime date = null;
		if (obj[4] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[4].toString().split(" ");
			date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		log.info("date split completed");
		salesOrderDTO.setDate(date);
		salesOrderDTO.setDocumentName(document.getName());
		salesOrderDTO.setDocumentAlias(document.getAlias());
		salesOrderDTO.setDocDiscountAmount(Double.parseDouble(obj[2].toString()));
		salesOrderDTO.setDocDiscountPercentage(Double.parseDouble(obj[3].toString()));
		if (receiverAccountProfile.getDefaultPriceLevel() != null) {
			salesOrderDTO.setPriceLevel(receiverAccountProfile.getDefaultPriceLevel().getName());
		}
		salesOrderDTO.setInventoryVoucherHeaderDTO(ivhObjectToivhDTO(obj, user, document, employeeProfile,
				executiveTaskExecution, receiverAccountProfile, supplierAccountProfile, priceLevel, orderStatus));
		log.info("function callss.....");
		salesOrderDTO.setLedgerState(receiverAccountProfile.getStateName());
		salesOrderDTO.setLedgerCountry(receiverAccountProfile.getCountryName());
		salesOrderDTO.setLedgerGstType(receiverAccountProfile.getGstRegistrationType());
		if (employeeProfile != null) {
			salesOrderDTO.setEmployeeAlias(employeeProfile.getAlias());
		}
		salesOrderDTO.setActivityRemarks(
				executiveTaskExecution.getRemarks() != null ? executiveTaskExecution.getRemarks() : "");
		return salesOrderDTO;
	}

	private InventoryVoucherHeaderDTO ivhObjectToivhDTO(Object[] obj, User user, Document document,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			AccountProfile receiverAccountProfile, AccountProfile supplierAccountProfile, PriceLevel priceLevel,
			OrderStatus orderStatus) {
		log.info("function starts .... ivhObjectToivhDTO");
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();

		inventoryVoucherHeaderDTO.setPid(obj[9].toString());
		inventoryVoucherHeaderDTO.setDocumentNumberLocal(obj[5].toString());
		inventoryVoucherHeaderDTO.setDocumentNumberServer(obj[6].toString());

		if (document != null) {
			inventoryVoucherHeaderDTO.setDocumentPid(document.getPid());
			inventoryVoucherHeaderDTO.setDocumentName(document.getName());
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime createdDate = null;
		LocalDateTime documentDate = null;
		if (obj[1] != null) {
			String[] splitDate = obj[1].toString().split(" ");
			createdDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		if (obj[4] != null) {
			String[] splitDate = obj[4].toString().split(" ");
			documentDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		log.info("date parsing completed");
		inventoryVoucherHeaderDTO.setCreatedDate(createdDate);
		inventoryVoucherHeaderDTO.setDocumentDate(documentDate);

		if (receiverAccountProfile != null) {
			inventoryVoucherHeaderDTO.setReceiverAccountPid(receiverAccountProfile.getPid());
			inventoryVoucherHeaderDTO.setReceiverAccountName(receiverAccountProfile.getName());
		}

		inventoryVoucherHeaderDTO.setProcessStatus(obj[22].toString());

		if (supplierAccountProfile != null) {
			inventoryVoucherHeaderDTO.setSupplierAccountPid(supplierAccountProfile.getPid());
			inventoryVoucherHeaderDTO.setSupplierAccountName(supplierAccountProfile.getName());
		}

		if (employeeProfile != null) {
			inventoryVoucherHeaderDTO.setEmployeePid(employeeProfile.getPid());
			inventoryVoucherHeaderDTO.setEmployeeName(employeeProfile.getName());
		}
		if (user != null) {
			inventoryVoucherHeaderDTO.setUserName(user.getFirstName());
		}

		inventoryVoucherHeaderDTO.setDocumentTotal(Double.parseDouble(obj[7].toString()));
		inventoryVoucherHeaderDTO.setDocumentVolume(Double.parseDouble(obj[8].toString()));
		inventoryVoucherHeaderDTO.setDocDiscountAmount(Double.parseDouble(obj[2].toString()));
		inventoryVoucherHeaderDTO.setDocDiscountPercentage(Double.parseDouble(obj[3].toString()));
		inventoryVoucherHeaderDTO.setStatus(Boolean.valueOf(obj[10].toString()));

		if (priceLevel != null) {
			inventoryVoucherHeaderDTO.setPriceLevelPid(priceLevel.getPid());
			inventoryVoucherHeaderDTO.setPriceLevelName(priceLevel.getName());
		}
		if (orderStatus != null) {
			inventoryVoucherHeaderDTO.setOrderStatusId(orderStatus.getId());
			inventoryVoucherHeaderDTO.setOrderStatusName(orderStatus.getName());
		}

		if (obj[26] != null) {
			inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(obj[26].toString()));

		}
		log.info("setOrderNumber obj[27]");
		inventoryVoucherHeaderDTO.setOrderNumber(obj[27] == null ? 0 : Long.parseLong(obj[27].toString()));

		inventoryVoucherHeaderDTO.setCustomeraddress(receiverAccountProfile.getAddress());
		inventoryVoucherHeaderDTO.setCustomerEmail(receiverAccountProfile.getEmail1());
		inventoryVoucherHeaderDTO.setCustomerPhone(receiverAccountProfile.getPhone1());
		inventoryVoucherHeaderDTO.setVisitRemarks(
				executiveTaskExecution.getRemarks() == null ? "" : executiveTaskExecution.getRemarks());

		inventoryVoucherHeaderDTO.setPdfDownloadStatus(Boolean.getBoolean(obj[28].toString()));
		if (obj[29] != null) {
			inventoryVoucherHeaderDTO.setSalesManagementStatus(SalesManagementStatus.valueOf(obj[29].toString()));

		}
		inventoryVoucherHeaderDTO.setDocumentTotalUpdated(Double.parseDouble(obj[30].toString()));
		inventoryVoucherHeaderDTO.setDocumentVolumeUpdated(Double.parseDouble(obj[31].toString()));
		inventoryVoucherHeaderDTO.setUpdatedStatus(Boolean.getBoolean(obj[32].toString()));
		if (inventoryVoucherHeaderDTO.getUpdatedStatus()) {
			inventoryVoucherHeaderDTO.setDocumentTotal(inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
			inventoryVoucherHeaderDTO.setDocumentVolume(inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
		}
		return inventoryVoucherHeaderDTO;
	}

}
