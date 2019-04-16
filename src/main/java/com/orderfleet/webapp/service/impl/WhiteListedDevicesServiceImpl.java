package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.WhiteListedDevices;
import com.orderfleet.webapp.repository.WhiteListedDevicesRepository;
import com.orderfleet.webapp.service.WhiteListedDevicesService;
import com.orderfleet.webapp.web.rest.dto.WhiteListedDevicesDTO;

@Service
@Transactional
public class WhiteListedDevicesServiceImpl implements WhiteListedDevicesService {

	@Inject
	private WhiteListedDevicesRepository whiteListedDevicesRepository;

	@Override
	public WhiteListedDevicesDTO save(WhiteListedDevicesDTO whiteListedDevicesDTO) {
		WhiteListedDevices whiteListedDevices = new WhiteListedDevices();
		whiteListedDevices.setDeviceKey(whiteListedDevicesDTO.getDeviceKey());
		whiteListedDevices.setDeviceName(whiteListedDevicesDTO.getDeviceName());
		whiteListedDevices.setDeviceVerificationNotRequired(true);
		whiteListedDevices = whiteListedDevicesRepository.save(whiteListedDevices);
		WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevices);
		return result;
	}

	@Override
	public WhiteListedDevicesDTO update(WhiteListedDevicesDTO whiteListedDevicesDTO) {
		WhiteListedDevices whiteListedDevices = whiteListedDevicesRepository.findOne(whiteListedDevicesDTO.getId());
		whiteListedDevices.setDeviceKey(whiteListedDevicesDTO.getDeviceKey());
		whiteListedDevices.setDeviceName(whiteListedDevicesDTO.getDeviceName());
		whiteListedDevices = whiteListedDevicesRepository.save(whiteListedDevices);
		WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevices);
		return result;
	}

	@Override
	public WhiteListedDevicesDTO findOne(Long id) {
		WhiteListedDevices whiteListedDevices = whiteListedDevicesRepository.findOne(id);
		WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevices);
		return result;
	}

	@Override
	public Optional<WhiteListedDevicesDTO> findByDeviceName(String deviceName) {
		return whiteListedDevicesRepository.findByDeviceNameIgnoreCase(deviceName).map(whiteListedDevices -> {
			WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevices);
			return result;
		});
	}

	@Override
	public void delete(Long id) {
		whiteListedDevicesRepository.delete(id);
	}

	@Override
	public List<WhiteListedDevicesDTO> findAll() {
		List<WhiteListedDevices> whiteListedDevices = whiteListedDevicesRepository.findAll();
		List<WhiteListedDevicesDTO> whiteListedDevicesDTOs = new ArrayList<>();
		for (WhiteListedDevices whiteListedDevice : whiteListedDevices) {
			WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevice);
			whiteListedDevicesDTOs.add(result);
		}
		return whiteListedDevicesDTOs;
	}

	@Override
	public WhiteListedDevicesDTO changeDeviceVerificationNotRequired(Long id, boolean deviceVerificationNotRequired) {
		WhiteListedDevices whiteListedDevices = whiteListedDevicesRepository.findOne(id);
		whiteListedDevices.setDeviceVerificationNotRequired(deviceVerificationNotRequired);
		whiteListedDevices = whiteListedDevicesRepository.save(whiteListedDevices);
		WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevices);
		return result;
	}

	@Override
	public List<WhiteListedDevicesDTO> findAllByDeviceKey(String deviceKey) {
		List<WhiteListedDevices> whiteListedDevices = whiteListedDevicesRepository.findByDeviceKey(deviceKey);
		List<WhiteListedDevicesDTO> whiteListedDevicesDTOs = new ArrayList<>();
		for (WhiteListedDevices whiteListedDevice : whiteListedDevices) {
			WhiteListedDevicesDTO result = new WhiteListedDevicesDTO(whiteListedDevice);
			whiteListedDevicesDTOs.add(result);
		}
		return whiteListedDevicesDTOs;
	}

}
