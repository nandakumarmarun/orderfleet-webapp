package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;

/**
 * Mapper for the entity StaticFormJSCode and its DTO StaticFormJSCodeDTO.
 * 
 * @author Sarath
 * @since Aug 3, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class StaticFormJSCodeMapper {

	@Mapping(source = "document.pid", target = "documentPid")
	@Mapping(source = "document.name", target = "documentName")
	public abstract StaticFormJSCodeDTO staticFormJSCodeToStaticFormJSCodeDTO(StaticFormJSCode staticFormJSCode);

	public abstract List<StaticFormJSCodeDTO> staticFormJSCodesToStaticFormJSCodeDTOs(
			List<StaticFormJSCode> staticFormJSCodes);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "document", ignore = true)
	public abstract StaticFormJSCode staticFormJSCodeDTOToStaticFormJSCode(StaticFormJSCodeDTO staticFormJSCodeDTO);

	public abstract List<StaticFormJSCode> staticFormJSCodeDTOsToStaticFormJSCodes(
			List<StaticFormJSCodeDTO> staticFormJSCodeDTOs);

}
