package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupSalesTargetGroupDTO;

@Controller
@RequestMapping("/web")
public class ProductGroupSalesTargetGroupResource {
	private final Logger log = LoggerFactory.getLogger(ProductGroupSalesTargetGroupResource.class);

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupSalesTargetGroupService productGroupSalesTargetGroupService;

	@RequestMapping(value = "/product-group-sales-target-group/saveProGroSalesTrgGro", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> saveProductGroupSalesTargetGroup(@RequestParam String productGroupPid,
			@RequestParam String selectedSalesGroups) throws URISyntaxException {
		productGroupSalesTargetGroupService.saveProductGroupSalesTargetGroup(productGroupPid, selectedSalesGroups);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/product-group-sales-target-group", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductGroupSalesTargetGroup(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetGroups");
		model.addAttribute("salesTargetGroups", salesTargetGroupService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));
		return "company/productGroupSalesTargetGroup";
	}

	@RequestMapping(value = "/product-group-sales-target-group/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupSalesTargetGroupDTO>> getProductGroupSalesTargetGroups(
			@PathVariable String pid) {
		log.debug("Web request to get Division by pid : {}", pid);
		List<ProductGroupSalesTargetGroupDTO> productGroupSalesTargetGroupDTOs = new ArrayList<>();
		if (pid.equals("no")) {
			productGroupSalesTargetGroupDTOs = productGroupSalesTargetGroupService.findAllByCompanyId();
		} else {
			ProductGroupSalesTargetGroupDTO productGroupSalesTargetGroupDTO = productGroupSalesTargetGroupService
					.findAllByProductGroupPid(pid);
			if (productGroupSalesTargetGroupDTO != null) {
				productGroupSalesTargetGroupDTOs.add(productGroupSalesTargetGroupDTO);
			}
		}

		return new ResponseEntity<>(productGroupSalesTargetGroupDTOs, HttpStatus.OK);
	}
}
