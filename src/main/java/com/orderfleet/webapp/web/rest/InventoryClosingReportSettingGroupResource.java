package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.Flow;
import com.orderfleet.webapp.service.InventoryClosingReportSettingGroupService;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class InventoryClosingReportSettingGroupResource {
	private final Logger log = LoggerFactory.getLogger(InventoryClosingReportSettingGroupResource.class);

	@Inject
	private InventoryClosingReportSettingGroupService inventoryClosingReportSettingGroupService;

	/**
	 * POST /inventory-closing-report-setting-group : Create a new inventoryClosingReportSettingGroup.
	 *
	 * @param inventoryClosingReportSettingGroupDTO
	 *            the inventoryClosingReportSettingGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new inventoryClosingReportSettingGroupDTO, or with status 400 (Bad Request) if the inventoryClosingReportSettingGroup has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-closing-report-setting-group", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<InventoryClosingReportSettingGroupDTO> createInventoryClosingReportSettingGroup(@Valid @RequestBody InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO) throws URISyntaxException {
		log.debug("Web request to save InventoryClosingReportSettingGroup : {}", inventoryClosingReportSettingGroupDTO);
		if (inventoryClosingReportSettingGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReportSettingGroup", "idexists", "A new inventoryClosingReportSettingGroup cannot already have an ID"))
					.body(null);
		}
		if (inventoryClosingReportSettingGroupService.findByName(inventoryClosingReportSettingGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReportSettingGroup", "nameexists", "InventoryClosingReportSettingGroup already in use")).body(null);
		}
		inventoryClosingReportSettingGroupDTO.setActivated(true);
		InventoryClosingReportSettingGroupDTO result = inventoryClosingReportSettingGroupService.save(inventoryClosingReportSettingGroupDTO);
		return ResponseEntity.created(new URI("/web/inventoryClosingReportSettingGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("inventoryClosingReportSettingGroup", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /inventory-closing-report-setting-group : Updates an existing inventoryClosingReportSettingGroup.
	 *
	 * @param inventoryClosingReportSettingGroupDTO
	 *            the inventoryClosingReportSettingGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         inventoryClosingReportSettingGroupDTO, or with status 400 (Bad Request) if the inventoryClosingReportSettingGroupDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the inventoryClosingReportSettingGroupDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-closing-report-setting-group", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryClosingReportSettingGroupDTO> updateInventoryClosingReportSettingGroup(@Valid @RequestBody InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO) throws URISyntaxException {
		log.debug("REST request to update InventoryClosingReportSettingGroup : {}", inventoryClosingReportSettingGroupDTO);
		if (inventoryClosingReportSettingGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReportSettingGroup", "idNotexists", "InventoryClosingReportSettingGroup must have an ID")).body(null);
		}
		Optional<InventoryClosingReportSettingGroupDTO> existingInventoryClosingReportSettingGroup = inventoryClosingReportSettingGroupService.findByName(inventoryClosingReportSettingGroupDTO.getName());
		if (existingInventoryClosingReportSettingGroup.isPresent() && (!existingInventoryClosingReportSettingGroup.get().getPid().equals(inventoryClosingReportSettingGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReportSettingGroup", "nameexists", "InventoryClosingReportSettingGroup already in use")).body(null);
		}
		InventoryClosingReportSettingGroupDTO result = inventoryClosingReportSettingGroupService.update(inventoryClosingReportSettingGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReportSettingGroup", "idNotexists", "Invalid InventoryClosingReportSettingGroup ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("inventoryClosingReportSettingGroup", inventoryClosingReportSettingGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /inventory-closing-report-setting-group : get all the inventoryClosingReportSettingGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventoryClosingReportSettingGroups in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/inventory-closing-report-setting-group", method = RequestMethod.GET)
	@Timed
	public String getAllInventoryClosingReportSettingGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of InventoryClosingReportSettingGroups");
		List<InventoryClosingReportSettingGroupDTO> inventoryClosingReportSettingGroups = inventoryClosingReportSettingGroupService.findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(true);
		List<InventoryClosingReportSettingGroupDTO> deactivatedInventoryClosingReportSettingGroups = inventoryClosingReportSettingGroupService.findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(false);
		model.addAttribute("inventoryClosingReportSettingGroups", inventoryClosingReportSettingGroups);
		model.addAttribute("deactivatedInventoryClosingReportSettingGroups", deactivatedInventoryClosingReportSettingGroups);
		List<Flow>flows=Arrays.asList(Flow.values());
		model.addAttribute("flows", flows);
		return "company/inventoryClosingReportSettingGroup";
	}

	
	/**
	 * GET /inventory-closing-report-setting-group/:pid : get the "pid" inventoryClosingReportSettingGroup.
	 *
	 * @param pid
	 *            the pid of the inventoryClosingReportSettingGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         inventoryClosingReportSettingGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/inventory-closing-report-setting-group/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryClosingReportSettingGroupDTO> getInventoryClosingReportSettingGroup(@PathVariable String pid) {
		log.debug("Web request to get InventoryClosingReportSettingGroup by pid : {}", pid);
		return inventoryClosingReportSettingGroupService.findOneByPid(pid).map(inventoryClosingReportSettingGroupDTO -> new ResponseEntity<>(inventoryClosingReportSettingGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /inventory-closing-report-setting-group/:id : delete the "id" inventoryClosingReportSettingGroup.
	 *
	 * @param id
	 *            the id of the inventoryClosingReportSettingGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/inventory-closing-report-setting-group/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteInventoryClosingReportSettingGroup(@PathVariable String pid) {
		log.debug("REST request to delete InventoryClosingReportSettingGroup : {}", pid);
		inventoryClosingReportSettingGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("inventoryClosingReportSettingGroup", pid.toString())).build();
	}

	/**
	 * 
	 *        UPDATE STATUS /inventory-closing-report-setting-group/changeStatus:inventoryClosingReportSettingGroupDTO : update status of inventoryClosingReportSettingGroup.
	 * 
	 * @param inventoryClosingReportSettingGroupDTO
	 *            the inventoryClosingReportSettingGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/inventory-closing-report-setting-group/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventoryClosingReportSettingGroupDTO> updateActivityGroupStatus(@Valid @RequestBody InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO) {
		log.debug("request to update inventoryClosingReportSettingGroup status", inventoryClosingReportSettingGroupDTO);
		InventoryClosingReportSettingGroupDTO res = inventoryClosingReportSettingGroupService.updateInventoryClosingReportSettingGroupStatus(inventoryClosingReportSettingGroupDTO.getPid(), inventoryClosingReportSettingGroupDTO.isActivated());
		return new ResponseEntity<InventoryClosingReportSettingGroupDTO>(res, HttpStatus.OK);
	}

	/**
	 * 
	 *        Activate STATUS /inventory-closing-report-setting-group/activateInventoryClosingReportSettingGroup : activate status of inventoryClosingReportSettingGroups.
	 * 
	 * @param inventoryClosingReportSettingGroups
	 * 				the inventoryClosingReportSettingGroups to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/inventory-closing-report-setting-group/activateInventoryClosingReportSettingGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventoryClosingReportSettingGroupDTO> activateInventoryClosingReportSettingGroup(@Valid @RequestParam String inventoryClosingReportSettingGroups) {
		log.debug("request to activate inventoryClosingReportSettingGroup ");
		String[] inventoryClosingReportSettingGrouparray = inventoryClosingReportSettingGroups.split(",");
		for (String inventoryClosingReportSettingGroup : inventoryClosingReportSettingGrouparray) {
			inventoryClosingReportSettingGroupService.updateInventoryClosingReportSettingGroupStatus(inventoryClosingReportSettingGroup, true);
		}
		return new ResponseEntity<InventoryClosingReportSettingGroupDTO>(HttpStatus.OK);
	}
}
