package com.orderfleet.webapp.web.rest;


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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.MobileMenuItemGroupService;
import com.orderfleet.webapp.service.UserMobileMenuItemGroupService;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;

@Controller
@RequestMapping("/web")
public class UserMobileMenuItemGroupResource {

	private static final Logger log = LoggerFactory.getLogger(UserMobileMenuItemGroupResource.class);

	@Inject
	private UserMobileMenuItemGroupService userMobileMenuItemGroupService;

	@Inject
	private CompanyService companyService;

	@Inject
	private MobileMenuItemGroupService mobileMenuItemGroupService;

	/**
	 * GET /userMobileMenuItemGroups : get the userMobileMenuItemGroups page.
	 */
	@Timed
	@GetMapping("/userMobileMenuItemGroups")
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserMenuItemPage(Model model) {
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("menuGroups", mobileMenuItemGroupService.findAll());
		return "site_admin/userMobileMenuItemGroups";
	}

	/**
	 * POST /userMobileMenuItemGroups/save : save userMenuGroup.
	 * 
	 * @param pid
	 * @param assignedMenuItems
	 * @return
	 */
	@Timed
	@PostMapping("/userMobileMenuItemGroups/save")
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String menuGroupPid) {
		log.debug("REST request to save assigned menu group {} ", menuGroupPid);
		userMobileMenuItemGroupService.save(userPid, menuGroupPid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@GetMapping("/userMobileMenuItemGroups/{userPid}")
	public ResponseEntity<MobileMenuItemGroupDTO> getUserMobileMenuItemGroup(@PathVariable String userPid) {
		MobileMenuItemGroupDTO mobileMenuItemGroupDTO = userMobileMenuItemGroupService
				.findUserMobileMenuItemGroupByUserPid(userPid);
		return new ResponseEntity<>(mobileMenuItemGroupDTO, HttpStatus.OK);
	}

}
