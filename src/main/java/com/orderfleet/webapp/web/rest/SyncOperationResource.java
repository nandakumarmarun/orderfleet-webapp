package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.web.rest.dto.SyncOperationManageDTO;

/**
 * Web controller for managing SyncOperation.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */

@Controller
@RequestMapping("/web")
public class SyncOperationResource {

	private final Logger log = LoggerFactory.getLogger(SyncOperationResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private SyncOperationService syncOperationService;

	@RequestMapping(value = "/syncOperation", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserManagementPage(Model model) {
		log.debug("Web request to get user-management page with user");
		model.addAttribute("syncOperations", syncOperationService.findAllSyncOperations());
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("syncOperationTypes", Arrays.asList(SyncOperationType.values()));
		return "site_admin/sync-operations";
	}

	@RequestMapping(value = "/syncOperation/saveSyncOperations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<SyncOperationManageDTO> saveSyncOperations(
			@Valid @RequestBody List<SyncOperationManageDTO> syncOperationManageDTOs) throws URISyntaxException {
		log.debug("Web request to save SyncOperation : {}", syncOperationManageDTOs.size());
		List<SyncOperationManageDTO> result = syncOperationService.saveSyncOperation(syncOperationManageDTOs);
		return result;
	}

	@RequestMapping(value = "/syncOperation/getAssighnedSyncOperations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<SyncOperationManageDTO> getAssighnedSyncOperations(@Valid @RequestParam String companyPid)
			throws URISyntaxException {
		log.debug("Web request to Get SyncOperation By company PId: {}", companyPid);
		List<SyncOperationManageDTO> result = syncOperationService.findAllSyncOperationByCompanyPid(companyPid);
		return result;
	}

}
