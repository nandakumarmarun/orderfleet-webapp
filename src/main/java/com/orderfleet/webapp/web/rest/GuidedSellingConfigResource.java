package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.service.GuidedSellingConfigService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.rest.dto.GuidedSellingConfigDTO;

/**
 * Web controller for managing GuidedSellingConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@Controller
@RequestMapping("/web")
public class GuidedSellingConfigResource {

	private final Logger log = LoggerFactory.getLogger(GuidedSellingConfigResource.class);

	@Inject
	private GuidedSellingConfigService guidedSellingConfigService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private DocumentRepository documentRepository;

	/**
	 * GET /guidedSellingConfig : get guidedSellingConfig.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         guidedSellingConfigs in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/guidedSellingConfig", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllGuidedSellingConfig(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of GuidedSellingConfigs");
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("documents", documentRepository.findAllDocumentsByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
		return "company/guidedSellingConfig";
	}

	@Timed
	@RequestMapping(value = "/guidedSellingConfig/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GuidedSellingConfigDTO> getGuidedSellingConfig() throws URISyntaxException {
		log.debug("Web request to get GuidedSellingConfigs ");
		return new ResponseEntity<>(guidedSellingConfigService.findByCompany(), HttpStatus.OK);
	}

	/**
	 * POST /guidedSellingConfig : Create a new guidedSellingConfig.
	 *
	 * @param guidedSellingConfigDTO
	 *            the guidedSellingConfigDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new guidedSellingConfigDTO, or with status 400 (Bad Request) if
	 *         the guidedSellingConfig has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/guidedSellingConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createGuidedSellingConfig(@RequestBody GuidedSellingConfigDTO guidedSellingConfigDTO)
			throws URISyntaxException {
		log.debug("Web request to save GuidedSellingConfigs ");
		guidedSellingConfigService.save(guidedSellingConfigDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
