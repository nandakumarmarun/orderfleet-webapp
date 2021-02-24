package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TPUnitOfMeasureProductDTO;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooProductProfile;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * Web controller for managing ActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Controller
@RequestMapping("/web")
public class OdooApiProductViewResource {

	private final Logger log = LoggerFactory.getLogger(OdooApiProductViewResource.class);

	private static String PRODUCT_API_URL = "http://edappal.nellara.com:1214/web/api/products";

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;
	@Inject
	private DivisionRepository divisionRepository;

	@RequestMapping(value = "/odoo-api-products", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivityGroups(Model model)
			throws URISyntaxException, IOException, JSONException, ParseException {
		log.debug("Web request to get a page of Odoo Products");

		model.addAttribute("products", getOdooProducts());

		return "company/odooApiProducts";
	}

	@Timed
	@RequestMapping(value = "/odoo-api-products/addProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductProfileDTO> addProduct(@Valid @RequestParam String productName,
			@Valid @RequestParam String productId) {
		log.debug("REST request to Save Product");

		log.info("Saving Product Profiles........." + productName + "(" + productId + ")");

		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		// find all exist product profiles

		Optional<ProductProfile> opProductProfile = productProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId,
				productName);

		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(company.getId());
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		String cat = productCategorys.get(0).getName();
		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), cat);
		ProductCategory productCategory = new ProductCategory();
		if (!defaultCategory.isPresent()) {
			productCategory = new ProductCategory();
			productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
			productCategory.setName("Not Applicable");
			productCategory.setDataSourceType(DataSourceType.TALLY);
			productCategory.setCompany(company);
			productCategory = productCategoryRepository.save(productCategory);
		} else {
			productCategory = defaultCategory.get();
		}

		ProductProfile productProfile = new ProductProfile();
		if (opProductProfile.isPresent()) {
			productProfile = opProductProfile.get();
		} else {
			productProfile = new ProductProfile();
			productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
			productProfile.setCompany(company);
			productProfile.setName(productName);

			productProfile.setDivision(defaultDivision);
			productProfile.setDataSourceType(DataSourceType.TALLY);
		}
		productProfile.setProductId(productId);

		productProfile.setActivated(true);

		productProfile.setTaxRate(5);

		productProfile.setPrice(BigDecimal.valueOf(0));

		productProfile.setMrp(0);

		productProfile.setUnitQty(1.0);
		productProfile.setDescription(productId);

		productProfile.setProductCategory(defaultCategory.get());

		productProfileRepository.save(productProfile);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private List<OdooProductProfile> getOdooProducts() throws IOException, JSONException, ParseException {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info("Get URL: " + PRODUCT_API_URL);

		ResponseBodyOdooProductProfile responseBodyProductProfile = restTemplate.postForObject(PRODUCT_API_URL, entity,
				ResponseBodyOdooProductProfile.class);

		log.info(entity.getBody().toString() + "");

		log.info("Product Profile Size= " + responseBodyProductProfile.getResult().getResponse().size()
				+ "------------");

		List<OdooProductProfile> list = responseBodyProductProfile.getResult().getResponse();
		list.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));
		return list;
	}

	private String getOdooRequestBody() throws JSONException, ParseException {
		JSONParser parser = new JSONParser();
		String s = "{}";
		Object params = parser.parse(s);

		JSONObject emptyJsonObject = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("jsonrpc", "2.0");
		jsonObject.put("params", emptyJsonObject);

		return jsonObject.toString();
	}

}
