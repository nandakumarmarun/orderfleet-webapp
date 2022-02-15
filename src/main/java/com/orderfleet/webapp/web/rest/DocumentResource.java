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
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Controller
@RequestMapping("/web")
public class DocumentResource {

	private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

	@Inject
	private DocumentService documentService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private DocumentAccountTypeService documentAccountTypeService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private DocumentPriceLevelService documentPriceLevelService;

	@Inject
	private DocumentStockCalculationRepository documentStockCalculationRepository;

	@Inject
	private CompanyService companyService;

	/**
	 * POST /documents : Create a new document.
	 *
	 * @param documentDTO the documentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         documentDTO, or with status 400 (Bad Request) if the document has
	 *         already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.MASTER_DATA_MANAGER })
	@RequestMapping(value = "/documents", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody DocumentDTO documentDTO)
			throws URISyntaxException {
		log.debug("Web request to save Document : {}", documentDTO);
		if (documentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("document", "idexists", "A new document cannot already have an ID"))
					.body(null);
		}
		if (documentService.findByName(documentDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "nameexists", "Document already in use"))
					.body(null);
		}
		if (documentService.findByDocumentPrefix(documentDTO.getDocumentPrefix()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("document", "documentPrefixexists", "Document Prefix already in use"))
					.body(null);
		}

		DocumentDTO result = documentService.save(documentDTO);
		return ResponseEntity.created(new URI("/web/documents/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("document", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /documents : Updates an existing document.
	 *
	 * @param documentDTO the documentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         documentDTO, or with status 400 (Bad Request) if the documentDTO is
	 *         not valid, or with status 500 (Internal Server Error) if the
	 *         documentDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/documents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DocumentDTO> updateDocument(@Valid @RequestBody DocumentDTO documentDTO)
			throws URISyntaxException {
		log.debug("Web request to update Document : {}", documentDTO);
		if (documentDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "idNotexists", "Document must have an ID"))
					.body(null);
		}
		Optional<DocumentDTO> existingDocument = documentService.findByName(documentDTO.getName());
		if (existingDocument.isPresent() && (!existingDocument.get().getPid().equals(documentDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "nameexists", "Document already in use"))
					.body(null);
		}
		existingDocument = documentService.findByDocumentPrefix(documentDTO.getDocumentPrefix());
		if (existingDocument.isPresent() && (!existingDocument.get().getPid().equals(documentDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("document", "documentPrefixexists", "Document Prefix already in use"))
					.body(null);
		}
		
		DocumentDTO result = documentService.update(documentDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("document", documentDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /documents : get all the documents.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of documents in
	 *         body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Documents");
		List<DocumentDTO> documents = documentService.findAllByCompany();
		model.addAttribute("documents", documents);
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		return "company/documents";
	}

	/**
	 * GET /documents/:id : get the "id" document.
	 *
	 * @param id the id of the documentDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/documents/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DocumentDTO> getDocument(@PathVariable String pid) {
		log.debug("Web request to get Document by pid : {}", pid);
		return documentService.findOneByPid(pid).map(documentDTO -> new ResponseEntity<>(documentDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /documents/:id : delete the "id" document.
	 *
	 * @param id the id of the documentDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/documents/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDocument(@PathVariable String pid) {
		log.debug("REST request to delete Document : {}", pid);
		documentService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("document", pid.toString())).build();
	}

	@RequestMapping(value = "/documents/accountTypes", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<AccountTypeDTO>> documentAccountTypes(@RequestParam String documentPid,
			@RequestParam String accountTypeColumn) {
		log.debug("REST request to document account types : {}", documentPid);
		List<AccountTypeDTO> accountTypeDTOs = documentAccountTypeService
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid,
						AccountTypeColumn.valueOf(accountTypeColumn));
		return new ResponseEntity<>(accountTypeDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/assignAccountTypes", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountTypes(@RequestParam String documentPid,
			@RequestParam String assignedAccountTypes, @RequestParam String accountTypeColumn) {
		log.debug("REST request to save assigned account type : {}", documentPid);
		documentAccountTypeService.save(documentPid, assignedAccountTypes,
				AccountTypeColumn.valueOf(accountTypeColumn));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/price-levels", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<PriceLevelDTO>> documentPriceLevels(@RequestParam String documentPid) {
		log.debug("REST request to document price levels : {}", documentPid);
		List<PriceLevelDTO> priceLevelDTOs = documentPriceLevelService.findPriceLevelByDocumentPid(documentPid);
		return new ResponseEntity<>(priceLevelDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/assign-price-levels", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedPriceLevels(@RequestParam String documentPid,
			@RequestParam String assignedPriceLevels) {
		log.debug("REST request to save assigned Price Levels : {}", documentPid);
		documentPriceLevelService.save(documentPid, assignedPriceLevels);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/documents/stock-calculation/{pid}", method = RequestMethod.GET)
	public ResponseEntity<DocumentStockCalculationDTO> stockCalculation(@PathVariable String pid) {
		log.debug("REST request to document stock Calculation : {}", pid);
		return documentStockCalculationRepository.findOneByDocumentPid(pid)
				.map(documentStockCalculation -> new ResponseEntity<>(
						new DocumentStockCalculationDTO(documentStockCalculation), HttpStatus.OK))
				.orElse(new ResponseEntity<>(new DocumentStockCalculationDTO(), HttpStatus.OK));
	}

	@Timed
	@RequestMapping(value = "/documents/stock-calculation", method = RequestMethod.POST)
	public ResponseEntity<DocumentStockCalculationDTO> saveStockCalculation(
			@RequestBody DocumentStockCalculationDTO documentStockCalculationDTO) {
		log.debug("REST request to save document stock Calculation");
		DocumentStockCalculationDTO result = documentService.saveDocumentStockCalculation(documentStockCalculationDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/site-documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllDocumentsForSite(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Documents");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("paymentModes", PaymentMode.values());
		model.addAttribute("stockFlows", StockFlow.values());
		return "site_admin/documents";
	}

	@Timed
	@RequestMapping(value = "/documents/get-by-company-filter/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentDTO>> getDocumentsByCompany(@PathVariable String companyPid) {
		log.debug("Web request to get get activitys by company Pid : {}", companyPid);
		List<DocumentDTO> result = documentService.findAllByCompanyPid(companyPid);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DocumentDTO> createDocumentFromAdmin(@Valid @RequestBody DocumentDTO documentDTO)
			throws URISyntaxException {
		log.debug("Web request to save Document : {}", documentDTO);
		if (documentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("document", "idexists", "A new document cannot already have an ID"))
					.body(null);
		}
		if (documentService.findByNameAndCompanyPid(documentDTO.getName(), documentDTO.getCompanyPid()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "nameexists", "Document already in use"))
					.body(null);
		}
		DocumentDTO result = documentService.saveFormSAdmin(documentDTO);
		return ResponseEntity.created(new URI("/web/activities/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("document", result.getPid().toString())).body(result);
	}

	@RequestMapping(value = "/documents/save", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DocumentDTO> updateDocumentFromAdmin(@Valid @RequestBody DocumentDTO documentDTO)
			throws URISyntaxException {
		log.debug("Web request to update Document : {}", documentDTO);
		if (documentDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "idNotexists", "Document must have an ID"))
					.body(null);
		}
		Optional<DocumentDTO> existingDocument = documentService.findByNameAndCompanyPid(documentDTO.getName(),
				documentDTO.getCompanyPid());
		if (existingDocument.isPresent() && (!existingDocument.get().getPid().equals(documentDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "nameexists", "Document already in use"))
					.body(null);
		}
		DocumentDTO result = documentService.update(documentDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("document", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("document", documentDTO.getPid().toString())).body(result);
	}
}
