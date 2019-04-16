package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DynamicReportHeader;
import com.orderfleet.webapp.domain.DynamicReportName;
import com.orderfleet.webapp.repository.DynamicReportUploadRepository;
import com.orderfleet.webapp.service.DynamicReportUploadService;
import com.orderfleet.webapp.web.rest.dto.DynamicReportUploadDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DynamicReportUploadResource {

	private final Logger log = LoggerFactory.getLogger(DynamicReportUploadResource.class);
	
	@Inject
	private DynamicReportUploadService dynamicReportUploadService;
	
	@Inject
	private DynamicReportUploadRepository dynamicReportUploadRepository;
	
	
	@RequestMapping(value = "/dynamic-report-uploads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DynamicReportUploadDTO> createDynamicReportUpload(@Valid @RequestBody DynamicReportUploadDTO dynamicReportUploadDTO) throws URISyntaxException {
		log.debug("Web request to save Dynamic Report Upload : {}", dynamicReportUploadDTO);
		if (dynamicReportUploadService.findByName(dynamicReportUploadDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dynamicReportUpload", "name exists", "Dynamic Report Upload already in use")).body(null);
		}
		DynamicReportUploadDTO result = dynamicReportUploadService.save(dynamicReportUploadDTO);
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping(value = "/dynamic-report-uploads", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DynamicReportUploadDTO> updateDynamicReportUpload(@Valid @RequestBody DynamicReportUploadDTO dynamicReportUploadDTO) throws URISyntaxException {
		log.debug("Web request to save Dynamic Report Upload : {}", dynamicReportUploadDTO);
		if (dynamicReportUploadService.findByName(dynamicReportUploadDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dynamicReportUpload", "name exists", "Dynamic Report Upload already in use")).body(null);
		}
		if(dynamicReportUploadRepository.findOne(dynamicReportUploadDTO.getId())==null){
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dynamicReportUpload", "Id not exists", "Dynamic Report Upload not present")).body(null);
		}
		DynamicReportUploadDTO result = dynamicReportUploadService.update(dynamicReportUploadDTO);
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping(value = "/dynamic-report-uploads", method = RequestMethod.GET)
	@Timed
	public String getAllDynamicReportUploads( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Dynamic Report Uploads");
		model.addAttribute("dynamicReportUploads", dynamicReportUploadService.findAllDynamicReportUploadByCompanyId());
		return "company/dynamicReportUpload";
	}
	
	@RequestMapping(value = "/dynamic-report-uploads/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDynamicReportUpload(@PathVariable long id) {
		log.debug("REST request to delete Dynamic Report Upload : {}", id);
		dynamicReportUploadService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("Dynamic Report Upload", "value")).build();
	}

	@RequestMapping(value = "/dynamic-report-uploads/getDynamicReportUpload", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DynamicReportUploadDTO> getDynamicReportUpload(@RequestParam long dynamicReportUploadId) {
		log.debug("REST request to get Dynamic Report Upload : {}", dynamicReportUploadId);
		DynamicReportUploadDTO dynamicReportUploadDTO=dynamicReportUploadService.findById(dynamicReportUploadId).get();
		return new ResponseEntity<DynamicReportUploadDTO>(dynamicReportUploadDTO, HttpStatus.OK);
	}
	
	/**
	 * Java method to read data from Excel file and insert into database. This method read value
	 * from .xlsx and .xls file, which is an OLE format.
	 * 
	 * @param id
	 * @param file
	 * @throws IOException
	 */
	@PostMapping("/dynamic-report-uploads/upload-file/{id}")
	public ResponseEntity<String> uploadDynamicReportData(@PathVariable Long id, MultipartFile file)
			throws IOException {
		log.debug("save xls file to dynamic document");
		
		// if empty file is uploaded or file size is 0
		if (file.isEmpty() || file.getSize() == 0) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-report-data-upload",
					"dynamic report document the file is empty", "the file is empty")).body(null);
		}
		//Dynamic Report Name is not present
		if (id == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-report-data-upload",
					"Dynamic report name is required", "is required")).body(null);
		}
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		//For checking the type of file
		if("xlsx".equals(ext) || "xls".equals(ext)){
			DynamicReportName dynamicReportName = dynamicReportUploadRepository.findOne(id);
			if(dynamicReportName.isOverwrite()){
				//delete
				Optional<DynamicReportHeader> optionalDynamicReportHeader=dynamicReportUploadService.findByDynamicReportNameId(id);
				if(optionalDynamicReportHeader.isPresent()){
					dynamicReportUploadService.deleteDynamicrReportHeader(optionalDynamicReportHeader.get().getId());
				}
				
			}
			String value=dynamicReportUploadService.readFromExcelAndSaveToDb(file.getInputStream(), dynamicReportName);
			if(value!=null ){
				//More than 30 cell present in a Row 
				if(value.startsWith(",")){
					return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-report-data-upload",
							"Each row contain more  than 30 column", "Less than 30 column is valid")).body(null);
				}
				// Header row contain Empty cell
				else{
				return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-report-data-upload",
						"Column "+value+ " is null", "is required")).body(null);
				}
			}
		}else{
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-report-data-upload",
					"This Type of Cannot be Upload", "Required File Extension is .xlsx or .xls")).body(null);
		}
		return ResponseEntity.ok().body("File: " + file.getOriginalFilename() + " has been uploaded successfully!");
	}
	
}
