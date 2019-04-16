package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardAttendanceRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardAttendanceService;
import com.orderfleet.webapp.web.rest.dto.DashboardAttendanceDTO;

@Service
@Transactional
public class DashboardAttendanceServiceImpl implements DashboardAttendanceService{

	@Inject
	private DashboardAttendanceRepository dashboardAttendanceRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;
	
	@Override
	public List<DashboardAttendanceDTO> findAllByCompany() {
		List<DashboardAttendance>dashboardAttendances=dashboardAttendanceRepository.findAllByCompanyId();
		List<DashboardAttendanceDTO>dashboardAttendanceDTOs=new ArrayList<>();
		for(DashboardAttendance dashboardAttendance:dashboardAttendances){
			DashboardAttendanceDTO dashboardAttendanceDTO=new DashboardAttendanceDTO(dashboardAttendance);
			dashboardAttendanceDTOs.add(dashboardAttendanceDTO);
		}
		return dashboardAttendanceDTOs;
	}

	@Override
	public DashboardAttendanceDTO save(DashboardAttendanceDTO dashboardAttendanceDTO) {
		DashboardAttendance dashboardAttendance = new DashboardAttendance();
		// set pid
		dashboardAttendance.setName(dashboardAttendanceDTO.getName());
		if(dashboardAttendanceDTO.getAttendanceStatus()!=null){
		dashboardAttendance.setAttendanceStatus(dashboardAttendanceDTO.getAttendanceStatus());
		}
		if(dashboardAttendanceDTO.getAttSSubgroupId()!=null){
		dashboardAttendance.setAttendanceStatusSubgroup(attendanceStatusSubgroupRepository.findOne(dashboardAttendanceDTO.getAttSSubgroupId()));
		}
		// set company
		dashboardAttendance.setCompany(companyRepository.getOne(SecurityUtils.getCurrentUsersCompanyId()));
		dashboardAttendance = dashboardAttendanceRepository.save(dashboardAttendance);
		DashboardAttendanceDTO result = new DashboardAttendanceDTO(dashboardAttendance);
		return result;
	}

	@Override
	public DashboardAttendanceDTO update(DashboardAttendanceDTO dashboardAttendanceDTO) {
		DashboardAttendance dashboardAttendance=new DashboardAttendance();
		dashboardAttendance.setName(dashboardAttendanceDTO.getName());
		dashboardAttendance.setId(dashboardAttendanceDTO.getId());
		dashboardAttendance.setCompany(companyRepository.getOne(SecurityUtils.getCurrentUsersCompanyId()));
		if(dashboardAttendanceDTO.getAttendanceStatus()!=null){
		dashboardAttendance.setAttendanceStatus(dashboardAttendanceDTO.getAttendanceStatus());
		}
		if(dashboardAttendanceDTO.getAttSSubgroupId()!=null){
		dashboardAttendance.setAttendanceStatusSubgroup(attendanceStatusSubgroupRepository.findOne(dashboardAttendanceDTO.getAttSSubgroupId()));
		}
		dashboardAttendance = dashboardAttendanceRepository.save(dashboardAttendance);
		DashboardAttendanceDTO result = new DashboardAttendanceDTO(dashboardAttendance);
		return result;
	}

	@Override
	public DashboardAttendanceDTO findOne(Long id) {
		DashboardAttendance dashboardAttendance = dashboardAttendanceRepository.findOne(id);
		DashboardAttendanceDTO dashboardAttendanceDTO = new DashboardAttendanceDTO(dashboardAttendance);
		return dashboardAttendanceDTO;
	}

	@Override
	public Optional<DashboardAttendanceDTO> findByName(String name) {
		return dashboardAttendanceRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(dashboardAttendance -> {
					DashboardAttendanceDTO dashboardAttendanceDTO = new DashboardAttendanceDTO(dashboardAttendance);
					return dashboardAttendanceDTO;
				});
	}

	@Override
	public void delete(Long id) {
		DashboardAttendance dashboardAttendance = dashboardAttendanceRepository.findOne(id);
		dashboardAttendanceRepository.delete(dashboardAttendance.getId());
	}

}
