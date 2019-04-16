package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Vehicle;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.VehicleRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.VehicleService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private VehicleRepository vehicleRepository;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private CompanyRepository companyRepository;

	@Override
	public VehicleDTO save(VehicleDTO vehicleDTO) {
		log.debug("request to save Vehicle : {}", vehicleDTO);
		
		Vehicle vehicle = new Vehicle();
		vehicle.setPid(VehicleService.PID_PREFIX + RandomUtil.generatePid());
		vehicle.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		vehicle.setName(vehicleDTO.getName());
		vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
		vehicle.setDescription(vehicleDTO.getDescription());
		if(!vehicleDTO.getStockLocationPid().equals("-1")){
			vehicle.setStockLocation(stockLocationRepository.findOneByPid(vehicleDTO.getStockLocationPid()).get());
		}else{
			vehicle.setStockLocation(null);
		}
		vehicle = vehicleRepository.save(vehicle);
		VehicleDTO result = new VehicleDTO(vehicle);
		return result;
	}

	@Override
	public VehicleDTO update(VehicleDTO vehicleDTO) {
		return vehicleRepository.findOneByPid(vehicleDTO.getPid()).map(vehicle -> {
			vehicle.setName(vehicleDTO.getName());
			vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
			vehicle.setDescription(vehicleDTO.getDescription());
			if(!vehicleDTO.getStockLocationPid().equals("-1")){
				vehicle.setStockLocation(stockLocationRepository.findOneByPid(vehicleDTO.getStockLocationPid()).get());
			}else{
				vehicle.setStockLocation(null);
			}
			vehicle = vehicleRepository.save(vehicle);
			VehicleDTO result = new VehicleDTO(vehicle);
			return result;
		}).orElse(null);
	}

	@Override
	public List<VehicleDTO> findAllByCompany() {
		List<VehicleDTO> vehicleDTOs = vehicleRepository.findAllByCompany()
				.stream().map(VehicleDTO::new).collect(Collectors.toList());
		return vehicleDTOs;
	}

	@Override
	public Optional<VehicleDTO> findOneByPid(String pid) {
		return vehicleRepository.findOneByPid(pid).map(vehicle -> {
			VehicleDTO vehicleDTO = new VehicleDTO(vehicle);
			return vehicleDTO;
		});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete Vehicle : {}", pid);
		vehicleRepository.findOneByPid(pid).ifPresent(vehicle -> {
			vehicleRepository.delete(vehicle.getId());
		});
	}

}
