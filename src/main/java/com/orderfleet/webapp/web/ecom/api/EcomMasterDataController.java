package com.orderfleet.webapp.web.ecom.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.EcomProductProfileProductService;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.service.ProductGroupInfoSectionService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.ecom.dto.ProductGroupInfoSectionDTO;
import com.orderfleet.webapp.web.rest.api.MasterDataController;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for e-commerce data given to android device.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
@RestController
@RequestMapping("/api/ecom")
public class EcomMasterDataController {

	private final Logger log = LoggerFactory.getLogger(MasterDataController.class);

	private ProductGroupInfoSectionService productGroupInfoSectionService;

	private EcomProductProfileService ecomProductProfileService;

	private UserAccountProfileRepository userAccountProfileRepository;

	private AccountProfileMapper accountProfileMapper;

	private DocumentAccountTypeService documentAccountTypeService;

	private AccountProfileService accountProfileService;

	@Inject
	private DocumentFormsRepository documentFormsRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private PriceLevelListService priceLevelListService;

	@Inject
	private PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private EcomProductProfileProductService ecomProductProfileProductService;

	@Inject
	private ProductGroupEcomProductsService productGroupEcomProductsService;

	@Inject
	public EcomMasterDataController(ProductGroupInfoSectionService productGroupInfoSectionService,
			EcomProductProfileService ecomProductProfileService,
			UserAccountProfileRepository userAccountProfileRepository, AccountProfileMapper accountProfileMapper,
			DocumentAccountTypeService documentAccountTypeService, AccountProfileService accountProfileService) {
		super();
		this.productGroupInfoSectionService = productGroupInfoSectionService;
		this.ecomProductProfileService = ecomProductProfileService;
		this.userAccountProfileRepository = userAccountProfileRepository;
		this.accountProfileMapper = accountProfileMapper;
		this.documentAccountTypeService = documentAccountTypeService;
		this.accountProfileService = accountProfileService;
	}

	/**
	 * GET /product-group-info-sections : get all the productGroupInfoSections.
	 * This will give info-section with product-groups
	 * 
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupInfoSections in body
	 */
	@GetMapping("/product-group-info-sections")
	@Timed
	public List<ProductGroupInfoSectionDTO> getAllProductGroupInfoSections() {
		log.debug("REST request to get all ProductGroupInfoSections");
		return productGroupInfoSectionService.findAllByCompanyWithRichText();
	}
	
	
	/**
	 * GET /product-group-info-sections : get all the productGroupInfoSections.
	 * This will give info-section with product-groups
	 * 
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupInfoSections in body
	 */
	@GetMapping("/ecom-product-group-info-sections")
	@Timed
	public List<ProductGroupInfoSectionDTO> getAllEcomProductGroupInfoSections() {
		log.debug("REST request to get all ProductGroupInfoSections");
		return Collections.emptyList();
		//return productGroupInfoSectionService.findAllByCompanyWithRichText();
	}
	

	/**
	 * GET /ecom-product-profile : get all the ecomProductProfile. This will
	 * give ecom-product with list of product profile
	 * 
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupInfoSections in body
	 */
	@GetMapping("/ecom-product-profile")
	@Timed
	public ResponseEntity<List<EcomProductProfileDTO>> getEcomProducs() {
		log.debug("REST request to get user EcomProductProfiles");
		return new ResponseEntity<>(ecomProductProfileService.findByCurrentUser(), HttpStatus.OK);
	}

	/**
	 * GET /account-profile : get the accountProfile. This will give all account
	 * profile from user-account-profile association table (One user has only
	 * one account profile)
	 *
	 * @return the ResponseEntity with status 200 (OK) and the accountProfile in
	 *         body
	 */
	@GetMapping("/account-profile")
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfileByUser() {
		log.debug("REST request to get receiver account profile");
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String login = SecurityUtils.getCurrentUserLogin();
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
				.findByCompanyIdAndUserLogin(companyId, login);
		if (userAccountProfile.isPresent()) {
			return new ResponseEntity<>(accountProfileMapper
					.accountProfileToAccountProfileDTO(userAccountProfile.get().getAccountProfile()), HttpStatus.OK);
		}
		return new ResponseEntity<>(new AccountProfileDTO(), HttpStatus.OK);
	}

	/**
	 * GET /account-profile/:pid : get the accountProfile. This will give
	 * default account profile for this company (Supplier)
	 * 
	 * @param pid
	 *            documentPid
	 *
	 * @return the ResponseEntity with status 200 (OK) and the accountProfile in
	 *         body
	 */
	@GetMapping("/account-profile/{pid}")
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfileCompany(@PathVariable String pid) {
		log.debug("REST request to get supplier account profile");
		List<AccountTypeDTO> accountTypeDTOs = documentAccountTypeService
				.findAccountTypesByDocumentPidAndAccountTypeColumn(pid, AccountTypeColumn.Supplier);
		if (!accountTypeDTOs.isEmpty()) {
			List<AccountProfileDTO> accountProfileDTOs = accountProfileService
					.findAllByAccountType(accountTypeDTOs.get(0).getPid());
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@GetMapping("/forms-form-elements/{documentPid}")
	@Timed
	public ResponseEntity<List<FormFormElementDTO>> getFormsFormElementsByDocument(@PathVariable String documentPid) {
		log.debug("REST request to get FormFormElement by document pid {}", documentPid);
		List<DocumentForms> documentForms = documentFormsRepository.findByDocumentPid(documentPid);
		if (documentForms != null && !documentForms.isEmpty()) {
			// Assuming it has only one dynamic form configured
			List<FormFormElementDTO> formFormElements = formFormElementRepository
					.findByFormPid(documentForms.get(0).getForm().getPid()).stream().map(FormFormElementDTO::new)
					.collect(Collectors.toList());
			return new ResponseEntity<>(formFormElements, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@GetMapping("/get-product-profile/{productPid}")
	@Timed
	public ResponseEntity<ProductProfileDTO> getProductPrice(@PathVariable String productPid) {
		log.debug("REST request to get product price by product pid {}", productPid);

		Double resultPrice = 0.0;

		ProductProfileDTO result = new ProductProfileDTO();

		Optional<ProductProfileDTO> productProfileDTO = productProfileService.findOneByPid(productPid);
		if (!productProfileDTO.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("produuctProfile", "not found", "Product Profile not found"))
					.body(null);
		}
		result = productProfileDTO.get();

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String login = SecurityUtils.getCurrentUserLogin();
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
				.findByCompanyIdAndUserLogin(companyId, login);
		if (userAccountProfile.isPresent()) {
			List<String> listPids = ecomProductProfileProductService
					.findEcomProductProfilePidsByProductPorfileIn(Arrays.asList(productProfileDTO.get()));
			List<ProductGroupDTO> productGroupDTOs = productGroupEcomProductsService
					.findAllProductGroupByEcomProductPidIn(listPids);
			Optional<PriceLevel> priceLevels = priceLevelAccountProductGroupRepository
					.findOneByAccountProfilePidAndProductGroupPid(companyId,
							userAccountProfile.get().getAccountProfile().getPid(), productGroupDTOs.get(0).getPid());
		
			if(priceLevels.isPresent()) {
				Optional<PriceLevelListDTO> opPriceLevel = priceLevelListService
						.findByCompanyIdAndPriceLevelPidAndProductProfilePidOrderByProductProfileNameDesc(companyId,
								priceLevels.get().getPid(), productPid);
				if (opPriceLevel.isPresent()) {
					resultPrice = opPriceLevel.get().getPrice();
				} else {
					resultPrice = productProfileDTO.get().getPrice().doubleValue();
				}
			}
			
		}

		result.setPrice(BigDecimal.valueOf(resultPrice));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
