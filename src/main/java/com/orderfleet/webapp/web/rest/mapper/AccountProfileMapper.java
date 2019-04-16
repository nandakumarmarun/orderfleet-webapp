package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

/**
 * Mapper for the entity AccountProfile and its DTO AccountProfileDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class AccountProfileMapper {

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private UserRepository userRepository;

	@Mapping(source = "defaultPriceLevel.pid", target = "defaultPriceLevelPid")
	@Mapping(source = "defaultPriceLevel.name", target = "defaultPriceLevelName")
	@Mapping(source = "accountType.pid", target = "accountTypePid")
	@Mapping(source = "accountType.name", target = "accountTypeName")
	@Mapping(source = "user.firstName", target = "userName")
	@Mapping(source = "user.pid", target = "userPid")
	public abstract AccountProfileDTO accountProfileToAccountProfileDTO(AccountProfile accountProfile);

	public abstract List<AccountProfileDTO> accountProfilesToAccountProfileDTOs(List<AccountProfile> accountProfiles);
	
	@Mapping(source = "defaultPriceLevelPid", target = "defaultPriceLevel")
	@Mapping(target = "activated", ignore = true)
	@Mapping(source = "accountTypePid", target = "accountType")
	@Mapping(source = "userPid", target = "user")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract AccountProfile accountProfileDTOToAccountProfile(AccountProfileDTO accountProfileDTO);


	public abstract List<AccountProfile> accountProfileDTOsToAccountProfiles(
			List<AccountProfileDTO> accountProfileDTOs);

	public AccountType accountTypeFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return accountTypeRepository.findOneByPid(pid).map(accountType -> accountType).orElse(null);
	}

	public PriceLevel priceLevelFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return priceLevelRepository.findOneByPid(pid).map(priceLevel -> priceLevel).orElse(null);
	}
	
	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}

	


}
