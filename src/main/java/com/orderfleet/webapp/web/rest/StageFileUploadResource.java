package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.validation.Valid;

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
import com.orderfleet.webapp.domain.StageFileUpload;
import com.orderfleet.webapp.repository.StageFileUploadRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StageFileUploadService;
import com.orderfleet.webapp.web.rest.dto.StageFileUploadDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class StageFileUploadResource {

	private final Logger log = LoggerFactory.getLogger(StageFileUploadResource.class);
	
	@Inject
	private StageRepository stageRepository;
	
	@Inject
	private StageFileUploadRepository stageFileUploadRepository;
	
	@Inject
	private StageFileUploadService stageFileUploadService;
	
	
	@RequestMapping(value = "/stage-file-upload", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getStageFileUpload(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Partners");
		model.addAttribute("stages",stageRepository.findAllByCompanyId());
		model.addAttribute("stageFileUploads",stageFileUploadRepository.findAllStageByCompany());
		return "company/stage-file-upload";
	}
	
	
	@RequestMapping(value = "/stage-file-upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<StageFileUploadDTO> createStageFileUpload(@Valid @RequestBody StageFileUploadDTO stageFileUploadDTO) {
		log.debug("REST request to create StageFileUpload : {}", stageFileUploadDTO);
		StageFileUploadDTO result = stageFileUploadService.save(stageFileUploadDTO);
		return ResponseEntity.ok().body(result);
	}
	
	
	@RequestMapping(value = "/stage-file-upload", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StageFileUploadDTO> updateSnrichPartner(@Valid @RequestBody StageFileUploadDTO stageFileUploadDTO)
			throws URISyntaxException {
		log.debug("REST request to update StageFileUpload : {}", stageFileUploadDTO);
		StageFileUploadDTO result = stageFileUploadService.update(stageFileUploadDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("StageFileUpload",result.getFileUploadName())).body(result);
	}
	
	
	@RequestMapping(value = "/stage-file-upload/{pid}", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<StageFileUploadDTO> editStageFileUpload(@PathVariable String pid){
		log.debug("Web request to get a page of StageFileUpload");
		return new ResponseEntity<>(stageFileUploadService.findOneByPid(pid), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/stage-file-upload/find", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<StageFileUploadDTO> getStageFileUpload(@RequestParam("pid") String stagePid){
		log.debug("Web request to get  StageFileUpload by pid");
		StageFileUpload stageFileUpload = stageFileUploadRepository.findByCompanyAndStagePid(stagePid);
		StageFileUploadDTO result = new StageFileUploadDTO();
		if(stageFileUpload != null) {
			result = new StageFileUploadDTO(stageFileUpload);
		}else {
			result.setFileUploadName("Quotation;Flowchart;Design");
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/stage-file-upload/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDocument(@PathVariable String pid) {
		log.debug("REST request to delete StageFileUpload : {}", pid);
		stageFileUploadService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("stageFileUpload", pid.toString())).build();
	}
}
