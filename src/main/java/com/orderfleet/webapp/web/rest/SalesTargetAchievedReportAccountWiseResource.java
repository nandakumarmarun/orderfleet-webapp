package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

@Controller
@RequestMapping("/web")
public class SalesTargetAchievedReportAccountWiseResource {
	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;
	
	@Inject
	private SalesTargetGroupUserTargetMapper salesTargetGroupUserTargetMapper;
	
	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;
	
	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;
	
	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Timed
	@RequestMapping(value = "/sales-target-vs-achieved-report-account-wise", method = RequestMethod.GET)
	public String getSalesTargetAchievedReportAccountWise(Model model) throws URISyntaxException {
		model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		return "company/salesTargetAchievedReportAccountWise";
	}
	
	@RequestMapping(value = "/sales-target-vs-achieved-report-account-wise/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceDTO performanceTargets(@RequestParam String accountPid,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findByAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(accountPid, fromDate, toDate);
		if (!salesTargetGroupUserTargets.isEmpty()) {
			SalesPerformaceDTO salesPerformaceDTO = new SalesPerformaceDTO();

			// Get months date between the date
			List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);

			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetMapper
					.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargets);
			// group by SalesTargetGroupName
			Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetBySalesTargetGroupName = salesTargetGroupUserTargetDTOs
					.parallelStream()
					.collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getSalesTargetGroupName));

			// actual sales user target
			Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetMap = new HashMap<>();
			salesTargetGroupUserTargetBySalesTargetGroupName.forEach((key, value) -> {

				List<Document> documents = salesTargetGroupDocumentRepository.findDocumentsBySalesTargetGroupName(key);
				List<ProductProfile> productProfiles = salesTargetGroupProductRepository
						.findProductsBySalesTargetGroupName(key);

				List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetList = new ArrayList<>();
				for (LocalDate monthDate : monthsBetweenDates) {
					String month = monthDate.getMonth().toString();
					// group by month, one month has only one user-target
					Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetByMonth = value.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (salesTargetGroupUserTargetByMonth.get(month) != null) {
						SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetByMonth
								.get(month).get(0);
								salesTargetGroupUserTargetDTO
										.setAchievedAmount(getAchievedAmountFromTransactionTerritoryWise(accountPid,
												monthDate, documents, productProfiles));
						salesTargetGroupUserTargetList.add(salesTargetGroupUserTargetDTO);
					} else {
						SalesTargetGroupUserTargetDTO defaultTarget = new SalesTargetGroupUserTargetDTO();
						salesTargetGroupUserTargetList.add(defaultTarget);
					}
				}
				salesTargetGroupUserTargetMap.put(key, salesTargetGroupUserTargetList);
			});
			List<String> monthList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				monthList.add(monthDate.getMonth().toString());
			}
			salesPerformaceDTO.setMonthList(monthList);
			salesPerformaceDTO.setSalesTargetGroupUserTargets(salesTargetGroupUserTargetMap);
			return salesPerformaceDTO;
		}
		return null;
	}

	private double getAchievedAmountFromTransactionTerritoryWise(String accountPid, LocalDate initialDate,List<Document> documents, List<ProductProfile> productProfiles) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
	
		if (!documents.isEmpty() && !productProfiles.isEmpty()) {
			// get achieved amount
			achievedAmount = inventoryVoucherDetailRepository
					.sumOfAmountByDocumentsAndProductsAndAccountProfilePidAndCreatedDateBetween(accountPid, documents, productProfiles, start.atTime(0, 0), end.atTime(23, 59));
		}
		return achievedAmount == null ? 0 : achievedAmount;
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}
}
