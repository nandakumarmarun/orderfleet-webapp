package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.MobileMenuItemGroupMenuItem;
import com.orderfleet.webapp.repository.MobileMenuItemGroupMenuItemRepository;
import com.orderfleet.webapp.repository.MobileMenuItemRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.MobileMenuItemGroupService;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing MobileMenuItemGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class MobileMenuItemGroupResource {

	private final Logger log = LoggerFactory.getLogger(MobileMenuItemGroupResource.class);

	@Inject
	private MobileMenuItemGroupService mobileMenuItemGroupService;

	@Inject
	private MobileMenuItemRepository mobileMenuItemRepository;

	@Inject
	private MobileMenuItemGroupMenuItemRepository mobileMenuItemGroupMenuItemRepository;

	/**
	 * GET /mobileMenuItemGroups : get all the mobileMenuItemGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         mobileMenuItemGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@RequestMapping(value = "/mobileMenuItemGroups", method = RequestMethod.GET)
	public String getAllMobileMenuItemGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of MobileMenuItemGroups");
		model.addAttribute("mobileMenuItems", mobileMenuItemRepository.findAll());
		model.addAttribute("mobileMenuItemGroups", mobileMenuItemGroupService.findAll());
		return "site_admin/mobileMenuItemGroups";
	}

	/**
	 * POST /mobileMenuItemGroups : Create a new mobileMenuItemGroup.
	 *
	 * @param mobileMenuItemGroupDTO
	 *            the mobileMenuItemGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new mobileMenuItemGroupDTO, or with status 400 (Bad Request) if
	 *         the mobileMenuItemGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@ResponseBody
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@RequestMapping(value = "/mobileMenuItemGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MobileMenuItemGroupDTO> createMobileMenuItemGroup(
			@Valid @RequestBody MobileMenuItemGroupDTO mobileMenuItemGroupDTO) throws URISyntaxException {
		log.debug("Web request to save MobileMenuItemGroup : {}", mobileMenuItemGroupDTO);
		if (mobileMenuItemGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobileMenuItemGroup", "idexists",
					"A new mobileMenuItemGroup cannot already have an ID")).body(null);
		}
		if (mobileMenuItemGroupService.findByName(mobileMenuItemGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobileMenuItemGroup",
					"nameexists", "MobileMenuItemGroup already in use")).body(null);
		}
		MobileMenuItemGroupDTO result = mobileMenuItemGroupService.save(mobileMenuItemGroupDTO);
		return ResponseEntity.created(new URI("/web/mobileMenuItemGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("mobileMenuItemGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /mobileMenuItemGroups : Updates an existing mobileMenuItemGroup.
	 *
	 * @param mobileMenuItemGroupDTO
	 *            the mobileMenuItemGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         mobileMenuItemGroupDTO, or with status 400 (Bad Request) if the
	 *         mobileMenuItemGroupDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the mobileMenuItemGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@RequestMapping(value = "/mobileMenuItemGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MobileMenuItemGroupDTO> updateMobileMenuItemGroup(
			@Valid @RequestBody MobileMenuItemGroupDTO mobileMenuItemGroupDTO) throws URISyntaxException {
		log.debug("Web request to update MobileMenuItemGroup : {}", mobileMenuItemGroupDTO);
		if (mobileMenuItemGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobileMenuItemGroup",
					"idNotexists", "MobileMenuItemGroup must have an ID")).body(null);
		}
		Optional<MobileMenuItemGroupDTO> existingMobileMenuItemGroup = mobileMenuItemGroupService
				.findByName(mobileMenuItemGroupDTO.getName());
		if (existingMobileMenuItemGroup.isPresent()
				&& (!existingMobileMenuItemGroup.get().getPid().equals(mobileMenuItemGroupDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("designation", "nameexists", "MobileMenuItemGroup already in use"))
					.body(null);
		}
		MobileMenuItemGroupDTO result = mobileMenuItemGroupService.update(mobileMenuItemGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("mobileMenuItemGroup",
					"idNotexists", "Invalid MobileMenuItemGroup ID")).body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("mobileMenuItemGroup", mobileMenuItemGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /mobileMenuItemGroups/:id : get the "id" mobileMenuItemGroup.
	 *
	 * @param id
	 *            the id of the mobileMenuItemGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         mobileMenuItemGroupDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/mobileMenuItemGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MobileMenuItemGroupDTO> getMobileMenuItemGroup(@PathVariable String pid) {
		log.debug("Web request to get MobileMenuItemGroup by pid : {}", pid);
		return mobileMenuItemGroupService.findOneByPid(pid)
				.map(mobileMenuItemGroupDTO -> new ResponseEntity<>(mobileMenuItemGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /mobileMenuItemGroups/:id : delete the "id" mobileMenuItemGroup.
	 *
	 * @param id
	 *            the id of the mobileMenuItemGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@RequestMapping(value = "/mobileMenuItemGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteMobileMenuItemGroup(@PathVariable String pid) {
		log.debug("REST request to delete MobileMenuItemGroup : {}", pid);
		mobileMenuItemGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("mobileMenuItemGroup", pid.toString()))
				.build();
	}

	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@RequestMapping(value = "/mobileMenuItemGroups/menuItems/{groupPid}", method = RequestMethod.POST)
	public ResponseEntity<Void> saveMenuItems(@PathVariable String groupPid,
			@RequestBody List<MobileMenuItemDTO> menuItems) {
		log.debug("REST request to save  assigned menu Items");
		Optional<MobileMenuItemGroupDTO> mobileMenuItemGroup = mobileMenuItemGroupService.findOneByPid(groupPid);	
		if(mobileMenuItemGroup.isPresent()){
			List<MobileMenuItemGroupMenuItem> menuItemGroupMenuItems=mobileMenuItemGroupMenuItemRepository.findAllByMobileMenuItemGroupPid(groupPid);
			if(menuItemGroupMenuItems!=null){
					mobileMenuItemGroupMenuItemRepository.deleteByMobileMenuItemGroupPid(groupPid);
			}
		}
		mobileMenuItemGroupService.save(groupPid, menuItems);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/mobileMenuItemGroups/menuItems/{groupPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MobileMenuItemDTO>> getGroupDocumentProduct(@PathVariable String groupPid) {
		log.debug("Web request to get get menu Items by menu group pid : {}", groupPid);
		List<MobileMenuItemGroupMenuItem> mobileMenuItemGroupMenuItems = mobileMenuItemGroupMenuItemRepository
				.findAllByMobileMenuItemGroupPid(groupPid);
		List<MobileMenuItemDTO> menuItems = mobileMenuItemGroupMenuItems.stream()
				.map(mmgmi -> new MobileMenuItemDTO(mmgmi.getMobileMenuItem().getId(),
						mmgmi.getMobileMenuItem().getName(), mmgmi.getLabel(), mmgmi.getLastModifiedDate()))
				.collect(Collectors.toList());
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}
}
