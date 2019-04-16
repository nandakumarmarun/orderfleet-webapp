package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;

/**
 * Mapper for the entity TaxMaster and its DTO TaxMasterDTO.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface TaxMasterMapper {

	TaxMasterDTO taxMasterToTaxMasterDTO(TaxMaster taxMaster);

	List<TaxMasterDTO> taxMastersToTaxMasterDTOs(List<TaxMaster> taxMasters);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	TaxMaster taxMasterDTOToTaxMaster(TaxMasterDTO taxMasterDTO);

	List<TaxMaster> taxMasterDTOsToTaxMasters(List<TaxMasterDTO> taxMasterDTOs);

}
