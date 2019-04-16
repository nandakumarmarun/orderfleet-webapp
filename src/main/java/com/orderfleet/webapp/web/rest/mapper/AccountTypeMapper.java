package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;

/**
 * Mapper for the entity AccountType and its DTO AccountTypeDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface AccountTypeMapper {

	AccountTypeDTO accountTypeToAccountTypeDTO(AccountType accountType);

	List<AccountTypeDTO> accountTypesToAccountTypeDTOs(List<AccountType> accountTypes);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	AccountType accountTypeDTOToAccountType(AccountTypeDTO accountTypeDTO);

	List<AccountType> accountTypeDTOsToAccountTypes(List<AccountTypeDTO> accountTypeDTOs);

}
