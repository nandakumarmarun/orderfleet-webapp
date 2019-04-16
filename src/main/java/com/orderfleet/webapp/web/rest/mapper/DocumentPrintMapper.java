package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.DocumentPrint;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;

/**
 * Mapper for the entity DocumentPrint and its DTO DocumentPrintDTO.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
@Mapper(componentModel = "spring", uses = {})
public interface DocumentPrintMapper {

	@Mapping(source = "user.login", target = "userLoginName")
	@Mapping(source = "user.firstName", target = "userFirstName")
	@Mapping(source = "user.lastName", target = "userLastName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "document.pid", target = "documentPid")
	@Mapping(source = "document.name", target = "documentName")
	@Mapping(source = "activity.pid", target = "activityPid")
	@Mapping(source = "activity.name", target = "activityName")
	DocumentPrintDTO documentPrintToDocumentPrintDTO(DocumentPrint documentPrint);

	List<DocumentPrintDTO> documentPrintsToDocumentPrintDTOs(List<DocumentPrint> documentPrints);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	DocumentPrint documentPrintDTOToDocumentPrint(DocumentPrintDTO documentPrintDTO);

	List<DocumentPrint> documentPrintDTOsToDocumentPrints(List<DocumentPrintDTO> documentPrintDTOs);

	default User userFromId(Long id) {
		if (id == null) {
			return null;
		}
		User employee = new User();
		employee.setId(id);
		return employee;
	}

	default Document documentFromId(Long id) {
		if (id == null) {
			return null;
		}
		Document employee = new Document();
		employee.setId(id);
		return employee;
	}

	default Activity activityFromId(Long id) {
		if (id == null) {
			return null;
		}
		Activity employee = new Activity();
		employee.setId(id);
		return employee;
	}
}
