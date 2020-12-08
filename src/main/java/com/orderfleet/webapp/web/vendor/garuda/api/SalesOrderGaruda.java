package com.orderfleet.webapp.web.vendor.garuda.api;

import java.util.List;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.AccountProfileGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.SalesOrderGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.service.AccountProfileGarudaUploadService;
import com.orderfleet.webapp.web.vendor.garuda.service.SalesOrderGarudaService;

@RestController
@RequestMapping(value = "/api/garuda")
public class SalesOrderGaruda {
private final Logger log = LoggerFactory.getLogger(AccountProfileGaruda.class);
	
	@Inject
	private SalesOrderGarudaService salesOrderGarudaService;
	
	@RequestMapping(value = "/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SalesOrderGarudaDTO>> sendTransactionsSapPravesh() throws MessagingException {

		log.info("sendSalesOrderGaruda()-----");

		 List<SalesOrderGarudaDTO> list = salesOrderGarudaService.sendSalesOrder();

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
