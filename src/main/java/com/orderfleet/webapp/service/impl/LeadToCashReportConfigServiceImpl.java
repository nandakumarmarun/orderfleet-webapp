package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.LeadToCashReportConfig;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LeadToCashReportConfigRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LeadToCashReportConfigService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LeadToCashReportConfigDTO;

@Service  
public class LeadToCashReportConfigServiceImpl implements LeadToCashReportConfigService {
 
	private final Logger log = LoggerFactory.getLogger(LeadToCashReportConfigServiceImpl.class);
	
	@Inject
	private LeadToCashReportConfigRepository leadToCashReportRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private StageRepository stageRepository;
	 
	@Override
	public List<LeadToCashReportConfigDTO> findAllByCompany() { 
		List<LeadToCashReportConfig> leadToCashReportList = leadToCashReportRepository.findAllByCompany();
		
		if(leadToCashReportList == null){
			return null;
		}
		List<LeadToCashReportConfigDTO> result = new ArrayList<>();
		leadToCashReportList.forEach(leadReport ->{
			LeadToCashReportConfigDTO leadToCashReportConfigDTO = new LeadToCashReportConfigDTO(leadReport);
				result.add(leadToCashReportConfigDTO);
		});
		return result;
	}
	
	@Override
	public List<String> findStagePidsByCompany() {
		return leadToCashReportRepository.findStagePidByCompany();
	}

	@Override
	public LeadToCashReportConfigDTO saveLeadToCashReportConfig(LeadToCashReportConfigDTO leadToCashReportConfigDTO) {
		log.debug("Request to save LeadToCashReportConfig : {}", leadToCashReportConfigDTO);
		LeadToCashReportConfig leadToCashReportConfig = new LeadToCashReportConfig();
		leadToCashReportConfig.setPid(LeadToCashReportConfigService.PID_PREFIX + RandomUtil.generatePid());
		leadToCashReportConfig.setName(leadToCashReportConfigDTO.getName());
		leadToCashReportConfig.setSortOrder(leadToCashReportConfigDTO.getSortOrder());
		leadToCashReportConfig.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		leadToCashReportConfig.setStage(stageRepository.findOneByPid(leadToCashReportConfigDTO.getStagetPid()).get());
		return new LeadToCashReportConfigDTO(leadToCashReportRepository.save(leadToCashReportConfig));
	}

	@Override
	public LeadToCashReportConfigDTO updateLeadToCashReportConfig(LeadToCashReportConfigDTO leadToCashReportConfigDTO) {
		log.debug("Request to Update LeadToCashReportConfig : {}", leadToCashReportConfigDTO);
		return leadToCashReportRepository.findOneByPid(leadToCashReportConfigDTO.getPid()).map(leadToCashReportConfig -> {
			leadToCashReportConfig.setName(leadToCashReportConfigDTO.getName());
			leadToCashReportConfig.setSortOrder(leadToCashReportConfigDTO.getSortOrder());
			leadToCashReportConfig.setStage(stageRepository.findOneByPid(leadToCashReportConfigDTO.getStagetPid()).get());
			return new LeadToCashReportConfigDTO(leadToCashReportRepository.save(leadToCashReportConfig));
		}).orElse(null);
	}

	@Override
	public Optional<LeadToCashReportConfigDTO> findLeadToCashReportConfigByPid(String pid) {
		log.debug("Request to get LeadToCashReportConfig by pid : {}", pid);
		return leadToCashReportRepository.findOneByPid(pid).map(leadToCashReportConfig -> {
			return new LeadToCashReportConfigDTO(leadToCashReportConfig);
		});
		
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete LeadToCashReportConfig : {}", pid);
		leadToCashReportRepository.findOneByPid(pid).ifPresent(leadToCashReportConfig -> {
			leadToCashReportRepository.delete(leadToCashReportConfig.getId());
		});
	}

	

}
