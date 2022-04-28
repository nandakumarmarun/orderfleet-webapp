package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.DistanceFare;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DistanceFareRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.DistanceFareService;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

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
	
	@Inject
	private DistanceFareRepository distanceFareRepository;

	/**
	 * Save a attendance.
	 * 
	 * @param attendanceDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public Attendance saveAttendance(AttendanceDTO attendanceDTO) {
		log.debug("Request to save  attendance : {}", attendanceDTO);
		log.info("******/" + attendanceDTO.getAttendanceSubGroupId() + attendanceDTO.getAttendanceSubGroupName());
		// find user and company
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();
		Optional<DistanceFare> distanceFare = distanceFareRepository.findOneByPid(attendanceDTO.getDistanceFarePid());
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
		//set oodoMeter
		attendance.setOodoMeter(attendanceDTO.getOodoMeter());
		
		//set DistaceFare
		if(distanceFare.isPresent()) {
			attendance.setDistanceFare(distanceFare.get());
		}
		// set location
		LocationType locationType = attendanceDTO.getLocationType();
		BigDecimal lat = attendanceDTO.getLatitude();
		BigDecimal lon = attendanceDTO.getLongitude();

		attendance.setLocationType(locationType);
		attendance.setLatitude(lat);
		attendance.setLongitude(lon);

		if (locationType.equals(LocationType.GpsLocation) || (lat != null && lat.compareTo(BigDecimal.ZERO) != 0
				&& lon != null && lon.compareTo(BigDecimal.ZERO) != 0)) {

			log.info("Attendance Gps Location----");
			try {
				attendance.setLocation(geoLocationService.findAddressFromLatLng(lat + "," + lon));
			} catch (HttpClientErrorException e) {
				log.info("HttpClientErrorException-- " + e.getMessage());
				attendance.setLocation("Unable to find location");
				log.error(e.getResponseBodyAsString());
				e.printStackTrace();
			} catch (GeoLocationServiceException e) {
				log.info("GeoLocationServiceException-- " + e.getMessage());
				attendance.setLocation("Unable to find location");
				e.printStackTrace();
			} catch (Exception e) {
				log.info("Exception-- " + e.getMessage());
				attendance.setLocation("Unable to find location");
				e.printStackTrace();
			}

//			if(attendanceDTO.getMcc() != null && !attendanceDTO.getMcc().equals("0") &&
//				attendanceDTO.getMnc() != null && !attendanceDTO.getMnc().equals("0") &&
//				attendanceDTO.getLac() != null && !attendanceDTO.getLac().equals("0") &&
//				attendanceDTO.getCellId() != null && !attendanceDTO.getCellId().equals("0")) {
//				
//			}

		} else {
			attendance.setLocation("No Location");
		}
		if (locationType.equals(LocationType.TowerLocation)
				|| attendanceDTO.getMcc() != null && !attendanceDTO.getMcc().equals("0")
						&& attendanceDTO.getMnc() != null && !attendanceDTO.getMnc().equals("0")
						&& attendanceDTO.getLac() != null && !attendanceDTO.getLac().equals("0")
						&& attendanceDTO.getCellId() != null && !attendanceDTO.getCellId().equals("0")) {

			log.info("Attendance Tower Location----");

			attendance.setMcc(attendanceDTO.getMcc());
			attendance.setMnc(attendanceDTO.getMnc());
			attendance.setCellId(attendanceDTO.getCellId());
			attendance.setLac(attendanceDTO.getLac());

			try {
				TowerLocation towerLocation = geoLocationService.findAddressFromCellTower(attendanceDTO.getMcc(),
						attendanceDTO.getMnc(), attendanceDTO.getCellId(), attendanceDTO.getLac());
				if (towerLocation != null) {
					attendance.setTowerLocation(towerLocation.getLocation());
					attendance.setTowerLatitude(towerLocation.getLat());
					attendance.setTowerLongitude(towerLocation.getLan());
				} else {
					attendance.setTowerLocation("Unable to find location");
				}
			} catch (HttpClientErrorException e) {
				log.info("HttpClientErrorException-- " + e.getMessage());
				attendance.setTowerLocation("Unable to find location");
				log.error(e.getResponseBodyAsString());
				e.printStackTrace();
			} catch (GeoLocationServiceException e) {
				log.info("GeoLocationServiceException-- " + e.getMessage());
				attendance.setTowerLocation("Unable to find location");
				e.printStackTrace();
			} catch (Exception e) {
				log.info("Exception-- " + e.getMessage());
				attendance.setTowerLocation("Unable to find location");
				e.printStackTrace();
			}
		} else {
			attendance.setTowerLocation("No Location");
		}

		if (locationType.equals(LocationType.NoLocation) || locationType.equals(LocationType.FlightMode)) {

			log.info("Attendance No Location/Flight Mode----");
			attendance.setLocation("No Location");
		}
		attendance = attendanceRepository.save(attendance);
		return attendance;
	}

	/**
	 * Save a attendance.
	 * 
	 * @param attendanceDTO the entity to save
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
	 * @param attendanceDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public Attendance update(Long subgroupId) {
		LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
		LocalDateTime toDate = LocalDate.now().atTime(23, 59);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_114" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get  attendane by companyId user Login and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendances = attendanceRepository.findByCompanyIdAndUserLoginAndDateBetweenOrderByDate(
				SecurityUtils.getCurrentUserLogin(), fromDate, toDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
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
	 * @param pageable the pagination information
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
		logger.debug("Request to get all Attendance");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all attendance by company id";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyId();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all attendance by checking user is current user";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendanceList = attendanceRepository.findByUserIsCurrentUser();
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the attendances.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AttendanceDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Attendance");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all attendance by company id using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		Page<Attendance> attendances = attendanceRepository.findAllByCompanyId(pageable);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		List<AttendanceDTO> attendanceDTOs = attendances.getContent().stream().map(AttendanceDTO::new)
				.collect(Collectors.toList());
		Page<AttendanceDTO> result = new PageImpl<AttendanceDTO>(attendanceDTOs, pageable,
				attendances.getTotalElements());
		return result;
	}

	/**
	 * Get one attendance by id.
	 *
	 * @param id the id of the entity
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
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AttendanceDTO> findOneByPid(String pid) {
		log.debug("Request to get Attendance by pid : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the one by Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AttendanceDTO> attendancedto =attendanceRepository.findOneByPid(pid).map(attendance -> {
			AttendanceDTO attendanceDTO = new AttendanceDTO(attendance);
			return attendanceDTO;
		});
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
				return attendancedto;

	}

	/**
	 * Delete the attendance by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Attendance : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		attendanceRepository.findOneByPid(pid).ifPresent(attendance -> {
			attendanceRepository.delete(attendance.getId());
		});
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by companyId and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AttendanceDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate) {

		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String id = "ATT_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId,userPid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdUserPidAndDateBetween(userPid,
				fromDate, toDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}

		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Long countByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="count attendance by companyId and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Long count = attendanceRepository.countByCompanyIdAndDateBetween(fromDate, toDate);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		return count;
	}

	@Override
	public List<AttendanceDTO> findAllByCompanyIdUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<User> users = userRepository.findByUserIdIn(userIds);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all attendance by companyId,userPidIn and date between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Attendance> attendanceList = attendanceRepository.findAllByCompanyIdUserPidInAndDateBetween(users,
				fromDate, toDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		List<AttendanceDTO> result = attendanceList.stream().map(AttendanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> findAllUniqueUsersFromAttendance(String companypid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all unique users from attendance";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<User> users = attendanceRepository.findAllUniqueUsersFromAttendance(companypid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> getCountUniqueUsersFromAttendanceAndCreateDateBetween(String companypid,
			LocalDateTime startDate, LocalDateTime endDate) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the count of unique users from attendance and create date between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
		List<User> users = attendanceRepository.getCountUniqueUsersFromAttendanceAndCreateDateBetween(companypid,
				startDate, endDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate1 = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Optional<AttendanceDTO> findTop1(Long companyId, String userPid) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATT_QUERY_118" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the top 1 attendance by company id and user pid and order by create date";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AttendanceDTO> attendance = attendanceRepository
				.findTop1ByCompanyIdAndUserPidOrderByCreatedDateDesc(companyId, userPid)
				.map(att -> new AttendanceDTO(att));
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		return attendance;
	}
}
