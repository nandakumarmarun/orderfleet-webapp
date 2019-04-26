package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;

import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing OpeningStock.
 * 
 * @author Sarath T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class OpeningStockResource {

	private final Logger log = LoggerFactory.getLogger(OpeningStockResource.class);

	@Inject
	private OpeningStockService openingStockService;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private ProductProfileService productProfileService;

	/**
	 * POST /openingStocks : Create a new openingStock.
	 *
	 * @param openingStockDTO
	 *            the openingStockDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         openingStockDTO, or with status 400 (Bad Request) if the openingStock
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/openingStocks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<OpeningStockDTO> createOpeningStock(@Valid @RequestBody OpeningStockDTO openingStockDTO)
			throws URISyntaxException {
		log.debug("Web request to save OpeningStock : {}", openingStockDTO);
		if (openingStockDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("openingStock", "idexists",
					"A new Opening Stock cannot already have an ID")).body(null);
		}
		if (openingStockService.findByName(openingStockDTO.getProductProfilePid(), openingStockDTO.getBatchNumber())
				.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("openingStock", "nameexists", "Opening Stock already in use"))
					.body(null);
		}
		openingStockDTO.setActivated(true);
		OpeningStockDTO result = openingStockService.save(openingStockDTO);
		return ResponseEntity.created(new URI("/web/openingStocks/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("openingStock", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /openingStocks : Updates an existing openingStock.
	 *
	 * @param openingStockDTO
	 *            the openingStockDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         openingStockDTO, or with status 400 (Bad Request) if the
	 *         openingStockDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the openingStockDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/openingStocks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<OpeningStockDTO> updateOpeningStock(@Valid @RequestBody OpeningStockDTO openingStockDTO)
			throws URISyntaxException {
		log.debug("Web request to update OpeningStock : {}", openingStockDTO);
		if (openingStockDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("openingStock", "idNotexists", "Opening Stock must have an ID"))
					.body(null);
		}
		Optional<OpeningStockDTO> existingOpeningStock = openingStockService.findByName(openingStockDTO.getPid(),
				openingStockDTO.getBatchNumber());
		if (existingOpeningStock.isPresent()
				&& (!existingOpeningStock.get().getPid().equals(openingStockDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("openingStock", "nameexists", "Opening Stock already in use"))
					.body(null);
		}
		OpeningStockDTO result = openingStockService.update(openingStockDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("openingStock", "idNotexists", "Invalid Opening Stock ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("openingStock", openingStockDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /openingStocks : get all the openingStocks.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of openingStocks
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/openingStocks", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllOpeningStocks(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of OpeningStocks");
		model.addAttribute("openingStocks", openingStockService.findAllByCompanyAndDeactivatedOpeningStock(true));
		model.addAttribute("stockLocations", stockLocationService.findAllActualByCompanyId());
		model.addAttribute("productProfiles", productProfileService.findAllByCompany());
		model.addAttribute("deactivatedOpeningStocks",
				openingStockService.findAllByCompanyAndDeactivatedOpeningStock(false));
		return "company/openingStocks";
	}

	/**
	 * GET /openingStocks/:id : get the "id" openingStock.
	 *
	 * @param id
	 *            the id of the openingStockDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         openingStockDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/openingStocks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<OpeningStockDTO> getOpeningStock(@PathVariable String pid) {
		log.debug("Web request to get OpeningStock by pid : {}", pid);
		return openingStockService.findOneByPid(pid)
				.map(openingStockDTO -> new ResponseEntity<>(openingStockDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /openingStocks/:pid : delete the "pid" openingStock.
	 *
	 * @param pid
	 *            the pid of the openingStockDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/openingStocks/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteOpeningStock(@PathVariable String pid) {
		log.debug("REST request to delete OpeningStock : {}", pid);
		openingStockService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("openingStock", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 8, 2017
	 * 
	 *        UPDATE STATUS /openingStocks/changeStatus:openingStockDTO : update
	 *        status of openingStock.
	 * 
	 * @param openingStockDTO
	 *            the openingStockDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/openingStocks/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OpeningStockDTO> updateOpeningStockStatus(
			@Valid @RequestBody OpeningStockDTO openingStockDTO) {
		log.debug("Web request to update status openingStock : {}", openingStockDTO);
		OpeningStockDTO res = openingStockService.updateOpeningStockStatus(openingStockDTO.getPid(),
				openingStockDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 * 
	 *        Activate STATUS /openingStocks/activateOpeningStock : activate status
	 *        of OpeningStocks.
	 * 
	 * @param openingstocks
	 *            the openingstocks to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/openingStocks/activateOpeningStock", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OpeningStockDTO> activateOpeningStock(@Valid @RequestParam String openingstocks) {
		log.debug("Web request to activate openingStock : {}");
		String[] openingStocks = openingstocks.split(",");
		for (String openingStockpid : openingStocks) {
			openingStockService.updateOpeningStockStatus(openingStockpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/openingStocks/download-openingStock-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(HttpServletResponse response) {
		List<OpeningStockDTO> openingStockDtos = new ArrayList<OpeningStockDTO>();

		openingStockDtos = openingStockService.findAllByCompanyAndDeactivatedOpeningStock(true);

		buildExcelDocument(openingStockDtos, response);
	}

	private void buildExcelDocument(List<OpeningStockDTO> openingStockDtos, HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "openingStocks" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Product Profile Name", "Batch Number", "Stock Location", "Quantity",
				"Opening Stock Date" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, openingStockDtos);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			// Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Product profiles {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<OpeningStockDTO> openingStockDtos) {
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		for (OpeningStockDTO pp : openingStockDtos) {
			HSSFRow row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(pp.getProductProfileName());
			row.createCell(1).setCellValue(pp.getBatchNumber());
			row.createCell(2).setCellValue(pp.getStockLocationName());
			row.createCell(3).setCellValue(pp.getQuantity());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String strDate = pp.getLastModifiedDate().format(formatter);
			row.createCell(4).setCellValue(strDate);

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
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	}
}
