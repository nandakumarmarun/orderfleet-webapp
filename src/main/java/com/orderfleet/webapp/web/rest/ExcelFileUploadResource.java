package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.async.ExcelPOIHelper;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.util.RandomUtil;

/**
 * Web controller for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Controller
@RequestMapping("/web")
public class ExcelFileUploadResource {

	private final Logger log = LoggerFactory.getLogger(ExcelFileUploadResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityService activityService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private StageService stageService;

	@Inject
	private DocumentService documentService;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	@Inject
	private ActivityStageRepository activityStageRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ExcelPOIHelper excelPOIHelper;
	
	@Inject
	private  BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject 
	private LocationRepository locationRepository;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private AccountTypeRepository accountTypeRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	@Inject
	private DivisionRepository divisionRepository;
	
	@Inject
	private ProductCategoryRepository productCategoryRepository;
	
	@Inject
	private ProductGroupRepository productGroupRepository;
	
	@Inject
	private ProductGroupProductRepository productGroupProductRepository;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private PriceLevelListRepository priceLevelListRepository;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;
	
	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;

	@RequestMapping(value = "/excel-file-upload", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivities(Model model) throws URISyntaxException {
		log.debug("Web request to get a page to upload a excel file");
		return "company/excelFileUpload";
	}

	@RequestMapping(value = "/excel-file-upload/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFile(MultipartHttpServletRequest request,@RequestParam("locHrchy") boolean locationHirarchy)
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		System.out.println("Location Hierarchy : "+locationHirarchy);
		Iterator<String> itrator = request.getFileNames();
		MultipartFile multipartFile = request.getFile(itrator.next());

		log.info("Request to upload a file: \t" + multipartFile.getOriginalFilename());

		File file = convert(multipartFile);

		String fileExtension = file.getName().split("\\.")[1];

		if (!fileExtension.equalsIgnoreCase("xlsx")) {
			return "FAILED";
		}
		String status = "";
		Optional<Company> opCompany = companyRepository.findById(SecurityUtils.getCurrentUsersCompanyId());
		if(opCompany.isPresent()) {
			Company company = opCompany.get();
			String uploadCustomeStatus = uploadCustomer(file,company,locationHirarchy);
			String uploadProductstatus = uploadProduct(file,company);
			String uploadProductPriceStatus = uploadProductPrice(file,company);
			String uploadStock = uploadStock(file,company);

			if (uploadCustomeStatus.equalsIgnoreCase("SUCCESS") && uploadProductstatus.equalsIgnoreCase("SUCCESS")
					&& uploadProductPriceStatus.equalsIgnoreCase("SUCCESS") && uploadStock.equalsIgnoreCase("SUCCESS")) {
				status = "SUCCESS";
				delete(file);
			} else {
				status = "UPLOAD FAILED";
			}
		}	
			return status;
	}

	private String uploadCustomer(File file,Company company,boolean isLocationHrchy) throws IOException {
		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 0);
			parseAndUploadLocations(data,company);
			if(isLocationHrchy) {
				parseAndUploadLocationsHierarchy(data,company);
			}
			parseAndUploadAccountPriceLevel(data,company);
			parseAndUploadAccountProfile(data,company);
			parseAndUploadLocationAccountProfile(data,company);
		return "SUCCESS";
	}

	private String uploadProduct(File file, Company company) throws IOException {
		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 1);
			parseAndUploadProductCategory(data,company);
			parseAndUploadProductProfile(data,company);
			parseAndUploadProductGroupProfile(data,company);
		return "SUCCESS";
	}

	private String uploadProductPrice(File file, Company company) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 2);
			parseAndUploadPriceLevel(data,company);
			parseAndUploadPriceLevelList(data,company);
			parseAndUploadProductPrice(data,company);
		return "SUCCESS";
	}

	private String uploadStock(File file,Company company) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 3);
		parseAndUploadStockLocation(data,company);
		parseAndUploadStock(data,company);

		return "SUCCESS";
	}

	private void parseAndUploadAccountPriceLevel(Map<Integer, List<String>> datas , Company company) {

		List<PriceLevel> priceLevelList = new ArrayList<>();
		List<PriceLevel> existingPriceLevelList = priceLevelRepository.findAllByCompanyId();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			PriceLevel priceLevel= new PriceLevel();
			
			String name = dataList.get(24) != null ? dataList.get(24) : "";
			if("".equals(name)) {
				continue;
			}
			Optional<PriceLevel> opPriceLevel = existingPriceLevelList.stream().filter(epl -> epl.getName().equals(name)).findAny();
			if(opPriceLevel.isPresent()) {
				priceLevel = opPriceLevel.get();
			}else {
				priceLevel.setName(dataList.get(24) != null ? dataList.get(24) : "");
				priceLevel.setAlias(dataList.get(24) != null ? dataList.get(24) : "");
				priceLevel.setCompany(company);
				priceLevel.setPid(PriceLevelService.PID_PREFIX+ RandomUtil.generatePid());
			}
			
			if(!priceLevelList.stream().filter(loc -> loc.getName().equals(name)).findAny().isPresent())
				priceLevelList.add(priceLevel);
		}
		System.out.println("Account Price Level :"+priceLevelList.size());
		priceLevelRepository.save(priceLevelList);

	}
	
	private void parseAndUploadAccountProfile(Map<Integer, List<String>> datas,Company company) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AT_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType accountType = accountTypeRepository.findByCompanyIdAndName("Sundry Debtors");
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

		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get all by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId();
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

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		List<PriceLevel> existingPriceLevelList = priceLevelRepository.findAllByCompanyId();
		if(!opUser.isPresent()) {
			return;
		}
		List<AccountProfile> accountProfileList = new ArrayList<>();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			AccountProfile accountProfile = new AccountProfile();
			String name = dataList.get(0) != null ? dataList.get(0) : "";
			if(name.isEmpty()) {
				continue;
			}
			Optional<AccountProfile> opAccountProfile = existingAccountProfiles.stream().filter(eap -> eap.getAlias().equals(name)).findAny();
			if(opAccountProfile.isPresent()) {
				accountProfile = opAccountProfile.get();
			}else {
				accountProfile.setName(dataList.get(0) != null ? dataList.get(0) : "");
				accountProfile.setAlias(dataList.get(1) != null ? dataList.get(1) : "");
				accountProfile.setPid(AccountProfileService.PID_PREFIX +  RandomUtil.generatePid());
				accountProfile.setAccountStatus(AccountStatus.Unverified);
				accountProfile.setAccountType(accountType);
				accountProfile.setUser(opUser.get());
				accountProfile.setCompany(company);
			}
			String defaultPriceLevel = dataList.get(24) != null && !dataList.get(24).equals("") ? dataList.get(24) : "General";
			Optional<PriceLevel> opPriceLevel = existingPriceLevelList.stream().filter(epl -> epl.getName().equals(defaultPriceLevel)).findAny();
			if(opPriceLevel.isPresent()) {
				accountProfile.setDefaultPriceLevel(opPriceLevel.get());
			}
			accountProfile.setCity(!dataList.get(5).isEmpty() ? dataList.get(5) : "No City");
			accountProfile.setPhone1(dataList.get(15) != null &&  1< dataList.get(15).length() && dataList.get(15).length() < 13 ? dataList.get(15) : "9999999999");
			accountProfile.setTinNo(dataList.get(19) != null ? dataList.get(19) : "");
			accountProfile.setAddress(dataList.get(2) != null ? dataList.get(2) : "No Address");
			if(!accountProfileList.stream().filter(acc -> acc.getName().equals(name)).findAny().isPresent())
				accountProfileList.add(accountProfile);
		}	
		System.out.println("Account Profiles : "+accountProfileList.size());
		accountProfileRepository.save(accountProfileList);
	}
			
	

	
	private void parseAndUploadLocations(Map<Integer, List<String>> datas, Company company) {

		List<Location> locationList = new ArrayList<>();
		List<Location> existingLocationList = locationRepository.findAllByCompanyId();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			Location location = new Location();
			
			String name = dataList.get(23) != null ? dataList.get(23) : "";
			if("".equals(name)) {
				continue;
			}
			Optional<Location> opLocation = existingLocationList.stream().filter(ell -> ell.getAlias().equals(name)).findAny();
			if(opLocation.isPresent()) {
				location = opLocation.get();
			}else {
				location.setName(dataList.get(23) != null ? dataList.get(23) : "");
				location.setAlias(dataList.get(23) != null ? dataList.get(23) : "");
				location.setCompany(company);
				location.setPid(LocationService.PID_PREFIX+ RandomUtil.generatePid());
			}
			
			if(!locationList.stream().filter(loc -> loc.getName().equals(name)).findAny().isPresent())
				locationList.add(location);
		}
		System.out.println("Locations :"+locationList.size());
		locationRepository.save(locationList);
		
	}
	
	
	private void parseAndUploadLocationsHierarchy(Map<Integer, List<String>> datas, Company company) {

		List<Location> existingLocationList = locationRepository.findAllByCompanyId();
		Optional<Location> defaultLocation = locationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Territory");
		if(!defaultLocation.isPresent()) {
			return;
		}
		

		Set<String> accountNames = new LinkedHashSet<String>();  
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			
			String name = dataList.get(23) != null ? dataList.get(23) : "";
			if("".equals(name)) {
				continue;
			}
			accountNames.add(name);
		}
		tpAccountProfileManagementService.saveUpdateLocationHierarchyExcel(company, accountNames, existingLocationList, defaultLocation.get());
		
		
	}
	
	private void parseAndUploadLocationAccountProfile(Map<Integer, List<String>> datas , Company company) {

		List<Location> locations =  locationRepository.findAllByCompanyId();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
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

		Set<LocationAccountProfile> locationAccountProfileList = new HashSet<>();
		if(!datas.isEmpty()) {
			List<Long> accountProfileIds = 
					accountProfiles.stream().filter(ap -> ap.getAccountType().getName().equals("Company"))
					.map(acc -> acc.getId()).collect(Collectors.toList());

			locationAccountProfileRepository.deleteByCompanyExcludeDefault(company.getId(),accountProfileIds);

		}
		
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
			String locationAlias = dataList.get(23) != null ? dataList.get(23) : "";
			String accountAlias = dataList.get(0) != null ? dataList.get(0) : "";

			Optional<Location> location = locations.stream().filter(loc -> loc.getAlias().equals(locationAlias)).findAny();
			Optional<AccountProfile> accountProfile = accountProfiles.stream().filter(acc -> acc.getAlias().equals(accountAlias)).findAny();
			if(location.isPresent() && accountProfile.isPresent()) {
				locationAccountProfile.setAccountProfile(accountProfile.get());
				locationAccountProfile.setLocation(location.get());
				locationAccountProfile.setCompany(company);
				locationAccountProfileList.add(locationAccountProfile);
			}
			
		}
		System.out.println("Location Account Profile : "+locationAccountProfileList.size());
		locationAccountProfileRepository.save(locationAccountProfileList);
		
	}
	
	
	private void parseAndUploadProductCategory(Map<Integer, List<String>> datas , Company company) {
		List<ProductCategory> existingProductCategories = productCategoryRepository.findAllByCompanyId();
		List<ProductCategory> productCategoryList = new ArrayList<>();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			ProductCategory productCategory = new ProductCategory();
			
			String name = dataList.get(7) != null ? dataList.get(7) : "Not Applicable";
			Optional<ProductCategory> opProductCategory = existingProductCategories.stream().filter(epc -> epc.getAlias().equals(name)).findAny();
			if(opProductCategory.isPresent()) {
				productCategory = opProductCategory.get();
			}else {
				productCategory.setAlias(dataList.get(7) != null ? dataList.get(7) : "");
				productCategory.setName(dataList.get(7) != null ? dataList.get(7) : "");
				productCategory.setCompany(company);
				productCategory.setPid(ProductCategoryService.PID_PREFIX+ RandomUtil.generatePid());
				productCategory.setActivated(true);
			}
			if(!productCategoryList.stream().filter(ppl -> ppl.getName().equals(name)).findAny().isPresent())
				productCategoryList.add(productCategory);
		}
		System.out.println("Product Categories :"+productCategoryList.size());
		productCategoryRepository.save(productCategoryList);

	}
	
	
	private void parseAndUploadProductProfile(Map<Integer, List<String>> datas , Company company) {
		
		List<ProductProfile> existingProductList = productProfileRepository.findAllByCompanyId();
		Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		List<ProductCategory> productCategoryList = productCategoryRepository.findAllByCompanyId();
		
		List<ProductProfile> productList = new ArrayList<>();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			ProductProfile productProfile = new ProductProfile();
			
			String name = dataList.get(0) != null ? dataList.get(0) : "";
			Optional<ProductProfile> opProduct = existingProductList.stream().filter(ell -> ell.getAlias().equals(name)).findAny();
			if(opProduct.isPresent()) {
				productProfile = opProduct.get();
			}else {
				productProfile.setAlias(dataList.get(0) != null ? dataList.get(0) : "");
				productProfile.setName(dataList.get(1) != null ? dataList.get(1) : "");
				productProfile.setCompany(company);
				productProfile.setPid(ProductProfileService.PID_PREFIX+ RandomUtil.generatePid());
				productProfile.setDataSourceType(DataSourceType.VENDOR);
				productProfile.setDivision(defaultDivision);
				String categoryName = (dataList.get(7) != null && !dataList.get(7).equals("")) ? dataList.get(7) : "Not Applicable";
				Optional<ProductCategory> opProductCategory = productCategoryList.stream().filter(pcl -> pcl.getName().equals(categoryName)).findAny();
				productProfile.setProductCategory(opProductCategory.get());
				productProfile.setActivated(true);
			}
				productProfile.setPrice(new BigDecimal(0.0));
				productProfile.setSku(dataList.get(3) != null ? dataList.get(3) : "");
				productProfile.setTaxRate(0.0);
				productProfile.setUnitQty(dataList.get(6) != null ? Double.parseDouble(dataList.get(6)) : 1.0);
			
			if(!productList.stream().filter(ppl -> ppl.getName().equals(name)).findAny().isPresent())
				productList.add(productProfile);
		}
		System.out.println("Product Profiles :"+productList.size());
		productProfileRepository.save(productList);

	}
	
	
	private void parseAndUploadProductGroupProfile(Map<Integer, List<String>> datas , Company company) {

		List<ProductProfile> productProfiles =  productProfileRepository.findAllByCompanyId();
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);
		Set<ProductGroupProduct> productGroupProductList = new HashSet<>();
		if(!datas.isEmpty()) {
			productGroupProductRepository.deleteByCompany(company.getId());
		}
		
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			ProductGroupProduct productGroupProduct = new ProductGroupProduct();
			String productAlias = dataList.get(0) != null ? dataList.get(0) : "";
			Optional<ProductGroup> opProductGroup = productGroups.stream().filter(pg -> pg.getName().equals("General")).findAny();
			if(!opProductGroup.isPresent()) {
				log.info("Product Group  General  not present");
				throw new IllegalArgumentException("Product Group General not present");
			}

			Optional<ProductProfile> opProductProfile = productProfiles.stream().filter(pp -> pp.getAlias().equals(productAlias)).findAny();
			if(opProductProfile.isPresent() && opProductGroup.isPresent()) {
				productGroupProduct.setProduct(opProductProfile.get());
				productGroupProduct.setProductGroup(opProductGroup.get());
				productGroupProduct.setCompany(company);
				productGroupProductList.add(productGroupProduct);
			}
			
		}
		System.out.println(" Product Group Profile Group : "+productGroupProductList.size());
		productGroupProductRepository.save(productGroupProductList);
		
	}
	
	private void parseAndUploadPriceLevel(Map<Integer, List<String>> datas , Company company) {

		List<PriceLevel> priceLevelList = new ArrayList<>();
		List<PriceLevel> existingPriceLevelList = priceLevelRepository.findAllByCompanyId();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			PriceLevel priceLevel= new PriceLevel();
			
			String name = dataList.get(1) != null ? dataList.get(1) : "";
			Optional<PriceLevel> opPriceLevel = existingPriceLevelList.stream().filter(epl -> epl.getName().equals(name)).findAny();
			if(opPriceLevel.isPresent()) {
				priceLevel = opPriceLevel.get();
			}else {
				priceLevel.setName(dataList.get(1) != null ? dataList.get(1) : "");
				priceLevel.setAlias(dataList.get(1) != null ? dataList.get(1) : "");
				priceLevel.setCompany(company);
				priceLevel.setPid(PriceLevelService.PID_PREFIX+ RandomUtil.generatePid());
			}
			
			if(!priceLevelList.stream().filter(loc -> loc.getName().equals(name)).findAny().isPresent())
				priceLevelList.add(priceLevel);
		}
		System.out.println("Price Level :"+priceLevelList.size());
		priceLevelRepository.save(priceLevelList);

	}

	private void parseAndUploadPriceLevelList(Map<Integer, List<String>> datas , Company company) {

		List<ProductProfile> productProfiles =  productProfileRepository.findAllByCompanyId();
		List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyId();
		List<PriceLevelList> priceLevelListList = new ArrayList<>();
		if(!datas.isEmpty()) {
			priceLevelListRepository.deleteByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		}
		
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			PriceLevelList priceLevelList = new PriceLevelList();
			String productAlias = dataList.get(0) != null ? dataList.get(0) : "";
			String priceLevelAlias = dataList.get(1) != null ? dataList.get(1) : "";
			double price = dataList.get(3) != null ? Double.parseDouble(dataList.get(3)) : 0.0;
			Optional<ProductProfile> opProductProfile = productProfiles.stream().filter(pg -> pg.getAlias().equals(productAlias)).findAny();
			Optional<PriceLevel> opPriceLevel = priceLevels.stream().filter(pp -> pp.getName().equals(priceLevelAlias)).findAny();
			if(opProductProfile.isPresent() && opPriceLevel.isPresent()) {
				priceLevelList.setProductProfile(opProductProfile.get());
				priceLevelList.setPriceLevel(opPriceLevel.get());
				priceLevelList.setPrice(price);
				priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
				priceLevelList.setCompany(company);
				priceLevelListList.add(priceLevelList);
			}
			
		}
		System.out.println(" Price Level List : "+priceLevelListList.size());
		priceLevelListRepository.save(priceLevelListList);
	}
	
	private void parseAndUploadProductPrice(Map<Integer, List<String>> datas , Company company) {

		List<ProductProfile> existingProductProfiles =  productProfileRepository.findAllByCompanyId();
		List<ProductProfile> productProfiles = new ArrayList<>();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			String productAlias = dataList.get(0) != null ? dataList.get(0) : "";
			double taxRate = dataList.get(4) != null ? Double.parseDouble(dataList.get(4)) : 0.0;
			double price = dataList.get(3) != null ? Double.parseDouble(dataList.get(3)) : 0.0;
			Optional<ProductProfile> opProductProfile = existingProductProfiles.stream().filter(pg -> pg.getAlias().equals(productAlias)).findAny();
			
			if(opProductProfile.isPresent()) {
				ProductProfile productProfile = opProductProfile.get();
				productProfile.setPrice(new BigDecimal(price));
				productProfile.setTaxRate(taxRate);
				productProfiles.add(productProfile);
			}
			
		}
		System.out.println(" Product Price & Tax update : "+productProfiles.size());
		productProfileRepository.save(productProfiles);
	}

	
	private void parseAndUploadStockLocation(Map<Integer, List<String>> datas , Company company) {

		List<StockLocation> existingStockLocations =  stockLocationRepository.findAllByCompanyId();
		List<StockLocation> stockLocations = new ArrayList<>();
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			String name = dataList.get(1) != null ? dataList.get(1) : "";
			double quantity = dataList.get(3) != null ? Double.parseDouble(dataList.get(3)) : 0.0;
			Optional<StockLocation> opStockLocation = existingStockLocations.stream().filter(pg -> pg.getAlias().equals(name)).findAny();
			StockLocation stockLocation = new StockLocation();
			if(opStockLocation.isPresent()) {
				stockLocation = opStockLocation.get();
			}else {
				stockLocation.setName(name);
				stockLocation.setAlias(name);
				stockLocation.setCompany(company);
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setStockLocationType(StockLocationType.ACTUAL);
			}
			
			if(!stockLocations.stream().filter(loc -> loc.getName().equals(name)).findAny().isPresent())
				stockLocations.add(stockLocation);
		}
		
		
		
		System.out.println(" Stock Locations : "+stockLocations.size());
		stockLocationRepository.save(stockLocations);
	}
	
	private void parseAndUploadStock(Map<Integer, List<String>> datas , Company company) {

		List<ProductProfile> productProfiles =  productProfileRepository.findAllByCompanyId();
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId();
		List<OpeningStock> openingStockList = new ArrayList<>();
		if(!datas.isEmpty()) {
			openingStockRepository.deleteByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		}
		
		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);
			OpeningStock openingStock = new OpeningStock();
			String productAlias = dataList.get(0) != null ? dataList.get(0) : "";
			String stockLocation = dataList.get(1) != null ? dataList.get(1) : "";
			double quantity = dataList.get(3) != null ? Double.parseDouble(dataList.get(3)) : 0.0;
			String stockDate = dataList.get(4) != null ? dataList.get(4) : "";
			//"dd/MMM/yyyy"
			Date date = new Date();
			try {
				date = new SimpleDateFormat("dd/MMM/yyyy").parse(stockDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
			LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
			
			Optional<ProductProfile> opProductProfile = productProfiles.stream().filter(pg -> pg.getAlias().equals(productAlias)).findAny();
			Optional<StockLocation> opStockLocation = stockLocations.stream().filter(sl -> sl.getName().equals(stockLocation)).findAny();
			if(opProductProfile.isPresent() && opStockLocation.isPresent()) {
				openingStock.setProductProfile(opProductProfile.get());
				openingStock.setActivated(true);
				openingStock.setBatchNumber("123");
				openingStock.setCompany(company);
				openingStock.setCreatedDate(LocalDateTime.now());
				openingStock.setOpeningStockDate(localDateTime);
				openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid());
				openingStock.setQuantity(quantity);
				openingStock.setStockLocation(opStockLocation.get());
				openingStockList.add(openingStock);
			}
			
		}
		System.out.println(" Opening stock : "+openingStockList.size());
		openingStockRepository.save(openingStockList);

	}

	public static File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
	
	public static void delete(File file) throws IOException {
		file.delete();
	}

}
