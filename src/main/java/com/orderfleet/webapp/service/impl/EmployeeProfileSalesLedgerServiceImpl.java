package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileSalesLedger;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.EmployeeProfileSalesLedgerRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.EmployeeProfileSalesLedgerService;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerDTO;

/**
 * Service Implementation for managing EmployeeProfileSalesLedger.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
@Service
@Transactional
public class EmployeeProfileSalesLedgerServiceImpl implements EmployeeProfileSalesLedgerService {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileSalesLedgerServiceImpl.class);

	@Inject
	private EmployeeProfileSalesLedgerRepository employeeProfileSalesLedgerRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	/**
	 * Save a EmployeeProfileSalesLedger.
	 * 
	 * @param employeeProfilePid
	 * @param assignedSalesLedgers
	 */
	@Override
	public void save(String employeeProfilePid, String assignedSalesLedgers) {
		log.debug("Request to save EmployeeProfile SalesLedger");

		EmployeeProfile employeeProfile = employeeProfileRepository.findOneByPid(employeeProfilePid).get();
		String[] salesLedgers = assignedSalesLedgers.split(",");

		List<EmployeeProfileSalesLedger> employeeProfileSalesLedgers = new ArrayList<>();

		for (String salesLedgerPid : salesLedgers) {
			Optional<SalesLedger> salesLedgerOp = salesLedgerRepository.findOneByPid(salesLedgerPid);
			if (salesLedgerOp.isPresent()) {
				employeeProfileSalesLedgers.add(new EmployeeProfileSalesLedger(employeeProfile, salesLedgerOp.get()));
			}
		}
		employeeProfileSalesLedgerRepository.deleteByEmployeeProfilePid(employeeProfilePid);
		employeeProfileSalesLedgerRepository.save(employeeProfileSalesLedgers);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesLedgerDTO> findSalesLedgersByEmployeeProfileIsCurrentUser() {
		List<SalesLedger> salesLedgerList = employeeProfileSalesLedgerRepository
				.findSalesLedgersByEmployeeProfileIsCurrentUser();
		List<SalesLedgerDTO> result = convertSalesLedgerToSalesLedgerDtoList(salesLedgerList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesLedgerDTO> findSalesLedgersByEmployeeProfilePid(String employeeProfilePid) {
		log.debug("Request to get all SalesLedgers");
		List<SalesLedger> salesLedgerList = employeeProfileSalesLedgerRepository
				.findSalesLedgersByEmployeeProfilePid(employeeProfilePid);
		List<SalesLedgerDTO> result = convertSalesLedgerToSalesLedgerDtoList(salesLedgerList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesLedgerDTO> findSalesLedgersByEmployeeProfileIsCurrentUserAndlastModifiedDate(
			LocalDateTime lastModifiedDate) {
		List<SalesLedger> salesLedgerList = employeeProfileSalesLedgerRepository
				.findSalesLedgersByEmployeeProfileIsCurrentUserAndlastModifiedDate(lastModifiedDate);
		List<SalesLedgerDTO> result = convertSalesLedgerToSalesLedgerDtoList(salesLedgerList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesLedgerDTO> findSalesLedgersByUserPid(String userPid) {
		List<SalesLedger> salesLedgerList = employeeProfileSalesLedgerRepository.findSalesLedgersByUserPid(userPid);
		List<SalesLedgerDTO> result = convertSalesLedgerToSalesLedgerDtoList(salesLedgerList);
		return result;
	}

	@Override
	public List<SalesLedgerDTO> findSalesLedgersByUserPidIn(List<String> UserPids) {
		List<SalesLedger> salesLedgerList = employeeProfileSalesLedgerRepository.findSalesLedgersByUserPidIn(UserPids);
		List<SalesLedgerDTO> result = convertSalesLedgerToSalesLedgerDtoList(salesLedgerList);
		return result;
	}

	private List<SalesLedgerDTO> convertSalesLedgerToSalesLedgerDtoList(List<SalesLedger> salesLedgers) {
		List<SalesLedgerDTO> salesLedgerDTOs = new ArrayList<>();

		for (SalesLedger salesLedger : salesLedgers) {
			salesLedgerDTOs.add(salesLedgerToSalesLedgerDTO(salesLedger));
		}
		return salesLedgerDTOs;
	}

	private SalesLedgerDTO salesLedgerToSalesLedgerDTO(SalesLedger salesLedger) {
		SalesLedgerDTO salesLedgerDTO = new SalesLedgerDTO();
		salesLedgerDTO.setPid(salesLedger.getPid());
		salesLedgerDTO.setActivated(salesLedger.getActivated());
		salesLedgerDTO.setName(salesLedger.getName());
		salesLedgerDTO.setAlias(salesLedger.getAlias());
		salesLedgerDTO.setDescription(salesLedger.getDescription());

		return salesLedgerDTO;
	}
}
