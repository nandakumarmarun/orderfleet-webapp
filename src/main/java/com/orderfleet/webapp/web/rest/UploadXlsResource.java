
package com.orderfleet.webapp.web.rest;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.repository.DistrictCRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.StateCRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UnitsRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;

/**
 * used to upload xls
 *
 * @author Sarath
 * @since Nov 16, 2016
 */

@Controller
@RequestMapping("/web")
public class UploadXlsResource {

	private final Logger log = LoggerFactory.getLogger(UploadXlsResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private CounrtyCRepository countryCRepository;

	@Inject
	private StateCRepository stateCRepository;

	@Inject
	private DistrictCRepository districtCRepository;

	@Inject
	private UnitsRepository unitsRepository;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@RequestMapping(value = "/upload-xls", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetGroups");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/uploadXls";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/upload-xls/saveAccountXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(MultipartHttpServletRequest request) {

		log.info("Request to Upload excel accountProfile..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] accountsNumbers = request.getParameterValues("accountNumbers");
		String[] allAccountNumbers = accountsNumbers[0].split(",");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountType> accountTypeDTOs = accountTypeRepository.findAllByCompanyPid(companyPid[0]);
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
		List<Location> locations = locationRepository
				.findAllByCompanyPidAndLocationActivatedOrDeactivated(companyPid[0], Boolean.TRUE);
		if (accountTypeDTOs.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		AccountType deafultAccountType = accountTypeDTOs.get(0);
		Company company = deafultAccountType.getCompany();

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		User user = userRepository.findTop1ByCompanyId(company.getId());
		List<PriceLevel> priceLevelDTOs = priceLevelRepository.findByCompanyId(company.getId());
		List<CountryC> countrycDTOS = countryCRepository.findAllCountries();
		List<StateC> statecDTOs = stateCRepository.findAllStates();
		List<DistrictC> districtcDTOs = districtCRepository.findAllDistricts();
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
				.findAllByCompanyId(company.getId());
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
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
		try {

			Iterator<String> itrator = request.getFileNames();
			MultipartFile multipartFile = request.getFile(itrator.next());
			System.out.println(allAccountNumbers.toString());
			int NameNumber = Integer.parseInt(allAccountNumbers[0]);

			int addressNumber = Integer.parseInt(allAccountNumbers[1]);
			int cityNumber = Integer.parseInt(allAccountNumbers[2]);
			int locationNumber = Integer.parseInt(allAccountNumbers[3]);
			int pinNumber = Integer.parseInt(allAccountNumbers[4]);
			int phone1Number = Integer.parseInt(allAccountNumbers[5]);

			int eMail1Number = Integer.parseInt(allAccountNumbers[6]);

			int descriptionNumber = Integer.parseInt(allAccountNumbers[7]);
			int contactPersonNumber = Integer.parseInt(allAccountNumbers[8]);
			int accountTypeNumber = Integer.parseInt(allAccountNumbers[9]);
			int territoryNumber = Integer.parseInt(allAccountNumbers[10]);
			int aliasNumber = Integer.parseInt(allAccountNumbers[11]);
			int closingBalanceNumber = Integer.parseInt(allAccountNumbers[12]);
			int creditDaysNumber = Integer.parseInt(allAccountNumbers[13]);
			int creditLimitNumber = Integer.parseInt(allAccountNumbers[14]);
			int priceLevelNumber = Integer.parseInt(allAccountNumbers[15]);
			int tinNoNumber = Integer.parseInt(allAccountNumbers[16]);

			int customerIdNumber = Integer.parseInt(allAccountNumbers[17]);
			int customerCodeNumber = Integer.parseInt(allAccountNumbers[18]);
			int countryNumber = Integer.parseInt(allAccountNumbers[19]);
			int stateNumber = Integer.parseInt(allAccountNumbers[20]);
			int districtNumber = Integer.parseInt(allAccountNumbers[21]);

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			int duplicateCount = 0;

			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					String accountProofileName = row.getCell(NameNumber).getStringCellValue();
					if (accountProofileName == null || accountProofileName.isEmpty()) {
						break;
					}

					Optional<AccountProfile> accountProfileDTO = existingAccountProfiles.stream()
							.filter(acp -> acp.getName().equals(accountProofileName)).findAny();

					AccountProfile accProfile = new AccountProfile();
					if (accountProfileDTO.isPresent()) {
						accProfile = accountProfileDTO.get();
					} else {
						accProfile = new AccountProfile();
						accProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
						accProfile.setCompany(company);
						accProfile.setAccountStatus(AccountStatus.Unverified);
//						accProfile.setAccountType(deafultAccountType);
						accProfile.setUser(user);
						accProfile.setName(accountProofileName);
					}

					if (addressNumber == -1) {
						accProfile.setAddress("No Address");
					} else {

						Cell cell = row.getCell(addressNumber);

						if (cell == null) {
							accProfile.setAddress("No Address");
						} else {
							String address = "";

							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(addressNumber).getCellType() == 0) {
								address = String.valueOf(row.getCell(addressNumber).getNumericCellValue());
							}

							if (row.getCell(addressNumber).getCellType() == 1) {
								address = String.valueOf(row.getCell(addressNumber).getStringCellValue());
							}

							accProfile.setAddress(address);
						}
					}
					if (cityNumber == -1) {
						accProfile.setCity("No city");
					} else {
						Cell cell = row.getCell(cityNumber);

						if (cell == null) {
							accProfile.setCity("No city");
						} else {
							String city = "";

							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(cityNumber).getCellType() == 0) {
								city = String.valueOf(row.getCell(cityNumber).getNumericCellValue());
							}

							if (row.getCell(cityNumber).getCellType() == 1) {
								city = String.valueOf(row.getCell(cityNumber).getStringCellValue());
							}
							accProfile.setCity(city);
						}
					}
					if (locationNumber != -1) {
						Cell cell = row.getCell(locationNumber);
						if (cell == null) {
							accProfile.setLocation(" ");
						} else {
							String location = "";

							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(locationNumber).getCellType() == 0) {
								location = String.valueOf(row.getCell(locationNumber).getNumericCellValue());
							}

							if (row.getCell(locationNumber).getCellType() == 1) {
								location = String.valueOf(row.getCell(locationNumber).getStringCellValue());
							}
							accProfile.setLocation(location);
						}
						
					}
					if (pinNumber != -1) {
						Cell cell = row.getCell(pinNumber);
						if (cell == null) {
							accProfile.setPin(" ");
						} else {
							String pin = formatter.formatCellValue(row.getCell(pinNumber));
							accProfile.setPin(pin);
						}
					}
					if (phone1Number != -1) {
						Cell cell = row.getCell(phone1Number);
						if (cell == null) {
							accProfile.setPhone2(" ");
						} else {
							String phones = formatter.formatCellValue(row.getCell(phone1Number));
							String[] phoneNums = phones.split("\n|\\/|\\,");
							accProfile.setPhone1(phoneNums[0]);
							if (phoneNums.length > 1) {
								accProfile.setPhone2(phoneNums[1]);
							}
						}
					}

					if (eMail1Number != -1) {
						Cell cell = row.getCell(eMail1Number);
						if (cell == null) {
							accProfile.setEmail1(" ");
						} else {
							accProfile.setEmail1(row.getCell(eMail1Number).getStringCellValue());
						}
					}

					if (descriptionNumber != -1) {
						Cell cell = row.getCell(descriptionNumber);
						if (cell == null) {
							accProfile.setDescription(" ");
						} else {
							String descriptions= "";

							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(descriptionNumber).getCellType() == 0) {
								descriptions = String.valueOf(row.getCell(descriptionNumber).getNumericCellValue());
							}

							if (row.getCell(descriptionNumber).getCellType() == 1) {
								descriptions = String.valueOf(row.getCell(descriptionNumber).getStringCellValue());
							}
							accProfile.setDescription(descriptions);
						}
					}
					if (contactPersonNumber != -1) {
						Cell cell = row.getCell(contactPersonNumber);
						if (cell == null) {
							accProfile.setContactPerson(" ");
						} else {
							String contactPerson = "";

						// celltype=0==>Numeric && celltype=1==>String
						if (row.getCell(contactPersonNumber).getCellType() == 0) {
							contactPerson = String.valueOf(row.getCell(contactPersonNumber).getNumericCellValue());
						}

						if (row.getCell(contactPersonNumber).getCellType() == 1) {
							contactPerson = String.valueOf(row.getCell(contactPersonNumber).getStringCellValue());
						}
							accProfile.setContactPerson(contactPerson);
						}
					}
					if (accountTypeNumber != -1) {
						Cell cell = row.getCell(accountTypeNumber);
						if (cell == null) {
							accProfile.setAccountType(deafultAccountType);
						} else {
							String accTypeName = row.getCell(accountTypeNumber).getStringCellValue();
							Optional<AccountType> opAccountType = accountTypeDTOs.stream()
									.filter(at -> at.getName().equals(accTypeName)).findAny();
							if (opAccountType.isPresent()) {
								accProfile.setAccountType(opAccountType.get());
							} else {
								accProfile.setAccountType(deafultAccountType);
							}
						}
					}
					if (aliasNumber != -1) {
						Cell cell = row.getCell(aliasNumber);
						if (cell == null) {
							accProfile.setAlias(" ");
						} else {
							String alias = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(aliasNumber).getCellType() == 0) {
								alias = String.valueOf(row.getCell(aliasNumber).getNumericCellValue());
							}

							if (row.getCell(aliasNumber).getCellType() == 1) {
								alias = String.valueOf(row.getCell(aliasNumber).getStringCellValue());
							}

							accProfile.setAlias(alias);
						}
					}
					if (closingBalanceNumber != -1) {
						Cell cell = row.getCell(closingBalanceNumber);
						if (cell == null) {
							accProfile.setClosingBalance(0);
						} else {
							String closeBal = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(closingBalanceNumber).getCellType() == 0) {
								closeBal = String.valueOf(row.getCell(closingBalanceNumber).getNumericCellValue());
							}

							if (row.getCell(closingBalanceNumber).getCellType() == 1) {
								closeBal = String.valueOf(row.getCell(closingBalanceNumber).getStringCellValue());
							}

							double closingBalance = Double.valueOf(closeBal);

							accProfile.setClosingBalance(closingBalance);
						}
					}
					if (creditDaysNumber != -1) {
						Cell cell = row.getCell(creditDaysNumber);
						if (cell == null) {
							accProfile.setCreditDays(0L);
						} else {
							Long creditdays = null;
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(creditDaysNumber).getCellType() == 0) {
								creditdays = (long) row.getCell(creditDaysNumber).getNumericCellValue();
							}

							if (row.getCell(creditDaysNumber).getCellType() == 1) {
								creditdays = Long.valueOf(row.getCell(creditDaysNumber).getStringCellValue());
							}

							Long creditDays = creditdays;
							accProfile.setCreditDays(creditDays);
						}
					}
					if (creditLimitNumber != -1) {
						Cell cell = row.getCell(creditLimitNumber);
						if (cell == null) {
							accProfile.setCreditLimit(0L);
						} else {
							Long creditlimit = null;
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(creditLimitNumber).getCellType() == 0) {
								creditlimit = (long) row.getCell(creditLimitNumber).getNumericCellValue();
							}

							if (row.getCell(creditLimitNumber).getCellType() == 1) {
								creditlimit = Long.valueOf(row.getCell(creditLimitNumber).getStringCellValue());
							}

							Long creditLimit = creditlimit;
							accProfile.setCreditLimit(creditLimit);
						}
					}
					if (tinNoNumber != -1) {
						Cell cell = row.getCell(tinNoNumber);
						if (cell == null) {
							accProfile.setTinNo(" ");
						} else {
							String tin = row.getCell(tinNoNumber).getStringCellValue();

							accProfile.setTinNo(tin);
						}
					}

					if (customerIdNumber == -1) {
						accProfile.setCustomerId("No value");
					} else {
						Cell cell = row.getCell(customerIdNumber);
						if (cell == null) {
							accProfile.setCustomerId(" ");
						}
						String customersid = "";
//						 celltype=0==>Numeric && celltype=1==>String
						if (row.getCell(customerIdNumber).getCellType() == 0) {
							customersid = String.valueOf(row.getCell(customerIdNumber).getNumericCellValue());
							accProfile.setCustomerId(customersid);
						}

						if (row.getCell(customerIdNumber).getCellType() == 1) {
							customersid = String.valueOf(row.getCell(customerIdNumber).getStringCellValue());
//							String s1 = customersid.substring(0, customersid.indexOf("."));
//							s1.trim();
//							accProfile.setCustomerId(s1);
						}
						accProfile.setCustomerId(customersid);
					}
					if (customerCodeNumber != -1) {
						Cell cell = row.getCell(customerCodeNumber);
						if (cell == null) {
							accProfile.setCustomerCode(" ");
						} else {
							String customerscode = "";
//							 celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(customerCodeNumber).getCellType() == 0) {
								customerscode = String.valueOf(row.getCell(customerCodeNumber).getNumericCellValue());
							}
							if (row.getCell(customerCodeNumber).getCellType() == 1) {
								customerscode = row.getCell(customerCodeNumber).getStringCellValue();
							}
							accProfile.setCustomerCode(customerscode);
						}
					}
					if (priceLevelNumber != -1) {
						Cell cell = row.getCell(priceLevelNumber);
						if (cell == null) {
							accProfile.setDefaultPriceLevel(null);
						} else {
							String priceLevelName = row.getCell(priceLevelNumber).getStringCellValue();
							Optional<PriceLevel> opPriceLevel = priceLevelDTOs.stream()
									.filter(pl -> pl.getName().equals(priceLevelName)).findAny();
							if (opPriceLevel.isPresent()) {
								accProfile.setDefaultPriceLevel(opPriceLevel.get());
							}
						}
					}
					if (countryNumber != -1) {
						Cell cell = row.getCell(countryNumber);
						if (cell == null) {
							accProfile.setCountryc(null);
						} else {
							String countryName = row.getCell(countryNumber).getStringCellValue();
							Optional<CountryC> opCountry = countrycDTOS.stream()
									.filter(ps -> ps.getName().equals(countryName)).findAny();
							if (opCountry.isPresent()) {
								accProfile.setCountryc(opCountry.get());
							}
						}
					}
					if (stateNumber != -1) {
						Cell cell = row.getCell(stateNumber);
						if (cell == null) {
							accProfile.setStatec(null);
						} else {
							String stateName = row.getCell(stateNumber).getStringCellValue();
							Optional<StateC> opState = statecDTOs.stream().filter(st -> st.getName().equals(stateName))
									.findAny();
							if (opState.isPresent()) {
								accProfile.setStatec(opState.get());
							}
						}
					}
					if (districtNumber != -1) {
						Cell cell = row.getCell(districtNumber);
						if (cell == null) {
							accProfile.setDistrictc(null);
						} else {
							String districtName = row.getCell(districtNumber).getStringCellValue();
							Optional<DistrictC> opDistrict = districtcDTOs.stream()
									.filter(dt -> dt.getName().equals(districtName)).findAny();
							if (opDistrict.isPresent()) {
								accProfile.setDistrictc(opDistrict.get());
							}
						}
					}
					if (saveUpdateAccountProfiles.stream().filter(acp -> acp.getName().equals(accountProofileName))
							.findAny().isPresent()) {
						duplicateCount++;
						System.out.println("Duplicate Account: ------- " + accountProofileName);
						continue;
					}

					saveUpdateAccountProfiles.add(accProfile);

				}
			}
			System.out.println("Duplicate Accounts Size: " + duplicateCount + "......");
			log.info("Saving AccountProfile Size: " + saveUpdateAccountProfiles.size() + ".....");
			bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);
			log.info("Saving AccountProfile Success..... ");

			List<AccountProfile> newAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
			Set<LocationAccountProfile> newLocationAccountProfiles = new HashSet<>();
			log.info("Saving LocationAccountProfile..");

			List<String> accountNames = new ArrayList<>();
			duplicateCount = 0;

			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;

					String accountProofileName = row.getCell(NameNumber).getStringCellValue();
					if (accountProofileName == null || accountProofileName.isEmpty()) {
						break;
					}

					if (territoryNumber != -1) {

						String territoryName = row.getCell(territoryNumber).getStringCellValue();
						Optional<Location> oplocation = locations.stream()
								.filter(l -> l.getName().equals(territoryName)).findAny();
						if (oplocation.isPresent()) {

							if (accountNames.stream().filter(an -> an.equals(accountProofileName)).findAny()
									.isPresent()) {
								duplicateCount++;
								System.out.println("Duplicate Account: ------- " + accountProofileName);
								continue;
							}

							accountNames.add(accountProofileName);

							for (AccountProfile accProfile : newAccountProfiles) {

								if (accProfile.getName().equalsIgnoreCase(accountProofileName)) {
									List<LocationAccountProfile> locAccProfiles = locationAccountProfiles.stream()
											.filter(lap -> lap.getAccountProfile().getPid().equals(accProfile.getPid())
													&& lap.getLocation().getPid().equals(oplocation.get().getPid()))
											.collect(Collectors.toList());

									if (locAccProfiles.isEmpty()) {
										LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
										locationAccountProfile.setAccountProfile(accProfile);
										locationAccountProfile.setLocation(oplocation.get());
										locationAccountProfile.setCompany(company);

										newLocationAccountProfiles.add(locationAccountProfile);

									}
								}
							}
						}

					}
				}
			}
			System.out.println("Duplicate Accounts(LocationAccountProfile) Size: " + duplicateCount + "...");
			accountNames.removeAll(accountNames);
			log.info("Saving LocationAccountProfile Size " + newLocationAccountProfiles.size() + "....");
			List<String> accountProfilePids = newLocationAccountProfiles.stream()
					.map(lap -> lap.getAccountProfile().getPid()).collect(Collectors.toList());
			locationAccountProfileRepository.deleteByCompanyIdAndAccountProfilePidIn(
					SecurityUtils.getCurrentUsersCompanyId(), accountProfilePids);
			locationAccountProfileRepository.save(newLocationAccountProfiles);
			log.info("Saving LocationAccountProfile Success......");
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info("Excel Account Profile Uploaded successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/upload-xls/saveProductXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProductProfiles(MultipartHttpServletRequest request) {
		log.info("Request to Upload excel productProfile..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] productsNumbers = request.getParameterValues("productNumbers");
		String[] allProductNumbers = productsNumbers[0].split(",");

		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(companyPid[0]);
		List<Division> divisions = divisionRepository.findAllByCompanyPid(companyPid[0]);
		Company company = companyRepository.findOneByPid(companyPid[0]).get();
		List<Units> units = unitsRepository.findAllUnits();
		Set<ProductProfile> saveUpdateProductProfiles = new HashSet<>();

		List<ProductGroup> productGroups = productGroupRepository.findByCompanyId(company.getId());

		List<ProductProfile> existingProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());

		int duplicateCount = 0;
		try {
			Iterator<String> itrator = request.getFileNames();
			MultipartFile multipartFile = request.getFile(itrator.next());

			int NameNumber = Integer.parseInt(allProductNumbers[0]);
			int aliasNumber = Integer.parseInt(allProductNumbers[1]);
			int descriptionNumber = Integer.parseInt(allProductNumbers[2]);
			int priceNumber = Integer.parseInt(allProductNumbers[3]);
			int skuNumber = Integer.parseInt(allProductNumbers[4]);
			int unitQuantityNumber = Integer.parseInt(allProductNumbers[5]);
			int TaxRateNumber = Integer.parseInt(allProductNumbers[6]);
			int sizeNumber = Integer.parseInt(allProductNumbers[7]);
			int openingStockNumber = Integer.parseInt(allProductNumbers[8]);
			int productGroupNumber = Integer.parseInt(allProductNumbers[9]);
			int mrpNumber = Integer.parseInt(allProductNumbers[10]);
			int productCategoryNumber = Integer.parseInt(allProductNumbers[11]);
			int hsnNumber = Integer.parseInt(allProductNumbers[12]);
			int productIdNumber = Integer.parseInt(allProductNumbers[13]);
			int compoundUnitNumber = Integer.parseInt(allProductNumbers[14]);
			int productCodeNumber = Integer.parseInt(allProductNumbers[15]);
			int unitsNumber = Integer.parseInt(allProductNumbers[16]);

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {
				rowNumber = row.getRowNum();

				if (rowNumber > 0) {

					rowNumber++;
					String productProfileName = "";

					if (row.getCell(NameNumber).getCellType() == 0) {

						productProfileName = String.valueOf((long) row.getCell(NameNumber).getNumericCellValue());

					}

					if (row.getCell(NameNumber).getCellType() == 1) {
						productProfileName = String.valueOf(row.getCell(NameNumber).getStringCellValue());

					}

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

					String ppName = productProfileName;
					Optional<ProductProfile> productProfileDTO = existingProductProfiles.stream()
							.filter(pp -> pp.getName().equals(ppName)).findAny();

					// Optional<ProductProfile> productProfileDTO = productProfileRepository
					// .findByCompanyIdAndNameIgnoreCase(company.getId(), productProfileName);

					ProductProfile productProfile = new ProductProfile();
					if (productProfileDTO.isPresent()) {
						productProfile = productProfileDTO.get();
					} else {
						productProfile = new ProductProfile();
						productProfile.setName(productProfileName);
						productProfile.setDivision(divisions.get(0));
						productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
						productProfile.setProductCategory(productCategories.get(0));
						productProfile.setCompany(company);
					}

					if (aliasNumber != -1) {
						Cell cell = row.getCell(aliasNumber);
						if (cell == null) {
							productProfile.setAlias(" ");
						} else {
							String alias = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(aliasNumber).getCellType() == 0) {
								alias = String.valueOf(row.getCell(aliasNumber).getNumericCellValue());
							}

							if (row.getCell(aliasNumber).getCellType() == 1) {
								alias = String.valueOf(row.getCell(aliasNumber).getStringCellValue());
							}

							productProfile.setAlias(alias);
						}
					}
					if (descriptionNumber != -1) {
						Cell cell = row.getCell(descriptionNumber);
						if (cell == null) {
							productProfile.setDescription(" ");
						} else {
							String description = "";

							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(descriptionNumber).getCellType() == 0) {
								description = String.valueOf(row.getCell(descriptionNumber).getNumericCellValue());
							}

							if (row.getCell(descriptionNumber).getCellType() == 1) {
								description = String.valueOf(row.getCell(descriptionNumber).getStringCellValue());
							}

							productProfile.setDescription(description);
						}
					}
					if (priceNumber != -1) {
						Cell cell = row.getCell(priceNumber);
						if (cell == null) {
							productProfile.setPrice(BigDecimal.valueOf(0));
						}

						else {
							productProfile.setPrice(BigDecimal.valueOf(row.getCell(priceNumber).getNumericCellValue()));
						}
					}
					if (skuNumber != -1) {
						Cell cell = row.getCell(skuNumber);
						if (cell == null) {
							productProfile.setSku(" ");
						} else {
							productProfile.setSku(row.getCell(skuNumber).getStringCellValue());
						}
					}
					if (unitQuantityNumber != -1) {
						Cell cell = row.getCell(unitQuantityNumber);
						if (cell == null) {
							productProfile.setUnitQty(0.0);
						} else {
							String unitQty = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(unitQuantityNumber).getCellType() == 0) {
								unitQty = String.valueOf(row.getCell(unitQuantityNumber).getNumericCellValue());
							}

							if (row.getCell(unitQuantityNumber).getCellType() == 1) {
								unitQty = String.valueOf(row.getCell(unitQuantityNumber).getStringCellValue());
							}
							double unitQuantity = 0.0;

							unitQuantity = Double.parseDouble(unitQty);

							productProfile.setUnitQty(unitQuantity);
						}
					}
					if (TaxRateNumber != -1) {
						Cell cell = row.getCell(TaxRateNumber);
						if (cell == null) {
							productProfile.setTaxRate(0.0);
						} else {
							productProfile.setTaxRate(row.getCell(TaxRateNumber).getNumericCellValue());
						}
					}
					if (sizeNumber != -1) {
						Cell cell = row.getCell(sizeNumber);
						if (cell == null) {
							productProfile.setSize(" ");
						} else {
							productProfile.setSize(String.valueOf(row.getCell(sizeNumber).getNumericCellValue()));
						}
					}

					if (mrpNumber != -1) {
						Cell cell = row.getCell(mrpNumber);
						if (cell == null) {
							productProfile.setMrp(0.0);
						} else {
							String mrp = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(mrpNumber).getCellType() == 0) {
								mrp = String.valueOf(row.getCell(mrpNumber).getNumericCellValue());
							}

							if (row.getCell(mrpNumber).getCellType() == 1) {
								mrp = String.valueOf(row.getCell(mrpNumber).getStringCellValue());
							}
							Double mRP = Double.parseDouble(mrp);
							productProfile.setMrp(mRP);
						}
					}
					if (productCategoryNumber != -1) {
						Cell cell = row.getCell(productCategoryNumber);
						if (cell == null) {
							productProfile.setProductCategory(null);
						} else {
							String productCategoryName = row.getCell(productCategoryNumber).getStringCellValue();
							Optional<ProductCategory> opProductCategory = productCategories.stream()
									.filter(pc -> pc.getName().equals(productCategoryName)).findAny();
							if (opProductCategory.isPresent()) {
								productProfile.setProductCategory(opProductCategory.get());
							}
						}
					}
					if (hsnNumber != -1) {
						Cell cell = row.getCell(hsnNumber);
						if (cell == null) {
							productProfile.setHsnCode(" ");
						} else {
							String HsnCode = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(hsnNumber).getCellType() == 0) {
								HsnCode = String.valueOf(row.getCell(hsnNumber).getNumericCellValue());
							}

							if (row.getCell(hsnNumber).getCellType() == 1) {
								HsnCode = String.valueOf(row.getCell(hsnNumber).getStringCellValue());
							}

							productProfile.setHsnCode(HsnCode);
						}
					}
					if (productIdNumber != -1) {
						Cell cell = row.getCell(productIdNumber);
						if (cell == null) {
							productProfile.setProductId(" ");
						} else {
							String proId = String.valueOf(row.getCell(productIdNumber).getNumericCellValue());

							String s1 = proId.substring(0, proId.indexOf("."));
							s1.trim();
							productProfile.setProductId(s1);
						}
					}
					if (productCodeNumber != -1) {
						Cell cell = row.getCell(productCodeNumber);
						if (cell == null) {
							productProfile.setProductCode(" ");
						} else {
							productProfile.setProductCode(
									String.valueOf(row.getCell(productCodeNumber).getNumericCellValue()));
						}
					}
					if (compoundUnitNumber != -1) {
						Cell cell = row.getCell(compoundUnitNumber);
						if (cell == null) {
							productProfile.setCompoundUnitQty(0.0);
						} else {
							String compoundUnit = "";
							// celltype=0==>Numeric && celltype=1==>String
							if (row.getCell(compoundUnitNumber).getCellType() == 0) {
								compoundUnit = String.valueOf(row.getCell(compoundUnitNumber).getNumericCellValue());
							}

							if (row.getCell(compoundUnitNumber).getCellType() == 1) {
								compoundUnit = String.valueOf(row.getCell(compoundUnitNumber).getStringCellValue());
							}
							Double cUNIT = Double.parseDouble(compoundUnit);
							productProfile.setCompoundUnitQty(cUNIT);
						}
					}
					if (unitsNumber != -1) {
						Cell cell = row.getCell(unitsNumber);
						if (cell == null) {
							productProfile.setUnits(null);
						} else {

							String unitsName = row.getCell(unitsNumber).getStringCellValue();
							Units unit = new Units();
							Optional<Units> unitname = units.stream().filter(st -> st.getName().equals(unitsName))
									.findAny();
							if (unitname.isPresent()) {
								unit.setName(unitsName);
								productProfile.setUnits(unitname.get());

							}
						}
					}

					if (saveUpdateProductProfiles.stream().filter(pp -> pp.getName().equals(ppName)).findAny()
							.isPresent()) {
						duplicateCount++;
						System.out.println("Duplicate Product: ------- " + ppName);
						continue;
					}

					saveUpdateProductProfiles.add(productProfile);
				}
			}
			System.out.println("Duplicate Product Profile Size: " + duplicateCount + ".....");
			duplicateCount = 0;
			log.info("Saving ProducProfile Size: " + saveUpdateProductProfiles.size() + ".....");
			bulkOperationRepositoryCustom.bulkSaveProductProfile(saveUpdateProductProfiles);
			log.info("Saving ProductProfile Success.....");

			List<ProductProfile> newProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
			System.out.println(newProductProfiles.size() + "-----------");
			List<ProductGroupProduct> productGroupProducts = productGroupProductRepository
					.findAllByCompanyPid(company.getPid());
			Set<ProductGroupProduct> newProductGroupProducts = new HashSet<>();
			log.info("Saving ProductGroupProfile...");

			List<String> productNames = new ArrayList<>();

			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;

					String productProfileName = "";

					if (row.getCell(NameNumber).getCellType() == 0) {

						productProfileName = String.valueOf((long) row.getCell(NameNumber).getNumericCellValue());

					}

					if (row.getCell(NameNumber).getCellType() == 1) {
						productProfileName = String.valueOf(row.getCell(NameNumber).getStringCellValue());
					}

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

					String ppName = productProfileName;

					if (productGroupNumber != -1) {
						
						String productGroupName = row.getCell(productGroupNumber).getStringCellValue();

						Optional<ProductGroup> opProductGroup = productGroups.stream()
								.filter(pg -> pg.getName().equals(productGroupName)).findAny();

						if (opProductGroup.isPresent()) {

							if (productNames.stream().filter(pn -> pn.equals(ppName)).findAny().isPresent()) {
								duplicateCount++;
								System.out.println("Duplicate Product: ------- " + productProfileName);
								continue;
							}

							productNames.add(productProfileName);

							for (ProductProfile productProfile : newProductProfiles) {
 
								if (productProfile.getName().equalsIgnoreCase(productProfileName)) {
									List<ProductGroupProduct> pgpProfiles = productGroupProducts.stream().filter(
											pgp -> pgp.getProduct().getPid().equals(productProfile.getPid()) && pgp
													.getProductGroup().getPid().equals(opProductGroup.get().getPid()))
											.collect(Collectors.toList());

									if (pgpProfiles.isEmpty()) {
										ProductGroupProduct productGroupProduct = new ProductGroupProduct();
										productGroupProduct.setCompany(company);
										productGroupProduct.setProduct(productProfile);
										productGroupProduct.setProductGroup(opProductGroup.get());
										productGroupProduct.setActivated(true);
										newProductGroupProducts.add(productGroupProduct);

									}
								}
							}

						}

					}

				}
			}
			System.out.println("Duplicate Product Profile(ProducGroupProduct) Size: " + duplicateCount + ".....");
			duplicateCount = 0;
			productNames.removeAll(productNames);

			log.info("Saving ProductGroupProductProfile Size " + newProductGroupProducts.size() + "....");
			bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(newProductGroupProducts);
			log.info("Saving ProductGroupProductProfile Success......");

			StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
			// find all exist product profiles

			long stockLocationId = 0L;

			if (defaultStockLocation != null) {
				if (defaultStockLocation.getName().equalsIgnoreCase("Main Location")) {
					stockLocationId = defaultStockLocation.getId();
				}
			}

			if (stockLocationId != 0) {
				openingStockRepository.deleteByStockLocationIdAndCompanyId(stockLocationId, company.getId());
			}

			log.info("Saving OpeningStock...");
			Set<OpeningStock> saveOpeningStocks = new HashSet<>();
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;

					String productProfileName = "";

					if (row.getCell(NameNumber).getCellType() == 0) {

						productProfileName = String.valueOf((long) row.getCell(NameNumber).getNumericCellValue());

					}

					if (row.getCell(NameNumber).getCellType() == 1) {
						productProfileName = String.valueOf(row.getCell(NameNumber).getStringCellValue());
					}

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

					String ppName = productProfileName;

					if (openingStockNumber != -1) {
						// String openingStockValue =
						// row.getCell(openingStockNumber).getStringCellValue();

						String openingStockValue = "";
						// celltype=0==>Numeric && celltype=1==>String
						if (row.getCell(openingStockNumber).getCellType() == 0) {
							openingStockValue = String.valueOf(row.getCell(openingStockNumber).getNumericCellValue());
						}

						if (row.getCell(openingStockNumber).getCellType() == 1) {
							openingStockValue = String.valueOf(row.getCell(openingStockNumber).getStringCellValue());
						}

						if (productNames.stream().filter(pn -> pn.equals(ppName)).findAny().isPresent()) {
							duplicateCount++;
							System.out.println("Duplicate Product: ------- " + productProfileName);
							continue;
						}

						productNames.add(productProfileName);

						for (ProductProfile productProfile : newProductProfiles) {

							if (productProfile.getName().equalsIgnoreCase(productProfileName)) {

								OpeningStock openingStock = new OpeningStock();
								openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
								openingStock.setOpeningStockDate(LocalDateTime.now());
								openingStock.setCreatedDate(LocalDateTime.now());
								openingStock.setCompany(company);
								openingStock.setProductProfile(productProfile);
								openingStock.setStockLocation(defaultStockLocation);

								openingStock.setQuantity(Double.parseDouble(openingStockValue));

								saveOpeningStocks.add(openingStock);

							}
						}
					}

				}
			}
			System.out.println("Duplicate Product Profile(Opening Stock) Size: " + duplicateCount + ".....");
			productNames.removeAll(productNames);
			log.info("Saving OpeningStock Size " + saveOpeningStocks.size() + "....");
			bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
			log.info("Saving OpeningStock Sucess....");
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		log.info("Excel Product Profile Uploaded successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-xls/saveInvoiceXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedInvoiceDetails(MultipartHttpServletRequest request) {
		log.info("Request to Upload excel invoice Details..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] invoiceNumbers = request.getParameterValues("invoiceNumbers");
		String[] allInvoiceNumbers = invoiceNumbers[0].split(",");

		Company company = companyRepository.findOneByPid(companyPid[0]).get();

		try {
			Iterator<String> itrator = request.getFileNames();
			MultipartFile multipartFile = request.getFile(itrator.next());

			int NameNumber = Integer.parseInt(allInvoiceNumbers[0]);
			int idNumber = Integer.parseInt(allInvoiceNumbers[1]);

			int docNoNumber = Integer.parseInt(allInvoiceNumbers[2]);
			int docDateNumber = Integer.parseInt(allInvoiceNumbers[3]);
			int docAmountNumber = Integer.parseInt(allInvoiceNumbers[4]);
			int balanceAmountNumber = Integer.parseInt(allInvoiceNumbers[5]);

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			List<String> cuIds = new ArrayList<>();

			for (Row row : sheet) {

				rowNumber = row.getRowNum();
				String customerId = null;
				String s1 = null;
				if (rowNumber > 0) {
					rowNumber++;

					if (idNumber != -1) {

						customerId = String.valueOf(row.getCell(idNumber).getNumericCellValue());
						s1 = customerId.substring(0, customerId.indexOf("."));
						s1.trim();

					}
				}
				cuIds.add(s1);
			}

			receivablePayableRepository.deleteByCompanyId(company.getId());
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAccountProfilesByCompanyIdAndCustomerIdIn(company.getId(), cuIds);
			Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
			for (Row row : sheet) {

				rowNumber = row.getRowNum();

				if (rowNumber > 0) {

					rowNumber++;
					String accountProfileName = row.getCell(NameNumber).getStringCellValue();

					Optional<AccountProfile> accProfile = accountProfiles.stream()
							.filter(a -> a.getName().equalsIgnoreCase(accountProfileName)).findAny();

					if (accProfile.isPresent()) {

						ReceivablePayable receivablePayable = new ReceivablePayable();
						receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
						receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						receivablePayable.setCompany(company);
						receivablePayable.setBillOverDue(null);
						receivablePayable.setAccountProfile(accProfile.get());

						if (docNoNumber != -1) {
							Cell cell = row.getCell(docNoNumber);
							if (cell == null) {
								receivablePayable.setReferenceDocumentNumber(" ");
							} else {
								String docNumber = row.getCell(docNoNumber).getStringCellValue();
								receivablePayable.setReferenceDocumentNumber(docNumber);
							}
						}

						if (docDateNumber != -1) {
							Cell cell = row.getCell(docDateNumber);
							if (cell == null) {
								receivablePayable.setReferenceDocumentDate(null);
							} else {
								Date documentDate = row.getCell(docDateNumber).getDateCellValue();
								LocalDate docDate = documentDate.toInstant().atZone(ZoneId.systemDefault())
										.toLocalDate();

								receivablePayable.setReferenceDocumentDate(docDate);
							}
						}
						if (docAmountNumber != -1) {
							Cell cell = row.getCell(docAmountNumber);
							if (cell == null) {
								receivablePayable.setReferenceDocumentAmount(0.0);
							} else {
								String docAmount = "";

								docAmount = String.valueOf(row.getCell(docAmountNumber).getNumericCellValue());
								Double docamt = Double.parseDouble(docAmount);
								receivablePayable.setReferenceDocumentAmount(docamt);
							}
						}
						if (balanceAmountNumber != -1) {
							Cell cell = row.getCell(balanceAmountNumber);
							if (cell == null) {
								receivablePayable.setReferenceDocumentBalanceAmount(0.0);
							} else {
								String balAmount = "";

								balAmount = String.valueOf(row.getCell(balanceAmountNumber).getNumericCellValue());
								Double balamt = Double.parseDouble(balAmount);
								receivablePayable.setReferenceDocumentBalanceAmount(balamt);
							}
						}
						saveReceivablePayable.add(receivablePayable);

					}

				}

			}
			log.info("Successfully saved Invoice Details");

			bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		log.info("Excel Invoice Details Uploaded successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
