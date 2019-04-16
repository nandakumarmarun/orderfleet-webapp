package com.orderfleet.webapp.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.mapper.StaticFormJSCodeMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api")
@Transactional(readOnly = true)
public class StaticFormController {

	private final Logger log = LoggerFactory.getLogger(StaticFormController.class);

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodesRepository;

	@Inject
	private StaticFormJSCodeMapper staticFormJSCodeMapper;

	/**
	 * POST /inventory-voucher : Create a new inventoryVoucher.
	 *
	 * @param inventoryVoucherDTO
	 *            the inventoryVoucherDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new inventoryVoucherDTO
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-voucher", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> createActivityGroup(
			@Valid @RequestBody InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) throws URISyntaxException {
		log.debug("Web request to save InventoryVoucher : {}", inventoryVoucherHeaderDTO);
		InventoryVoucherHeaderDTO result = inventoryVoucherService.save(inventoryVoucherHeaderDTO);
		return ResponseEntity.created(new URI("/api/inventory-voucher/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("inventoryVoucher", result.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /dynamic-formula : get all the StaticFormJSCode.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         staticFormJSCodes in body
	 */
	@RequestMapping(value = "/static-form-js-code", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<StaticFormJSCodeDTO> getAllActivityGroupUserTargets() {
		log.debug("REST request to get all staticFormJSCodes");
		List<StaticFormJSCode> staticFormJSCodes = staticFormJSCodesRepository.findAllByCompanyId();
		List<StaticFormJSCodeDTO> result = staticFormJSCodeMapper
				.staticFormJSCodesToStaticFormJSCodeDTOs(staticFormJSCodes);
		return result;
	}

}
