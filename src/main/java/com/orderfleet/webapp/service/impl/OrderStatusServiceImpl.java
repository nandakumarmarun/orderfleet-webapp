package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.repository.OrderStatusRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.OrderStatusService;
import com.orderfleet.webapp.web.rest.dto.OrderStatusDTO;

@Service
@Transactional
public class OrderStatusServiceImpl implements OrderStatusService {

	private final Logger log = LoggerFactory.getLogger(OrderStatusServiceImpl.class);

	@Inject
	private OrderStatusRepository orderStatusRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public OrderStatusDTO save(OrderStatusDTO orderStatusDTO) {
		log.debug("Request to save OrderStatus : {}", orderStatusDTO);
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setName(orderStatusDTO.getName());
		orderStatus.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		orderStatus.setDocumentType(orderStatusDTO.getDocumentType());
		orderStatus.setActive(true);
		orderStatus = orderStatusRepository.save(orderStatus);
		OrderStatusDTO result = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(), orderStatus.getActive(),
				orderStatus.getDocumentType());
		return result;
	}

	@Override
	public OrderStatusDTO update(OrderStatusDTO orderStatusDTO) {
		log.debug("Request to Update OrderStatus : {}", orderStatusDTO);
		OrderStatus orderStatus = orderStatusRepository.findOne(orderStatusDTO.getId());
		orderStatus.setName(orderStatusDTO.getName());
		orderStatus.setDocumentType(orderStatusDTO.getDocumentType());
		orderStatus = orderStatusRepository.save(orderStatus);
		OrderStatusDTO result = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(), orderStatus.getActive(),
				orderStatus.getDocumentType());
		return result;

	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderStatusDTO> findAllByCompany() {
		log.debug("Request to get all OrderStatuss");
		List<OrderStatus> orderStatuses = orderStatusRepository.findAllByCompanyId();
		List<OrderStatusDTO> orderStatusDTOs = new ArrayList<>();
		for (OrderStatus orderStatus : orderStatuses) {
			OrderStatusDTO orderStatusDTO = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(),
					orderStatus.getActive(), orderStatus.getDocumentType());
			orderStatusDTOs.add(orderStatusDTO);
		}
		return orderStatusDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public OrderStatusDTO findOne(Long id) {
		log.debug("Request to get OrderStatus : {}", id);
		OrderStatus orderStatus = orderStatusRepository.findOne(id);
		OrderStatusDTO result = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(), orderStatus.getActive(),
				orderStatus.getDocumentType());
		return result;
	}

	@Override
	public Optional<OrderStatusDTO> findByName(String name) {
		log.debug("Request to get OrderStatus by name : {}", name);
		return orderStatusRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(orderStatus -> {
					OrderStatusDTO result = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(),
							orderStatus.getActive(), orderStatus.getDocumentType());
					return result;
				});
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete OrderStatus : {}", id);
		OrderStatus orderStatus = orderStatusRepository.findOne(id);
		if (orderStatus != null) {
			orderStatusRepository.delete(id);
		}
	}

	@Override
	public List<OrderStatusDTO> findAllByDocumentType(DocumentType documentType) {
		List<OrderStatus> orderStatuses = orderStatusRepository.findAllByDocumentType(documentType);
		List<OrderStatusDTO> orderStatusDTOs = new ArrayList<>();
		for (OrderStatus orderStatus : orderStatuses) {
			OrderStatusDTO orderStatusDTO = new OrderStatusDTO(orderStatus.getId(), orderStatus.getName(),
					orderStatus.getActive(), orderStatus.getDocumentType());
			orderStatusDTOs.add(orderStatusDTO);
		}
		return orderStatusDTOs;
	}

}
