package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class EmployeeHierarchyResource {

	private final Logger log = LoggerFactory.getLogger(EmployeeHierarchyResource.class);

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	/**
	 * POST /employee-hierarchy : Create a new employee hierarchy.
	 *
	 * @param employeePid
	 * @param parentPid
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new EmployeeHierarchyDTO,
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/employee-hierarchy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<EmployeeHierarchyDTO> createEmployeeHierarchy(@RequestParam("employeePid") String employeePid,
			@RequestParam("parentPid") String parentPid) throws URISyntaxException {
		log.debug("Web request to create Employee Hierarchy ");
		log.debug("employeePid :- " + employeePid);
		log.debug("parentPid :- " + parentPid);
		if (employeeHierarchyService.findOneByEmployeeIdAndActivatedTrue(employeePid) != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("employeeHierarchy", "employee", "Employee already in use"))
					.body(null);
		}
		EmployeeHierarchyDTO result = employeeHierarchyService.save(employeePid, parentPid);
		return ResponseEntity.created(new URI("/web/employee-hierarchy/" + result.getEmployeeId()))
				.headers(HeaderUtil.createEntityCreationAlert("employeeHierarchy", result.getEmployeeId().toString()))
				.body(result);
	}

	@RequestMapping(value = "/employee-hierarchical-view", method = RequestMethod.GET)
	@Timed
	public String getEmployeeHierarchyViewPage(Model model) throws URISyntaxException  {
		log.debug("Web request to get a page of EmployeeHierarchyViewPage");
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		
		return "company/employee-hierarchy-view";
	}
	
	@RequestMapping(value = "/set-employee-hierarchy-root", method = RequestMethod.GET)
	@Timed
	public String getEmployeeHierarchyRootPage(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of EmployeeHierarchyRootPage");
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		model.addAttribute("root", employeeHierarchyService.findEmployeeHierarchyRootByCompany());
		return "company/set-employee-hierarchy";
	}

	@RequestMapping(value = "/set-employee-hierarchy-root", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<EmployeeHierarchyDTO> createEmployeeHierarchyRoot(@RequestParam("employeePid") String employeePid,
			@RequestParam("parentPid") String parentPid) throws URISyntaxException {
		log.debug("Web request to create Employee Hierarchy ");
		log.debug("employeePid :- " + employeePid);
		log.debug("parentPid :- " + parentPid);
		if (employeeHierarchyService.findOneByEmployeeIdAndActivatedTrue(employeePid) != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("employeeHierarchy", "employee assigned", "Employee already in use"))
					.body(null);
		}
		EmployeeHierarchyDTO result = employeeHierarchyService.saveRoot(employeePid, parentPid);
		return ResponseEntity.created(new URI("/web/set-employee-hierarchy-root" ))
				.headers(HeaderUtil.createEntityCreationAlert("employeeHierarchy", result.getEmployeeId().toString()))
				.body(result);
	}
	
	@RequestMapping(value = "/set-employee-hierarchy-root", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeHierarchyDTO> updateEmployeeHierarchyRoot(@RequestParam("employeePid") String employeePid,
			@RequestParam("parentPid") String parentPid) throws URISyntaxException {
		log.debug("Web request to update Employee Hierarchy ");
		log.debug("employeePid :- " + employeePid);
		log.debug("parentPid :- " + parentPid);
		EmployeeHierarchyDTO employeeHierarchyDTO=employeeHierarchyService.findOneByEmployeeIdAndActivatedTrue(parentPid);
		if(employeeHierarchyService.countByParentIdAndActivatedTrue(employeeHierarchyDTO.getEmployeePid())!=null){
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("employeeHierarchy", "assigned to employees", "Root already assigned"))
					.body(null);
		}
		EmployeeHierarchyDTO employeeHierarchyDTO1=employeeHierarchyService.findOneByEmployeeIdAndActivatedTrue(employeePid);
		if(employeeHierarchyDTO1!=null){
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("employeeHierarchy", "employee assigned", "Employee already in use"))
					.body(null);
		}
		employeeHierarchyService.deleteByEmployeePid(parentPid);
		EmployeeHierarchyDTO result = employeeHierarchyService.saveRoot(employeePid, parentPid);
		return ResponseEntity.created(new URI("/web/set-employee-hierarchy-root" ))
				.headers(HeaderUtil.createEntityCreationAlert("employeeHierarchy", result.getEmployeeId().toString()))
				.body(result);
	}
	
	@RequestMapping(value = "/set-employee-hierarchy-root/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<EmployeeHierarchyDTO> getEmployeeHierarchieByPid(@PathVariable String id) throws URISyntaxException {
		log.debug("REST request to get EmployeeHierarchies");
		EmployeeHierarchyDTO employeeHierarchyDTO = employeeHierarchyService.findOneByEmployeeIdAndActivatedTrue(id);
		return new ResponseEntity<>(employeeHierarchyDTO, HttpStatus.OK);
	}
	
	/**
	 * GET /employee-hierarchical-view : Get all activated employee from
	 * hierarchy and show in hierarchical view. This is a REST request.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         employeeHierarchies in body
	 */
	@RequestMapping(value = "/employee-hierarchical-data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<EmployeeHierarchyDTO>> getAllEmployeeHierarchies() throws URISyntaxException {
		log.debug("REST request to get EmployeeHierarchies");
		List<EmployeeHierarchyDTO> employeeHierarchyDTOs = employeeHierarchyService.findAllByCompanyAndActivatedTrue();
		return new ResponseEntity<>(employeeHierarchyDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/employee-hierarchical-data/employee/{pid}", method = RequestMethod.GET)
	@Timed
	public String getEmployeeFromEmployeeHierarchy(@PathVariable String pid, Model model) {
		log.debug("Web request to get EmployeeHierarchy Employee by pid : {}", pid);
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(pid);
		if (employeeProfileDTO.isPresent()) {
			EmployeeHierarchyDTO employeeHierarchyDTO = employeeHierarchyService
					.findOneByEmployeeIdAndActivatedTrue(employeeProfileDTO.get().getPid());
			if (employeeHierarchyDTO != null) {
				model.addAttribute("employee", employeeProfileDTO.get());
				model.addAttribute("parentEmployeePid", employeeHierarchyDTO.getParentPid());
				List<EmployeeProfileDTO>employeeProfileDTOs=employeeProfileService.findAllByCompany();
				List<EmployeeProfileDTO>newList=new ArrayList<>();
				for(EmployeeProfileDTO employeeProfileDTO2:employeeProfileDTOs){
					if(employeeProfileDTO2.getPid().equals(employeeProfileDTO.get().getPid())){
						
					}else{
						newList.add(employeeProfileDTO2);
					}
				}
				model.addAttribute("employees", newList);
			}
		}
		return "company/employee-hierarchy-edit";
	}

	/**
	 * PUT /employee-hierarchy : Updates an existing employeeHierarchy.
	 *
	 * @param employeeHierarchyDTO
	 *            the employeeHierarchyDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         employeeHierarchyDTO, or with status 400 (Bad Request) if the
	 *         employeeHierarchyDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the employeeHierarchyDTO couldn't be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/employee-hierarchical-view", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeHierarchyDTO> updateEmployeeHierarchy(@RequestParam("employeeId") String employeeId,
			@RequestParam("parentId") String parentId) throws URISyntaxException {
		log.debug("REST request to update EmployeeHierarchy by employeeId : {}", employeeId);
		if (StringUtils.isEmpty(employeeId)) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("employeeHierarchy", "idNotexists",
					"employeeHierarchy must have an ID")).body(null);
		}
		if (StringUtils.isEmpty(parentId)) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("employeeHierarchy", "idNotexists",
					"employeeHierarchy must have an Parent ID")).body(null);
		}
		// if this employee has active children. Then do not update
		/*
		 * Long children =
		 * employeeHierarchyService.countByParentIdAndActivatedTrue(employeeId); if
		 * (children != null) { return
		 * ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(
		 * "employeeHierarchy", "childrenexists",
		 * "This Employee cannot updated. It has active children.")).body(null); }
		 */
		// update
		EmployeeHierarchyDTO result = employeeHierarchyService.update(employeeId, parentId);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("employeeHierarchy", "idNotexists", "Invalid Employee or parent"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("employeeHierarchy", employeeId.toString())).body(result);
	}

	/**
	 * DELETE /employee-hierarchy/:id : delete the "id" employeeHierarchy.
	 *
	 * @param id
	 *            the id of the employeeHierarchyDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/employee-hierarchical-view/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> softDeleteEmployeeHierarchy(@PathVariable String id) {
		log.debug("REST request to soft delete EmployeeHierarchy by pid : {}", id);

		if (!StringUtils.isEmpty(id)) {
			// if this employee has active children. Then do not update
			Long children = employeeHierarchyService.countByParentIdAndActivatedTrue(id);
			if (children != null) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("employeeHierarchy",
						"childrenexists", "This Employee cannot delete. It has active children")).body(null);
			}
			employeeHierarchyService.inactivateEmployeeHierarchy(id);
		}

		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("employeeHierarchy", id.toString()))
				.build();
	}

}
