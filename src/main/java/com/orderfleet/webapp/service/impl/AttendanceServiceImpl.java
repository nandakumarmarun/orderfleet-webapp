package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;

/**
 * Service Implementation for managing Attendance.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

	private final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;
	
	@Inject
	private GeoLocationService geoLocationService;

	/**
	 * Save a attendance.
	 * 
	 * @param attendanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public Attendance saveAttendance(AttendanceDTO attendanceDTO) {
		log.debug("Request to save  attendance : {}", attendanceDTO);
		log.info("******/"+attendanceDTO.getAttendanceSubGroupId()+attendanceDTO.getAttendanceSubGroupName());
		// find user and company
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();

		Attendance attendance = new Attendance();
		attendance.setPid(AttendanceService.PID_PREFIX + RandomUtil.generatePid());
		attendance.setClientTransactionKey(attendanceDTO.getClientTransactionKey());
		attendance.setCreatedDate(LocalDateTime.now());
		attendance.setIsCompleted(false);
		attendance.setPlannedDate(attendanceDTO.getPlannedDate());
		attendance.setRemarks(attendanceDTO.getRemarks());
		attendance.setLatitude(attendanceDTO.getLatitude());
		attendance.setLongitude(attendanceDTO.getLongitude());
		attendance.setAttendanceStatus(attendanceDTO.getAttendanceStatus());
		attendance.setUser(user);
		attendance.setCompany(user.getCompany());
		attendance.setImageRefNo(attendanceDTO.getImageRefNo());
		// set subgroup
		if (attendanceDTO.getAttendanceSubGroupId() != null) {
			attendance.setAttendanceStatusSubgroup(
					attendanceStatusSubgroupRepository.findOne(attendanceDTO.getAttendanceSubGroupId()));
		}

		// set location
		LocationType locationType = attendanceDTO.getLocationType();
		BigDecimal lat = attendanceDTO.getLatitude();
		BigDecimal lon = attendanceDTO.getLongitude();

		attendance.setLocationType(locationType);
		attendance.setLatitude(lat);
		attendance.setLongitude(lon);

		if (locationType.equals(LocationType.GpsLocation) || (lat != null && lat.compareTo(BigDecimal.ZERO) != 0 && 
															  lon != null && lon.compareTo(BigDecimal.ZERO) != 0)) {
			attendance.setLocation(geoLocationService.findAddressFromLatLng(lat + "," +lon));
			
//			if(attendanceDTO.getMcc() != null && !attendanceDTO.getMcc().equals("0") &&
//				attendanceDTO.getMnc() != null && !attendanceDTO.getMnc().equals("0") &&
//				attendanceDTO.getLac() != null && !attendanceDTO.getLac().equals("0") &&
//				attendanceDTO.getCellId() != null && !attendanceDTO.getCellId().equals("0")) {
//				
//			}
			
		}else {
			attendance.setLocation("No Location");
		} 
		if (locationType.equals(LocationType.TowerLocation) || attendanceDTO.getMcc() != null && !attendanceDTO.getMcc().equals("0") &&
																	  attendanceDTO.getMnc() != null && !attendanceDTO.getMnc().equals("0") &&
																	  attendanceDTO.getLac() != null && !attendanceDTO.getLac().equals("0") &&
																	  attendanceDTO.getCellId() != null && !attendanceDTO.getCellId().equals("0")) {
			attendance.setMcc(attendanceDTO.getMcc());
			attendance.setMnc(attendanceDTO.getMnc());
			attendance.setCellId(attendanceDTO.getCellId());
			attendance.setLac(attendanceDTO.getLac());
			TowerLocation towerLocation = geoLocationService.findAddressFromCellTower(attendanceDTO.getMcc(), attendanceDTO.getMnc(),
					attendanceDTO.getCellId(), attendanceDTO.getLac());
			if (towerLocation != null) {
				attendance.setTowerLocation(towerLocation.getLocation());
				attendance.setTowerLatitude(towerLocation.getLat());
				attendance.setTowerLongitude(towerLocation.getLan());
			} else {
				attendance.setLocation("Unable to find location");
			}
		} else {
			attendance.setTowerLocation("No Location");
		}
		
		if (locationType.equals(LocationType.NoLocation) || locationType.equals(LocationType.FlightMode)) {
			attendance.setLocation("No Location");
		}
		attendance = attendanceRepository.save(attendance);
		return attendance;
	}

	/**
	 * Save a attendance.
	 * 
	 * @param attendanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AttendanceDTO save(AttendanceDTO attendanceDTO) {
		log.debug("Request to save Attendance : {}", attendanceDTO);
		Attendance attendance = new Attendance();
		// set pid
		attendance.setPid(AttendanceService.PID_PREFIX + RandomUtil.generatePid());

		attendance.setCreatedDate(LocalDateTime.now());
		attendance.setIsCompleted(false);

		// <.............PlanedDate must check.............>
		attendance.setPlannedDate(attendanceDTO.getPlannedDate());
		attendance.setRemarks(attendanceDTO.getRemarks());
		attendance.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		// set company
		attendance.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		attendance = attendanceRepository.save(attendance);
		AttendanceDTO result = new AttendanceDTO(attendance);
		return result;
	}

	/**
	 * Update a attendance.
	 * 
	 * @param attendanceDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public Attendance update(Long subgroupId) {
		LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
		LocalDateTime toDate = LocalDate.now().atTime(23, 59);
		List<Attendance> attendances = attendanceRepository.findByCompanyIdAndUserLoginAndDateBetweenOrderByDate(
				SecurityUtils.getCurrentUserLogin(), fromDate, toDate);
		AttendanceStatusSubgroup attendanceStatusSubgroup = attendanceStatusSubgroupRepository.findOne(subgroupId);
		Attendance attendance = attendances.get(attendances.size() - 1);
		if (attendance != null) {
			attendance.setAttendanceStatusSubgroup(attendanceStatusSubgroup);
			attendanceRepository.save(attendance);

		}
		return attendance;
	}

	/**
	 * Get all the attendances.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Attendance> findAll(Pageable pageable) {
		log.debug("Request to get all Attendances");
		Page<Attendance> result = attendanceRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the attendances.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findAllByCompany() {
		log.debug("Request to get all Attendance");
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyId();
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the attendances.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findByUserIsCurrentUser() {
		log.debug("Request to get Current User Attendance");
		List<Attendance> attendanceList = attendanceRepository.findByUserIsCurrentUser();
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the attendances.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AttendanceDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Attendance");
		Page<Attendance> attendances = attendanceRepository.findAllByCompanyId(pageable);
		List<AttendanceDTO> attendanceDTOs = attendances.getContent().stream().map(AttendanceDTO::new)
				.collect(Collectors.toList());
		Page<AttendanceDTO> result = new PageImpl<AttendanceDTO>(attendanceDTOs, pageable,
				attendances.getTotalElements());
		return result;
	}

	/**
	 * Get one attendance by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public AttendanceDTO findOne(Long id) {
		log.debug("Request to get Attendance : {}", id);
		Attendance attendance = attendanceRepository.findOne(id);
		AttendanceDTO attendanceDTO = new AttendanceDTO(attendance);
		return attendanceDTO;
	}

	/**
	 * Get one attendance by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AttendanceDTO> findOneByPid(String pid) {
		log.debug("Request to get Attendance by pid : {}", pid);
		return attendanceRepository.findOneByPid(pid).map(attendance -> {
			AttendanceDTO attendanceDTO = new AttendanceDTO(attendance);
			return attendanceDTO;
		});
	}

	/**
	 * Delete the attendance by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Attendance : {}", pid);
		attendanceRepository.findOneByPid(pid).ifPresent(attendance -> {
			attendanceRepository.delete(attendance.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdUserPidAndDateBetween(userPid,
				fromDate, toDate);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Long countByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		return attendanceRepository.countByCompanyIdAndDateBetween(fromDate, toDate);
	}

	@Override
	public List<AttendanceDTO> findAllByCompanyIdUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<User> users = userRepository.findByUserIdIn(userIds);
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdUserPidInAndDateBetween(users,
				fromDate, toDate);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> findAllUniqueUsersFromAttendance(String companypid) {
		List<User> users = attendanceRepository.findAllUniqueUsersFromAttendance(companypid);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> getCountUniqueUsersFromAttendanceAndCreateDateBetween(String companypid,
			LocalDateTime startDate, LocalDateTime endDate) {
		List<User> users = attendanceRepository.getCountUniqueUsersFromAttendanceAndCreateDateBetween(companypid,
				startDate, endDate);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Optional<AttendanceDTO> findTop1(Long companyId, String userPid) {
		Optional<AttendanceDTO> attendance = attendanceRepository.findTop1ByCompanyIdAndUserPidOrderByCreatedDateDesc(companyId,userPid)
																.map(att -> new AttendanceDTO(att));
		return attendance;
	}
}
