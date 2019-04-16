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

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDistance;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserDistanceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.UserDistanceService;
import com.orderfleet.webapp.web.rest.dto.UserDistanceDTO;

/**
 * Service Implementation for managing UserDistanceService.
 *
 * @author Sarath
 * @since May 25, 2017
 *
 */
@Service
@Transactional
public class UserDistanceServiceImpl implements UserDistanceService {

	private final Logger log = LoggerFactory.getLogger(UserDistanceServiceImpl.class);

	@Inject
	private UserDistanceRepository userDistanceRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;
	

	/**
	 * Save a UserDistance.
	 * 
	 * @param UserDistanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserDistanceDTO save(UserDistanceDTO userDistanceDTO, Long companyId) {
		log.debug("Request to save UserDistance : {}", userDistanceDTO);

		UserDistance userDistance = new UserDistance();

		userDistance.setDate(userDistanceDTO.getDate());
		userDistance.setEndLocation(userDistanceDTO.getEndLocation());
		userDistance.setKilometre(userDistanceDTO.getKilometre());
		userDistance.setStartLocation(userDistanceDTO.getStartLocation());
		User user = userRepository.findOneByPid(userDistanceDTO.getUserPid()).get();
		userDistance.setUser(user);
		// set company
		userDistance.setCompany(companyRepository.findOne(companyId));
		userDistance = userDistanceRepository.save(userDistance);
		UserDistanceDTO result = new UserDistanceDTO(userDistance);
		return result;
	}

	@Override
	public UserDistanceDTO update(UserDistanceDTO userDistanceDTO, Long companyId) {
		log.debug("Request to Update UserDistance : {}", userDistanceDTO);
		return userDistanceRepository
				.findByCompanyIdAndUserPidAndDate(companyId, userDistanceDTO.getUserPid(), userDistanceDTO.getDate())
				.map(userDistance -> {
					userDistance.setEndLocation(userDistanceDTO.getEndLocation());
					userDistance.setKilometre(userDistanceDTO.getKilometre());
					userDistance = userDistanceRepository.save(userDistance);
					UserDistanceDTO result = new UserDistanceDTO(userDistance);
					return result;
				}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserDistanceDTO> findByCompanyIdAndUserIdAndDate(Long companyId, String userPid, LocalDate date) {
		log.debug("Request to get UserDistance by companyId and userPid and date : {}",
				companyId + " , " + userPid + " , " + date);
		return userDistanceRepository.findByCompanyIdAndUserPidAndDate(companyId, userPid, date).map(userDistance -> {
			UserDistanceDTO userDistanceDTO = new UserDistanceDTO(userDistance);
			return userDistanceDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompany() {
		log.debug("Request to get UserDistance by company: {}");
		List<UserDistance> userDistances = userDistanceRepository.findAllByCompany();
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserDistanceDTO> findAllByCompanyIdOrderByDateDesc(Pageable pageable) {
		log.debug("Request to get UserDistance by company pageble: {}");
		Page<UserDistance> documentDistances = userDistanceRepository.findAllByCompanyIdOrderByDateDesc(pageable);
		Page<UserDistanceDTO> result = new PageImpl<UserDistanceDTO>(
				(documentDistances.getContent().stream().map(UserDistanceDTO::new).collect(Collectors.toList())),
				pageable, documentDistances.getTotalElements());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndUserPid(String userPid) {
		log.debug("Request to get UserDistance by company and userPid: {} " + userPid);
		List<UserDistance> userDistances = userDistanceRepository.findAllByCompanyIdAndUserPid(userPid);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate, LocalDate toDate) {
		log.debug("Request to get UserDistance by company and userPid and date between: {}" + userPid);
		List<UserDistance> userDistances = userDistanceRepository
				.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDate fromDate,
			LocalDate toDate) {
		log.debug("Request to get UserDistance by company and date between: {}");
		List<UserDistance> userDistances = userDistanceRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate) {
		log.debug("Request to get UserDistance by company and date between: {}");
		List<UserDistance> userDistances = userDistanceRepository
				.findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(userPid, fromDate);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(
			List<String> userPids, LocalDate fromDate, LocalDate toDate) {
		log.debug("Request to get UserDistance by company and date between: {}");
		List<UserDistance> userDistances = userDistanceRepository
				.findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(userPids, fromDate, toDate);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDistanceDTO> findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(List<String> userPids,
			LocalDate fromDate) {
		log.debug("Request to get UserDistance by company and date between: {}");
		List<UserDistance> userDistances = userDistanceRepository
				.findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(userPids, fromDate);
		List<UserDistanceDTO> result = userDistances.stream().map(UserDistanceDTO::new).collect(Collectors.toList());
		return result;
	}
	
	

}
