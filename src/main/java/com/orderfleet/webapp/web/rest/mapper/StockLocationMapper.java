package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Mapper for the entity StockLocation and its DTO StockLocationDTO.
 * 
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface StockLocationMapper {

	StockLocationDTO stockLocationToStockLocationDTO(StockLocation stockLocation);

	List<StockLocationDTO> stockLocationsToStockLocationDTOs(List<StockLocation> stockLocations);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	StockLocation stockLocationDTOToStockLocation(StockLocationDTO stockLocationDTO);

	List<StockLocation> stockLocationDTOsToStockLocations(List<StockLocationDTO> stockLocationDTOs);

}
