package com.orderfleet.webapp.service.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.repository.BestPerformerRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.BestPerformerService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BestPerformerUploadDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;


@Service
@Transactional
public class BestPerformerServiceImpl  implements BestPerformerService {


	private final Logger log = LoggerFactory.getLogger(BestPerformerServiceImpl.class);
	

	@Inject
	private BestPerformerRepository companyDetailRepository;

	@Override
	@Transactional(readOnly = true)
	public Optional<BestPerformerUploadDTO> findOneByPid(String pid) {
		log.debug("Request to get Company by pid : {}", pid);
		return companyDetailRepository.findOneByPid(pid).map(company -> {
			BestPerformerUploadDTO bestPerformerDTO = new BestPerformerUploadDTO(company);
			return bestPerformerDTO;
		});
	}
	
	@Override
	public BestPerformerUploadDTO update(BestPerformerUploadDTO bestPerformerDTO) {
		log.debug("Request to Update BestPerformer : {}", bestPerformerDTO);

		return companyDetailRepository.findOneByPid(bestPerformerDTO.getPid()).map(company -> {
			company.setLogo(bestPerformerDTO.getLogo());
			company.setLogoContentType(bestPerformerDTO.getLogoContentType());
			company = companyDetailRepository.save(company);
			BestPerformerUploadDTO result = new BestPerformerUploadDTO(company);
			return result;
		}).orElse(null);
	}

}
