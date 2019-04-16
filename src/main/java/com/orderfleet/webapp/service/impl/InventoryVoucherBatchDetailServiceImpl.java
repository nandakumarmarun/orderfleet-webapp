package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherBatchDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryVoucherBatchDetailService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;

/**
 * Service Implementation for managing InventoryVoucherBatchDetail.
 *
 * @author Sarath
 * @since Dec 7, 2016
 */
@Service
@Transactional
public class InventoryVoucherBatchDetailServiceImpl implements InventoryVoucherBatchDetailService {

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherBatchDetailServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private InventoryVoucherBatchDetailRepository inventoryVoucherBatchDetailRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Override
	public InventoryVoucherBatchDetailDTO save(InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO) {

		log.debug("Request to save InventoryVoucherBatchDetail : {}", inventoryVoucherBatchDetailDTO);

		ProductProfile productProfile = productProfileRepository
				.findOneByPid(inventoryVoucherBatchDetailDTO.getProductProfilePid()).get();
		InventoryVoucherDetail inventoryVoucherDetail = inventoryVoucherDetailRepository
				.findOne(inventoryVoucherBatchDetailDTO.getInventoryVoucherDetailId());

		InventoryVoucherBatchDetail inventoryVoucherBatchDetail = new InventoryVoucherBatchDetail();
		inventoryVoucherBatchDetail.setBatchDate(inventoryVoucherBatchDetailDTO.getBatchDate());
		inventoryVoucherBatchDetail.setBatchNumber(inventoryVoucherBatchDetailDTO.getBatchNumber());
		inventoryVoucherBatchDetail.setQuantity(inventoryVoucherBatchDetailDTO.getQuantity());
		inventoryVoucherBatchDetail.setRemarks(inventoryVoucherBatchDetailDTO.getRemarks());
		inventoryVoucherBatchDetail.setProductProfile(productProfile);
		inventoryVoucherBatchDetail.setInventoryVoucherDetail(inventoryVoucherDetail);
		inventoryVoucherBatchDetail.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		inventoryVoucherBatchDetail = inventoryVoucherBatchDetailRepository.save(inventoryVoucherBatchDetail);
		InventoryVoucherBatchDetailDTO result = new InventoryVoucherBatchDetailDTO(inventoryVoucherBatchDetail);
		return result;
	}

	@Override
	public InventoryVoucherBatchDetailDTO update(InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InventoryVoucherBatchDetailDTO> findAllByCompany() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<InventoryVoucherBatchDetailDTO> findAllByCompany(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryVoucherBatchDetailDTO findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<InventoryVoucherBatchDetailDTO> findOneByPid(String pid) {
		// TODO Auto-generated method stub
		return null;
	}

}
