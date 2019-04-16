package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.repository.MenuItemRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.MenuItemService;
import com.orderfleet.webapp.web.rest.dto.MenuItemDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class MenuItemResource {

	private static final Logger log = LoggerFactory.getLogger(MenuItemResource.class);

	@Inject
	private MenuItemRepository menuItemRepository;

	@Inject
	private MenuItemService menuItemService;

	/**
	 * GET /menu-items : get all the menuItems.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         menuItems in body
	 */
	@GetMapping("/menu-items")
	@ResponseBody
	@Timed
	public ResponseEntity<List<MenuItem>> getAllMenuItems() {
		log.debug("REST request to get MenuItems");
		List<MenuItem> menuItems = menuItemRepository.findByActivatedTrue();
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}

	/**
	 * GET /menu-item : get menuItem.
	 * 
	 */
	@RequestMapping(value = "/menu-item", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getMenuItem(Model model) {
		log.debug("Web request to get a form to create inventory documents");
		model.addAttribute("menuItems", menuItemService.findByActivatedTrue());
		return "site_admin/menuItem";
	}

	/**
	 * POST /menu-item : Create a new menuItem.
	 *
	 * @param menuItemDTO
	 *            the menuItemDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new menuItemDTO, or with status 400 (Bad Request) if the menuItem
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/menu-item", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO)
			throws URISyntaxException {
		log.debug("Web request to save MenuItem : {}", menuItemDTO);
		if (menuItemDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("menuItem", "idexists", "A new menuItem cannot already have an ID"))
					.body(null);
		}
		MenuItemDTO result = menuItemService.save(menuItemDTO);
		return ResponseEntity.created(new URI("/web/menuItems/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("menuItem", result.getId().toString())).body(result);
	}

	/**
	 * PUT /menu-item : Updates an existing menuItem.
	 *
	 * @param menuItemDTO
	 *            the menuItemDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         menuItemDTO, or with status 400 (Bad Request) if the menuItemDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         menuItemDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/menu-item", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<MenuItemDTO> updateMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO)
			throws URISyntaxException {
		log.debug("REST request to update MenuItem : {}", menuItemDTO);
		if (menuItemDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("menuItem", "idNotexists", "MenuItem must have an ID"))
					.body(null);
		}
		MenuItemDTO result = menuItemService.update(menuItemDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("menuItem", "idNotexists", "Invalid MenuItem ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("menuItem", menuItemDTO.getId().toString())).body(result);
	}

	/**
	 * GET /menu-item/:id : get the "id" menuItem.
	 *
	 * @param id
	 *            the id of the menuItemDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         menuItemDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/menu-item/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<MenuItemDTO> getMenuItem(@PathVariable Long id) {
		log.debug("Web request to get MenuItem by id : {}", id);
		MenuItemDTO menuItemDTO = menuItemService.findOne(id);
		if (menuItemDTO != null) {
			return new ResponseEntity<>(menuItemDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * DELETE /menu-item/:id : delete the "id" menuItem.
	 *
	 * @param id
	 *            the id of the menuItemDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/menu-item/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
		log.debug("REST request to delete MenuItem : {}", id);
		menuItemService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("menuItem", id.toString())).build();
	}
}
