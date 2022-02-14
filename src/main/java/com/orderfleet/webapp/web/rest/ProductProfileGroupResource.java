package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Resource for ProductProfileGroup
 *
 * @author fahad
 * @since Feb 28, 2017
 */
@Controller
@RequestMapping("/web")
public class ProductProfileGroupResource {

	private final Logger log = LoggerFactory.getLogger(ProductProfileGroupResource.class);

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductProfileService productProfileService;
	
	@Inject
	private ProductGroupMapper productGroupMapper;
	
	@Inject
	private ProductProfileMapper productProfileMapper;

	@RequestMapping(value = "/productProfileGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductProfileGroups");
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		return "company/productProfileGroups";
	}

	@RequestMapping(value = "/productProfileGroups/filterByCategoryGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<ProductProfileGroupDTO>> filterProductProfilesByCategoryAndGroup(
			@RequestParam String categoryPids, @RequestParam String groupPids) throws URISyntaxException {
		List<ProductProfileGroupDTO> productProfileGroupDTOs = new ArrayList<>();
		// none selected
		if (categoryPids.isEmpty() && groupPids.isEmpty()) {
			List<ProductGroupProduct> productGroupProducts = productGroupProductService
					.findAllByCompanyAndActivated(true);
			productProfileGroupDTOs = addListToProductGroupProductDTOs(productGroupProducts);

			List<ProductProfileDTO> productProfileDTOs = productProfileService
					.findAllByCompanyAndActivatedProductProfileOrderByName(true);

			for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
				List<ProductProfileDTO> ProductDtos = productGroupProductService
						.findAllByProductProfilePid(productProfileDTO.getPid());
				if (ProductDtos.isEmpty()) {
					ProductProfileGroupDTO productProfileGroupDTO = new ProductProfileGroupDTO();
					productProfileGroupDTO.setProductProfileDTO(productProfileDTO);
					productProfileGroupDTOs.add(productProfileGroupDTO);
				}
			}

			return new ResponseEntity<>(productProfileGroupDTOs, HttpStatus.OK);
		}
		// both selected
		if (!categoryPids.isEmpty() && !groupPids.isEmpty()) {
			List<ProductGroupProduct> productGroupProducts = productGroupProductService
					.findProductGroupProductByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
							Arrays.asList(groupPids.split(",")), Arrays.asList(categoryPids.split(",")));
			productProfileGroupDTOs = addListToProductGroupProductDTOs(productGroupProducts);
			return new ResponseEntity<>(productProfileGroupDTOs, HttpStatus.OK);
		}
		// category selected
		if (!categoryPids.isEmpty() && groupPids.isEmpty()) {
			List<ProductGroupProduct> productGroupProducts = productGroupProductService
					.findProductGroupProductByProductCategoryPidsAndActivated(Arrays.asList(categoryPids.split(",")));
			productProfileGroupDTOs = addListToProductGroupProductDTOs(productGroupProducts);
			return new ResponseEntity<>(productProfileGroupDTOs, HttpStatus.OK);
		}
		// group selected
		if (categoryPids.isEmpty() && !groupPids.isEmpty()) {
			List<ProductGroupProduct> productGroupProducts = productGroupProductService
					.findProductGroupProductByProductGroupPidsAndActivated(Arrays.asList(groupPids.split(",")));
			productProfileGroupDTOs = addListToProductGroupProductDTOs(productGroupProducts);
			return new ResponseEntity<>(productProfileGroupDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(productProfileGroupDTOs, HttpStatus.OK);
	}

	private List<ProductProfileGroupDTO> addListToProductGroupProductDTOs(
			List<ProductGroupProduct> productGroupProducts) {

		List<ProductProfileGroupDTO> productProfileGroupDTOs = new ArrayList<>();
		Map<String, List<ProductGroupProduct>> productGroupProductMap = productGroupProducts.parallelStream()
				.collect(Collectors.groupingBy(pl -> pl.getProduct().getName()));
		for (Map.Entry<String, List<ProductGroupProduct>> entry : productGroupProductMap.entrySet()) {

			ProductProfileGroupDTO productProfileGroupDTO = new ProductProfileGroupDTO();
			ProductProfileDTO productProfileDTO =productProfileMapper.productProfileToProductProfileDTODescription(entry.getValue().get(0).getProduct());
			productProfileGroupDTO.setProductProfileDTO(productProfileDTO);
			List<ProductGroupDTO> productGroupDTOs = new ArrayList<>();
			for (ProductGroupProduct productGroupProduct : entry.getValue()) {
//				ProductGroupDTO groupDTO = new ProductGroupDTO(productGroupProduct.getProductGroup());
				ProductGroupDTO groupDTO =productGroupMapper.productGroupToProductGroupDTODescription(productGroupProduct.getProductGroup());
				productGroupDTOs.add(groupDTO);
			}
			
			productProfileGroupDTO.setProductGroupDTOs(productGroupDTOs);
			productProfileGroupDTOs.add(productProfileGroupDTO);
		}
		return productProfileGroupDTOs;
	}

}
