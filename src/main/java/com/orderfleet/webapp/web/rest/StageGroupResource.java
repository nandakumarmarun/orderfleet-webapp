package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.StageGroup;
import com.orderfleet.webapp.domain.StageStageGroup;
import com.orderfleet.webapp.repository.StageGroupRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.StageStageGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StageGroupService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.dto.StageGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class StageGroupResource {

	@Inject
	private StageGroupService stageGroupService;
	
	@Inject
	private StageGroupRepository stageGroupRepository;
	
	@Inject
	private StageService stageService;
	
	@Inject
	private StageRepository stageRepository;
	
	@Inject
	private StageStageGroupRepository stageStageGroupRepository;
	
	@RequestMapping(value = "/stage-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<StageGroupDTO> createStage(@Valid @RequestBody StageGroupDTO stageGroupDTO) {
		if (stageGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("stage", "idexists", "A new stage cannot already have an ID"))
					.body(null);
		}
		if (stageGroupService.findByName(stageGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "nameexists", "Stage Name already in use"))
					.body(null);
		}
		stageGroupDTO.setActivated(true);
		StageGroupDTO result = stageGroupService.save(stageGroupDTO);
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/stage-groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageGroupDTO> updateStage(@Valid @RequestBody StageGroupDTO stageGroupDTO) {
		if (stageGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "idNotexists", "Stage must have an ID")).body(null);
		}
		Optional<StageGroup> existingStage = stageGroupService.findByName(stageGroupDTO.getName());
		if (existingStage.isPresent() && (!existingStage.get().getPid().equals(stageGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "nameexists", "Stage name already in use"))
					.body(null);
		}
		StageGroupDTO result = stageGroupService.update(stageGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "idNotexists", "")).body(null);
		}
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/stage-groups", method = RequestMethod.GET)
	public String getAllStages(Model model) {
		model.addAttribute("stageGroups", stageGroupService.findAllByCompany());
		model.addAttribute("stages", stageService.findAllByCompanyAndActivated(true));
		return "company/stage/stage-groups";
	}

	@RequestMapping(value = "/stage-groups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageGroupDTO> getStage(@PathVariable String pid) {
		return stageGroupService.findOneByPid(pid)
				.map(stageGroup -> new ResponseEntity<>(new StageGroupDTO(stageGroup), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/stage-groups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteStage(@PathVariable String pid) {
		stageGroupService.delete(pid);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/stage-groups/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageGroupDTO> updateStageStatus(
			@Valid @RequestBody StageGroupDTO stageGroupDTO) {
		return new ResponseEntity<>(stageGroupService.updateStageGroupStatus(stageGroupDTO.getPid(),
				stageGroupDTO.getActivated()), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/stage-groups/findStages/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StageDTO>> getStages(@PathVariable String pid) {
		List<Stage> stages = stageStageGroupRepository.findStageByStageGroupPid(pid);
		List<StageDTO> stageDTOs = stages.stream().map(s -> {
			StageDTO stageDTO = new StageDTO();
			stageDTO.setPid(s.getPid());
			stageDTO.setName(s.getName());
			return stageDTO;
		}).collect(Collectors.toList());
		return new ResponseEntity<>(stageDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/stage-groups/assignStages", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Void> saveAssignedAccounts(@RequestParam String pid, @RequestParam String[] assignedStagePids) {
		Optional<StageGroup> opStageGroup = stageGroupRepository.findOneByPid(pid);
		List<Stage> stages = stageRepository.findByPidIn(Arrays.asList(assignedStagePids));
		List<StageStageGroup> stageStageGroups = new ArrayList<>(); 
		if(opStageGroup.isPresent() && !stages.isEmpty()) {
			Long companyId = SecurityUtils.getCurrentUsersCompanyId();
			for (String stagePid : assignedStagePids) {
				StageStageGroup stageStageGroup = new StageStageGroup();
				Optional<Stage> opStage = stages.stream().filter(s -> s.getPid().equals(stagePid)).findAny();
				if(opStage.isPresent()) {
					stageStageGroup.setStage(opStage.get());
					stageStageGroup.setStageGroup(opStageGroup.get());
					stageStageGroup.setCompanyId(companyId);
					stageStageGroups.add(stageStageGroup);
				}
			}
		}
		if(!stageStageGroups.isEmpty()) {
			//delete
			stageStageGroupRepository.deleteByStageGroupPid(pid);
			stageStageGroupRepository.flush();
			stageStageGroupRepository.save(stageStageGroups);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
