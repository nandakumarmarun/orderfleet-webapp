package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.DocumentPrint;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;

/**
 * Mapper for the entity DocumentPrint and its DTO DocumentPrintDTO.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
@Component
public abstract class DocumentPrintMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

//	@Mapping(source = "user.login", target = "userLoginName")
//	@Mapping(source = "user.firstName", target = "userFirstName")
//	@Mapping(source = "user.lastName", target = "userLastName")
//	@Mapping(source = "user.pid", target = "userPid")
//	@Mapping(source = "document.pid", target = "documentPid")
//	@Mapping(source = "document.name", target = "documentName")
//	@Mapping(source = "activity.pid", target = "activityPid")
//	@Mapping(source = "activity.name", target = "activityName")
	public abstract DocumentPrintDTO documentPrintToDocumentPrintDTO(DocumentPrint documentPrint);

	public abstract List<DocumentPrintDTO> documentPrintsToDocumentPrintDTOs(List<DocumentPrint> documentPrints);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract DocumentPrint documentPrintDTOToDocumentPrint(DocumentPrintDTO documentPrintDTO);

	public abstract List<DocumentPrint> documentPrintDTOsToDocumentPrints(List<DocumentPrintDTO> documentPrintDTOs);

	public  User userFromId(Long id) {
		if (id == null) {
			return null;
		}
		User employee = new User();
		employee.setId(id);
		return employee;
	}

	public Document documentFromId(Long id) {
		if (id == null) {
			return null;
		}
		Document employee = new Document();
		employee.setId(id);
		return employee;
	}

	public Activity activityFromId(Long id) {
		if (id == null) {
			return null;
		}
		Activity employee = new Activity();
		employee.setId(id);
		return employee;
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
