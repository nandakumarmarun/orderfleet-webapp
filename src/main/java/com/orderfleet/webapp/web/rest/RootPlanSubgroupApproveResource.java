package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.service.RootPlanSubgroupApproveService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.RootPlanSubgroupApproveDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Controller for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
@Controller
@RequestMapping("/web")
public class RootPlanSubgroupApproveResource {

	private final Logger log = LoggerFactory.getLogger(RootPlanSubgroupApproveResource.class);
	
	@Inject
	private RootPlanSubgroupApproveService rootPlanSubgroupApproveService;
	
	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;
	
	@Inject
	private UserService userService;
	
	/**
	 * POST /root-plan-subgroup-approves : Create a new rootPlanSubgroupApprove.
	 *
	 * @param rootPlanSubgroupApproveDTO
	 *            the rootPlanSubgroupApproveDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new rootPlanSubgroupApproveDTO, or with status 400 (Bad Request) if the rootPlanSubgroupApprove
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/root-plan-subgroup-approves", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<RootPlanSubgroupApproveDTO> createRootPlanSubgroupApprove(@Valid @RequestBody RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO)
			throws URISyntaxException {
		log.debug("Web request to save RootPlanSubgroupApprove : {}", rootPlanSubgroupApproveDTO);
		if (rootPlanSubgroupApproveDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("rootPlanSubgroupApprove", "idexists", "A new rootPlanSubgroupApprove cannot already have an ID"))
					.body(null);
		}
		if (rootPlanSubgroupApproveService.findOneByUserPidAndAttendanceStatusSubgroupId(rootPlanSubgroupApproveDTO.getUserPid(), rootPlanSubgroupApproveDTO.getAttendanceStatusSubgroupId()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("rootPlanSubgroupApprove", "rootPlanSubgroupApproveexists", "RootPlanSubgroupApprove already in use")).body(null);
		}
		RootPlanSubgroupApproveDTO result = rootPlanSubgroupApproveService.save(rootPlanSubgroupApproveDTO);
		return ResponseEntity.created(new URI("/web/root-plan-subgroup-approves/" ))
				.headers(HeaderUtil.createEntityCreationAlert("rootPlanSubgroupApprove", result.getId().toString())).body(result);
	}
	
	/**
	 * GET /root-plan-subgroup-approves : get all the rootPlanSubgroupApproves.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of rootPlanSubgroupApproves
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/root-plan-subgroup-approves", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllRootPlanSubgroupApproves(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of RootPlanSubgroupApproves");
		List<RootPlanSubgroupApproveDTO>rootPlanSubgroupApproveDTOs=rootPlanSubgroupApproveService.findAllByCompany();
		model.addAttribute("rootPlanSubgroupApproves", rootPlanSubgroupApproveDTOs);
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("attendanceStatusSubgroups", attendanceStatusSubgroupService.findAllByCompany());
		return "company/rootPlanSubgroupApprove";
	}
	
	/**
	 * PUT /root-plan-subgroup-approves : Updates an existing rootPlanSubgroupApprove.
	 *
	 * @param rootPlanSubgroupApproveDTO
	 *            the rootPlanSubgroupApproveDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         rootPlanSubgroupApproveDTO, or with status 400 (Bad Request) if the rootPlanSubgroupApproveDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the rootPlanSubgroupApproveDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/root-plan-subgroup-approves", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RootPlanSubgroupApproveDTO> updateRootPlanSubgroupApprove(@Valid @RequestBody RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO) throws URISyntaxException {
		log.debug("REST request to update RootPlanSubgroupApprove : {}", rootPlanSubgroupApproveDTO);
		if (rootPlanSubgroupApproveDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("rootPlanSubgroupApprove", "idNotexists", "RootPlanSubgroupApprove must have an ID")).body(null);
		}
		RootPlanSubgroupApproveDTO result = null;
		Optional<RootPlanSubgroupApproveDTO>optionalRootPlanSubgroupApproveDTO=rootPlanSubgroupApproveService.findOneByUserPidAndAttendanceStatusSubgroupId(rootPlanSubgroupApproveDTO.getUserPid(), rootPlanSubgroupApproveDTO.getAttendanceStatusSubgroupId());
		Optional<RootPlanSubgroupApproveDTO>optionalRootPlanSubgroupApproveDTO1=rootPlanSubgroupApproveService.findOneByUserPidAndId(rootPlanSubgroupApproveDTO.getUserPid(), rootPlanSubgroupApproveDTO.getId());
		if (optionalRootPlanSubgroupApproveDTO.isPresent()) {
			if(optionalRootPlanSubgroupApproveDTO1.get().getId()==optionalRootPlanSubgroupApproveDTO.get().getId()) {
				 result = rootPlanSubgroupApproveService.update(rootPlanSubgroupApproveDTO);
			}else {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("rootPlanSubgroupApprove", "rootPlanSubgroupApproveexists", "RootPlanSubgroupApprove in use")).body(null);
			}
		}else {
			result = rootPlanSubgroupApproveService.update(rootPlanSubgroupApproveDTO);
		}
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("rootPlanSubgroupApprove", "idNotexists", "Invalid RootPlanSubgroupApprove ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("rootPlanSubgroupApprove", rootPlanSubgroupApproveDTO.getId().toString()))
				.body(result);
	}
	
	/**
	 * GET /root-plan-subgroup-approves/:pid : get the "pid" rootPlanSubgroupApprove.
	 *
	 * @param pid
	 *            the pid of the rootPlanSubgroupApproveDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         rootPlanSubgroupApproveDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/root-plan-subgroup-approves/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RootPlanSubgroupApproveDTO> getRootPlanSubgroupApprove(@PathVariable Long id) {
		log.debug("Web request to get RootPlanSubgroupApprove by id : {}", id);
		RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO=rootPlanSubgroupApproveService.findOneId(id);
		return new ResponseEntity<RootPlanSubgroupApproveDTO>(rootPlanSubgroupApproveDTO, HttpStatus.OK);
	}

	/**
	 * DELETE /root-plan-subgroup-approves/:id : delete the "id" rootPlanSubgroupApprove.
	 *
	 * @param id
	 *            the id of the rootPlanSubgroupApproveDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/root-plan-subgroup-approves/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteRootPlanSubgroupApprove(@PathVariable Long id) {
		log.debug("REST request to delete RootPlanSubgroupApprove : {}", id);
		rootPlanSubgroupApproveService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("rootPlanSubgroupApprove", id.toString())).build();
	}

}
