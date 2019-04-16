package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.InventoryOrderStatusHistory;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.repository.InventoryOrderStatusHistoryRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OrderStatusRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SetOrderStatusInventoryService;
import com.orderfleet.webapp.service.util.RandomUtil;

@Service
@Transactional
public class SetOrderStatusInventoryServiceImpl implements SetOrderStatusInventoryService {

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryOrderStatusHistoryRepository inventoryOrderStatusHistoryRepository;

	@Inject
	private OrderStatusRepository orderStatusRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public void saveSetOrderStatusInventoryHistory(String inventoryPid, String status) {
		Optional<InventoryVoucherHeader> inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryPid);
		Optional<OrderStatus> orderStatus = orderStatusRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), status);
		if (inventoryVoucherHeader.isPresent() && orderStatus.isPresent()) {
			InventoryOrderStatusHistory inventoryOrderStatusHistory = new InventoryOrderStatusHistory();
			inventoryOrderStatusHistory.setAccountProfile(inventoryVoucherHeader.get().getReceiverAccount());
			inventoryOrderStatusHistory.setCompany(inventoryVoucherHeader.get().getCompany());
			inventoryOrderStatusHistory.setInventoryVoucherHeader(inventoryVoucherHeader.get());
			inventoryOrderStatusHistory.setStatusUpdatedDate(LocalDateTime.now());
			inventoryOrderStatusHistory
					.setStatusUpdatedUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
			inventoryOrderStatusHistory.setOrderAmount(inventoryVoucherHeader.get().getDocumentTotal());
			inventoryOrderStatusHistory.setOrderDate(inventoryVoucherHeader.get().getDocumentDate());
			inventoryOrderStatusHistory.setOrderStatus(orderStatus.get());
			inventoryOrderStatusHistory.setPid(SetOrderStatusInventoryService.PID_PREFIX + RandomUtil.generatePid());
			inventoryOrderStatusHistory.setOrderStatus(orderStatus.get());
			inventoryOrderStatusHistoryRepository.save(inventoryOrderStatusHistory);

			inventoryVoucherHeader.get().setOrderStatus(orderStatus.get());
			inventoryVoucherHeaderRepository.save(inventoryVoucherHeader.get());
		}

	}

}
