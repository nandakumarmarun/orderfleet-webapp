package com.orderfleet.webapp.web.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.Funnel;
import com.orderfleet.webapp.domain.FunnelStage;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.repository.FunnelRepository;
import com.orderfleet.webapp.repository.FunnelStageRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FunnelService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class FunnelResource {

	@Inject
	private StageService stageService;
	
	@Inject
	private StageRepository stageRepository;
	
	@Inject
	private FunnelService funnelService;
	
	@Inject
	private FunnelRepository funnelRepository;
	
	@Inject
	private FunnelStageRepository funnelStageRepository;
	
	@RequestMapping(value = "/funnels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Funnel> createStage(@RequestParam("name") String name, @RequestParam("sortOrder") int sortOrder) {
		if (funnelService.findByName(name).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("funnel", "nameexists", "Funnel Name already in use"))
					.body(null);
		}
		Funnel result = funnelService.save(name, sortOrder);
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/funnels", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Funnel> updateStage(@RequestParam Long id, @RequestParam String name, @RequestParam int sortOrder) {
		Optional<Funnel> existingFunnel = funnelService.findByName(name);
		if (existingFunnel.isPresent() && (existingFunnel.get().getId() != id)) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("funnel", "nameexists", "Funnel name already in use"))
					.body(null);
		}
		Funnel result = funnelService.update(id, name, sortOrder);
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/funnels", method = RequestMethod.GET)
	public String getAllStages(Model model) {
		model.addAttribute("funnels", funnelService.findAllByCompany());
		model.addAttribute("stages", stageService.findAllByCompanyAndActivated(true));
		return "company/stage/funnels";
	}

	@RequestMapping(value = "/funnels/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Funnel> getFunnel(@PathVariable Long id) {
		Funnel funnel = funnelService.findOne(id);
		if(funnel == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(funnel, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/funnels/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteFunnel(@PathVariable Long id) {
		funnelService.delete(id);
		return ResponseEntity.ok().build();
	}
	
	/*@RequestMapping(value = "/funnels/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageGroupDTO> updateStageStatus(
			@Valid @RequestBody StageGroupDTO stageGroupDTO) {
		return new ResponseEntity<>(stageGroupService.updateStageGroupStatus(stageGroupDTO.getPid(),
				stageGroupDTO.getActivated()), HttpStatus.OK);
	}*/
	
	@RequestMapping(value = "/funnels/findStages/{funnelId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StageDTO>> getStages(@PathVariable Long funnelId) {
		List<Stage> stages = funnelStageRepository.findStageByFunnelId(funnelId);
		List<StageDTO> stageDTOs = stages.stream().map(s -> {
			StageDTO stageDTO = new StageDTO();
			stageDTO.setPid(s.getPid());
			stageDTO.setName(s.getName());
			return stageDTO;
		}).collect(Collectors.toList());
		return new ResponseEntity<>(stageDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/funnels/assignStages", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Void> saveAssignedStages(@RequestParam Long id, @RequestParam String[] assignedStagePids) {
		Funnel funnel = funnelService.findOne(id);
		funnelStageRepository.deleteByFunnelId(id);
		List<Stage> stages = stageRepository.findByPidIn(Arrays.asList(assignedStagePids));
		if(funnel != null && !stages.isEmpty()) {
			Long companyId = SecurityUtils.getCurrentUsersCompanyId();
			for (String stagePid : assignedStagePids) {
				FunnelStage funnelStage = new FunnelStage();
				Optional<Stage> opStage = stages.stream().filter(s -> s.getPid().equals(stagePid)).findAny();
				if(opStage.isPresent()) {
					funnelStage.setStage(opStage.get());
					funnelStage.setFunnel(funnel);
					funnelStage.setCompanyId(companyId);
					funnel.addStage(funnelStage);
				}
			}
		}
		funnelRepository.save(funnel);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
