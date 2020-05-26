package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DownloadConfig;
import com.orderfleet.webapp.domain.enums.DownloadFileColumns;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * Controller for UserDeviceKeyResource
 *
 * @author Athul
 * @since Apr 29, 2018
 */
@RequestMapping("/web")
@Controller
public class DownloadFileConfigResource {

	private final Logger log = LoggerFactory.getLogger(DownloadFileConfigResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private UserService userService;

	@GetMapping("/download-file-config")
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserDeviceKeysFrom(Model model) {
		model.addAttribute("companies", companyService.findAllCompanySortedByName());
		model.addAttribute("downloadColumns", DownloadFileColumns.values());
		model.addAttribute("downloadConfig", DownloadConfig.values());
		return "site_admin/download-file-config";
	}

	@RequestMapping(value = "/download-file-config/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllUsers(@RequestParam String companyPid, @RequestParam String userPid)
			throws URISyntaxException {
		log.debug("Web request to Get UserDevice : {}", companyPid);

		List<UserDTO> result = new ArrayList<>();
		if (companyPid.equalsIgnoreCase("no")) {
			result = userService.findAllByActivated(true);
		} else if (!companyPid.equalsIgnoreCase("no") && userPid.equalsIgnoreCase("no")) {
			result = userService.findAllByCompanyPid(companyPid);
		} else if (!companyPid.equalsIgnoreCase("no") && !userPid.equalsIgnoreCase("no")) {
			if (userService.findAllUserByCompanyPidAndUserPid(companyPid, userPid).isPresent()) {
				Optional<UserDTO> dto = userService.findAllUserByCompanyPidAndUserPid(companyPid, userPid);
				result.add(dto.get());
			}
		}
		return result;
	}

	@PostMapping(value = "/download-file-config/update", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> updateUserDeviceKey(@RequestBody List<UserDTO> dtoUsers) {
		userService.updateUser(dtoUsers);
		return ResponseEntity.ok().build();
	}

}
