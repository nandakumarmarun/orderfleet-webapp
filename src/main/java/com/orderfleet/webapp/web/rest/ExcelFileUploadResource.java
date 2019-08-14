package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.service.async.ExcelPOIHelper;

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
	private CompanyService companyService;

	@Inject
	private ExcelPOIHelper excelPOIHelper;

	@RequestMapping(value = "/excel-file-upload", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivities(Model model) throws URISyntaxException {
		log.debug("Web request to get a page to upload a excel file");
		return "company/excelFileUpload";
	}

	@RequestMapping(value = "/excel-file-upload/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFile(MultipartHttpServletRequest request)
			throws IOException, EncryptedDocumentException, InvalidFormatException {

		Iterator<String> itrator = request.getFileNames();
		MultipartFile multipartFile = request.getFile(itrator.next());

		log.info("Request to upload a file: \t" + multipartFile.getOriginalFilename());

		File file = convert(multipartFile);

		String fileExtension = file.getName().split("\\.")[1];

		if (!fileExtension.equalsIgnoreCase("xls") && !fileExtension.equalsIgnoreCase("xlsx")) {
			return "FAILED";
		}

		String uploadCustomeStatus = uploadCustomer(file);
		String uploadProductstatus = uploadProduct(file);
		String uploadProductPriceStatus = uploadProductPrice(file);
		String uploadStock = uploadStock(file);

		String status = "";

		if (uploadCustomeStatus.equalsIgnoreCase("SUCCESS") && uploadProductstatus.equalsIgnoreCase("SUCCESS")
				&& uploadProductPriceStatus.equalsIgnoreCase("SUCCESS") && uploadStock.equalsIgnoreCase("SUCCESS")) {
			status = "SUCCESS";
		} else {
			status = "UPLOAD FAILED";
		}

		return status;
	}

	private String uploadCustomer(File file) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 0);

		parseAndUploadAccountProfile(data);

		return "SUCCESS";
	}

	private String uploadProduct(File file) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 1);

		parseAndUploadProductProfile(data);

		return "SUCCESS";
	}

	private String uploadProductPrice(File file) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 2);

		parseAndUploadPriceLevelList(data);

		return "SUCCESS";
	}

	private String uploadStock(File file) throws IOException {

		Map<Integer, List<String>> data = excelPOIHelper.readExcel(file, 3);

		parseAndUploadStock(data);

		return "SUCEESS";
	}

	private void parseAndUploadAccountProfile(Map<Integer, List<String>> datas) {

		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);

			String accountProfileName = dataList.get(0) != null ? dataList.get(0) : "";

			System.out.println(accountProfileName + "-------------accountProfileName");
		}

	}

	private void parseAndUploadProductProfile(Map<Integer, List<String>> datas) {

		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);

			String productProfileName = dataList.get(0) != null ? dataList.get(0) : "";

			System.out.println(productProfileName + "-------------productProfileName");

		}

	}

	private void parseAndUploadPriceLevelList(Map<Integer, List<String>> datas) {

		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);

			String productCode = dataList.get(0) != null ? dataList.get(0) : "";

			System.out.println(productCode + "-------------productCode Price Level");
		}

	}

	private void parseAndUploadStock(Map<Integer, List<String>> datas) {

		for (int rowCount = 1; rowCount < datas.size(); rowCount++) {
			List<String> dataList = datas.get(rowCount);

			String productCode = dataList.get(0) != null ? dataList.get(0) : "";

			System.out.println(productCode + "-------------productCode Stock");
		}

	}

	public static File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

}
