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
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentSettingsHeaderService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsColumnsDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsListDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsRowColourDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing DynamicDocumentReportSettings.
 *
 * @author Sarath
 * @since Aug 25, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class DynamicDocumentReportSettingsResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentReportSettingsResource.class);

	@Inject
	private DocumentFormsService documentFormsService;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private FormElementService formElementService;

	@Inject
	private DocumentService documentService;

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
	@RequestMapping(value = "/dynamic-document-report-settings", method = RequestMethod.GET)
	@Timed
	public String getAllDynamicDocumentReports(Model model) throws URISyntaxException {
		log.debug("Web request to get DynamicDocumentReports");
		List<DocumentDTO> documentDTOs = documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		List<DynamicDocumentSettingsHeaderDTO> documentSettingsHeaderDTOs = dynamicDocumentSettingsHeaderService
				.findAllByCompanyId();
		model.addAttribute("documents", documentDTOs);
		model.addAttribute("documentSettingsHeaders", documentSettingsHeaderDTOs);
		return "company/dynamic-document-report-settings";
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> saveDynamicDocumentSettingsHeaders(
			@RequestBody DynamicDocumentSettingsHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to save dynamic document header");

		if (dynamicDocumentHeaderDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"idexists", "A new dynamic document header cannot already have an ID")).body(null);
		}
		if (dynamicDocumentSettingsHeaderService.findOneByName(dynamicDocumentHeaderDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"name exists", "dynamic document header already in use")).body(null);
		}

		DynamicDocumentSettingsHeaderDTO result = dynamicDocumentSettingsHeaderService.save(dynamicDocumentHeaderDTO);
		return ResponseEntity
				.created(new URI("/web/dynamic-document-report-settings/" + result.getPid())).headers(HeaderUtil
						.createEntityCreationAlert("dynamic-document-report-settings", result.getPid().toString()))
				.body(result);
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> updateDynamicDocumentSettingsHeaders(
			@RequestBody DynamicDocumentSettingsHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to save dynamic document header");

		if (dynamicDocumentHeaderDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"idexists", "A new dynamic document header cannot already have an ID")).body(null);
		}
		Optional<DynamicDocumentSettingsHeaderDTO> existingDivision = dynamicDocumentSettingsHeaderService
				.findOneByName(dynamicDocumentHeaderDTO.getName());
		if (existingDivision.isPresent()
				&& (!existingDivision.get().getPid().equals(dynamicDocumentHeaderDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"namee xists", "dynamic document header already in use")).body(null);
		}
		DynamicDocumentSettingsHeaderDTO result = dynamicDocumentSettingsHeaderService.update(dynamicDocumentHeaderDTO);

		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dynamic-document-report-settings",
					"idNotexists", "Invalid Division ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("dynamic-document-report-settings",
				dynamicDocumentHeaderDTO.getPid().toString())).body(result);
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> getdynamicDocumentSettingsHeaderByPid(
			@PathVariable String pid) throws URISyntaxException {
		log.debug("Web request to save dynamic document header");
		return dynamicDocumentSettingsHeaderService.findOneByPid(pid)
				.map(dynamicDocumentSettingsHeaderDTO -> new ResponseEntity<>(dynamicDocumentSettingsHeaderDTO,
						HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /dynamic-document-report-settings/:id : delete the "id"
	 * DynamicDocumentSettingsHeader.
	 *
	 * @param id
	 *            the id of the DynamicDocumentSettingsHeaderDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/dynamic-document-report-settings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDynamicDocumentSettingsHeader(@PathVariable String pid) {
		log.debug("REST request to delete DynamicDocumentSettingsHeader : {}", pid);
		dynamicDocumentSettingsHeaderService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("dynamic-document-report-settings", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/dynamic-document-report-settings/loadFormElements", method = RequestMethod.GET)
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
		return formElementDTOs;
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings/getdynamicDocumentSettingsColumns", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DynamicDocumentSettingsListDTO getdynamicDocumentSettingsColumnsByPid(
			@RequestParam(value = "headPid") String headPid, @RequestParam(value = "documentPid") String documentPid)
			throws URISyntaxException {
		log.debug("Web request to save dynamicDocumentSettingsColumns");
		Optional<DynamicDocumentSettingsHeaderDTO> opDynamicDocumentSettingsHeaderDTO = dynamicDocumentSettingsHeaderService
				.findOneByPid(headPid);
		List<DynamicDocumentSettingsColumnsDTO> columnsDTOs = new ArrayList<>();
		if (opDynamicDocumentSettingsHeaderDTO.isPresent()
				&& opDynamicDocumentSettingsHeaderDTO.get().getDocumentSettingsColumnsDTOs() != null) {
			columnsDTOs.addAll(opDynamicDocumentSettingsHeaderDTO.get().getDocumentSettingsColumnsDTOs());
		}
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
		DynamicDocumentSettingsListDTO result = new DynamicDocumentSettingsListDTO(formElementDTOs, columnsDTOs, null);
		return result;
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings/getDynamicDocumentSettingsRowColours", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DynamicDocumentSettingsListDTO getDynamicDocumentSettingsRowColoursByPid(
			@RequestParam(value = "headPid") String headPid, @RequestParam(value = "documentPid") String documentPid)
			throws URISyntaxException {
		log.debug("Web request to save DynamicDocumentSettingsRowColours");
		Optional<DynamicDocumentSettingsHeaderDTO> opDynamicDocumentSettingsHeaderDTO = dynamicDocumentSettingsHeaderService
				.findOneByPid(headPid);

		List<DynamicDocumentSettingsRowColourDTO> rowColorDTOs = new ArrayList<>();
		List<FormElementDTO> formElementDTOs = new ArrayList<>();
		if (opDynamicDocumentSettingsHeaderDTO.isPresent()
				&& opDynamicDocumentSettingsHeaderDTO.get().getDocumentSettingsRowColourDTOs() != null) {
			rowColorDTOs.addAll(opDynamicDocumentSettingsHeaderDTO.get().getDocumentSettingsRowColourDTOs());
				formElementDTOs = formElementService
						.findAllByCompanyIdAndFormElementPidIn(Arrays.asList(opDynamicDocumentSettingsHeaderDTO.get()
								.getDocumentSettingsRowColourDTOs().get(0).getFormElementPid()));
		}
		DynamicDocumentSettingsListDTO result = new DynamicDocumentSettingsListDTO(formElementDTOs, null, rowColorDTOs);
		return result;
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings/assignDocumentSettingsColumns", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> saveAssignedDocumentSettingsColumns(
			@RequestBody List<DynamicDocumentSettingsColumnsDTO> dynamicDocumentSettingsColumnsDTOs)
			throws URISyntaxException {
		log.debug("Web request to save dynamic document header");
		dynamicDocumentSettingsHeaderService.updateDynamicDocumentSettingsColumns(dynamicDocumentSettingsColumnsDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/dynamic-document-report-settings/assignDynamicDocumentSettingsRowColours", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentSettingsHeaderDTO> saveDynamicDocumentSettingsRowColour(
			@RequestBody List<DynamicDocumentSettingsRowColourDTO> dynamicDocumentSettingsRowColourDTOs)
			throws URISyntaxException {
		log.debug("Web request to save dynamic document header");
		dynamicDocumentSettingsHeaderService.saveDynamicDocumentSettingsRowColour(dynamicDocumentSettingsRowColourDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
