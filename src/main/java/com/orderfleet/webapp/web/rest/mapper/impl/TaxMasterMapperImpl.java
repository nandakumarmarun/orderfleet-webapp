package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;
@Component
public class TaxMasterMapperImpl extends TaxMasterMapper {

  @Override
    public TaxMasterDTO taxMasterToTaxMasterDTO(TaxMaster taxMaster) {
        if ( taxMaster == null ) {
            return null;
        }

        TaxMasterDTO taxMasterDTO = new TaxMasterDTO();

        taxMasterDTO.setPid( taxMaster.getPid() );
        taxMasterDTO.setVatName( taxMaster.getVatName() );
        taxMasterDTO.setDescription( taxMaster.getDescription() );
        taxMasterDTO.setVatPercentage( taxMaster.getVatPercentage() );
        taxMasterDTO.setVatClass( taxMaster.getVatClass() );
        taxMasterDTO.setTaxId( taxMaster.getTaxId() );
        taxMasterDTO.setTaxCode( taxMaster.getTaxCode() );

        return taxMasterDTO;
    }
  public TaxMasterDTO taxMasterToTaxMasterDTODescription(TaxMaster taxMaster) {
      if ( taxMaster == null ) {
          return null;
      }

      TaxMasterDTO taxMasterDTO = new TaxMasterDTO();

      taxMasterDTO.setPid( taxMaster.getPid() );
      taxMasterDTO.setVatName(taxMaster.getDescription() != null && !taxMaster.getDescription().equalsIgnoreCase("common")
				? taxMaster.getDescription()
				: taxMaster.getVatName());
      taxMasterDTO.setDescription( taxMaster.getDescription() );
      taxMasterDTO.setVatPercentage( taxMaster.getVatPercentage() );
      taxMasterDTO.setVatClass( taxMaster.getVatClass() );
      taxMasterDTO.setTaxId( taxMaster.getTaxId() );
      taxMasterDTO.setTaxCode( taxMaster.getTaxCode() );

      return taxMasterDTO;
  }
    @Override
    public List<TaxMasterDTO> taxMastersToTaxMasterDTOs(List<TaxMaster> taxMasters) {
        if ( taxMasters == null ) {
            return null;
        }

        List<TaxMasterDTO> list = new ArrayList<TaxMasterDTO>();
        if(getCompanyCofig())
        {
        for ( TaxMaster taxMaster : taxMasters ) {
            list.add( taxMasterToTaxMasterDTODescription( taxMaster ) );
        }}
        else
        {
        	for ( TaxMaster taxMaster : taxMasters ) {
                list.add( taxMasterToTaxMasterDTO( taxMaster ) );
            }
        }

        return list;
    }

    @Override
    public TaxMaster taxMasterDTOToTaxMaster(TaxMasterDTO taxMasterDTO) {
        if ( taxMasterDTO == null ) {
            return null;
        }

        TaxMaster taxMaster = new TaxMaster();

        taxMaster.setPid( taxMasterDTO.getPid() );
        taxMaster.setVatName( taxMasterDTO.getVatName() );
        taxMaster.setDescription( taxMasterDTO.getDescription() );
        taxMaster.setVatPercentage( taxMasterDTO.getVatPercentage() );
        taxMaster.setVatClass( taxMasterDTO.getVatClass() );
        taxMaster.setTaxId( taxMasterDTO.getTaxId() );
        taxMaster.setTaxCode( taxMasterDTO.getTaxCode() );

        return taxMaster;
    }

    @Override
    public List<TaxMaster> taxMasterDTOsToTaxMasters(List<TaxMasterDTO> taxMasterDTOs) {
        if ( taxMasterDTOs == null ) {
            return null;
        }

        List<TaxMaster> list = new ArrayList<TaxMaster>();
        for ( TaxMasterDTO taxMasterDTO : taxMasterDTOs ) {
            list.add( taxMasterDTOToTaxMaster( taxMasterDTO ) );
        }

        return list;
    }
    private String taxMasterName(TaxMaster taxMaster)  {
        if(taxMaster.getDescription()!=null && getCompanyCofig() && !taxMaster.getDescription().equals("common")) {
        return taxMaster.getDescription();
        }
       
    return taxMaster.getVatName();
    }
}
