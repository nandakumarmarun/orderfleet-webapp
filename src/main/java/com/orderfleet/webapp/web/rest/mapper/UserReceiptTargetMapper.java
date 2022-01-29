package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;

/**
 * Mapper for the entity UserReceiptTarget and its DTO UserReceiptTargetDTO.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Component
public abstract class UserReceiptTargetMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private UserRepository userRepository;

//	@Mapping(target = "achievedAmount", ignore = true)
//	@Mapping(source = "user.pid", target = "userPid")
//	@Mapping(source = "user.firstName", target = "userName")
	public abstract UserReceiptTargetDTO userReceiptTargetToUserReceiptTargetDTO(UserReceiptTarget userReceiptTarget);

	public abstract List<UserReceiptTargetDTO> userReceiptTargetsToUserReceiptTargetDTOs(
			List<UserReceiptTarget> userReceiptTargets);

//	@Mapping(source = "userPid", target = "user")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract UserReceiptTarget userReceiptTargetDTOToUserReceiptTarget(
			UserReceiptTargetDTO userReceiptTargetDTO);

	public abstract List<UserReceiptTarget> userReceiptTargetDTOsToUserReceiptTargets(
			List<UserReceiptTargetDTO> userReceiptTargetDTOs);

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
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