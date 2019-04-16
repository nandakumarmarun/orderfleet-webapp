package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DistanceFare;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DistanceFareRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DistanceFareDTO;

@Service
@Transactional
public class DistanceFareServiceImpl implements DistanceFareService {
	
	private final Logger log = LoggerFactory.getLogger(DistanceFareServiceImpl.class);
	
	@Inject
	private DistanceFareRepository distanceFareRepository;
	
	@Inject
	private CompanyRepository companyRepository;

	@Override
	public DistanceFareDTO save(DistanceFareDTO distanceFareDTO) {
		log.debug("Request to save DistanceFare : {}", distanceFareDTO);
		distanceFareDTO.setPid(DistanceFareService.PID_PREFIX + RandomUtil.generatePid()); //set Pid
		
		DistanceFare distanceFare = new DistanceFare();
		distanceFare.setPid(distanceFareDTO.getPid());
		distanceFare.setVehicleType(distanceFareDTO.getVehicleType());
		distanceFare.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		distanceFare.setFare(distanceFareDTO.getFare());
		distanceFare = distanceFareRepository.save(distanceFare);
		DistanceFareDTO result = new DistanceFareDTO(distanceFare);
		return result;
	}

	@Override
	public DistanceFareDTO update(DistanceFareDTO distanceFareDTO) {
		log.debug("Request to Update DistanceFare : {}", distanceFareDTO);
		
		return distanceFareRepository.findOneByPid(distanceFareDTO.getPid()).map(distanceFare -> {
			distanceFare.setVehicleType(distanceFareDTO.getVehicleType());
			distanceFare.setFare(distanceFareDTO.getFare());
			distanceFare = distanceFareRepository.save(distanceFare);
			DistanceFareDTO result = new DistanceFareDTO(distanceFare);
			return result;
		}).orElse(null);
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete DistanceFare : {}", pid);
		distanceFareRepository.findOneByPid(pid).ifPresent(distanceFare -> {
			distanceFareRepository.delete(distanceFare.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DistanceFareDTO> findOneByPid(String pid) {
		log.debug("Request to get DistanceFare by pid : {}", pid);
		return distanceFareRepository.findOneByPid(pid).map(distanceFare ->{
			DistanceFareDTO result = new DistanceFareDTO(distanceFare);
			return result;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<DistanceFareDTO> findAllByCompany() {
		log.debug("Request to get all DistanceFare");
		List<DistanceFare> distanceFareList = distanceFareRepository.findAllByCompanyId();
		List<DistanceFareDTO> result = new ArrayList<DistanceFareDTO>();
		distanceFareList.forEach(distanceFare ->{
			DistanceFareDTO distanceFareDTO = new DistanceFareDTO(distanceFare);
			result.add(distanceFareDTO);
		});
		return result;
	}

}
