package com.orderfleet.webapp.service.impl;

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
		List<AttendanceStatusSubgroup> attendanceStatusSubgroupList = attendanceStatusSubgroupRepository.findAllByCompanyId();
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
		return attendanceStatusSubgroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(attendanceStatusSubgroup -> {
					AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO = attendanceStatusSubgroupMapper.attendanceStatusSubgroupToAttendanceStatusSubgroupDTO(attendanceStatusSubgroup);
					return attendanceStatusSubgroupDTO;
				});
	}
	
	

}
