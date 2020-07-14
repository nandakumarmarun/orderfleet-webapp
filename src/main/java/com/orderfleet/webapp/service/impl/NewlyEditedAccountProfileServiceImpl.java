package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.NewlyEditedAccountProfileRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.NewlyEditedAccountProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.dto.NewlyEditedAccountProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Implementation for managing AccountProfile.
 *
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Service
@Transactional
public class NewlyEditedAccountProfileServiceImpl implements NewlyEditedAccountProfileService {

	private final Logger log = LoggerFactory.getLogger(NewlyEditedAccountProfileServiceImpl.class);

	@Inject
	private NewlyEditedAccountProfileRepository newlyEditedAccountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Override
	public NewlyEditedAccountProfile accountProfileToNewlyEditedAccountProfile(AccountProfile accountProfile) {

		NewlyEditedAccountProfile newlyEditedAccountProfile = new NewlyEditedAccountProfile();

		newlyEditedAccountProfile.setAccountProfile(accountProfile);
		newlyEditedAccountProfile.setAccountStatus(AccountStatus.Unverified);
		newlyEditedAccountProfile.setDefaultPriceLevel(accountProfile.getDefaultPriceLevel());
		newlyEditedAccountProfile.setAccountType(accountProfile.getAccountType());
		newlyEditedAccountProfile.setUser(accountProfile.getUser());
		newlyEditedAccountProfile.setPid(accountProfile.getPid());
		newlyEditedAccountProfile.setName(accountProfile.getName());
		newlyEditedAccountProfile.setAlias(accountProfile.getAlias());
		newlyEditedAccountProfile.setDescription(accountProfile.getDescription());
		newlyEditedAccountProfile.setAddress(accountProfile.getAddress());
		newlyEditedAccountProfile.setCity(accountProfile.getCity());
		newlyEditedAccountProfile.setLocation(accountProfile.getLocation());
		newlyEditedAccountProfile.setPin(accountProfile.getPin());
		newlyEditedAccountProfile.setLatitude(accountProfile.getLatitude());
		newlyEditedAccountProfile.setLongitude(accountProfile.getLongitude());
		newlyEditedAccountProfile.setPhone1(accountProfile.getPhone1());
		newlyEditedAccountProfile.setPhone2(accountProfile.getPhone2());
		newlyEditedAccountProfile.setWhatsAppNo(accountProfile.getWhatsAppNo());
		newlyEditedAccountProfile.setEmail1(accountProfile.getEmail1());
		newlyEditedAccountProfile.setEmail2(accountProfile.getEmail2());
		newlyEditedAccountProfile.setAccountStatus(accountProfile.getAccountStatus());
		newlyEditedAccountProfile.setImportStatus(accountProfile.getImportStatus());
		newlyEditedAccountProfile.setCreditDays(accountProfile.getCreditDays());
		newlyEditedAccountProfile.setCreditLimit(accountProfile.getCreditLimit());
		newlyEditedAccountProfile.setContactPerson(accountProfile.getContactPerson());
		newlyEditedAccountProfile.setDataSourceType(accountProfile.getDataSourceType());
		newlyEditedAccountProfile.setTinNo(accountProfile.getTinNo());
		newlyEditedAccountProfile.setClosingBalance(accountProfile.getClosingBalance());
		newlyEditedAccountProfile.setDefaultDiscountPercentage(accountProfile.getDefaultDiscountPercentage());
		newlyEditedAccountProfile.setTrimChar(accountProfile.getTrimChar());
		newlyEditedAccountProfile.setLeadToCashStage(accountProfile.getLeadToCashStage());
		newlyEditedAccountProfile.setStateName(accountProfile.getStateName());
		newlyEditedAccountProfile.setCountryName(accountProfile.getCountryName());
		newlyEditedAccountProfile.setGstRegistrationType(accountProfile.getGstRegistrationType());

		return newlyEditedAccountProfile;
	}

	@Override
	public NewlyEditedAccountProfileDTO findOneByPid(String pid) {
		log.debug("Request to get AccountProfile by pid : {}", pid);

		Optional<NewlyEditedAccountProfile> opNewlyEditedAccountProfile = newlyEditedAccountProfileRepository
				.findOneByPid(pid);

		NewlyEditedAccountProfileDTO newlyEditedAccountProfileDTO = new NewlyEditedAccountProfileDTO();

		if (opNewlyEditedAccountProfile.isPresent()) {

			newlyEditedAccountProfileDTO = newlyEditedAccountProfileToNewlyEditedAccountProfileDTO(
					opNewlyEditedAccountProfile.get());

		}

		return newlyEditedAccountProfileDTO;
	}

	@Override
	public List<NewlyEditedAccountProfileDTO> findByCompanyIdAndUserIdInAndCreatedDateBetweenOrderAndAccountStatusByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate,
			List<AccountStatus> accountStatus) {

		List<NewlyEditedAccountProfile> accountProfileList = newlyEditedAccountProfileRepository
				.findByCompanyIdAndUserIdInAndCreatefDateBetweenOrderAndAccountStatusByCreatedDateDesc(companyId,
						userIds, fromDate, toDate, accountStatus);

		List<NewlyEditedAccountProfileDTO> accountProfileListDTO = new ArrayList<>();

		accountProfileListDTO = newlyEditedAccountProfileListToNewlyEditedAccountProfileListDTO(accountProfileList);

		return accountProfileListDTO;
	}

	private List<NewlyEditedAccountProfileDTO> newlyEditedAccountProfileListToNewlyEditedAccountProfileListDTO(
			List<NewlyEditedAccountProfile> accountProfileList) {

		List<NewlyEditedAccountProfileDTO> accountProfileListDTO = new ArrayList<>();

		for (NewlyEditedAccountProfile newAp : accountProfileList) {

			accountProfileListDTO.add(newlyEditedAccountProfileToNewlyEditedAccountProfileDTO(newAp));

		}

		return accountProfileListDTO;
	}

	private NewlyEditedAccountProfileDTO newlyEditedAccountProfileToNewlyEditedAccountProfileDTO(
			NewlyEditedAccountProfile newlyEditedAccountProfile) {

		Optional<EmployeeProfile> opEmp = employeeProfileRepository
				.findByUserPid(newlyEditedAccountProfile.getUser().getPid());

		NewlyEditedAccountProfileDTO newlyEditedAccountProfileDTO = new NewlyEditedAccountProfileDTO();

		if (opEmp.isPresent()) {

			newlyEditedAccountProfileDTO = new NewlyEditedAccountProfileDTO(newlyEditedAccountProfile);
			newlyEditedAccountProfileDTO.setEmployeeName(opEmp.get().getName());

		}

		return newlyEditedAccountProfileDTO;
	}

}
