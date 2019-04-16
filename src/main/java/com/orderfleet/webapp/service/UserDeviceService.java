package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Service Interface for managing UserDeviceService.
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */
public interface UserDeviceService {

	String PID_PREFIX = "USRDIVSN-";

	UserDeviceDTO save(String deviceKey, String fcmKey);

	UserDeviceDTO save(String deviceKey);

	void updateFcmKey(String fcmKey);

	void deActivateUserDeviceByPid(String pid);

	UserDeviceDTO update(UserDeviceDTO userDeviceDTO);

	void update(String pid, String lastAccesedDeviceKey, String login, LocalDateTime lastUpdateDate,
			String buildVersion);

	Page<UserDevice> findAll(Pageable pageable);

	List<UserDevice> findAll();

	List<UserDeviceDTO> findAllByCompany();

	Page<UserDevice> findAllByCompany(Pageable pageable);

	UserDeviceDTO findOne(Long id);

	Optional<UserDeviceDTO> findOneByPid(String pid);

	Optional<UserDeviceDTO> findByName(String name);

	void delete(String pid);

	List<UserDeviceDTO> findAllByCompanyIdAndActivatedTrue();

	List<UserDeviceDTO> findAllUserDevice();

	List<UserDeviceDTO> findAllUserDevicesByCompanyPid(String companyPid);

	List<UserDeviceDTO> findAllUserDevicesByUserPid(String companyPid, String userPid);

	List<UserDeviceDTO> findAllUserDeviceSortedByActivated();

	List<UserDeviceDTO> findByCompanyIdAndActivatedTrueAndUserPidIn(List<Long> userPids);

	List<UserDeviceDTO> findAllByCompanyIdAndActivatedTrueConsistEmployee();

	List<UserDeviceDTO> findByCompanyIdAndActivatedTrueAndUserPidInConsistEmployee(List<Long> userPids);

	UserDeviceDTO saveDiviceKeyAndBuildVersion(String deviceKey, String buildVersion);

	List<UserDeviceDTO> findAllUserDeviceByCompanyPidAndActivated(String companyPid, boolean activated);

	List<UserDeviceDTO> findAllUserDeviceByCompanyPidAndUserPidAndActivated(String companyPid, String userPid,
			boolean activated);

	List<UserDeviceDTO> findAllActivated(boolean activated);

	List<UserDevice> findAllByCompanyIdAndActivatedAndUserPidIn(List<String> userPids);
}
