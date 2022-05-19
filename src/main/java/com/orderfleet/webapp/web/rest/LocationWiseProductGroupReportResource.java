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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupLocationTargetRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationWiseProductGroupPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

@Controller
@RequestMapping("/web")
public class LocationWiseProductGroupReportResource {
	private final Logger log = LoggerFactory.getLogger(LocationWiseProductGroupReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private ProductGroupLocationTargetRepository productGroupLocationTargetRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;
	
	@Inject
	private ProductGroupSalesTargetGroupService productGroupSalesTargetGroupService;
	
	@Timed
	@RequestMapping(value = "/location-wise-product-group-report", method = RequestMethod.GET)
	public String getLocationWiseTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Location Wise Product Group Comparison Report");
		model.addAttribute("products", productGroupSalesTargetGroupService.findAllProductGroupByCompany());
		return "company/locationWiseProductGroupReport";
	}

	@RequestMapping(value = "/location-wise-product-group-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public LocationWiseProductGroupPerformanceDTO performanceTargets(
			@RequestParam("productGroupPid") String productGroupPid,
			@RequestParam(value = "fromFirstDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromFirstDate,
			@RequestParam(value = "toFirstDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toFirstDate) {
		log.info("Location Wise Product Group Report : {} , and date between {}, {}, {}, {}",
				productGroupPid, fromFirstDate, toFirstDate);
		long starttime = System.nanoTime();
		
		
		if (productGroupPid.equals("no")) {
			return null;
		}

		Set<Long> productProfileIds = productGroupProductRepository.findProductIdByProductGroupPid(productGroupPid);
	
		// filter based on product group
		List<LocationDTO> locations = locationService.findAllLocationsByCompanyAndActivatedLocations();
		
		List<String> locationPids = locations.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<ProductGroupLocationTarget> firstProductGroupLocationTargets = productGroupLocationTargetRepository
				.findByLocationPidInAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(locationPids,
						productGroupPid, fromFirstDate, toFirstDate);

		

		log.info("Product Group Location Targets ", firstProductGroupLocationTargets.size());

		List<Object[]> allLocationAccountProfiles = locationAccountProfileRepository.findAllByAccountProfileOptimised();

//		List<InventoryVoucherHeader> allInventoryVoucherHeader = inventoryVoucherHeaderRepository
//				.findAllByCompanyIdOrderByCreatedDateDesc();

		
		List<Object[]> allInventoryVoucherHeaderObject = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndOrderByCreatedDateDesc();
		
		List<InventoryVoucherHeaderDTO> allInventoryVoucherHeader = new ArrayList<>();

		for (Object[] obj : allInventoryVoucherHeaderObject) {

			InventoryVoucherHeaderDTO ivh = new InventoryVoucherHeaderDTO();

			LocalDateTime documentDate = LocalDateTime.parse(obj[3].toString());
			ivh.setDocumentDate(documentDate);
			ivh.setInventoryVoucherHeaderId(Long.parseLong(obj[0].toString()));
			ivh.setReceiverAccountProfileId(Long.parseLong(obj[1].toString()));
			ivh.setDocumentId(Long.parseLong(obj[2].toString()));

			allInventoryVoucherHeader.add(ivh);
		}

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				SecurityUtils.getCurrentUsersCompanyId());

//		if (primarySecDoc.isEmpty()) {
//			log.debug("........No PrimarySecondaryDocument configuration Available...........");
//			return 0;
//		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		// if (!productGroupLocationTargets.isEmpty()) {
		LocationWiseProductGroupPerformanceDTO locationWiseProductGroupPerformanceDTO = new LocationWiseProductGroupPerformanceDTO();
		// Get months date between the date
		List<LocalDate> firstMonthsBetweenDates = monthsDateBetweenDates(fromFirstDate, toFirstDate);
		
		List<ProductGroupLocationTargetDTO> firstProductGroupLocationTargetDTOs = new ArrayList<>();
		

		for (ProductGroupLocationTarget productGroupLocationTarget : firstProductGroupLocationTargets) {
			firstProductGroupLocationTargetDTOs.add(new ProductGroupLocationTargetDTO(productGroupLocationTarget));
		}

	

		log.info("Product Group Location DTOs {} {}", firstProductGroupLocationTargetDTOs.size());

		// group by SalesTargetGroupName
		Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargetByLocationName = firstProductGroupLocationTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(ProductGroupLocationTargetDTO::getLocationName));

		

		// actual sales user target
		Map<String, List<ProductGroupLocationTargetDTO>> firstProductGroupLocationTargetMap = new HashMap<>();
		

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

//				productGroupLocationTargetDTO
//				.setAchievedVolume(getAchievedVolume(location.getPid(), productProfileIds, monthDate));

				Set<Long> accountProfileIds = allLocationAccountProfiles.stream()
						.filter(a -> Boolean.valueOf(a[0].toString()))
						.filter(a -> a[1].toString().equals(location.getPid())).map(m -> Long.valueOf(m[2].toString()))
						.collect(Collectors.toSet());

				LocalDate start1 = monthDate.with(TemporalAdjusters.firstDayOfMonth());
				LocalDate end = monthDate.with(TemporalAdjusters.lastDayOfMonth());

				Double achievedVolume = 0D;
				if (!accountProfileIds.isEmpty() && !productProfileIds.isEmpty()) {
					Set<Long> ivHeaderIds = allInventoryVoucherHeader.stream().filter(av -> {
						if (av.getDocumentDate().isAfter(start1.atTime(0, 0))
								&& av.getDocumentDate().isBefore(end.atTime(23, 59))) {
							return true;
						}
						return false;
					}).filter(a -> {
						Optional<Long> t = accountProfileIds.stream()
								.filter(ap -> ap.equals(a.getReceiverAccountProfileId())).findAny();
						return t.isPresent();
					}).filter(d -> {
						Optional<Long> t = documentIds.stream().filter(di -> di.equals(d.getDocumentId())).findAny();
						return t.isPresent();
					}).map(m -> m.getInventoryVoucherHeaderId()).collect(Collectors.toSet());

					if (!ivHeaderIds.isEmpty()) {
						achievedVolume = inventoryVoucherDetailRepository
								.sumOfVolumeByAndProductIdsAndHeaderIds(productProfileIds, ivHeaderIds);
					}
				}
				achievedVolume = achievedVolume == null ? 0 : achievedVolume;
				productGroupLocationTargetDTO.setAchievedVolume(achievedVolume);

				productGroupLocationTargetList.add(productGroupLocationTargetDTO);
			}
			firstProductGroupLocationTargetMap.put(locationName, productGroupLocationTargetList);
		}


		List<String> firstMonthList = new ArrayList<>();
		for (LocalDate monthDate : firstMonthsBetweenDates) {
			firstMonthList.add(monthDate.getMonth().toString());
		}

		
		locationWiseProductGroupPerformanceDTO.setFirstProductGroupLocationTargets(
				firstProductGroupLocationTargetMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		
		locationWiseProductGroupPerformanceDTO.setFirstMonthList(firstMonthList);
		
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

//	private double getAchievedVolume(String locationPid, Set<Long> productProfileIds, LocalDate initialDate) {
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
//		if (!accountProfileIds.isEmpty() && !productProfileIds.isEmpty()) {
//			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository.findIdByAccountProfileAndDocumentDateBetween(
//					accountProfileIds, documentIds, start.atTime(0, 0), end.atTime(23, 59));
//			if (!ivHeaderIds.isEmpty()) {
//				achievedVolume = inventoryVoucherDetailRepository
//						.sumOfVolumeByAndProductIdsAndHeaderIds(productProfileIds, ivHeaderIds);
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
