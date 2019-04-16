package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ProductGroupPrice;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.ProductGroupPriceRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupPriceDTO;

/**
 * Web controller for managing ProductGroupPrice.
 * 
 * @author Shaheer
 * @since December 12, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductGroupPriceResource {

	private final Logger log = LoggerFactory.getLogger(ProductGroupPriceResource.class);

	@Inject
	private ProductGroupPriceRepository productGroupPriceRepository;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	/**
	 * GET /productGroupPrice : get all the productGroupPrices.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupPrice in body
	 */
	@GetMapping(value = "/productGroupPrice")
	@Timed
	public String getAllProductGroupPrice(Model model) {
		log.debug("Web request to get all productGroupPrice");
		List<ProductGroupDTO> productGroupDTOs = productGroupService.findAllByCompany();
		List<ProductGroupPrice> productGroupPrices = productGroupPriceRepository.findByCompanyIsCurrentCompany();
		model.addAttribute("productGroups", productGroupDTOs);
		model.addAttribute("productGroupPrices",
				productGroupPrices.parallelStream().map(ProductGroupPriceDTO::new).collect(Collectors.toList()));
		return "company/productGroupPrice";
	}

	/**
	 * POST /productGroupPrice : Create a new productGroupPrice.
	 *
	 * @param productGroupPriceDTO
	 *            the productGroupPriceDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new productGroupPriceDTO, or with status 400 (Bad Request) if the
	 *         productGroupPriceDTO has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PostMapping(value = "/productGroupPrice")
	@Timed
	public String createProductGroup(@RequestParam("productGroupPid") String productGroupPid,
			@RequestParam("price") BigDecimal price) throws URISyntaxException {
		log.debug("Web request to save ProductGroupPrice : {} , {}", productGroupPid, price);
		productGroupRepository.findOneByPid(productGroupPid).ifPresent(pg -> {
			ProductGroupPrice pgPrice = productGroupPriceRepository.findByProductGroupPidAndCurrentCompany(pg.getPid());
			if (pgPrice == null) {
				pgPrice = new ProductGroupPrice();
				pgPrice.setCompany(pg.getCompany());
			}
			pgPrice.setProductGroup(pg);
			pgPrice.setPrice(price);
			// save or update product group price.
			productGroupPriceRepository.save(pgPrice);
			// update product profile price.
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPid(productGroupPid);
			if (productProfiles.size() > 0) {
				for (ProductProfile productProfile : productProfiles) {
					productProfile.setPrice(price);
				}
				productProfileRepository.save(productProfiles);
			}
		});
		return "redirect:/web/productGroupPrice";
	}

	@GetMapping(value = "/productGroupPrice/{id}/update")
	public String updateProductGroupPrice(@PathVariable("id") Long id, @RequestParam("price") BigDecimal price) {
		log.debug("Web request to update ProductGroupPrice : {}, {}", id, price);
		ProductGroupPrice pgPrice = productGroupPriceRepository.findOne(id);
		if (pgPrice != null) {
			pgPrice.setPrice(price);
			// update product group price.
			productGroupPriceRepository.save(pgPrice);
			// update product profile price.
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPid(pgPrice.getProductGroup().getPid());
			if (productProfiles.size() > 0) {
				for (ProductProfile productProfile : productProfiles) {
					productProfile.setPrice(price);
				}
				productProfileRepository.save(productProfiles);
			}
		}
		return "redirect:/web/productGroupPrice";
	}

}
