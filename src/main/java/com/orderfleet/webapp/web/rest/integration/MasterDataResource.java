package com.orderfleet.webapp.web.rest.integration;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.TallyIntegrationStatus;
import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.TallyIntegrationStatusService;
import com.orderfleet.webapp.service.impl.integration.async.ThirdPartySaveService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.dto.TallyResponseDTO;

/**
 * REST controller for managing master data for third party application.
 * 
 * @author Sarath
 * @since October 01, 2016
 */
@RestController
@RequestMapping(value = "/api/tp")
public class MasterDataResource {

	private final Logger log = LoggerFactory.getLogger(MasterDataResource.class);

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ThirdPartySaveService thirdPartySaveService;

	@Inject
	private TallyIntegrationStatusService tallyIntegrationStatusService;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * POST /account-types.json : Create new accountTypes.
	 *
	 * @param accountTypeDTOs
	 *            the List<accountType> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/account-types.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createAccountTypesJSON(
			@Valid @RequestBody List<AccountTypeDTO> accountTypeDTOs) throws URISyntaxException {
		log.debug("REST request to save AccountType : {}", accountTypeDTOs.size());

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.ACCOUNT_TYPE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.ACCOUNT_TYPE,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.ACCOUNT_TYPE);
			tallyIntegrationStatusService.save(tis);
		}
		thirdPartySaveService.saveUpdateAccountTypes(accountTypeDTOs, companyId);

		return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null), HttpStatus.CREATED);
	}

	/**
	 * POST /account-profile.json : Create a new accountProfile.
	 *
	 * @param accountProfile
	 *            the accountProfile to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new accountProfile, or with status 400 (Bad Request) if the name
	 *         is already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/account-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createAccountProfileJSON(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) throws URISyntaxException {
		log.debug("REST request to save AccountProfile : {}", accountProfileDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		final String userLogin = SecurityUtils.getCurrentUserLogin();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.ACCOUNT_PROFILE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.ACCOUNT_PROFILE,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.ACCOUNT_PROFILE);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusLocation = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.LOCATION);

		if (statusLocation.isPresent() && statusLocation.get().isIntegrated()) {
			thirdPartySaveService.saveUpdateAccountProfiles(accountProfileDTOs, companyId,userLogin);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new TallyResponseDTO("PROCESSING", "location saving in Progress.....", null),
					HttpStatus.CREATED);
		}
	}

	/**
	 * save all location Assighned Accounts
	 * 
	 * @param accountProfileDTOs
	 */
	@RequestMapping(value = "/location-account-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> saveLocationAccountProfiles(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) throws URISyntaxException {
		log.debug("REST request to save AccountProfile : {}", accountProfileDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId,
						TallyIntegrationStatusType.LOCATION_ACCOUNT_PROFILE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId,
					TallyIntegrationStatusType.LOCATION_ACCOUNT_PROFILE, false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.LOCATION_ACCOUNT_PROFILE);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusLocation = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.LOCATION);
		Optional<TallyIntegrationStatus> statusAccountProfile = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.ACCOUNT_PROFILE);

		if (statusLocation.isPresent() && statusLocation.get().isIntegrated() && statusAccountProfile.isPresent()
				&& statusAccountProfile.get().isIntegrated()) {
			thirdPartySaveService.saveAndUpdateLocationAccountProfile(accountProfileDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					new TallyResponseDTO("PROCESSING", "location or account profile saving in Progress.....", null),
					HttpStatus.CREATED);
		}
	}

	/**
	 * POST /product-categories.json : Create new productCategories.
	 *
	 * @param productCategoryDTOs
	 *            the List<productCategory> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-categories.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createProductCategoriesJSON(
			@Valid @RequestBody List<ProductCategoryDTO> productCategoryDTOs) throws URISyntaxException {
		log.debug("REST request to save ProductCategories : {}", productCategoryDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId,
					TallyIntegrationStatusType.PRODUCT_CATEGORIES, false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.PRODUCT_CATEGORIES);
			tallyIntegrationStatusService.save(tis);
		}
		thirdPartySaveService.saveUpdateProductCategories(productCategoryDTOs, companyId);

		return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null), HttpStatus.CREATED);
	}

	/**
	 * POST /product-profile.json : Create new productProfiles.
	 *
	 * @param productProfileDTOs
	 *            the List<productProfile> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createProductProfilesJSON(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) throws URISyntaxException {
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.PRODUCT_PROFILE);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusProductCategory = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES);
		Optional<TallyIntegrationStatus> statusProductGroup = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_GROUP);

		if (statusProductCategory.isPresent() && statusProductCategory.get().isIntegrated()
				&& statusProductGroup.isPresent() && statusProductGroup.get().isIntegrated()) {
			thirdPartySaveService.saveAndUpdateProductProfileList(productProfileDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new TallyResponseDTO("PROCESSING",
					"Product Category and Product Group  saving in Progress.....", null), HttpStatus.CREATED);
		}
	}

	/**
	 * POST /product-profile.json : Create new productProfiles.
	 *
	 * @param productProfileDTOs
	 *            the List<productProfile> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-group_product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createProductGroupUnderProduct(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) throws URISyntaxException {
		log.debug("REST request to save product-group_Product-Profiles : {}", productProfileDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId,
						TallyIntegrationStatusType.PRODUCT_GROUP_PRODUCT_PROFILE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId,
					TallyIntegrationStatusType.PRODUCT_GROUP_PRODUCT_PROFILE, false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.PRODUCT_GROUP_PRODUCT_PROFILE);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusProducProfile = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE);

		Optional<TallyIntegrationStatus> statusProductCategory = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES);
		Optional<TallyIntegrationStatus> statusProductGroup = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_GROUP);

		if (statusProducProfile.isPresent() && statusProducProfile.get().isIntegrated()
				&& statusProductCategory.isPresent() && statusProductCategory.get().isIntegrated()
				&& statusProductGroup.isPresent() && statusProductGroup.get().isIntegrated()) {
			thirdPartySaveService.saveAndUpdateProductGroupProductProfileList(productProfileDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					new TallyResponseDTO("PROCESSING", "Product Profile  saving in Progress.....", null),
					HttpStatus.CREATED);
		}
	}

	/**
	 * POST /opening-stock.json : Create new openingStocks.
	 *
	 * @param openingStockDTOs
	 *            the List<openingStock> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createOpeningStocksJSON(
			@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) throws URISyntaxException {

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.OPENING_STOCK);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.OPENING_STOCK,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.OPENING_STOCK);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusProducProfile = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE);

		Optional<TallyIntegrationStatus> statusProductCategory = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES);
		Optional<TallyIntegrationStatus> statusProductGroup = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_GROUP);

		if (statusProducProfile.isPresent() && statusProducProfile.get().isIntegrated()
				&& statusProductCategory.isPresent() && statusProductCategory.get().isIntegrated()
				&& statusProductGroup.isPresent() && statusProductGroup.get().isIntegrated()) {
			thirdPartySaveService.saveOpeningStocks(openingStockDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					new TallyResponseDTO("PROCESSING", "Product Profile  saving in Progress.....", null),
					HttpStatus.CREATED);
		}
	}

	/**
	 * POST /product-group.json : Create new productGroups.
	 *
	 * @param productgroupDTOs
	 *            the List<productGroup> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-group.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createProductGroupsJSON(
			@Valid @RequestBody List<ProductGroupDTO> productGroupDTOs) throws URISyntaxException {
		log.debug("REST request to save ProductGroups : {}", productGroupDTOs.size());

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_GROUP);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRODUCT_GROUP,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.PRODUCT_GROUP);
			tallyIntegrationStatusService.save(tis);
		}

		thirdPartySaveService.saveUpdateProductGroups(productGroupDTOs, companyId);

		return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null), HttpStatus.CREATED);
	}

	/**
	 * POST /price-level-list.json : Create new priceLists.
	 *
	 * @param priceLevelListDTOs
	 *            the List<productGroup> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-level-list.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createPriceListsJSON(
			@Valid @RequestBody List<PriceLevelListDTO> priceLevelListDTOs) throws URISyntaxException {
		log.debug("REST request to save priceLevelListDTOs : {}", priceLevelListDTOs.size());

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRICE_LEVEL_LIST);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.PRICE_LEVEL_LIST,
					false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.PRICE_LEVEL_LIST);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusProducProfile = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_PROFILE);

		Optional<TallyIntegrationStatus> statusProductCategory = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_CATEGORIES);
		Optional<TallyIntegrationStatus> statusProductGroup = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.PRODUCT_GROUP);

		if (statusProducProfile.isPresent() && statusProducProfile.get().isIntegrated()
				&& statusProductCategory.isPresent() && statusProductCategory.get().isIntegrated()
				&& statusProductGroup.isPresent() && statusProductGroup.get().isIntegrated()) {
			thirdPartySaveService.saveAndUpdatePriceList(priceLevelListDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					new TallyResponseDTO("PROCESSING", "Product Profile  saving in Progress.....", null),
					HttpStatus.CREATED);
		}

	}

	/**
	 * POST /receivable-payable.json : Create new productGroups.
	 *
	 * @param ReceivablePayableDTOs
	 *            the List<ReceivablePayable> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/receivable-payable.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createReceivablePayablesJSON(
			@Valid @RequestBody List<ReceivablePayableDTO> receivablePayableDTOs) throws URISyntaxException {
		log.debug("REST request to save Receivable Payable : {}", receivablePayableDTOs.size());

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.RECEIVABLE_PAYABLE);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId,
					TallyIntegrationStatusType.RECEIVABLE_PAYABLE, false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.RECEIVABLE_PAYABLE);
			tallyIntegrationStatusService.save(tis);
		}

		Optional<TallyIntegrationStatus> statusLocation = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.LOCATION);
		Optional<TallyIntegrationStatus> statusAccountProfile = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.ACCOUNT_PROFILE);

		if (statusLocation.isPresent() && statusLocation.get().isIntegrated() && statusAccountProfile.isPresent()
				&& statusAccountProfile.get().isIntegrated()) {
			thirdPartySaveService.saveReceivablePayables(receivablePayableDTOs, companyId);
			return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					new TallyResponseDTO("PROCESSING", "location or account profile saving in Progress.....", null),
					HttpStatus.CREATED);
		}
	}

	/**
	 * POST /location.json : Create new productCategories.
	 *
	 * @param productCategoryDTOs
	 *            the List<Location> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/location.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createLocationsJSON(@Valid @RequestBody List<LocationDTO> locationDTOs)
			throws URISyntaxException {
		log.debug("REST request to save locations : {}", locationDTOs.size());

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<TallyIntegrationStatus> tallyIntegrationStatus = tallyIntegrationStatusService
				.findOneByCompanyIdTallyIntegrationStatusType(companyId, TallyIntegrationStatusType.LOCATION);

		if (tallyIntegrationStatus.isPresent()) {
			tallyIntegrationStatusService.updateIntegratedStatus(companyId, TallyIntegrationStatusType.LOCATION, false);
		} else {
			TallyIntegrationStatus tis = new TallyIntegrationStatus();
			tis.setCompany(companyRepository.findOne(companyId));
			tis.setIntegrated(false);
			tis.setTallyIntegrationStatusType(TallyIntegrationStatusType.LOCATION);
			tallyIntegrationStatusService.save(tis);
		}
		thirdPartySaveService.saveUpdateLocations(locationDTOs, companyId);

		return new ResponseEntity<>(new TallyResponseDTO("OK", "successfully uploading...", null), HttpStatus.CREATED);
	}

	/**
	 * POST /total-stock-amount : Create new productProfile.
	 *
	 * @param productProfiles
	 *            the List<ProductProfileDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the ProductProfileDTO URI syntax is incorrect
	 */
	boolean updateble = true;

	@RequestMapping(value = "/total-stock-amount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Timed
	public ResponseEntity<TallyResponseDTO> updateTotalStockAmount(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) throws URISyntaxException {
		long start = System.currentTimeMillis();
		log.debug("REST request to save productProfiles : {}", productProfileDTOs.size());
		// updateble used to aTallyResponseDTO update the discription
		if (updateble) {
			productProfileRepository.updateSDiscription();
			updateble = false;
		} else {
			updateble = true;
		}
		for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
			Optional<ProductProfileDTO> productProfile = productProfileService.findByName(productProfileDTO.getName());
			if (productProfile.isPresent()) {
				double totalStockAmount = Double.valueOf(productProfileDTO.getDescription());
				String BoxAndSheet = "";
				if (productProfileDTO.getAlias() != null && !productProfileDTO.getAlias().equalsIgnoreCase("")) {
					BoxAndSheet = productProfileDTO.getAlias();
				} else {
					if (productProfile.get().getUnitQty() != 0) {
						Double BoxAndSheetValue = totalStockAmount / productProfile.get().getUnitQty();
						BoxAndSheet = String.valueOf(Math.round(BoxAndSheetValue));
					} else {
						BoxAndSheet = totalStockAmount + " " + productProfile.get().getSku();
					}
				}
				productProfile.get().setDescription(BoxAndSheet);
				productProfileService.update(productProfile.get());
			}
		}
		log.info("Elapsed time in new: TotalStockAmount : " + (System.currentTimeMillis() - start));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


}
