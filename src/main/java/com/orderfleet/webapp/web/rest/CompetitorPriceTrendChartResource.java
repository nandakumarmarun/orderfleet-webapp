package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.PriceTrendConfiguration;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.repository.PriceTrendConfigurationRepository;
import com.orderfleet.webapp.repository.PriceTrendProductGroupRepository;
import com.orderfleet.webapp.repository.custom.ChartRepositoryCustom;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.chart.dto.BarCharrtDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendChartFilterDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing DashboardUser.
 * 
 * @author Muhammed Riyas T
 * @since Sep 20, 2016
 */
@Controller
@RequestMapping("/web/competitor-price-trend/chart")
public class CompetitorPriceTrendChartResource {

	private final Logger log = LoggerFactory.getLogger(CompetitorPriceTrendChartResource.class);

	private final ChartRepositoryCustom chartRepositoryCustom;
	
	private final EmployeeHierarchyService employeeHierarchyService;
	
	private final UserService userService;
	
	private final PriceTrendConfigurationService priceTrendConfigurationService;
	
	private final PriceTrendConfigurationRepository priceTrendConfigurationRepository;

	private final PriceTrendProductGroupService priceTrendProductGroupService;
	
	private final PriceTrendProductGroupRepository priceTrendProductGroupRepository;

	public CompetitorPriceTrendChartResource(ChartRepositoryCustom chartRepositoryCustom,
			EmployeeHierarchyService employeeHierarchyService, UserService userService,
			PriceTrendConfigurationService priceTrendConfigurationService,
			PriceTrendConfigurationRepository priceTrendConfigurationRepository,
			PriceTrendProductGroupService priceTrendProductGroupService,
			PriceTrendProductGroupRepository priceTrendProductGroupRepository) {
		super();
		this.chartRepositoryCustom = chartRepositoryCustom;
		this.employeeHierarchyService = employeeHierarchyService;
		this.userService = userService;
		this.priceTrendConfigurationService = priceTrendConfigurationService;
		this.priceTrendConfigurationRepository = priceTrendConfigurationRepository;
		this.priceTrendProductGroupService = priceTrendProductGroupService;
		this.priceTrendProductGroupRepository = priceTrendProductGroupRepository;
	}

	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public String getCompetitorPriceTrendChart(Model model) {
		log.debug("Web request to get a CompetitorPriceTrend chart");
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		List<PriceTrendConfigurationDTO> configurations = priceTrendConfigurationService.findAllByCompany();
		List<String> prices = new ArrayList<>();
		for (PriceTrendConfigurationDTO priceTrendConfigurationDTO : configurations) {
			if(!"remarks".equalsIgnoreCase(priceTrendConfigurationDTO.getName())){
				prices.add(priceTrendConfigurationDTO.getName());	
			}
		}
		model.addAttribute("productGroups", priceTrendProductGroupService.findAllByCompany());
		model.addAttribute("prices", prices);
		return "company/competitor-pricetrends-chart";
	}

	@Transactional
	@RequestMapping(value = "/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<BarCharrtDTO>> getBarChart(@RequestBody CompetitorPriceTrendChartFilterDTO cpTrendChartDto) {
		String filterBy = cpTrendChartDto.getFilterBy();
		LocalDate start = null;
		LocalDate end = null;
		if ("THIS_MONTH".equals(filterBy)) {
			start = LocalDate.now().withDayOfMonth(1);
			end = LocalDate.now();
		} else if ("PREVIOUS_MONTH".equals(filterBy)) {
			LocalDate previousMonth = LocalDate.now().minusMonths(1);
			start = previousMonth.withDayOfMonth(1);
			end = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());
		} else if ("CUSTOM".equals(filterBy)) {
			start = cpTrendChartDto.getFromDate();
			end = cpTrendChartDto.getToDate();
		}
		if(start == null || end == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("chartFilter", "dateNotExists", "Filter date not present."))
					.body(null);
		}
		List<BarCharrtDTO> barChartDTOs = new ArrayList<>();
		Set<PriceTrendProduct>  priceTrendProducts = new HashSet<>();
		List<PriceTrendProductGroup> pTrendProducts = priceTrendProductGroupRepository.findByProductGroupPid(cpTrendChartDto.getPtProductGroupPid());
		for (PriceTrendProductGroup priceTrendProductGroup : pTrendProducts) {
			priceTrendProducts.addAll( new ArrayList<>(priceTrendProductGroup.getPriceTrendProducts()));
		}
		Map<String, String> priceLevels = new LinkedHashMap<>();
		List<PriceTrendConfiguration> configurations = priceTrendConfigurationRepository.findByCompanyAndNameIn(cpTrendChartDto.getPriceLevelNames());
		for (PriceTrendConfiguration configuration : configurations) {
			priceLevels.put(configuration.getName(), configuration.getValue());
		}
		for (PriceTrendProduct priceTrendProduct : priceTrendProducts) {
			for(String priceLevelName : priceLevels.keySet()){
				BarCharrtDTO barChartDTO = chartRepositoryCustom.getCompetitorPriceTrendByUser(priceTrendProduct.getId(), cpTrendChartDto.getUserPid(), start.atTime(0, 0), end.atTime(23, 59), priceLevelName);
				if(barChartDTO != null) {
					barChartDTO.setTitleText(priceTrendProduct.getName() + " : " + priceLevels.get(priceLevelName));
					barChartDTOs.add(barChartDTO);
				}
			}
		}
		return new ResponseEntity<>(barChartDTOs, HttpStatus.OK);
	}
	
	@Transactional
	@RequestMapping(value = "/product-groups", method = RequestMethod.GET)
	@ResponseBody
	public List<PriceTrendProductDTO> getPriceTrendProductByProductGroup(@RequestParam(value="productGroupPids") String productGroupPids) {
		List<PriceTrendProductDTO> priceTrendProductDtos = new ArrayList<>();
		List<String> prodtGpPids = Arrays.asList(productGroupPids.split("\\s*,\\s*"));
		List<PriceTrendProductGroup> pTrendProducts = priceTrendProductGroupRepository.findByProductGroupPidIn(prodtGpPids);
		for (PriceTrendProductGroup priceTrendProductGroup : pTrendProducts) {
			List<PriceTrendProductDTO> ptps = priceTrendProductGroup.getPriceTrendProducts().stream().map(PriceTrendProductDTO::new).collect(Collectors.toList());
			priceTrendProductDtos.addAll(ptps);
		}
		return priceTrendProductDtos;
	}
	
}
