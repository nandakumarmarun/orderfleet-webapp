package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.IncomeExpenseHead;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.IncomeExpenseHeadRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.IncomeExpenseHeadService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;

import com.orderfleet.webapp.web.rest.mapper.IncomeExpenseHeadMapper;

/**
 *Service Implementation for managing IncomeExpenseHead.
 *
 * @author fahad
 * @since Feb 15, 2017
 */
@Service
@Transactional
public class IncomeExpenseHeadServiceImpl implements IncomeExpenseHeadService{

	private final Logger log = LoggerFactory.getLogger(IncomeExpenseHeadServiceImpl.class);

	@Inject
	private IncomeExpenseHeadRepository incomeExpenseHeadRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private IncomeExpenseHeadMapper incomeExpenseHeadMapper;
	
	
	@Override
	public IncomeExpenseHeadDTO save(IncomeExpenseHeadDTO incomeExpenseHeadDTO) {
		log.debug("Request to save IncomeExpenseHead : {}", incomeExpenseHeadDTO);
		incomeExpenseHeadDTO.setPid(IncomeExpenseHeadService.PID_PREFIX + RandomUtil.generatePid()); // set
																			// pid
		IncomeExpenseHead incomeExpenseHead = incomeExpenseHeadMapper.incomeExpenseHeadDTOToIncomeExpenseHead(incomeExpenseHeadDTO);
		incomeExpenseHead.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		incomeExpenseHead = incomeExpenseHeadRepository.save(incomeExpenseHead);
		IncomeExpenseHeadDTO result = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
		return result;
		
	}

	@Override
	public IncomeExpenseHeadDTO update(IncomeExpenseHeadDTO incomeExpenseHeadDTO) {
		log.debug("Request to Update IncomeExpenseHead : {}", incomeExpenseHeadDTO);

		return incomeExpenseHeadRepository.findOneByPid(incomeExpenseHeadDTO.getPid()).map(incomeExpenseHead -> {
			incomeExpenseHead.setName(incomeExpenseHeadDTO.getName());
			incomeExpenseHead.setAlias(incomeExpenseHeadDTO.getAlias());
			incomeExpenseHead.setDescription(incomeExpenseHeadDTO.getDescription());

			incomeExpenseHead = incomeExpenseHeadRepository.save(incomeExpenseHead);
			IncomeExpenseHeadDTO result = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
			return result;
		}).orElse(null);
	}

	@Override
	public List<IncomeExpenseHeadDTO> findAllByCompany() {
		log.debug("Request to get all IncomeExpenseHeads");
		List<IncomeExpenseHead> incomeExpenseHeadList = incomeExpenseHeadRepository.findAllByCompanyId();
		List<IncomeExpenseHeadDTO> result = incomeExpenseHeadMapper.incomeExpenseHeadsToIncomeExpenseHeadDTOs(incomeExpenseHeadList);
		return result;
	}

	@Override
	public Page<IncomeExpenseHeadDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all IncomeExpenseHeads");
		Page<IncomeExpenseHead> incomeExpenseHeads = incomeExpenseHeadRepository.findAllByCompanyIdOrderByIncomeExpenseHeadName(pageable);
		Page<IncomeExpenseHeadDTO> result = new PageImpl<IncomeExpenseHeadDTO>(incomeExpenseHeadMapper.incomeExpenseHeadsToIncomeExpenseHeadDTOs(incomeExpenseHeads.getContent()), pageable,
				incomeExpenseHeads.getTotalElements());
		return result;
	}

	@Override
	public IncomeExpenseHeadDTO findOne(Long id) {
		log.debug("Request to get IncomeExpenseHead : {}", id);
		IncomeExpenseHead incomeExpenseHead = incomeExpenseHeadRepository.findOne(id);
		IncomeExpenseHeadDTO incomeExpenseHeadDTO = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
		return incomeExpenseHeadDTO;
	}

	@Override
	public Optional<IncomeExpenseHeadDTO> findOneByPid(String pid) {
		log.debug("Request to get IncomeExpenseHead by pid : {}", pid);
		return incomeExpenseHeadRepository.findOneByPid(pid).map(incomeExpenseHead -> {
			IncomeExpenseHeadDTO incomeExpenseHeadDTO = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
			return incomeExpenseHeadDTO;
		});
	}

	@Override
	public Optional<IncomeExpenseHeadDTO> findByName(String name) {
		log.debug("Request to get IncomeExpenseHead by name : {}", name);
		return incomeExpenseHeadRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(incomeExpenseHead -> {
					IncomeExpenseHeadDTO incomeExpenseHeadDTO = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
					return incomeExpenseHeadDTO;
				});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete IncomeExpenseHead : {}", pid);
		incomeExpenseHeadRepository.findOneByPid(pid).ifPresent(incomeExpenseHead -> {
			incomeExpenseHeadRepository.delete(incomeExpenseHead.getId());
		});
	}

	@Override
	public IncomeExpenseHeadDTO updateIncomeExpenseHeadStatus(String pid, boolean activate) {
		log.debug("Request to update IncomeExpenseHead status: {}");
		return incomeExpenseHeadRepository.findOneByPid(pid).map(incomeExpenseHead -> {
			incomeExpenseHead.setActivated(activate);
			incomeExpenseHead = incomeExpenseHeadRepository.save(incomeExpenseHead);
			IncomeExpenseHeadDTO result = incomeExpenseHeadMapper.incomeExpenseHeadToIncomeExpenseHeadDTO(incomeExpenseHead);
			return result;
		}).orElse(null);
	}

	@Override
	public Page<IncomeExpenseHeadDTO> findAllByCompanyAndActivatedIncomeExpenseHeadOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get Activated IncomeExpenseHead ");
		Page<IncomeExpenseHead> pageIncomeExpenseHeads = incomeExpenseHeadRepository.findAllByCompanyAndActivatedIncomeExpenseHeadOrderByName(pageable, active);
		Page<IncomeExpenseHeadDTO> pageIncomeExpenseHeadDTOs = new PageImpl<IncomeExpenseHeadDTO>(incomeExpenseHeadMapper.incomeExpenseHeadsToIncomeExpenseHeadDTOs(pageIncomeExpenseHeads.getContent()),pageable,pageIncomeExpenseHeads.getTotalElements());
		return pageIncomeExpenseHeadDTOs;
	}

	@Override
	public List<IncomeExpenseHeadDTO> findAllByCompanyAndDeactivatedIncomeExpenseHead(boolean deactive) {
		log.debug("Request to get Deactivated IncomeExpenseHead ");
		List<IncomeExpenseHead> incomeExpenseHeads = incomeExpenseHeadRepository.findAllByCompanyAndDeactivatedIncomeExpenseHead(deactive);
		List<IncomeExpenseHeadDTO> incomeExpenseHeadDTOs = incomeExpenseHeadMapper.incomeExpenseHeadsToIncomeExpenseHeadDTOs(incomeExpenseHeads);
		return incomeExpenseHeadDTOs;
	}

}

