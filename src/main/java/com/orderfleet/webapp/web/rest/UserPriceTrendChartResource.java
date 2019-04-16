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

import javax.inject.Inject;
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
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.web.rest.chart.dto.BarCharrtDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendChartFilterDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web/user-price-trend/chart")
public class UserPriceTrendChartResource {

	private final Logger log = LoggerFactory.getLogger(UserPriceTrendChartResource.class);

	@Inject
	private ChartRepositoryCustom chartRepositoryCustom;
	
	@Inject
	private PriceTrendConfigurationService priceTrendConfigurationService;
	
	@Inject
	private PriceTrendConfigurationRepository priceTrendConfigurationRepository;

	@Inject
	private PriceTrendProductGroupService priceTrendProductGroupService;
	
	@Inject
	private PriceTrendProductGroupRepository priceTrendProductGroupRepository;
	
	@Inject
	private CompetitorProfileService competitorProfileService;


	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public String getCompetitorPriceTrendChart(Model model) {
		log.debug("Web request to get a CompetitorPriceTrend chart");
		// competitor profiles
		List<CompetitorProfileDTO> competitorProfileDTOs = competitorProfileService.findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(true);
		model.addAttribute("activatedCompetitorProfiles", competitorProfileDTOs);
		
		List<PriceTrendConfigurationDTO> configurations = priceTrendConfigurationService.findAllByCompany();
		List<String> prices = new ArrayList<>();
		for (PriceTrendConfigurationDTO priceTrendConfigurationDTO : configurations) {
			if(!"remarks".equalsIgnoreCase(priceTrendConfigurationDTO.getName())){
				prices.add(priceTrendConfigurationDTO.getName());	
			}
		}
		model.addAttribute("productGroups", priceTrendProductGroupService.findAllByCompany());
		model.addAttribute("prices", prices);
		return "company/user-pricetrends-chart";
	}
	
	@Transactional
	@RequestMapping(value = "/load-chart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
		//get all pricetred products
		Set<PriceTrendProduct>  priceTrendProducts = new HashSet<>();
		List<PriceTrendProductGroup> pTrendProducts = priceTrendProductGroupRepository.findByProductGroupPid(cpTrendChartDto.getPtProductGroupPid());
		for (PriceTrendProductGroup priceTrendProductGroup : pTrendProducts) {
			priceTrendProducts.addAll( new ArrayList<>(priceTrendProductGroup.getPriceTrendProducts()));
		}
		//get price trends
		Map<String, String> priceLevels = new LinkedHashMap<>();
		List<PriceTrendConfiguration> configurations = priceTrendConfigurationRepository.findByCompanyAndNameIn(cpTrendChartDto.getPriceLevelNames());
		for (PriceTrendConfiguration configuration : configurations) {
			priceLevels.put(configuration.getName(), configuration.getValue());
		}
		
		for (PriceTrendProduct priceTrendProduct : priceTrendProducts) {
			for(String priceLevelName : priceLevels.keySet()){
				BarCharrtDTO barChartDTO = chartRepositoryCustom.getCompetitorPriceTrendByCompetitorProfile(priceTrendProduct.getId(), cpTrendChartDto.getCompetitorPid(), start.atTime(0, 0), end.atTime(23, 59), priceLevelName);
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
