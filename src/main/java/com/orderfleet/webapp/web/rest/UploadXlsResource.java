package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.ProductProfileService;
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

	@RequestMapping(value = "/upload-xls", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetGroups");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/uploadXls";
	}

	@RequestMapping(value = "/upload-xls/saveAccountXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(MultipartHttpServletRequest request) {

		log.info("Request to Upload excel accountProfile..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] accountsNumbers = request.getParameterValues("accountNumbers");
		String[] allAccountNumbers = accountsNumbers[0].split(",");

		List<AccountType> accountTypeDTOs = accountTypeRepository.findAllByCompanyPid(companyPid[0]);
		List<Location> locations = locationRepository
				.findAllByCompanyPidAndLocationActivatedOrDeactivated(companyPid[0], Boolean.TRUE);
		if (accountTypeDTOs.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		AccountType deafultAccountType = accountTypeDTOs.get(0);
		Company company = deafultAccountType.getCompany();

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		User user = userRepository.findTop1ByCompanyId(company.getId());

		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
				.findAllByCompanyId(company.getId());
		List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());

		try {

			Iterator<String> itrator = request.getFileNames();
			MultipartFile multipartFile = request.getFile(itrator.next());
			System.out.println(allAccountNumbers.toString());
			int NameNumber = Integer.parseInt(allAccountNumbers[0]);
			int addressNumber = Integer.parseInt(allAccountNumbers[1]);
			int cityNumber = Integer.parseInt(allAccountNumbers[2]);
			int locationNumber = Integer.parseInt(allAccountNumbers[3]);
			int pinNumber = Integer.parseInt(allAccountNumbers[4]);
			int phoneNumber = Integer.parseInt(allAccountNumbers[5]);
			int eMailNumber = Integer.parseInt(allAccountNumbers[6]);
			int descriptionNumber = Integer.parseInt(allAccountNumbers[7]);
			int contactPersonNumber = Integer.parseInt(allAccountNumbers[8]);
			int accountTypeNumber = Integer.parseInt(allAccountNumbers[9]);
			int territoryNumber = Integer.parseInt(allAccountNumbers[10]);
			int aliasNumber = Integer.parseInt(allAccountNumbers[11]);
			int closingBalanceNumber = Integer.parseInt(allAccountNumbers[12]);

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
						accProfile.setAccountType(deafultAccountType);
						accProfile.setUser(user);
						accProfile.setName(accountProofileName);
					}

					if (addressNumber == -1) {
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

						if (address == null || address.isEmpty()) {
							accProfile.setAddress("No Address");
						} else {
							accProfile.setAddress(address);
						}
					}
					if (cityNumber == -1) {
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

						if (city == null || city.isEmpty()) {
							accProfile.setCity("No city");
						} else {
							accProfile.setCity(city);
						}

					}
					if (locationNumber != -1) {
						accProfile.setLocation(row.getCell(locationNumber).getStringCellValue());
					}
					if (pinNumber != -1) {
						String pin = formatter.formatCellValue(row.getCell(pinNumber));
						accProfile.setPin(pin);
					}
					if (phoneNumber != -1) {
						String phones = formatter.formatCellValue(row.getCell(phoneNumber));
						String[] phoneNums = phones.split("\n|\\/");
						accProfile.setPhone1(phoneNums[0]);
						if (phoneNums.length > 1) {
							accProfile.setPhone2(phoneNums[1]);
						}
					}
					if (eMailNumber != -1) {
						accProfile.setEmail1(row.getCell(eMailNumber).getStringCellValue());
					}
					if (descriptionNumber != -1) {
						accProfile.setDescription(row.getCell(descriptionNumber).getStringCellValue());
					}
					if (contactPersonNumber != -1) {
						accProfile.setContactPerson(row.getCell(contactPersonNumber).getStringCellValue());
					}
					if (accountTypeNumber != -1) {
						String accTypeName = row.getCell(accountTypeNumber).getStringCellValue();
						Optional<AccountType> opAccountType = accountTypeDTOs.stream()
								.filter(at -> at.getName().equals(accTypeName)).findAny();
						if (opAccountType.isPresent()) {
							accProfile.setAccountType(opAccountType.get());
						}
					}
					if (aliasNumber != -1) {

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
					if (closingBalanceNumber != -1) {

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

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					String productProfileName = row.getCell(NameNumber).getStringCellValue();

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

					Optional<ProductProfile> productProfileDTO = existingProductProfiles.stream()
							.filter(pp -> pp.getName().equals(productProfileName)).findAny();

					// Optional<ProductProfile> productProfileDTO = productProfileRepository
					// .findByCompanyIdAndNameIgnoreCase(company.getId(), productProfileName);

					ProductProfile productProfile = new ProductProfile();
					if (productProfileDTO.isPresent()) {
						productProfile = productProfileDTO.get();
					} else {
						productProfile = new ProductProfile();
						productProfile.setName(productProfileName);
						productProfile.setProductCategory(productCategories.get(0));
						productProfile.setDivision(divisions.get(0));
						productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
						productProfile.setCompany(company);
					}

					if (aliasNumber != -1) {

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
					if (descriptionNumber != -1) {
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
					if (priceNumber == -1) {
						productProfile.setPrice(BigDecimal.valueOf(0));
					} else {
						productProfile.setPrice(BigDecimal.valueOf(row.getCell(priceNumber).getNumericCellValue()));
					}
					if (skuNumber != -1) {
						productProfile.setSku(row.getCell(skuNumber).getStringCellValue());
					}
					if (unitQuantityNumber != -1) {

						String unitQty = "";
						// celltype=0==>Numeric && celltype=1==>String
						if (row.getCell(unitQuantityNumber).getCellType() == 0) {
							unitQty = String.valueOf(row.getCell(unitQuantityNumber).getNumericCellValue());
						}

						if (row.getCell(unitQuantityNumber).getCellType() == 1) {
							unitQty = String.valueOf(row.getCell(unitQuantityNumber).getStringCellValue());
						}
						double unitQuantity = Double.parseDouble(unitQty);
						if (unitQty == null || unitQty.isEmpty()) {
							unitQuantity = 0.0;
						}
						productProfile.setUnitQty(unitQuantity);
					}
					if (TaxRateNumber != -1) {
						productProfile.setTaxRate(row.getCell(TaxRateNumber).getNumericCellValue());
					}
					if (sizeNumber != -1) {
						productProfile.setSize(String.valueOf(row.getCell(sizeNumber).getNumericCellValue()));
					}

					if (saveUpdateProductProfiles.stream().filter(pp -> pp.getName().equals(productProfileName))
							.findAny().isPresent()) {
						duplicateCount++;
						System.out.println("Duplicate Product: ------- " + productProfileName);
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

					String productProfileName = row.getCell(NameNumber).getStringCellValue();

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

					if (productGroupNumber != -1) {
						String productGroupName = row.getCell(productGroupNumber).getStringCellValue();
						Optional<ProductGroup> opProductGroup = productGroups.stream()
								.filter(pg -> pg.getName().equals(productGroupName)).findAny();
						if (opProductGroup.isPresent()) {

							if (productNames.stream().filter(pn -> pn.equals(productProfileName)).findAny()
									.isPresent()) {
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

					String productProfileName = row.getCell(NameNumber).getStringCellValue();

					if (productProfileName == null || productProfileName.isEmpty()) {
						break;
					}

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

						if (productNames.stream().filter(pn -> pn.equals(productProfileName)).findAny().isPresent()) {
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

}
