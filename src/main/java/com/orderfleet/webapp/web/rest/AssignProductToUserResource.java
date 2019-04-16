package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AssignProductSetUpDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

@Controller
@RequestMapping("/web")
public class AssignProductToUserResource {

	private final Logger log = LoggerFactory.getLogger(AssignProductToUserResource.class);

	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;

	@Inject
	private CompanyService companyService;

	@Inject
	private UserService userService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductCategoryService productCategoryService;

	@RequestMapping(value = "/saveProduct", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getSaveProductPage(Model model) {
		model.addAttribute("companies", companyService.findAll());
		return "site_admin/assignProducts";
	}

	@RequestMapping(value = "/saveProduct/loadUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<AssignProductSetUpDTO> loadUser(@RequestParam String companyPid) {
		log.debug("Load User by Company Pid", companyPid);
		List<UserDTO> userDTOs = userService.findAllByCompanyPid(companyPid);
		List<ProductGroupDTO> productGroupDTOs = productGroupService.findAllProductGroupByCompanyPid(companyPid);
		List<ProductCategoryDTO> productCategoryDTOs = productCategoryService.findAllByCompanyPid(companyPid);
		AssignProductSetUpDTO assignProductSetUpDTO = new AssignProductSetUpDTO(userDTOs, productGroupDTOs,
				productCategoryDTOs);
		return new ResponseEntity<AssignProductSetUpDTO>(assignProductSetUpDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/saveProduct/assign-product", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<Void> assignProductsToUser(@RequestParam String companyPid,
			@RequestParam("selectedUserPid") String selectedUserPid, @RequestParam String categoryPids,
			@RequestParam String groupPids) {
		log.debug("Assign Product to user", companyPid);
		companyTrialSetUpService.assignProductGroups(companyPid, selectedUserPid, groupPids);
		companyTrialSetUpService.assignProductCategories(companyPid, selectedUserPid, categoryPids);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
