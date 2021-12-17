package com.orderfleet.webapp.web.rest.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.PurchaseHistoryConfig;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.PurchaseHistoryConfigRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDate;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryStringDTO;

@RestController
@RequestMapping("/api")
public class PurchaseHistoryController {

	private final Logger log = LoggerFactory.getLogger(PurchaseHistoryController.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PurchaseHistoryConfigRepository purchaseHistoryConfigRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	/**
	 * GET /ecom-product-purchase-history
	 */
	@Timed
	@Transactional(readOnly = true)
	@GetMapping("/ecom-product-purchase-history")
	public ResponseEntity<List<PurchaseHistoryStringDTO>> getEcomPurchaseHistory(@RequestParam String accountPid) {
		return new ResponseEntity<>(purchaseHistory(accountPid), HttpStatus.OK);
	}

	private List<PurchaseHistoryStringDTO> purchaseHistory(String accountPid) {

		List<PurchaseHistoryStringDTO> purchaseHistoryStringDTOs = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		if (company.getPeriodStartDate() == null || company.getPeriodEndDate() == null) {
			log.info("Company financial period not configure....");
			return purchaseHistoryStringDTOs;
		}

		// find Purchase History Configs
		List<PurchaseHistoryConfig> purchaseHistoryConfigs = purchaseHistoryConfigRepository.findAllByCompanyId();
		if (purchaseHistoryConfigs.size() == 0) {
			log.info("Please Configure Purchase History Settings....");
			return purchaseHistoryStringDTOs;
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
		Optional<CompanyConfiguration> performanceConfig = companyConfigurationRepository.findByCompanyIdAndName(
				SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);
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
			documents = primarySecondaryDocumentRepository
					.findDocumentsByVoucherTypeAndCompanyId(VoucherType.valueOf(performanceConfig.get().getValue()));
		} else {
			documents = primarySecondaryDocumentRepository
					.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);
		}
		if (documents.size() == 0) {
			log.info("Performance documents not configure....");
			return purchaseHistoryStringDTOs;
		}

		// find periods from Purchase History Config
		List<PurchaseHistoryConfigDate> dates = findDatesFromPurchaseHistoryConfigs(purchaseHistoryConfigs, company);

		Map<String, List<Value>> mapEcomProducts = new HashMap<>();
		for (PurchaseHistoryConfigDate purchaseHistoryConfigDate : dates) {

			// get inventory Voucher Details
			List<Object[]> objects = inventoryVoucherDetailRepository.findPurchaseByDocumentsAccountAndDateBetween(
					documents, accountPid, purchaseHistoryConfigDate.getStartDate(),
					purchaseHistoryConfigDate.getEndDate());
			for (Object[] obj : objects) {

				Long productId = Long.valueOf(obj[0].toString());
				Double quantity = Double.valueOf(obj[1].toString());

				// find ecomproducts
				List<EcomProductProfile> ecomProducts = ecomProductProfileProductRepository
						.findEcomProductProfilesByProductId(productId);
				for (EcomProductProfile ecomProductProfile : ecomProducts) {
					List<Value> values = new ArrayList<>();
					if (mapEcomProducts.get(ecomProductProfile.getPid()) != null) {
						values = mapEcomProducts.get(ecomProductProfile.getPid());
						boolean add = true;
						for (Value value : values) {
							if (value.getId().equals(purchaseHistoryConfigDate.getId())) {
								value.setValue(value.getValue() + quantity);
								add = false;
								break;
							}
						}
						if (add) {
							values.add(new Value(purchaseHistoryConfigDate.getId(),
									purchaseHistoryConfigDate.getLabel(), quantity));
						}
					} else {
						values.add(new Value(purchaseHistoryConfigDate.getId(), purchaseHistoryConfigDate.getLabel(),
								quantity));
					}
					mapEcomProducts.put(ecomProductProfile.getPid(), values);
				}
			}
		}
		for (Map.Entry<String, List<Value>> obj : mapEcomProducts.entrySet()) {
			String result = "";
			for (Value value : obj.getValue()) {
				result += value.getLabel() + ": " + value.getValue() + ", ";
			}
			result = result.substring(0, result.length() - 2);
			purchaseHistoryStringDTOs.add(new PurchaseHistoryStringDTO(accountPid, obj.getKey(), result));
		}
		return purchaseHistoryStringDTOs;
	}

	private List<PurchaseHistoryConfigDate> findDatesFromPurchaseHistoryConfigs(
			List<PurchaseHistoryConfig> purchaseHistoryConfigs, Company company) {
		List<PurchaseHistoryConfigDate> dates = new ArrayList<>();
		for (PurchaseHistoryConfig purchaseHistoryConfig : purchaseHistoryConfigs) {

			LocalDate startDate = findStartDate(purchaseHistoryConfig, company);
			LocalDate endDate = findEndDate(purchaseHistoryConfig, company);
			String result = "";
			if (purchaseHistoryConfig.getCreateDynamicLabel()) {
				String startMonth = startDate.getMonth().name().substring(0, 3);
				String startYear = String.valueOf(startDate.getYear());

				String endMonth = endDate.getMonth().name().substring(0, 3);
				String endYear = String.valueOf(endDate.getYear());

				result += startMonth + " " + startYear.substring(startYear.length() - 2);
				if (!startMonth.equals(endMonth) || !startYear.equals(endYear)) {
					result += " - " + endMonth + " " + endYear.substring(endYear.length() - 2);
				}
			} else {
				result += purchaseHistoryConfig.getName();
			}
			dates.add(new PurchaseHistoryConfigDate(purchaseHistoryConfig.getId(), result, startDate.atTime(0, 0),
					endDate.atTime(23, 59),purchaseHistoryConfig.getName(),purchaseHistoryConfig.getSortOrder()));
		}
		dates.sort((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		return dates;

	}

	private LocalDate findStartDate(PurchaseHistoryConfig purchaseHistoryConfig, Company company) {
		final Month month;
		if (purchaseHistoryConfig.getStartMonth() == 0) {
			month = LocalDate.now().minusMonths(purchaseHistoryConfig.getStartMonthMinus()).getMonth();
		} else {
			month = Month.of(purchaseHistoryConfig.getStartMonth());
		}
		List<LocalDate> monthRanges = getFinancialYearMonths(
				company.getPeriodStartDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		return LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
	}

	private LocalDate findEndDate(PurchaseHistoryConfig purchaseHistoryConfig, Company company) {
		final Month month;
		if (purchaseHistoryConfig.getEndMonth() == 0) {
			month = LocalDate.now().minusMonths(purchaseHistoryConfig.getEndMonthMinus()).getMonth();
		} else {
			month = Month.of(purchaseHistoryConfig.getEndMonth());
		}
		List<LocalDate> monthRanges = getFinancialYearMonths(
				company.getPeriodStartDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(purchaseHistoryConfig.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		LocalDate initial = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
		return initial.withDayOfMonth(initial.lengthOfMonth());
	}

	private List<LocalDate> getFinancialYearMonths(LocalDate periodStartDate, LocalDate periodEndDate) {
		List<LocalDate> monthRanges = new ArrayList<>();
		while (!periodStartDate.isAfter(periodEndDate)) {
			monthRanges.add(periodStartDate);
			periodStartDate = periodStartDate.plusMonths(1);
		}
		return monthRanges;
	}

	class Value {

		private Long id;

		private String label;

		private Double value;

		public Value(Long id, String label, Double value) {
			super();
			this.id = id;
			this.label = label;
			this.value = value;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}

	}

}
