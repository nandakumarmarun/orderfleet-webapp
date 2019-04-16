package com.orderfleet.webapp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardItemService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardItemDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Service Implementation for managing DashboardItem.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Service
@Transactional
public class DashboardItemServiceImpl implements DashboardItemService {

	private final Logger log = LoggerFactory.getLogger(DashboardItemServiceImpl.class);

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private ProductGroupRepository productGroupRepository;
	
	@Inject
	private SalesTargetBlockRepository salesTargetBlockRepository;
	
	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;
	
	/**
	 * Save a dashboardItem.
	 * 
	 * @param dashboardItemDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DashboardItemDTO save(DashboardItemDTO dashboardItemDTO) {
		log.debug("Request to save DashboardItem : {}", dashboardItemDTO);

		DashboardItem dashboardItem = new DashboardItem();
		// set pid
		dashboardItem.setPid(DashboardItemService.PID_PREFIX + RandomUtil.generatePid());
		dashboardItem.setName(dashboardItemDTO.getName());
		dashboardItem.setSortOrder(dashboardItemDTO.getSortOrder());
		dashboardItem.setDashboardItemType(dashboardItemDTO.getDashboardItemType());
		dashboardItem.setDocumentType(dashboardItemDTO.getDocumentType());
		dashboardItem.setTaskPlanType(dashboardItemDTO.getTaskPlanType());
		dashboardItem.setDashboardItemConfigType(dashboardItemDTO.getDashboardItemConfigType());
		if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
			Set<Activity> newActivities = new HashSet<>();
			for (ActivityDTO activityDTO : dashboardItemDTO.getActivities()) {
				newActivities.add(activityRepository.findOneByPid(activityDTO.getPid()).get());
			}
			dashboardItem.setActivities(newActivities);
		} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
			Set<Document> newDocuments = new HashSet<>();
			for (DocumentDTO documentDTO : dashboardItemDTO.getDocuments()) {
				newDocuments.add(documentRepository.findOneByPid(documentDTO.getPid()).get());
			}
			dashboardItem.setDocuments(newDocuments);
		} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.PRODUCT)) {
			Set<Document> newDocuments = new HashSet<>();
			for (DocumentDTO documentDTO : dashboardItemDTO.getDocuments()) {
				newDocuments.add(documentRepository.findOneByPid(documentDTO.getPid()).get());
			}
			Set<ProductGroup> newProductGroups = new HashSet<>();
			for (ProductGroupDTO pgDTO : dashboardItemDTO.getProductGroups()) {
				newProductGroups.add(productGroupRepository.findOneByPid(pgDTO.getPid()).get());
			}
			dashboardItem.setDocuments(newDocuments);
			dashboardItem.setProductGroups(newProductGroups);
		} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.TARGET)) {
			dashboardItem.setSalesTargetGroup(salesTargetGroupRepository.findOneByPid(dashboardItemDTO.getSalesTargetGroupPid()).get());
			dashboardItem.setSalesTargetBlock(salesTargetBlockRepository.findOneByPid(dashboardItemDTO.getSalesTargetBlockPid()).get());
		}
		// set company
		dashboardItem.setCompany(companyRepository.getOne(SecurityUtils.getCurrentUsersCompanyId()));
		dashboardItem = dashboardItemRepository.save(dashboardItem);
		return new DashboardItemDTO(dashboardItem);
	}

	/**
	 * Update a dashboardItem.
	 * 
	 * @param dashboardItemDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DashboardItemDTO update(DashboardItemDTO dashboardItemDTO) {
		log.debug("Request to Update DashboardItem : {}", dashboardItemDTO);

		return dashboardItemRepository.findOneByPid(dashboardItemDTO.getPid()).map(dashboardItem -> {
			dashboardItem.setName(dashboardItemDTO.getName());
			dashboardItem.setSortOrder(dashboardItemDTO.getSortOrder());
			dashboardItem.setDashboardItemType(dashboardItemDTO.getDashboardItemType());
			dashboardItem.setDocumentType(dashboardItemDTO.getDocumentType());
			dashboardItem.setTaskPlanType(dashboardItemDTO.getTaskPlanType());
			dashboardItem.setDashboardItemConfigType(dashboardItemDTO.getDashboardItemConfigType());
			if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
				dashboardItem.setDocuments(null);
				Set<Activity> newActivities = new HashSet<>();
				for (ActivityDTO activityDTO : dashboardItemDTO.getActivities()) {
					newActivities.add(activityRepository.findOneByPid(activityDTO.getPid()).get());
				}
				dashboardItem.setActivities(newActivities);
			} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
				dashboardItem.setActivities(null);
				Set<Document> newDocuments = new HashSet<>();
				for (DocumentDTO documentDTO : dashboardItemDTO.getDocuments()) {
					newDocuments.add(documentRepository.findOneByPid(documentDTO.getPid()).get());
				}
				dashboardItem.setDocuments(newDocuments);
			} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.PRODUCT)) {
				dashboardItem.setActivities(null);
				Set<Document> newDocuments = new HashSet<>();
				for (DocumentDTO documentDTO : dashboardItemDTO.getDocuments()) {
					newDocuments.add(documentRepository.findOneByPid(documentDTO.getPid()).get());
				}
				Set<ProductGroup> newProductGroups = new HashSet<>();
				for (ProductGroupDTO pgDTO : dashboardItemDTO.getProductGroups()) {
					newProductGroups.add(productGroupRepository.findOneByPid(pgDTO.getPid()).get());
				}
				dashboardItem.setDocuments(newDocuments);
				dashboardItem.setProductGroups(newProductGroups);
			} else if (dashboardItemDTO.getDashboardItemType().equals(DashboardItemType.TARGET)) {
				dashboardItem.setDocuments(null);
				dashboardItem.setActivities(null);
				dashboardItem.setProductGroups(null);
				dashboardItem.setSalesTargetGroup(salesTargetGroupRepository.findOneByPid(dashboardItemDTO.getSalesTargetGroupPid()).get());
				dashboardItem.setSalesTargetBlock(salesTargetBlockRepository.findOneByPid(dashboardItemDTO.getSalesTargetBlockPid()).get());
			}
			dashboardItem = dashboardItemRepository.save(dashboardItem);
			return new DashboardItemDTO(dashboardItem);
		}).orElse(null);
	}

	/**
	 * Get all the dashboardItems.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DashboardItemDTO> findAllByCompany() {
		log.debug("Request to get all DashboardItems");
		List<DashboardItem> dashboardItemList = dashboardItemRepository.findAllByCompanyId();
		List<DashboardItemDTO> result = dashboardItemList.stream().map(DashboardItemDTO::new)
				.collect(Collectors.toList());
		return result;
	}
	

	@Override
	public List<DashboardItemDTO> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all DashboardItems");
		List<DashboardItem> dashboardItemList = dashboardItemRepository.findByCompanyId(companyId);
		List<DashboardItemDTO> result = dashboardItemList.stream().map(DashboardItemDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one dashboardItem by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DashboardItemDTO findOne(Long id) {
		log.debug("Request to get DashboardItem : {}", id);
		DashboardItem dashboardItem = dashboardItemRepository.findOne(id);
		DashboardItemDTO dashboardItemDTO = new DashboardItemDTO(dashboardItem);
		return dashboardItemDTO;
	}

	/**
	 * Get one dashboardItem by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DashboardItemDTO> findOneByPid(String pid) {
		log.debug("Request to get DashboardItem by pid : {}", pid);
		return dashboardItemRepository.findOneByPid(pid).map(dashboardItem -> {
			DashboardItemDTO dashboardItemDTO = new DashboardItemDTO(dashboardItem);
			return dashboardItemDTO;
		});
	}

	/**
	 * Get one dashboardItem by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DashboardItemDTO> findByName(String name) {
		log.debug("Request to get DashboardItem by name : {}", name);
		return dashboardItemRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(DashboardItemDTO::new);
	}

	/**
	 * Delete the dashboardItem by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete DashboardItem : {}", pid);
		dashboardItemRepository.findOneByPid(pid).ifPresent(dashboardItem -> {
			dashboardItemRepository.delete(dashboardItem.getId());
		});
	}

}
