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
import com.orderfleet.webapp.service.IncomeExpenseHeadService;
import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing IncomeExpenseHead.
 *
 * @author fahad
 * @since Feb 15, 2017
 */
@Controller
@RequestMapping("/web")
public class IncomeExpenseHeadResource {

	private final Logger log = LoggerFactory.getLogger(IncomeExpenseHeadResource.class);

	@Inject
	private IncomeExpenseHeadService incomeExpenseHeadService;

	/**
	 * POST /income-expense-heads : Create a new incomeExpenseHead.
	 *
	 * @param incomeExpenseHeadDTO
	 *            the incomeExpenseHeadDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new incomeExpenseHeadDTO, or with status 400 (Bad Request) if the
	 *         bank has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/income-expense-heads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<IncomeExpenseHeadDTO> createIncomeExpenseHead(
			@Valid @RequestBody IncomeExpenseHeadDTO incomeExpenseHeadDTO) throws URISyntaxException {
		log.debug("Web request to save IncomeExpenseHead : {}", incomeExpenseHeadDTO);
		if (incomeExpenseHeadDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("incomeExpenseHead", "idexists",
					"A new incomeExpenseHead cannot already have an ID")).body(null);
		}
		if (incomeExpenseHeadService.findByName(incomeExpenseHeadDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("incomeExpenseHead", "nameexists",
					"IncomeExpenseHead already in use")).body(null);
		}
		incomeExpenseHeadDTO.setActivated(true);
		IncomeExpenseHeadDTO result = incomeExpenseHeadService.save(incomeExpenseHeadDTO);
		return ResponseEntity.created(new URI("/web/income-expense-heads/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("incomeExpenseHead", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /banks : Updates an existing incomeExpenseHead.
	 *
	 * @param incomeExpenseHeadDTO
	 *            the incomeExpenseHeadDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         incomeExpenseHeadDTO, or with status 400 (Bad Request) if the
	 *         incomeExpenseHeadDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the incomeExpenseHeadDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/income-expense-heads", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<IncomeExpenseHeadDTO> updateIncomeExpenseHead(
			@Valid @RequestBody IncomeExpenseHeadDTO incomeExpenseHeadDTO) throws URISyntaxException {
		log.debug("REST request to update IncomeExpenseHead : {}", incomeExpenseHeadDTO);
		if (incomeExpenseHeadDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("incomeExpenseHead", "idNotexists",
					"IncomeExpenseHead must have an ID")).body(null);
		}
		Optional<IncomeExpenseHeadDTO> existingIncomeExpenseHead = incomeExpenseHeadService
				.findByName(incomeExpenseHeadDTO.getName());
		if (existingIncomeExpenseHead.isPresent()
				&& (!existingIncomeExpenseHead.get().getPid().equals(incomeExpenseHeadDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("incomeExpenseHead", "nameexists",
					"IncomeExpenseHead already in use")).body(null);
		}
		IncomeExpenseHeadDTO result = incomeExpenseHeadService.update(incomeExpenseHeadDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("incomeExpenseHead", "idNotexists", "Invalid IncomeExpenseHead ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("incomeExpenseHead", incomeExpenseHeadDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /income-expense-heads : get all the incomeExpenseHeads.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         incomeExpenseHeads in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/income-expense-heads", method = RequestMethod.GET)
	@Timed
	public String getAllIncomeExpenseHeads(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of IncomeExpenseHeads");
		List<IncomeExpenseHeadDTO> incomeExpenseHeads = incomeExpenseHeadService
				.findAllByCompanyAndDeactivatedIncomeExpenseHead(true);
		List<IncomeExpenseHeadDTO> deactivatedIncomeExpenseHeads = incomeExpenseHeadService
				.findAllByCompanyAndDeactivatedIncomeExpenseHead(false);
		model.addAttribute("incomeExpenseHeads", incomeExpenseHeads);
		model.addAttribute("deactivatedIncomeExpenseHeads", deactivatedIncomeExpenseHeads);
		return "company/incomeExpenseHeads";
	}

	/**
	 * GET /income-expense-heads/:pid : get the "pid" incomeExpenseHead.
	 *
	 * @param pid
	 *            the pid of the incomeExpenseHeadDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         incomeExpenseHeadDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/income-expense-heads/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<IncomeExpenseHeadDTO> getIncomeExpenseHead(@PathVariable String pid) {
		log.debug("Web request to get IncomeExpenseHead by pid : {}", pid);
		return incomeExpenseHeadService.findOneByPid(pid)
				.map(incomeExpenseHeadDTO -> new ResponseEntity<>(incomeExpenseHeadDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /income-expense-heads/:id : delete the "id" incomeExpenseHead.
	 *
	 * @param id
	 *            the id of the incomeExpenseHeadDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/income-expense-heads/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteIncomeExpenseHead(@PathVariable String pid) {
		log.debug("REST request to delete IncomeExpenseHead : {}", pid);
		incomeExpenseHeadService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("incomeExpenseHead", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017 UPDATE STATUS
	 *        /income-expense-heads/changeStatus:incomeExpenseHeadDTO : update
	 *        status of incomeExpenseHead.
	 * 
	 * @param incomeExpenseHeadDTO
	 *            the incomeExpenseHeadDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/income-expense-heads/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IncomeExpenseHeadDTO> updateActivityGroupStatus(
			@Valid @RequestBody IncomeExpenseHeadDTO incomeExpenseHeadDTO) {
		log.debug("request to update bank status", incomeExpenseHeadDTO);
		IncomeExpenseHeadDTO res = incomeExpenseHeadService.updateIncomeExpenseHeadStatus(incomeExpenseHeadDTO.getPid(),
				incomeExpenseHeadDTO.getActivated());
		return new ResponseEntity<IncomeExpenseHeadDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /income-expense-heads/activateIncomeExpenseHead :
	 *        activate status of incomeExpenseHeads.
	 * 
	 * @param incomeExpenseHeads
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/income-expense-heads/activateIncomeExpenseHead", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IncomeExpenseHeadDTO> activateIncomeExpenseHead(
			@Valid @RequestParam String incomeExpenseHeads) {
		log.debug("request to activate incomeExpenseHead ");
		String[] incomeExpenseHeadarray = incomeExpenseHeads.split(",");
		for (String incomeExpenseHead : incomeExpenseHeadarray) {
			incomeExpenseHeadService.updateIncomeExpenseHeadStatus(incomeExpenseHead, true);
		}
		return new ResponseEntity<IncomeExpenseHeadDTO>(HttpStatus.OK);
	}
}
