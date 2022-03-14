package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.orderfleet.webapp.domain.ProductGroupProduct;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DivisionService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductSize;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ProductProfile.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
/**
 *
 * @author Android
 * @since Feb 7, 2017
 */
@Controller
@RequestMapping("/web")
public class ProductProfileResource {

	private final Logger log = LoggerFactory.getLogger(ProductProfileResource.class);

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private DivisionService divisionService;

	@Inject
	private ProductGroupService productGroupService;
	
	@Inject
	private UnitsService unitservise;

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private TaxMasterService taxmasterService;

	/**
	 * POST /productProfiles : Create a new productProfile.
	 *
	 * @param productProfileDTO
	 *            the productProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new productProfileDTO, or with status 400 (Bad Request) if the
	 *         productProfile has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/productProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ProductProfileDTO> createProductProfile(
			@Valid @RequestBody ProductProfileDTO productProfileDTO) throws URISyntaxException {
		log.debug("Web request to save ProductProfile : {}", productProfileDTO);
		log.debug("UNITSID : {}", productProfileDTO.getUnitsPid());
		if (productProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productProfile", "idexists",
					"A new product profile cannot already have an ID")).body(null);
		}
		if (productProfileService.findByName(productProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productProfile", "nameexists", "Product Profile already in use"))
					.body(null);
		}
		productProfileDTO.setActivated(true);
		System.out.println("uId"+ productProfileDTO.getUnitsPid());
		ProductProfileDTO result = productProfileService.save(productProfileDTO);
		return ResponseEntity.created(new URI("/web/productProfiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("productProfile", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /productProfiles : Updates an existing productProfile.
	 *
	 * @param productProfileDTO
	 *            the productProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         productProfileDTO, or with status 400 (Bad Request) if the
	 *         productProfileDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the productProfileDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/productProfiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductProfileDTO> updateProductProfile(
			@Valid @RequestBody ProductProfileDTO productProfileDTO) throws URISyntaxException {
		log.debug("Web request to update ProductProfile : {}", productProfileDTO);
		if (productProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productProfile", "idNotexists", "Product Profile must have an ID"))
					.body(null);
		}
		Optional<ProductProfileDTO> existingProductProfile = productProfileService
				.findByName(productProfileDTO.getName());
		if (existingProductProfile.isPresent()
				&& (!existingProductProfile.get().getPid().equals(productProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productProfile", "nameexists", "Product Profile already in use"))
					.body(null);
		}
		ProductProfileDTO result = productProfileService.update(productProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productProfile", "idNotexists", "Invalid Product Profile ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("productProfile", productProfileDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /productProfiles : get all the productProfiles in ascending order of
	 * name.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/productProfiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductProfiles(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductProfiles");
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("divisions", divisionService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("productProfiles",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(true));
		model.addAttribute("taxMasters", taxmasterService.findAllByCompany());
		model.addAttribute("unitlist", unitservise.findAll());
		model.addAttribute("deactivatedProductProfiles",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(false));
		return "company/productProfiles";
	}

	/**
	 * GET /productProfiles/:id : get the "id" productProfile.
	 *
	 * @param id
	 *            the id of the productProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         productProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/productProfiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductProfileDTO> getProductProfile(@PathVariable String pid) {
		log.debug("Web request to get ProductProfile by pid : {}", pid);
		return productProfileService.findOneByPid(pid)
				.map(productProfileDTO -> new ResponseEntity<>(productProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /productProfiles/:pid : delete the "pid" productProfile.
	 *
	 * @param pid
	 *            the pid of the productProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/productProfiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductProfile(@PathVariable String pid) {
		log.debug("REST request to delete ProductProfile : {}", pid);
		productProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productProfile", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/productProfiles/filterByCategoryGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<ProductProfileDTO>> filterProductProfilesByCategoryAndGroup(
			@RequestParam String categoryPids, @RequestParam String groupPids) throws URISyntaxException {
		List<ProductProfileDTO> productProfiles = new ArrayList<>();
		// none selected
		if (categoryPids.isEmpty() && groupPids.isEmpty()) {
			productProfiles.addAll(productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(true));
			return new ResponseEntity<>(productProfiles, HttpStatus.OK);
		}
		// both selected
		if (!categoryPids.isEmpty() && !groupPids.isEmpty()) {
			productProfiles
					.addAll(productGroupProductService.findByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
							Arrays.asList(groupPids.split(",")), Arrays.asList(categoryPids.split(","))));
			return new ResponseEntity<>(productProfiles, HttpStatus.OK);

		}
		// category selected correct
		if (!categoryPids.isEmpty() && groupPids.isEmpty()) {
			productProfiles.addAll(productProfileService
					.findByProductCategoryPidsAndActivated(Arrays.asList(categoryPids.split(","))));
			return new ResponseEntity<>(productProfiles, HttpStatus.OK);
		}
		// group selected
		if (categoryPids.isEmpty() && !groupPids.isEmpty()) {
			productProfiles.addAll(
					productGroupProductService.findByProductGroupPidsAndActivated(Arrays.asList(groupPids.split(","))));
			return new ResponseEntity<>(productProfiles, HttpStatus.OK);
		}
		return new ResponseEntity<>(productProfiles, HttpStatus.OK);
	}

	@RequestMapping(value = "/productProfiles/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Set<FileDTO>> getProductProfileImages(@PathVariable String pid) {
		log.debug("Web request to get ProductProfile images by pid : {}", pid);
		return new ResponseEntity<>(productProfileService.findProductProfileImages(pid), HttpStatus.OK);
	}

	@RequestMapping(value = "/productProfiles/delete-image/{pid}/{filePid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> deleteProductProfileImage(@PathVariable String pid, @PathVariable String filePid) {
		log.debug("REST request to delete product Profile image : {}", filePid);
		return new ResponseEntity<>(productProfileService.deleteProductProfileImage(pid, filePid), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/productProfiles/set-size", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setProductProfileSize(@RequestBody ProductSize productSize) throws URISyntaxException {
		log.debug("Web request to save ProductSize : {}", productSize);
		productProfileService.updateSize(productSize.getSize(), productSize.getProducts());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of ProductProfile.
	 * @param productProfileDTO
	 *            the productProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ProductProfileDTO
	 */
	@Timed
	@RequestMapping(value = "/productProfiles/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductProfileDTO> updateProductProfileStatus(
			@Valid @RequestBody ProductProfileDTO productProfileDTO) {
		log.debug("Web request to change status of ProductProfile ", productProfileDTO);
		ProductProfileDTO res = productProfileService.updateProductProfileStatus(productProfileDTO.getPid(),
				productProfileDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/productProfiles/searchByName", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProductProfileDTO>>searchByName(@RequestParam String input) {
		log.debug("Web request to get searchbyname : {}");
		List<ProductProfileDTO> productProfileDTOs = productProfileService.searchByName(input);
		return new ResponseEntity<>(productProfileDTOs, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 * 
	 *        Activate STATUS /productProfiles/activateProductProfiles :
	 *        activate status of productProfiles.
	 * 
	 * @param productprofiles
	 *            the productprofiles to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/productProfiles/activateProductProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductProfileDTO> activateProductProfile(@Valid @RequestParam String productprofiles) {
		log.debug("Web request to activate the status of ProductProfile ");
		String[] productProfiles = productprofiles.split(",");
		for (String productprofilepid : productProfiles) {
			productProfileService.updateProductProfileStatus(productprofilepid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/productProfiles/productGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupDTO>> getProductProfileProductGroups(@PathVariable String pid) {
		log.debug("Web request to get ProductProfile images by pid : {}", pid);
		return new ResponseEntity<>(productGroupProductService.findProductGroupByProductPid(pid), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/productProfiles/assign-tax-master/{productProfilePid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAssignedTax(@RequestParam String selectedTax,
			@PathVariable String productProfilePid) {
		log.debug("Web request to save ProductProfile by productProfilePids : {}", productProfilePid);
		List<String> taxMasterPids = new ArrayList<>();
		String[] taxMasterpids = selectedTax.split(",");
		for (String taxMasterpid : taxMasterpids) {
			taxMasterPids.add(taxMasterpid);
		}
		productProfileService.saveTaxMaster(taxMasterPids, productProfilePid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/productProfiles/assignTax/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaxMasterDTO>> getAssignTaxes(@PathVariable String pid) {
		log.debug("Web request to get TaxMasterDTO  by pid : {}", pid);
		List<TaxMasterDTO> taxMasterDTOs = productProfileService.getAssignedTaxMaster(pid);
		return new ResponseEntity<>(taxMasterDTOs, HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/productProfiles/upload-image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> uploadFile(MultipartHttpServletRequest request) {
		log.debug("Request to upload a file : {}");
		String productPid = request.getParameterValues("productPid")[0];

		Iterator<String> itrator = request.getFileNames();
		MultipartFile file = request.getFile(itrator.next());
		List<String> filePid = new ArrayList<>();
		if (file != null && file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		try {
			String newFilepid = productProfileService.saveProductImage(file, productPid);
			filePid.add(newFilepid);
			return new ResponseEntity<>(filePid, HttpStatus.OK);
		} catch (FileManagerException | IOException ex) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
		}
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/productProfiles/upload-edit-image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> uploadEditFile(MultipartHttpServletRequest request) {
		log.debug("Request to upload a file : {}");
		String productPid = request.getParameterValues("productPid")[0];
		String ImgFilePid = request.getParameterValues("ImgFilePid")[0];
		List<String> sucessfilePid = new ArrayList<>(Arrays.asList(ImgFilePid));
		Iterator<String> itrator = request.getFileNames();
		MultipartFile file = request.getFile(itrator.next());

		if (file != null && file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		try {
			productProfileService.updateProductImage(file, productPid, ImgFilePid);
			return new ResponseEntity<>(sucessfilePid, HttpStatus.OK);
		} catch (FileManagerException | IOException ex) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
		}
	}

	@Timed
	@RequestMapping(value = "/productProfiles/get-by-status-filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProductProfileDTO>> getAccountProfilesByStatus(@RequestParam boolean active,
			@RequestParam boolean deactivate) {
		log.debug("Web request to get get activitys : {}");
		List<ProductProfileDTO> productProfileDTOs = new ArrayList<>();
		if (active == true && deactivate == true) {
			productProfileDTOs
					.addAll(productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(true));
			productProfileDTOs
					.addAll(productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(false));
		} else if (active) {
			productProfileDTOs
					.addAll(productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(true));
		} else if (deactivate) {
			productProfileDTOs
					.addAll(productProfileService.findAllByCompanyAndActivatedProductProfileOrderByNameLiits(false));
		}
		return new ResponseEntity<>(productProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/productProfiles/download-profile-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(@RequestParam String status,HttpServletResponse response) {
		List<ProductGroupProduct> productGroupProducts =  productGroupProductService.findAllByCompany();
		List<ProductProfileDTO> productProfileDTOs = new ArrayList<ProductProfileDTO>();
		switch (status)
		{
		case "All":
			productProfileDTOs = productProfileService.findAllByCompany();
			break;
		case "Active":
			productProfileDTOs = productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true);
			break;
		case "Deactive":
			productProfileDTOs = productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(false);
			break;
		case "MultipleActivate":
//			productProfileDTOs = productProfileService.findAllByCompany();
			break;
		}
		buildExcelDocument(productGroupProducts, productProfileDTOs, response);
	}
	
	private void buildExcelDocument(List<ProductGroupProduct> productGroupProducts,
									List<ProductProfileDTO> productProfileDTOs,
									HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "productProfile" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = {"Name", "Category", "Group", "Division", "Unit Quantity", "SKU", "Price", "Alias", "Status",
				"Product Id", "MRP", "Tax Rate", "Size", "HSN Code", "Description", "Product Code"};
		try(HSSFWorkbook workbook = new HSSFWorkbook()){
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, productProfileDTOs, productGroupProducts);
			// Resize all columns to fit the content size
	        for(int i = 0; i < headerColumns.length; i++) {
	        	worksheet.autoSizeColumn(i);
	        }
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			//Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Product profiles {}", ex.getMessage());
		}
	}
	
	private void createReportRows(HSSFSheet worksheet, List<ProductProfileDTO> productProfileDTOs, List<ProductGroupProduct> productGroupProducts) {
		/* CreationHelper helps us create instances of various things like DataFormat, 
        Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
        // Create Other rows and cells with Sales data
        int rowNum = 1;
    	for (ProductProfileDTO pp: productProfileDTOs) {
    		// find product group
			Optional<ProductGroupProduct> temp =  productGroupProducts.stream()
					.filter(pGroup -> pGroup.getProduct().getPid().equals(pp.getPid())).findAny();
			String productGroup ="";
			if (temp.isPresent()) {
				productGroup = temp.get().getProductGroup().getName();
			}


    		HSSFRow row = worksheet.createRow(rowNum++);
    		row.createCell(0).setCellValue(pp.getName().replace("#13;#10;", " "));
    		row.createCell(1).setCellValue(pp.getProductCategoryName());
    		row.createCell(2).setCellValue(productGroup);
    		row.createCell(3).setCellValue(pp.getDivisionName());
    		row.createCell(4).setCellValue(pp.getUnitQty() == null ? "" : pp.getUnitQty().toString());
    		row.createCell(5).setCellValue(pp.getSku() == null ? "" : pp.getSku());
    		row.createCell(6).setCellValue(pp.getPrice().doubleValue());
    		row.createCell(7).setCellValue(pp.getAlias());
    		row.createCell(8).setCellValue(pp.getActivated() == true ? "Activated" : "Deactivated");
			row.createCell(9).setCellValue(pp.getProductId());
			row.createCell(10).setCellValue(pp.getMrp());
			row.createCell(11).setCellValue(pp.getTaxRate());
			row.createCell(12).setCellValue(pp.getSize());
			row.createCell(13).setCellValue(pp.getHsnCode());
			row.createCell(14).setCellValue(pp.getDescription());
			row.createCell(15).setCellValue(pp.getProductCode());
		}
		
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// Create a Font for styling header cells
        Font headerFont = worksheet.getWorkbook().createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        // Create a CellStyle with the font
        HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
        headerCellStyle.setFont(headerFont);
        // Create a Row
     	HSSFRow headerRow = worksheet.createRow(0);
     	// Create cells
		for(int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headerColumns[i]);
            cell.setCellStyle(headerCellStyle);
        }
	}
}
