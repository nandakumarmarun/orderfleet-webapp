package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.LeadToCashReportConfigRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.StageHeaderDTO;

@Controller
@RequestMapping("/web")
public class LeadToCashReportResource {

	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String TILLDATE = "TILLDATE";
	
	@Inject
	private LeadToCashReportConfigRepository leadToCashReportConfigRepository;
	
	@Inject
	private StageHeaderRepository stageHeaderRepository;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
	
	@Inject
	private FilledFormRepository filledFormRepository;

	@RequestMapping(value = "/lead-to-cash-report", method = RequestMethod.GET)
	public String stageReport(Model model) {
		List<String> leadToCash = leadToCashReportConfigRepository.findNamesByCompany();
		model.addAttribute("columnNames", leadToCash);
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/stage/lead-to-cash-report";
	}

	@RequestMapping(value = "/lead-to-cash-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String,List<Long>>> filterStageReport(@RequestParam("employeePids") List<String> employeePids, @RequestParam("filterBy") String filterBy, @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
		if (filterBy.equals(LeadToCashReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(LeadToCashReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(LeadToCashReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(LeadToCashReportResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(LeadToCashReportResource.TILLDATE)) {
			fromDate = LocalDate.of(2018, Month.JANUARY, 1);
			toDate = LocalDate.now();
		}
		Map<String,List<Long>> reportDatas = createLeadToCashReportByEmployeeAndDate(employeePids, fromDate, toDate);
		return new ResponseEntity<>(reportDatas, HttpStatus.OK);
	}

	private Map<String,List<Long>> createLeadToCashReportByEmployeeAndDate(List<String> employeePids, LocalDate fDate, LocalDate tDate) {
		//remove later
		final int leadIndex = 0;
		final int proposedIndex = 1;
		final int wonIndex = 2;
		final int lostIndex = 3;
		
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> stageIdConfigs = leadToCashReportConfigRepository.findStageIdOrderBySortOrderAsc();
		if(stageIdConfigs.isEmpty()) {
			return Collections.emptyMap();
		}
		//get lead account by employee
		List<Object[]> leadEmpAccountArray = stageHeaderRepository.findEmployeeNameAndAccountIdByEmployeePidInAndStageIdAndDateBetweenWithEmpPid(employeePids, stageIdConfigs.get(leadIndex), fromDate, toDate);
		if(leadEmpAccountArray.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map<String, List<Object[]>> groupLeadsByEmployee = leadEmpAccountArray.stream().collect(Collectors.groupingBy(obj -> obj[0].toString()+"~"+obj[3].toString()));
		Map<String,List<Long>> reportDatas = new LinkedHashMap<>();
		groupLeadsByEmployee.forEach((k,v) -> {
			List<Long> columns = new ArrayList<>();
//			columns.add(Long.valueOf(v.size()));
			
			Set<Long> accountIds = v.stream().map(obj -> (Long)obj[1]).collect(Collectors.toSet());
			//total Leads
			columns.add(Long.valueOf(accountIds.size()));
			List<StageHeader> stageHeaderByAccountProfile = stageHeaderRepository.findByAccountIdIn(new ArrayList<Long>(accountIds));

			Collection<Optional<StageHeader>> stageHeaders = 
					stageHeaderByAccountProfile.stream()
					.collect(Collectors.groupingBy(sh -> sh.getAccountProfile().getId(),
					Collectors.maxBy(Comparator.comparing(StageHeader::getCreatedDate)))).values();
			
			//proposed
			long proposedCount =0;
			if(stageHeaders != null && stageHeaders.size()!=0) {
				for(Optional<StageHeader> stageHead :stageHeaders) {
					if(stageHead.get().getStage().getId()==stageIdConfigs.get(proposedIndex)) {
						proposedCount += 1;
					}
				}
			}
			columns.add(proposedCount);
			
			//won
			long closedWonCount = 0;
			if(stageHeaders != null && stageHeaders.size()!=0) {
				for(Optional<StageHeader> stageHead :stageHeaders) {
					if(stageHead.get().getStage().getId()==stageIdConfigs.get(wonIndex)) {
						closedWonCount += 1;
					}
				}
			}
			columns.add(closedWonCount);
			
			
			//lost
			long closedLostCount = 0;
			if(stageHeaders != null && stageHeaders.size()!=0) {
				for(Optional<StageHeader> stageHead :stageHeaders) {
					if(stageHead.get().getStage().getId()==stageIdConfigs.get(lostIndex)) {
						closedLostCount += 1;
					}
				}
			}
			columns.add(closedLostCount);
			
			//close%
			columns.add((columns.get(wonIndex) * 100)/columns.get(leadIndex));
		
			long totalDays = 0;
			if(stageHeaders != null && stageHeaders.size()!=0) {
				for(Optional<StageHeader> stageHead :stageHeaders) {
					if(stageHead.get().getStage().getId()==stageIdConfigs.get(wonIndex)) {
						
						Optional<StageHeader> minCreatedDate = 
						stageHeaderByAccountProfile.stream()
						.filter(sth -> sth.getAccountProfile().getId() == stageHead.get().getAccountProfile().getId())
						.min(Comparator.comparing(StageHeader::getCreatedDate));
						
						long noOfDaysBetween = 
								ChronoUnit.DAYS.between(minCreatedDate.get().getCreatedDate(),
														stageHead.get().getCreatedDate());
						totalDays += noOfDaysBetween;
					}
				}
			}
			//lead to sales days
			if(columns.get(wonIndex)!=0) {
				columns.add(totalDays/columns.get(wonIndex));
			}else {
				columns.add(0L);
			}
			
			reportDatas.put(k, columns);
		});
		return reportDatas;
	}

}
