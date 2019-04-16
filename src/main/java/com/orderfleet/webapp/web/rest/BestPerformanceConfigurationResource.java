package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.BestPerformanceConfigurationService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.dto.BestPerformanceConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing BestPerformanceConfiguration.
 *
 * @author Sarath
 * @since Mar 27, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class BestPerformanceConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(BestPerformanceConfigurationResource.class);

	@Inject
	private BestPerformanceConfigurationService bestPerformanceConfigurationService;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/best-performance-configuration", method = RequestMethod.GET)
	@Timed
	public String getAllBestPerformanceConfigurations(Model model) {
		List<DocumentType> documentTypes = new ArrayList<>(Arrays.asList(DocumentType.values()));
		List<BestPerformanceType> bestPerformanceTypes = new ArrayList<>(Arrays.asList(BestPerformanceType.values()));

		model.addAttribute("documentTypes", documentTypes);
		model.addAttribute("bestPerformanceTypes", bestPerformanceTypes);

		List<BestPerformanceConfigurationDTO> bestPerformanceConfigurationDTOs = bestPerformanceConfigurationService
				.findByCompanyId();

		Map<BestPerformanceType, List<BestPerformanceConfigurationDTO>> bestPerformanceConfigurationDTOMap = bestPerformanceConfigurationDTOs
				.parallelStream()
				.collect(Collectors.groupingBy(BestPerformanceConfigurationDTO::getBestPerformanceType));

		model.addAttribute("bestPerformanceConfigurations", bestPerformanceConfigurationDTOMap);
		return "company/best-performance-configurations";
	}

	@RequestMapping(value = "/best-performance-configuration/{documentType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getDocuments(@PathVariable DocumentType documentType) {
		List<DocumentDTO> documents = documentService.findAllByDocumentType(documentType);
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}

	@RequestMapping(value = "/best-performance-configuration/saveDocuments", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveBestPerformanceConfiguration(@RequestParam String assignedDocuments,
			@RequestParam DocumentType documentType, @RequestParam BestPerformanceType bestPerformanceType) {
		log.debug("REST request to save assigned documents {}", assignedDocuments);
		bestPerformanceConfigurationService.save(assignedDocuments, documentType, bestPerformanceType);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/best-performance-configuration/getTrueDocuments/{bestPerformanceType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getTrueDocuments(@PathVariable BestPerformanceType bestPerformanceType) {
		List<DocumentDTO> documents = bestPerformanceConfigurationService
				.findAllDocumentsByBestPerformanceType(bestPerformanceType);
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}

	@RequestMapping(value = "/best-performance-configuration/{bestPerformanceType}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteBestPerformanceConfiguration(
			@PathVariable BestPerformanceType bestPerformanceType) {
		log.debug("REST request to delete BestPerformanceConfiguration : {}", bestPerformanceType);
		bestPerformanceConfigurationService.deleteByBestPerformanceType(bestPerformanceType);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityDeletionAlert("best-performance-configuration", bestPerformanceType.toString()))
				.build();
	}
}
