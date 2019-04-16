package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * Mapper for the entity ReceivablePayable and its DTO ReceivablePayableDTO.
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */

@Mapper(componentModel = "spring", uses = {})
public interface ReceivablePayableMapper {

	@Mapping(source = "accountProfile.pid", target = "accountPid")
	@Mapping(source = "accountProfile.name", target = "accountName")
	@Mapping(source = "accountProfile.accountType.name", target = "accountType")
	@Mapping(source = "accountProfile.address", target = "accountAddress")
	ReceivablePayableDTO receivablePayableToReceivablePayableDTO(ReceivablePayable receivablePayable);

	List<ReceivablePayableDTO> receivablePayablesToReceivablePayableDTOs(List<ReceivablePayable> productCategories);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "accountProfile", ignore = true)
	ReceivablePayable receivablePayableDTOToReceivablePayable(ReceivablePayableDTO receivablePayableDTO);

	List<ReceivablePayable> receivablePayableDTOsToProductCategories(List<ReceivablePayableDTO> receivablePayableDTOs);

}
