package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
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
public class UploadXlsUpdateNameResource {

	private final Logger log = LoggerFactory.getLogger(UploadXlsUpdateNameResource.class);

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
	private DivisionRepository divisionRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@RequestMapping(value = "/upload-xls-update-name", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetGroups");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/uploadXlsUploadName";
	}

	@RequestMapping(value = "/upload-xls-update-name/saveAccountXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(MultipartHttpServletRequest request) {
		log.info("accounts request to get successfully..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] accountsNumbers = request.getParameterValues("accountNumbers");
		String[] allAccountNumbers = accountsNumbers[0].split(",");

		List<AccountType> accountTypeDTOs = accountTypeRepository.findAllByCompanyPid(companyPid[0]);
		if (accountTypeDTOs.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		AccountType deafultAccountType = accountTypeDTOs.get(0);
		Company company = deafultAccountType.getCompany();
		try {
			Iterator<String> itrator = request.getFileNames();
			MultipartFile multipartFile = request.getFile(itrator.next());
			System.out.println(allAccountNumbers.toString());
			int snrichNameNumber = Integer.parseInt(allAccountNumbers[0]);
			int tallyNameNumber = Integer.parseInt(allAccountNumbers[1]);

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			List<AccountProfile> accountProfileList = accountProfileRepository.findAllByCompanyId(company.getId());
			List<AccountProfile> accProfileList = new ArrayList<>();
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					String snrichAccountProofileName = row.getCell(snrichNameNumber).getStringCellValue();
					String tallyAccountProofileName = row.getCell(tallyNameNumber).getStringCellValue();
					 Optional<AccountProfile> opAccountProfile = accountProfileList.stream().
							filter(ap -> ap.getName().trim().equals(snrichAccountProofileName.trim())).findAny();
					
					Optional<AccountProfile> opAccProfileExist =  accountProfileList.stream()
					 .filter(ap -> ap.getName().trim().equals(tallyAccountProofileName.trim())).findAny();
					
					if (opAccountProfile.isPresent() && !opAccProfileExist.isPresent()) {
						
						AccountProfile accProfile = new AccountProfile();
						accProfile = opAccountProfile.get();
						accProfile.setName(tallyAccountProofileName);
						log.info(tallyAccountProofileName+"--");
						accProfileList.add(accProfile);
					} else {
						log.info(snrichAccountProofileName+" donot exist");
					}
				}
			}
			if(!accProfileList.isEmpty()) {
				log.info("Updated account profile size "+accProfileList.size());
				accountProfileRepository.save(accProfileList);
			}
			
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info("accounts saved successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-xls-update-name/saveProductXls", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProductProfiles(MultipartHttpServletRequest request) {
		log.info("accounts request to get successfully..................");

		String[] companyPid = request.getParameterValues("companyId");
		String[] productsNumbers = request.getParameterValues("productNumbers");
		String[] allProductNumbers = productsNumbers[0].split(",");

		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(companyPid[0]);
		List<Division> divisions = divisionRepository.findAllByCompanyPid(companyPid[0]);
		Company company = companyRepository.findOneByPid(companyPid[0]).get();
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

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					String productProfileName = row.getCell(NameNumber).getStringCellValue();
					Optional<ProductProfile> productProfileDTO = productProfileRepository
							.findByCompanyIdAndNameIgnoreCase(company.getId(), productProfileName);
					if (productProfileDTO.isPresent()) {
						ProductProfile profile = productProfileDTO.get();
						if (aliasNumber != -1) {
							profile.setAlias(row.getCell(aliasNumber).getStringCellValue());
						}
						if (descriptionNumber != -1) {
							profile.setDescription(row.getCell(descriptionNumber).getStringCellValue());
						}
						if (priceNumber != -1) {
							profile.setPrice(BigDecimal.valueOf(row.getCell(priceNumber).getNumericCellValue()));
						}
						if (skuNumber != -1) {
							profile.setSku(row.getCell(skuNumber).getStringCellValue());
						}
						if (unitQuantityNumber != -1) {
							profile.setUnitQty(row.getCell(unitQuantityNumber).getNumericCellValue());
						}
						if (TaxRateNumber != -1) {
							profile.setTaxRate(row.getCell(TaxRateNumber).getNumericCellValue());
						}
						if (sizeNumber != -1) {
							profile.setSize(String.valueOf(row.getCell(sizeNumber).getNumericCellValue()));
						}
						log.debug("Request to update ProductProfile : {}", profile);
						productProfileRepository.save(profile);
					} else {
						ProductProfile productProfile = new ProductProfile();
						productProfile.setName(productProfileName);
						if (aliasNumber != -1) {
							productProfile.setAlias(row.getCell(aliasNumber).getStringCellValue());
						}
						if (descriptionNumber != -1) {
							productProfile.setDescription(row.getCell(descriptionNumber).getStringCellValue());
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
							productProfile.setUnitQty(row.getCell(unitQuantityNumber).getNumericCellValue());
						}
						if (TaxRateNumber != -1) {
							productProfile.setTaxRate(row.getCell(TaxRateNumber).getNumericCellValue());
						}
						if (sizeNumber != -1) {
							productProfile.setSize(String.valueOf(row.getCell(sizeNumber).getNumericCellValue()));
						}

						productProfile.setProductCategory(productCategories.get(0));
						productProfile.setDivision(divisions.get(0));
						log.debug("Request to save ProductProfile : {}", productProfile);
						productProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
						productProfile.setCompany(company);
						productProfileRepository.save(productProfile);
					}
				}
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		log.info("products saved successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
