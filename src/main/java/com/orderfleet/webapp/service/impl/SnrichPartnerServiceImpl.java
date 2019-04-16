package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SnrichPartner;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.SnrichPartnerRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.service.SnrichPartnerService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SnrichPartnerDTO;

@Service
@Transactional
public class SnrichPartnerServiceImpl implements SnrichPartnerService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private SnrichPartnerRepository snrichPartnerRepository;
	
	@Inject
	private CountryRepository countryRepository;
	
	@Inject
	private StateRepository stateRepository;
	
	@Inject
	private DistrictRepository districtRepository;
	
	@Override
	public SnrichPartnerDTO save(SnrichPartnerDTO snrichPartnerDTO) {
		log.debug("request to save Partner : {}", snrichPartnerDTO);
		SnrichPartner snrichPartner = new SnrichPartner();
		snrichPartner.setPid(SnrichPartnerService.PID_PREFIX + RandomUtil.generatePid());
		snrichPartner.setName(snrichPartnerDTO.getName());
		snrichPartner.setAddress1(snrichPartnerDTO.getAddress1());
		snrichPartner.setAddress2(snrichPartnerDTO.getAddress2());
		snrichPartner.setCountry(countryRepository.findByCode(snrichPartnerDTO.getCountryCode()));
		snrichPartner.setState(stateRepository.findByCode(snrichPartnerDTO.getStateCode()));
		snrichPartner.setDistrict(districtRepository.findByCode(snrichPartnerDTO.getDistrictCode()));
		snrichPartner.setEmail(snrichPartnerDTO.getEmail());
		snrichPartner.setPhone(snrichPartnerDTO.getPhone());
		snrichPartner.setLocation(snrichPartnerDTO.getLocation());
		snrichPartner.setActivated(snrichPartnerDTO.getActivated());
		SnrichPartnerDTO result = new SnrichPartnerDTO(snrichPartnerRepository.save(snrichPartner));
		return result;
	}

	@Override
	public SnrichPartnerDTO update(SnrichPartnerDTO snrichPartnerDTO) {
		return snrichPartnerRepository.findOneByPid(snrichPartnerDTO.getPid()).map(snrichPartner -> {
			snrichPartner.setPid(snrichPartnerDTO.getPid());
			snrichPartner.setName(snrichPartnerDTO.getName());
			snrichPartner.setAddress1(snrichPartnerDTO.getAddress1());
			snrichPartner.setAddress2(snrichPartnerDTO.getAddress2());
			snrichPartner.setCountry(countryRepository.findByCode(snrichPartnerDTO.getCountryCode()));
			snrichPartner.setState(stateRepository.findByCode(snrichPartnerDTO.getStateCode()));
			snrichPartner.setDistrict(districtRepository.findByCode(snrichPartnerDTO.getDistrictCode()));
			snrichPartner.setEmail(snrichPartnerDTO.getEmail());
			snrichPartner.setPhone(snrichPartnerDTO.getPhone());
			snrichPartner.setLocation(snrichPartnerDTO.getLocation());
			snrichPartner.setActivated(snrichPartnerDTO.getActivated());
			snrichPartner = snrichPartnerRepository.save(snrichPartner);
			SnrichPartnerDTO result = new SnrichPartnerDTO(snrichPartner);
			return result;
		}).orElse(null);
	}

	@Override
	public List<SnrichPartnerDTO> GetAllActivatedPartners(boolean activated) {
		return null;
	}

	@Override
	public List<SnrichPartnerDTO> GetAllPartners() {
		List<SnrichPartnerDTO> snrichPartnerDTOs = snrichPartnerRepository.findAll()
				.stream().map(SnrichPartnerDTO::new).collect(Collectors.toList());
		return snrichPartnerDTOs;
	}

	@Override
	public Optional<SnrichPartnerDTO> findOneByPid(String pid) {
		return snrichPartnerRepository.findOneByPid(pid).map(snrichPartner -> {
			SnrichPartnerDTO snrichPartnerDTO = new SnrichPartnerDTO(snrichPartner);
			return snrichPartnerDTO;
		});
	}

	@Override
	public SnrichPartnerDTO updatePartnerStatus(String pid, Boolean activated) {
		SnrichPartner snrichPartner = snrichPartnerRepository.findOneByPid(pid).get();
		snrichPartner.setActivated(activated);
		snrichPartner = snrichPartnerRepository.save(snrichPartner);
		SnrichPartnerDTO result = new SnrichPartnerDTO(snrichPartner);
		return result;
	}

}
