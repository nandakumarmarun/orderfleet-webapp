package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetBlockService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;

/**
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
@Service
@Transactional
public class SalesTargetBlockServiceImpl implements SalesTargetBlockService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetBlockServiceImpl.class);

	@Inject
	private SalesTargetBlockRepository salesTargetBlockRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a salesTargetBlock.
	 * 
	 * @param salesTargetBlockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetBlockDTO save(SalesTargetBlockDTO salesTargetBlockDTO) {
		log.debug("Request to save SalesTargetBlock : {}", salesTargetBlockDTO);
		SalesTargetBlock salesTargetBlock = new SalesTargetBlock();
		salesTargetBlock.setCreateDynamicLabel(salesTargetBlockDTO.getCreateDynamicLabel());
		salesTargetBlock.setDescription(salesTargetBlockDTO.getDescription());
		salesTargetBlock.setEndMonth(salesTargetBlockDTO.getEndMonth());
		salesTargetBlock.setEndMonthName(salesTargetBlockDTO.getEndMonthName());
		salesTargetBlock.setEndMonthMinus(salesTargetBlockDTO.getEndMonthMinus());
		salesTargetBlock.setEndMonthYearMinus(salesTargetBlockDTO.getEndMonthYearMinus());
		salesTargetBlock.setName(salesTargetBlockDTO.getName());
		salesTargetBlock.setStartMonth(salesTargetBlockDTO.getStartMonth());
		salesTargetBlock.setStartMonthName(salesTargetBlockDTO.getStartMonthName());
		salesTargetBlock.setStartMonthMinus(salesTargetBlockDTO.getStartMonthMinus());
		salesTargetBlock.setStartMonthYearMinus(salesTargetBlockDTO.getStartMonthYearMinus());
		salesTargetBlock.setTargetSettingType(salesTargetBlockDTO.getTargetSettingType());
		// set pid
		salesTargetBlock.setPid(SalesTargetBlockService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		salesTargetBlock.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesTargetBlock = salesTargetBlockRepository.save(salesTargetBlock);
		SalesTargetBlockDTO result = new SalesTargetBlockDTO(salesTargetBlock);
		return result;
	}

	/**
	 * Update a salesTargetBlock.
	 * 
	 * @param salesTargetBlockDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetBlockDTO update(SalesTargetBlockDTO salesTargetBlockDTO) {
		log.debug("Request to Update SalesTargetBlock : {}", salesTargetBlockDTO);

		return salesTargetBlockRepository.findOneByPid(salesTargetBlockDTO.getPid()).map(salesTargetBlock -> {
			salesTargetBlock.setCreateDynamicLabel(salesTargetBlockDTO.getCreateDynamicLabel());
			salesTargetBlock.setDescription(salesTargetBlockDTO.getDescription());
			salesTargetBlock.setEndMonth(salesTargetBlockDTO.getEndMonth());
			salesTargetBlock.setEndMonthName(salesTargetBlockDTO.getEndMonthName());
			salesTargetBlock.setEndMonthMinus(salesTargetBlockDTO.getEndMonthMinus());
			salesTargetBlock.setEndMonthYearMinus(salesTargetBlockDTO.getEndMonthYearMinus());
			salesTargetBlock.setName(salesTargetBlockDTO.getName());
			salesTargetBlock.setStartMonth(salesTargetBlockDTO.getStartMonth());
			salesTargetBlock.setStartMonthName(salesTargetBlockDTO.getStartMonthName());
			salesTargetBlock.setStartMonthMinus(salesTargetBlockDTO.getStartMonthMinus());
			salesTargetBlock.setStartMonthYearMinus(salesTargetBlockDTO.getStartMonthYearMinus());
			salesTargetBlock.setTargetSettingType(salesTargetBlockDTO.getTargetSettingType());
			salesTargetBlock = salesTargetBlockRepository.save(salesTargetBlock);
			SalesTargetBlockDTO result = new SalesTargetBlockDTO(salesTargetBlock);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the salesTargetBlocks.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetBlockDTO> findAllByCompany() {
		log.debug("Request to get all SalesTargetBlocks");
		List<SalesTargetBlock> salesTargetBlockList = salesTargetBlockRepository.findAllByCompanyId();
		List<SalesTargetBlockDTO> result = salesTargetBlockList.stream().map(SalesTargetBlockDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one salesTargetBlock by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetBlockDTO> findOneByPid(String pid) {
		log.debug("Request to get SalesTargetBlock by pid : {}", pid);
		return salesTargetBlockRepository.findOneByPid(pid).map(salesTargetBlock -> {
			SalesTargetBlockDTO salesTargetBlockDTO = new SalesTargetBlockDTO(salesTargetBlock);
			return salesTargetBlockDTO;
		});
	}

	/**
	 * Get one salesTargetBlock by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetBlockDTO> findByName(String name) {
		log.debug("Request to get SalesTargetBlock by name : {}", name);
		return salesTargetBlockRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(salesTargetBlock -> {
					SalesTargetBlockDTO salesTargetBlockDTO = new SalesTargetBlockDTO(salesTargetBlock);
					return salesTargetBlockDTO;
				});
	}

	/**
	 * Delete the salesTargetBlock by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete SalesTargetBlock : {}", pid);
		salesTargetBlockRepository.findOneByPid(pid).ifPresent(salesTargetBlock -> {
			salesTargetBlockRepository.delete(salesTargetBlock.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetBlockDTO> findAllByCompanyIdAndtargetSettingType(BestPerformanceType targetSettingType) {
		log.debug("Request to get all SalesTargetBlocks by targetSettingType" , targetSettingType);
		List<SalesTargetBlock> salesTargetBlockList = salesTargetBlockRepository.findAllByCompanyIdAndtargetSettingType(targetSettingType);
		List<SalesTargetBlockDTO> result = salesTargetBlockList.stream().map(SalesTargetBlockDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
