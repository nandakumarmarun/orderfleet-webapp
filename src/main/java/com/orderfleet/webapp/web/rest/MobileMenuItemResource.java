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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.MobileMenuItemService;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing MobileMenuItem.
 * 
 * @author fahad
 * @since Jun 12, 2017
 */
@Controller
@RequestMapping(value="/web")
public class MobileMenuItemResource {

	private static final Logger log = LoggerFactory.getLogger(MobileMenuItemResource.class);
	
	@Inject
	private MobileMenuItemService mobileMenuItemService;
	
	
	/**
	 * GET /mobile-menu-item : get all the mobileMenuItems.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         mobileMenuItems in body
	 * @throws URISyntaxException
	 *             if there is an error to generate  HTTP headers
	 */
	@RequestMapping(value = "/mobile-menu-item", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getMenuItem(Model model) {
		log.debug("Web request to get a form to create Mobile Menu");
		model.addAttribute("mobileMenuItems", mobileMenuItemService.findAllMobileMenuItem());
		return "site_admin/mobileMenuItems";
	}
	
	/**
	 * POST /mobile-menu-item : Create a new mobileMenuItem.
	 *
	 * @param mobileMenuItemDTO
	 *            the mobileMenuItemDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new mobileMenuItemDTO, or with status 400 (Bad Request) if
	 *         the mobileMenuItem has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/mobile-menu-item", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<MobileMenuItemDTO> createMobileMenuItem(@Valid @RequestBody MobileMenuItemDTO mobileMenuItemDTO)
			throws URISyntaxException {
		log.debug("Web request to save MobileMenuItem : {}", mobileMenuItemDTO);
		if (mobileMenuItemDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("mobileMenuItem", "idexists", "A new mobileMenuItem cannot already have an ID"))
					.body(null);
		}
		if (mobileMenuItemService.findByName(mobileMenuItemDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "nameexists", "mobileMenuItem already in use")).body(null);
		}
		if (mobileMenuItemService.findByLabel(mobileMenuItemDTO.getLabel()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "labelexists", "mobileMenuItem already in use")).body(null);
		}
		MobileMenuItemDTO result = mobileMenuItemService.save(mobileMenuItemDTO);
		return ResponseEntity.created(new URI("/web/mobile-menu-item/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("mobileMenuItem", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /mobile-menu-item : Updates an existing mobileMenuItem.
	 *
	 * @param mobileMenuItemDTO
	 *            the mobileMenuItemDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         mobileMenuItemDTO, or with status 400 (Bad Request) if the
	 *         mobileMenuItemDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the mobileMenuItemDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/mobile-menu-item", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<MobileMenuItemDTO> updateMobileMenuItem(@Valid @RequestBody MobileMenuItemDTO mobileMenuItemDTO)
			throws URISyntaxException {
		log.debug("REST request to update MobileMenuItem : {}", mobileMenuItemDTO);
		if (mobileMenuItemDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "idNotexists", "MenuItem must have an ID"))
					.body(null);
		}
		Optional<MobileMenuItemDTO>optionalName=mobileMenuItemService.findByName(mobileMenuItemDTO.getName());
		if(optionalName.isPresent()){
			if(optionalName.get().getId()!=mobileMenuItemDTO.getId()){
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "nameexists", "mobileMenuItem already in use")).body(null);
			}
		}
		Optional<MobileMenuItemDTO>optionalLabel=mobileMenuItemService.findByLabel(mobileMenuItemDTO.getLabel());
		if(optionalLabel.isPresent()){
			if(optionalLabel.get().getId()!=mobileMenuItemDTO.getId()){
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "labelexists", "mobileMenuItem already in use")).body(null);
			}
		}
		MobileMenuItemDTO result = mobileMenuItemService.update(mobileMenuItemDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("mobileMenuItem", "idNotexists", "Invalid MenuItem ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("mobileMenuItem", mobileMenuItemDTO.getId().toString())).body(result);
	}
	
	/**
	 * GET /mobile-menu-item/:id : get the "id" mobileMenuItem.
	 *
	 * @param id
	 *            the id of the mobileMenuItemDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         mobileMenuItemDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/mobile-menu-item/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<MobileMenuItemDTO> getMobileMenuItem(@PathVariable Long id) {
		log.debug("Web request to get MobileMenuItem by id : {}", id);
		MobileMenuItemDTO mobileMenuItemDTO = mobileMenuItemService.findOne(id);
		if (mobileMenuItemDTO != null) {
			return new ResponseEntity<>(mobileMenuItemDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}
	
	/**
	 * DELETE /mobile-menu-item/:id : delete the "id" mobileMenuItem.
	 *
	 * @param id
	 *            the id of the mobileMenuItemDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/mobile-menu-item/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteMobileMenuItem(@PathVariable Long id) {
		log.debug("REST request to delete MobileMenuItem : {}", id);
		mobileMenuItemService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("MobilemenuItem", id.toString())).build();
	}
}
