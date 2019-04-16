package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.MediaType;
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
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.web.rest.dto.SyncOperationTimeDTO;

@Controller
@RequestMapping("/web")
public class SyncOperationTimeResource {

	@Inject
	private CompanyService companyService;
	
	@Inject
	private SyncOperationService syncOperationService;
	
	@RequestMapping(value="/sync-operation-time",method=RequestMethod.GET)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@Timed
	public String getAllSyncOperationTime(Model model) throws URISyntaxException{
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/sync-operation-times";
	}
	
	@RequestMapping(value = "/sync-operation-time/getSyncOperationTimes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<SyncOperationTimeDTO> getAssighnedSyncOperations(@Valid @RequestParam String companyPid) throws URISyntaxException {
		List<SyncOperationTimeDTO> result = syncOperationService.findAllSyncOperationTimesBycompanyPid(companyPid);
		return result;
	}
}
