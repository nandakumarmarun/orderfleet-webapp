package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupSalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupSalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

@Service
@Transactional
public class ProductGroupSalesTargetGroupServiceImpl implements ProductGroupSalesTargetGroupService {

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	@Override
	public void saveProductGroupSalesTargetGroup(String productGroupPid, String SalesTargetGroupPids) {
		List<ProductGroupSalesTargetGroup> productGroupSalesTargetGroups = new ArrayList<>();
		productGroupSalesTargetGrouprepository.deleteByProductGroupPid(productGroupPid);
		ProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		String[] salesTargetGrouppids = SalesTargetGroupPids.split(",");
		for (String salesTargetGroupPid : salesTargetGrouppids) {
			SalesTargetGroup salesTargetGroup = salesTargetGroupRepository.findOneByPid(salesTargetGroupPid).get();
			ProductGroupSalesTargetGroup productGroupSalesTargetGroup = new ProductGroupSalesTargetGroup(productGroup,
					salesTargetGroup, productGroup.getCompany());
			productGroupSalesTargetGroups.add(productGroupSalesTargetGroup);
		}
		productGroupSalesTargetGrouprepository.save(productGroupSalesTargetGroups);
	}

	@Override
	public List<ProductGroupSalesTargetGroupDTO> findAllByCompanyId() {
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);

		List<ProductGroupSalesTargetGroupDTO> productGroupSalesTargetGroupDTOs = new ArrayList<>();
		for (ProductGroup productGroup : productGroups) {
			List<SalesTargetGroup> salesTargetGroups = productGroupSalesTargetGrouprepository
					.findSalesTargetGroupByProductGroupPid(productGroup.getPid());
			List<SalesTargetGroupDTO> salesTargetGroupDTOs = new ArrayList<>();
			for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
				SalesTargetGroupDTO salesTargetGroupDTO = new SalesTargetGroupDTO();
				salesTargetGroupDTO.setName(salesTargetGroup.getName());
				salesTargetGroupDTOs.add(salesTargetGroupDTO);
			}
			ProductGroupSalesTargetGroupDTO productGroupSalesTargetGroupDTO = new ProductGroupSalesTargetGroupDTO(
					productGroup.getName(), salesTargetGroupDTOs);
			if (!salesTargetGroups.isEmpty()) {
				productGroupSalesTargetGroupDTOs.add(productGroupSalesTargetGroupDTO);
			}
		}

		return productGroupSalesTargetGroupDTOs;
	}

	@Override
	public ProductGroupSalesTargetGroupDTO findAllByProductGroupPid(String productGroupPid) {
		List<SalesTargetGroup> salesTargetGroups = productGroupSalesTargetGrouprepository
				.findSalesTargetGroupByProductGroupPid(productGroupPid);
		ProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		List<SalesTargetGroupDTO> salesTargetGroupDTOs = new ArrayList<>();
		for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
			SalesTargetGroupDTO salesTargetGroupDTO = new SalesTargetGroupDTO(salesTargetGroup);
			salesTargetGroupDTOs.add(salesTargetGroupDTO);
		}
		ProductGroupSalesTargetGroupDTO productGroupSalesTargetGroupDTO = new ProductGroupSalesTargetGroupDTO();
		if (!salesTargetGroupDTOs.isEmpty()) {
			productGroupSalesTargetGroupDTO.setProductGroupName(productGroup.getName());
			productGroupSalesTargetGroupDTO.setSalesTargetGroups(salesTargetGroupDTOs);
		}

		return productGroupSalesTargetGroupDTO;
	}

	@Override
	public List<ProductGroupDTO> findAllProductGroupByCompany() {
		List<ProductGroup> productGroups = productGroupSalesTargetGrouprepository.findAllProductGroupByCompanyId();
		List<ProductGroup> newProductGroups = new ArrayList<>(new HashSet<>(productGroups));
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(newProductGroups);
		return productGroupDTOs;
	}

}
