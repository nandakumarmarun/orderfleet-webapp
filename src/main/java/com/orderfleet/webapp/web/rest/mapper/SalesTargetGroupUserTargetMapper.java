package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;

/**
 * Mapper for the entity SalesTargetGroupUserTarget and its DTO
 * SalesTargetGroupUserTargetDTO.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Component
public abstract class SalesTargetGroupUserTargetMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private UserRepository userRepository;

//	@Mapping(source = "accountProfile.pid", target = "accountProfilePid")
//	@Mapping(source = "accountProfile.name", target = "accountProfileName")
//	@Mapping(target = "achievedVolume", ignore = true)
//	@Mapping(target = "achievedAmount", ignore = true)
//	@Mapping(source = "salesTargetGroup.pid", target = "salesTargetGroupPid")
//	@Mapping(source = "salesTargetGroup.name", target = "salesTargetGroupName")
//	@Mapping(source = "salesTargetGroup.targetUnit", target = "targetUnit")
//	@Mapping(source = "user.pid", target = "userPid")
//	@Mapping(source = "user.firstName", target = "userName")
	public abstract SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetToSalesTargetGroupUserTargetDTO(
			SalesTargetGroupUserTarget salesTargetGroupUserTarget);

	public abstract List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(
			List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets);

//	@Mapping(target = "accountProfile", ignore = true)
//	@Mapping(source = "salesTargetGroupPid", target = "salesTargetGroup")
//	@Mapping(source = "userPid", target = "user")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract SalesTargetGroupUserTarget salesTargetGroupUserTargetDTOToSalesTargetGroupUserTarget(
			SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO);

	public abstract List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetDTOsToSalesTargetGroupUserTargets(
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs);

	public SalesTargetGroup salesTargetGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return salesTargetGroupRepository.findOneByPid(pid).map(salesTargetGroup -> salesTargetGroup).orElse(null);
	}

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
