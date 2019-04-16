package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserStockLocationService;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.StockLocationMapper;

/**
 * Service Implementation for managing UserStockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
@Service
@Transactional
public class UserStockLocationServiceImpl implements UserStockLocationService {

	private final Logger log = LoggerFactory.getLogger(UserStockLocationServiceImpl.class);

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private StockLocationMapper stockLocationMapper;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a UserStockLocation.
	 * 
	 * @param userPid
	 * @param assignedActivities
	 */
	@Override
	public void save(String userPid, String assignedStockLocations) {
		log.debug("Request to save User Stock Locations");

		User user = userRepository.findOneByPid(userPid).get();
		String[] stockLocations = assignedStockLocations.split(",");

		List<UserStockLocation> userActivities = new ArrayList<>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		for (String stockLocationPid : stockLocations) {
			StockLocation stockLocation = stockLocationRepository.findOneByPid(stockLocationPid).get();
			userActivities.add(new UserStockLocation(user, stockLocation, company));
		}
		userStockLocationRepository.deleteByUserPid(userPid);
		userStockLocationRepository.save(userActivities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findStockLocationsByUserIsCurrentUser() {
		log.debug("Request to get current user Stock Locations");
		List<StockLocation> stockLocationList = userStockLocationRepository.findStockLocationsByUserIsCurrentUser();
		return stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findStockLocationsByUserPid(String userPid) {
		log.debug("Request to get all Stock Locations under in a user");
		List<StockLocation> stockLocationList = userStockLocationRepository.findStockLocationsByUserPid(userPid);
		return stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all stockLocationDTOs from StockLocation by status and
	 *        company user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public List<StockLocationDTO> findStockLocationsByUserIsCurrentUserAndStockLocationActivated(boolean active) {
		log.debug("request to get current user activated Stock Locations");
		List<StockLocation> stockLocations = userStockLocationRepository
				.findStockLocationsByUserIsCurrentUserAndStockLocationActivated(active);
		return stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocations);
	}

	@Override
	public List<StockLocationDTO> findStockLocationsByUserIsCurrentUserAndStockLocationActivatedAndLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate) {
		log.debug("request to get current user activated Stock Locations");
		List<StockLocation> stockLocations = userStockLocationRepository
				.findStockLocationsByUserIsCurrentUserAndStockLocationActivatedAndLastModifiedDate(active,
						lastModifiedDate);
		return stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocations);
	}

	@Override
	public void copyStockLocations(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userStockLocationRepository.deleteByUserPidIn(toUserPids);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(fromUserPid);
		if (userStockLocations != null && !userStockLocations.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserStockLocation> newUserStockLocations = userStockLocations.stream()
						.map(usl -> new UserStockLocation(user, usl.getStockLocation(), company))
						.collect(Collectors.toList());
				userStockLocationRepository.save(newUserStockLocations);
			}
		}
	}

}
