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
import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileSalesLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.SalesLedgerWiseTargetRepository;
import com.orderfleet.webapp.repository.SalesLedgerWiseTargetRepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerPerformaceAchivementDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceAchivementDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;
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
public class SalesLedgerWiseTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesLedgerWiseTargetAchievedReportResource.class);

	@Inject
	private SalesLedgerWiseTargetRepository salesSalesLedgerWiseTargetRepository;

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

	@Inject
	private EmployeeProfileSalesLedgerRepository employeeProfileSalesLedgerRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	@Inject
	private SalesLedgerWiseTargetRepository salesLedgerWiseTargetRepository;

	/**
	 * GET /sales-product-group-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/sales-ledger-target-vs-achieved-report", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Sales Ledger Activity Performance");
		return "company/salesLedgerWiseTargetAchievedReport";
	}

	@RequestMapping(value = "/sales-ledger-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesLedgerPerformaceAchivementDTO performanceTargets(@RequestParam("employeePid") String employeePid,
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

		// user's account profile
		Set<Long> salesLedgerIds = employeeProfileSalesLedgerRepository
				.findSalesLedgerIdsByUserPidIn(Arrays.asList(userPid));

		// filter based on product group
		List<SalesLedger> salesLedgers = salesLedgerRepository
				.findAllCompanyAndSalesLedgerActivatedandSalesLedgerIdsIn(true, salesLedgerIds);
		List<String> allSalesLedgerPids = salesLedgers.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<SalesLedgerWiseTarget> salesLedgerWiseTargets = salesLedgerWiseTargetRepository
				.findBySalesLedgerPidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(allSalesLedgerPids, fromDate,
						toDate);

		SalesLedgerPerformaceAchivementDTO salesPerformaceDTO = new SalesLedgerPerformaceAchivementDTO();
		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		List<SalesLedgerWiseTargetDTO> salesLedgerWiseTargetDTOs = new ArrayList<SalesLedgerWiseTargetDTO>();
		if (!salesLedgerWiseTargets.isEmpty()) {
			salesLedgerWiseTargetDTOs = salesLedgerWiseTargetsToSalesLedgerWiseTargetDTOs(salesLedgerWiseTargets);
		}
		// group by SalesTargetGroupName
		Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargetBySalesTargetGroupName = salesLedgerWiseTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(SalesLedgerWiseTargetDTO::getSalesLedgerName));

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.TALLY_SALES,
				SecurityUtils.getCurrentUsersCompanyId());
//			if (primarySecDoc.isEmpty()) {
//				log.debug("........No PrimarySecondaryDocument configuration Available...........");
//				return 0;
//			}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

//			List<InventoryVoucherHeader> allInventoryVoucherHeader = inventoryVoucherHeaderRepository
//					.findAllByCompanyIdOrderByCreatedDateDesc();

		String id="INV_QUERY_102";
		String description="Selecting Inventory voucher id,receiver account id,document id and document date from invntory voucher header by validating company id and order by create date in descending order and getting result asa list of object";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
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

		// actual sales user target
		Map<String, List<SalesLedgerWiseTargetDTO>> salesSalesLedgerWiseTargetMap = new HashMap<>();
		
		Double ta = 0D;
		Double aa = 0D;
		for (SalesLedger salesLedger : salesLedgers) {

			String groupName = salesLedger.getName();
			List<SalesLedgerWiseTargetDTO> salesSalesLedgerWiseTargetList = new ArrayList<>();
			Double Tamountttt = 0D;
			Double Aamountttt = 0D;

			for (LocalDate monthDate : monthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargetByMonth = null;
				SalesLedgerWiseTargetDTO salesSalesLedgerWiseTargetDTO = null;
				if (!salesLedgerWiseTargetBySalesTargetGroupName.isEmpty()) {
					List<SalesLedgerWiseTargetDTO> userTarget = salesLedgerWiseTargetBySalesTargetGroupName
							.get(groupName);

					if (userTarget != null) {
						salesLedgerWiseTargetByMonth = userTarget.stream()
								.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
						if (salesLedgerWiseTargetByMonth != null && salesLedgerWiseTargetByMonth.get(month) != null) {
							salesSalesLedgerWiseTargetDTO = salesLedgerWiseTargetByMonth.get(month).get(0);
						}

					}
				}
				// no target saved, add a default one
				if (salesSalesLedgerWiseTargetDTO == null) {
					salesSalesLedgerWiseTargetDTO = new SalesLedgerWiseTargetDTO();
					salesSalesLedgerWiseTargetDTO.setSalesLedgerName(groupName);
					salesSalesLedgerWiseTargetDTO.setSalesLedgerPid(salesLedger.getPid());
					salesSalesLedgerWiseTargetDTO.setAmount(0);
				}

				Tamountttt += salesSalesLedgerWiseTargetDTO.getAmount();

				LocalDate start = monthDate.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
				LocalDate end = monthDate.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
				Double achievedAmount = 0D;
				if (!salesLedgerIds.isEmpty()) {
					Set<Long> ivHeaderIds = allInventoryVoucherHeader.stream().filter(av -> {
						if (av.getDocumentDate().isAfter(start.atTime(23, 59))
								&& av.getDocumentDate().isBefore(end.atTime(0, 0))) {
							return true;
						}
						return false;
					}).filter(d -> {
						Optional<Long> t = documentIds.stream().filter(di -> di.equals(d.getDocumentId())).findAny();
						return t.isPresent();
					}).map(m -> m.getInventoryVoucherHeaderId()).collect(Collectors.toSet());

					if (!ivHeaderIds.isEmpty()) {
						String id1="INV_QUERY_211";
						String description1="Finding sum of amount by sales ledger Id and HeaderIds";
						log.info("{ Query Id:- "+id1+" Query Description:- "+description1+" }");
						achievedAmount = inventoryVoucherHeaderRepository
								.sumOfAmountByAndSalesLedgerIdAndHeaderIds(salesLedger.getId(), ivHeaderIds);
					}
				}

				achievedAmount = achievedAmount == null ? 0 : achievedAmount;
				salesSalesLedgerWiseTargetDTO.setAchievedAmount(achievedAmount);

				Aamountttt += salesSalesLedgerWiseTargetDTO.getAchievedAmount();

				salesSalesLedgerWiseTargetList.add(salesSalesLedgerWiseTargetDTO);
			}
			log.info(Tamountttt + "----" + Aamountttt);
			ta +=Tamountttt;
			aa+=Aamountttt;
			salesSalesLedgerWiseTargetMap.put(groupName, salesSalesLedgerWiseTargetList);
		}
		log.info(ta + "----" + aa);
		List<String> monthList = new ArrayList<>();
		for (LocalDate monthDate : monthsBetweenDates) {
			monthList.add(monthDate.getMonth().toString());
		}
		salesPerformaceDTO.setMonthList(monthList);
		
		log.info(salesSalesLedgerWiseTargetMap.size()+"");
		
		int total = 0;
		for (List<SalesLedgerWiseTargetDTO> l : salesSalesLedgerWiseTargetMap.values()) {
		    total += total;
		}
        log.info(total+"");
		salesPerformaceDTO.setSalesLedgerWiseTargets(
				salesSalesLedgerWiseTargetMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		// salesPerformaceDTO.setSalesLedgerWiseTargets(salesSalesLedgerWiseTargetMap);
		return salesPerformaceDTO;

	}

	private double getAchievedAmountFromTransactionUserWise(String userPid, LocalDate initialDate,
			Set<Long> documentIds, Set<Long> productProfileIds) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			String id="INV_QUERY_166";
			String description="Listing inv Vouchers by using AccountPid,DocPid and Doc date between and tallydownloadStaus";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");




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

	private List<SalesLedgerWiseTargetDTO> salesLedgerWiseTargetsToSalesLedgerWiseTargetDTOs(
			List<SalesLedgerWiseTarget> salesLedgerWiseTargets) {
		if (salesLedgerWiseTargets == null) {
			return null;
		}

		List<SalesLedgerWiseTargetDTO> list = new ArrayList<SalesLedgerWiseTargetDTO>();
		for (SalesLedgerWiseTarget salesSalesLedgerWiseTarget : salesLedgerWiseTargets) {
			list.add(salesSalesLedgerWiseTargetToSalesTargetGroupUserTargetDTO(salesSalesLedgerWiseTarget));
		}
		return list;
	}

	public SalesLedgerWiseTargetDTO salesSalesLedgerWiseTargetToSalesTargetGroupUserTargetDTO(
			SalesLedgerWiseTarget salesSalesLedgerWiseTarget) {
		if (salesSalesLedgerWiseTarget == null) {
			return null;
		}

		SalesLedgerWiseTargetDTO salesSalesLedgerWiseTargetDTO = new SalesLedgerWiseTargetDTO();

		salesSalesLedgerWiseTargetDTO.setSalesLedgerName(salesSalesLedgerWiseTarget.getSalesLedger().getName());
		salesSalesLedgerWiseTargetDTO.setSalesLedgerPid(salesSalesLedgerWiseTarget.getSalesLedger().getPid());
		salesSalesLedgerWiseTargetDTO.setPid(salesSalesLedgerWiseTarget.getPid());
		salesSalesLedgerWiseTargetDTO.setFromDate(salesSalesLedgerWiseTarget.getFromDate());
		salesSalesLedgerWiseTargetDTO.setToDate(salesSalesLedgerWiseTarget.getToDate());
		salesSalesLedgerWiseTargetDTO.setAmount(salesSalesLedgerWiseTarget.getAmount());

		salesSalesLedgerWiseTargetDTO.setLastModifiedDate(salesSalesLedgerWiseTarget.getLastModifiedDate());

		return salesSalesLedgerWiseTargetDTO;
	}

}
