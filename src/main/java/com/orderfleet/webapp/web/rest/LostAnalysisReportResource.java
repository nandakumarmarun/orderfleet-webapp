package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import com.orderfleet.webapp.domain.StageHeaderRca;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.RootCauseAnalysisReasonRepository;
import com.orderfleet.webapp.repository.StageGroupRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.StageStageGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.StageHeaderDTO;

@Controller
@RequestMapping("/web")
public class LostAnalysisReportResource {

	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String TILLDATE = "TILLDATE";

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private RootCauseAnalysisReasonRepository rootCauseRepository;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@RequestMapping(value = "/lost-analysis-report", method = RequestMethod.GET)
	public String stageReport(Model model) {
		model.addAttribute("accountProfiles",accountProfileService.findByStageTypeName(StageNameType.CLOSED_LOST));
		model.addAttribute("rcas",rootCauseRepository.findAllByCompanyId());
		return "company/stage/lost-analysis-report";
	}
	
	@RequestMapping(value = "/lost-analysis-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<StageHeaderDTO>> filterStageReport(
			@RequestParam("rcaPid") String[] rcaPids,@RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy,@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
		if (filterBy.equals(LostAnalysisReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(LostAnalysisReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(LostAnalysisReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(LostAnalysisReportResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(LostAnalysisReportResource.TILLDATE)) {
			fromDate = LocalDate.of(2018, Month.JANUARY, 1);
			toDate = LocalDate.now();
		}
		List<StageHeaderDTO> stageHeaders = createStageReportByAccountAndDate(Arrays.asList(rcaPids), accountPid, fromDate, toDate);
		return new ResponseEntity<>(stageHeaders, HttpStatus.OK);
	}

	private List<StageHeaderDTO> createStageReportByAccountAndDate(
			List<String> rcaPids,String accountPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		
		Set<String> accountPids = new HashSet<>();
		accountPids.addAll(Arrays.asList(accountPid.split(",")));
		
		List<StageHeader> stageHeaders = stageHeaderRepository.findByUserIdInAndStagePidInAndAccountPidInAndDateBetween(
				StageNameType.CLOSED_LOST,accountPids, fromDate, toDate);
		
		

		Set<StageHeaderRca> stageHeaderRcaList = new HashSet<>();
		for(String rca : rcaPids) {
			List<StageHeaderRca> shRcaList = new ArrayList<>();
			for(StageHeader stageHeader : stageHeaders) {
				
				shRcaList = stageHeader.getStageHeaderRca().stream()
							.filter(shrca -> shrca.getRootCauseAnalysisReason().getPid().equals(rca)).collect(Collectors.toList());
				stageHeaderRcaList.addAll(shRcaList);
			}
			
		}
		
		return stageHeaderRcaList.stream().map(sh -> new StageHeaderDTO(sh.getStageHeader(), filledFormRepository))
				.collect(Collectors.toSet()).stream().collect(Collectors.toList());

	}
	

}
