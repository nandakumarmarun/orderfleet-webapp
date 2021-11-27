package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.orderfleet.webapp.web.rest.InventoryVoucherTransactionResource;

@Service
@Transactional
public class SetOrderStatusInventoryServiceImpl implements SetOrderStatusInventoryService {

	private final Logger log = LoggerFactory.getLogger(SetOrderStatusInventoryServiceImpl.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		Optional<InventoryVoucherHeader> inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryPid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
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
