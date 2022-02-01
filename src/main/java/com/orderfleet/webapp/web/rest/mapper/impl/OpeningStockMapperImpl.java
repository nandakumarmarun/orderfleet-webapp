package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.mapper.OpeningStockMapper;
@Component
public class OpeningStockMapperImpl extends OpeningStockMapper{
	


    @Override
    public OpeningStockDTO openingStockToOpeningStockDTO(OpeningStock openingStock) {
        if ( openingStock == null ) {
            return null;
        }

        OpeningStockDTO openingStockDTO = new OpeningStockDTO();

        openingStockDTO.setProductProfilePid( openingStockProductProfilePid( openingStock ) );
        openingStockDTO.setStockLocationName( openingStockStockLocationName( openingStock ) );
        openingStockDTO.setStockLocationPid( openingStockStockLocationPid( openingStock ) );
        openingStockDTO.setProductProfileName( openingStockProductProfileName( openingStock ) );
        openingStockDTO.setActivated( openingStock.getActivated() );
        openingStockDTO.setReservedStock( openingStock.getReservedStock() );
        openingStockDTO.setPid( openingStock.getPid() );
        openingStockDTO.setBatchNumber( openingStock.getBatchNumber() );
        openingStockDTO.setQuantity( openingStock.getQuantity() );
        openingStockDTO.setCreatedDate( openingStock.getCreatedDate() );
        openingStockDTO.setOpeningStockDate( openingStock.getOpeningStockDate() );
        openingStockDTO.setLastModifiedDate( openingStock.getLastModifiedDate() );

        return openingStockDTO;
    }
    public OpeningStockDTO openingStockToOpeningStockDTODescription(OpeningStock openingStock) {
        if ( openingStock == null ) {
            return null;
        }

        OpeningStockDTO openingStockDTO = new OpeningStockDTO();

        openingStockDTO.setProductProfilePid( openingStockProductProfilePid( openingStock ) );
        openingStockDTO.setStockLocationName( openingStockStockLocationDescription( openingStock ) );
        openingStockDTO.setStockLocationPid( openingStockStockLocationPid( openingStock ) );
        openingStockDTO.setProductProfileName( openingStockProductProfileDescription( openingStock ) );
        openingStockDTO.setActivated( openingStock.getActivated() );
        openingStockDTO.setReservedStock( openingStock.getReservedStock() );
        openingStockDTO.setPid( openingStock.getPid() );
        openingStockDTO.setBatchNumber( openingStock.getBatchNumber() );
        openingStockDTO.setQuantity( openingStock.getQuantity() );
        openingStockDTO.setCreatedDate( openingStock.getCreatedDate() );
        openingStockDTO.setOpeningStockDate( openingStock.getOpeningStockDate() );
        openingStockDTO.setLastModifiedDate( openingStock.getLastModifiedDate() );

        return openingStockDTO;
    }

    @Override
    public List<OpeningStockDTO> openingStocksToOpeningStockDTOs(List<OpeningStock> openingStocks) {
        if ( openingStocks == null ) {
            return null;
        }

        List<OpeningStockDTO> list = new ArrayList<OpeningStockDTO>();
        if(getCompanyCofig())
        {
        for ( OpeningStock openingStock : openingStocks ) {
            list.add( openingStockToOpeningStockDTODescription( openingStock ) );
        }
        }
        else
        {
        	 for ( OpeningStock openingStock : openingStocks ) {
                 list.add( openingStockToOpeningStockDTO( openingStock ) );
             }
        }
        return list;
    }

    @Override
    public OpeningStock openingStockDTOToOpeningStock(OpeningStockDTO openingStockDTO) {
        if ( openingStockDTO == null ) {
            return null;
        }

        OpeningStock openingStock = new OpeningStock();

        openingStock.setStockLocation( stockLocationFromPid( openingStockDTO.getStockLocationPid() ) );
        openingStock.setProductProfile( productProfileFromPid( openingStockDTO.getProductProfilePid() ) );
        openingStock.setActivated( openingStockDTO.getActivated() );
        openingStock.setPid( openingStockDTO.getPid() );
        openingStock.setBatchNumber( openingStockDTO.getBatchNumber() );
        openingStock.setQuantity( openingStockDTO.getQuantity() );
        openingStock.setCreatedDate( openingStockDTO.getCreatedDate() );
        openingStock.setOpeningStockDate( openingStockDTO.getOpeningStockDate() );
        openingStock.setReservedStock( openingStockDTO.getReservedStock() );

        return openingStock;
    }

    @Override
    public List<OpeningStock> openingStockDTOsToOpeningStocks(List<OpeningStockDTO> openingStockDTOs) {
        if ( openingStockDTOs == null ) {
            return null;
        }

        List<OpeningStock> list = new ArrayList<OpeningStock>();
        for ( OpeningStockDTO openingStockDTO : openingStockDTOs ) {
            list.add( openingStockDTOToOpeningStock( openingStockDTO ) );
        }

        return list;
    }

    private String openingStockProductProfilePid(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        ProductProfile productProfile = openingStock.getProductProfile();
        if ( productProfile == null ) {
            return null;
        }
        String pid = productProfile.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String openingStockStockLocationName(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        StockLocation stockLocation = openingStock.getStockLocation();
        if ( stockLocation == null ) {
            return null;
        }
        String name = stockLocation.getName();
        if ( name == null ) {
            return null;
        }
//        if(stockLocation.getDescription()!=null && getCompanyCofig() && !stockLocation.getDescription().equals("common")) {
//	        return stockLocation.getDescription();
//	        }
	       
        return name;
    }
    private String openingStockStockLocationDescription(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        StockLocation stockLocation = openingStock.getStockLocation();
        if ( stockLocation == null ) {
            return null;
        }
        String name = stockLocation.getName();
        if ( name == null ) {
            return null;
        }
        if(stockLocation.getDescription()!=null && !stockLocation.getDescription().equals("common")) {
	        return stockLocation.getDescription();
	        }
	       
        return name;
    }
    private String openingStockStockLocationPid(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        StockLocation stockLocation = openingStock.getStockLocation();
        if ( stockLocation == null ) {
            return null;
        }
        String pid = stockLocation.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String openingStockProductProfileName(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        ProductProfile productProfile = openingStock.getProductProfile();
        if ( productProfile == null ) {
            return null;
        }
        String name = productProfile.getName();
        if ( name == null ) {
            return null;
        }
//        if(productProfile.getProductDescription()!=null && getCompanyCofig() && !productProfile.getProductDescription().equals("common")) {
//	        return productProfile.getProductDescription();
//	        }
	       
        return name;
    }
    private String openingStockProductProfileDescription(OpeningStock openingStock) {

        if ( openingStock == null ) {
            return null;
        }
        ProductProfile productProfile = openingStock.getProductProfile();
        if ( productProfile == null ) {
            return null;
        }
        String name = productProfile.getName();
        if ( name == null ) {
            return null;
        }
        if(productProfile.getProductDescription()!=null && !productProfile.getProductDescription().equals("common")) {
	        return productProfile.getProductDescription();
	        }
	       
        return name;
    }

}
