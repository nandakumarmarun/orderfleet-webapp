package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.InventoryClosingReportSettingGroupService;
import com.orderfleet.webapp.service.InventoryClosingReportSettingsService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingGroupDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class InventoryClosingReportSettingsResource {

private final Logger log = LoggerFactory.getLogger(InventoryClosingReportSettingsResource.class);
	
	@Inject
	private InventoryClosingReportSettingsService inventoryClosingReportService;
	
	@Inject
	private InventoryClosingReportSettingGroupService inventoryClosingReportSettingGroupService;
	
	@Inject
	private DocumentService documentService;
	
	/**
	 * GET /inventory-closing-report : get all the inventoryClosingReport.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of inventoryClosingReport
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/inventory-closing-report-settings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllInventoryClosingReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of inventoryClosingReport");
		List<InventoryClosingReportSettingsDTO> inventoryClosingReportDTOs = inventoryClosingReportService.findAllByCompany();
		model.addAttribute("inventoryClosingReports", inventoryClosingReportDTOs);
		List<InventoryClosingReportSettingGroupDTO> closingReportSettingGroupDTOs = inventoryClosingReportSettingGroupService.findAllByCompany();
		model.addAttribute("inventoryClosingReportGrps", closingReportSettingGroupDTOs);
		List<DocumentDTO>documentDTOs=documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER);
		model.addAttribute("documents", documentDTOs);
		return "company/inventoryClosingReportSettings";
	}
	
	/**
	 * POST /inventory-closing-report : Create a new inventoryClosingReport.
	 *
	 * @param inventoryClosingReportDTO
	 *            the inventoryClosingReportDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new inventoryClosingReportDTO, or with status 400 (Bad Request) if the inventoryClosingReport
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-closing-report-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<InventoryClosingReportSettingsDTO> createInventoryClosingReport(@Valid @RequestBody InventoryClosingReportSettingsDTO inventoryClosingReportDTO)
			throws URISyntaxException {
		log.debug("Web request to save InventoryClosingReport : {}", inventoryClosingReportDTO);
		if (inventoryClosingReportDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("inventoryClosingReport", "idexists", "A new inventoryClosingReport cannot already have an ID"))
					.body(null);
		}
		
		InventoryClosingReportSettingsDTO result = inventoryClosingReportService.save(inventoryClosingReportDTO);
		return ResponseEntity.created(new URI("/web/financialClosing-report-settings"))
				.headers(HeaderUtil.createEntityCreationAlert("inventoryClosingReport", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /inventory-closing-report: Updates an existing document.
	 *
	 * @param documentDTO
	 *            the documentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         documentDTO, or with status 400 (Bad Request) if the documentDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         documentDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-closing-report-settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryClosingReportSettingsDTO> updateInventoryClosingReport(@Valid @RequestBody InventoryClosingReportSettingsDTO inventoryClosingReportDTO)
			throws URISyntaxException {
		log.debug("Web request to update InventoryClosingReport : {}", inventoryClosingReportDTO);
		if (inventoryClosingReportDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReport", "idNotexists", "InventoryClosingReport must have an ID"))
					.body(null);
		}
		Optional<InventoryClosingReportSettingsDTO> existingInventoryClosingReport = inventoryClosingReportService.findByCompanyIdAndDocumentPid(inventoryClosingReportDTO.getDocumentPid());
		if (existingInventoryClosingReport.isPresent() && (!existingInventoryClosingReport.get().getId().equals(inventoryClosingReportDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReport", "inventoryClosingReportexists", "InventoryClosingReport already in use"))
					.body(null);
		}
		InventoryClosingReportSettingsDTO result = inventoryClosingReportService.update(inventoryClosingReportDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("inventoryClosingReport", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("inventoryClosingReport", inventoryClosingReportDTO.getId().toString())).body(result);
	}
	
	@RequestMapping(value = "/inventory-closing-report-settings/loaddocument" , method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "documentType") DocumentType documentType) {
		List<DocumentDTO>documentDTOs=documentService.findAllByDocumentType(documentType);
		return documentDTOs;
	}
	
	/**
	 * GET /inventory-closing-report/:id : get the "id" inventoryClosingReport.
	 *
	 * @param id
	 *            the id of the inventoryClosingReportDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/inventory-closing-report-settings/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryClosingReportSettingsDTO> getInventoryClosingReport(@PathVariable Long id) {
		log.debug("Web request to get InventoryClosingReport by id : {}", id);
		InventoryClosingReportSettingsDTO inventoryClosingReportDTO=inventoryClosingReportService.findOne(id);
		return new ResponseEntity<InventoryClosingReportSettingsDTO>(inventoryClosingReportDTO, HttpStatus.OK);
		
	}

	/**
	 * DELETE /inventory-closing-report/:id : delete the "id" inventoryClosingReport.
	 *
	 * @param id
	 *            the id of the inventoryClosingReportDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/inventory-closing-report-settings/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteInventoryClosingReport(@PathVariable Long id) {
		log.debug("REST request to delete InventoryClosingReport : {}", id);
		inventoryClosingReportService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("inventoryClosingReport", id.toString())).build();
	}
}
