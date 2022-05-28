package com.orderfleet.webapp.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EmployeeVehicleAssosiationHistory;
import com.orderfleet.webapp.repository.EmployeeVehicleAssosiationHistoryRepository;
import com.orderfleet.webapp.service.EmployeeVehicleAssociationHistoryService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.EmployeeVehicleAssosiationHistoryDTO;

@Service
@Transactional
public class EmployeeVehicleAssociationHistoryServiceImpl implements EmployeeVehicleAssociationHistoryService {

	@Inject
	private EmployeeVehicleAssosiationHistoryRepository employeeVehicleAssosiationHistoryRepository;
	
	@Override
	public EmployeeVehicleAssosiationHistoryDTO save(
			EmployeeVehicleAssosiationHistoryDTO EmployeeVehicleAssosiationHistoryDTO) {

		System.out.println("entering to employeeVehicleAssosiationHistory saving ");
		EmployeeVehicleAssosiationHistory employeeVehicleAssosiationHistory = new EmployeeVehicleAssosiationHistory();
		employeeVehicleAssosiationHistory.setCompany(EmployeeVehicleAssosiationHistoryDTO.getCompany());
		employeeVehicleAssosiationHistory.setEmployeeProfile(EmployeeVehicleAssosiationHistoryDTO.getEmployeeProfile());
		employeeVehicleAssosiationHistory.setVehicle(EmployeeVehicleAssosiationHistoryDTO.getVehicle());
		employeeVehicleAssosiationHistory.setCreatedDate(EmployeeVehicleAssosiationHistoryDTO.getCreatedDate());
		employeeVehicleAssosiationHistory.setPid(EmployeeVehicleAssociationHistoryService.PID_PREFIX+RandomUtil.generatePid());
		employeeVehicleAssosiationHistory.setEmployeeName(EmployeeVehicleAssosiationHistoryDTO.getEmployeeProfile().getName());
		employeeVehicleAssosiationHistory.setRegistrationNumber(EmployeeVehicleAssosiationHistoryDTO.getVehicle().getRegistrationNumber());
		employeeVehicleAssosiationHistoryRepository.save(employeeVehicleAssosiationHistory);
		System.out.println("entering to employeeVehicleAssosiationHistory saved ");
		return null;
	}

}
