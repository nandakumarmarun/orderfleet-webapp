package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.DistanceFare;
import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.DistanceFareRepository;
import com.orderfleet.webapp.repository.PunchOutRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PunchOutService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;

/**
 * Service Implementation managing PunchOut.
 * 
 * @author Athul
 * @since March 27,2018
 */

@Service
@Transactional
public class PunchOutServiceImpl implements PunchOutService {

	private final Logger log = LoggerFactory.getLogger(PunchOutServiceImpl.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private PunchOutRepository punchOutRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private GeoLocationService geoLocationService;
	


	/**
	 * Save a punchout.
	 * 
	 * @param punchOutDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PunchOutDTO savePunchOut(PunchOutDTO punchOutDTO) {

		// find user and company
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername());
		if (user.isPresent()) {
			log.debug("punchOut user : {}", user.get().getLogin() + "----");

			LocalDateTime clientFromDate = punchOutDTO.getPunchOutDate().toLocalDate().atTime(0, 0);
			LocalDateTime clientToDate = punchOutDTO.getPunchOutDate().toLocalDate().atTime(23, 59);
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ATT_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get the top 1 by companyPid ,userPid and planned date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<Attendance> optionalAttendence = attendanceRepository
					.findTop1ByCompanyPidAndUserPidAndPlannedDateBetweenOrderByCreatedDateDesc(
							user.get().getCompany().getPid(), user.get().getPid(), clientFromDate, clientToDate);
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

			if (optionalAttendence.isPresent()) {
				log.info("Attendance exist");

				Optional<PunchOut> optionalPunchOut = punchOutRepository
						.findIsAttendancePresent(optionalAttendence.get().getPid());
			
				if (!optionalPunchOut.isPresent()) {
					log.info("Attendance pid not present on punchout");
					PunchOut punchOut = new PunchOut();
					punchOut.setPid(PunchOutService.PID_PREFIX + RandomUtil.generatePid());
					punchOut.setClientTransactionKey(punchOutDTO.getClientTransactionKey());
					punchOut.setRemarks(punchOutDTO.getPunchOutRemarks());
					punchOut.setLatitude(punchOutDTO.getLatitude());
					punchOut.setLongitude(punchOutDTO.getLongitude());
					punchOut.setPunchOutDate(punchOutDTO.getPunchOutDate());
					punchOut.setUser(user.get());
					punchOut.setCompany(user.get().getCompany());
					punchOut.setAttendance(optionalAttendence.get());
					punchOut.setOodoMeter(punchOutDTO.getOodoMeter());
					punchOut.setImageRefNo(punchOutDTO.getImageRefNo());

					// set location
					LocationType locationType = punchOutDTO.getLocationType();
					BigDecimal lat = punchOutDTO.getLatitude();
					BigDecimal lon = punchOutDTO.getLongitude();

					punchOut.setLocationType(locationType);
					punchOut.setLatitude(lat);
					punchOut.setLongitude(lon);
					punchOut.setMcc(punchOutDTO.getMcc());
					punchOut.setMnc(punchOutDTO.getMnc());
					punchOut.setCellId(punchOutDTO.getCellId());
					punchOut.setLac(punchOutDTO.getLac());

					log.info("Location type :" + locationType);

					if (locationType.equals(LocationType.GpsLocation)) {
						log.info("LocationType : GpsLocaTION");
						try {
							punchOut.setLocation(geoLocationService.findAddressFromLatLng(lat + "," + lon));
						} catch (GeoLocationServiceException lae) {
							log.debug("Geo Location Service Exception while calling google GPS geo location API {}", lae.getMessage());
							punchOut.setLocation("Unable to find Location");
						}catch (HttpClientErrorException e) {
							log.debug("Http CLient Error Exception while calling google GPS geo location API {}", e.getMessage());
							punchOut.setLocation("Unable to find Location");
						} catch (Exception e) {
							log.debug("Exception while calling google GPS geo location API {}", e.getMessage());
							punchOut.setLocation("Unable to find Location");
						}
						
					} else if (locationType.equals(LocationType.TowerLocation)
							|| (punchOutDTO.getMcc().length() > 1 && punchOutDTO.getMnc().length() > 1
									&& punchOutDTO.getCellId().length() > 1 && punchOutDTO.getLac().length() > 1)) {
						log.info("LocationType : TowerLocation");
						try {
							TowerLocation towerLocation = geoLocationService.findAddressFromCellTower(
									punchOutDTO.getMcc(), punchOutDTO.getMnc(), punchOutDTO.getCellId(),
									punchOutDTO.getLac());
							log.info("LocationType : TowerLocation 1");
							punchOut.setLocation(towerLocation.getLocation());

						} catch (GeoLocationServiceException lae) {
							log.debug("Geo Location Service Exception while calling google Tower geo location API {}", lae);
							String locationDetails = "Tower Location => mcc:" + punchOutDTO.getMcc() + " mnc:"
									+ punchOutDTO.getMnc() + " cellID:" + punchOutDTO.getCellId() + " lac:"
									+ punchOutDTO.getLac();
							String errorMsg = "Exception while calling google Tower geo location API. " + "Company : "
									+ user.get().getCompany().getLegalName() + " User: " + user.get().getLogin();
							punchOut.setLocation("Unable to find Location");
							log.info(locationDetails + "\n\n" + errorMsg);

						}catch (HttpClientErrorException e) {
							log.debug("Http Client Error Exception while calling google Tower geo location API {}", e);
							String locationDetails = "Tower Location => mcc:" + punchOutDTO.getMcc() + " mnc:"
									+ punchOutDTO.getMnc() + " cellID:" + punchOutDTO.getCellId() + " lac:"
									+ punchOutDTO.getLac();
							String errorMsg = "Exception while calling google Tower geo location API. " + "Company : "
									+ user.get().getCompany().getLegalName() + " User: " + user.get().getLogin();
							punchOut.setLocation("Unable to find Location");
							log.info(locationDetails + "\n\n" + errorMsg);
						} catch (Exception e) {
							log.debug("Exception while calling google GPS geo location API {}", e.getMessage());
							punchOut.setLocation("Unable to find Location");
						}
					} else if (locationType.equals(LocationType.NoLocation)
							|| locationType.equals(LocationType.FlightMode)) {
						log.info("LocationType : NoLocation or flightmode");
						punchOut.setLocation("No Location");
					} else {
						log.info("LocationType : else case");
						punchOut.setLocation("No Location");
					}

					/*
					 * else if (locationType.equals(LocationType.TowerLocation)) {
					 * punchOut.setMcc(punchOutDTO.getMcc()); punchOut.setMnc(punchOutDTO.getMnc());
					 * punchOut.setCellId(punchOutDTO.getCellId());
					 * punchOut.setLac(punchOutDTO.getLac()); TowerLocation towerLocation = null;
					 * try { towerLocation =
					 * geoLocationService.findAddressFromCellTower(punchOutDTO.getMcc(),
					 * punchOutDTO.getMnc(), punchOutDTO.getCellId(), punchOutDTO.getLac()); } catch
					 * (Exception e) { log.error(e.getMessage()); } if (towerLocation != null) {
					 * punchOut.setLocation(towerLocation.getLocation());
					 * punchOut.setLatitude(towerLocation.getLat());
					 * punchOut.setLongitude(towerLocation.getLan()); } else {
					 * punchOut.setLocation("Unable to find location"); } } else if
					 * (locationType.equals(LocationType.NoLocation) ||
					 * locationType.equals(LocationType.FlightMode)) {
					 * punchOut.setLocation("No Location"); }
					 */

					// punchOut.setLocation("Unable to find Location");

					log.info("saving PunchOut.....");
					punchOut = punchOutRepository.save(punchOut);
					PunchOutDTO result = new PunchOutDTO(punchOut);
					return result;
				} else {
					log.info("Punchout Already Marked For Attendance Pid: " + optionalAttendence.get().getPid());
					throw new GeoLocationServiceException("Punchout Already Marked For Attendance Pid");
				}
			} else {
				log.info("Attendance not marked");
				throw new GeoLocationServiceException("Attendance not marked");
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdAndPunchDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		log.debug("Request to find all by companyid and date between : {}", fromDate, " - ", toDate);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdAndPunchDateBetween(fromDate, toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidInAndPunchDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid In And Date Between : {}", fromDate, " - ", toDate);

		List<User> users = userRepository.findByUserIdIn(userIds);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidInAndPunchDateBetween(users, fromDate,
				toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidAndPunchDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid And Date Between : {}", fromDate, " - ", toDate);

		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidAndPunchDateBetween(userPid, fromDate,
				toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdAndCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		log.debug("Request to find all by companyid and date between : {}", fromDate, " - ", toDate);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdAndCreatedDateBetween(fromDate, toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidInAndCreatedDateBetween(List<Long> userIds,
			LocalDateTime fromDate, LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid In And Date Between : {}", fromDate, " - ", toDate);

		List<User> users = userRepository.findByUserIdIn(userIds);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidInAndCreatedDateBetween(users,
				fromDate, toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidAndCreatedDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid And Date Between : {}", fromDate, " - ", toDate);

		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidAndCreatedDateBetween(userPid,
				fromDate, toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PunchOutDTO> findAllByCompanyIdUserPidAndPunchDate(String userPId, LocalDateTime fromDate,
			LocalDateTime toDate) {
		Optional<User> user = userRepository.findOneByPid(userPId);
		List<PunchOutDTO> result = null;
		if(user.isPresent()) {
			  List<PunchOut> punchOuts = punchOutRepository.findAllByCompanyIdUserAndCreatedDate(user.get(),fromDate,toDate);
			   result = punchOuts.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		}
		
		return result;
	}
	
}
