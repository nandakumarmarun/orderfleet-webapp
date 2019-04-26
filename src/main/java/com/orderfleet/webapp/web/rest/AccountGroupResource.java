package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.service.AccountGroupAccountProfileService;
import com.orderfleet.webapp.service.AccountGroupService;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

import javassist.expr.NewArray;

/**
 * Web controller for managing AccountGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Controller
@RequestMapping("/web")
public class AccountGroupResource {

	private final Logger log = LoggerFactory.getLogger(AccountGroupResource.class);

	@Inject
	private AccountGroupService accountGroupService;

	@Inject
	private AccountGroupAccountProfileService accountGroupAccountProfileService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	/**
	 * POST /accountGroups : Create a new accountGroup.
	 *
	 * @param accountGroupDTO
	 *            the accountGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         accountGroupDTO, or with status 400 (Bad Request) if the accountGroup
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the AccountGroup URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AccountGroupDTO> createAccountGroup(@Valid @RequestBody AccountGroupDTO accountGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save AccountGroup : {}", accountGroupDTO);
		if (accountGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountGroup", "idexists",
					"A new accountGroup cannot already have an ID")).body(null);
		}
		if (accountGroupService.findByName(accountGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "nameexists", "AccountGroup already in use"))
					.body(null);
		}
		accountGroupDTO.setActivated(true);
		AccountGroupDTO result = accountGroupService.save(accountGroupDTO);
		return ResponseEntity.created(new URI("/web/accountGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("accountGroup", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /accountGroups : Updates an existing accountGroup.
	 *
	 * @param accountGroupDTO
	 *            the accountGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         accountGroupDTO, or with status 400 (Bad Request) if the
	 *         accountGroupDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the accountGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the AccountGroup URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountGroupDTO> updateAccountGroup(@Valid @RequestBody AccountGroupDTO accountGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update AccountGroups : {}", accountGroupDTO);
		if (accountGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountGroup", "idNotexists", "AccountGroup must have an ID"))
					.body(null);
		}
		Optional<AccountGroupDTO> existingAccountGroup = accountGroupService.findByName(accountGroupDTO.getName());
		if (existingAccountGroup.isPresent()
				&& (!existingAccountGroup.get().getPid().equals(accountGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "nameexists", "AccountGroup already in use"))
					.body(null);
		}
		AccountGroupDTO result = accountGroupService.update(accountGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "idNotexists", "Invalid AccountGroup ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("accountGroup", accountGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /accountGroups : get all the accountGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of accountGroups
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/accountGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of AccountGroups");
		model.addAttribute("accountsGroups", accountGroupService.findAllByCompanyAndDeactivated(true));
		model.addAttribute("deactivatedGroups", accountGroupService.findAllByCompanyAndDeactivated(false));
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
		System.out.println(accountProfiles.size() + "----------------");
		if (!accountProfiles.isEmpty()) {
			List<AccountProfileDTO> accountProfileDtos = new ArrayList<>();

			for (AccountProfile accountProfile : accountProfiles) {
				AccountProfileDTO accDto = new AccountProfileDTO();
				accDto.setPid(accountProfile.getPid());
				accDto.setAlias(accountProfile.getAlias());
				accDto.setName(accountProfile.getName());
				accDto.setDescription(accountProfile.getDescription());
				accDto.setAddress(accountProfile.getAddress());
				if (accountProfile.getUser() != null) {
					accDto.setUserName(accountProfile.getUser().getFirstName());
				} else {
					accDto.setUserName("-");
				}
				accountProfileDtos.add(accDto);
			}

			model.addAttribute("accountProfiles", accountProfileDtos);
		}
		return "company/accountGroups";
	}

	/**
	 * GET /accountGroups/:id : get the "id" accountGroup.
	 *
	 * @param id
	 *            the id of the accountGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accountGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountGroupDTO> getAccountGroup(@PathVariable String pid) {
		log.debug("Web request to get AccountGroup by pid : {}", pid);
		return accountGroupService.findOneByPid(pid)
				.map(accountGroupDTO -> new ResponseEntity<>(accountGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * 
	 * 
	 * DELETE /accountGroups/:id : delete the "id" accountGroup.
	 *
	 * @param id
	 *            the id of the accountGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/accountGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAccountGroup(@PathVariable String pid) {
		log.debug("REST request to delete AccountGroup : {}", pid);
		accountGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("accountGroup", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since feb 6, 2017
	 * 
	 *        UPDATE STATUS /accountGroups/change:accountGroupDTO : update status of
	 *        accountGroup.
	 * 
	 * @param accountGroupDTO
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/accountGroups/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountGroupDTO> updateAccountGroupStatus(
			@Valid @RequestBody AccountGroupDTO accountGroupDTO) {
		log.debug("Web request to update status of AccountGroup ", accountGroupDTO);
		AccountGroupDTO res = accountGroupService.updateAccountGroupStatus(accountGroupDTO.getPid(),
				accountGroupDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since feb 10, 2017
	 * 
	 *        updateActivateAccountTypeStatus
	 *        /accountGroups/activateAccountGroup:accountgroup : Activate Account
	 *        Group.
	 * 
	 * @param accountgroup
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/accountGroups/activateAccountGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountTypeDTO> updateActivateAccountGroupStatus(@Valid @RequestParam String accountgroup) {
		log.debug("REST request to update status of  accountGroup : {}", accountgroup);
		String[] accountGroup = accountgroup.split(",");
		for (String accountgroupPid : accountGroup) {
			accountGroupService.updateAccountGroupStatus(accountgroupPid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/accountGroups/accounts", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> accountGroupAccountProfiles(@RequestParam String accountGroupPid) {
		log.debug("REST request to accountGroup Account Profiles : {}", accountGroupPid);
		List<AccountProfileDTO> accountProfileDTOs = accountGroupAccountProfileService
				.findAccountProfileByAccountGroupPid(accountGroupPid);
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/accountGroups/assign-accounts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(@RequestParam String accountGroupPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", accountGroupPid);
		accountGroupAccountProfileService.save(accountGroupPid, assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
