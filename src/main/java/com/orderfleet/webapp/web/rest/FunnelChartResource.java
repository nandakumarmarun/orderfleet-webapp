package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderfleet.webapp.domain.enums.ChartType;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.web.rest.chart.dto.FunnelChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;

@Controller
@RequestMapping("/web")
public class FunnelChartResource {
	
	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String TILLDATE = "TILLDATE";

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@RequestMapping(value = "/funnel-chart", method = RequestMethod.GET)
	public String customerJourney(Model model) {
		return "company/stage/funnel-chart";
	}
	
	@RequestMapping(value = "/funnel-chart/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardChartDTO> getFunnelChartData(@RequestParam("filterBy") String filterBy,
			@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
		if (filterBy.equals(FunnelChartResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(FunnelChartResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(FunnelChartResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(FunnelChartResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(FunnelChartResource.TILLDATE)) {
			fromDate = LocalDate.of(2018, Month.JANUARY, 1);
			toDate = LocalDate.now();
		}
		DashboardChartDTO dashboardChartDTO = new DashboardChartDTO();
		dashboardChartDTO.setChartType(ChartType.FUNNEL);
		dashboardChartDTO.setFunnelChartDtos(makeFunnelChart(fromDate, toDate));
		dashboardChartDTO.setChartLabel("Sales Funnel");
		return new ResponseEntity<>(dashboardChartDTO, HttpStatus.OK);
	}
	
	private List<FunnelChartDTO> makeFunnelChart(LocalDate fDate, LocalDate tDate) {
		List<FunnelChartDTO> funnelChartDtos = new ArrayList<>();
		List<Object[]> fNameAndCounts = stageHeaderRepository.findFunnelDetails(fDate.atTime(0, 0), tDate.atTime(23, 59));
		if(fNameAndCounts.isEmpty()) {
			return funnelChartDtos;
		}
		for (Object[] stage : fNameAndCounts) {
			String amount = stage[3] == null ? "0.00" : stage[3].toString();
			funnelChartDtos.add(new FunnelChartDTO(stage[1].toString() + "\nCount: "+ stage[2].toString() +" ", amount));
		}
		return funnelChartDtos;
	}

}
