package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.orderfleet.webapp.domain.UserWiseSalesTarget;
import com.orderfleet.webapp.repository.UserWiseSalesTargetRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserWiseSalesTargetService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesTargetDTO;

/**
 * Web controller for managing UserWiseSalesTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class UserWiseSalesTargetResource {

	private final Logger log = LoggerFactory.getLogger(UserWiseSalesTargetResource.class);

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserWiseSalesTargetService userWiseSalesTargetService;

	@Inject
	private UserWiseSalesTargetRepository userWiseSalesTargetRepository;

	@Timed
	@RequestMapping(value = "/user-wise-monthly-sales-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set User Wise Sales Targets");
		model.addAttribute("employees", employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
		return "company/set-user-wise-monthly-sales-target";
	}

	@RequestMapping(value = "/user-wise-monthly-sales-targets/monthly-user-wise-sales-targets", method = RequestMethod.GET)
	public @ResponseBody List<UserWiseSalesMonthlyTargetDTO> monthlySalesTargets(@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly User Wise Sales Targets");

		List<EmployeeProfileDTO> employees = employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true);

		if (employees.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<UserWiseSalesMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (EmployeeProfileDTO employee : employees) {
				UserWiseSalesMonthlyTargetDTO monthlyTargetDTO = new UserWiseSalesMonthlyTargetDTO();
				monthlyTargetDTO.setUserPid(employee.getPid());
				monthlyTargetDTO.setUserName(employee.getName());

				List<UserWiseSalesTarget> userWiseSalesTargets = userWiseSalesTargetRepository
						.findByEmployeeProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(employee.getPid(),
								firstDateMonth, lastDateMonth);
				if (userWiseSalesTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesTargetGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (UserWiseSalesTarget userWiseSalesTarget : userWiseSalesTargets) {
						totalAmount = totalAmount + userWiseSalesTarget.getAmount();
						totalVolume = totalVolume + userWiseSalesTarget.getVolume();
					}
					monthlyTargetDTO.setAmount(totalAmount);
					monthlyTargetDTO.setVolume(totalVolume);
					monthlyTargetDTO.setUserWiseSalesTargetPid(userWiseSalesTargets.get(0).getPid());
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

	@RequestMapping(value = "/user-wise-monthly-sales-targets/monthly-user-wise-sales-targets", method = RequestMethod.POST)
	public @ResponseBody UserWiseSalesMonthlyTargetDTO saveMonthlyActivityTargets(
			@RequestBody UserWiseSalesMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Targets {}" + monthlyTargetDTO);

		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
				.findOneByPid(monthlyTargetDTO.getUserPid());
		if (employeeProfileDTO.isPresent()) {
			monthlyTargetDTO.setUserPid(employeeProfileDTO.get().getPid());
			if (monthlyTargetDTO.getUserWiseSalesTargetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				UserWiseSalesTargetDTO result = userWiseSalesTargetService.saveMonthlyTarget(monthlyTargetDTO,
						firstDateMonth, lastDateMonth);
				monthlyTargetDTO.setUserWiseSalesTargetPid(result.getPid());
			} else {
				userWiseSalesTargetRepository.findOneByPid(monthlyTargetDTO.getUserWiseSalesTargetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
							userWiseSalesTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

}