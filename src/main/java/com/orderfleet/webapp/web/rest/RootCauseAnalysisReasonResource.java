package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.RootCauseAnalysisReasonService;
import com.orderfleet.webapp.web.rest.dto.RootCauseAnalysisReasonDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;


@Controller
@RequestMapping("/web")
public class RootCauseAnalysisReasonResource {
	
	private final Logger log = LoggerFactory.getLogger(RootCauseAnalysisReasonResource.class);
	
	@Inject
	private RootCauseAnalysisReasonService rootCauseAnalysisReasonService;

	@RequestMapping(value = "/root-cause-analysis-reason", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllRootCauseAnalysisReasonResource(Model model) {
		log.debug("Web request to get a page of RootCauseAnalysisReason");
		model.addAttribute("rootCauseAnalysisReasons",rootCauseAnalysisReasonService.findAllByCompany());
		return "company/root-cause-analysis-reason";
	}
	
	@RequestMapping(value = "/root-cause-analysis-reason", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<RootCauseAnalysisReasonDTO> createRootCauseAnalysisReason(
			@Valid @RequestBody RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO) throws URISyntaxException{
		log.debug("Web request to save RootCauseAnalysisReason : {}", rootCauseAnalysisReasonDTO);
		RootCauseAnalysisReasonDTO result = rootCauseAnalysisReasonService.save(rootCauseAnalysisReasonDTO);
		return ResponseEntity.created(new URI("/web/root-cause-analysis-reason" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("distanceFare", result.getPid().toString()))
				.body(result);
		
	}
	
	@RequestMapping(value = "/root-cause-analysis-reason", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RootCauseAnalysisReasonDTO> updateRootCauseAnalysisReason(
			@Valid @RequestBody RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO) {
		log.debug("Web request to update RootCauseAnalysisReason : {}", rootCauseAnalysisReasonDTO);
		RootCauseAnalysisReasonDTO result = rootCauseAnalysisReasonService.update(rootCauseAnalysisReasonDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("rootCauseAnalysisReason", rootCauseAnalysisReasonDTO.getPid().toString()))
				.body(result);
	}
	
	@RequestMapping(value = "/root-cause-analysis-reason/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteRootCauseAnalysisReason(@PathVariable String pid) {
		log.debug("REST request to delete RootCauseAnalysisReason : {}", pid);
		rootCauseAnalysisReasonService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("rootCauseAnalysisReason", pid.toString()))
				.build();
	}
	
	@RequestMapping(value = "/root-cause-analysis-reason/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RootCauseAnalysisReasonDTO> getRootCauseAnalysisReason(@PathVariable String pid) {
		log.debug("Web request to get RootCauseAnalysisReason by pid : {}", pid);
		return rootCauseAnalysisReasonService.findOneByPid(pid)
				.map(rootCauseAnalysisReasonDTO -> new ResponseEntity<>(rootCauseAnalysisReasonDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
