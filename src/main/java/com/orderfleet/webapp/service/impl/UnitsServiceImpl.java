package com.orderfleet.webapp.service.impl;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.repository.UnitsRepository;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.UnitsDTO;
import com.orderfleet.webapp.web.rest.dto.BankDTO;

@Service
@Transactional
public class UnitsServiceImpl implements UnitsService {
	private final Logger log = LoggerFactory.getLogger(UnitsServiceImpl.class);
	
	@Inject
	private UnitsRepository unitsRepo;
	@Override
	public UnitsDTO save(UnitsDTO unitsDTO) {
		log.debug("Request to save Units : {}", unitsDTO);
		unitsDTO.setPid(UnitsService.PID_PREFIX + RandomUtil.generatePid());
		Units units = new Units();
		units.setPid(unitsDTO.getPid());
		units.setName(unitsDTO.getName());
		units.setShortName(unitsDTO.getShortName());
		units.setAlias(unitsDTO.getAlias());
		units.setUnitId(unitsDTO.getUnitId());
		units.setUnitCode(unitsDTO.getUnitCode());
		Units result = unitsRepo.save(units);
		UnitsDTO dto = new UnitsDTO();
		dto.setPid(result.getPid());
		dto.setName(result.getName());
		dto.setShortName(result.getShortName());
		dto.setAlias(result.getAlias());
		dto.setUnitId(result.getUnitId());
		dto.setUnitCode(result.getUnitCode());
		return dto;
	}

	@Override
	public UnitsDTO update(UnitsDTO unitsDTO) {
		log.debug("Request to update Units : {}", unitsDTO);	
		 return unitsRepo.findOneByPid(unitsDTO.getPid()).map(unit -> {
			 unit.setName(unitsDTO.getName());
			 unit.setShortName(unitsDTO.getShortName());
			 unit.setAlias(unitsDTO.getAlias());
			 unit.setUnitId(unitsDTO.getUnitId());
			 unit.setUnitCode(unitsDTO.getUnitCode());
			 
			 unit = unitsRepo.save(unit);
			 UnitsDTO dto = new UnitsDTO();
			    dto.setPid(unit.getPid());
				dto.setName(unit.getName());
				dto.setShortName(unit.getShortName());
				dto.setAlias(unit.getAlias());
				dto.setUnitId(unit.getUnitId());
				dto.setUnitCode(unit.getUnitCode());
			
			return dto;
		}).orElse(null);
	}

	@Override
	public List<UnitsDTO> findAll() {
		log.debug("Request to get all Units");
		List<UnitsDTO> unitsDTOs = new ArrayList<>();
		List<Units> units = unitsRepo.findAll();
		for(Units units2 : units) {
			UnitsDTO dto = new UnitsDTO();
			dto.setPid(units2.getPid());
			dto.setName(units2.getName());
			dto.setShortName(units2.getShortName());
			dto.setAlias(units2.getAlias());
			dto.setUnitId(units2.getUnitId());
			dto.setUnitCode(units2.getUnitCode());
			unitsDTOs.add(dto);
		}
		return unitsDTOs;
	}

	@Override
	public UnitsDTO findOneByPid(String pid) {
		Units result = unitsRepo.findOneByPid(pid).orElse(null);
		UnitsDTO dto= new UnitsDTO();
		dto.setPid(result.getPid());
		dto.setName(result.getName());
		dto.setShortName(result.getShortName());
		dto.setAlias(result.getAlias());
		dto.setUnitId(result.getUnitId());
		dto.setUnitCode(result.getUnitCode());
		return dto;
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete Units : {}", pid);
		unitsRepo.findOneByPid(pid).ifPresent(unit -> {
			unitsRepo.delete(unit.getId());
		});
		
	}

	
	

	
	
}
