package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

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
import com.orderfleet.webapp.domain.enums.DebitCredit;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.FinancialClosingReportSettingsService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportSettingsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class FinancialClosingReportSettingsResource {

	private final Logger log = LoggerFactory.getLogger(FinancialClosingReportSettingsResource.class);
	
	@Inject
	private FinancialClosingReportSettingsService financialClosingReportSettingsService;
	
	@Inject
	private DocumentService documentService;
	
	
	/**
	 * GET /financialClosing-report-settings : get all the financialClosingReportSettings.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of financialClosingReportSettings
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/financial-closing-report-settings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllFinancialClosingReportSettings(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of financialClosingReportSettings");
		List<FinancialClosingReportSettingsDTO> financialClosingReportSettingsDTOs = financialClosingReportSettingsService.findAllByCompany();
		model.addAttribute("financialClosingReportSettings", financialClosingReportSettingsDTOs);
		List<DebitCredit>debitCredits=Arrays.asList(DebitCredit.values());
		model.addAttribute("debitCredits", debitCredits);
		List<PaymentMode>paymentModes=Arrays.asList(PaymentMode.values());
		model.addAttribute("paymentModes", paymentModes);
		List<DocumentDTO>documentDTOs=documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		model.addAttribute("documents", documentDTOs);
		return "company/financialClosingReportSettings";
	}
	
	/**
	 * POST /financialClosing-report-settings : Create a new financialClosingReportSettings.
	 *
	 * @param financialClosingReportSettingsDTO
	 *            the financialClosingReportSettingsDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new financialClosingReportSettingsDTO, or with status 400 (Bad Request) if the financialClosingReportSettings
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/financial-closing-report-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<FinancialClosingReportSettingsDTO> createFinancialClosingReportSettings(@Valid @RequestBody FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO)
			throws URISyntaxException {
		log.debug("Web request to save FinancialClosingReportSettings : {}", financialClosingReportSettingsDTO);
		if (financialClosingReportSettingsDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("financialClosingReportSettings", "idexists", "A new financialClosingReportSettings cannot already have an ID"))
					.body(null);
		}

		FinancialClosingReportSettingsDTO result = financialClosingReportSettingsService.save(financialClosingReportSettingsDTO);
		return ResponseEntity.created(new URI("/web/financialClosing-report-settings"))
				.headers(HeaderUtil.createEntityCreationAlert("financialClosingReportSettings", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /financialClosing-report-settings : Updates an existing document.
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
	@RequestMapping(value = "/financial-closing-report-settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FinancialClosingReportSettingsDTO> updateFinancialClosingReportSettings(@Valid @RequestBody FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO)
			throws URISyntaxException {
		log.debug("Web request to update FinancialClosingReportSettings : {}", financialClosingReportSettingsDTO);
		if (financialClosingReportSettingsDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("financialClosingReportSettings", "idNotexists", "FinancialClosingReportSettings must have an ID"))
					.body(null);
		}
//		Optional<FinancialClosingReportSettingsDTO> existingFinancialClosingReportSettings = financialClosingReportSettingsService.findByCompanyIdAndDocumentTypeAndDocumentPid(financialClosingReportSettingsDTO.getDocumentType(), financialClosingReportSettingsDTO.getDocumentPid());
//		if (existingFinancialClosingReportSettings.isPresent() && (!existingFinancialClosingReportSettings.get().getId().equals(financialClosingReportSettingsDTO.getId()))) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("financialClosingReportSettings", "financialClosingReportSettingsexists", "FinancialClosingReportSettings already in use"))
//					.body(null);
//		}
		FinancialClosingReportSettingsDTO result = financialClosingReportSettingsService.update(financialClosingReportSettingsDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("financialClosingReportSettings", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("financialClosingReportSettings", financialClosingReportSettingsDTO.getId().toString())).body(result);
	}
	
	@RequestMapping(value = "/financial-closing-report-settings/loaddocument" , method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "documentType") DocumentType documentType) {
		List<DocumentDTO>documentDTOs=documentService.findAllByDocumentType(documentType);
		return documentDTOs;
	}
	
	/**
	 * GET /financialClosing-report-settings/:id : get the "id" financialClosingReportSettings.
	 *
	 * @param id
	 *            the id of the financialClosingReportSettingsDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/financial-closing-report-settings/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FinancialClosingReportSettingsDTO> getFinancialClosingReportSettings(@PathVariable Long id) {
		log.debug("Web request to get FinancialClosingReportSettings by id : {}", id);
		FinancialClosingReportSettingsDTO financialClosingReportSettingsDTO=financialClosingReportSettingsService.findOne(id);
		return new ResponseEntity<FinancialClosingReportSettingsDTO>(financialClosingReportSettingsDTO, HttpStatus.OK);
		
	}

	/**
	 * DELETE /financialClosing-report-settings/:id : delete the "id" financialClosingReportSettings.
	 *
	 * @param id
	 *            the id of the financialClosingReportSettingsDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/financial-closing-report-settings/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteFinancialClosingReportSettings(@PathVariable Long id) {
		log.debug("REST request to delete FinancialClosingReportSettings : {}", id);
		financialClosingReportSettingsService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("financialClosingReportSettings", id.toString())).build();
	}
}
