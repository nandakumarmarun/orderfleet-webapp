package com.orderfleet.webapp.web.tally.api;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.tally.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.tally.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.tally.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.tally.service.AssignTallyDataService;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;


@Controller
@RequestMapping(value = "/api/tally")
public class ProductTallyController {

	private final Logger log = LoggerFactory.getLogger(ProductTallyController.class);

	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private AssignTallyDataService assignTallyDataService;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private TallyDataUploadService tallyDataUploadService;
	
	@Inject
	private ProductGroupRepository productGroupRepository;
	
	@Inject
	private ProductCategoryRepository productCategoryRepository;
	 
	@PostMapping("/product-profiles-tally")
	@ResponseBody
	public ResponseEntity<String> uploadProductProfiles(@RequestBody List<ProductProfileDTO> productProfileDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert product profiles "+productProfileDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			tallyDataUploadService.saveOrUpdateProductProfiles(productProfileDtos, company);
			assignTallyDataService.productProductGroupAssociation(productProfileDtos,company);
			return new ResponseEntity<String>("Successfully inserted product profiles",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/product-groups-tally")
	@ResponseBody
	public ResponseEntity<String> uploadProductGroups(@RequestBody List<ProductGroupDTO> productGroupsDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert product groups "+productGroupsDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			List<ProductGroup> existingProductGroups = productGroupRepository.findAllByCompanyPid(company.getPid());
			
			tallyDataUploadService.saveOrUpdateProductGroup(productGroupsDtos, company);
			log.debug("Inserted all Product groups");
			
			assignTallyDataService.assignDocumentProductGroup(productGroupsDtos, company);
			log.debug("Assigned all Product groups with documents");
			
			assignTallyDataService.assignUserProductGroup(productGroupsDtos, existingProductGroups, company);
			log.debug("Assigned all Product groups with users");
			
			return new ResponseEntity<String>("Successfully inserted product groups",HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	
	@PostMapping("/product-categories-tally")
	@ResponseBody
	public ResponseEntity<String> uploadProductCategories(@RequestBody List<ProductCategoryDTO> productCategoriesDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert product groups "+productCategoriesDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			
			List<ProductCategory> existingProductCategories = productCategoryRepository.findAllByCompanyPid(company.getPid());
			tallyDataUploadService.saveOrUpdateProductCategory(productCategoriesDtos, company);
			log.debug("Inserted all Product categories");
			
			assignTallyDataService.assignDocumentProductCategory(productCategoriesDtos, company);
			log.debug("Assigned all Product categories with documents");
			
			assignTallyDataService.assignUserProductCategory(productCategoriesDtos, existingProductCategories, company);
			log.debug("Assigned all Product categories with users");
			
			return new ResponseEntity<String>("Successfully inserted product categories",HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	
	@PostMapping("/price-level-list-tally")
	@ResponseBody
	public ResponseEntity<String> uploadPriceLevelList(@RequestBody List<PriceLevelListDTO> priceLevelListDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert price level list "+priceLevelListDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			List<PriceLevel> existingPriceLevels = priceLevelRepository.findByCompanyId(company.getId());
			tallyDataUploadService.saveOrPriceLevel(priceLevelListDtos, company);
			tallyDataUploadService.saveOrPriceLevelList(priceLevelListDtos, company);
			assignTallyDataService.documentPriceLevelAssociation(company);
			assignTallyDataService.userPriceLevelAssociation(existingPriceLevels, company);
			return new ResponseEntity<String>("Successfully inserted pricelevels and price level list",HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}
