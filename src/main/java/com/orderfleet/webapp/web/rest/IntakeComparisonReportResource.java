package com.orderfleet.webapp.web.rest;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDate;
import com.orderfleet.webapp.web.rest.dto.IntakeComparisonDTO;
import com.orderfleet.webapp.web.rest.dto.IntakeComparisonDTO.IntakeComparisonMonth;

/**
 * Web controller for managing IntakeComparisonReport.
 * 
 * @author Muhammed Riyas T
 * @since Mar 03, 2017
 */
@Controller
@RequestMapping("/web")
public class IntakeComparisonReportResource {

	private final Logger log = LoggerFactory.getLogger(IntakeComparisonReportResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	/**
	 * GET /intake-comparison-report : get intakeComparisonReport.
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/intake-comparison-report", method = RequestMethod.GET)
	public String getIntakeComparisonReport(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of intake comparison report");
		return "company/intakeComparisonReport";
	}

	@Timed
	@RequestMapping(value = "/intake-comparison-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<IntakeComparisonDTO>> filterIntakeComparisonReport(
			@RequestParam(value = "fromDate") String fromMonth,
			@RequestParam(value = "toDate", required = false) String toMonth) {
		log.debug("Web request to filter sales in take report");
		LocalDate fDate = getDate(fromMonth);
		LocalDate tDate = getDate(toMonth);
		return new ResponseEntity<>(getFilterData(fDate, tDate), HttpStatus.OK);
	}

	private LocalDate getDate(String monthYear) {
		String monthYearArray[] = monthYear.split(",");
		String month = monthYearArray[0].toUpperCase();
		int year = Integer.valueOf(monthYearArray[1]);
		return LocalDate.of(year, Month.valueOf(month), 1);
	}

	private List<IntakeComparisonDTO> getFilterData(LocalDate fDate, LocalDate tDate) {
		List<PurchaseHistoryConfigDate> localDates = getMonths(fDate, tDate);
		List<IntakeComparisonDTO> intakeComparisonDTOs = new ArrayList<>();

		LocalDateTime sDate = fDate.withDayOfMonth(1).atTime(0, 0);
		LocalDateTime eDate = tDate.with(lastDayOfMonth()).atTime(23, 59);

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
		
		if (documents.size() > 0) {
			List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetailRepository
					.findByDocumentInAndDateBetween(documents, sDate, eDate);
			if (ivDetails.size() > 0) {
				List<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository.findAllByCompanyIdAndActivatedOrDeactivatedEcomProductProfile(true);
				for (EcomProductProfile ecomProductProfile : ecomProductProfiles) {
					IntakeComparisonDTO intakeComparison = new IntakeComparisonDTO();
					intakeComparison.setProductName(ecomProductProfile.getName());
					intakeComparison.setMonths(findIntakeComparisonMonths(ecomProductProfile.getPid(), localDates,
							intakeComparison, ivDetails));
					intakeComparisonDTOs.add(intakeComparison);
				}
			}
		}
		return intakeComparisonDTOs;
	}

	private List<IntakeComparisonMonth> findIntakeComparisonMonths(String pid, List<PurchaseHistoryConfigDate> months,
			IntakeComparisonDTO intakeComparison, List<InventoryVoucherDetail> ivDetails) {
		List<IntakeComparisonMonth> intakeComparisonMonths = new ArrayList<>();
		List<ProductProfile> products = ecomProductProfileProductRepository.findProductByEcomProductProfilePid(pid);
		for (PurchaseHistoryConfigDate date : months) {
			IntakeComparisonMonth intakeComparisonMonth = intakeComparison.new IntakeComparisonMonth();
			intakeComparisonMonth.setMonth(date.getLabel());
			Double quantity = 0d;
			if (products.size() > 0) {
				quantity = getSumOfQuantity(ivDetails, products, date.getStartDate(), date.getEndDate());
			}
			intakeComparisonMonth.setQuantity(quantity);
			intakeComparisonMonths.add(intakeComparisonMonth);
		}
		return intakeComparisonMonths;
	}

	private Double getSumOfQuantity(List<InventoryVoucherDetail> ivDetails, List<ProductProfile> productProfiles,
			LocalDateTime startDate, LocalDateTime endDate) {
		double sumofQty = ivDetails.parallelStream().filter(ivd -> {
			if ((ivd.getInventoryVoucherHeader().getCreatedDate().compareTo(startDate) >= 0
					&& ivd.getInventoryVoucherHeader().getCreatedDate().compareTo(endDate) <= 0)
					&& productProfiles.stream().anyMatch(p -> p.equals(ivd.getProduct()))) {
				return true;
			} else {
				return false;
			}
		}).collect(Collectors.summingDouble(iv -> iv.getQuantity()));
		return sumofQty;
	}

	private List<PurchaseHistoryConfigDate> getMonths(LocalDate fDate, LocalDate tDate) {
		List<LocalDate> monthRanges = new ArrayList<>();
		while (!fDate.isAfter(tDate)) {
			monthRanges.add(fDate);
			fDate = fDate.plusMonths(1);
		}
		List<PurchaseHistoryConfigDate> dates = new ArrayList<>();
		for (LocalDate date : monthRanges) {
			LocalDateTime sDate = date.atTime(0, 0);
			LocalDateTime eDate = date.with(lastDayOfMonth()).atTime(23, 59);

			String startMonth = sDate.getMonth().name().substring(0, 3);
			String startYear = String.valueOf(sDate.getYear());
			startYear = startYear.substring(startYear.length() - 2);
			dates.add(new PurchaseHistoryConfigDate(null, startMonth + " " + startYear, sDate, eDate,null,0));
		}
		return dates;
	}

}
