package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.report.enums.DynamicDocumentHeaderColumn;
import com.orderfleet.webapp.report.enums.ExecutiveTaskExecutionColumn;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentSettingsHeaderService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing DynamicDocumentReportConfiguration.
 *
 * @author Sarath
 * @since Mar 23, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class DynamicDocumentReportConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentReportConfigurationResource.class);

	@Inject
	private DocumentService documentService;

	@Inject
	private DocumentFormsService documentFormsService;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private FormElementService formElementService;

	@Inject
	private DynamicDocumentSettingsHeaderService dynamicDocumentSettingsHeaderService;

	/**
	 * GET /dynamic-document-report-settings : get all the
	 * dynamic-document-report-settings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         dynamic-document-report-settings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/dynamic-document-report-configuration", method = RequestMethod.GET)
	@Timed
	public String getAllDynamicDocumentReportConfigurations(Model model) throws URISyntaxException {
		log.debug("Web request to get Dynamic Document Report Configurations");
		List<DocumentDTO> documentDTOs = documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		List<DynamicDocumentSettingsHeaderDTO> documentSettingsHeaderDTOs = dynamicDocumentSettingsHeaderService
				.findAllByCompanyId();
		model.addAttribute("documents", documentDTOs);
		model.addAttribute("documentSettingsHeaders", documentSettingsHeaderDTOs);
		return "company/dynamic-document-report-configuration";
	}

	@RequestMapping(value = "/dynamic-document-report-configuration/loadFormElements", method = RequestMethod.GET)
	public @ResponseBody List<FormElementDTO> getFormElementsByDocument(
			@RequestParam(value = "documentPid") String documentPid) {
		log.debug("Web request to get FormElements by documentPid");
		List<String> formPids = documentFormsService.findFormsByDocumentPid(documentPid).stream().map(f -> f.getPid())
				.collect(Collectors.toList());
		List<FormElementDTO> formElementDTOs = new ArrayList<>();
		if (!formPids.isEmpty() && formPids.size() > 0) {
			List<String> formElementPids = formFormElementService.findByFormPidIn(formPids).stream()
					.map(f -> f.getFormElementPid()).collect(Collectors.toList());
			if (formElementPids.size() > 0) {
				formElementDTOs = formElementService.findAllByCompanyIdAndFormElementPidIn(formElementPids);
			}
		}

		List<DynamicDocumentHeaderColumn> headerColumns = Arrays.asList(DynamicDocumentHeaderColumn.values());
		List<ExecutiveTaskExecutionColumn> executionColumns = Arrays.asList(ExecutiveTaskExecutionColumn.values());

		return formElementDTOs;
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> saveDynamicDocumentconfigurations(
			@RequestBody DynamicDocumentSettingsHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to save dynamic document configurations");

		if (dynamicDocumentHeaderDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dynamic-document-report-configuration", "idexists",
							"A new dynamic document header cannot already have an ID"))
					.body(null);
		}
		if (dynamicDocumentSettingsHeaderService.findOneByName(dynamicDocumentHeaderDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dynamic-document-report-configuration", "name exists",
							"dynamic document header already in use"))
					.body(null);
		}

		DynamicDocumentSettingsHeaderDTO result = dynamicDocumentSettingsHeaderService.save(dynamicDocumentHeaderDTO);

		return ResponseEntity
				.created(new URI("/web/dynamic-document-report-configuration/" + result.getPid())).headers(HeaderUtil
						.createEntityCreationAlert("dynamic-document-report-configuration", result.getPid().toString()))
				.body(result);
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> updateDynamicDocumentSettingsHeaders(
			@RequestBody DynamicDocumentSettingsHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to update dynamic document header");

		if (dynamicDocumentHeaderDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"idNotexists", "A new dynamic document header must have an ID")).body(null);
		}
		Optional<DynamicDocumentSettingsHeaderDTO> existingDivision = dynamicDocumentSettingsHeaderService
				.findOneByName(dynamicDocumentHeaderDTO.getName());
		if (existingDivision.isPresent()
				&& (!existingDivision.get().getPid().equals(dynamicDocumentHeaderDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"namee xists", "dynamic document header already in use")).body(null);
		}
		DynamicDocumentSettingsHeaderDTO result = dynamicDocumentSettingsHeaderService.updateDynamicDocumentSettingsHeader(dynamicDocumentHeaderDTO);

		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"idNotexists", "Invalid Division ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("dynamic-document-report-settings",
				dynamicDocumentHeaderDTO.getPid().toString())).body(result);
	}
	
	
	@Timed
	@RequestMapping(value = "/dynamic-document-report-configuration/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> getdynamicDocumentSettingsHeaderByPid(
			@PathVariable String pid) throws URISyntaxException {
		log.debug("Web request to save dynamic document header");
		return dynamicDocumentSettingsHeaderService.findOneByPid(pid)
				.map(dynamicDocumentSettingsHeaderDTO -> new ResponseEntity<>(dynamicDocumentSettingsHeaderDTO,
						HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/dynamic-document-report-configuration/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDynamicDocumentSettingsHeader(@PathVariable String pid) {
		log.debug("REST request to delete DynamicDocumentSettingsHeader : {}", pid);
		dynamicDocumentSettingsHeaderService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("dynamic-document-report-settings", pid.toString()))
				.build();
	}
}
