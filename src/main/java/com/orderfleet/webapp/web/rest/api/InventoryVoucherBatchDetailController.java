package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.InventoryVoucherBatchDetailService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;

/**
 * REST controller for getting tasks assigned to a User.
 * 
 * @author Sarath
 * @since Dec 7, 2016
 */
@RestController
@RequestMapping(value = "/api/inventory-voucher-batch-detail", produces = MediaType.APPLICATION_JSON_VALUE)
public class InventoryVoucherBatchDetailController {

	@Inject
	private InventoryVoucherBatchDetailService inventoryVoucherBatchDetailService;

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherBatchDetailController.class);

	/**
	 * POST /inventory-voucher-batch-detail : Create a new
	 * inventoryVoucherBatchDetail.
	 *
	 * @param inventoryVoucherBatchDetailDTO
	 *            the inventoryVoucherBatchDetailDTO to create
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void executiveTaskSubmission(
			@Valid @RequestBody InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO)
			throws URISyntaxException {
		log.debug("Web request to save ExecutiveTaskExecution");
		inventoryVoucherBatchDetailService.save(inventoryVoucherBatchDetailDTO);
	}

}
