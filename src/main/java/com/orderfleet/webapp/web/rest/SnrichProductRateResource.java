package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.OrderProPaymentMode;
import com.orderfleet.webapp.repository.SnrichProductRateRepository;
import com.orderfleet.webapp.repository.SnrichProductRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.SnrichProductRateService;
import com.orderfleet.webapp.web.rest.dto.SnrichProductRateDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class SnrichProductRateResource {

	private final Logger log = LoggerFactory.getLogger(SnrichProductRateResource.class);
	
	@Inject
	private SnrichProductRepository snrichProductRepository;
	
	@Inject
	private SnrichProductRateRepository snrichProductRateRepository;
	
	@Inject
	private SnrichProductRateService snrichProductRateService;
	
	
	@RequestMapping(value = "/snrich-product-rate", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN})
	public String getAllSnrichProductRate(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SnrichProductRate");
		model.addAttribute("snrichProducts", snrichProductRepository.findAll());
		model.addAttribute("snrichProductRates", snrichProductRateRepository.findAll());
		model.addAttribute("orderProPaymentModes", OrderProPaymentMode.values());
		return "site_admin/snrich-product-rate";
	}
	
	
	@RequestMapping(value = "/snrich-product-rate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SnrichProductRateDTO> createSnrichProductRate(@Valid 
			@RequestBody SnrichProductRateDTO snrichProductRateDTO) throws URISyntaxException {
		log.debug("Web request to save SnrichProductRate : {}", snrichProductRateDTO);
		if (snrichProductRateDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productRate", "idexists", "A new product rate cannot already have an ID"))
					.body(null);
		}
		SnrichProductRateDTO result = snrichProductRateService.save(snrichProductRateDTO);
		return ResponseEntity.created(new URI("/web/snrichProductRate/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("product rate", result.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/snrich-product-rate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SnrichProductRateDTO> updateSnrichProductRate(@Valid 
			@RequestBody SnrichProductRateDTO snrichProductRateDTO)throws URISyntaxException {
		log.debug("REST request to update Product Rate : {}", snrichProductRateDTO);
		
		if (snrichProductRateDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("product rate", "idNotexists", "Product Rate must have an ID"))
					.body(null);
		}
		
		SnrichProductRateDTO result = snrichProductRateService.update(snrichProductRateDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("product rate", "idNotexists", "Invalid Product Rate ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("product rate", snrichProductRateDTO.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/snrich-product-rate/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SnrichProductRateDTO> getSnrichProductRate(@PathVariable String pid) {
		log.debug("Web request to get ProductRate by pid : {}", pid);
		return snrichProductRateService.findOneByPid(pid).map(snrichProductRateDTO -> new ResponseEntity<>(snrichProductRateDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/snrich-product-rate/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductRate(@PathVariable String pid) {
		log.debug("REST request to delete Product Rate : {}", pid);
		snrichProductRateService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productRate", pid.toString())).build();
	}
}
