package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.orderfleet.webapp.domain.IncomeExpenseHead;

import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;

/**
 *Mapper for IncomeExpenseHead
 *
 * @author fahad
 * @since Feb 15, 2017
 */
@Mapper(componentModel = "spring", uses = {})
public interface IncomeExpenseHeadMapper {

	IncomeExpenseHeadDTO incomeExpenseHeadToIncomeExpenseHeadDTO(IncomeExpenseHead incomeExpenseHead);

	List<IncomeExpenseHeadDTO> incomeExpenseHeadsToIncomeExpenseHeadDTOs(List<IncomeExpenseHead> incomeExpenseHeads);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	IncomeExpenseHead incomeExpenseHeadDTOToIncomeExpenseHead(IncomeExpenseHeadDTO incomeExpenseHeadDTO);

	List<IncomeExpenseHead> incomeExpenseHeadDTOsToIncomeExpenseHeads(List<IncomeExpenseHeadDTO> incomeExpenseHeadDTOs);
}
