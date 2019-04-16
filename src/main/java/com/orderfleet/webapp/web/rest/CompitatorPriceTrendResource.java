package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.repository.PriceTrendProductGroupRepository;
import com.orderfleet.webapp.repository.custom.ChartRepositoryCustom;
import com.orderfleet.webapp.service.CompetitorPriceTrendService;
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.service.PriceTrendProductService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.chart.dto.BarCharrtDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;

/**
 * Web controller for managing DashboardUser.
 * 
 * @author Muhammed Riyas T
 * @since Sep 20, 2016
 */
@Controller
@RequestMapping("/web")
public class CompitatorPriceTrendResource {

	private final Logger log = LoggerFactory.getLogger(CompitatorPriceTrendResource.class);

	private final CompetitorPriceTrendService competitorPriceTrendService;

	private final PriceTrendConfigurationService priceTrendConfigurationService;
	
	private final UserService userService;
	
	@Inject
	private PriceTrendProductService priceTrendProductService;

	@Inject
	private PriceTrendProductGroupService priceTrendProductGroupService;
	
	@Inject
	private PriceTrendProductGroupRepository priceTrendProductGroupRepository;
	
	@Inject
	private CompetitorProfileService competitorProfileService;
	
	@Inject
	private ChartRepositoryCustom chartRepositoryCustom;
	
	public CompitatorPriceTrendResource(CompetitorPriceTrendService competitorPriceTrendService,
			PriceTrendConfigurationService priceTrendConfigurationService,UserService userService) {
		super();
		this.competitorPriceTrendService = competitorPriceTrendService;
		this.priceTrendConfigurationService = priceTrendConfigurationService;
		this.userService = userService;
	}

	/**
	 * GET /competitor-profiles : get all the competitorProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         competitorProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/competitor-price-trends", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllCompetitorPriceTrend(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of CompetitorPriceTrend");
		List<PriceTrendConfigurationDTO> configurations = priceTrendConfigurationService.findAllByCompany();
		for (PriceTrendConfigurationDTO priceTrendConfigurationDTO : configurations) {
			model.addAttribute(priceTrendConfigurationDTO.getName(), true);
		}
		model.addAttribute("pageCompetitorPriceTrend", competitorPriceTrendService.findAllByCompany(pageable));
		model.addAttribute("products", priceTrendProductService.findAllByCompany());
		model.addAttribute("productGroups", priceTrendProductGroupService.findAllByCompany());
		model.addAttribute("competitors", competitorProfileService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/competitorPriceTrends";
	}

	@RequestMapping(value = "/competitor-price-trends/getAllPriceTrendConfiguration", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<PriceTrendConfigurationDTO>> getAllConfiguration()  {
		log.debug("Web request to get a page of CompetitorPriceTrend");
		List<PriceTrendConfigurationDTO> configurations = priceTrendConfigurationService.findAllByCompany();
		return new ResponseEntity<List<PriceTrendConfigurationDTO>>(configurations, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/competitor-price-trends/filterByProductProductGroupCompetitor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<CompetitorPriceTrendDTO>> filterByProductProductGroupCompetitor(
			@RequestParam String productPids, @RequestParam String productGroupPids, @RequestParam String competitorPids) throws URISyntaxException {
		List<CompetitorPriceTrendDTO> competitorPriceTrendDTOs = new ArrayList<>();
		// none selected
		if (productPids.isEmpty() && productGroupPids.isEmpty() && competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService.findAllByCompany());
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (!productPids.isEmpty() && !productGroupPids.isEmpty() && !competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService.findByProductProductGroupCompetitor(Arrays.asList(productPids.split(",")), Arrays.asList(productGroupPids.split(",")), Arrays.asList(competitorPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);

		}
		if (!productPids.isEmpty() && productGroupPids.isEmpty() && competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByProductPidIn(Arrays.asList(productPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (productPids.isEmpty() && !productGroupPids.isEmpty() && competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByProductGroupPidIn(Arrays.asList(productGroupPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (productPids.isEmpty() && productGroupPids.isEmpty() && !competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByCompetitorPidIn(Arrays.asList(competitorPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (!productPids.isEmpty() && !productGroupPids.isEmpty() && competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByProductPidInAndProductGroupPidIn(Arrays.asList(productPids.split(",")), Arrays.asList(productGroupPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (productPids.isEmpty() && !productGroupPids.isEmpty() && !competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByProductGroupPidInAndCompetitorPidIn(Arrays.asList(productGroupPids.split(",")), Arrays.asList(competitorPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		if (!productPids.isEmpty() && productGroupPids.isEmpty() && !competitorPids.isEmpty()) {
			competitorPriceTrendDTOs.addAll(competitorPriceTrendService
					.findProductByProductPidInAndCompetitorPidIn(Arrays.asList(productPids.split(",")), Arrays.asList(competitorPids.split(","))));
			return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(competitorPriceTrendDTOs, HttpStatus.OK);
	}
	
	@Transactional
	@RequestMapping(value = "/competitor-price-trends/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, List<BarCharrtDTO>>> filterExecutiveTaskExecutions(@RequestParam String userPid, @RequestParam String productGroupPid, @RequestParam("filterBy") String filterBy,
		@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter CompetitorPriceTrends");
		 if (filterBy.equals("THISWEEK")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			return new ResponseEntity<>(getFilterData(userPid,productGroupPid, weekStartDate, LocalDate.now()),HttpStatus.OK);
		} else if (filterBy.equals("THISMONTH")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			return new ResponseEntity<>(getFilterData(userPid,productGroupPid, monthStartDate, LocalDate.now()),HttpStatus.OK);
		} else if (filterBy.equals("LASTMONTH")) {
			LocalDate firstOfThisMonth = LocalDate.now().withDayOfMonth( 1 );
			LocalDate monthStartDate = firstOfThisMonth.minusMonths(1);
			LocalDate monthEndDate =firstOfThisMonth.minusDays(1);
			return new ResponseEntity<>(getFilterData(userPid,productGroupPid, monthStartDate, monthEndDate),HttpStatus.OK);
		} 
		else if (filterBy.equals("CUSTOMDATE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			return new ResponseEntity<>(getFilterData(userPid,productGroupPid, fromDateTime, toFateTime),HttpStatus.OK);
		}
		else if (filterBy.equals("CUSTOMMONTH")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			return new ResponseEntity<>(getFilterData(userPid,productGroupPid, fromDateTime, toFateTime),HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private Map<String, List<BarCharrtDTO>> getFilterData(String userPid,String productGroupPid,LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		Set<PriceTrendProduct>  priceTrendProducts = new HashSet<>();
		List<PriceTrendProductGroup> pTrendProducts = priceTrendProductGroupRepository.findByProductGroupPid(productGroupPid);
		for (PriceTrendProductGroup priceTrendProductGroup : pTrendProducts) {
			priceTrendProducts.addAll( new ArrayList<>(priceTrendProductGroup.getPriceTrendProducts()));
		}
		List<PriceTrendConfigurationDTO> configurations = priceTrendConfigurationService.findAllByCompany();
		Map<String, String> prices = new LinkedHashMap<>();
		for (PriceTrendConfigurationDTO priceTrendConfigurationDTO : configurations) {
			if(!"remarks".equalsIgnoreCase(priceTrendConfigurationDTO.getName())){
				prices.put(priceTrendConfigurationDTO.getName(), priceTrendConfigurationDTO.getValue());
			}
		}
		Map<String, List<BarCharrtDTO>> result = new LinkedHashMap<>();
		for (PriceTrendProduct priceTrendProduct : priceTrendProducts) {
			List<BarCharrtDTO> barChartDTOs = new ArrayList<>();
			for(String priceLevelName : prices.keySet()){
				BarCharrtDTO barChartDTO = chartRepositoryCustom.getCompetitorPriceTrendByUser(priceTrendProduct.getId(), userPid, fromDate, toDate, priceLevelName);
				if(barChartDTO != null) {
					barChartDTO.setTitleText(prices.get(priceLevelName));
					barChartDTOs.add(barChartDTO);
				}
			}
			result.put(priceTrendProduct.getName(), barChartDTOs);
		}
		return result;
	}
}
