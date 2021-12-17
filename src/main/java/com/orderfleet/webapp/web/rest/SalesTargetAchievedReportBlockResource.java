package com.orderfleet.webapp.web.rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.domain.enums.TargetType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PerformanceReportMobileRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockSalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetAchievedDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;

@Controller
@RequestMapping("/web")
public class SalesTargetAchievedReportBlockResource {
	private final Logger log = LoggerFactory.getLogger(SalesTargetAchievedReportBlockResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private PerformanceReportMobileRepository performanceReportMobileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	@Inject
	private SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Timed
	@RequestMapping(value = "/sales-target-vs-achieved-block-report", method = RequestMethod.GET)
	public String getSalesTargetAchievedBlockReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("products", productGroupRepository.findAllByCompanyId(true));
		return "company/salesTargetAchievedBlockReport";
	}

	@RequestMapping(value = "/sales-target-vs-achieved-block-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceDTO performanceTargetBlocks(@RequestParam("employeePid") String employeePid,
			@RequestParam("productGroupPid") String productGroupPid,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		String userPid = null;
		if (employeePid.equals("no")) {
			return null;
		}
		Optional<EmployeeProfileDTO> employee = employeeProfileService.findOneByPid(employeePid);
		if (employee.isPresent()) {
			userPid = employee.get().getUserPid();
		} else {
			return null;
		}
		// user performance
		List<SalesTargetReportSetting> salesTargetReportSettings = performanceReportMobileRepository
				.findUserSalesTargetReportSettingByMobileUI(MobileUINames.USER_PERFORMANCE);
		if (salesTargetReportSettings.isEmpty()) {
			return null;
		}
		// for an mobile UI and setting only one data exist
		SalesTargetReportSetting salesTargetReportSetting = salesTargetReportSettings.get(0);

		// check territory wise or user wise
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> optLocWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.LOCATION_WISE);
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
		return setTargetAndAchieved(userPid, fromDate, toDate, optLocWiseCompanyConfig.isPresent(),
				salesTargetReportSetting);
	}

	private SalesPerformaceDTO setTargetAndAchieved(String userPid, LocalDate fromDate, LocalDate toDate,
			boolean isTerritoryWise, SalesTargetReportSetting salesTargetReportSetting) {

		// find sales target block
		List<SalesTargetReportSettingSalesTargetBlock> salesTargetReportSettingSalesTargetBlocks = salesTargetReportSettingSalesTargetBlockRepository
				.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSetting.getPid());
		List<String> pidLists = new ArrayList<>();
		for (SalesTargetReportSettingSalesTargetBlock salesTargetReportSettingSalesTargetBlock : salesTargetReportSettingSalesTargetBlocks) {
			pidLists.add(salesTargetReportSettingSalesTargetBlock.getSalesTargetBlock().getPid());
		}
		// find sales target Group
		List<SalesTargetGroup> salesTargetGroups = salesTargetBlockSalesTargetGroupRepository
				.findSalesTargetGroupsBySalesTargetBlockPidIn(pidLists);

		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		//
		Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetMap = new HashMap<>();
		if (!salesTargetGroups.isEmpty()) {
			SalesPerformaceDTO salesPerformaceDTO = new SalesPerformaceDTO();
			for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
				Set<Long> documents = salesTargetGroupDocumentRepository
						.findDocumentIdsBySalesTargetGroupName(salesTargetGroup.getName());
				Set<Long> productProfiles = salesTargetGroupProductRepository
						.findProductIdsBySalesTargetGroupName(salesTargetGroup.getName());

				List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetList = getSalesTargetGroupUserTargetByMonth(
						salesTargetGroup, monthsBetweenDates, userPid, isTerritoryWise, documents, productProfiles,
						salesTargetReportSetting);
				salesTargetGroupUserTargetMap.put(salesTargetGroup.getName(), salesTargetGroupUserTargetList);
			}
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

	private List<SalesTargetGroupUserTargetDTO> getSalesTargetGroupUserTargetByMonth(SalesTargetGroup salesTargetGroup,
			List<LocalDate> monthsBetweenDates, String userPid, boolean isTerritoryWise, Set<Long> documents,
			Set<Long> productProfiles, SalesTargetReportSetting salesTargetReportSetting) {
		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetList = new ArrayList<>();

		// loop months
		for (LocalDate monthDate : monthsBetweenDates) {
			LocalDate monthStartDate = monthDate.with(TemporalAdjusters.firstDayOfMonth());
			LocalDate monthEndDate = monthDate.with(TemporalAdjusters.lastDayOfMonth());

			SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();
			// check territory wise or user wise
			if (isTerritoryWise) {
				salesTargetGroupUserTargetDTO.setAchievedAmount(getAchievedAmountFromTransactionTerritoryWise(userPid,
						documents, productProfiles, salesTargetReportSetting, monthStartDate, monthEndDate));
				// if day wise, loop date
				if (TargetFrequency.DAY.equals(salesTargetReportSetting.getTargetPeriod())) {
					List<SalesTargetAchievedDTO> salesTargetAchievedDTOs = new ArrayList<>();
					LocalDate start = monthStartDate;
					LocalDate end = monthEndDate;
					double totalTarget = 0;
					for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
						SalesTargetAchievedDTO salesTargetAchievedDTO = new SalesTargetAchievedDTO();
						salesTargetAchievedDTO.setTargetAmountVolume(
								getTargetAmountVolume(userPid, salesTargetReportSetting, salesTargetGroup, date, date));
						salesTargetAchievedDTO.setDayDate(date);
						salesTargetAchievedDTO.setAchievedAmountVolume(getAchievedAmountFromTransactionTerritoryWise(
								userPid, documents, productProfiles, salesTargetReportSetting, date, date));
						totalTarget = totalTarget + salesTargetAchievedDTO.getTargetAmountVolume();
						salesTargetAchievedDTOs.add(salesTargetAchievedDTO);
					}
					salesTargetGroupUserTargetDTO.setTotalTarget(totalTarget);
					salesTargetGroupUserTargetDTO.setSalesTargetAchievedDTOs(salesTargetAchievedDTOs);
				}

			} else {
				salesTargetGroupUserTargetDTO.setAchievedAmount(getAchievedAmountFromTransactionUserWise(userPid,
						documents, productProfiles, salesTargetReportSetting, monthStartDate, monthEndDate));
				// if day wise, loop date
				if (TargetFrequency.DAY.equals(salesTargetReportSetting.getTargetPeriod())) {
					List<SalesTargetAchievedDTO> salesTargetAchievedDTOs = new ArrayList<>();
					LocalDate start = monthStartDate;
					LocalDate end = monthEndDate;
					double totalTarget = 0;
					for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
						SalesTargetAchievedDTO salesTargetAchievedDTO = new SalesTargetAchievedDTO();
						salesTargetAchievedDTO.setTargetAmountVolume(
								getTargetAmountVolume(userPid, salesTargetReportSetting, salesTargetGroup, date, date));
						salesTargetAchievedDTO.setDayDate(date);
						salesTargetAchievedDTO.setAchievedAmountVolume(getAchievedAmountFromTransactionUserWise(userPid,
								documents, productProfiles, salesTargetReportSetting, date, date));
						totalTarget = totalTarget + salesTargetAchievedDTO.getTargetAmountVolume();
						salesTargetAchievedDTOs.add(salesTargetAchievedDTO);
					}
					salesTargetGroupUserTargetDTO.setTotalTarget(totalTarget);
					salesTargetGroupUserTargetDTO.setSalesTargetAchievedDTOs(salesTargetAchievedDTOs);
				}
			}
			salesTargetGroupUserTargetList.add(salesTargetGroupUserTargetDTO);

		}
		return salesTargetGroupUserTargetList;
	}

	private double getTargetAmountVolume(String userPid, SalesTargetReportSetting salesTargetReportSetting,
			SalesTargetGroup salesTargetGroup, LocalDate start, LocalDate end) {
		Double targetAmountVolume = 0D;
		if (TargetType.AMOUNT.equals(salesTargetReportSetting.getTargetType())) {
			List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
					.findByUserPidAndSalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
							userPid, salesTargetGroup.getPid(), start, end);
			if (!salesTargetGroupUserTargets.isEmpty()) {
				targetAmountVolume = salesTargetGroupUserTargets.get(0).getAmount();
			}
		} else if (TargetType.VOLUME.equals(salesTargetReportSetting.getTargetType())) {
			List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
					.findByUserPidAndSalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
							userPid, salesTargetGroup.getPid(), start, end);
			if (!salesTargetGroupUserTargets.isEmpty()) {
				targetAmountVolume = salesTargetGroupUserTargets.get(0).getVolume();
			}
		}
		return targetAmountVolume == null ? 0D : targetAmountVolume;
	}

	private double getAchievedAmountFromTransactionUserWise(String userPid, Set<Long> documentIds,
			Set<Long> productProfileIds, SalesTargetReportSetting salesTargetReportSetting, LocalDate start,
			LocalDate end) {
		Double achievedAmountVolume = 0D;
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			// get achieved amount/volume
			if (TargetType.AMOUNT.equals(salesTargetReportSetting.getTargetType())) {
				achievedAmountVolume = inventoryVoucherDetailRepository
						.sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
								productProfileIds, start.atTime(0, 0), end.atTime(23, 59));
			} else if (TargetType.VOLUME.equals(salesTargetReportSetting.getTargetType())) {
				achievedAmountVolume = inventoryVoucherDetailRepository
						.sumOfVolumeByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
								productProfileIds, start.atTime(0, 0), end.atTime(23, 59));
			}
		}
		return achievedAmountVolume == null ? 0D : achievedAmountVolume;
	}

	private double getAchievedAmountFromTransactionTerritoryWise(String userPid, Set<Long> documents,
			Set<Long> productProfiles, SalesTargetReportSetting salesTargetReportSetting, LocalDate start,
			LocalDate end) {

		Double achievedAmountVolume = 0D;
		// user's account profile
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
		Set<Long> accountProfileIds = locationAccountProfileRepository
				.findAccountProfileIdsByUserLocationIdsIn(locationIds);
		if (!documents.isEmpty() && !productProfiles.isEmpty()) {
			// get achieved amount/volume
			if (TargetType.AMOUNT.equals(salesTargetReportSetting.getTargetType())) {
				achievedAmountVolume = inventoryVoucherDetailRepository
						.sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfileIds,
								documents, productProfiles, start.atTime(0, 0), end.atTime(23, 59));
			} else if (TargetType.VOLUME.equals(salesTargetReportSetting.getTargetType())) {
				achievedAmountVolume = inventoryVoucherDetailRepository
						.sumOfVolumeByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfileIds,
								documents, productProfiles, start.atTime(0, 0), end.atTime(23, 59));
			}

		}
		return achievedAmountVolume == null ? 0 : achievedAmountVolume;
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}
}
