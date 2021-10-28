package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PerformanceReportMobileRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockSalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PerformanceReportMobileService;
import com.orderfleet.webapp.web.rest.dto.PerformanceReportMobileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;

@Service
@Transactional
public class PerformanceReportMobileServiceImpl implements PerformanceReportMobileService {

	private final Logger log = LoggerFactory.getLogger(PerformanceReportMobileServiceImpl.class);

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	private final PerformanceReportMobileRepository performanceReportMobileRepository;

	private final SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	private final SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository;

	private final SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	private final InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	private final SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	private final UserRepository userRepository;

	private final SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	private final InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	public PerformanceReportMobileServiceImpl(PerformanceReportMobileRepository performanceReportMobileRepository,
			SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository,
			SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository,
			SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository,
			InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository,
			SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository, UserRepository userRepository,
			SalesTargetGroupProductRepository salesTargetGroupProductRepository,
			InventoryVoucherDetailRepository inventoryVoucherDetailRepository) {
		super();
		this.performanceReportMobileRepository = performanceReportMobileRepository;
		this.salesTargetReportSettingSalesTargetBlockRepository = salesTargetReportSettingSalesTargetBlockRepository;
		this.salesTargetBlockSalesTargetGroupRepository = salesTargetBlockSalesTargetGroupRepository;
		this.salesTargetGroupUserTargetRepository = salesTargetGroupUserTargetRepository;
		this.inventoryVoucherHeaderRepository = inventoryVoucherHeaderRepository;
		this.salesTargetGroupDocumentRepository = salesTargetGroupDocumentRepository;
		this.userRepository = userRepository;
		this.salesTargetGroupProductRepository = salesTargetGroupProductRepository;
		this.inventoryVoucherDetailRepository = inventoryVoucherDetailRepository;
	}

	@Override
	public SalesTargetReportSettingDTO getAccountWiseSalesTargetPerformanceReport(MobileUINames mobileUIName,
			String accountProfilePids) {
		List<String> accountPids = Arrays.asList(accountProfilePids.split(","));
		List<SalesTargetReportSetting> salesTargetReportSettings = performanceReportMobileRepository
				.findAccountSalesTargetReportSettingByMobileUI(mobileUIName);
		if (salesTargetReportSettings != null && !salesTargetReportSettings.isEmpty()) {
			List<SalesTargetBlockDTO> salesTargetBlockDTOs = new ArrayList<>();
			// for an mobile UI and setting only one data exist
			SalesTargetReportSetting salesTargetReportSetting = salesTargetReportSettings.get(0);
			// check amount/volume - salesTargetReportSetting.getTargetType();
			// check period - salesTargetReportSetting.getTargetPeriod();

			// find salestargetblocks
			salesTargetReportSettingSalesTargetBlockRepository
					.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSetting.getPid()).forEach(strsStb -> {
						SalesTargetBlockDTO slBlockDTO = setAccountWiseTargetAndAchieved(
								new SalesTargetBlockDTO(strsStb.getSalesTargetBlock(), strsStb.getSortOrder()),
								strsStb.getCompany(), accountPids, salesTargetReportSetting);
						salesTargetBlockDTOs.add(slBlockDTO);
					});
			SalesTargetReportSettingDTO salesTargetReportSettingDTO = new SalesTargetReportSettingDTO(
					salesTargetReportSetting);
			salesTargetReportSettingDTO.setSalesTargetBlocks(salesTargetBlockDTOs);
			return salesTargetReportSettingDTO;
		}
		return null;
	}

	@Override
	public SalesTargetReportSettingDTO getUserWiseSalesTargetPerformanceReport(MobileUINames mobileUIName,
			String login) {
		Optional<User> user = userRepository.findOneByLogin(login);
		if (user.isPresent()) {
			List<SalesTargetReportSetting> salesTargetReportSettings = performanceReportMobileRepository
					.findUserSalesTargetReportSettingByMobileUI(mobileUIName);
			if (salesTargetReportSettings != null && !salesTargetReportSettings.isEmpty()) {
				List<SalesTargetBlockDTO> salesTargetBlockDTOs = new ArrayList<>();
				// for an mobile UI and setting only one data exist
				SalesTargetReportSetting salesTargetReportSetting = salesTargetReportSettings.get(0);

				// check territory wise or user wise
				Optional<CompanyConfiguration> optLocWiseCompanyConfig = companyConfigurationRepository
						.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.LOCATION_WISE);
				if (optLocWiseCompanyConfig.isPresent()) {
					salesTargetReportSettingSalesTargetBlockRepository
							.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSetting.getPid()).forEach(strsStb -> {
								SalesTargetBlockDTO slBlockDTO = setTerritoryWiseTargetAndAchieved(
										new SalesTargetBlockDTO(strsStb.getSalesTargetBlock(), strsStb.getSortOrder()),
										strsStb.getCompany(), user.get().getPid());
								salesTargetBlockDTOs.add(slBlockDTO);
							});
				} else {
					salesTargetReportSettingSalesTargetBlockRepository
							.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSetting.getPid()).forEach(strsStb -> {
								SalesTargetBlockDTO slBlockDTO = setUserWiseTargetAndAchieved(
										new SalesTargetBlockDTO(strsStb.getSalesTargetBlock(), strsStb.getSortOrder()),
										strsStb.getCompany(), user.get().getPid(),
										salesTargetReportSetting.getTargetPeriod());
								salesTargetBlockDTOs.add(slBlockDTO);
							});
				}
				SalesTargetReportSettingDTO salesTargetReportSettingDTO = new SalesTargetReportSettingDTO(
						salesTargetReportSetting);
				salesTargetReportSettingDTO.setSalesTargetBlocks(salesTargetBlockDTOs);
				return salesTargetReportSettingDTO;
			}
		}
		return null;
	}

	private SalesTargetBlockDTO setTerritoryWiseTargetAndAchieved(SalesTargetBlockDTO salesTargetBlockDTO,
			Company company, String userPid) {
		LocalDate startDate = findStartDate(salesTargetBlockDTO, company);
		LocalDate endDate = findEndDate(salesTargetBlockDTO, company);

		// find sales target group
		List<SalesTargetGroup> salesTargetGroups = salesTargetBlockSalesTargetGroupRepository
				.findSalesTargetGroupsBySalesTargetBlockPid(salesTargetBlockDTO.getPid());
		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(stg -> stg.getPid())
				.collect(Collectors.toList());

		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPid, startDate, endDate);
		Double targetAmount = 0d;
		Double targetVolume = 0d;
		Double achievedAmount = 0d;
		Double achievedVolume = 0d;
		// user's account profile
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		List<AccountProfile> accountProfiles = locationAccountProfileRepository
				.findAccountProfilesByUserLocationsOrderByAccountProfilesName(locations);
		if (!salesTargetGroupUserTargetList.isEmpty()) {
			for (SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargetList) {
				targetAmount += salesTargetGroupUserTarget.getAmount();
				targetVolume += salesTargetGroupUserTarget.getVolume();

				// get sales Target Group documents
				List<Document> documents = salesTargetGroupDocumentRepository
						.findDocumentsBySalesTargetGroupPid(salesTargetGroupUserTarget.getSalesTargetGroup().getPid());
				List<ProductProfile> productProfiles = salesTargetGroupProductRepository
						.findProductsBySalesTargetGroupPid(salesTargetGroupUserTarget.getSalesTargetGroup().getPid());
				if (!documents.isEmpty() && !productProfiles.isEmpty()) {
					// get achieved amount
					Double amount = inventoryVoucherDetailRepository
							.sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfiles,
									documents, productProfiles, startDate.atTime(0, 0), endDate.atTime(23, 59));
					// get achieved volume
					Double volume = inventoryVoucherDetailRepository
							.sumOfVolumeByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfiles,
									documents, productProfiles, startDate.atTime(0, 0), endDate.atTime(23, 59));
					// set achieved amount
					if (amount != null)
						achievedAmount += amount;
					// set achieved amount
					if (volume != null)
						achievedVolume += volume;
				}
			}
		}
		double roundedTrgtAmt = new BigDecimal(targetAmount.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		double roundedTrgtVlm = new BigDecimal(targetVolume.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		double roundedAchdAmt = new BigDecimal(achievedAmount.toString()).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		double roundedAchdVlm = new BigDecimal(achievedVolume.toString()).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		salesTargetBlockDTO.setTargetAmount(roundedTrgtAmt);
		salesTargetBlockDTO.setTargetVolume(roundedTrgtVlm);
		salesTargetBlockDTO.setAchievedAmount(roundedAchdAmt);
		salesTargetBlockDTO.setAchievedVolume(roundedAchdVlm);
		return salesTargetBlockDTO;
	}

	private SalesTargetBlockDTO setAccountWiseTargetAndAchieved(SalesTargetBlockDTO salesTargetBlockDTO,
			Company company, List<String> accountPids, SalesTargetReportSetting salesTargetReportSetting) {
		LocalDate startDate = findStartDate(salesTargetBlockDTO, company);
		LocalDate endDate = findEndDate(salesTargetBlockDTO, company);
		// find sales target group
		List<SalesTargetGroup> salesTargetGroups = salesTargetBlockSalesTargetGroupRepository
				.findSalesTargetGroupsBySalesTargetBlockPid(salesTargetBlockDTO.getPid());
		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(SalesTargetGroup::getPid)
				.collect(Collectors.toList());
		// get sales Target Group documents
		List<Document> documents = salesTargetGroupDocumentRepository
				.findDocumentsBySalesTargetGroupPid(salesTargetGroupPids);
		if (salesTargetReportSetting.getMonthlyAverageWise()) {
			// get months
			List<LocalDate> monthRanges = getMonthsBetweenDates(startDate, endDate);
			double targetAmt = 0;
			double targetVlm = 0;
			double achievedAmt = 0;
			double achievedVlm = 0;
			for (LocalDate localDate : monthRanges) {
				SalesTargetBlockDTO tempTarget = setSumOfAccountWiseTargetBetweenDate(salesTargetBlockDTO, accountPids,
						localDate, localDate);
				targetAmt += tempTarget.getTargetAmount();
				targetVlm += tempTarget.getTargetVolume();
				SalesTargetBlockDTO tempAchieved = setSumOfAccountWiseAchievedBetweenDate(salesTargetBlockDTO,
						accountPids, documents, localDate, localDate);
				achievedAmt += tempAchieved.getAchievedAmount();
				achievedVlm += tempAchieved.getAchievedVolume();
			}
			int months = monthRanges.size();
			double targetVlmAvg = targetVlm/months;
			double targetAmtAvg = targetAmt/months;
			double achievedAmtAvg = achievedAmt/months;
			double achievedVlmAvg = achievedVlm/months;
			salesTargetBlockDTO.setTargetVolume(BigDecimal.valueOf(targetVlmAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
			salesTargetBlockDTO.setTargetAmount(BigDecimal.valueOf(targetAmtAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
			salesTargetBlockDTO.setAchievedAmount(BigDecimal.valueOf(achievedAmtAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
			salesTargetBlockDTO.setAchievedVolume(BigDecimal.valueOf(achievedVlmAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
		} else {
			// find and set target achieved
			setSumOfAccountWiseTargetBetweenDate(salesTargetBlockDTO, accountPids, startDate, endDate);
			setSumOfAccountWiseAchievedBetweenDate(salesTargetBlockDTO, accountPids, documents, startDate, endDate);
		}
		return salesTargetBlockDTO;
	}

	private SalesTargetBlockDTO setSumOfAccountWiseAchievedBetweenDate(SalesTargetBlockDTO salesTargetBlockDTO,
			List<String> accountPids, List<Document> documents, LocalDate startDate, LocalDate endDate) {
		String id="INV_QUERY_124";
		String description="selecting sum of docTotal,docvolume of inv_voucher from inv_voucher_header where executivetaskexecution.accProfile.pid and create date between and doc in ";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
		Object sumOfAccountWiseAchieved = inventoryVoucherHeaderRepository
				.getAmountAndVolumeByAccountInAndDocumentsInDateBetween(accountPids, startDate.atTime(0, 0),
						endDate.atTime(23, 59), documents);
		Object[] achievedAmountVolume = (Object[]) sumOfAccountWiseAchieved;
		if (achievedAmountVolume[0] != null) {
			double roundedAchievedAmt = new BigDecimal(achievedAmountVolume[0].toString())
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			salesTargetBlockDTO.setAchievedAmount(roundedAchievedAmt);
		}
		if (achievedAmountVolume[1] != null) {
			double roundedAchievedVlm = new BigDecimal(achievedAmountVolume[1].toString())
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			salesTargetBlockDTO.setAchievedVolume(roundedAchievedVlm);
		}
		return salesTargetBlockDTO;
	}

	private SalesTargetBlockDTO setSumOfAccountWiseTargetBetweenDate(SalesTargetBlockDTO salesTargetBlockDTO,
			List<String> accountPids, LocalDate startDate, LocalDate endDate) {
		Object sumOfAccountWiseTargetBetweenDate = salesTargetGroupUserTargetRepository
				.findSumOfAccountWiseTarget(accountPids, startDate, endDate);
		Object[] targetAmountVolume = (Object[]) sumOfAccountWiseTargetBetweenDate;
		if (targetAmountVolume[0] != null) {
			double roundedTrgtAmt = new BigDecimal(targetAmountVolume[0].toString()).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			salesTargetBlockDTO.setTargetAmount(roundedTrgtAmt);
		}
		if (targetAmountVolume[1] != null) {
			double roundedTrgtVlm = new BigDecimal(targetAmountVolume[1].toString()).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			salesTargetBlockDTO.setTargetVolume(roundedTrgtVlm);
		}
		return salesTargetBlockDTO;
	}

	private SalesTargetBlockDTO setUserWiseTargetAndAchieved(SalesTargetBlockDTO salesTargetBlockDTO, Company company,
			String userPid, TargetFrequency targetFrequency) {
		LocalDate startDate = findStartDate(salesTargetBlockDTO, company);
		LocalDate endDate = findEndDate(salesTargetBlockDTO, company);
		if (TargetFrequency.DAY.equals(targetFrequency)) {
			startDate = LocalDate.now();
			endDate = LocalDate.now();
		}

		// find sales target group
		List<SalesTargetGroup> salesTargetGroups = salesTargetBlockSalesTargetGroupRepository
				.findSalesTargetGroupsBySalesTargetBlockPid(salesTargetBlockDTO.getPid());
		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(SalesTargetGroup::getPid)
				.collect(Collectors.toList());
		
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPid, startDate, endDate);
		Double targetAmount = 0d;
		Double targetVolume = 0d;
		Double achievedAmount = 0d;
		Double achievedVolume = 0d;
		if (!salesTargetGroupUserTargetList.isEmpty()) {
			for (SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargetList) {
				targetAmount += salesTargetGroupUserTarget.getAmount();
				targetVolume += salesTargetGroupUserTarget.getVolume();

				// get sales Target Group documents
				List<Document> documents = salesTargetGroupDocumentRepository
						.findDocumentsBySalesTargetGroupPid(salesTargetGroupUserTarget.getSalesTargetGroup().getPid());
				Set<Long> productProfiles = salesTargetGroupProductRepository
						.findProductIdBySalesTargetGroupPid(salesTargetGroupUserTarget.getSalesTargetGroup().getPid());
				if (!documents.isEmpty() && !productProfiles.isEmpty()) {
					// get achieved amount
					Double amount = inventoryVoucherDetailRepository
							.sumOfAmountByUserPidAndDocumentsAndProductIdInAndDocumentDateBetween(userPid, documents, productProfiles,
									startDate.atTime(0, 0), endDate.atTime(23, 59));
					// get achieved volume
					Double volume = inventoryVoucherDetailRepository
							.sumOfVolumeByUserPidAndDocumentsAndProductIdInAndDocumentDateBetween(userPid, documents, productProfiles,
									startDate.atTime(0, 0), endDate.atTime(23, 59));
					// set achieved amount
					if (amount != null)
						achievedAmount += amount;
					// set achieved amount
					if (volume != null)
						achievedVolume += volume;
				}
			}
		}
		double roundedTrgtAmt = new BigDecimal(targetAmount.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		double roundedTrgtVlm = new BigDecimal(targetVolume.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		double roundedAchdAmt = new BigDecimal(achievedAmount.toString()).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		double roundedAchdVlm = new BigDecimal(achievedVolume.toString()).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		salesTargetBlockDTO.setTargetAmount(roundedTrgtAmt);
		salesTargetBlockDTO.setTargetVolume(roundedTrgtVlm);
		salesTargetBlockDTO.setAchievedAmount(roundedAchdAmt);
		salesTargetBlockDTO.setAchievedVolume(roundedAchdVlm);
		return salesTargetBlockDTO;
	}

	private LocalDate findStartDate(SalesTargetBlockDTO salesTargetBlockDTO, Company company) {
		final Month month;
		if (salesTargetBlockDTO.getStartMonth() == 0) {
			month = LocalDate.now().minusMonths(salesTargetBlockDTO.getStartMonthMinus()).getMonth();
		} else {
			month = Month.of(salesTargetBlockDTO.getStartMonth());
		}
		List<LocalDate> monthRanges = getMonthsBetweenDates(
				company.getPeriodStartDate().minusYears(salesTargetBlockDTO.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(salesTargetBlockDTO.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		return LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
	}

	private LocalDate findEndDate(SalesTargetBlockDTO salesTargetBlockDTO, Company company) {
		final Month month;
		if (salesTargetBlockDTO.getEndMonth() == 0) {
			month = LocalDate.now().minusMonths(salesTargetBlockDTO.getEndMonthMinus()).getMonth();
		} else {
			month = Month.of(salesTargetBlockDTO.getEndMonth());
		}
		List<LocalDate> monthRanges = getMonthsBetweenDates(
				company.getPeriodStartDate().minusYears(salesTargetBlockDTO.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(salesTargetBlockDTO.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		LocalDate initial = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
		return initial.withDayOfMonth(initial.lengthOfMonth());
	}

	private List<LocalDate> getMonthsBetweenDates(LocalDate periodStartDate, LocalDate periodEndDate) {
		List<LocalDate> monthRanges = new ArrayList<>();
		while (!periodStartDate.isAfter(periodEndDate)) {
			monthRanges.add(periodStartDate);
			periodStartDate = periodStartDate.plusMonths(1);
		}
		return monthRanges;
	}

	/*
	 * @author sarath
	 * 
	 * @since mar 2, 2017
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PerformanceReportMobileDTO> findOneByMobileUINameAndalesTargetReportSetting(
			MobileUINames mobileUIName, String salesTargetReportSettingPid) {
		log.debug("Request to get PerformanceReportMobile by salesTargetReportSettingPid  : ",
				salesTargetReportSettingPid + "  and mobileUIName:" + mobileUIName);

		return performanceReportMobileRepository
				.findOneByMobileUINameAndalesTargetReportSetting(mobileUIName, salesTargetReportSettingPid)
				.map(performanceReportMobile -> {
					PerformanceReportMobileDTO performanceReportMobileDTO = new PerformanceReportMobileDTO(
							performanceReportMobile);
					return performanceReportMobileDTO;
				});

	}

	/*
	 * @author sarath
	 * 
	 * @since mar 2, 2017
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PerformanceReportMobileDTO> findOneBySalesTargetReportSettingPid(
			String salesTargetReportSettingPid) {
		log.debug("Request to get PerformanceReportMobile by salesTargetReportSettingPid ",
				salesTargetReportSettingPid);

		return performanceReportMobileRepository.findOneBySalesTargetReportSettingPid(salesTargetReportSettingPid)
				.map(performanceReportMobile -> {
					PerformanceReportMobileDTO performanceReportMobileDTO = new PerformanceReportMobileDTO(
							performanceReportMobile);
					return performanceReportMobileDTO;
				});
	}
}
