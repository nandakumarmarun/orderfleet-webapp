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
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserPriceLevel;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.UserPriceLevelRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.UserPriceLevelService;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

/**
 * Service Implementation for managing UserPriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
@Service
@Transactional
public class UserPriceLevelServiceImpl implements UserPriceLevelService {

	private final Logger log = LoggerFactory.getLogger(UserPriceLevelServiceImpl.class);

	@Inject
	private UserPriceLevelRepository userPriceLevelRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private PriceLevelMapper priceLevelMapper;

	@Inject
	private PriceLevelListService priceLevelListService;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a UserPriceLevel.
	 * 
	 * @param userPid
	 * @param assignedPriceLevels
	 */
	@Override
	public void save(String userPid, String assignedPriceLevels) {
		log.debug("Request to save User PriceLevel");

		User user = userRepository.findOneByPid(userPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] priceLevels = assignedPriceLevels.split(",");

		List<UserPriceLevel> userPriceLevels = new ArrayList<>();

		for (String priceLevelPid : priceLevels) {
			PriceLevel priceLevel = priceLevelRepository.findOneByPid(priceLevelPid).get();
			userPriceLevels.add(new UserPriceLevel(user, priceLevel, company));
		}
		userPriceLevelRepository.deleteByUserPid(userPid);
		userPriceLevelRepository.save(userPriceLevels);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUser() {
		List<PriceLevel> priceLevelList = userPriceLevelRepository.findPriceLevelsByUserIsCurrentUser();
		System.out.println(SecurityUtils.getCurrentUserLogin());
		List<PriceLevelDTO> result = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevelList);
		result.forEach(priceLevelDTO -> priceLevelDTO
				.setLevelListDTOs(priceLevelListService.findAllByCompanyIdAndPriceLevelPid(priceLevelDTO.getPid())));
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelDTO> findPriceLevelsByUserPid(String userPid) {
		log.debug("Request to get all PriceLevels");
		List<PriceLevel> priceLevelList = userPriceLevelRepository.findPriceLevelsByUserPid(userPid);
		return priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevelList);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceLevelDTO from PriceLevel by status and user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	@Override
	public List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUserAndPriceLevelActivated(boolean active) {
		log.debug("Request to get all activated PriceLevels");
		List<PriceLevel> priceLevels = userPriceLevelRepository
				.findPriceLevelsByUserIsCurrentUserAndPriceLevelActivated(active);
		return priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevels);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		List<PriceLevel> priceLevelList = userPriceLevelRepository.findPriceLevelsByUserIsCurrentUser();
		List<PriceLevelDTO> result = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevelList);

		List<PriceLevelDTO> priceLevelDTOs = new ArrayList<>();

		result.forEach(priceLevelDTO -> {
			if (priceLevelDTO.getLastModifiedDate().isAfter(lastModifiedDate)) {
				priceLevelDTO.setLevelListDTOs(
						priceLevelListService.findAllByCompanyIdAndPriceLevelPidAndpriceLevelLastModifiedDate(
								priceLevelDTO.getPid(), lastModifiedDate));
			} else {
				priceLevelDTO
						.setLevelListDTOs(priceLevelListService.findAllByCompanyIdAndPriceLevelPidAndLastModifiedDate(
								priceLevelDTO.getPid(), lastModifiedDate));
			}
			if (!priceLevelDTO.getLevelListDTOs().isEmpty()) {
				priceLevelDTOs.add(priceLevelDTO);
			}
		});
		return priceLevelDTOs;
	}

	@Override
	public void copyPriceLevels(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userPriceLevelRepository.deleteByUserPidIn(toUserPids);
		List<UserPriceLevel> userPriceLevels = userPriceLevelRepository.findByUserPid(fromUserPid);
		if (userPriceLevels != null && !userPriceLevels.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserPriceLevel> newUserPriceLevels = userPriceLevels.stream()
						.map(upl -> new UserPriceLevel(user, upl.getPriceLevel(),upl.getCompany())).collect(Collectors.toList());
				userPriceLevelRepository.save(newUserPriceLevels);
			}
		}
	}
}
