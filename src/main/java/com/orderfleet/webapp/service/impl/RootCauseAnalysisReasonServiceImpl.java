package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.RootCauseAnalysisReason;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RootCauseAnalysisReasonRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RootCauseAnalysisReasonService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.RootCauseAnalysisReasonDTO;

@Service
@Transactional
public class RootCauseAnalysisReasonServiceImpl implements RootCauseAnalysisReasonService {
	
	private final Logger log = LoggerFactory.getLogger(RootCauseAnalysisReasonServiceImpl.class);
	
	@Inject
	private RootCauseAnalysisReasonRepository rootCauseAnalysisReasonRepository;
	
	@Inject
	private CompanyRepository companyRepository;

	@Override
	public RootCauseAnalysisReasonDTO save(RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO) {
		log.debug("Request to save RootCauseAnalysisReason : {}", rootCauseAnalysisReasonDTO);
		rootCauseAnalysisReasonDTO.setPid(RootCauseAnalysisReasonService.PID_PREFIX + RandomUtil.generatePid()); //set Pid
		
		RootCauseAnalysisReason rootCauseAnalysisReason = new RootCauseAnalysisReason();
		rootCauseAnalysisReason.setPid(rootCauseAnalysisReasonDTO.getPid());
		rootCauseAnalysisReason.setName(rootCauseAnalysisReasonDTO.getName());
		rootCauseAnalysisReason.setAlias(rootCauseAnalysisReasonDTO.getAlias());
		rootCauseAnalysisReason.setDescription(rootCauseAnalysisReasonDTO.getDescription());
		rootCauseAnalysisReason.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		rootCauseAnalysisReason = rootCauseAnalysisReasonRepository.save(rootCauseAnalysisReason);
		RootCauseAnalysisReasonDTO result = new RootCauseAnalysisReasonDTO(rootCauseAnalysisReason);
		return result;
	}

	@Override
	public RootCauseAnalysisReasonDTO update(RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO) {
		log.debug("Request to Update RootCauseAnalysisReason : {}", rootCauseAnalysisReasonDTO);
		
		return rootCauseAnalysisReasonRepository.findOneByPid(rootCauseAnalysisReasonDTO.getPid()).map(rootCauseAnalysisReason -> {
			rootCauseAnalysisReason.setName(rootCauseAnalysisReasonDTO.getName());
			rootCauseAnalysisReason.setAlias(rootCauseAnalysisReasonDTO.getAlias());
			rootCauseAnalysisReason.setDescription(rootCauseAnalysisReasonDTO.getDescription());
			rootCauseAnalysisReason = rootCauseAnalysisReasonRepository.save(rootCauseAnalysisReason);
			RootCauseAnalysisReasonDTO result = new RootCauseAnalysisReasonDTO(rootCauseAnalysisReason);
			return result;
		}).orElse(null);
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete RootCauseAnalysisReason : {}", pid);
		rootCauseAnalysisReasonRepository.findOneByPid(pid).ifPresent(rootCauseAnalysisReason -> {
			rootCauseAnalysisReasonRepository.delete(rootCauseAnalysisReason.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RootCauseAnalysisReasonDTO> findOneByPid(String pid) {
		log.debug("Request to get RootCauseAnalysisReason by pid : {}", pid);
		return rootCauseAnalysisReasonRepository.findOneByPid(pid).map(rootCauseAnalysisReason ->{
			RootCauseAnalysisReasonDTO result = new RootCauseAnalysisReasonDTO(rootCauseAnalysisReason);
			return result;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<RootCauseAnalysisReasonDTO> findAllByCompany() {
		log.debug("Request to get all DistanceFare");
		List<RootCauseAnalysisReason> rootCauseAnalysisReasonDTOList = rootCauseAnalysisReasonRepository.findAllByCompanyId();
		List<RootCauseAnalysisReasonDTO> result = new ArrayList<>();
		rootCauseAnalysisReasonDTOList.forEach(rootCauseAnalysisReason ->{
			RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO = new RootCauseAnalysisReasonDTO(rootCauseAnalysisReason);
			result.add(rootCauseAnalysisReasonDTO);
		});
		return result;
	}

}
