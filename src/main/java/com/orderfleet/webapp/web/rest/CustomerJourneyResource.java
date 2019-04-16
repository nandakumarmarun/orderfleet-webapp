package com.orderfleet.webapp.web.rest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.StageHeaderDTO;

@Controller
@RequestMapping("/web")
public class CustomerJourneyResource {

	@Inject
	private LocationService locationService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@RequestMapping(value = "/customer-journey", method = RequestMethod.GET)
	public String customerJourney(Model model) {
		List<LocationDTO> locationDTOs = locationService.findAllByCompanyAndLocationActivated(true);
		model.addAttribute("locations", locationDTOs);
		List<String> locationPids = locationDTOs.stream().map(LocationDTO::getPid).collect(Collectors.toList());
		model.addAttribute("accountProfiles",locationAccountProfileRepository.findAccountProfileByLocationPidInAndAccountProfileActivated(locationPids, Boolean.TRUE));
		return "company/stage/customer-journey";
	}
	
	@RequestMapping(value = "/customer-journey/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<StageHeaderDTO>> filterCustomerJourney(@RequestParam("accountPid") String[] accountPids) {
		List<StageHeaderDTO> stageHeaders = createStageReportByAccountPids(Arrays.asList(accountPids));
		return new ResponseEntity<>(stageHeaders, HttpStatus.OK);
	}

	private List<StageHeaderDTO> createStageReportByAccountPids(List<String> accountPids) {
		List<StageHeader> stageHeaders = stageHeaderRepository.findByAccountPidIn(accountPids);
		return stageHeaders.stream().map(sh -> new StageHeaderDTO(sh, filledFormRepository))
				.collect(Collectors.toList());
	}
	
	@GetMapping("/customer-journey/stage-header-files/{filepid}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filepid) throws FileManagerException {
		Resource file = this.fileManagerService.loadAsResource(filepid);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

}
