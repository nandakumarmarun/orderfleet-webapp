package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.LeadToCashReportConfig;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.repository.LeadToCashReportConfigRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LeadToCashReportConfigService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.dto.LeadToCashReportConfigDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class LeadToCashReportConfigResource {

	private final Logger log = LoggerFactory.getLogger(LeadManagementResource.class);
	
	@Inject
	private StageRepository stageRepository;
	
	@Inject
	private LeadToCashReportConfigRepository leadToCashReportConfigRepo;
	
	@Inject
	private LeadToCashReportConfigService leadToCashReportConfigService;
	
	
	@RequestMapping(value = "/lead-to-cash-report-config", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllLeadToCashReportConfig(Pageable pageable, Model model) {
		model.addAttribute("stages",stageRepository.findAllByCompanyId());
		List<String> stagePids = leadToCashReportConfigService.findStagePidsByCompany();
		model.addAttribute("stagePids", stagePids);
		return "company/stage/leadToCashReportConfig";
	}
	
	@Timed
	@RequestMapping(value = "/lead-to-cash-report-config/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<LeadToCashReportConfigDTO>> loadLeadToCashReportConfig() {
		log.debug("Web request to load leadToCashReportConfigs ");
		return new ResponseEntity<>(leadToCashReportConfigService.findAllByCompany(), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/lead-to-cash-report-config", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<LeadToCashReportConfigDTO> createLeadToCashReportConfig(
			@Valid @RequestBody LeadToCashReportConfigDTO leadToCashReportConfigDTO) throws URISyntaxException {
		log.debug("Web request to save LeadToCashReportConfig : {}", leadToCashReportConfigDTO);	
		Optional<LeadToCashReportConfig> existData = leadToCashReportConfigRepo.findByNameAndCompanyId(leadToCashReportConfigDTO.getName(),SecurityUtils.getCurrentUsersCompanyId());
		if (existData.isPresent()) {
			leadToCashReportConfigDTO.setPid(existData.get().getPid());
			LeadToCashReportConfigDTO result = leadToCashReportConfigService.updateLeadToCashReportConfig(leadToCashReportConfigDTO);
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityUpdateAlert("leadToCashReportConfig", result.getPid().toString()))
					.body(result);
		}else {
			LeadToCashReportConfigDTO result = leadToCashReportConfigService.saveLeadToCashReportConfig(leadToCashReportConfigDTO);
			return ResponseEntity.created(new URI("/web/leadto-cashreport-config/" + result.getPid()))
					.headers(HeaderUtil.createEntityCreationAlert("leadToCashReportConfig", result.getPid().toString()))
					.body(result);
		}
		
	}
	
	
	
	@RequestMapping(value = "/lead-to-cash-report-config/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<LeadToCashReportConfigDTO> getLeadToCashReportConfig(@PathVariable String pid) {
		log.debug("Web request to get LeadToCashReportConfig by pid : {}", pid);
		return leadToCashReportConfigService.findLeadToCashReportConfigByPid(pid)
				.map(leadToCashReportConfigDTO -> new ResponseEntity<>(leadToCashReportConfigDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
	@RequestMapping(value = "/lead-to-cash-report-config/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteLeadToCashReportConfig(@PathVariable String pid) {
		log.debug("REST request to delete LeadToCashReportConfig : {}", pid);
		leadToCashReportConfigService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("leadToCashReportConfig", pid.toString())).build();
	}
}
