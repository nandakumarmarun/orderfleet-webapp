package com.orderfleet.webapp.web.rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupLocationTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationWiseProductGroupPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;

@Controller
@RequestMapping("/web")
public class LocationWiseReceiptComparisonResource {
	private final Logger log = LoggerFactory.getLogger(LocationWiseReceiptComparisonResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private LocationService locationService;

	@Inject
	private ProductGroupLocationTargetRepository productGroupLocationTargetRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Timed
	@RequestMapping(value = "/location-wise-receipt-comparison-report", method = RequestMethod.GET)
	public String getLocationWiseSalesComparisonReport(Model model) {
		log.debug("Web request to get a page of LocationWiseSalesComparisonReport");
		return "company/locationWiseReceiptComparisonReport";
	}

	@RequestMapping(value = "/location-wise-receipt-comparison-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public LocationWiseProductGroupPerformanceDTO performanceTargets(
			@RequestParam(value = "fromFirstDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromFirstDate,
			@RequestParam(value = "toFirstDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toFirstDate,
			@RequestParam(value = "fromSecondDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromSecondDate,
			@RequestParam(value = "toSecondDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toSecondDate) {
		log.info("Location Wise Receipt Comparison Report First date {} - {}, Second Date {} - {}", fromFirstDate,
				toFirstDate, fromSecondDate, toSecondDate);
		long starttime = System.nanoTime();

		// filter based on product group
		List<LocationDTO> locations = locationService.findAllLocationsByCompanyAndActivatedLocations();
		List<String> locationPids = locations.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<ProductGroupLocationTarget> firstProductGroupLocationTargets = productGroupLocationTargetRepository
				.findByLocationPidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(locationPids, fromFirstDate,
						toFirstDate);

		List<ProductGroupLocationTarget> secondProductGroupLocationTargets = productGroupLocationTargetRepository
				.findByLocationPidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(locationPids, fromSecondDate,
						toSecondDate);

		log.info("Product Group Location Targets {} {}", firstProductGroupLocationTargets.size(),
				secondProductGroupLocationTargets.size());

		// List<LocationAccountProfile> allLocationAccountProfiles =
		// locationAccountProfileRepository.findAllByAccountProfile();
		// List<AccountingVoucherHeader> allAccountingVoucherHeader =
		// accountingVoucherHeaderRepository
		// .findAllByCompanyIdOrderByCreatedDateDesc();

		List<Object[]> allLocationAccountProfiles = locationAccountProfileRepository.findAllByAccountProfileOptimised();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_160" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by companyId and order by create date desc";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> allAccountingVoucherHeaderObject = accountingVoucherHeaderRepository
				.findAllByCompanyIdAndOrderByCreatedDateDesc();
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


		List<AccountingVoucherHeaderDTO> allAccountingVoucherHeader = new ArrayList<>();

		for (Object[] obj : allAccountingVoucherHeaderObject) {

			AccountingVoucherHeaderDTO avh = new AccountingVoucherHeaderDTO();

			LocalDateTime documentDate = LocalDateTime.parse(obj[3].toString());
			avh.setDocumentDate(documentDate);
			avh.setId(Long.parseLong(obj[0].toString()));
			avh.setAccountProfileId(Long.parseLong(obj[1].toString()));
			avh.setDocumentId(Long.parseLong(obj[2].toString()));
			avh.setTotalAmount(Double.parseDouble(obj[4].toString()));

			allAccountingVoucherHeader.add(avh);
		}

		log.info("All accounting voucher header size {}", allAccountingVoucherHeader.size());
		log.info("All Location Account Profiles size {}", allLocationAccountProfiles.size());

		// if (!productGroupLocationTargets.isEmpty()) {
		LocationWiseProductGroupPerformanceDTO locationWiseProductGroupPerformanceDTO = new LocationWiseProductGroupPerformanceDTO();
		// Get months date between the date
		List<LocalDate> firstMonthsBetweenDates = monthsDateBetweenDates(fromFirstDate, toFirstDate);
		List<LocalDate> secondMonthsBetweenDates = monthsDateBetweenDates(fromSecondDate, toSecondDate);
		List<ProductGroupLocationTargetDTO> firstProductGroupLocationTargetDTOs = new ArrayList<>();
		List<ProductGroupLocationTargetDTO> secondProductGroupLocationTargetDTOs = new ArrayList<>();

		for (ProductGroupLocationTarget productGroupLocationTarget : firstProductGroupLocationTargets) {
			firstProductGroupLocationTargetDTOs.add(new ProductGroupLocationTargetDTO(productGroupLocationTarget));
		}

		for (ProductGroupLocationTarget productGroupLocationTarget : secondProductGroupLocationTargets) {
			secondProductGroupLocationTargetDTOs.add(new ProductGroupLocationTargetDTO(productGroupLocationTarget));
		}

		// group by SalesTargetGroupName
		Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargetByLocationName = firstProductGroupLocationTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(ProductGroupLocationTargetDTO::getLocationName));

		Map<String, List<ProductGroupLocationTargetDTO>> secondProductGroupLocationTargetByLocationName = secondProductGroupLocationTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(ProductGroupLocationTargetDTO::getLocationName));

		// actual sales user target
		Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargetMap = new HashMap<>();
		Map<String, List<ProductGroupLocationTargetDTO>> secondProductGroupLocationTargetMap = new HashMap<>();

		log.info("FirstProductGroupLocationTargetMap set achievedAmount");
		for (LocationDTO location : locations) {
			String locationName = location.getName();
			List<ProductGroupLocationTargetDTO> productGroupLocationTargetList = new ArrayList<>();
			for (LocalDate monthDate : firstMonthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargetByMonth = null;
				List<ProductGroupLocationTargetDTO> userTarget = firstProductGroupLocationTargetByLocationName
						.get(locationName);
				ProductGroupLocationTargetDTO productGroupLocationTargetDTO = null;
				if (userTarget != null) {
					productGroupLocationTargetByMonth = userTarget.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (productGroupLocationTargetByMonth != null
							&& productGroupLocationTargetByMonth.get(month) != null) {
						productGroupLocationTargetDTO = productGroupLocationTargetByMonth.get(month).get(0);
					}
				}
				// no target saved, add a default one
				if (productGroupLocationTargetDTO == null) {
					productGroupLocationTargetDTO = new ProductGroupLocationTargetDTO();
					productGroupLocationTargetDTO.setLocationName(locationName);
					productGroupLocationTargetDTO.setLocationPid(location.getPid());
					productGroupLocationTargetDTO.setVolume(0);
					productGroupLocationTargetDTO.setAmount(0);
				}

//				Set<Long> accountProfileIds = allLocationAccountProfiles.stream().filter(a -> a.getLocation().getActivated())
//						.filter(a -> a.getLocation().getPid().equals(location.getPid()))
//						.filter(distinctByKey(LocationAccountProfile::getAccountProfile))
//						.map(m -> m.getAccountProfile().getId()).collect(Collectors.toSet());

				Set<Long> accountProfileIds = allLocationAccountProfiles.stream()
						.filter(a -> Boolean.valueOf(a[0].toString()))
						.filter(a -> a[1].toString().equals(location.getPid())).map(m -> Long.valueOf(m[2].toString()))
						.collect(Collectors.toSet());

				LocalDate start = monthDate.with(TemporalAdjusters.firstDayOfMonth());
				LocalDate end = monthDate.with(TemporalAdjusters.lastDayOfMonth());

				Double achievedAmount = 0D;
				if (!accountProfileIds.isEmpty()) {
					Stream<AccountingVoucherHeaderDTO> accountingVoucherHeader = allAccountingVoucherHeader.stream()
							.filter(av -> {
								if (av.getDocumentDate().isAfter(start.atTime(0, 0))
										&& av.getDocumentDate().isBefore(end.atTime(23, 59))) {
									return true;
								}
								return false;
							}).filter(a -> {
								Optional<Long> t = accountProfileIds.stream()
										.filter(ap -> ap.equals(a.getAccountProfileId())).findAny();
								return t.isPresent();
							});

					achievedAmount = accountingVoucherHeader.mapToDouble(o -> o.getTotalAmount()).sum();
				}

				productGroupLocationTargetDTO.setAchievedAmount(achievedAmount);

				/*
				 * productGroupLocationTargetDTO
				 * .setAchievedVolume(getAchievedVolume(location.getPid(), monthDate));
				 */

				productGroupLocationTargetList.add(productGroupLocationTargetDTO);
			}
			firstProductGroupLocationTargetMap.put(locationName, productGroupLocationTargetList);
		}

		log.info("SecondProductGroupLocationTargetMap set achievedAmount");
		for (LocationDTO location : locations) {
			String locationName = location.getName();
			List<ProductGroupLocationTargetDTO> productGroupLocationTargetList = new ArrayList<>();
			for (LocalDate monthDate : secondMonthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargetByMonth = null;
				List<ProductGroupLocationTargetDTO> userTarget = secondProductGroupLocationTargetByLocationName
						.get(locationName);
				ProductGroupLocationTargetDTO productGroupLocationTargetDTO = null;
				if (userTarget != null) {
					productGroupLocationTargetByMonth = userTarget.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (productGroupLocationTargetByMonth != null
							&& productGroupLocationTargetByMonth.get(month) != null) {
						productGroupLocationTargetDTO = productGroupLocationTargetByMonth.get(month).get(0);
					}
				}
				// no target saved, add a default one
				if (productGroupLocationTargetDTO == null) {
					productGroupLocationTargetDTO = new ProductGroupLocationTargetDTO();
					productGroupLocationTargetDTO.setLocationName(locationName);
					productGroupLocationTargetDTO.setLocationPid(location.getPid());
					productGroupLocationTargetDTO.setVolume(0);
					productGroupLocationTargetDTO.setAmount(0);
				}

//				Set<Long> accountProfileIds = allLocationAccountProfiles.stream().filter(a -> a.getLocation().getActivated())
//						.filter(a -> a.getLocation().getPid().equals(location.getPid()))
//						.filter(distinctByKey(LocationAccountProfile::getAccountProfile))
//						.map(m -> m.getAccountProfile().getId()).collect(Collectors.toSet());

				Set<Long> accountProfileIds = allLocationAccountProfiles.stream()
						.filter(a -> Boolean.valueOf(a[0].toString()))
						.filter(a -> a[1].toString().equals(location.getPid())).map(m -> Long.valueOf(m[2].toString()))
						.collect(Collectors.toSet());

				LocalDate start = monthDate.with(TemporalAdjusters.firstDayOfMonth());
				LocalDate end = monthDate.with(TemporalAdjusters.lastDayOfMonth());

				Double achievedAmount = 0D;
				if (!accountProfileIds.isEmpty()) {
					Stream<AccountingVoucherHeaderDTO> accountingVoucherHeader = allAccountingVoucherHeader.stream()
							.filter(av -> {
								if (av.getDocumentDate().isAfter(start.atTime(0, 0))
										&& av.getDocumentDate().isBefore(end.atTime(23, 59))) {
									return true;
								}
								return false;
							}).filter(a -> {
								Optional<Long> t = accountProfileIds.stream()
										.filter(ap -> ap.equals(a.getAccountProfileId())).findAny();
								return t.isPresent();
							});

					achievedAmount = accountingVoucherHeader.mapToDouble(o -> o.getTotalAmount()).sum();
				}

				productGroupLocationTargetDTO.setAchievedAmount(achievedAmount);

				productGroupLocationTargetList.add(productGroupLocationTargetDTO);
			}
			secondProductGroupLocationTargetMap.put(locationName, productGroupLocationTargetList);
		}

		List<String> firstMonthList = new ArrayList<>();
		for (LocalDate monthDate : firstMonthsBetweenDates) {
			firstMonthList.add(monthDate.getMonth().toString());
		}

		List<String> secondMonthList = new ArrayList<>();
		for (LocalDate monthDate : secondMonthsBetweenDates) {
			secondMonthList.add(monthDate.getMonth().toString());
		}
		locationWiseProductGroupPerformanceDTO.setFirstProductGroupLocationTargets(
				firstProductGroupLocationTargetMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		locationWiseProductGroupPerformanceDTO.setSecondProductGroupLocationTargets(
				secondProductGroupLocationTargetMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		locationWiseProductGroupPerformanceDTO.setFirstMonthList(firstMonthList);
		locationWiseProductGroupPerformanceDTO.setSecondMonthList(secondMonthList);
		/*
		 * productGroupLocationPerformanceDTO.setSalesTargetGroupUserTargets(
		 * productGroupLocationTargetMap);
		 */
		long endtime = System.nanoTime();
		double elapsedTime = (endtime - starttime) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
		return locationWiseProductGroupPerformanceDTO;
		/*
		 * } return null;
		 */
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

//	private double getAchievedVolume(String locationPid, LocalDate initialDate) {
//
//		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
//		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
//
//		Set<Long> accountProfileIds = locationAccountProfileRepository.findAccountProfileIdByLocationPid(locationPid);
//
//		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
//		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
//				SecurityUtils.getCurrentUsersCompanyId());
//		if (primarySecDoc.isEmpty()) {
//			log.debug("........No PrimarySecondaryDocument configuration Available...........");
//			return 0;
//		}
//		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
//				.collect(Collectors.toList());
//
//		Double achievedVolume = 0D;
//		if (!accountProfileIds.isEmpty()) {
//			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository.findIdByAccountProfileAndDocumentDateBetween(
//					accountProfileIds, documentIds, start.atTime(0, 0), end.atTime(23, 59));
//			if (!ivHeaderIds.isEmpty()) {
//				achievedVolume = inventoryVoucherDetailRepository
//						.sumOfVolumeByAndHeaderIds(ivHeaderIds);
//			}
//		}
//		return achievedVolume == null ? 0 : achievedVolume;
//
//	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}
}
