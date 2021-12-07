package com.orderfleet.webapp.web.rest.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.repository.DistrictCRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.StateCRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

/**
 * Mapper for the entity AccountProfile and its DTO AccountProfileDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class AccountProfileMapper {
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CounrtyCRepository countrycrepository;

	@Inject
	StateCRepository statecRepository;

	@Inject
	DistrictCRepository districtcRepository;

	@Mapping(source = "defaultPriceLevel.pid", target = "defaultPriceLevelPid")
	@Mapping(source = "defaultPriceLevel.name", target = "defaultPriceLevelName")
	@Mapping(source = "accountType.pid", target = "accountTypePid")
	@Mapping(source = "accountType.name", target = "accountTypeName")
	@Mapping(source = "countryc.id", target = "countryId")
	@Mapping(source = "statec.id", target = "stateId")
	@Mapping(source = "districtc.id", target ="districtId")

	@Mapping(source = "user.firstName", target = "userName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "geoTaggedUser.firstName", target = "geoTaggedUserName")
	@Mapping(source = "geoTaggedUser.pid", target = "geoTaggedUserPid")
	@Mapping(source = "geoTaggedUser.login", target = "geoTaggedUserLogin")
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		 AccountType ap= accountTypeRepository.findOneByPid(pid).map(accountType -> accountType).orElse(null);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
				return ap;

	}

	public CountryC countryFromId(Long id) {

		if (id == null) {
			return null;
		}
		return countrycrepository.findOne(id);

	}

	public StateC stateFromId(Long id) {

		if (id == null ) {
			return null;
		}
		return statecRepository.findOne(id);

	}

	public DistrictC districtFromId(Long id) {

		if (id == null) {
			return null;
		}
		return districtcRepository.findOne(id);

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
