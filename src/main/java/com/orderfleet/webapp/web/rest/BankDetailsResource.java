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
import com.orderfleet.webapp.service.BankDetailsService;

import com.orderfleet.webapp.web.rest.dto.BankDetailsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing BankDetails.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Controller
@RequestMapping("/web")
public class BankDetailsResource {

	private final Logger log = LoggerFactory.getLogger(BankDetailsResource.class);

	@Inject
	private BankDetailsService bankDetailsService;

	/**
	 * POST /bankDetails : Create a new bankDetails.
	 *
	 * @param bankDetailsDTO the bankDetailsDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         bankDetailsDTO, or with status 400 (Bad Request) if the bankDetails
	 *         has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/bankDetails", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<BankDetailsDTO> createBank(@Valid @RequestBody BankDetailsDTO bankDetailsDTO)
			throws URISyntaxException {
		log.debug("Web request to save BankDetails : {}", bankDetailsDTO);
		if (bankDetailsDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bankDetails", "idexists",
					"A new bankDetails cannot already have an ID")).body(null);
		}
//		if (bankDetailsService.findByName(bankDetailsDTO.getName()).isPresent()) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("bankDetails", "nameexists", "BankDetails already in use")).body(null);
//		}
		// bankDetailsDTO.setActivated(true);
		BankDetailsDTO result = bankDetailsService.save(bankDetailsDTO);
		return ResponseEntity.created(new URI("/web/bankDetails/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("bankDetails", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /bankDetails : Updates an existing bankDetails.
	 *
	 * @param bankDetailsDTO the bankDetailsDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         bankDetailsDTO, or with status 400 (Bad Request) if the
	 *         bankDetailsDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the bankDetailsDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/bankDetails", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BankDetailsDTO> updateBank(@Valid @RequestBody BankDetailsDTO bankDetailsDTO)
			throws URISyntaxException {
		log.debug("REST request to update BankDetails : {}", bankDetailsDTO);
		if (bankDetailsDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bankDetails", "idNotexists", "BankDetails must have an ID"))
					.body(null);
		}
//		Optional<BankDetailsDTO> existingBank = bankDetailsService.findByName(bankDetailsDTO.getName());
//		if (existingBank.isPresent() && (!existingBank.get().getPid().equals(bankDetailsDTO.getPid()))) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("bankDetails", "nameexists", "BankDetails already in use")).body(null);
//		}
		BankDetailsDTO result = bankDetailsService.update(bankDetailsDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bankDetails", "idNotexists", "Invalid BankDetails ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("bankDetails", bankDetailsDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /bankDetails : get all the bankDetails.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of bankDetails
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/bankDetails", method = RequestMethod.GET)
	@Timed
	public String getAllBanks(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Banks");
		List<BankDetailsDTO> bankDetails = bankDetailsService.findAllByCompany();
		model.addAttribute("bankDetails", bankDetails);
		return "company/bankDetails";
	}

	/**
	 * GET /bankDetails/:pid : get the "pid" bankDetails.
	 *
	 * @param pid the pid of the bankDetailsDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         bankDetailsDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/bankDetails/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BankDetailsDTO> getBank(@PathVariable String pid) {
		log.debug("Web request to get BankDetails by pid : {}", pid);
//		return bankDetailsService.findOneByPid(pid).map(bankDetailsDTO -> new ResponseEntity<>(bankDetailsDTO, HttpStatus.OK))
//				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		return null;
	}

	/**
	 * DELETE /bankDetails/:id : delete the "id" bankDetails.
	 *
	 * @param id the id of the bankDetailsDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/bankDetails/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteBank(@PathVariable String pid) {
		log.debug("REST request to delete BankDetails : {}", pid);
		bankDetailsService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bankDetails", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /bankDetails/changeStatus:bankDetailsDTO : update status
	 *        of bankDetails.
	 * 
	 * @param bankDetailsDTO the bankDetailsDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/bankDetails/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankDetailsDTO> updateActivityGroupStatus(@Valid @RequestBody BankDetailsDTO bankDetailsDTO) {
		log.debug("request to update bankDetails status", bankDetailsDTO);
		// BankDetailsDTO res =
		// bankDetailsService.updateBankStatus(bankDetailsDTO.getPid(),
		// bankDetailsDTO.getActivated());
		// return new ResponseEntity<BankDetailsDTO>(res, HttpStatus.OK);
		return null;
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /bankDetails/activateBank : activate status of
	 *        bankDetails.
	 * 
	 * @param bankDetails the bankDetails to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/bankDetails/activateBank", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankDetailsDTO> activateBank(@Valid @RequestParam String bankDetails) {
		log.debug("request to activate bankDetails ");
		String[] bankarray = bankDetails.split(",");
		for (String bankDetails1 : bankarray) {
			// bankDetailsService.updateBankStatus(bankDetails1, true);
		}
		return new ResponseEntity<BankDetailsDTO>(HttpStatus.OK);
	}
}
