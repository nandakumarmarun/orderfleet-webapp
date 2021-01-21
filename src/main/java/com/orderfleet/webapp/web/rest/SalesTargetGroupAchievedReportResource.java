package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.SalesProductGroupUserTarget;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupProduct;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesProductGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceAchivementDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesProductGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesTargetGroupAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupAchievedReportResource.class);

	@Inject
	private SalesProductGroupUserTargetRepository salesProductGroupUserTargetRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private SalesTargetGroupUserTargetMapper salesTargetGroupUserTargetMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private SalesSummaryAchievmentRepository salesSummaryAchievmentRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private ProductGroupSalesTargetGroupService productGroupSalesTargetGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	/**
	 * GET /sales-target-group-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/sales-target-group-vs-achieved-report", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("products", salesTargetGroupRepository.findAllByCompanyId());
		return "company/salesTargetGroupTargetAchievedReport";
	}

	@RequestMapping(value = "/sales-target-group-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceDTO performanceTargets(@RequestParam("employeePid") String employeePid,
			@RequestParam("productGroupPids") List<String> productGroupPids,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.info("Rest Request to load sales target vs achieved report : {} , and date between {}, {}", employeePid,
				fromDate, toDate);
		if (employeePid.equals("no")) {
			return null;
		}
		String userPid = employeeRepository.findUserPidByEmployeePid(employeePid);
		if (userPid == null || userPid.isEmpty()) {
			return null;
		}

		Double achievedAmount = 0D;
		// user's account profile
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
		Set<Long> accountProfileIds = locationAccountProfileRepository
				.findAccountProfileIdsByUserLocationIdsIn(locationIds);
		// filter based on product group
		List<SalesTargetGroup> productGroups = salesTargetGroupRepository.findAllByCompanyIdAndPidIn(productGroupPids);
		List<String> allProductGroupPids = productGroups.stream().map(a -> a.getPid()).collect(Collectors.toList());

		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						allProductGroupPids, userPid, fromDate, toDate);

		SalesPerformaceDTO salesPerformaceDTO = new SalesPerformaceDTO();
		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = new ArrayList<SalesTargetGroupUserTargetDTO>();
		if (!salesTargetGroupUserTargets.isEmpty()) {
			salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetMapper
					.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargets);
		}
		// group by SalesTargetGroupName
		Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetBySalesTargetGroupName = salesTargetGroupUserTargetDTOs
				.parallelStream()
				.collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getSalesTargetGroupName));

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				SecurityUtils.getCurrentUsersCompanyId());
//			if (primarySecDoc.isEmpty()) {
//				log.debug("........No PrimarySecondaryDocument configuration Available...........");
//				return 0;
//			}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		Set<Long> productProfileIds = new HashSet<>();

//			List<InventoryVoucherHeader> allInventoryVoucherHeader = inventoryVoucherHeaderRepository
//					.findAllByCompanyIdOrderByCreatedDateDesc();

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

		log.info("Get All inventory voucher header size {}", allInventoryVoucherHeader.size());

		List<SalesTargetGroupProduct> productGroupProducts = salesTargetGroupProductRepository.findAllByCompanyId();

		// actual sales user target
		Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetMap = new HashMap<>();
		for (SalesTargetGroup productGroup : productGroups) {
//				productProfileIds = productGroupProductRepository.findProductIdByProductGroupPid(productGroup.getPid());

			productProfileIds = productGroupProducts.stream()
					.filter(pgp -> pgp.getSalesTargetGroup().getPid().equals(productGroup.getPid()))
					.map(psd -> psd.getProduct().getId()).collect(Collectors.toSet());

			// productGroupProductRepository.findProductIdByProductGroupPid(productGroup.getPid());
			String groupName = productGroup.getName();
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetByMonth = null;
				SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = null;
				if (!salesTargetGroupUserTargetBySalesTargetGroupName.isEmpty()) {
					List<SalesTargetGroupUserTargetDTO> userTarget = salesTargetGroupUserTargetBySalesTargetGroupName
							.get(groupName);

					if (userTarget != null) {
						salesTargetGroupUserTargetByMonth = userTarget.stream()
								.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
						if (salesTargetGroupUserTargetByMonth != null
								&& salesTargetGroupUserTargetByMonth.get(month) != null) {
							salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetByMonth.get(month).get(0);
						}
					}
				}
				// no target saved, add a default one
				if (salesTargetGroupUserTargetDTO == null) {
					salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();
					salesTargetGroupUserTargetDTO.setSalesTargetGroupName(groupName);
					salesTargetGroupUserTargetDTO.setSalesTargetGroupPid(productGroup.getPid());
					salesTargetGroupUserTargetDTO.setVolume(0);
				}

				LocalDate start = monthDate.with(TemporalAdjusters.firstDayOfMonth());
				LocalDate end = monthDate.with(TemporalAdjusters.lastDayOfMonth());
				Double achievedVolume = 0D;
				if (!accountProfileIds.isEmpty() && !productProfileIds.isEmpty()) {
					Set<Long> ivHeaderIds = allInventoryVoucherHeader.stream().filter(av -> {
						if (av.getDocumentDate().isAfter(start.atTime(0, 0))
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
				salesTargetGroupUserTargetDTO.setAchievedVolume(achievedVolume);

				salesTargetGroupUserTargetList.add(salesTargetGroupUserTargetDTO);
			}
			salesTargetGroupUserTargetMap.put(groupName, salesTargetGroupUserTargetList);
		}
		List<String> monthList = new ArrayList<>();
		for (LocalDate monthDate : monthsBetweenDates) {
			monthList.add(monthDate.getMonth().toString());
		}
		salesPerformaceDTO.setMonthList(monthList);

		salesPerformaceDTO.setSalesTargetGroupUserTargets(
				salesTargetGroupUserTargetMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		// salesPerformaceDTO.setSalesProductGroupUserTargets(salesProductGroupUserTargetMap);
		return salesPerformaceDTO;

	}

	private double getAchievedAmountFromTransactionUserWise(String userPid, LocalDate initialDate,
			Set<Long> documentIds, Set<Long> productProfileIds) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository
					.findIdByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
							start.atTime(0, 0), end.atTime(23, 59));
			if (!ivHeaderIds.isEmpty()) {
				achievedAmount = inventoryVoucherDetailRepository
						.sumOfAmountByAndProductIdsAndHeaderIds(productProfileIds, ivHeaderIds);
			}
		}
		return achievedAmount == null ? 0 : achievedAmount;
	}

	private double getAchievedAmountFromTransactionTerritoryWise(String userPid, LocalDate initialDate,
			Set<Long> documentIds, Set<Long> productProfileIds) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
		// user's account profile
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
		Set<Long> accountProfileIds = locationAccountProfileRepository
				.findAccountProfileIdsByUserLocationIdsIn(locationIds);
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			// get achieved amount
			achievedAmount = inventoryVoucherDetailRepository
					.sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfileIds,
							documentIds, productProfileIds, start.atTime(0, 0), end.atTime(23, 59));
		}
		return achievedAmount == null ? 0 : achievedAmount;
	}

	private double getAchievedAmountFromSummary(String userPid,
			SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO, LocalDate initialDate) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		List<SalesSummaryAchievment> salesSummaryAchievmentList = salesSummaryAchievmentRepository
				.findByUserPidAndSalesTargetGroupPidAndAchievedDateBetween(userPid,
						salesTargetGroupUserTargetDTO.getSalesTargetGroupPid(), start, end);
		double achievedAmount = 0;
		if (!salesSummaryAchievmentList.isEmpty()) {
			for (SalesSummaryAchievment summaryAchievment : salesSummaryAchievmentList) {
				achievedAmount += summaryAchievment.getAmount();
			}
		}
		return achievedAmount;
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}

	private List<SalesProductGroupUserTargetDTO> salesProductGroupUserTargetsToSalesProductGroupUserTargetDTOs(
			List<SalesProductGroupUserTarget> salesProductGroupUserTargets) {
		if (salesProductGroupUserTargets == null) {
			return null;
		}

		List<SalesProductGroupUserTargetDTO> list = new ArrayList<SalesProductGroupUserTargetDTO>();
		for (SalesProductGroupUserTarget salesProductGroupUserTarget : salesProductGroupUserTargets) {
			list.add(salesProductGroupUserTargetToSalesTargetGroupUserTargetDTO(salesProductGroupUserTarget));
		}
		return list;
	}

	public SalesProductGroupUserTargetDTO salesProductGroupUserTargetToSalesTargetGroupUserTargetDTO(
			SalesProductGroupUserTarget salesProductGroupUserTarget) {
		if (salesProductGroupUserTarget == null) {
			return null;
		}

		SalesProductGroupUserTargetDTO salesProductGroupUserTargetDTO = new SalesProductGroupUserTargetDTO();

		salesProductGroupUserTargetDTO.setUserPid(salesProductGroupUserTarget.getUser().getPid());
		salesProductGroupUserTargetDTO.setUserName(salesProductGroupUserTarget.getUser().getFirstName());
		salesProductGroupUserTargetDTO.setProductGroupName(salesProductGroupUserTarget.getProductGroup().getName());
		salesProductGroupUserTargetDTO.setProductGroupPid(salesProductGroupUserTarget.getProductGroup().getPid());
		salesProductGroupUserTargetDTO.setPid(salesProductGroupUserTarget.getPid());
		salesProductGroupUserTargetDTO.setFromDate(salesProductGroupUserTarget.getFromDate());
		salesProductGroupUserTargetDTO.setToDate(salesProductGroupUserTarget.getToDate());
		salesProductGroupUserTargetDTO.setVolume(salesProductGroupUserTarget.getVolume());
		salesProductGroupUserTargetDTO.setAmount(salesProductGroupUserTarget.getAmount());

		salesProductGroupUserTargetDTO.setLastModifiedDate(salesProductGroupUserTarget.getLastModifiedDate());

		return salesProductGroupUserTargetDTO;
	}

}
