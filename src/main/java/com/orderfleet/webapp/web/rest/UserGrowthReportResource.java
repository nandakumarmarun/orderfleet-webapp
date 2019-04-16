package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.rest.dto.UserGrowthReportDTO;

/**
 * Web controller for managing UserGrowth.
 *
 * @author Sarath
 * @since Apr 20, 2018
 *
 */
@Controller
@RequestMapping("/web")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class UserGrowthReportResource {

	private final Logger log = LoggerFactory.getLogger(UserGrowthReportResource.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@RequestMapping(value = "/user-growth-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserGrowthReportReport(Model model) {
		log.debug("Web request to get a page of user growth report");
		model.addAttribute("companies", companyRepository.findAllCompaniesByActivatedTrue());
		return "site_admin/user-growth-report";
	}

	@RequestMapping(value = "/user-growth-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public UserGrowthReportDTO performanceTargets(@RequestParam("companyPid") String companyPid,
			@RequestParam(value = "fromMonth", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromMonth,
			@RequestParam(value = "toMonth", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toMonth) {
		log.info("Rest Request to load user-growth-report date between {}, {}", fromMonth, toMonth);
		List<String> companyNames = new ArrayList<>();
		if (companyPid.equals("no")) {
			companyNames = companyRepository.findAllCompanyNameByActivatedTrueOrderByCreatedDate();
		} else {
			Optional<Company> opCompany = companyRepository.findOneByPid(companyPid);
			if (opCompany.isPresent()) {
				companyNames.add(opCompany.get().getLegalName());
			}
		}
		if(companyNames.isEmpty()) {
			return null;
		}
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromMonth, toMonth);
		if(monthsBetweenDates.isEmpty()) {
			return null;
		}
		UserGrowthReportDTO userGrowthReportDTO = new UserGrowthReportDTO();
		boolean doCalculateMonthWiseUserCount = true;
		for (String compName : companyNames) {
			Map<YearMonth, Long> userCountMap = new LinkedHashMap<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				YearMonth yearMonth = YearMonth.from(monthDate);
				ZonedDateTime fromDate = monthDate.atTime(0, 0).atZone(ZoneId.systemDefault());
				ZonedDateTime toDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth()).atTime(23, 59).atZone(ZoneId.systemDefault());
				Long userCount = userRepository.getCountofUserByCompanyNameAndCreatedDateBetween(compName, fromDate,
						toDate);
				userCountMap.put(yearMonth, userCount);
				if(doCalculateMonthWiseUserCount) {
					Long monthWiseUserCount = userRepository.getCountofUserByCompanyNameInAndCreatedDateBetween(companyNames, fromDate,toDate);
					StringBuilder monthName = new StringBuilder(monthDate.getMonth().toString());
					monthName.append("-").append(monthDate.getYear());
					if(monthWiseUserCount == null) {
						monthName.append("(0)");
					} else {
						monthName.append("(").append(monthWiseUserCount).append(")");
					}
					userGrowthReportDTO.getHtmlTblHeadermonths().add(monthName.toString());
				}
			}
			doCalculateMonthWiseUserCount = false;
			userGrowthReportDTO.getCompanyMonthWiseCount().put(compName, userCountMap);
		}
		return userGrowthReportDTO;
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}
}
