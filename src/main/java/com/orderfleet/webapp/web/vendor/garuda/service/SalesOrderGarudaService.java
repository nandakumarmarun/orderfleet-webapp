package com.orderfleet.webapp.web.vendor.garuda.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.garuda.dto.SalesOrderDetailGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.SalesOrderGarudaDTO;
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
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

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
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(
							documentIdList);
		} else {
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

			List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndIdsIn(employeeIds);

			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);

			for (Object[] obj : inventoryVoucherHeaders) {

				Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

				Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
						.filter(emp -> emp.getId() == Long.parseLong(obj[14].toString())).findAny();

				SalesOrderGarudaDTO garudaInvoice = new SalesOrderGarudaDTO();

				LocalDateTime date = null;
				if (obj[4] != null) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					String[] splitDate = obj[4].toString().split(" ");
					date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

				}

				DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

				garudaInvoice.setInvoiceNo(obj[6] != null ? obj[6].toString() : "");
				garudaInvoice.setPid(obj[9] != null ? obj[9].toString() : "");
				garudaInvoice.setCustomerCode(
						opRecAccPro.get().getCustomerId() != null ? opRecAccPro.get().getCustomerId() : "");
				garudaInvoice.setEmployeeName(opEmployeeProfile.get().getName());
				garudaInvoice.setInvoiceDate(date.format(formatter1));
				garudaInvoice.setTotal(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
				
				if (opRecAccPro.get() != null) {
					garudaInvoice.setOrderType(opRecAccPro.get().getDefaultPriceLevel().getName());
				}
				

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
					garudaInvoiceDetail.setMrp(inventoryVoucherDetail.getMrp());
					garudaInvoiceDetail.setDiscPer(inventoryVoucherDetail.getDiscountPercentage());
					garudaInvoiceDetail.setTaxPer(inventoryVoucherDetail.getTaxPercentage());
					garudaInvoiceDetail.setFreeQuantity(inventoryVoucherDetail.getFreeQuantity());
					garudaInvoiceDetail.setQuantity(inventoryVoucherDetail.getQuantity());

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
