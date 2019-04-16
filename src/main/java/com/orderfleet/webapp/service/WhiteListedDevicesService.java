package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.WhiteListedDevicesDTO;

public interface WhiteListedDevicesService {

	WhiteListedDevicesDTO save(WhiteListedDevicesDTO whiteListedDevicesDTO);
	
	WhiteListedDevicesDTO update(WhiteListedDevicesDTO whiteListedDevicesDTO);
	
	WhiteListedDevicesDTO findOne(Long id);
	
	Optional<WhiteListedDevicesDTO> findByDeviceName(String deviceName);
	
	void delete(Long id);
	
	List<WhiteListedDevicesDTO>findAll();
	
	WhiteListedDevicesDTO changeDeviceVerificationNotRequired(Long id,boolean deviceVerificationNotRequired);
	
	List<WhiteListedDevicesDTO>findAllByDeviceKey(String deviceKey);
}
