package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * Mapper for the entity User and its DTO UserDTO.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {

	@Mapping(target = "companyId", ignore = true)
	@Mapping(target = "companyPid", ignore = true)
	@Mapping(target = "companyName", ignore = true)
	UserDTO userToUserDTO(User user);

	List<UserDTO> usersToUserDTOs(List<User> users);

	@Mapping(source = "companyPid", target = "company")
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedBy", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "persistentTokens", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "deviceKey", ignore = true)
	@Mapping(target = "deviceType", ignore = true)
	@Mapping(target = "activationKey", ignore = true)
	@Mapping(target = "resetKey", ignore = true)
	@Mapping(target = "resetDate", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "pid", ignore = true)
	@Mapping(target = "version", ignore = true)
	User userDTOToUser(UserDTO userDTO);

	List<User> userDTOsToUsers(List<UserDTO> userDTOs);

	default User userFromId(Long id) {
		if (id == null) {
			return null;
		}
		User user = new User();
		user.setId(id);
		return user;
	}

	default Company companyFromPid(String pid) {
		if (pid == null || pid.isEmpty()) {
			return null;
		}
		Company company = new Company();
		company.setPid(pid);
		return company;
	}

	default Set<String> stringsFromAuthorities(Set<Authority> authorities) {
		return authorities.stream().map(Authority::getName).collect(Collectors.toSet());
	}

	default Set<Authority> authoritiesFromStrings(Set<String> strings) {
		return strings.stream().map(string -> {
			Authority auth = new Authority();
			auth.setName(string);
			return auth;
		}).collect(Collectors.toSet());
	}
}
