package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.web.rest.dto.UserMenuItemDTO;

@Controller
@RequestMapping("/web")
public class UserMenuItemResource {

	private static final Logger log = LoggerFactory.getLogger(UserMenuItemResource.class);
	
	@Inject
	private UserMenuItemService userMenuItemService;
	
	@Inject
	private CompanyService companyService;

	/**
	 * GET /user-menu : get the user-menu page.
	 */
	@GetMapping("/user-menu")
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserMenuItemPage(Model model) {
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/user-menu";
	}

	@GetMapping("/user-menu/{userPid}")
	@ResponseBody
	@Timed
	public ResponseEntity<List<MenuItem>> getUserMenuItem(@PathVariable String userPid) {
		List<MenuItem> menuItems = userMenuItemService.findMenuItemsByUserPid(userPid);
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}
	
	@GetMapping("/root-user-menu/{userPid}")
	@ResponseBody
	@Timed
	public ResponseEntity<List<UserMenuItemDTO>> getUserRootMenuItems(@PathVariable String userPid) {
		List<UserMenuItemDTO> userMenuItemDTOs = userMenuItemService.findUserMenuItemsByUserPid(userPid);
		return new ResponseEntity<>(userMenuItemDTOs, HttpStatus.OK);
	}
	
	/**
	 * POST /user-menu/save : save userMenuItems.
	 * 
	 * @param pid
	 * @param assignedMenuItems
	 * @return
	 */
	@PostMapping("/user-menu/save")
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedMenuItems) {
		log.debug("REST request to save assigned menu items {} ", assignedMenuItems);
		userMenuItemService.save(userPid, assignedMenuItems);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * POST /user-menu/save : save userMenuItems.
	 * 
	 * @param pid
	 * @param assignedMenuItems
	 * @return
	 */
	@PostMapping("/user-menu/sort-order/save")
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<Void> saveSortOrder(@RequestParam String sortOrders) {
		log.debug("REST request to save menu items sort order  {} ", sortOrders);
		userMenuItemService.updateSortOrder(sortOrders);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * GET /current-user/menu-items : get all the menuItems of the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         menuItems in body
	 */
	@GetMapping("/current-user/menu-items")
	@ResponseBody
	@Timed
	public ResponseEntity<List<MenuItem>> getAllCurrentUserMenuItems() {
		List<MenuItem> menuItems = userMenuItemService.findMenuItemsByCurrentUser();
		return new ResponseEntity<>(menuItems, HttpStatus.OK);
	}
}
