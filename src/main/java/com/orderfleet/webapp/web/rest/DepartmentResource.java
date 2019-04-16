package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DepartmentService;

import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Department.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Controller
@RequestMapping("/web")
public class DepartmentResource {

	private final Logger log = LoggerFactory.getLogger(DepartmentResource.class);

	@Inject
	private DepartmentService departmentService;

	/**
	 * POST /departments : Create a new department.
	 *
	 * @param departmentDTO
	 *            the departmentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new departmentDTO, or with status 400 (Bad Request) if the
	 *         department has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/departments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO)
			throws URISyntaxException {
		log.debug("Web request to save Department : {}", departmentDTO);
		if (departmentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("department", "idexists",
					"A new department cannot already have an ID")).body(null);
		}
		if (departmentService.findByName(departmentDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("department", "nameexists", "Department already in use"))
					.body(null);
		}
		departmentDTO.setActivated(true);
		DepartmentDTO result = departmentService.save(departmentDTO);
		return ResponseEntity.created(new URI("/web/departments/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("department", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /departments : Updates an existing department.
	 *
	 * @param departmentDTO
	 *            the departmentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         departmentDTO, or with status 400 (Bad Request) if the
	 *         departmentDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the departmentDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/departments", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DepartmentDTO> updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO)
			throws URISyntaxException {
		log.debug("Web request to update Department : {}", departmentDTO);
		if (departmentDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("department", "idNotexists", "Department must have an ID"))
					.body(null);
		}
		Optional<DepartmentDTO> existingDepartment = departmentService.findByName(departmentDTO.getName());
		if (existingDepartment.isPresent() && (!existingDepartment.get().getPid().equals(departmentDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("department", "nameexists", "Department already in use"))
					.body(null);
		}
		DepartmentDTO result = departmentService.update(departmentDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("department", "idNotexists", "Invalid Department ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("department", departmentDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /departments : get all the departments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         departments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/departments", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDepartments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Departments");
		model.addAttribute("departments",
				departmentService.findAllByCompanyAndDeactivatedDepartment( true));
		model.addAttribute("deactivatedDepartments", departmentService.findAllByCompanyAndDeactivatedDepartment(false));
		return "company/departments";
	}

	/**
	 * GET /departments/:id : get the "id" department.
	 *
	 * @param id
	 *            the id of the departmentDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         departmentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/departments/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable String pid) {
		log.debug("Web request to get Department by pid : {}", pid);
		return departmentService.findOneByPid(pid)
				.map(departmentDTO -> new ResponseEntity<>(departmentDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /departments/:id : delete the "id" department.
	 *
	 * @param id
	 *            the id of the departmentDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/departments/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDepartment(@PathVariable String pid) {
		log.debug("REST request to delete Department : {}", pid);
		departmentService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("department", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /departments/changeStatus:DepartmentDTO : update
	 *        status of Department.
	 * 
	 * @param DepartmentDTO
	 *            the DepartmentDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/departments/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DepartmentDTO> updateDepartmentStatus(@Valid @RequestBody DepartmentDTO departmentDTO) {
		log.debug("Web request to update status Department : {}", departmentDTO);
		DepartmentDTO res = departmentService.updateDepartmentStatus(departmentDTO.getPid(),
				departmentDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /departments/activateDepartment : activate status
	 *        of Department.
	 * 
	 * @param departments
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/departments/activateDepartment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DepartmentDTO> activateDepartment(@Valid @RequestParam String departments) {
		log.debug("Web request to activate status of Department ");
		String[] department = departments.split(",");
		for (String depart : department) {
			departmentService.updateDepartmentStatus(depart, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
