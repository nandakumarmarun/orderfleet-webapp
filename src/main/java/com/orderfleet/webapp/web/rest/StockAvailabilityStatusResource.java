package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Web controller for managing ProductProfile.
 *
 * @author Sarath
 * @since Jan 8, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class StockAvailabilityStatusResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private ProductGroupProductService productGroupProductService;

	/**
	 * GET /stock-availability-status : get all the stock-availability-status in
	 * ascending order of name.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         stock-availability-status in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/stock-availability-status", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductProfiles(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductProfiles");
		model.addAttribute("productCategories", productCategoryService.findAllByCompanyAndDeactivated(true));
		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));
		model.addAttribute("productProfiles",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
		return "company/stock_availability_status";
	}

	@RequestMapping(value = "/stock-availability-status/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProductProfiles(@RequestParam String filter) {
		log.debug("Web request to get ProductProfile  by filter : {}", filter);

		List<ProductProfileDTO> result = new ArrayList<>();

		if (filter.equalsIgnoreCase("All")) {
			result = productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true);
		} else if (filter.equalsIgnoreCase("AVAILABLE")) {
			result = productProfileService
					.findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(true,
							StockAvailabilityStatus.AVAILABLE);
		} else if (filter.equalsIgnoreCase("OUT_OFF_STOCK")) {
			result = productProfileService
					.findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(true,
							StockAvailabilityStatus.OUT_OFF_STOCK);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/stock-availability-status/filterByGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProductProfilesbyGroup(@RequestParam String groupPid,
			@RequestParam String filter) {
		log.debug("Web request to get ProductProfile  by group pid : {}", groupPid);

		List<ProductProfileDTO> result = new ArrayList<>();

		if (filter.equalsIgnoreCase("no") && groupPid.equalsIgnoreCase("no")) {
			result = productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true);
		} else if (filter.equalsIgnoreCase("AVAILABLE") && groupPid.equalsIgnoreCase("no")) {
			result = productProfileService
					.findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(true,
							StockAvailabilityStatus.AVAILABLE);
		} else if (filter.equalsIgnoreCase("OUT_OFF_STOCK") && groupPid.equalsIgnoreCase("no")) {
			result = productProfileService
					.findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(true,
							StockAvailabilityStatus.OUT_OFF_STOCK);
		} else if (filter.equalsIgnoreCase("no") && !groupPid.equalsIgnoreCase("no")) {
			result = productGroupProductService.findEcomProductProfileByProductGroupPid(groupPid);
		} else if (filter.equalsIgnoreCase("AVAILABLE") && !groupPid.equalsIgnoreCase("no")) {
			result = productGroupProductService.findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(
					groupPid, true, StockAvailabilityStatus.AVAILABLE);
		} else if (filter.equalsIgnoreCase("OUT_OFF_STOCK") && !groupPid.equalsIgnoreCase("no")) {
			result = productGroupProductService.findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(
					groupPid, true, StockAvailabilityStatus.OUT_OFF_STOCK);
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/stock-availability-status/changeStockAvailabilityStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeStockAvailabilityStatus(@RequestParam("productPid") String productPid,
			@RequestParam("status") String status) throws URISyntaxException {
		log.debug("Web request to save Company Theme");
		Optional<ProductProfileDTO> productProfileDTO = productProfileService.findOneByPid(productPid);
		if (productProfileDTO.isPresent()) {
			productProfileDTO.get().setStockAvailabilityStatus(StockAvailabilityStatus.valueOf(status));
			productProfileService.update(productProfileDTO.get());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/stock-availability-status/changeMultipleStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeMultipleStockAvailabilityStatus(@RequestParam("productPids") String productPids,
			@RequestParam("status") String status) throws URISyntaxException {
		log.debug("Web request to save Company Theme");

		String[] accountGroup = productPids.split(",");
		for (String accountgroupPid : accountGroup) {
			Optional<ProductProfileDTO> productProfileDTO = productProfileService.findOneByPid(accountgroupPid);
			if (productProfileDTO.isPresent()) {
				productProfileDTO.get().setStockAvailabilityStatus(StockAvailabilityStatus.valueOf(status));
				productProfileService.update(productProfileDTO.get());
			}
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
