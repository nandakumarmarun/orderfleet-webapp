package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;

/**
 * Web controller for managing SetAccountSalesTargetResource.
 */
@Controller
@RequestMapping("/web")
public class SetAccountSalesTargetResource {

	private final Logger log = LoggerFactory.getLogger(SetAccountSalesTargetResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private SalesTargetGroupUserTargetService salesTargetGroupUserTargetService;

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private UserService userService;
	
	@Inject
	private LocationService locationService;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	/** account monthly sales targets */
	@Timed
	@RequestMapping(value = "/account-monthly-sales-targets", method = RequestMethod.GET)
	public String setAccountMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to set account Sales Target");
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));
		model.addAttribute("salesTargetGroups", salesTargetGroupService.findAllByCompany());
		return "company/set-account-monthly-sales-target";
	}
	
	@RequestMapping(value = "/account-monthly-sales-targets/load", method = RequestMethod.GET)
	public @ResponseBody List<SalesMonthlyTargetDTO> loadAccountMonthlySalesTargets(@RequestParam String salesTargetGroupPid, @RequestParam String monthAndYear, @RequestParam String filterBy) {
		List<AccountProfile> accountProfiles = new ArrayList<>();
		if("ALL".equals(filterBy)){
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfiles.addAll(accountProfileRepository.findAllByCompanyId());
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

		} else {
			//split
			List<String> alphabets = Arrays.asList(filterBy.split("\\s*-\\s*"));
			for (String alphabet : alphabets) {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AP_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get by name starting letter";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				accountProfiles.addAll(accountProfileRepository.findByNameStartingWith(alphabet));
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
			}
		}
		
		if (!accountProfiles.isEmpty()) {
			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<SalesMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (AccountProfile accountProfile : accountProfiles) {
				SalesMonthlyTargetDTO monthlyTargetDTO = new SalesMonthlyTargetDTO();
				monthlyTargetDTO.setAccountProfileName(accountProfile.getName());
				monthlyTargetDTO.setAccountProfilePid(accountProfile.getPid());
				//monthlyTargetDTO.setUserPid(userPid);

				List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
						.findBySalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
								salesTargetGroupPid, accountProfile.getPid(), firstDateMonth, lastDateMonth);
				if (salesTargetGroupUserTargets.size() > 0) {
					monthlyTargetDTO.setAmount(salesTargetGroupUserTargets.get(0).getAmount());
					monthlyTargetDTO.setVolume(salesTargetGroupUserTargets.get(0).getVolume());
					monthlyTargetDTO.setSalesTargetGroupUserTragetPid(salesTargetGroupUserTargets.get(0).getPid());
				} else {
					monthlyTargetDTO.setAmount(0);
					monthlyTargetDTO.setVolume(0);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/account-monthly-sales-targets", method = RequestMethod.POST)
	public @ResponseBody SalesMonthlyTargetDTO saveAccountWiseMonthlyActivityTargets(
			@RequestBody SalesMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save Account monthly Sales Targets");
		if (monthlyTargetDTO.getSalesTargetGroupUserTragetPid().equals("null")) {
			String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService.saveMonthlyTarget(monthlyTargetDTO,
					firstDateMonth, lastDateMonth);
			monthlyTargetDTO.setSalesTargetGroupUserTragetPid(result.getPid());
		} else {
			salesTargetGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupUserTragetPid())
					.ifPresent(activityUserTarget -> {
						activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
						activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
						salesTargetGroupUserTargetRepository.save(activityUserTarget);
					});
		}
		return monthlyTargetDTO;
	}
	
}