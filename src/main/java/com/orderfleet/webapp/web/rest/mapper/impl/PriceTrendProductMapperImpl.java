package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceTrendProductMapper;
@Component
public class PriceTrendProductMapperImpl extends PriceTrendProductMapper {

  @Override
    public PriceTrendProductDTO priceTrendProductToPriceTrendProductDTO(PriceTrendProduct priceTrendProduct) {
        if ( priceTrendProduct == null ) {
            return null;
        }

        PriceTrendProductDTO priceTrendProductDTO = new PriceTrendProductDTO();

        priceTrendProductDTO.setActivated( priceTrendProduct.getActivated() );
        priceTrendProductDTO.setPid( priceTrendProduct.getPid() );
        priceTrendProductDTO.setName( priceTrendProductName(priceTrendProduct));
        priceTrendProductDTO.setAlias( priceTrendProduct.getAlias() );
        priceTrendProductDTO.setDescription( priceTrendProduct.getDescription() );
        priceTrendProductDTO.setLastModifiedDate( priceTrendProduct.getLastModifiedDate() );

        return priceTrendProductDTO;
    }

    @Override
    public List<PriceTrendProductDTO> priceTrendProductsToPriceTrendProductDTOs(List<PriceTrendProduct> priceTrendProducts) {
        if ( priceTrendProducts == null ) {
            return null;
        }

        List<PriceTrendProductDTO> list = new ArrayList<PriceTrendProductDTO>();
        for ( PriceTrendProduct priceTrendProduct : priceTrendProducts ) {
            list.add( priceTrendProductToPriceTrendProductDTO( priceTrendProduct ) );
        }

        return list;
    }

    @Override
    public PriceTrendProduct priceTrendProductDTOToPriceTrendProduct(PriceTrendProductDTO priceTrendProductDTO) {
        if ( priceTrendProductDTO == null ) {
            return null;
        }

        PriceTrendProduct priceTrendProduct = new PriceTrendProduct();

        priceTrendProduct.setActivated( priceTrendProductDTO.getActivated() );
        priceTrendProduct.setPid( priceTrendProductDTO.getPid() );
        priceTrendProduct.setName( priceTrendProductDTO.getName() );
        priceTrendProduct.setAlias( priceTrendProductDTO.getAlias() );
        priceTrendProduct.setDescription( priceTrendProductDTO.getDescription() );

        return priceTrendProduct;
    }

    @Override
    public List<PriceTrendProduct> priceTrendProductDTOsToPriceTrendProducts(List<PriceTrendProductDTO> priceTrendProductDTOs) {
        if ( priceTrendProductDTOs == null ) {
            return null;
        }

        List<PriceTrendProduct> list = new ArrayList<PriceTrendProduct>();
        for ( PriceTrendProductDTO priceTrendProductDTO : priceTrendProductDTOs ) {
            list.add( priceTrendProductDTOToPriceTrendProduct( priceTrendProductDTO ) );
        }

        return list;
    }
    private String priceTrendProductName(PriceTrendProduct priceTrendProduct) {
        if(priceTrendProduct.getDescription()!=null && getCompanyCofig() && !priceTrendProduct.getDescription().equals("common")) {
        return priceTrendProduct.getDescription();
        }
       
    return priceTrendProduct.getName();
    }
}
