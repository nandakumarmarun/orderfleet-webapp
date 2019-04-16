
package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
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

import com.orderfleet.webapp.domain.KilometreCalculation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.KilometreCalculationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.KilometreCalculationService;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;

/**
 * @author Anish
 *
 */
@Service
@Transactional
public class KilometreCalculationServiceImpl implements KilometreCalculationService {

	
	private final Logger log = LoggerFactory.getLogger(KilometreCalculationServiceImpl.class);

	@Inject
	private KilometreCalculationRepository kilometreCalculationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private ExecutiveTaskExecutionRepository taskExecutionRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	

	@Override
	public KilometerCalculationDTO save(KilometerCalculationDTO kilometreCalculationDTO, Long companyId) {
		
		log.debug("Request to save KilometreCalculation : {}", kilometreCalculationDTO);

		KilometreCalculation kilometreCalculation = new KilometreCalculation();

		kilometreCalculation.setDate(kilometreCalculationDTO.getDate());
		kilometreCalculation.setEndLocation(kilometreCalculationDTO.getEndLocation());
		kilometreCalculation.setKilometre(kilometreCalculationDTO.getKilometre());
		kilometreCalculation.setMetres(kilometreCalculationDTO.getMetres());
		kilometreCalculation.setStartLocation(kilometreCalculationDTO.getStartLocation());
		User user = userRepository.findOneByPid(kilometreCalculationDTO.getUserPid()).get();
		kilometreCalculation.setUser(user);
		if(kilometreCalculationDTO.getTaskExecutionPid() == null) {
			kilometreCalculation.setExecutiveTaskExecution(null);
		}else {
			kilometreCalculation.setExecutiveTaskExecution(taskExecutionRepository.findOneByPid(kilometreCalculationDTO.getTaskExecutionPid()).get());
		}
		kilometreCalculation.setEmployee(employeeProfileRepository.findEmployeeProfileByUser(user));
		// set company
		kilometreCalculation.setCompany(companyRepository.findOne(companyId));
		kilometreCalculation = kilometreCalculationRepository.save(kilometreCalculation);
		KilometerCalculationDTO result = new KilometerCalculationDTO(kilometreCalculation);
		return result;
	}




	@Override
	public List<KilometerCalculationDTO> findAllByCompany() {
		log.debug("Request to get KilometreCalculation by company: {}");
		List<KilometreCalculation> kilometreCalculations = kilometreCalculationRepository.findAllByCompany();
		List<KilometerCalculationDTO> result = kilometreCalculations.stream().map(KilometerCalculationDTO::new).collect(Collectors.toList());
		return result;
	}


	@Override
	public Page<KilometerCalculationDTO> findAllByCompanyIdOrderByDateDesc(Pageable pageable) {
		log.debug("Request to get KilometreCalculation by company pageble: {}");
		Page<KilometreCalculation> documentDistances = kilometreCalculationRepository.findAllByCompanyIdOrderByDateDesc(pageable);
		Page<KilometerCalculationDTO> result = new PageImpl<KilometerCalculationDTO>(
				(documentDistances.getContent().stream().map(KilometerCalculationDTO::new).collect(Collectors.toList())),
				pageable, documentDistances.getTotalElements());
		return result;
	}




	@Override							
	public List<KilometerCalculationDTO> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, LocalDate fromDate, LocalDate toDate) {
		log.debug("Request to get KilometreCalculation by company and userPid and date between: {}" + userPid);
		List<KilometreCalculation> kilometreCalculations = kilometreCalculationRepository
				.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
		List<KilometerCalculationDTO> result = kilometreCalculations.stream().map(KilometerCalculationDTO::new).collect(Collectors.toList());
		return result;
	}

	
	@Override
	public List<KilometerCalculationDTO> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate) {
		log.debug("Request to get UserDistance by company and date between: {}");
		List<KilometreCalculation> kilometreCalculations = kilometreCalculationRepository
				.findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(userPid, fromDate);
		List<KilometerCalculationDTO> result = kilometreCalculations.stream().map(KilometerCalculationDTO::new).collect(Collectors.toList());
		return result;
	}

	
	@Override
	public Optional<KilometerCalculationDTO> findByCompanyIdAndUserIdAndDate(Long companyId, String userId,
			LocalDate date) {
		log.debug("Request to get UserDistance by companyId and userPid and date : {}",
				companyId + " , " + userId + " , " + date);
		return kilometreCalculationRepository.findByCompanyIdAndUserPidAndDate(companyId, userId, date).map(userDistance -> {
			KilometerCalculationDTO kilometreCalculationDTO = new KilometerCalculationDTO(userDistance);
			return kilometreCalculationDTO;
		});
	}


}
