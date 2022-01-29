package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.StockLocationMapper;
@Component
public class StockLocationMapperImpl extends StockLocationMapper{
	
    @Override
    public StockLocationDTO stockLocationToStockLocationDTO(StockLocation stockLocation) {
        if ( stockLocation == null ) {
            return null;
        }

        StockLocationDTO stockLocationDTO = new StockLocationDTO();

        stockLocationDTO.setActivated( stockLocation.getActivated() );
        stockLocationDTO.setPid( stockLocation.getPid() );
        stockLocationDTO.setName( stockLocationName(stockLocation));
        stockLocationDTO.setAlias( stockLocation.getAlias() );
        stockLocationDTO.setDescription( stockLocation.getDescription() );
        stockLocationDTO.setStockLocationType( stockLocation.getStockLocationType() );
        stockLocationDTO.setLastModifiedDate( stockLocation.getLastModifiedDate() );

        return stockLocationDTO;
    }

    @Override
    public List<StockLocationDTO> stockLocationsToStockLocationDTOs(List<StockLocation> stockLocations) {
        if ( stockLocations == null ) {
            return null;
        }

        List<StockLocationDTO> list = new ArrayList<StockLocationDTO>();
        for ( StockLocation stockLocation : stockLocations ) {
            list.add( stockLocationToStockLocationDTO( stockLocation ) );
        }

        return list;
    }

    @Override
    public StockLocation stockLocationDTOToStockLocation(StockLocationDTO stockLocationDTO) {
        if ( stockLocationDTO == null ) {
            return null;
        }

        StockLocation stockLocation = new StockLocation();

        stockLocation.setActivated( stockLocationDTO.getActivated() );
        stockLocation.setPid( stockLocationDTO.getPid() );
        stockLocation.setName( stockLocationDTO.getName() );
        stockLocation.setAlias( stockLocationDTO.getAlias() );
        stockLocation.setDescription( stockLocationDTO.getDescription() );
        stockLocation.setStockLocationType( stockLocationDTO.getStockLocationType() );

        return stockLocation;
    }

    @Override
    public List<StockLocation> stockLocationDTOsToStockLocations(List<StockLocationDTO> stockLocationDTOs) {
        if ( stockLocationDTOs == null ) {
            return null;
        }

        List<StockLocation> list = new ArrayList<StockLocation>();
        for ( StockLocationDTO stockLocationDTO : stockLocationDTOs ) {
            list.add( stockLocationDTOToStockLocation( stockLocationDTO ) );
        }

        return list;
    }
    private String stockLocationName(StockLocation stockLocation) {
        if(stockLocation.getDescription()!=null && getCompanyCofig() && !stockLocation.getDescription().equals("common")) {
        return stockLocation.getDescription();
        }
       
    return stockLocation.getName();
    }
}
