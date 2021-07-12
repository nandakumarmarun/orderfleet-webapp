package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.repository.AccountActivityTaskConfigRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountActivityTaskConfigService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.SalesLedgerService;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesLedger.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */

@Controller
@RequestMapping("/web")
public class SalesLedgerResource {

	private final Logger log = LoggerFactory.getLogger(SalesLedgerResource.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	/**
	 * POST /salesLedgers : Create a new salesLedger.
	 *
	 * @param salesLedgerDTO the salesLedgerDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         salesLedgerDTO, or with status 400 (Bad Request) if the salesLedger
	 *         has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesLedgers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesLedgerDTO> createSalesLedger(@Valid @RequestBody SalesLedgerDTO salesLedgerDTO)
			throws URISyntaxException {
		log.debug("Web request to save SalesLedger : {}", salesLedgerDTO);
		if (salesLedgerDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesLedger", "idexists",
					"A new salesLedger cannot already have an ID")).body(null);
		}
		if (salesLedgerRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), salesLedgerDTO.getName())
				.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("salesLedger", "nameexists", "Account type already in use"))
					.body(null);
		}
		salesLedgerDTO.setActivated(true);
		SalesLedgerDTO result = salesLedgerSave(salesLedgerDTO);
		return ResponseEntity.created(new URI("/web/salesLedgers/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("salesLedger", result.getPid().toString())).body(result);
	}

	private SalesLedgerDTO salesLedgerSave(SalesLedgerDTO salesLedgerDTO) {
		SalesLedger salesLedger = new SalesLedger();
		salesLedger.setPid(SalesLedgerService.PID_PREFIX + RandomUtil.generatePid());

		salesLedger.setName(salesLedgerDTO.getName());
		salesLedger.setAlias(salesLedgerDTO.getAlias());
		salesLedger.setDescription(salesLedgerDTO.getDescription());
		salesLedger.setActivated(salesLedgerDTO.getActivated());
		salesLedger.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesLedger = salesLedgerRepository.save(salesLedger);
		SalesLedgerDTO result = salesLedgerToSalesLedgerDTO(salesLedger);
		return result;
	}

	private SalesLedgerDTO salesLedgerToSalesLedgerDTO(SalesLedger salesLedger) {
		SalesLedgerDTO salesLedgerDTO = new SalesLedgerDTO();
		salesLedgerDTO.setPid(salesLedger.getPid());
		salesLedgerDTO.setActivated(salesLedger.getActivated());
		salesLedgerDTO.setName(salesLedger.getName());
		salesLedgerDTO.setAlias(salesLedger.getAlias());
		salesLedgerDTO.setDescription(salesLedger.getDescription());

		return salesLedgerDTO;
	}

	/**
	 * PUT /salesLedgers : Updates an existing salesLedger.
	 *
	 * @param salesLedgerDTO the salesLedgerDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         salesLedgerDTO, or with status 400 (Bad Request) if the
	 *         salesLedgerDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the salesLedgerDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesLedgers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesLedgerDTO> updateSalesLedger(@Valid @RequestBody SalesLedgerDTO salesLedgerDTO)
			throws URISyntaxException {
		log.debug("Web request to update SalesLedger : {}", salesLedgerDTO);
		if (salesLedgerDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("salesLedger", "idNotexists", "Account Type must have an ID"))
					.body(null);
		}
		Optional<SalesLedger> existingSalesLedger = salesLedgerRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), salesLedgerDTO.getName());
		if (existingSalesLedger.isPresent() && (!existingSalesLedger.get().getPid().equals(salesLedgerDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("salesLedger", "nameexists", "Account type already in use"))
					.body(null);
		}
		salesLedgerDTO.setActivated(true);
		SalesLedgerDTO result = salesLedgerupdate(salesLedgerDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("salesLedger", "idNotexists", "Invalid account type ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("salesLedger", salesLedgerDTO.getPid().toString()))
				.body(result);
	}

	private SalesLedgerDTO salesLedgerupdate(SalesLedgerDTO salesLedgerDTO) {

		return salesLedgerRepository.findOneByPid(salesLedgerDTO.getPid()).map(salesLedger -> {
			salesLedger.setName(salesLedgerDTO.getName());
			salesLedger.setAlias(salesLedgerDTO.getAlias());
			salesLedger.setDescription(salesLedgerDTO.getDescription());
			salesLedger.setActivated(salesLedgerDTO.getActivated());
			salesLedger = salesLedgerRepository.save(salesLedger);
			SalesLedgerDTO result = salesLedgerToSalesLedgerDTO(salesLedger);
			return result;
		}).orElse(null);
	}

	/**
	 * GET /salesLedgers : get all the salesLedgers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of salesLedgers
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/salesLedgers", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesLedgers(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesLedgers");

		List<SalesLedger> salesLedgers = salesLedgerRepository.findAllCompanyAndSalesLedgerActivated(true);

		List<SalesLedgerDTO> salesLedgerDTOs = convertSalesLedgerToSalesLedgerDtoList(salesLedgers);
		model.addAttribute("salesLedgers", salesLedgerDTOs);

		return "company/salesLedgers";
	}

	private List<SalesLedgerDTO> convertSalesLedgerToSalesLedgerDtoList(List<SalesLedger> salesLedgers) {
		List<SalesLedgerDTO> salesLedgerDTOs = new ArrayList<>();

		for (SalesLedger salesLedger : salesLedgers) {
			salesLedgerDTOs.add(salesLedgerToSalesLedgerDTO(salesLedger));
		}
		return salesLedgerDTOs;
	}

//	/**
//	 * GET /salesLedgers/:id : get the "id" salesLedger.
//	 *
//	 * @param id the id of the salesLedgerDTO to retrieve
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         salesLedgerDTO, or with status 404 (Not Found)
//	 */
	@RequestMapping(value = "/salesLedgers/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesLedgerDTO> getSalesLedger(@PathVariable String pid) {
		log.debug("Web request to get SalesLedger by pid : {}", pid);

		Optional<SalesLedger> salesLedger = salesLedgerRepository.findOneByPid(pid);
		if (salesLedger.isPresent()) {
			return new ResponseEntity<>(salesLedgerToSalesLedgerDTO(salesLedger.get()), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * DELETE /salesLedgers/:id : delete the "id" salesLedger. *
	 * 
	 * @param id the id of the salesLedgerDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/salesLedgers/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteSalesLedger(@PathVariable String pid) {
		log.debug("REST request to delete SalesLedger : {}", pid);
		salesLedgerRepository.deleteOneByPid(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("salesLedger", pid.toString())).build();
	}

//	@RequestMapping(value = "/salesLedgers/assignAccounts", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedAccounts(@RequestParam String pid, @RequestParam String assignedAccounts) {
//		log.debug("REST request to save assigned account type : {}", pid);
//		String[] accounts = assignedAccounts.split(",");
//		for (String accountPid : accounts) {
//			AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountPid).get();
//			accountProfileDTO.setSalesLedgerPid(pid);
//			accountProfileService.update(accountProfileDTO);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/assignActivities", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedActivities(@RequestParam String pid,
//			@RequestParam String assignedActivities) {
//
//		log.debug("REST request to save assigned account type : {}", pid);
//		activityConfigRepository.deleteBySalesLedgerPidAndCompanyId(pid, SecurityUtils.getCurrentUsersCompanyId());
//		String[] activity = assignedActivities.split(",");
//		for (String activityPid : activity) {
//			String actypid = activityPid.split("~")[0].toString();
//			boolean assignNotification = activityPid.split("~")[1].toString().equals("true") ? true : false;
//			AccountActivityTaskConfigDTO activityAccountTaskDTO = new AccountActivityTaskConfigDTO();
//			activityAccountTaskDTO.setSalesLedgerPid(pid);
//			activityAccountTaskDTO.setActivityPid(actypid);
//			activityAccountTaskDTO.setAssignNotification(assignNotification);
//			activitySalesLedgerConfigService.saveActivitySalesLedgerConfig(activityAccountTaskDTO);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/assignAssociatedSalesLedgers", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedSalesLedgers(@RequestParam String pid,
//			@RequestParam String assignedSalesLedgers) {
//
//		log.debug("REST request to save assigned account types : {}", pid);
//		salesLedgerAssociationRepository.deleteBySalesLedgerPidAndCompanyId(pid,
//				SecurityUtils.getCurrentUsersCompanyId());
//		String[] associatedSalesLedger = assignedSalesLedgers.split(",");
//		for (String associatedSalesLedgerPid : associatedSalesLedger) {
//			String aatpid = associatedSalesLedgerPid.split("~")[0].toString();
//
//			SalesLedgerAssociation salesLedgerAssociation = new SalesLedgerAssociation();
//
//			Optional<SalesLedger> opSalesLedger = salesLedgerRepository.findOneByPid(pid);
//			Optional<SalesLedger> opAssociatedSalesLedger = salesLedgerRepository.findOneByPid(aatpid);
//
//			if (opSalesLedger.isPresent() && opAssociatedSalesLedger.isPresent()) {
//				salesLedgerAssociation.setSalesLedger(opSalesLedger.get());
//				salesLedgerAssociation.setCompany(opSalesLedger.get().getCompany());
//				salesLedgerAssociation.setAssociatedSalesLedger(opAssociatedSalesLedger.get());
//			}
//
//			salesLedgerAssociationRepository.save(salesLedgerAssociation);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/findAccounts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountProfileDTO>> getAccounts(@PathVariable String pid) {
//		log.debug("REST request to get accounts by salesLedgerPid : {}", pid);
//		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllBySalesLedger(pid);
//		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/findActivities/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountActivityTaskConfigDTO>> getActivities(@PathVariable String pid) {
//		log.debug("REST request to get accounts by salesLedgerPid : {}", pid);
//		List<Object[]> activityConfig = activityConfigRepository.findActivityPidBySalesLedgerPid(pid);
//		List<AccountActivityTaskConfigDTO> activityConfiglist = new ArrayList<>();
//
//		for (Object[] obj : activityConfig) {
//			AccountActivityTaskConfigDTO accActivityConfig = new AccountActivityTaskConfigDTO();
//			log.info(obj[0].toString() + "  ---- " + obj[1].toString());
//			accActivityConfig.setActivityPid(obj[0].toString());
//			accActivityConfig.setAssignNotification((boolean) obj[1]);
//			activityConfiglist.add(accActivityConfig);
//		}
//
//		return new ResponseEntity<>(activityConfiglist, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/findAssociatedSalesLedgers/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<SalesLedgerAssociationDTO>> getAssociatedSalesLedgers(@PathVariable String pid) {
//		log.debug("REST request to get associated account types by salesLedgerPid : {}", pid);
//		// List<Object[]> activityConfig =
//		// activityConfigRepository.findActivityPidBySalesLedgerPid(pid);
//		List<String> associatedSalesLedgers = salesLedgerAssociationRepository.findAssociatedSalesLedgerByPid(pid);
//
//		List<SalesLedgerAssociationDTO> salesLedgerAssociationDTOs = new ArrayList<>();
//
//		for (String associatedSalesLedger : associatedSalesLedgers) {
//			SalesLedgerAssociationDTO salesLedgerAssociationDTO = new SalesLedgerAssociationDTO();
//			log.info(associatedSalesLedger);
//			salesLedgerAssociationDTO.setAssociatedSalesLedgerPid(associatedSalesLedger);
//
//			salesLedgerAssociationDTOs.add(salesLedgerAssociationDTO);
//		}
//
//		return new ResponseEntity<>(salesLedgerAssociationDTOs, HttpStatus.OK);
//	}
//
//	/**
//	 * Update Status /salesLedgers/changeStatus: Activate or Deactivate salesLedger.
//	 *
//	 * @param salesLedgerDTO the salesLedgerDTO to update
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         salesLedgerDTO
//	 */
//
//	@Timed
//	@RequestMapping(value = "/salesLedgers/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<SalesLedgerDTO> updateSalesLedgerStatus(@Valid @RequestBody SalesLedgerDTO salesLedgerDTO) {
//		log.debug("REST request to update status of  salesLedger : {}", salesLedgerDTO);
//		SalesLedgerDTO res = salesLedgerService.updateSalesLedgerStatus(salesLedgerDTO.getPid(),
//				salesLedgerDTO.getActivated());
//		return new ResponseEntity<>(res, HttpStatus.OK);
//	}
//
//	/**
//	 * Update Status /salesLedgers/activateSalesLedgers: Activate salesLedger.
//	 *
//	 * @param salesLedgerDTO the salesLedgerDTO to update
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         salesLedgerDTO
//	 */
//
//	@Timed
//	@RequestMapping(value = "/salesLedgers/activateSalesLedgers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<SalesLedgerDTO> updateActivateSalesLedgerStatus(@Valid @RequestParam String accounttypes) {
//		log.debug("REST request to update status of  salesLedger : {}", accounttypes);
//		String[] salesLedgers = accounttypes.split(",");
//		for (String accounttypePid : salesLedgers) {
//			salesLedgerService.updateSalesLedgerStatus(accounttypePid, true);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/salesLedgers/deactivatedAccounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountProfileDTO>> getDeActivatedAccounts() {
//		log.debug("REST request to get deactivated accounts ");
//		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(false);
//		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
//	}
}
