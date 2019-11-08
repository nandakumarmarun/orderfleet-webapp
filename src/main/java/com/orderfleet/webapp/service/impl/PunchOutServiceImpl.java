package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.AttendanceRepository;
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
		log.debug("Request to save  punchOut : {}", punchOutDTO);

		// find user and company
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername());
		if (user.isPresent()) {
			Optional<Attendance> optionalAttendence = attendanceRepository
					.findTop1ByCompanyPidAndUserPidOrderByCreatedDateDesc(user.get().getCompany().getPid(),
							user.get().getPid());
			if (optionalAttendence.isPresent()) {
				Optional<PunchOut> optionalPunchOut = punchOutRepository
						.findIsAttendancePresent(optionalAttendence.get().getPid());
				if (!optionalPunchOut.isPresent()) {
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

					// set location
					LocationType locationType = punchOutDTO.getLocationType();
					BigDecimal lat = punchOutDTO.getLatitude();
					BigDecimal lon = punchOutDTO.getLongitude();

					punchOut.setLocationType(locationType);
					punchOut.setLatitude(lat);
					punchOut.setLongitude(lon);

					if (locationType.equals(LocationType.GpsLocation)) {
						punchOut.setLocation(geoLocationService.findAddressFromLatLng(lat + "," + lon));
					} else if (locationType.equals(LocationType.TowerLocation)) {
						punchOut.setMcc(punchOutDTO.getMcc());
						punchOut.setMnc(punchOutDTO.getMnc());
						punchOut.setCellId(punchOutDTO.getCellId());
						punchOut.setLac(punchOutDTO.getLac());
						TowerLocation towerLocation = null;
						try {
							towerLocation = geoLocationService.findAddressFromCellTower(punchOutDTO.getMcc(),
									punchOutDTO.getMnc(), punchOutDTO.getCellId(), punchOutDTO.getLac());
						} catch (Exception e) {
							log.error(e.getMessage());
						}
						if (towerLocation != null) {
							punchOut.setLocation(towerLocation.getLocation());
							punchOut.setLatitude(towerLocation.getLat());
							punchOut.setLongitude(towerLocation.getLan());
						} else {
							punchOut.setLocation("Unable to find location");
						}
					} else if (locationType.equals(LocationType.NoLocation)
							|| locationType.equals(LocationType.FlightMode)) {
						punchOut.setLocation("No Location");
					}
					punchOut = punchOutRepository.save(punchOut);
					PunchOutDTO result = new PunchOutDTO(punchOut);
					return result;
				}
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		log.debug("Request to find all by companyid and date between : {}", fromDate, " - ", toDate);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid In And Date Between : {}", fromDate, " - ", toDate);

		List<User> users = userRepository.findByUserIdIn(userIds);
		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidInAndDateBetween(users, fromDate,
				toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PunchOutDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to find All By Company Id UserPid And Date Between : {}", fromDate, " - ", toDate);

		List<PunchOut> punchOutList = punchOutRepository.findAllByCompanyIdUserPidAndDateBetween(userPid, fromDate,
				toDate);
		List<PunchOutDTO> result = punchOutList.stream().map(PunchOutDTO::new).collect(Collectors.toList());
		return result;
	}
}
