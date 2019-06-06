package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.domain.UserCustomerGroup;
import com.orderfleet.webapp.domain.UserCustomerGroupTarget;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupTargetRepository;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.ActivityUserTargetService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.service.UserCustomerGroupTargetService;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ActivityUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
@Controller
@RequestMapping("/web")
public class UsercustomerGroupTargetResource {

	private final Logger log = LoggerFactory.getLogger(UsercustomerGroupTargetResource.class);

	@Inject
	private UserCustomerGroupTargetService userCustomerGroupTargetService;

	@Inject
	private UserService userService;

	@Inject
	private UserCustomerGroupRepository userCustomerGroupRepository;

	@Inject
	private UserCustomerGroupTargetRepository userCustomerGroupTargetRepository;

	@Timed
	@RequestMapping(value = "/set-customer-group-targets", method = RequestMethod.GET)
	public String setActivityTargets(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Activity Target");
		model.addAttribute("users", userService.findAllByCompany());
		return "company/setCustomerGroupTarget";
	}

	@RequestMapping(value = "/set-customer-group-targets/monthly-customer-group-targets", method = RequestMethod.GET)
	public @ResponseBody List<MonthlyCustomerGroupTargetDTO> monthlyActivityTargets(@RequestParam String userPid,
			@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly Activity Targets");
		List<UserCustomerGroup> userCustomerGroups = userCustomerGroupRepository
				.findUserCustomerGroupsByUserPid(userPid);
		if (userCustomerGroups.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();

			List<MonthlyCustomerGroupTargetDTO> monthlyTargetDTOs = new ArrayList<>();
			for (UserCustomerGroup userCustomerGroup : userCustomerGroups) {
				MonthlyCustomerGroupTargetDTO monthlyTargetDTO = new MonthlyCustomerGroupTargetDTO();
				monthlyTargetDTO.setStagePid(userCustomerGroup.getStage().getPid());
				monthlyTargetDTO.setStageName(userCustomerGroup.getStage().getName());
				monthlyTargetDTO.setUserPid(userPid);
				List<UserCustomerGroupTarget> userCustomerGroupTargets = userCustomerGroupTargetRepository
						.findByUserPidAndStagePidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(userPid,
								userCustomerGroup.getStage().getPid(), firstDateMonth, lastDateMonth);
				if (userCustomerGroupTargets.size() > 0) {
					monthlyTargetDTO.setTarget(userCustomerGroupTargets.get(0).getTargetNumber());
					monthlyTargetDTO.setUserCustomerGroupTragetPid(userCustomerGroupTargets.get(0).getPid());
				} else {
					monthlyTargetDTO.setTarget(0L);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/set-customer-group-targets/monthly-customer-group-targets", method = RequestMethod.POST)
	public @ResponseBody MonthlyCustomerGroupTargetDTO saveMonthlyActivityTargets(
			@RequestBody MonthlyCustomerGroupTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Activity Targets");
		if (monthlyTargetDTO.getUserCustomerGroupTragetPid().equals("null")) {
			String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			UserCustomerGroupTargetDTO result = userCustomerGroupTargetService.saveMonthlyTarget(monthlyTargetDTO,
					firstDateMonth, lastDateMonth);
			monthlyTargetDTO.setUserCustomerGroupTragetPid(result.getPid());
		} else {
			userCustomerGroupTargetRepository.findOneByPid(monthlyTargetDTO.getUserCustomerGroupTragetPid())
					.ifPresent(userCustomerGroupTarget -> {
						userCustomerGroupTarget.setTargetNumber(monthlyTargetDTO.getTarget());
						userCustomerGroupTargetRepository.save(userCustomerGroupTarget);
					});
		}
		return monthlyTargetDTO;
	}

}
