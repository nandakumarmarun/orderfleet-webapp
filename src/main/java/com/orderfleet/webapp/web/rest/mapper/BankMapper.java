package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.web.rest.dto.BankDTO;

/**
 * Mapper for the entity Bank and its DTO BankDTO.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface BankMapper {

	BankDTO bankToBankDTO(Bank bank);

	List<BankDTO> banksToBankDTOs(List<Bank> banks);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Bank bankDTOToBank(BankDTO bankDTO);

	List<Bank> bankDTOsToBanks(List<BankDTO> bankDTOs);

}
