package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.AccountGroupAccountProfileRepository;
import com.orderfleet.webapp.repository.AccountGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountGroupAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountGroupAccountProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Implementation for managing AccountGroupAccountProfile.
 * 
 * @author Prashob Sasidharan
 * @since April 11, 2019
 */
@Service
@Transactional
public class AccountGroupAccountProfileServiceImpl implements AccountGroupAccountProfileService {
	private final Logger log = LoggerFactory.getLogger(AccountGroupAccountProfileServiceImpl.class);

	@Inject
	private AccountGroupRepository accountGroupRepository;

	@Inject
	private AccountGroupAccountProfileRepository accountGroupAccountProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String accountGroupPid, String assignedAccountProfile) {
		log.debug("Request to save AccountGroup AccountProfile");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		AccountGroup accountGroup = accountGroupRepository.findOneByPid(accountGroupPid).get();
		String[] accountProfiles = assignedAccountProfile.split(",");
		List<AccountGroupAccountProfile> accountGroupAccountProfile = new ArrayList<>();
		for (String accountProfilePid : accountProfiles) {
			AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
			accountGroupAccountProfile.add(new AccountGroupAccountProfile(accountGroup, accountProfile, company));
		}
		accountGroupAccountProfileRepository.deleteByAccountGroupPid(accountGroupPid);
		accountGroupAccountProfileRepository.save(accountGroupAccountProfile);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAccountProfileByAccountGroupPid(String accountGroupPid) {
		log.debug("Request to get all AccountProfile under in a accountGroups");
		List<AccountProfile> accountProfileList = accountGroupAccountProfileRepository
				.findAccountProfileByAccountGroupPid(accountGroupPid);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountGroupPidIn(List<String> accountGroupPids) {
		List<AccountProfile> accountProfileList = accountGroupAccountProfileRepository
				.findAccountProfileByAccountGroupPidIn(accountGroupPids);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	/**
	 * Get all the accountProfiles.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountGroupAccountProfile> findAllByCompany() {
		log.debug("Request to get all AccountProfile");
		List<AccountGroupAccountProfile> accountGroupAccountProfile = accountGroupAccountProfileRepository
				.findAllByCompanyId();
		return accountGroupAccountProfile;
	}

	@Override
	public Page<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroups(Pageable pageable) {
		// current user employee accountGroups
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			Page<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountProfilesByUserAccountGroups(accountGroups, pageable);
			// remove duplicates
			result = accountProfiles.getContent().parallelStream().distinct().collect(Collectors.toList());
		}
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroups() {
		// current user employee accountGroups
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (!accountGroups.isEmpty()) {
			List<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountProfilesByUserAccountGroupsOrderByAccountProfilesName(accountGroups);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountGroupAccountProfile> findAllByAccountProfilePidAndAccountGroupPid(String accountProfilePid,
			String accountGroupPid) {
		log.debug("Request to get all AccountProfile");
		return accountGroupAccountProfileRepository.findAllByAccountProfilePidAndAccountGroupPid(accountProfilePid,
				accountGroupPid);

	}

	@Override
	public void deleteByCompanyId(Long companyId) {
		accountGroupAccountProfileRepository.deleteByCompanyId(companyId);
	}

	@Override
	public List<AccountGroupAccountProfile> findAllByCompanyAndAccountProfilePid(Long companyId,
			String accountProfilePid) {
		log.debug("Request to get all AccountGroupAccountProfile by AccountProfile");
		return accountGroupAccountProfileRepository.findAllByCompanyAccountProfilePid(accountProfilePid, companyId);
	}

	/**
	 * get all accountProfiles activated true
	 * 
	 * @author Sarath T
	 * 
	 * @since Jan 20, 2017
	 */
	@Override
	public Page<AccountProfileDTO> findAccountProfilesByUserAccountGroupsAndAccountProfileActivated(int page,
			int count) {
		// current user employee accountGroups
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();
		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		log.info("===============================================================");
		log.info("get accountProfiles..........");
		log.info("Page : {}" + page);
		log.info("Count : {}" + count);
		if (accountGroups.size() > 0) {
			Page<AccountGroupAccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findDistinctAccountProfileByAccountProfileActivatedTrueAndAccountGroupInOrderByIdAsc(accountGroups,
							new PageRequest(page, count));
			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());
		}
		System.out.println("===============================================================");
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	/**
	 * get accountGroup wise accountProfiles activated true
	 * 
	 * @author Riyas
	 * @since Jan 28, 2017
	 * 
	 * @author Shaheer
	 * @since July 26, 2018
	 */
	@Override
	public List<AccountGroupAccountProfileDTO> findByUserAccountGroupsAndAccountProfileActivated(int limit,
			int offset) {
		return null;
		// current user employee accountGroups
		/*
		 * Set<Long> accountGroupIds = employeeProfileAccountGroupRepository.
		 * findAccountGroupIdsByEmployeeProfileIsCurrentUser(); // get accounts in
		 * employee accountGroups List<AccountGroupAccountProfileDTO> result = new
		 * ArrayList<>(); if (!accountGroupIds.isEmpty()) { List<Object[]>
		 * locAccProfiles = accountGroupAccountProfileRepository.
		 * findByAccountGroupInAndAccountProfileActivatedPaginated(accountGroupIds,
		 * limit, offset); for (Object[] objects : locAccProfiles) {
		 * AccountGroupAccountProfileDTO locAPDto = new
		 * AccountGroupAccountProfileDTO(objects[0].toString(), objects[1].toString()
		 * ,objects[2].toString(),objects[3].toString(),((java.sql.Timestamp)objects[4])
		 * .toLocalDateTime()); result.add(locAPDto); } } return result;
		 */
	}

	/**
	 * Get all the accountProfiles.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountGroupAccountProfile> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all AccountProfile");
		List<AccountGroupAccountProfile> accountGroupAccountProfile = accountGroupAccountProfileRepository
				.findAllByCompanyId(companyId);
		return accountGroupAccountProfile;
	}

	/**
	 * get all accountProfiles activated true And modifiedDateCheck.
	 * 
	 * @author Sarath T
	 * 
	 * @since Mar 30, 2017
	 */
	@Override
	public Page<AccountProfileDTO> findAllByAccountProfileActivatedTrueAndAccountGroupInAndLastModifiedDate(int page,
			int count, LocalDateTime lastSyncdate) {
		// current user employee accountGroups
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			Page<AccountGroupAccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAllByAccountProfileActivatedTrueAndAccountGroupInAndLastModifiedDate(accountGroups, true,
							lastSyncdate, new PageRequest(page, count));
			result = accountProfiles.getContent().stream().map(la -> la.getAccountProfile())
					.collect(Collectors.toList());
		}
		return new PageImpl<>(accountProfileMapper.accountProfilesToAccountProfileDTOs(result));
	}

	@Override
	public void saveAccountGroupAccountProfile(AccountGroup accountGroup, String accountProfilePid) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).get();
		AccountGroupAccountProfile accountGroupAccountProfile = new AccountGroupAccountProfile(accountGroup,
				accountProfile, company);
		accountGroupAccountProfileRepository.save(accountGroupAccountProfile);
	}

	/**
	 * get accountGroup wise accountProfiles activated true
	 * 
	 * @author Sarath T
	 * @since April 7, 2017
	 * 
	 * @author Shaheer
	 * @since July 26, 2018
	 */
	@Override
	public List<AccountGroupAccountProfileDTO> findByUserAccountGroupsAndAccountProfileActivatedAndLastModified(
			int limit, int offset, LocalDateTime lastModified) {
		return null;
		// current user employee accountGroups
		/*
		 * Set<Long> accountGroupIds = employeeProfileAccountGroupRepository.
		 * findAccountGroupIdsByEmployeeProfileIsCurrentUser();
		 * List<AccountGroupAccountProfileDTO> result = new ArrayList<>(); if
		 * (!accountGroupIds.isEmpty()) { List<Object[]> locAccProfiles =
		 * accountGroupAccountProfileRepository
		 * .findByAccountGroupInAndAccountProfileActivatedPaginated(accountGroupIds,
		 * lastModified, limit, offset); for (Object[] objects : locAccProfiles) {
		 * AccountGroupAccountProfileDTO locAPDto = new
		 * AccountGroupAccountProfileDTO(objects[0].toString(), objects[1].toString()
		 * ,objects[2].toString(),objects[3].toString(),((java.sql.Timestamp)objects[4])
		 * .toLocalDateTime()); result.add(locAPDto); } } return result;
		 */
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountGroupPidInAndActivated(List<String> accountGroupPids,
			boolean activated) {
		List<AccountProfile> accountProfileList = accountGroupAccountProfileRepository
				.findAccountProfileByAccountGroupPidIn(accountGroupPids);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountGroupAccountProfileDTO> findAccountGroupByAccountProfilePid(String accountProfilePid) {
		List<AccountGroupAccountProfile> accountGroupAccountProfiles = accountGroupAccountProfileRepository
				.findAllByAccountProfilePid(accountProfilePid);
		List<AccountGroupAccountProfileDTO> accountGroupAccountProfileDTOs = new ArrayList<>();
		for (AccountGroupAccountProfile accountGroupAccountProfile : accountGroupAccountProfiles) {
			AccountGroupAccountProfileDTO accountGroupAccountProfileDTO = new AccountGroupAccountProfileDTO(
					accountGroupAccountProfile);
			accountGroupAccountProfileDTOs.add(accountGroupAccountProfileDTO);
		}
		return accountGroupAccountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAllAccountProfileByAccountGroupPidInAndActivated(List<String> accountGroupPids,
			boolean activated) {
		List<AccountProfile> accountProfileList = accountGroupAccountProfileRepository
				.findAccountProfileByAccountGroupPidInAndAccountProfileActivated(accountGroupPids, activated);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountGroupPidAndActivated(String accountGroupPid,
			boolean activated) {
		List<AccountProfile> accountProfileList = accountGroupAccountProfileRepository
				.findAccountProfileByAccountGroupPidAndAccountProfileActivated(accountGroupPid, activated);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroupsAndAccountTypePidIn(
			List<String> accountTypePids, boolean importStatus) {
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			List<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountProfilesByUserAccountGroupsAndAccountTypePidInOrderByAccountProfilesName(accountGroups,
							accountTypePids, importStatus);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountByCurrentUserAccountGroupsAndAccountTypePidIn(
			List<String> accountTypePids) {
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			List<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountByUserAccountGroupsAndAccountTypePidInOrderByAccountProfilesName(accountGroups,
							accountTypePids);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByCurrentUserAccountGroupsAndImpotedStatus(boolean importStatus) {
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			List<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountByUserAccountGroupsAndImportedStatusOrderByAccountProfilesName(accountGroups,
							importStatus);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public List<AccountProfileDTO> findAccountByCurrentUserAccountGroupsAndAllImpotedStatus() {
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyId();

		// get accounts in employee accountGroups
		List<AccountProfile> result = new ArrayList<>();
		if (accountGroups.size() > 0) {
			List<AccountProfile> accountProfiles = accountGroupAccountProfileRepository
					.findAccountByUserAccountGroupsAndAllImportedStatusOrderByAccountProfilesName(accountGroups);
			// remove duplicates
			result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());
		}
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
	}

	@Override
	public void saveAccountGroupAccountProfileSingle(String accountGroupPid, String accountProfilePid) {
		log.debug("Request to save AccountGroup AccountProfile");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
		Optional<AccountGroup> accountGroup = accountGroupRepository.findOneByPid(accountGroupPid);
		if (accountProfile.isPresent() && accountGroup.isPresent()) {
			AccountGroupAccountProfile accountGroupAccountProfile = new AccountGroupAccountProfile(accountGroup.get(),
					accountProfile.get(), company);
			accountGroupAccountProfileRepository.save(accountGroupAccountProfile);
		}
	}

	@Override
	public List<AccountProfileDTO> findAccountProfilesByUsers(List<Long> userIds) {
		return null;
		/*
		 * // current user's subordinates employee accountGroups //List<AccountGroup>
		 * accountGroups =
		 * employeeProfileAccountGroupRepository.findAccountGroupsByUserIdIn(userIds);
		 * 
		 * // get accounts in employee accountGroups List<AccountProfile> result = new
		 * ArrayList<>(); if (!accountGroups.isEmpty()) { List<AccountProfile>
		 * accountProfiles = accountGroupAccountProfileRepository
		 * .findAccountProfilesByUserAccountGroupsOrderByAccountProfilesName(
		 * accountGroups); // remove duplicates result =
		 * accountProfiles.parallelStream().distinct().collect(Collectors.toList()); }
		 * return accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
		 */
	}

	@Override
	public Set<String> findAccountProfilePidsByUsers(List<Long> userIds) {
		return null;
		// current user's subordinates employee accountGroups
		/*
		 * List<AccountGroup> accountGroups =
		 * employeeProfileAccountGroupRepository.findAccountGroupsByUserIdIn(userIds);
		 * if (!accountGroups.isEmpty()) { return accountGroupAccountProfileRepository.
		 * findAccountProfilePidsByUserAccountGroupsOrderByAccountProfilesName(
		 * accountGroups); } return Collections.emptySet();
		 */
	}

	@Override
	public List<AccountGroupAccountProfile> findAllAccountGroupAccountProfiles(Long companyId) {
		List<AccountGroupAccountProfile> accountGroupAccountProfiles = new ArrayList<>();
		for (Object[] object : accountGroupAccountProfileRepository
				.findAllAccountGroupAccountProfilesByCompanyId(companyId)) {
			if (object[1] == null || object[2] == null) {
				log.error("Either one of AccountProfilePid or AccountGroupPid is null!");
				continue;
			}
			AccountGroupAccountProfile accountGroupAccountProfile = new AccountGroupAccountProfile();
			AccountProfile accountProfile = new AccountProfile();
			AccountGroup accountGroup = new AccountGroup();
			accountGroupAccountProfile.setId((Long) object[0]);
			accountProfile.setPid(object[1].toString());
			accountGroup.setPid(object[2].toString());
			accountGroupAccountProfile.setAccountProfile(accountProfile);
			accountGroupAccountProfile.setAccountGroup(accountGroup);
			accountGroupAccountProfiles.add(accountGroupAccountProfile);
		}
		return accountGroupAccountProfiles;
	}
}
