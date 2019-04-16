package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Service Implementation for managing UserDevice.
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */
@Service
@Transactional
public class UserDeviceServiceImpl implements UserDeviceService {

	private final Logger log = LoggerFactory.getLogger(UserDeviceServiceImpl.class);

	@Inject
	private UserDeviceRepository userDeviceRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Override
	public UserDeviceDTO save(String deviceKey, String fcmKey) {
		String devicePid = UserDeviceService.PID_PREFIX + RandomUtil.generatePid();
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		UserDevice userDevice = new UserDevice(devicePid, deviceKey, "Mobile", fcmKey, user, user.getCompany(), true,
				"");
		return new UserDeviceDTO(userDeviceRepository.save(userDevice));
	}

	@Override
	public UserDeviceDTO save(String deviceKey) {
		String devicePid = UserDeviceService.PID_PREFIX + RandomUtil.generatePid();
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		UserDevice userDevice = new UserDevice(devicePid, deviceKey, "Mobile", "dummykey", user, user.getCompany(),
				true, "");
		return new UserDeviceDTO(userDeviceRepository.save(userDevice));
	}

	@Override
	public UserDeviceDTO saveDiviceKeyAndBuildVersion(String deviceKey, String buildVersion) {
		String devicePid = UserDeviceService.PID_PREFIX + RandomUtil.generatePid();
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		UserDevice userDevice = new UserDevice(devicePid, deviceKey, "Mobile", "dummykey", user, user.getCompany(),
				true, buildVersion);
		return new UserDeviceDTO(userDeviceRepository.save(userDevice));
	}

	@Override
	public void updateFcmKey(String fcmKey) {
		userDeviceRepository.findByUserLoginAndActivatedTrue(SecurityUtils.getCurrentUserLogin())
				.ifPresent(userDevice -> {
					userDevice.setFcmKey(fcmKey);
					userDevice = userDeviceRepository.save(userDevice);
				});
	}

	@Override
	public void deActivateUserDeviceByPid(String pid) {
		userDeviceRepository.findOneByPid(pid).ifPresent(ud -> {
			ud.setActivated(false);
			userDeviceRepository.save(ud);
			log.debug("Inactivated User device: {}", ud);
		});
	}

	@Override
	public UserDeviceDTO update(UserDeviceDTO userDeviceDTO) {
		log.debug("Request to Update UserDevice : {}", userDeviceDTO);
		return userDeviceRepository.findOneByPid(userDeviceDTO.getPid()).map(userDevice -> {
			userDevice.setDeviceKey(userDeviceDTO.getDeviceKey());
			userDevice.setDeviceName(userDeviceDTO.getDeviceName());
			userDevice.setFcmKey(userDeviceDTO.getFcmKey());
			userDevice = userDeviceRepository.save(userDevice);
			UserDeviceDTO result = new UserDeviceDTO(userDevice);
			return result;
		}).orElse(null);
	}

	@Override
	public void update(String pid, String lastAccesedDeviceKey, String login, LocalDateTime lastUpdateDate,
			String buildVersion) {
		userDeviceRepository.findOneByPid(pid).ifPresent(userDevice -> {
			userDevice.setLastAcessedDeviceKey(lastAccesedDeviceKey);
			userDevice.setLastAcessedLogin(login);
			userDevice.setLastModifiedDate(lastUpdateDate);
			userDevice.setLastModifiedDate(lastUpdateDate);
			userDevice.setBuildVersion(buildVersion);
			userDeviceRepository.save(userDevice);
		});
	}

	@Override
	public Page<UserDevice> findAll(Pageable pageable) {
		Page<UserDevice> result = userDeviceRepository.findAllByActivatedTrue(pageable);
		return result;
	}

	@Override
	public List<UserDeviceDTO> findAllByCompany() {
		log.debug("Request to get all UserDevices");
		List<UserDevice> userDeviceList = userDeviceRepository.findAllByCompanyId();
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserDevice> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserDevices");
		Page<UserDevice> result = userDeviceRepository.findAllByCompanyId(pageable);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDeviceDTO findOne(Long id) {
		log.debug("Request to get UserDevice : {}", id);
		UserDevice userDevice = userDeviceRepository.findOne(id);
		UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
		return userDeviceDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserDeviceDTO> findOneByPid(String pid) {
		log.debug("Request to get UserDevice by pid : {}", pid);
		return userDeviceRepository.findOneByPid(pid).map(userDevice -> {
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			return userDeviceDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserDeviceDTO> findByName(String name) {
		log.debug("Request to get UserDevice by name : {}", name);
		return userDeviceRepository
				.findByCompanyIdAndDeviceNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(userDevice -> {
					UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
					return userDeviceDTO;
				});
	}

	public void delete(String pid) {
		log.debug("Request to delete UserDevice : {}", pid);
		userDeviceRepository.findOneByPid(pid).ifPresent(userDevice -> {
			userDeviceRepository.delete(userDevice.getId());
		});
	}

	@Override
	public List<UserDeviceDTO> findAllByCompanyIdAndActivatedTrue() {
		log.debug("Request to get all UserDevices");
		List<UserDevice> userDeviceList = userDeviceRepository.findAllByCompanyIdAndActivatedTrue();
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDevice() {
		List<UserDevice> userDevices = userDeviceRepository.findAll();
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDevices) {
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			userDeviceDTOs.add(userDeviceDTO);
		}
		return userDeviceDTOs;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDevicesByCompanyPid(String companyPid) {
		List<UserDevice> userDevices = userDeviceRepository.findAllUserDeviceByCompanyAndLastModifiedDate(companyPid);
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDevices) {
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			userDeviceDTOs.add(userDeviceDTO);
		}
		return userDeviceDTOs;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDevicesByUserPid(String companyPid, String userPid) {
		List<UserDevice> userDevices = userDeviceRepository
				.findAllUserDeviceByCompanyAndUserAndLastModifiedDate(companyPid, userPid);
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDevices) {
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			userDeviceDTOs.add(userDeviceDTO);
		}
		return userDeviceDTOs;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDeviceSortedByActivated() {

		List<UserDevice> userDevices = userDeviceRepository.findAllUserDeviceOrderByActivatedAndLastModifiedDate();
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDevices) {
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			userDeviceDTOs.add(userDeviceDTO);
		}
		return userDeviceDTOs;

	}

	@Override
	public List<UserDeviceDTO> findByCompanyIdAndActivatedTrueAndUserPidIn(List<Long> userPids) {
		List<UserDevice> userDeviceList = userDeviceRepository.findAllByCompanyIdAndActivatedTrueAndUserPidIn(userPids);
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDevice> findAll() {
		List<UserDevice> result = userDeviceRepository.findAllByActivatedTrue();
		return result;
	}

	@Override
	public List<UserDeviceDTO> findAllByCompanyIdAndActivatedTrueConsistEmployee() {
		List<UserDevice> userDeviceList = userDeviceRepository.findAllByCompanyIdAndActivatedTrue();
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDeviceList) {
			EmployeeProfile employeeProfile = employeeProfileRepository
					.findEmployeeProfileByUserLogin(userDevice.getUser().getLogin());
			if (employeeProfile != null) {
				UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
				userDeviceDTO.setEmployeeName(employeeProfile.getName());
				userDeviceDTOs.add(userDeviceDTO);
			}
		}
		return userDeviceDTOs;
	}

	@Override
	public List<UserDeviceDTO> findByCompanyIdAndActivatedTrueAndUserPidInConsistEmployee(List<Long> userPids) {
		List<UserDevice> userDeviceList = userDeviceRepository.findAllByCompanyIdAndActivatedTrueAndUserPidIn(userPids);
		List<UserDeviceDTO> userDeviceDTOs = new ArrayList<>();
		for (UserDevice userDevice : userDeviceList) {
			EmployeeProfile employeeProfile = employeeProfileRepository
					.findEmployeeProfileByUserLogin(userDevice.getUser().getLogin());
			UserDeviceDTO userDeviceDTO = new UserDeviceDTO(userDevice);
			userDeviceDTO.setEmployeeName(employeeProfile.getName());
			userDeviceDTOs.add(userDeviceDTO);
		}
		return userDeviceDTOs;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDeviceByCompanyPidAndActivated(String companyPid, boolean activated) {
		List<UserDevice> userDeviceList = userDeviceRepository.findAllUserDeviceByCompanyPidAndActivated(companyPid,
				activated);
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDeviceDTO> findAllUserDeviceByCompanyPidAndUserPidAndActivated(String companyPid, String userPid,
			boolean activated) {
		List<UserDevice> userDeviceList = userDeviceRepository
				.findAllUserDeviceByCompanyPidAndUserPidAndActivated(companyPid, userPid, activated);
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDeviceDTO> findAllActivated(boolean activated) {
		List<UserDevice> userDeviceList = userDeviceRepository.findAllActivated(true);
		List<UserDeviceDTO> result = userDeviceList.stream().map(UserDeviceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDevice> findAllByCompanyIdAndActivatedAndUserPidIn(List<String> userPids) {
		List<UserDevice> result = userDeviceRepository.findAllByCompanyIdAndActivatedAndUserPidIn(userPids);
		return result;
	}
	
}
