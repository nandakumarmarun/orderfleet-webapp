package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.service.spi.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.service.BrandDevaService;
import com.orderfleet.webapp.service.BussinessUnitService;
import com.orderfleet.webapp.service.CityService;
import com.orderfleet.webapp.service.ContryFocusService;
import com.orderfleet.webapp.service.DistrictFocusService;
import com.orderfleet.webapp.service.StateFocusService;
import com.orderfleet.webapp.service.FiscalYearService;
import com.orderfleet.webapp.service.LengthTypeService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.RouteCodeService;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.RouteCodeRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;
import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;
import com.orderfleet.webapp.web.rest.dto.FiscalYearDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.OutStandingFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.OutStandingResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewResponceFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.service.AccountProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.OutStandingFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.ProductProfileFocusUploadService;

import net.minidev.json.parser.ParseException;

@Service
public class UploadFocusResourceAutoShedule {

	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";

	private static String ACCOUNT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetCustomerData?Auth_Token=";

	private static String PRODUCT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GETProductsWithPrice?Auth_Token=";

	private static String RECEIVABLE_PAYABLE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/OutStandingReportALL?Auth_Token=";

	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private UserRepository userRepository;
	@Inject
	private LocationService locationService;
	@Inject
	private CompanyRepository companyRepository;
	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;
	@Inject
	private RouteCodeRepository routeCodeRepository;
	@Inject
	private SyncOperationRepository syncOperationRepository;
	@Inject
	private DivisionRepository divisionRepository;
	@Inject
	private ProductCategoryRepository productCategoryRepository;
	@Inject
	private ProductGroupRepository productGroupRepository;
	@Inject
	private ProductProfileRepository productProfileRepository;
	@Inject
	private ProductGroupProductRepository productGroupProductRepository;
	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	private final Logger log = LoggerFactory.getLogger(UploadFocusResourceAutoShedule.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	
	final Long companyId = (long) 304975;

	@Transactional
	public void uploadToFocusAutomatically() throws URISyntaxException, IOException, JSONException, ParseException {
		log.debug("Web request to get a page of focus Upload Masters");
		uploadAccountProfiles();
		uploadProductProfiles();
		uploadReceivablePayable();

	}

	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Account Profile URL: " + ACCOUNT_PROFILE_API_URL);

			AccountProfileResponseFocus accountProfileResponseFocus = restTemplate
					.getForObject(ACCOUNT_PROFILE_API_URL + authToken, AccountProfileResponseFocus.class);

			if (accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles() != null) {

				log.info("Saving " + accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles().size()
						+ " Account Profiles");

				if (accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles().size() > 0) {
					saveUpdateAccountProfiles(
							accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles());
				}
			}
			System.out.println("response ok");
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Product Profiles...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Product Profile URL: " + PRODUCT_PROFILE_API_URL);

			ProductProfileNewResponceFocus productProfileNewResponceFocus = restTemplate
					.getForObject(PRODUCT_PROFILE_API_URL + authToken, ProductProfileNewResponceFocus.class);

			if (productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct() != null) {

				log.info("Saving " + productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct().size()
						+ " product Profiles");
				if (productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct().size() > 0) {
					saveUpdateProductProfiles(
							productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct());
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity<Void> uploadReceivablePayable() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload ReceivablePayable...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Account Profile URL: " + RECEIVABLE_PAYABLE_API_URL);

			OutStandingResponseFocus OutStandingResponseFocus = restTemplate
					.getForObject(RECEIVABLE_PAYABLE_API_URL + authToken, OutStandingResponseFocus.class);

			if (OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus() != null) {

				log.info("Saving "
						+ OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus().size()
						+ " Receivable payable ");

				if (OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus().size() > 0) {

					saveUpdateReceivablePayable(
							OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus());
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getAuthenticationToken() {

		AuthenticationRequstFocus authenticationRequstFocus = authenticationBody();

		HttpEntity<AuthenticationRequstFocus> entity = new HttpEntity<>(authenticationRequstFocus,
				RestClientUtil.createTokenAuthHeaders());

		ObjectMapper Obj = new ObjectMapper();

		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(entity.getBody());
			log.info("Authentication Request Body: -" + jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Authentication URL: " + AUTHENTICATE_API_URL);

		try {
			AuthenticationResponseFocus authenticationResponseFocus = restTemplate.postForObject(AUTHENTICATE_API_URL,
					entity, AuthenticationResponseFocus.class);

			Obj = new ObjectMapper();

			String jsonStr1;
			try {
				jsonStr1 = Obj.writeValueAsString(authenticationResponseFocus);
				log.info("Authentication Response Body: -" + jsonStr1);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (authenticationResponseFocus.getGetloginResult().getsMessage().equals("Token Generated")) {

				return authenticationResponseFocus.getGetloginResult().getAuthToken();
			}

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return null;
	}

	private String productRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Item_Master");
		return jsonObject.toString();

	}

	private AuthenticationRequstFocus authenticationBody() {
		AuthenticationRequstFocus authenticationRequstFocus = new AuthenticationRequstFocus();
		authenticationRequstFocus.setUserName("su");
		authenticationRequstFocus.setPassword("deva$focus");
		authenticationRequstFocus.setCompanyCode("070");

		return authenticationRequstFocus;
	}

	

	public void saveUpdateAccountProfiles(List<AccountProfileFocus> accountProfileDTos) {

		log.info("Saving Account Profiles.........");
		LocalDateTime starttime = LocalDateTime.now();
		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin("admin@deva").get();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		log.info("Company ID" + companyId);
		Company company = companyRepository.findOne(companyId);
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		// All product must have a division/category, if not, set a default one
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_109" + "_" + "admin@deva" + "_" + LocalDateTime.now();
		String description = "get first by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType customerAccount = accountTypeRepository.findByCompanyIdAndName("Customer");
		AccountType defaultAccountType = accountTypeRepository.findByAccountTypePidAndCompanyId(customerAccount.getPid(),companyId);
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(companyId, true);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		// find all exist account profiles

//		List<String> apNames = list.stream().map(apDto -> apDto.getName()).collect(Collectors.toList());
//		List<AccountProfile> accountProfiles = accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
//				apNames);
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_104" + "_" + "admin@deva" + "_" + LocalDateTime.now();
		String description1 = "get all by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId(companyId);
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
		logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);

		List<LocationDTO> locationDtos = new ArrayList<>();
		List<LocationAccountProfileDTO> locationAccountProfileDtos = new ArrayList<>();
		List<RouteCode> routecodeList = new ArrayList<>();
		List<RouteCode> RouteCodeList = routeCodeRepository.findAll();

		if (accountProfileDTos != null) {
			for (AccountProfileFocus apDto : accountProfileDTos) {
				// check exist by name, only one exist with a name

				Optional<AccountProfile> optionalAP = accountProfiles.stream()
						.filter(pc -> pc.getName().equalsIgnoreCase(apDto.getName())).findAny();
				AccountProfile accountProfile;
				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();
					// if not update, skip this iteration. Not implemented now
					// if (!accountProfile.getThirdpartyUpdate()) { continue; }
				} else {
					accountProfile = new AccountProfile();
					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setName(apDto.getName());
					accountProfile.setAddress(apDto.getAddress());
					accountProfile.setCustomerId(apDto.getCode());
					accountProfile.setCreditDays(Long.valueOf(apDto.getCreditDays()));
					accountProfile.setClosingBalance(Double.parseDouble(apDto.getClosing_Balance()));
					accountProfile.setTinNo(apDto.getGstin());
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.TALLY);
					accountProfile.setImportStatus(true);

				}
				if (apDto.getCreditLimit() != null && !apDto.getCreditLimit().equals("")) {
					Float floatnum = Float.parseFloat(apDto.getCreditLimit());
					accountProfile.setCreditLimit((Long) floatnum.longValue());
				}

				accountProfile.setActivated(true);
				if (apDto.getAddress() != null && !apDto.getAddress().equals("")) {
					accountProfile.setAddress(apDto.getAddress());
				} else {
					accountProfile.setAddress("No Address");
				}
				if (apDto.getCity() != null && !apDto.getCity().equals("")) {
					accountProfile.setCity(apDto.getCity());
				} else {
					accountProfile.setCity("No City");
				}
				if (apDto.getPhone() != null && !apDto.getPhone().equals("")) {
					if (isValidPhone(apDto.getPhone())) {
						accountProfile.setPhone1(apDto.getPhone());
					}
				} else {
					accountProfile.setPhone1("");
				}

				// account type

//				if (accountProfile.getAccountType() == null) {
//					accountProfile.setAccountType(defaultAccountType);
//				}

				Optional<AccountType> opAccountType = accountTypes.stream()
						.filter(acc -> acc.getName().equalsIgnoreCase(apDto.getAccountType())).findFirst();
				if (opAccountType.isPresent()) {
					accountProfile.setAccountType(opAccountType.get());
				} else {
					accountProfile.setAccountType(defaultAccountType);
				}

				Optional<AccountProfile> opAccP = saveUpdateAccountProfiles.stream()
						.filter(so -> so.getName().equalsIgnoreCase(apDto.getName())).findAny();

				if (opAccP.isPresent()) {
					continue;
				}

				LocationDTO locationDTO = new LocationDTO();

				LocationAccountProfileDTO locationAccountProfileDto = new LocationAccountProfileDTO();

				if (apDto.getRouteCode().equalsIgnoreCase("null") && apDto.getRouteName().equalsIgnoreCase("null")) {
					locationDTO.setLocationId("null");
					locationDTO.setName("No Location");

					locationDtos.add(locationDTO);
					locationAccountProfileDto.setAccountProfileName(apDto.getName());
					locationAccountProfileDto.setLocationName("No Location");

				} else {

					locationDTO.setLocationId(apDto.getRouteCode());
					locationDTO.setName(apDto.getRouteName());
					locationDtos.add(locationDTO);
					locationAccountProfileDto.setAccountProfileName(apDto.getName());
					locationAccountProfileDto.setLocationName(apDto.getRouteName());
				}

				RouteCode routecode = new RouteCode();
				if (!apDto.getRouteCode().equalsIgnoreCase("null") && !apDto.getRouteName().equalsIgnoreCase("null")) {

					Optional<RouteCode> routeCodeop = RouteCodeList.stream()
							.filter(pc -> pc.getMasterCode().equals(apDto.getRouteCode())).findAny();
					if (routeCodeop.isPresent()) {
						routecode = routeCodeop.get();
						routecode.setId(routeCodeop.get().getId());
						routecode.setMasterCode(apDto.getRouteCode());
						routecode.setMasterName(apDto.getRouteName());
						routecode.setAccountProfile(accountProfile);
						routecode.setCompany(company);

					} else {
						routecode.setPid(RouteCodeService.PID_PREFIX + RandomUtil.generatePid());
						routecode.setMasterCode(apDto.getRouteCode());
						routecode.setMasterName(apDto.getRouteName());
						routecode.setAccountProfile(accountProfile);
						routecode.setCompany(company);

					}
				}
				locationAccountProfileDtos.add(locationAccountProfileDto);
				accountProfile.setDataSourceType(DataSourceType.TALLY);
				saveUpdateAccountProfiles.add(accountProfile);
				routecodeList.add(routecode);
			}

			if (locationDtos.size() > 0)

			{
				saveUpdateLocations(locationDtos);
				saveUpdateLocationHierarchy(locationDtos);
			}

			bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
			routeCodeRepository.save(routecodeList);
			saveUpdateLocationAccountProfiles(locationAccountProfileDtos);
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;

		// update sync table

		Optional<SyncOperation> oPsyncOperation = syncOperationRepository.findOneByCompanyIdAndOperationType(companyId,
				SyncOperationType.ACCOUNT_PROFILE);
		SyncOperation syncOperation;
		if (oPsyncOperation.isPresent()) {
			syncOperation = oPsyncOperation.get();
			syncOperation.setOperationType(SyncOperationType.ACCOUNT_PROFILE);
			syncOperation.setCompleted(true);
			syncOperation.setLastSyncStartedDate(starttime);
			syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
			syncOperation.setLastSyncTime(elapsedTime);
			syncOperation.setCompany(company);
			System.out.println("syncCompleted Date : " + syncOperation.getLastSyncCompletedDate());
			syncOperationRepository.save(syncOperation);
		}

		log.info("Sync completed in {} ms", elapsedTime);
	}

	private boolean isValidPhone(String phone) {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional
	public void saveUpdateLocations(final List<LocationDTO> locationDTOs) {

		log.info("Saving Locations.........");
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<Location> saveUpdateLocations = new HashSet<>();
		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(companyId);
		for (LocationDTO locDto : locationDTOs) {
			// check exist by name, only one exist with a name
			Optional<Location> optionalLoc = locations.stream().filter(p -> p.getName().equals(locDto.getName()))
					.findAny();
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setName(locDto.getName());
				location.setCompany(company);
			}
			location.setAlias(locDto.getAlias());
			location.setActivated(true);

			Optional<Location> opLoc = saveUpdateLocations.stream()
					.filter(so -> so.getName().equalsIgnoreCase(locDto.getName())).findAny();
			if (opLoc.isPresent()) {
				continue;
			}

			saveUpdateLocations.add(location);
		}
		locationRepository.save(saveUpdateLocations);
		// bulkOperationRepositoryCustom.bulkSaveLocations(saveUpdateLocations);
		locationRepository.flush();
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateLocationAccountProfiles(final List<LocationAccountProfileDTO> locationAccountProfileDTOs) {

		log.info("Saving Location Account Profiles.........");
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService
				.findAllLocationAccountProfiles(companyId);
		// delete all assigned location account profile from tally
		// locationAccountProfileRepository.deleteByCompanyIdAndDataSourceTypeAndThirdpartyUpdateTrue(company.getId(),DataSourceType.TALLY);
		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
		List<Long> locationAccountProfilesIds = new ArrayList<>();

		for (LocationAccountProfileDTO locAccDto : locationAccountProfileDTOs) {
			LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
			// find location

			Optional<Location> loc = locations.stream().filter(pl -> locAccDto.getLocationName().equals(pl.getName()))
					.findFirst();
			// find accountprofile
			// System.out.println(loc.get()+"===Location");

			Optional<AccountProfile> acc = accountProfiles.stream()
					.filter(ap -> locAccDto.getAccountProfileName().equals(ap.getName())).findFirst();
			if (acc.isPresent()) {
				List<Long> locationAccountProfileIds = locationAccountProfiles.stream()
						.filter(lap -> acc.get().getPid().equals(lap.getAccountProfile().getPid()))
						.map(lap -> lap.getId()).collect(Collectors.toList());
				if (locationAccountProfileIds.size() != 0) {
					locationAccountProfilesIds.addAll(locationAccountProfileIds);
				}
				if (loc.isPresent()) {
					locationAccountProfile.setLocation(loc.get());
				} else if (acc.isPresent()) {
					locationAccountProfile.setLocation(locations.get(0));
				}
				locationAccountProfile.setAccountProfile(acc.get());
				locationAccountProfile.setCompany(company);
				newLocationAccountProfiles.add(locationAccountProfile);
			}
		}
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		locationAccountProfileRepository.save(newLocationAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	private void saveUpdateLocationHierarchy(List<LocationDTO> locationDtos) {

		List<LocationHierarchyDTO> locationHierarchyDTOs = locationDtosToLocationHierarchyDtos(locationDtos);

		log.info("Saving Location Hierarchies.........");

		long start = System.nanoTime();

//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();

		Long version;

		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);

		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivated(ZonedDateTime.now(),
					locationHierarchy.get().getVersion(), companyId);
			version = locationHierarchy.get().getVersion() + 1;

		} else {
			version = 1L;
		}

		// find all locations
		List<Location> locations = locationRepository.findByCompanyIdAndActivatedTrue(companyId);

		// create hierarchy
		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {

			// check location exist
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getName().equals(locationDTO.getLocationName())).findAny();

			if (optionalLoc.isPresent()) {
				if (locationDTO.getParentName() != null && locationDTO.getParentName().length() > 0) {
					// check parent location exist
					Optional<Location> optionalParentLoc = locations.stream()
							.filter(p -> p.getName().equals(locationDTO.getParentName())).findAny();

					if (optionalParentLoc.isPresent()) {
						locationHierarchyRepository.insertLocationHierarchyWithParent(version,
								optionalLoc.get().getId(), optionalParentLoc.get().getId(), companyId);
					}
				} else {
					locationHierarchyRepository.insertLocationHierarchyWithNoParentAndCompanyId(version,
							optionalLoc.get().getId(), companyId);
				}
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private List<LocationHierarchyDTO> locationDtosToLocationHierarchyDtos(List<LocationDTO> locationDtos) {

		Set<String> locations = new HashSet<>();

		for (LocationDTO locationDTO : locationDtos) {

			locations.add(locationDTO.getName());
		}

		List<LocationHierarchyDTO> locationHierarchyDTOs = new ArrayList<>();

		LocationHierarchyDTO defaultLocationHierarchyDTO = new LocationHierarchyDTO();

		defaultLocationHierarchyDTO.setLocationName("Territory");
		defaultLocationHierarchyDTO.setParentName(null);
		locationHierarchyDTOs.add(defaultLocationHierarchyDTO);

		for (String location : locations) {

			LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();

			locationHierarchyDTO.setLocationName(location);
			locationHierarchyDTO.setParentName("Territory");
			locationHierarchyDTOs.add(locationHierarchyDTO);

		}

		return locationHierarchyDTOs;
	}

	public void saveUpdateProductProfiles(List<ProductProfileNewFocus> list) {

		log.info("Saving Product Profiles.........");

		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		LocalDateTime starttime = LocalDateTime.now();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		Set<String> ppNames = list.stream().map(p -> p.getItemName()).collect(Collectors.toSet());

		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		List<ProductCategory> productCategorys = productCategoryRepository.findByCompanyId(companyId);

		List<ProductGroupDTO> productGroupDtos = new ArrayList<>();

		List<TPProductGroupProductDTO> productGroupProductDTOs = new ArrayList<>();

		// All product must have a division/category, if not, set a default one
		Division defaultDivision = divisionRepository.findFirstByCompanyId(companyId);

		String cat = productCategorys.get(0).getName();
		Optional<ProductCategory> defaultCategory = productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(companyId, cat);
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
		for (ProductProfileNewFocus ppDto : list) {
			// check exist by name, only one exist with a name
			Optional<ProductProfile> optionalPP = productProfiles.stream()
					.filter(p -> p.getName().equals(ppDto.getItemName())).findAny();
			ProductProfile productProfile;
			if (optionalPP.isPresent()) {
				productProfile = optionalPP.get();
				// if not update, skip this iteration.
				if (!productProfile.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productProfile = new ProductProfile();
				productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				productProfile.setCompany(company);
				productProfile.setName(ppDto.getItemName());
				productProfile.setDivision(defaultDivision);
				productProfile.setDataSourceType(DataSourceType.TALLY);
				productProfile.setWidth(Double.parseDouble(ppDto.itemwidth));
				productProfile.setRateConversion(Double.parseDouble(ppDto.getRateConversion()));
			}

			productProfile.setProductId(ppDto.getItemCode());
			productProfile.setPrice(BigDecimal.valueOf(ppDto.getSellingRate()));
			productProfile.setTaxRate(ppDto.getGstPer());
			productProfile.setMrp(0);
			productProfile.setActivated(true);
			productProfile.setWidth(Double.parseDouble(ppDto.itemwidth));
			productProfile.setRateConversion(Double.parseDouble(ppDto.getRateConversion()));

			if (ppDto.getHsnCode() != null && !ppDto.getHsnCode().equals("")) {
				productProfile.setHsnCode(ppDto.getHsnCode());
			}

			productProfile.setSku(ppDto.getBaseUnits());

//					if (ppDto.getSellingRate() != null && !ppDto.getSellingRate().equals("")) {
//						productProfile.setPrice(BigDecimal.valueOf(Double.valueOf(ppDto.getStandard_price())));
//					} else {
//						productProfile.setPrice(BigDecimal.valueOf(0));
//					}

			Optional<ProductProfile> opAccP = saveUpdateProductProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(ppDto.getItemName())).findAny();
			if (opAccP.isPresent()) {
				continue;
			}
			productProfile.setUnitQty(1.0);
			productProfile.setProductCategory(defaultCategory.get());
			TPProductGroupProductDTO productGroupProductDTO = new TPProductGroupProductDTO();

//					ProductGroup defaultPg = productGroupRepository.findFirstByCompanyId(company.getId());

//					Optional<ProductGroup> defaultProductGroup =productGroupRepository. findByCompanyIdAndNameIgnoreCase(company.getId(),"General");
			ProductGroup defaultpg = productGroupRepository.findFirstByCompanyIdOrderByIdAsc(companyId);
//					System.out.println("************"+defaultpg);
			productGroupProductDTO
					.setGroupName(ppDto.getItemType() != null ? ppDto.getItemType() : defaultpg.getName());

			productGroupProductDTO.setProductName(ppDto.getItemName());

			productGroupProductDTOs.add(productGroupProductDTO);

			ProductGroupDTO productGroupDTO = new ProductGroupDTO();
			productGroupDTO.setName(ppDto.getItemType());
			productGroupDTO.setAlias(ppDto.getItemType());
			productGroupDtos.add(productGroupDTO);

			saveUpdateProductProfiles.add(productProfile);
		}
		log.info("Saving product profiles");
		bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
		log.info("Saving product groups");
		saveUpdateProductGroups(productGroupDtos);
		log.info("Saving product group product profiles");
		saveUpdateProductGroupProduct(productGroupProductDTOs);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		Optional<SyncOperation> oPsyncOperation = syncOperationRepository.findOneByCompanyIdAndOperationType(companyId,
				SyncOperationType.PRODUCTPROFILE);
		SyncOperation syncOperation;
		if (oPsyncOperation.isPresent()) {
			syncOperation = oPsyncOperation.get();
			syncOperation.setOperationType(SyncOperationType.PRODUCTPROFILE);
			syncOperation.setCompleted(true);
			syncOperation.setLastSyncStartedDate(starttime);
			syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
			syncOperation.setLastSyncTime(elapsedTime);
			syncOperation.setCompany(company);
			System.out.println("syncCompleted Date : " + syncOperation.getLastSyncCompletedDate());
			syncOperationRepository.save(syncOperation);
		}
		log.info("Sync completed in {} ms", elapsedTime);
	}

	private void saveUpdateProductGroupProduct(List<TPProductGroupProductDTO> productGroupProductDTOs) {
		log.debug("Saving Product Group Products : ");
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		log.debug("Login details : " + "[" + companyId + "," + company.getLegalName() + "]");
		log.debug("initializing session Registries");
		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();
		log.debug("Clearing Existing Association");
		productGroupProductRepository.deleteByCompany(companyId);
		log.debug("Fetching product profiles");
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(companyId);
		log.debug("Fetching product Groups");
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		log.debug("Processing With New data");
		for (TPProductGroupProductDTO pgpDto : productGroupProductDTOs) {
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();
			Optional<ProductGroup> opPg = productGroups.stream()
					.filter(pl -> pgpDto.getGroupName().equals(pl.getName())).findFirst();
			Optional<ProductProfile> opPp = productProfiles.stream()
					.filter(pp -> pgpDto.getProductName().equals(pp.getName())).findFirst();
			if (opPp.isPresent() && opPg.isPresent()) {
				productGroupProduct.setProductGroup(opPg.get());
				productGroupProduct.setProduct(opPp.get());
				productGroupProduct.setCompany(company);
				newProductGroupProducts.add(productGroupProduct);
			}
		}
		log.debug("Saving new Product Group product Association");
		List<ProductGroupProduct> result = productGroupProductRepository.save(newProductGroupProducts);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}

	private void saveUpdateProductGroups(List<ProductGroupDTO> productGroupDtos) {
		// TODO Auto-generated method stub
		log.info("Saving Product Groups.........");
		long start = System.nanoTime();
		Set<ProductGroup> saveUpdateProductGroups = new HashSet<>();

//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		// find all product group
		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(companyId);
		for (ProductGroupDTO pgDto : productGroupDtos) {
			// check exist by name, only one exist with a name
			Optional<ProductGroup> optionalPG = productGroups.stream().filter(p -> p.getName().equals(pgDto.getName()))
					.findAny();
			ProductGroup productGroup;
			if (optionalPG.isPresent()) {
				productGroup = optionalPG.get();
				// if not update, skip this iteration.
				if (!productGroup.getThirdpartyUpdate()) {
					continue;
				}
			} else {
				productGroup = new ProductGroup();
				productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
				productGroup.setName(pgDto.getName());
				productGroup.setDataSourceType(DataSourceType.TALLY);
				productGroup.setCompany(company);
			}
			productGroup.setAlias(pgDto.getAlias());
			productGroup.setDescription(pgDto.getDescription());
			productGroup.setActivated(true);

			Optional<ProductGroup> opPgs = saveUpdateProductGroups.stream()
					.filter(so -> so.getName().equalsIgnoreCase(pgDto.getName())).findAny();
			if (opPgs.isPresent()) {
				continue;
			}

			saveUpdateProductGroups.add(productGroup);
		}
		bulkOperationRepositoryCustom.bulkSaveProductGroup(saveUpdateProductGroups);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	public void saveUpdateReceivablePayable(List<OutStandingFocus> OutStandingFocusDTos) {
		log.info("Saving Outstanding Invoice...");
		long start = System.nanoTime();
		LocalDateTime starttime = LocalDateTime.now();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		receivablePayableRepository.deleteByCompanyId(companyId);
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();

		List<String> customerIds = OutStandingFocusDTos.stream().map(a -> a.getCustomerCode())
				.filter(c -> c != null && !c.equalsIgnoreCase("false")).collect(Collectors.toList());

		log.info("Customer Ids size {}", customerIds.size());

		log.info("CompanyID", companyId);

//		Map<String, Double> accountBalanceMap = new HashMap<>();

		List<AccountProfile> accProfiles = accountProfileRepository.findAccountProfileAndCustomerIds(companyId,
				customerIds);

		for (OutStandingFocus OutStandingFocusDTO : OutStandingFocusDTos) {

			Optional<AccountProfile> opCustomers = accProfiles.stream()
					.filter(a -> a.getCustomerId().equalsIgnoreCase(OutStandingFocusDTO.getCustomerCode())).findAny();

			if (opCustomers.isPresent()) {
				ReceivablePayable receivablePayable = new ReceivablePayable();
				receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
				receivablePayable.setAccountProfile(opCustomers.get());
				receivablePayable.setCompany(company);
				receivablePayable
						.setReferenceDocumentBalanceAmount(Double.parseDouble(OutStandingFocusDTO.getBalanceAmount()));
				receivablePayable.setReferenceDocumentNumber(OutStandingFocusDTO.getVoucherNo());
				receivablePayable.setReferenceDocumentDate(convertDate(OutStandingFocusDTO.getVoucherDate()));
				receivablePayable.setBillOverDue(dueUpdate(convertDate(OutStandingFocusDTO.getDueDate())));
				receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
				saveReceivablePayable.add(receivablePayable);

//				 double currBal = accountBalanceMap.containsKey(OutStandingFocusDTO.getCustomerCode())
//							? accountBalanceMap.get(OutStandingFocusDTO.getCustomerCode())
//							: 0.0;
//					accountBalanceMap.put(OutStandingFocusDTO.getCustomerCode(), currBal + Double.parseDouble(OutStandingFocusDTO.getBalanceAmount()));
			}
		}

//	     log.info("Account balance map size {}", accountBalanceMap.size());
//			for (Map.Entry<String, Double> entry : accountBalanceMap.entrySet()) {
//				// entry.getKey(), entry.getValue());
//				accProfiles.stream().filter(a -> a.getCustomerId().equalsIgnoreCase(entry.getKey())).findAny()
//					.ifPresent(ap -> {
//						ap.setClosingBalance(entry.getValue());
//					});
//			}
		log.info("Save account profile size {}", accProfiles.size());
		accountProfileRepository.save(accProfiles);
		log.info("Save receivable size {}", saveReceivablePayable.size());
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		Optional<SyncOperation> oPsyncOperation = syncOperationRepository.findOneByCompanyIdAndOperationType(companyId,
				SyncOperationType.RECEIVABLE_PAYABLE);
		SyncOperation syncOperation;
		if (oPsyncOperation.isPresent()) {
			syncOperation = oPsyncOperation.get();
			syncOperation.setOperationType(SyncOperationType.RECEIVABLE_PAYABLE);
			syncOperation.setCompleted(true);
			syncOperation.setLastSyncStartedDate(starttime);
			syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
			syncOperation.setLastSyncTime(elapsedTime);
			syncOperation.setCompany(company);
			System.out.println("syncCompleted Date : " + syncOperation.getLastSyncCompletedDate());
			syncOperationRepository.save(syncOperation);
		}
		log.info("Sync completed in {} ms", elapsedTime);

	}

	private LocalDate convertDate(String date) {
		if (date != null && date != "" && !date.equalsIgnoreCase("false")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate dateTime = LocalDate.parse(date, formatter);
			return dateTime;
		}
		return LocalDate.now();
	}

	public Long dueUpdate(LocalDate referenceDocDate) {
		if (referenceDocDate != null) {
			LocalDate currentDate = LocalDate.now();
			Long differenceInDays = Math.abs(ChronoUnit.DAYS.between(currentDate, referenceDocDate));
			return differenceInDays;
		}
		return 0L;
	}

}
