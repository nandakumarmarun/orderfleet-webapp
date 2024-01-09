package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.District;
import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Component
public class AccountProfileMapperImpl extends AccountProfileMapper {

	@Override
	public AccountProfileDTO accountProfileToAccountProfileDTO(AccountProfile accountProfile) {
		if (accountProfile == null) {
			return null;
		}

		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();

		accountProfileDTO.setDefaultPriceLevelName(accountProfileDefaultPriceLevelName(accountProfile));
		accountProfileDTO.setStateId(accountProfileStatecId(accountProfile));
		accountProfileDTO.setGeoTaggedUserName(accountProfileGeoTaggedUserFirstName(accountProfile));
		accountProfileDTO.setAccountTypeName(accountProfileAccountTypeName(accountProfile));
		accountProfileDTO.setUserName(accountProfileUserFirstName(accountProfile));
		accountProfileDTO.setCountryId(accountProfileCountrycId(accountProfile));
		accountProfileDTO.setGeoTaggedUserPid(accountProfileGeoTaggedUserPid(accountProfile));
		accountProfileDTO.setDistrictId(accountProfileDistrictcId(accountProfile));
		accountProfileDTO.setAccountTypePid(accountProfileAccountTypePid(accountProfile));
		accountProfileDTO.setDefaultPriceLevelPid(accountProfileDefaultPriceLevelPid(accountProfile));
		accountProfileDTO.setUserPid(accountProfileUserPid(accountProfile));
		accountProfileDTO.setGeoTaggedUserLogin(accountProfileGeoTaggedUserLogin(accountProfile));
		accountProfileDTO.setAccountStatus(accountProfile.getAccountStatus());
		accountProfileDTO.setActivated(accountProfile.getActivated());
		accountProfileDTO.setAddress(accountProfile.getAddress());
		accountProfileDTO.setAlias(accountProfile.getAlias());
		accountProfileDTO.setCity(accountProfile.getCity());
		accountProfileDTO.setClosingBalance(accountProfile.getClosingBalance());
		accountProfileDTO.setContactPerson(accountProfile.getContactPerson());
		accountProfileDTO.setCountryName(accountProfile.getCountryName());
		accountProfileDTO.setCreatedDate(accountProfile.getCreatedDate());
		accountProfileDTO.setCreditDays(accountProfile.getCreditDays());
		accountProfileDTO.setCreditLimit(accountProfile.getCreditLimit());
		accountProfileDTO.setCustomerCode(accountProfile.getCustomerCode());
		accountProfileDTO.setCustomerId(accountProfile.getCustomerId());
		accountProfileDTO.setDataSourceType(accountProfile.getDataSourceType());
		accountProfileDTO.setDefaultDiscountPercentage(accountProfile.getDefaultDiscountPercentage());
		accountProfileDTO.setDescription(accountProfile.getDescription());
		accountProfileDTO.setDistrictName(accountProfileDistrictName(accountProfile));
		accountProfileDTO.setEmail1(accountProfile.getEmail1());
		accountProfileDTO.setEmail2(accountProfile.getEmail2());
		accountProfileDTO.setGeoTaggedTime(accountProfile.getGeoTaggedTime());
		accountProfileDTO.setGeoTaggingType(accountProfile.getGeoTaggingType());
		accountProfileDTO.setGstRegistrationType(accountProfile.getGstRegistrationType());
		accountProfileDTO.setGeoTaggingStatus(accountProfile.getGeoTaggingStatus());
		if (accountProfile.getImportStatus() != null) {
			accountProfileDTO.setImportStatus(accountProfile.getImportStatus());
		}
		accountProfileDTO.setLastModifiedDate(accountProfile.getLastModifiedDate());
		accountProfileDTO.setLatitude(accountProfile.getLatitude());
		accountProfileDTO.setLeadToCashStage(accountProfile.getLeadToCashStage());
		accountProfileDTO.setLocation(accountProfile.getLocation());
		accountProfileDTO.setLocationRadius(accountProfile.getLocationRadius());
		accountProfileDTO.setLongitude(accountProfile.getLongitude());
		accountProfileDTO.setMailingName(accountProfile.getMailingName());
		accountProfileDTO.setName(accountProfile.getName());
		accountProfileDTO.setPhone1(accountProfile.getPhone1());
		accountProfileDTO.setPhone2(accountProfile.getPhone2());
		accountProfileDTO.setPid(accountProfile.getPid());
		accountProfileDTO.setPin(accountProfile.getPin());
		accountProfileDTO.setStateName(accountProfile.getStateName());
		accountProfileDTO.setTinNo(accountProfile.getTinNo());
		accountProfileDTO.setTrimChar(accountProfile.getTrimChar());
		accountProfileDTO.setWhatsAppNo(accountProfile.getWhatsAppNo());

		return accountProfileDTO;
	}

	public AccountProfileDTO accountProfileToAccountProfileDTODescription(AccountProfile accountProfile) {
		if (accountProfile == null) {
			return null;
		}

		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();

		accountProfileDTO.setDefaultPriceLevelName(accountProfileDefaultPriceLevelName(accountProfile));
		accountProfileDTO.setStateId(accountProfileStatecId(accountProfile));
		accountProfileDTO.setGeoTaggedUserName(accountProfileGeoTaggedUserFirstName(accountProfile));
		accountProfileDTO.setAccountTypeName(accountProfileAccountTypeName(accountProfile));
		accountProfileDTO.setUserName(accountProfileUserFirstName(accountProfile));
		accountProfileDTO.setCountryId(accountProfileCountrycId(accountProfile));
		accountProfileDTO.setGeoTaggedUserPid(accountProfileGeoTaggedUserPid(accountProfile));
		accountProfileDTO.setDistrictId(accountProfileDistrictcId(accountProfile));
		accountProfileDTO.setAccountTypePid(accountProfileAccountTypePid(accountProfile));
		accountProfileDTO.setDefaultPriceLevelPid(accountProfileDefaultPriceLevelPid(accountProfile));
		accountProfileDTO.setUserPid(accountProfileUserPid(accountProfile));
		accountProfileDTO.setGeoTaggedUserLogin(accountProfileGeoTaggedUserLogin(accountProfile));
		accountProfileDTO.setAccountStatus(accountProfile.getAccountStatus());
		accountProfileDTO.setActivated(accountProfile.getActivated());
		accountProfileDTO.setAddress(accountProfile.getAddress());
		accountProfileDTO.setAlias(accountProfile.getAlias());
		accountProfileDTO.setCity(accountProfile.getCity());
		accountProfileDTO.setDistrictName(accountProfileDistrictName(accountProfile));
		accountProfileDTO.setClosingBalance(accountProfile.getClosingBalance());
		accountProfileDTO.setContactPerson(accountProfile.getContactPerson());
		accountProfileDTO.setCountryName(accountProfile.getCountryName());
		accountProfileDTO.setCreatedDate(accountProfile.getCreatedDate());
		accountProfileDTO.setCreditDays(accountProfile.getCreditDays());
		accountProfileDTO.setCreditLimit(accountProfile.getCreditLimit());
		accountProfileDTO.setCustomerCode(accountProfile.getCustomerCode());
		accountProfileDTO.setCustomerId(accountProfile.getCustomerId());
		accountProfileDTO.setDataSourceType(accountProfile.getDataSourceType());
		accountProfileDTO.setDefaultDiscountPercentage(accountProfile.getDefaultDiscountPercentage());
		accountProfileDTO.setDescription(accountProfile.getDescription());
		accountProfileDTO.setEmail1(accountProfile.getEmail1());
		accountProfileDTO.setEmail2(accountProfile.getEmail2());
		accountProfileDTO.setGeoTaggedTime(accountProfile.getGeoTaggedTime());
		accountProfileDTO.setGeoTaggingType(accountProfile.getGeoTaggingType());
		accountProfileDTO.setGstRegistrationType(accountProfile.getGstRegistrationType());
		accountProfileDTO.setGeoTaggingStatus(accountProfile.getGeoTaggingStatus());
		if (accountProfile.getImportStatus() != null) {
			accountProfileDTO.setImportStatus(accountProfile.getImportStatus());
		}
		accountProfileDTO.setLastModifiedDate(accountProfile.getLastModifiedDate());
		accountProfileDTO.setLatitude(accountProfile.getLatitude());
		accountProfileDTO.setLeadToCashStage(accountProfile.getLeadToCashStage());
		accountProfileDTO.setLocation(accountProfile.getLocation());
		accountProfileDTO.setLocationRadius(accountProfile.getLocationRadius());
		accountProfileDTO.setLongitude(accountProfile.getLongitude());
		accountProfileDTO.setMailingName(accountProfile.getMailingName());
		accountProfileDTO.setName(
				accountProfile.getDescription() != null && !accountProfile.getDescription().equalsIgnoreCase("common")
						? accountProfile.getDescription()
						: accountProfile.getName());
		accountProfileDTO.setPhone1(accountProfile.getPhone1());
		accountProfileDTO.setPhone2(accountProfile.getPhone2());
		accountProfileDTO.setPid(accountProfile.getPid());
		accountProfileDTO.setPin(accountProfile.getPin());
		accountProfileDTO.setStateName(accountProfile.getStateName());
		accountProfileDTO.setTinNo(accountProfile.getTinNo());
		accountProfileDTO.setTrimChar(accountProfile.getTrimChar());
		accountProfileDTO.setWhatsAppNo(accountProfile.getWhatsAppNo());
		return accountProfileDTO;
	}

	@Override
	public List<AccountProfileDTO> accountProfilesToAccountProfileDTOs(List<AccountProfile> accountProfiles) {
		if (accountProfiles == null) {
			return null;
		}
		List<AccountProfileDTO> list = new ArrayList<AccountProfileDTO>();
		if (getCompanyCofig()) {

			for (AccountProfile accountProfile : accountProfiles) {
				list.add(accountProfileToAccountProfileDTODescription(accountProfile));
			}
		} else {
			for (AccountProfile accountProfile : accountProfiles) {
				list.add(accountProfileToAccountProfileDTO(accountProfile));
			}
		}
		return list;
	}

	@Override
	public AccountProfile accountProfileDTOToAccountProfile(AccountProfileDTO accountProfileDTO) {
		if (accountProfileDTO == null) {
			return null;
		}

		AccountProfile accountProfile = new AccountProfile();

		accountProfile.setDefaultPriceLevel(priceLevelFromPid(accountProfileDTO.getDefaultPriceLevelPid()));
		accountProfile.setAccountType(accountTypeFromPid(accountProfileDTO.getAccountTypePid()));
		accountProfile.setUser(userFromPid(accountProfileDTO.getUserPid()));
		accountProfile.setAccountStatus(accountProfileDTO.getAccountStatus());
		accountProfile.setAddress(accountProfileDTO.getAddress());
		accountProfile.setAlias(accountProfileDTO.getAlias());
		accountProfile.setCity(accountProfileDTO.getCity());
		accountProfile.setClosingBalance(accountProfileDTO.getClosingBalance());
		accountProfile.setContactPerson(accountProfileDTO.getContactPerson());
		accountProfile.setCountryName(accountProfileDTO.getCountryName());
		accountProfile.setCreditDays(accountProfileDTO.getCreditDays());
		accountProfile.setCreditLimit(accountProfileDTO.getCreditLimit());
		accountProfile.setCustomerCode(accountProfileDTO.getCustomerCode());
		accountProfile.setCustomerId(accountProfileDTO.getCustomerId());
		accountProfile.setDataSourceType(accountProfileDTO.getDataSourceType());
		accountProfile.setDefaultDiscountPercentage(accountProfileDTO.getDefaultDiscountPercentage());
		accountProfile.setDescription(accountProfileDTO.getDescription());
		accountProfile.setEmail1(accountProfileDTO.getEmail1());
		accountProfile.setEmail2(accountProfileDTO.getEmail2());
		accountProfile.setGeoTaggedTime(accountProfileDTO.getGeoTaggedTime());
		accountProfile.setGeoTaggingType(accountProfileDTO.getGeoTaggingType());
		accountProfile.setGstRegistrationType(accountProfileDTO.getGstRegistrationType());
		accountProfile.setImportStatus(accountProfileDTO.isImportStatus());
		accountProfile.setLatitude(accountProfileDTO.getLatitude());
		accountProfile.setLeadToCashStage(accountProfileDTO.getLeadToCashStage());
		accountProfile.setLocation(accountProfileDTO.getLocation());
		accountProfile.setLocationRadius(accountProfileDTO.getLocationRadius());
		accountProfile.setLongitude(accountProfileDTO.getLongitude());
		accountProfile.setMailingName(accountProfileDTO.getMailingName());
		accountProfile.setName(accountProfileDTO.getName());
		accountProfile.setPhone1(accountProfileDTO.getPhone1());
		accountProfile.setPhone2(accountProfileDTO.getPhone2());
		accountProfile.setPid(accountProfileDTO.getPid());
		accountProfile.setPin(accountProfileDTO.getPin());
		accountProfile.setStateName(accountProfileDTO.getStateName());
		accountProfile.setTinNo(accountProfileDTO.getTinNo());
		accountProfile.setTrimChar(accountProfileDTO.getTrimChar());
		accountProfile.setWhatsAppNo(accountProfileDTO.getWhatsAppNo());
		accountProfile.setCountryName(accountProfileDTO.getCountryName());
		accountProfile.setStateName(accountProfileDTO.getStateName());
		accountProfile.setDistrictName(accountProfileDTO.getDistrictName());

		return accountProfile;
	}

	@Override
	public List<AccountProfile> accountProfileDTOsToAccountProfiles(List<AccountProfileDTO> accountProfileDTOs) {
		if (accountProfileDTOs == null) {
			return null;
		}

		List<AccountProfile> list = new ArrayList<AccountProfile>();
		for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
			list.add(accountProfileDTOToAccountProfile(accountProfileDTO));
		}

		return list;
	}

	private String accountProfileDefaultPriceLevelName(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		PriceLevel defaultPriceLevel = accountProfile.getDefaultPriceLevel();
		if (defaultPriceLevel == null) {
			return null;
		}
		String name = defaultPriceLevel.getName();
		if (name == null) {
			return null;
		}
		if (defaultPriceLevel.getDescription() != null && getCompanyCofig()
				&& !defaultPriceLevel.getDescription().equals("common")) {
			return defaultPriceLevel.getDescription();
		}
		return name;
	}

	private Long accountProfileStatecId(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		StateC statec = accountProfile.getStatec();
		if (statec == null) {
			return null;
		}
		Long id = statec.getId();
		if (id == null) {
			return null;
		}
		return id;
	}

	private String accountProfileGeoTaggedUserFirstName(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		User geoTaggedUser = accountProfile.getGeoTaggedUser();
		if (geoTaggedUser == null) {
			return null;
		}
		String firstName = geoTaggedUser.getFirstName();
		if (firstName == null) {
			return null;
		}
		return firstName;
	}

	private String accountProfileAccountTypeName(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		AccountType accountType = accountProfile.getAccountType();
		if (accountType == null) {
			return null;
		}
		String name = accountType.getName();
		if (name == null) {
			return null;
		}
		if (accountType.getDescription() != null && getCompanyCofig()
				&& !accountType.getDescription().equals("common")) {
			return accountType.getDescription();
		}
		return name;
	}

	private String accountProfileUserFirstName(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		User user = accountProfile.getUser();
		if (user == null) {
			return null;
		}
		String firstName = user.getFirstName();
		if (firstName == null) {
			return null;
		}
		return firstName;
	}
	
	private String accountProfileDistrictName(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		DistrictC district = accountProfile.getDistrictc();
		if (district == null) {
			return null;
		}
		String districtName = district.getName();
		if (districtName == null) {
			return null;
		}
		return districtName;
	}

	private Long accountProfileCountrycId(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		CountryC countryc = accountProfile.getCountryc();
		if (countryc == null) {
			return null;
		}
		long id = countryc.getId();
		return id;
	}

	private String accountProfileGeoTaggedUserPid(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		User geoTaggedUser = accountProfile.getGeoTaggedUser();
		if (geoTaggedUser == null) {
			return null;
		}
		String pid = geoTaggedUser.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private Long accountProfileDistrictcId(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		DistrictC districtc = accountProfile.getDistrictc();
		if (districtc == null) {
			return null;
		}
		Long id = districtc.getId();
		if (id == null) {
			return null;
		}
		return id;
	}

	private String accountProfileAccountTypePid(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		AccountType accountType = accountProfile.getAccountType();
		if (accountType == null) {
			return null;
		}
		String pid = accountType.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String accountProfileDefaultPriceLevelPid(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		PriceLevel defaultPriceLevel = accountProfile.getDefaultPriceLevel();
		if (defaultPriceLevel == null) {
			return null;
		}
		String pid = defaultPriceLevel.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String accountProfileUserPid(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		User user = accountProfile.getUser();
		if (user == null) {
			return null;
		}
		String pid = user.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String accountProfileGeoTaggedUserLogin(AccountProfile accountProfile) {

		if (accountProfile == null) {
			return null;
		}
		User geoTaggedUser = accountProfile.getGeoTaggedUser();
		if (geoTaggedUser == null) {
			return null;
		}
		String login = geoTaggedUser.getLogin();
		if (login == null) {
			return null;
		}
		return login;
	}

	private String accountProfileName(AccountProfile accountProfile) {
		if (accountProfile.getDescription() != null && getCompanyCofig()
				&& !accountProfile.getDescription().equals("common")) {
			return accountProfile.getDescription();
		}

		return accountProfile.getName();
	}
}
