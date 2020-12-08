package com.orderfleet.webapp.web.vendor.garuda.service;

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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.garuda.dto.SalesOrderDetailGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.SalesOrderGarudaDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoiceLine;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ApiResponseDataSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.SalesOrderItemDetailsSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.SalesOrderMasterSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.SendTransactionSapPraveshService;

@Service
public class SalesOrderGarudaService {
	
	private final Logger log = LoggerFactory.getLogger(SendTransactionSapPraveshService.class);
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;
	
	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	@Transactional
	public List<SalesOrderGarudaDTO> sendSalesOrder() {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<SalesOrderGarudaDTO> garudaInvoicesList = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				company.getId());

		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<String> inventoryHeaderPids = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
		inventoryVoucherHeaders =
			inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(documentIdList);

//			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
//					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateAscLimit(
//							documentIdList);
		} else {
			inventoryVoucherHeaders =
			 inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateDesc(documentIdList);

//			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
//					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateAscLimit(documentIdList);
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
				inventoryHeaderPids.add(obj[9].toString());
				userIds.add(Long.parseLong(obj[12].toString()));
				documentIds.add(Long.parseLong(obj[13].toString()));
				employeeIds.add(Long.parseLong(obj[14].toString()));
				exeIds.add(Long.parseLong(obj[15].toString()));
				receiverAccountProfileIds.add(Long.parseLong(obj[16].toString()));
				supplierAccountProfileIds.add(Long.parseLong(obj[17].toString()));
				priceLeveIds.add(obj[18] != null ? Long.parseLong(obj[18].toString()) : 0);
				orderStatusIds.add(obj[23] != null ? Long.parseLong(obj[23].toString()) : 0);

			}

			if (!ivhPids.isEmpty()) {
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPids);
				log.debug("updated " + updated + " to PROCESSING");
			}

			List<AccountProfile> receiverAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);

//			List<AccountProfile> supplierAccountProfiles = accountProfileRepository
//					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);

			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);

			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);
			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();

			for (Object[] obj : inventoryVoucherHeaders) {

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
						.findAny();

				Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

				Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

				SalesOrderGarudaDTO garudaInvoice = new SalesOrderGarudaDTO();

				LocalDateTime date = null;
				if (obj[4] != null) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					String[] splitDate = obj[4].toString().split(" ");
					date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

				}

				DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				garudaInvoice.setInvoiceDate(date.format(formatter1));
				garudaInvoice.setTotal(obj[34] != null ? Double.parseDouble(obj[34].toString()) : 0.0);

				List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
						.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId)).collect(Collectors.toList());

				List<SalesOrderDetailGarudaDTO> garudaInvoiceDetailList = new ArrayList<SalesOrderDetailGarudaDTO>();
				for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {

					SalesOrderDetailGarudaDTO garudaInvoiceDetail = new SalesOrderDetailGarudaDTO();

					garudaInvoiceDetail.setDiscPrice(inventoryVoucherDetail.getDiscountAmount());
					garudaInvoiceDetail.setProductCode((inventoryVoucherDetail.getProduct().getProductId() != null
							&& !inventoryVoucherDetail.getProduct().getProductId().equals("")
									? inventoryVoucherDetail.getProduct().getProductId()
									: ""));
					garudaInvoiceDetail.setRate(inventoryVoucherDetail.getSellingRate());
					garudaInvoiceDetail.setQuantity(inventoryVoucherDetail.getQuantity());

					Optional<UnitOfMeasureProduct> opUnitOfMeasure = unitOfMeasureProducts.stream()
							.filter(us -> us.getProduct().getPid().equals(inventoryVoucherDetail.getProduct().getPid()))
							.findAny();


					garudaInvoiceDetailList.add(garudaInvoiceDetail);
				}

				garudaInvoice.setSalesOrderdetails(garudaInvoiceDetailList);

				// inventoryHeaderPids.add(obj[9].toString());

				garudaInvoicesList.add(garudaInvoice);
			}

			log.info("Sending (" + garudaInvoicesList.size() + ") Invoices to Garuda....");
		}
		return garudaInvoicesList;
	}

}
