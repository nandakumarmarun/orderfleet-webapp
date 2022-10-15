package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.stereotype.Controller;
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
import com.orderfleet.webapp.service.RouteCodeService;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;
import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;
import com.orderfleet.webapp.web.rest.dto.FiscalYearDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.OutStandingResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewResponceFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.service.AccountProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.OutStandingFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.ProductProfileFocusUploadService;

import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class UploadFocusResource {

	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";
	
	private static String LENGTH_TYPE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetLengthType?Auth_Token=";

	private static String ACCOUNT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetCustomerData?Auth_Token=";

	private static String PRODUCT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GETProductsWithPrice?Auth_Token=";

	private static String RECEIVABLE_PAYABLE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/OutStandingReportALL?Auth_Token=";

	private static String GET_MASTER_DATA_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetMasterData?Auth_Token=";

	@Inject
	private AccountTypeService AccountTypeService;

	@Inject
	private AccountProfileFocusUploadService accountProfileFocusUploadService;

	@Inject
	private OutStandingFocusUploadService outStandingFocusUploadService;

	@Inject
	private BrandDevaService brandDevaService;

	@Inject
	private FiscalYearService fiscalYearService;

	@Inject
	private ContryFocusService contryFocusService;

	@Inject
	private StateFocusService stateFocusService;

	@Inject
	private BussinessUnitService bussinessUnitService;

	@Inject
	private LengthTypeService lengthTypeService;

	@Inject
	private RouteCodeService routeCodeService;

	@Inject
	private DistrictFocusService districtFocusService;

	@Inject
	private CityService cityService;

	@Inject
	private UnitsService unitsService;

	@Inject
	private ProductProfileFocusUploadService productProfileFocusUploadService;
	private final Logger log = LoggerFactory.getLogger(UploadFocusResource.class);

	@RequestMapping(value = "/upload-focus", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Focus Masters");

		return "company/uploadFocus";
	}

	@RequestMapping(value = "/upload-focus/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
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
					accountProfileFocusUploadService.saveUpdateAccountProfiles(
							accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles());
				}
			}
			System.out.println("response ok");
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
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
					productProfileFocusUploadService.saveUpdateProductProfiles(
							productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct());
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/uploadReceivablePayable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
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

					outStandingFocusUploadService.saveUpdateReceivablePayable(
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

//	@RequestMapping(value = "/upload-focus/uploadProductProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {
//
//		log.debug("web request to upload productProfiles");
//
//		String jsonstring = productRequestBody();
//		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());
//
//		RestTemplate restTemplate = new RestTemplate();
//		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//
//		log.info("Product profile URL:" + PRODUCT_PROFILE_API_URL);
//
//		ProductProfileResponseFocus productProfileResponseFocus = restTemplate.postForObject(PRODUCT_PROFILE_API_URL,
//				entity, ProductProfileResponseFocus.class);
//
//		productProfileFocusUploadService
//				.saveUpdateProductProfiles(productProfileResponseFocus.getGetMasterDataResult().getProductProfiles());
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

	private String productRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Item_Master");
		return jsonObject.toString();

	}

	private String brandDevaRequestBody() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "BrandDeva");
		return jsonObject.toString();

	}

	private String lengthTypeRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Length_Type");
		return jsonObject.toString();

	}

	private String districtRequestBody() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "District");
		return jsonObject.toString();
	}

	private String fiscalYearRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "FiscalYear");
		return jsonObject.toString();
	}

	private String unitMasterRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Unit_Master");
		return jsonObject.toString();
	}

	private String BusinessUnitRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Business_Unit");
		return jsonObject.toString();
	}

	private String routeCodeRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Route_Code");
		return jsonObject.toString();
	}

	private String countryRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Country");
		return jsonObject.toString();
	}

	private String customerTypeRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Customer_Type");
		return jsonObject.toString();
	}

	private String stateRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "State");
		return jsonObject.toString();
	}

	private String cityRequestBody() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "City");
		return jsonObject.toString();
	}

	@RequestMapping(value = "/upload-focus/lengthType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadlengthType() throws IOException, JSONException, ParseException {

		log.debug("web request to length types");

		List<LengthTypeDTO> lengthTypeDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = lengthTypeRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + LENGTH_TYPE_API_URL);

		LengthTypeReponseFocus MasterDataReponseFocus = restTemplate.getForObject(LENGTH_TYPE_API_URL + authToken, LengthTypeReponseFocus.class);

		List<LengthTypeResponseObject> branddeva = MasterDataReponseFocus.getLengthTypeDataFocus().getMasterDatas();

		MasterDataReponseFocus.getLengthTypeDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			LengthTypeDTO lengthTypeDTO = new LengthTypeDTO();
			lengthTypeDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			lengthTypeDTO.setMasterName(masterdataresponseObject.getMasterName());
			lengthTypeDTO.setMeterConversion(Double.parseDouble(masterdataresponseObject.getMeterConversion()));
			lengthTypeDTOs.add(lengthTypeDTO);
		});

		lengthTypeService.save(lengthTypeDTOs);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	@RequestMapping(value = "/upload-focus/branddeva", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadBrandDeva() throws IOException, JSONException, ParseException {

		log.debug("web request to upload productProfiles");

		List<BrandDevaDTO> brandDevaDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = brandDevaRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			BrandDevaDTO brandDevaDTO = new BrandDevaDTO();
			brandDevaDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			brandDevaDTO.setMasterName(masterdataresponseObject.getMasterName());
			brandDevaDTOs.add(brandDevaDTO);
		});


		brandDevaService.save(brandDevaDTOs);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/district", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploaddistrict() throws IOException, JSONException, ParseException {

		log.debug("web request to upload districts");

		List<DistrictFocusDTO> districtFocusDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = districtRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
		});


		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			DistrictFocusDTO districtFocusDTO = new DistrictFocusDTO();
			districtFocusDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			districtFocusDTO.setMasterName(masterdataresponseObject.getMasterName());
			districtFocusDTOs.add(districtFocusDTO);
		});
		districtFocusService.save(districtFocusDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/unitmaster", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadUnitMaster() throws IOException, JSONException, ParseException {

		log.debug("web request to upload unit master");

		List<UnitsDTO> unitsDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = unitMasterRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			UnitsDTO unitDTO = new UnitsDTO();
			unitDTO.setName(masterdataresponseObject.getMasterCode());
			unitDTO.setShortName(masterdataresponseObject.getMasterName());
			unitsDTOs.add(unitDTO);
		});
		unitsService.saveorUpdate(unitsDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/customertype", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadCustomerType() throws IOException, JSONException, ParseException {

		log.debug("web request to upload CustomerType");

		List<AccountTypeDTO> accounttypeDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = customerTypeRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			AccountTypeDTO accountTypeDTO = new AccountTypeDTO();
			accountTypeDTO.setName(masterdataresponseObject.getMasterCode());
			accountTypeDTO.setAlias(masterdataresponseObject.getMasterCode());
			accounttypeDTOs.add(accountTypeDTO);
		});
		AccountTypeService.saveOrUpdate(accounttypeDTOs);
	
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/city", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadCity() throws IOException, JSONException, ParseException {

		log.debug("web request to upload unit master");

		List<CityFocusDTO> cityFocusDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = cityRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			CityFocusDTO cityFocusDTO = new CityFocusDTO();
			cityFocusDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			cityFocusDTO.setMasterName(masterdataresponseObject.getMasterName());
			cityFocusDTOs.add(cityFocusDTO);
		});
		cityService.save(cityFocusDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/businessunit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadBusinessUnit() throws IOException, JSONException, ParseException {

		log.debug("web request to upload BussinessUnit");

		List<BussinessUnitDTO> bussinessUnitDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = BusinessUnitRequestBody();
		System.out.println();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			BussinessUnitDTO bussinessUnitDTO = new BussinessUnitDTO();
			bussinessUnitDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			bussinessUnitDTO.setMasterName(masterdataresponseObject.getMasterName());
			bussinessUnitDTOs.add(bussinessUnitDTO);
		});

		bussinessUnitService.save(bussinessUnitDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/routecode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadRouteCode() throws IOException, JSONException, ParseException {

		log.debug("web request to routecode ");

		List<RouteCodeDTO> routeCodeDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = routeCodeRequestBody();
		System.out.println(jsonstring);
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			RouteCodeDTO routeCodeDTO = new RouteCodeDTO();
			routeCodeDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			routeCodeDTO.setMasterName(masterdataresponseObject.getMasterName());
			routeCodeDTOs.add(routeCodeDTO);
		});
	
		routeCodeService.save(routeCodeDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/fiscalyear", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadFiscalYear() throws IOException, JSONException, ParseException {

		log.debug("web request to fiscal year ");

		List<FiscalYearDTO> fiscalYearDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = fiscalYearRequestBody();
		System.out.println(jsonstring);
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();
		
		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			FiscalYearDTO fiscalYearDTO = new FiscalYearDTO();
			fiscalYearDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			fiscalYearDTO.setMasterName(masterdataresponseObject.getMasterName());
			fiscalYearDTOs.add(fiscalYearDTO);
		});
		fiscalYearDTOs.forEach(data -> System.out.println(data.getMasterCode() + "=====" + data.getMasterName()));
		fiscalYearService.save(fiscalYearDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/state", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadState() throws IOException, JSONException, ParseException {

		log.debug("web request to upload unit master");

		List<StateFocusDTO> stateFocusDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = stateRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			StateFocusDTO stateFocusDTO = new StateFocusDTO();
			stateFocusDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			stateFocusDTO.setMasterName(masterdataresponseObject.getMasterName());
			stateFocusDTOs.add(stateFocusDTO);
		});
		stateFocusService.save(stateFocusDTOs);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/contry", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadContry() throws IOException, JSONException, ParseException {

		log.debug("web request to upload contry");

		List<ContryFocusDTO> contryFocusDTOs = new ArrayList();
		String authToken = getAuthenticationToken();
		String jsonstring = countryRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("MASTER_DATA_API:" + GET_MASTER_DATA_API_URL);

		MasterDataReponseFocus MasterDataReponseFocus = restTemplate.postForObject(GET_MASTER_DATA_API_URL + authToken,
				entity, MasterDataReponseFocus.class);

		List<MasterDataResponseObject> branddeva = MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas();

		MasterDataReponseFocus.getGetMasterDataFocus().getMasterDatas().forEach(masterdataresponseObject -> {
			ContryFocusDTO contryFocusDTO = new ContryFocusDTO();
			contryFocusDTO.setMasterCode(masterdataresponseObject.getMasterCode());
			contryFocusDTO.setMasterName(masterdataresponseObject.getMasterName());
			contryFocusDTOs.add(contryFocusDTO);
		});
		
		contryFocusService.save(contryFocusDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private AuthenticationRequstFocus authenticationBody() {
		AuthenticationRequstFocus authenticationRequstFocus = new AuthenticationRequstFocus();
		authenticationRequstFocus.setUserName("su");
		authenticationRequstFocus.setPassword("deva$focus");
		authenticationRequstFocus.setCompanyCode("070");

		return authenticationRequstFocus;
	}

}
