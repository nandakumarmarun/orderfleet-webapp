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
import com.orderfleet.webapp.domain.PriceLevel;
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

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public NewlyEditedAccountProfile accountProfileToNewlyEditedAccountProfile(AccountProfileDTO accountProfileDTO,
			Optional<AccountProfile> exisitingAccountProfile) {

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

		NewlyEditedAccountProfile newlyEditedAccountProfile = new NewlyEditedAccountProfile();

		newlyEditedAccountProfile.setAccountProfile(exisitingAccountProfile.get());
		newlyEditedAccountProfile.setAccountStatus(AccountStatus.Unverified);
		newlyEditedAccountProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		newlyEditedAccountProfile.setDefaultPriceLevel(exisitingAccountProfile.get().getDefaultPriceLevel());
		newlyEditedAccountProfile.setAccountType(exisitingAccountProfile.get().getAccountType());
		if (opUser.isPresent()) {
			newlyEditedAccountProfile.setUser(opUser.get());
		}
		newlyEditedAccountProfile.setPid(NewlyEditedAccountProfileService.PID_PREFIX + RandomUtil.generatePid());
		newlyEditedAccountProfile.setName(accountProfileDTO.getName());
		newlyEditedAccountProfile.setAlias(accountProfileDTO.getAlias());
		newlyEditedAccountProfile.setDescription(accountProfileDTO.getDescription());
		newlyEditedAccountProfile.setAddress(accountProfileDTO.getAddress());
		newlyEditedAccountProfile.setCity(accountProfileDTO.getCity());
		newlyEditedAccountProfile.setLocation(accountProfileDTO.getLocation());
		newlyEditedAccountProfile.setPin(accountProfileDTO.getPin());
		newlyEditedAccountProfile.setLatitude(accountProfileDTO.getLatitude());
		newlyEditedAccountProfile.setLongitude(accountProfileDTO.getLongitude());
		newlyEditedAccountProfile.setPhone1(accountProfileDTO.getPhone1());
		newlyEditedAccountProfile.setPhone2(accountProfileDTO.getPhone2());
		newlyEditedAccountProfile.setWhatsAppNo(accountProfileDTO.getWhatsAppNo());
		newlyEditedAccountProfile.setEmail1(accountProfileDTO.getEmail1());
		newlyEditedAccountProfile.setEmail2(accountProfileDTO.getEmail2());
		newlyEditedAccountProfile.setAccountStatus(AccountStatus.Unverified);
		newlyEditedAccountProfile.setContactPerson(accountProfileDTO.getContactPerson());
		newlyEditedAccountProfile.setDataSourceType(DataSourceType.MOBILE);
		newlyEditedAccountProfile.setTinNo(accountProfileDTO.getTinNo());
		newlyEditedAccountProfile.setClosingBalance(accountProfileDTO.getClosingBalance());
		newlyEditedAccountProfile.setDefaultDiscountPercentage(accountProfileDTO.getDefaultDiscountPercentage());
		newlyEditedAccountProfile.setTrimChar(accountProfileDTO.getTrimChar());
		newlyEditedAccountProfile.setLeadToCashStage(accountProfileDTO.getLeadToCashStage());
		newlyEditedAccountProfile.setStateName(accountProfileDTO.getStateName());
		newlyEditedAccountProfile.setCountryName(accountProfileDTO.getCountryName());
		newlyEditedAccountProfile.setGstRegistrationType(accountProfileDTO.getGstRegistrationType());

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

			newlyEditedAccountProfileDTO = dtoConversion(newlyEditedAccountProfile);
			newlyEditedAccountProfileDTO.setEmployeeName(opEmp.get().getName());

		}

		return newlyEditedAccountProfileDTO;
	}

	private NewlyEditedAccountProfileDTO dtoConversion(NewlyEditedAccountProfile newlyEditedAccountProfile) {

		NewlyEditedAccountProfileDTO newlyEditedAccountProfileDTO = new NewlyEditedAccountProfileDTO();

		AccountProfile accountprofile = newlyEditedAccountProfile.getAccountProfile();

		newlyEditedAccountProfileDTO.setName(accountprofile.getName());
		newlyEditedAccountProfileDTO.setAlias(
				accountprofile.getAlias() != null && !accountprofile.getAlias().equals("") ? accountprofile.getAlias()
						: "-");
		newlyEditedAccountProfileDTO.setTinNo(
				accountprofile.getTinNo() != null && !accountprofile.getTinNo().equals("") ? accountprofile.getTinNo()
						: "-");
		newlyEditedAccountProfileDTO
				.setDescription(accountprofile.getDescription() != null && !accountprofile.getDescription().equals("")
						? accountprofile.getDescription()
						: "-");
		newlyEditedAccountProfileDTO
				.setAddress(accountprofile.getAddress() != null && !accountprofile.getAddress().equals("")
						? accountprofile.getAddress()
						: "-");
		newlyEditedAccountProfileDTO.setCity(
				accountprofile.getCity() != null && !accountprofile.getCity().equals("") ? accountprofile.getCity()
						: "-");
		newlyEditedAccountProfileDTO.setPin(
				accountprofile.getPin() != null && !accountprofile.getPin().equals("") ? accountprofile.getPin() : "-");
		newlyEditedAccountProfileDTO
				.setPhone1(accountprofile.getPhone1() != null && !accountprofile.getPhone1().equals("")
						? accountprofile.getPhone1()
						: "-");
		newlyEditedAccountProfileDTO
				.setEmail1(accountprofile.getEmail1() != null && !accountprofile.getEmail1().equals("")
						? accountprofile.getEmail1()
						: "-");
		newlyEditedAccountProfileDTO
				.setEmail2(accountprofile.getEmail2() != null && !accountprofile.getEmail2().equals("")
						? accountprofile.getEmail2()
						: "-");
		newlyEditedAccountProfileDTO.setContactPerson(
				accountprofile.getContactPerson() != null && !accountprofile.getContactPerson().equals("")
						? accountprofile.getContactPerson()
						: "-");

		newlyEditedAccountProfileDTO.setCreatedDate(newlyEditedAccountProfile.getCreatedDate());
		newlyEditedAccountProfileDTO.setAccountStatus(newlyEditedAccountProfile.getAccountStatus());

		newlyEditedAccountProfileDTO.setNewlyEditedPid(newlyEditedAccountProfile.getPid());
		newlyEditedAccountProfileDTO.setNewlyEditedCreatedDate(newlyEditedAccountProfile.getCreatedDate());

		if (newlyEditedAccountProfile.getAlias() != null && !newlyEditedAccountProfile.getAlias().equals("")
				&& !newlyEditedAccountProfile.getAlias().equals(newlyEditedAccountProfileDTO.getAlias())) {
			newlyEditedAccountProfileDTO.setNewlyEditedAlias(newlyEditedAccountProfile.getAlias());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedAlias("-");
		}

		if (newlyEditedAccountProfile.getTinNo() != null && !newlyEditedAccountProfile.getTinNo().equals("")
				&& !newlyEditedAccountProfile.getTinNo().equals(newlyEditedAccountProfileDTO.getTinNo())) {
			newlyEditedAccountProfileDTO.setNewlyEditedTinNo(newlyEditedAccountProfile.getTinNo());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedTinNo("-");
		}

		if (newlyEditedAccountProfile.getDescription() != null && !newlyEditedAccountProfile.getDescription().equals("")

				&& !newlyEditedAccountProfile.getDescription().equals(newlyEditedAccountProfileDTO.getDescription())) {
			newlyEditedAccountProfileDTO.setNewlyEditedDescription(newlyEditedAccountProfile.getDescription());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedDescription("-");
		}

		if (newlyEditedAccountProfile.getAddress() != null && !newlyEditedAccountProfile.getAddress().equals("")
				&& !newlyEditedAccountProfile.getAddress().equals(newlyEditedAccountProfileDTO.getAddress())) {
			newlyEditedAccountProfileDTO.setNewlyEditedAddress(newlyEditedAccountProfile.getAddress());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedAddress("-");
		}

		if (newlyEditedAccountProfile.getCity() != null && !newlyEditedAccountProfile.getCity().equals("")
				&& !newlyEditedAccountProfile.getCity().equals(newlyEditedAccountProfileDTO.getCity())) {
			newlyEditedAccountProfileDTO.setNewlyEditedCity(newlyEditedAccountProfile.getCity());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedCity("-");
		}

		if (newlyEditedAccountProfile.getPin() != null && !newlyEditedAccountProfile.getPin().equals("")
				&& !newlyEditedAccountProfile.getPin().equals(newlyEditedAccountProfileDTO.getPin())) {
			newlyEditedAccountProfileDTO.setNewlyEditedPin(newlyEditedAccountProfile.getPin());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedPin("-");
		}

		if (newlyEditedAccountProfile.getPhone1() != null && !newlyEditedAccountProfile.getPhone1().equals("")
				&& !newlyEditedAccountProfile.getPhone1().equals(newlyEditedAccountProfileDTO.getPhone1())) {
			newlyEditedAccountProfileDTO.setNewlyEditedPhone1(newlyEditedAccountProfile.getPhone1());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedPhone1("-");
		}

		if (newlyEditedAccountProfile.getEmail1() != null && !newlyEditedAccountProfile.getEmail1().equals("")
				&& !newlyEditedAccountProfile.getEmail1().equals(newlyEditedAccountProfileDTO.getEmail1())) {
			newlyEditedAccountProfileDTO.setNewlyEditedEmail1(newlyEditedAccountProfile.getEmail1());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedEmail1("-");
		}

		if (newlyEditedAccountProfile.getEmail2() != null && !newlyEditedAccountProfile.getEmail2().equals("")
				&& !newlyEditedAccountProfile.getEmail2().equals(newlyEditedAccountProfileDTO.getEmail2())) {
			newlyEditedAccountProfileDTO.setNewlyEditedEmail2(newlyEditedAccountProfile.getEmail2());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedEmail2("-");
		}

		if (newlyEditedAccountProfile.getContactPerson() != null
				&& !newlyEditedAccountProfile.getContactPerson().equals("") && !newlyEditedAccountProfile
						.getContactPerson().equals(newlyEditedAccountProfileDTO.getContactPerson())) {
			newlyEditedAccountProfileDTO.setNewlyEditedContactPerson(newlyEditedAccountProfile.getContactPerson());
		} else {
			newlyEditedAccountProfileDTO.setNewlyEditedContactPerson("-");
		}

		return newlyEditedAccountProfileDTO;

	}

	@Override
	public AccountProfileDTO newlyEditedAccountProfileToAccountProfileDTO(
			NewlyEditedAccountProfile newlyEditedAccountProfile) {
		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();

		accountProfileDTO.setPid(newlyEditedAccountProfile.getAccountProfile().getPid());
		accountProfileDTO.setAccountTypeName(newlyEditedAccountProfile.getAccountProfile().getAccountType().getName());
		accountProfileDTO.setAccountTypePid(newlyEditedAccountProfile.getAccountProfile().getAccountType().getPid());
		accountProfileDTO.setName(newlyEditedAccountProfile.getAccountProfile().getName());
		accountProfileDTO.setAlias(newlyEditedAccountProfile.getAlias());
		accountProfileDTO.setDescription(newlyEditedAccountProfile.getDescription());
		accountProfileDTO.setAddress(newlyEditedAccountProfile.getAddress());
		accountProfileDTO.setCity(newlyEditedAccountProfile.getCity());
		accountProfileDTO
				.setGstRegistrationType(newlyEditedAccountProfile.getAccountProfile().getGstRegistrationType());
		accountProfileDTO.setLocation(newlyEditedAccountProfile.getAccountProfile().getLocation());
		accountProfileDTO.setPin(newlyEditedAccountProfile.getPin());
		accountProfileDTO.setLatitude(newlyEditedAccountProfile.getAccountProfile().getLatitude());
		accountProfileDTO.setLongitude(newlyEditedAccountProfile.getAccountProfile().getLongitude());
		accountProfileDTO.setPhone1(newlyEditedAccountProfile.getPhone1());
		accountProfileDTO.setPhone2(newlyEditedAccountProfile.getPhone2());
		accountProfileDTO.setEmail1(newlyEditedAccountProfile.getEmail1());
		accountProfileDTO.setEmail2(newlyEditedAccountProfile.getEmail2());
		accountProfileDTO.setWhatsAppNo(newlyEditedAccountProfile.getWhatsAppNo());
		accountProfileDTO.setAccountStatus(newlyEditedAccountProfile.getAccountStatus());
		accountProfileDTO.setCreditDays(newlyEditedAccountProfile.getCreditDays());
		accountProfileDTO.setCreditLimit(newlyEditedAccountProfile.getCreditLimit());
		accountProfileDTO.setContactPerson(newlyEditedAccountProfile.getContactPerson());
		accountProfileDTO.setActivated(newlyEditedAccountProfile.getActivated());
		accountProfileDTO.setTinNo(newlyEditedAccountProfile.getTinNo());
		accountProfileDTO.setClosingBalance(newlyEditedAccountProfile.getAccountProfile().getClosingBalance());
		if (newlyEditedAccountProfile.getAccountProfile().getImportStatus() != null) {
			accountProfileDTO.setImportStatus(newlyEditedAccountProfile.getAccountProfile().getImportStatus());
		}
		accountProfileDTO.setDefaultDiscountPercentage(
				newlyEditedAccountProfile.getAccountProfile().getDefaultDiscountPercentage());
		accountProfileDTO.setTrimChar(newlyEditedAccountProfile.getAccountProfile().getTrimChar());
		accountProfileDTO.setLeadToCashStage(newlyEditedAccountProfile.getAccountProfile().getLeadToCashStage());
		accountProfileDTO.setStateName(newlyEditedAccountProfile.getAccountProfile().getStateName());
		accountProfileDTO.setDataSourceType(newlyEditedAccountProfile.getAccountProfile().getDataSourceType());
		accountProfileDTO.setCountryName(newlyEditedAccountProfile.getAccountProfile().getCountryName());
		accountProfileDTO.setGeoTaggingType(newlyEditedAccountProfile.getAccountProfile().getGeoTaggingType());
		accountProfileDTO.setGeoTaggedTime(newlyEditedAccountProfile.getAccountProfile().getGeoTaggedTime());

		return accountProfileDTO;

	}

}
