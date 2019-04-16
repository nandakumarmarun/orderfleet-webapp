package com.orderfleet.webapp.web.rest;

import java.util.Optional;

import javax.inject.Inject;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class StageResource {

	@Inject
	private StageService stageService;

	@RequestMapping(value = "/stages", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<StageDTO> createStage(@Valid @RequestBody StageDTO stageDTO) {
		if (stageDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("stage", "idexists", "A new stage cannot already have an ID"))
					.body(null);
		}
		if (stageService.findByName(stageDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "nameexists", "Stage Name already in use"))
					.body(null);
		}
		stageDTO.setActivated(true);
		StageDTO result = stageService.save(stageDTO);
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/stages", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StageDTO> updateStage(@Valid @RequestBody StageDTO stageDTO) {
		if (stageDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "idNotexists", "Stage must have an ID")).body(null);
		}
		Optional<Stage> existingStage = stageService.findByName(stageDTO.getName());
		if (existingStage.isPresent() && (!existingStage.get().getPid().equals(stageDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "nameexists", "Stage name already in use"))
					.body(null);
		}
		StageDTO result = stageService.update(stageDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stage", "idNotexists", "Invalid Stage ID")).body(null);
		}
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/stages", method = RequestMethod.GET)
	public String getAllStages(Model model) {
		
		model.addAttribute("stages", stageService.findAllByCompany());
		model.addAttribute("stageNametypes",StageNameType.values());
		return "company/stage/stages";
	}

	@RequestMapping(value = "/stages/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StageDTO> getStage(@PathVariable String pid) {
		return stageService.findOneByPid(pid)
				.map(stage -> new ResponseEntity<>(new StageDTO(stage), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/stages/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteStage(@PathVariable String pid) {
		stageService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("stage", pid))
				.build();
	}
	
	@RequestMapping(value = "/stages/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageDTO> updateStageStatus(
			@Valid @RequestBody StageDTO stageDTO) {
		return new ResponseEntity<>(stageService.updateStageStatus(stageDTO.getPid(),
				stageDTO.getActivated()), HttpStatus.OK);

	}
	
}
