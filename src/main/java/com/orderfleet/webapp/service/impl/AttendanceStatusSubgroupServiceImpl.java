package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;
import com.orderfleet.webapp.web.rest.mapper.AttendanceStatusSubgroupMapper;

/**
 *Service Impl for AttendanceStatusSubgroupService
 *
 * @author fahad
 * @since Jul 25, 2017
 */
@Service
@Transactional
public class AttendanceStatusSubgroupServiceImpl implements AttendanceStatusSubgroupService{

	private final Logger log = LoggerFactory.getLogger(AttendanceStatusSubgroupServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AttendanceStatusSubgroupMapper attendanceStatusSubgroupMapper;
	
	@Override
	public AttendanceStatusSubgroupDTO save(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO) {
		log.debug("Request to save AttendanceStatusSubgroup : {}", attendanceStatusSubgroupDTO);
		AttendanceStatusSubgroup attendanceStatusSubgroup = attendanceStatusSubgroupMapper.attendanceStatusSubgroupDTOToAttendanceStatusSubgroup(attendanceStatusSubgroupDTO);
		attendanceStatusSubgroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		attendanceStatusSubgroup = attendanceStatusSubgroupRepository.save(attendanceStatusSubgroup);
		AttendanceStatusSubgroupDTO result = attendanceStatusSubgroupMapper.attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup);
		return result;
	}

	@Override
	public AttendanceStatusSubgroupDTO update(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO) {
		log.debug("Request to Update AttendanceStatusSubgroup : {}", attendanceStatusSubgroupDTO);
		AttendanceStatusSubgroup attendanceStatusSubgroup=attendanceStatusSubgroupRepository.findOne(attendanceStatusSubgroupDTO.getId());
		attendanceStatusSubgroup.setAttendanceStatus(attendanceStatusSubgroupDTO.getAttendanceStatus());
		attendanceStatusSubgroup.setCode(attendanceStatusSubgroupDTO.getCode());
		attendanceStatusSubgroup.setDescription(attendanceStatusSubgroupDTO.getDescription());
		attendanceStatusSubgroup.setName(attendanceStatusSubgroupDTO.getName());
		attendanceStatusSubgroup.setId(attendanceStatusSubgroupDTO.getId());
		attendanceStatusSubgroup.setSortOrder(attendanceStatusSubgroupDTO.getSortOrder());
		attendanceStatusSubgroupRepository.save(attendanceStatusSubgroup);
		AttendanceStatusSubgroupDTO result = attendanceStatusSubgroupMapper.attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup);
		return result;
	}

	@Override
	public List<AttendanceStatusSubgroupDTO> findAllByCompany() {
		log.debug("Request to get all AttendanceStatusSubgroups");
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ASS_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AttendanceStatusSubgroup> attendanceStatusSubgroupList = attendanceStatusSubgroupRepository.findAllByCompanyId();
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

		List<AttendanceStatusSubgroupDTO> result = attendanceStatusSubgroupMapper.attendanceStatusSubgroupsToAttendanceStatusSubgroupDTOs(attendanceStatusSubgroupList);
		return result;
	}

	@Override
	public AttendanceStatusSubgroupDTO findOne(Long id) {
		log.debug("Request to get AttendanceStatusSubgroup : {}", id);
		AttendanceStatusSubgroup attendanceStatusSubgroup = attendanceStatusSubgroupRepository.findOne(id);
		AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO = attendanceStatusSubgroupMapper.attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup);
		return attendanceStatusSubgroupDTO;
	}


	@Override
	public void delete(Long id) {
		log.debug("Request to delete AttendanceStatusSubgroup : {}", id);
		AttendanceStatusSubgroup attendanceStatusSubgroup=attendanceStatusSubgroupRepository.findOne(id);
		attendanceStatusSubgroupRepository.delete(attendanceStatusSubgroup.getId());
	}

	@Override
	public Optional<AttendanceStatusSubgroupDTO> findByName(String name) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ASS_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and name ignore";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AttendanceStatusSubgroupDTO> assDTO= attendanceStatusSubgroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(attendanceStatusSubgroup -> {
					AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO = attendanceStatusSubgroupMapper.attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup);
					return attendanceStatusSubgroupDTO;
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
					return assDTO;

	}
	
	

}
