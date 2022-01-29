package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * Mapper for the entity User and its DTO UserDTO.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Component
public abstract class UserMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
//	@Mapping(target = "companyId", ignore = true)
//	@Mapping(target = "companyPid", ignore = true)
//	@Mapping(target = "companyName", ignore = true)
	public abstract UserDTO userToUserDTO(User user);

	public abstract List<UserDTO> usersToUserDTOs(List<User> users);

//	@Mapping(source = "companyPid", target = "company")
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "createdDate", ignore = true)
//	@Mapping(target = "lastModifiedBy", ignore = true)
//	@Mapping(target = "lastModifiedDate", ignore = true)
//	@Mapping(target = "persistentTokens", ignore = true)
//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "deviceKey", ignore = true)
//	@Mapping(target = "deviceType", ignore = true)
//	@Mapping(target = "activationKey", ignore = true)
//	@Mapping(target = "resetKey", ignore = true)
//	@Mapping(target = "resetDate", ignore = true)
//	@Mapping(target = "password", ignore = true)
//	@Mapping(target = "pid", ignore = true)
//	@Mapping(target = "version", ignore = true)
	public abstract User userDTOToUser(UserDTO userDTO);

	public abstract List<User> userDTOsToUsers(List<UserDTO> userDTOs);

	public User userFromId(Long id) {
		if (id == null) {
			return null;
		}
		User user = new User();
		user.setId(id);
		return user;
	}

	public Company companyFromPid(String pid) {
		if (pid == null || pid.isEmpty()) {
			return null;
		}
		Company company = new Company();
		company.setPid(pid);
		return company;
	}

	public Set<String> stringsFromAuthorities(Set<Authority> authorities) {
		return authorities.stream().map(Authority::getName).collect(Collectors.toSet());
	}

	public Set<Authority> authoritiesFromStrings(Set<String> strings) {
		return strings.stream().map(string -> {
			Authority auth = new Authority();
			auth.setName(string);
			return auth;
		}).collect(Collectors.toSet());
	}
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
