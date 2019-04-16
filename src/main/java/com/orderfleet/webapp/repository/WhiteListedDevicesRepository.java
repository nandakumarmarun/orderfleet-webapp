package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.WhiteListedDevices;

public interface WhiteListedDevicesRepository extends JpaRepository<WhiteListedDevices, Long>{
	
	Optional<WhiteListedDevices> findByDeviceNameIgnoreCase(String name);
	
	@Query("select whiteListedDevices from WhiteListedDevices whiteListedDevices where whiteListedDevices.deviceKey = ?1")
	List<WhiteListedDevices> findByDeviceKey(String deviceKey);
	
	@Query("select whiteListedDevices.deviceKey from WhiteListedDevices whiteListedDevices where whiteListedDevices.deviceKey = ?1")
	List<String> findDevicesByDeviceKey(String deviceKey);
	
	
}
