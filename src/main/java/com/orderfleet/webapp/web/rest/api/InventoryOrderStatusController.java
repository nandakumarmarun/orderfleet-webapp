package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

@RestController
@RequestMapping("/api")
public class InventoryOrderStatusController {

	private final Logger log = LoggerFactory.getLogger(InventoryOrderStatusController.class);

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@GetMapping("/inventory-order-status")
	@Timed
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> getInventoryVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") LocalDateTime fromDate,
			@RequestParam(required = false, value = "toDate") LocalDateTime toDate) {
		log.debug("request for get inventory-order-status ");
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();
			if (fromDate != null && toDate != null) {
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdUserIdAndDateBetweenOrderByDocumentDateDesc(user.getId(), fromDate, toDate);
				inventoryVoucherHeaderDTOs = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
			} else {
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findTop3ByCompanyIdAndExecutiveTaskExecutionUserPidOrderByDocumentDateDesc(
								user.getCompany().getId(), user.getPid());
				inventoryVoucherHeaderDTOs = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
			}
		}
		return new ResponseEntity<List<InventoryVoucherHeaderDTO>>(inventoryVoucherHeaderDTOs, HttpStatus.OK);

	}
}
