package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Web controller for managing ProductIntakeReport.
 * 
 * @author Muhammed Riyas T
 * @since Mar 03, 2017
 */
@Controller
@RequestMapping("/web")
public class ProductIntakeReportResource {

	private final Logger log = LoggerFactory.getLogger(ProductIntakeReportResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
	
	/**
	 * GET /product-intake-report : get productIntakeReport.
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/product-intake-report", method = RequestMethod.GET)
	public String getProductIntakeReport(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of accounting vouchers");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/productIntakeReport";
	}

	@Timed
	@RequestMapping(value = "/product-intake-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Map<String, Double>>> filterProductIntakeReport(
			@RequestParam(value = "employeePid") String employeePid,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter product in take report");
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
		LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
		LocalDate toDateTime = LocalDate.parse(toDate, formatter);
		return new ResponseEntity<>(getFilterData(userPid, fromDateTime, toDateTime), HttpStatus.OK);
	}

	private Map<String, Map<String, Double>> getFilterData(String userPid, LocalDate fDate, LocalDate tDate) {

		LocalDateTime sDate = fDate.atTime(0, 0);
		LocalDateTime eDate = tDate.atTime(23, 59);
		String total="Total";
		List<Location> locations = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
		if (locations.size() > 0) {
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAndAccountProfileActivated(locations);
			if (accountProfiles.size() > 0) {
				List<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository.findAllByCompanyIdAndActivatedOrDeactivatedEcomProductProfile(true);
				if (ecomProductProfiles.size() > 0) {
					// find ecom products
					Map<String, List<ProductProfile>> mapProducts = new LinkedHashMap<>();
					for (EcomProductProfile ecomProductProfile : ecomProductProfiles) {
						List<ProductProfile> products = ecomProductProfileProductRepository
								.findProductByEcomProductProfilePid(ecomProductProfile.getPid());
						mapProducts.put(ecomProductProfile.getName(), products);
					}

					// find performance document
					List<Document> documents = null;
					DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			        DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description1 ="get by compId and name";
					LocalDateTime startLCTime1 = LocalDateTime.now();
					String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
					String startDate1 = startLCTime1.format(DATE_FORMAT1);
					logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
					Optional<CompanyConfiguration> performanceConfig = companyConfigurationRepository
							.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(),
									CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);
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
					if (performanceConfig.isPresent()) {
						documents = primarySecondaryDocumentRepository.findDocumentsByVoucherTypeAndCompanyId(
								VoucherType.valueOf(performanceConfig.get().getValue()));
					} else {
						documents = primarySecondaryDocumentRepository
								.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);
					}

					if (documents.size() > 0) {
						List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetailRepository
								.findByDocumentInAndHeaderDocumentDateBetween(documents, sDate, eDate);
						if (ivDetails.size() > 0) {
							Map<String, Map<String, Double>> mapAccounts = new LinkedHashMap<>();
							Map<String, Double> ecomProductQuantity = new LinkedHashMap<>();
							for (Map.Entry<String, List<ProductProfile>> ecomProduct : mapProducts.entrySet()) {
								Double quantitys = 0d;
								for (AccountProfile accountProfile : accountProfiles) {
									Double quantity = 0d;
									if (ecomProduct.getValue().size() > 0) {
										quantity = getSumOfQuantity(accountProfile.getPid(), ecomProduct.getValue(),
												ivDetails);
									}
									quantitys=quantitys+quantity;
								}
								ecomProductQuantity.put(ecomProduct.getKey(), quantitys);
							}
							mapAccounts.put(total, ecomProductQuantity);
							for (AccountProfile accountProfile : accountProfiles) {
								Map<String, Double> ecomProductQuantity1 = new LinkedHashMap<>();
								
								for (Map.Entry<String, List<ProductProfile>> ecomProduct : mapProducts.entrySet()) {
									Double quantity = 0d;
									if (ecomProduct.getValue().size() > 0) {
										quantity = getSumOfQuantity(accountProfile.getPid(), ecomProduct.getValue(),
												ivDetails);
									}
									ecomProductQuantity1.put(ecomProduct.getKey(), quantity);
								}
								
								mapAccounts.put(accountProfile.getName(), ecomProductQuantity1);
								
							}
							
							return mapAccounts;
						}
					}
				}
			}
		}
		return null;
	}

	private Double getSumOfQuantity(String accountPid, List<ProductProfile> productProfiles,
			List<InventoryVoucherDetail> ivDetails) {
		double sumofQty = ivDetails.parallelStream().filter(ivd -> {
			if (ivd.getInventoryVoucherHeader().getExecutiveTaskExecution().getAccountProfile().getPid()
					.equals(accountPid) && productProfiles.stream().anyMatch(p -> p.equals(ivd.getProduct()))) {
				return true;
			} else {
				return false;
			}
		}).collect(Collectors.summingDouble(iv -> iv.getQuantity()));
		return sumofQty;
	}

}
