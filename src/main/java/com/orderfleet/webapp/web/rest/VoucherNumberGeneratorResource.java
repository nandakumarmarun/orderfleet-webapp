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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.VoucherNumberGeneratorService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class VoucherNumberGeneratorResource {
	private final Logger log = LoggerFactory.getLogger(VoucherNumberGeneratorResource.class);
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private	UserDocumentService userDocumentService;
	
	@Inject
	private VoucherNumberGeneratorService voucherNumberGeneratorService;
	
	/**
	 * GET /voucher-number-generator : get all the Voucher Number Generators.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of filled
	 *         forms in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/voucher-number-generator", method = RequestMethod.GET)
	public String getAllVoucherNumberGenerators(Model model) throws URISyntaxException {
		log.debug("Web request to get Voucher Number Generators page");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		model.addAttribute("VoucherNumberGenerators", voucherNumberGeneratorService.findAllByCompany());
		return "company/voucherNumberGenerator";
	}
	
	@Timed
	@RequestMapping(value = "/voucher-number-generator/loaddocument" , method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "userPid") String userPid) {
		log.debug("Web request to get all document",userPid);
		List<DocumentDTO>documentDTOs=userDocumentService.findDocumentsByUserPid(userPid);
		return documentDTOs;
	}
	
	/**
	 * POST /voucher-number-generator : Create a new VoucherNumberGenerator.
	 *
	 * @param voucherNumberGeneratorDTO
	 *            the voucherNumberGeneratorDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new voucherNumberGeneratorDTO, or with status 400 (Bad Request) if the voucherNumberGenerator
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/voucher-number-generator", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<VoucherNumberGeneratorDTO> createVoucherNumberGenerator(@Valid @RequestBody VoucherNumberGeneratorDTO voucherNumberGeneratorDTO)
			throws URISyntaxException {
		log.debug("Web request to save voucherNumberGenerator : {}", voucherNumberGeneratorDTO);
		if (voucherNumberGeneratorDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("voucherNumberGenerator", "idexists", "A new voucherNumberGenerator cannot already have an ID"))
					.body(null);
		}
		if (voucherNumberGeneratorService.findByPrefix(voucherNumberGeneratorDTO.getPrefix()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("voucherNumberGenerator", "prefixexists", "VoucherNumberGenerator already in use"))
					.body(null);
		}

		VoucherNumberGeneratorDTO result = voucherNumberGeneratorService.save(voucherNumberGeneratorDTO);
		return ResponseEntity.created(new URI("/web/voucher-number-generator"))
				.headers(HeaderUtil.createEntityCreationAlert("voucherNumberGenerator", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /voucher-number-generator : Updates an existing document.
	 *
	 * @param voucherNumberGeneratorDTO
	 *            the voucherNumberGeneratorDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         voucherNumberGeneratorDTO, or with status 400 (Bad Request) if the voucherNumberGeneratorDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         voucherNumberGeneratorDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/voucher-number-generator", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<VoucherNumberGeneratorDTO> updateDocument(@Valid @RequestBody VoucherNumberGeneratorDTO voucherNumberGeneratorDTO)
			throws URISyntaxException {
		log.debug("Web request to update VoucherNumberGenerator : {}", voucherNumberGeneratorDTO);
		if (voucherNumberGeneratorDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("voucherNumberGenerator", "idNotexists", "VoucherNumberGenerator must have an ID"))
					.body(null);
		}
		Optional<VoucherNumberGeneratorDTO> existingVoucherNumberGenerator = voucherNumberGeneratorService.findByPrefix(voucherNumberGeneratorDTO.getPrefix());
		if (existingVoucherNumberGenerator.isPresent() && (!existingVoucherNumberGenerator.get().getId().equals(voucherNumberGeneratorDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("voucherNumberGenerator", "Prefixexists", "voucherNumberGenerator already in use"))
					.body(null);
		}
		
		VoucherNumberGeneratorDTO result = voucherNumberGeneratorService.update(voucherNumberGeneratorDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("voucherNumberGenerator", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("voucherNumberGenerator", voucherNumberGeneratorDTO.getId().toString())).body(result);
	}
	
	/**
	 * GET /voucher-number-generator/:id : get the "id" voucherNumberGenerator.
	 *
	 * @param id
	 *            the id of the voucherNumberGeneratorDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         voucherNumberGeneratorDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/voucher-number-generator/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<VoucherNumberGeneratorDTO> getVoucherNumberGenerator(@PathVariable Long id) {
		log.debug("Web request to get VoucherNumberGenerator by id : {}", id);
		return voucherNumberGeneratorService.findById(id).map(voucherNumberGeneratorDTO -> new ResponseEntity<>(voucherNumberGeneratorDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	/**
	 * DELETE /voucher-number-generator/:id : delete the "id" voucherNumberGenerator.
	 *
	 * @param id
	 *            the id of the voucherNumberGeneratorDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/voucher-number-generator/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteVoucherNumberGenerator(@PathVariable Long id) {
		log.debug("REST request to delete VoucherNumberGenerator : {}", id);
		voucherNumberGeneratorService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("voucherNumberGenerator", id.toString())).build();
	}
}
