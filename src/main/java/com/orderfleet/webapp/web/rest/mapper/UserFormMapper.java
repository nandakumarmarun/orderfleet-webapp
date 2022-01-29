package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.UserFormDTO;

/**
 * Mapper for the entity UserForm and its DTO UserFormDTO.
 *
 * @author Sarath
 * @since Apr 19, 2017
 */
@Component
public abstract class UserFormMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private FormRepository formRepository;

	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "user.login", target = "userName")
	@Mapping(source = "form.pid", target = "formPid")
	@Mapping(source = "form.name", target = "formName")
	public abstract UserFormDTO userFormToUserFormDTO(UserForm userForm);

	public abstract List<UserFormDTO> userFormsToUserFormDTOs(List<UserForm> userForms);

	@Mapping(source = "userPid", target = "user")
	@Mapping(source = "formPid", target = "form")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract UserForm userFormDTOToUserForm(UserFormDTO userFormDTO);

	public abstract List<UserForm> userFormDTOsToUserForms(List<UserFormDTO> userFormDTOs);

	public Form userFormFormPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return formRepository.findOneByPid(pid).map(form -> form).orElse(null);
	}

	public User userFormUserPid(String pid) {
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
