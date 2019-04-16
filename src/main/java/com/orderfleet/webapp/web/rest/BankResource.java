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
import com.orderfleet.webapp.service.BankService;

import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Bank.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Controller
@RequestMapping("/web")
public class BankResource {

	private final Logger log = LoggerFactory.getLogger(BankResource.class);

	@Inject
	private BankService bankService;

	/**
	 * POST /banks : Create a new bank.
	 *
	 * @param bankDTO
	 *            the bankDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new bankDTO, or with status 400 (Bad Request) if the bank has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/banks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<BankDTO> createBank(@Valid @RequestBody BankDTO bankDTO) throws URISyntaxException {
		log.debug("Web request to save Bank : {}", bankDTO);
		if (bankDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bank", "idexists", "A new bank cannot already have an ID"))
					.body(null);
		}
		if (bankService.findByName(bankDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bank", "nameexists", "Bank already in use")).body(null);
		}
		bankDTO.setActivated(true);
		BankDTO result = bankService.save(bankDTO);
		return ResponseEntity.created(new URI("/web/banks/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("bank", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /banks : Updates an existing bank.
	 *
	 * @param bankDTO
	 *            the bankDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         bankDTO, or with status 400 (Bad Request) if the bankDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the bankDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/banks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BankDTO> updateBank(@Valid @RequestBody BankDTO bankDTO) throws URISyntaxException {
		log.debug("REST request to update Bank : {}", bankDTO);
		if (bankDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bank", "idNotexists", "Bank must have an ID")).body(null);
		}
		Optional<BankDTO> existingBank = bankService.findByName(bankDTO.getName());
		if (existingBank.isPresent() && (!existingBank.get().getPid().equals(bankDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bank", "nameexists", "Bank already in use")).body(null);
		}
		BankDTO result = bankService.update(bankDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("bank", "idNotexists", "Invalid Bank ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("bank", bankDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /banks : get all the banks.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of banks in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/banks", method = RequestMethod.GET)
	@Timed
	public String getAllBanks(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Banks");
		List<BankDTO> banks = bankService.findAllByCompanyAndDeactivatedBank(true);
		List<BankDTO> deactivatedBanks = bankService.findAllByCompanyAndDeactivatedBank(false);
		model.addAttribute("banks", banks);
		model.addAttribute("deactivatedBanks", deactivatedBanks);
		return "company/banks";
	}

	/**
	 * GET /banks/:pid : get the "pid" bank.
	 *
	 * @param pid
	 *            the pid of the bankDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         bankDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/banks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BankDTO> getBank(@PathVariable String pid) {
		log.debug("Web request to get Bank by pid : {}", pid);
		return bankService.findOneByPid(pid).map(bankDTO -> new ResponseEntity<>(bankDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /banks/:id : delete the "id" bank.
	 *
	 * @param id
	 *            the id of the bankDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/banks/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteBank(@PathVariable String pid) {
		log.debug("REST request to delete Bank : {}", pid);
		bankService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bank", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /banks/changeStatus:bankDTO : update status of bank.
	 * 
	 * @param bankDTO
	 *            the bankDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/banks/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankDTO> updateActivityGroupStatus(@Valid @RequestBody BankDTO bankDTO) {
		log.debug("request to update bank status", bankDTO);
		BankDTO res = bankService.updateBankStatus(bankDTO.getPid(), bankDTO.getActivated());
		return new ResponseEntity<BankDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /banks/activateBank : activate status of banks.
	 * 
	 * @param banks
	 * 				the banks to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/banks/activateBank", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankDTO> activateBank(@Valid @RequestParam String banks) {
		log.debug("request to activate bank ");
		String[] bankarray = banks.split(",");
		for (String bank : bankarray) {
			bankService.updateBankStatus(bank, true);
		}
		return new ResponseEntity<BankDTO>(HttpStatus.OK);
	}
}
