package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;

/**
 * Mapper for the entity AccountGroup and its DTO AccountGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface AccountGroupMapper {

	AccountGroupDTO accountGroupToAccountGroupDTO(AccountGroup accountGroup);

	List<AccountGroupDTO> accountGroupsToAccountGroupDTOs(List<AccountGroup> productCategories);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	AccountGroup accountGroupDTOToAccountGroup(AccountGroupDTO accountGroupDTO);

	List<AccountGroup> accountGroupDTOsToProductCategories(List<AccountGroupDTO> accountGroupDTOs);

}
