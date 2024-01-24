package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Receivable Payable.
 * 
 * @author Muhammed Riyas T
 * @since November 5, 2016
 */
@Controller
@RequestMapping("/web")
public class ReceivablePayableEditResource {

	private final Logger log = LoggerFactory.getLogger(ReceivablePayableEditResource.class);

	@Inject
	private ReceivablePayableService receivablePayableService;

	@Inject
	private AccountProfileService accountProfileService;

	/**
	 * GET /receivable-payables : get receivable-payables.
	 *
	 * @param model
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) in body
	 */
	@Timed
	@RequestMapping(value = "/receivable-payables-edit", method = RequestMethod.GET)
	public String getReceivablePayables(Model model) {
		log.debug("Web request to get a page of receivable PayableS");
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/receivable-payables-edit";
	}

	/**
	 * GET receivablePayable : get all receivablePayable.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         receivablePayable in body
	 */
	@Timed
	@GetMapping("/receivable-payables-edit/load")
	public ResponseEntity<Map<String, List<ReceivablePayableDTO>>> receivablePayableReport(
			@RequestParam String accountPid) {
		log.debug("REST request to get all receivablePayable");
		List<ReceivablePayableDTO> receivablePayableDTOs = null;
		if (accountPid.equals("no")) {
			receivablePayableDTOs = receivablePayableService.findAllByCompany();
		} else {
			receivablePayableDTOs = receivablePayableService.findAllByAccountProfilePid(accountPid);
		}
		Map<String, List<ReceivablePayableDTO>> result = receivablePayableDTOs.parallelStream()
				.collect(Collectors.groupingBy(ReceivablePayableDTO::getAccountPid));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/receivable-payables-edit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ReceivablePayableDTO> createReceivablePayable(
			@Valid @RequestBody ReceivablePayableDTO receivablePayableDTO) throws URISyntaxException {
		log.debug("Web request to save Receivable Payable : {}", receivablePayableDTO.getAccountName() + " : " + receivablePayableDTO.getAccountPid());
		receivablePayableDTO.setBillOverDue(00L);
		ReceivablePayableDTO result = receivablePayableService.save(receivablePayableDTO);
		return ResponseEntity.created(new URI("/web/receivable-payables-edit/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("receivablePayables", result.getPid().toString()))
				.body(result);
		
	}
	
	@RequestMapping(value = "/receivable-payables-edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ReceivablePayableDTO> updateReceivablePayable(
			@Valid @RequestBody ReceivablePayableDTO receivablePayableDTO) throws URISyntaxException {
		log.debug("Web request to Update Receivable Payable : {}", receivablePayableDTO.getAccountName());
		ReceivablePayableDTO result = receivablePayableService.updateFromPage(receivablePayableDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/receivable-payables-edit/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<ReceivablePayableDTO> getReceivablePayable(@PathVariable String pid) {
		log.debug("Web request to get Receivable Payable by pid : {}", pid);
		return receivablePayableService.findOneByPid(pid)
				.map(receivablePayableDTO -> new ResponseEntity<>(receivablePayableDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/receivable-payables-edit/dueUpdate", method = RequestMethod.GET)
	@Timed
	public @ResponseBody ResponseEntity<Integer> receivablePayableDueUpdate() {
		log.debug("Web request to Update All Overdue Receivable Payable by pid : {}");
		Integer result = receivablePayableService.dueUpdate();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/receivable-payables-edit/{pid}", method = RequestMethod.DELETE)
	@Timed
	public @ResponseBody ResponseEntity<Integer> receivablePayableDelete(@PathVariable String pid) {
		log.debug("Web request to Delete Receivable Payable by pid : {}");
		receivablePayableService.delete(pid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
