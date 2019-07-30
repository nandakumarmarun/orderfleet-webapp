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
import com.orderfleet.webapp.domain.UserWiseReceiptTarget;
import com.orderfleet.webapp.repository.UserWiseReceiptTargetRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserWiseReceiptTargetService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptTargetDTO;

/**
 * Web controller for managing SalesTargetGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class UserWiseReceiptTargetResource {

	private final Logger log = LoggerFactory.getLogger(UserWiseReceiptTargetResource.class);

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserWiseReceiptTargetService userWiseReceiptTargetService;

	@Inject
	private UserWiseReceiptTargetRepository userWiseReceiptTargetRepository;

	@Timed
	@RequestMapping(value = "/user-wise-monthly-receipt-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set User Wise Receipt Targets");
		model.addAttribute("employees", employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
		return "company/set-user-wise-monthly-receipt-target";
	}

	@RequestMapping(value = "/user-wise-monthly-receipt-targets/monthly-user-wise-receipt-targets", method = RequestMethod.GET)
	public @ResponseBody List<UserWiseReceiptMonthlyTargetDTO> monthlySalesTargets(@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly User Wise Receipts Targets");

		List<EmployeeProfileDTO> employees = employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true);

		if (employees.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<UserWiseReceiptMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (EmployeeProfileDTO employee : employees) {
				UserWiseReceiptMonthlyTargetDTO monthlyTargetDTO = new UserWiseReceiptMonthlyTargetDTO();
				monthlyTargetDTO.setUserPid(employee.getPid());
				monthlyTargetDTO.setUserName(employee.getName());

				List<UserWiseReceiptTarget> userWiseReceiptTargets = userWiseReceiptTargetRepository
						.findByEmployeeProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(employee.getPid(),
								firstDateMonth, lastDateMonth);
				if (userWiseReceiptTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesTargetGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (UserWiseReceiptTarget userWiseReceiptTarget : userWiseReceiptTargets) {
						totalAmount = totalAmount + userWiseReceiptTarget.getAmount();
						totalVolume = totalVolume + userWiseReceiptTarget.getVolume();
					}
					monthlyTargetDTO.setAmount(totalAmount);
					monthlyTargetDTO.setVolume(totalVolume);
					monthlyTargetDTO.setUserWiseReceiptTargetPid(userWiseReceiptTargets.get(0).getPid());
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

	@RequestMapping(value = "/user-wise-monthly-receipt-targets/monthly-user-wise-receipt-targets", method = RequestMethod.POST)
	public @ResponseBody UserWiseReceiptMonthlyTargetDTO saveMonthlyActivityTargets(
			@RequestBody UserWiseReceiptMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Targets {}" + monthlyTargetDTO);

		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
				.findOneByPid(monthlyTargetDTO.getUserPid());
		if (employeeProfileDTO.isPresent()) {
			monthlyTargetDTO.setUserPid(employeeProfileDTO.get().getPid());
			if (monthlyTargetDTO.getUserWiseReceiptTargetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				UserWiseReceiptTargetDTO result = userWiseReceiptTargetService.saveMonthlyTarget(monthlyTargetDTO,
						firstDateMonth, lastDateMonth);
				monthlyTargetDTO.setUserWiseReceiptTargetPid(result.getPid());
			} else {
				userWiseReceiptTargetRepository.findOneByPid(monthlyTargetDTO.getUserWiseReceiptTargetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
							userWiseReceiptTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

}