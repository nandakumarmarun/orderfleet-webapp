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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.OrderStatusService;
import com.orderfleet.webapp.web.rest.dto.OrderStatusDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class OrderStatusResource {

	private final Logger log = LoggerFactory.getLogger(OrderStatusResource.class);

	@Inject
	private OrderStatusService orderStatusService;

	@RequestMapping(value = "/order-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<OrderStatusDTO> createOrderStatus(@Valid @RequestBody OrderStatusDTO orderStatusDTO)
			throws URISyntaxException {
		log.debug("Web request to save OrderStatus : {}", orderStatusDTO);
		if (orderStatusDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("orderStatus", "idexists",
					"A new orderStatus cannot already have an ID")).body(null);
		}
		if (orderStatusService.findByName(orderStatusDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("orderStatus", "nameexists", "OrderStatus already in use"))
					.body(null);
		}
		OrderStatusDTO result = orderStatusService.save(orderStatusDTO);
		return ResponseEntity.created(new URI("/web/order-status/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("orderStatus", result.getId().toString())).body(result);
	}

	@RequestMapping(value = "/order-status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<OrderStatusDTO> updateOrderStatus(@Valid @RequestBody OrderStatusDTO orderStatusDTO) {
		log.debug("REST request to update OrderStatus : {}", orderStatusDTO);
		if (orderStatusDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("orderStatus", "idNotexists", "OrderStatus must have an ID"))
					.body(null);
		}
		Optional<OrderStatusDTO> existingOrderStatus = orderStatusService.findByName(orderStatusDTO.getName());
		if (existingOrderStatus.isPresent() && (!existingOrderStatus.get().getId().equals(orderStatusDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("orderStatus", "nameexists", "OrderStatus already in use"))
					.body(null);
		}
		OrderStatusDTO result = orderStatusService.update(orderStatusDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("orderStatus", "idNotexists", "Invalid OrderStatus ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("orderStatus", orderStatusDTO.getId().toString()))
				.body(result);
	}

	@RequestMapping(value = "/order-status", method = RequestMethod.GET)
	@Timed
	public String getAllOrderStatuss(Model model) {
		log.debug("Web request to get a page of OrderStatuss");
		List<OrderStatusDTO> orderStatuss = orderStatusService.findAllByCompany();
		model.addAttribute("orderStatuses", orderStatuss);
		return "company/orderStatus";
	}

	@RequestMapping(value = "/order-status/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<OrderStatusDTO> getOrderStatus(@PathVariable Long id) {
		log.debug("Web request to get OrderStatus by id : {}", id);
		OrderStatusDTO orderStatusDTO = orderStatusService.findOne(id);
		return new ResponseEntity<>(orderStatusDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/order-status/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteOrderStatus(@PathVariable Long id) {
		log.debug("REST request to delete OrderStatus : {}", id);
		orderStatusService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("orderStatus", id.toString())).build();
	}

}
