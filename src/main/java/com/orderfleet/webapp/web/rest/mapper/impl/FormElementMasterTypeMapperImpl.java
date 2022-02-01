package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.FormElementMasterType;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.FormElementMasterTypeMapper;
@Component
public class FormElementMasterTypeMapperImpl extends FormElementMasterTypeMapper {



    @Override
    public FormElementMasterTypeDTO formElementMasterToFormElementMasterTypeDTO(FormElementMasterType formElementMaster) {
        if ( formElementMaster == null ) {
            return null;
        }

        FormElementMasterTypeDTO formElementMasterTypeDTO = new FormElementMasterTypeDTO();

        formElementMasterTypeDTO.setActivated( formElementMaster.getActivated() );
        formElementMasterTypeDTO.setPid( formElementMaster.getPid() );
        formElementMasterTypeDTO.setName( formElementMaster.getName());
        formElementMasterTypeDTO.setAlias( formElementMaster.getAlias() );
        formElementMasterTypeDTO.setDescription( formElementMaster.getDescription() );

        return formElementMasterTypeDTO;
    }
    public FormElementMasterTypeDTO formElementMasterToFormElementMasterTypeDTODescription(FormElementMasterType formElementMaster) {
        if ( formElementMaster == null ) {
            return null;
        }

        FormElementMasterTypeDTO formElementMasterTypeDTO = new FormElementMasterTypeDTO();

        formElementMasterTypeDTO.setActivated( formElementMaster.getActivated() );
        formElementMasterTypeDTO.setPid( formElementMaster.getPid() );
        formElementMasterTypeDTO.setName(formElementMaster.getDescription() != null && !formElementMaster.getDescription().equalsIgnoreCase("common")
				? formElementMaster.getDescription()
				: formElementMaster.getName());
        formElementMasterTypeDTO.setAlias( formElementMaster.getAlias() );
        formElementMasterTypeDTO.setDescription( formElementMaster.getDescription() );

        return formElementMasterTypeDTO;
    }
    @Override
    public List<FormElementMasterTypeDTO> formElementMastersToFormElementMasterTypeDTOs(List<FormElementMasterType> formElementMasters) {
        if ( formElementMasters == null ) {
            return null;
        }

        List<FormElementMasterTypeDTO> list = new ArrayList<FormElementMasterTypeDTO>();
      if(getCompanyCofig()) {
        for ( FormElementMasterType formElementMasterType : formElementMasters ) {
            list.add( formElementMasterToFormElementMasterTypeDTODescription( formElementMasterType ) );
        }
      }
      else
      { for ( FormElementMasterType formElementMasterType : formElementMasters ) {
          list.add( formElementMasterToFormElementMasterTypeDTO( formElementMasterType ) );
      }
    	  
      }
        return list;
    }

    @Override
    public FormElementMasterType formElementMasterDTOToFormElementMasterType(FormElementMasterTypeDTO formElementMasterDTO) {
        if ( formElementMasterDTO == null ) {
            return null;
        }

        FormElementMasterType formElementMasterType = new FormElementMasterType();

        formElementMasterType.setActivated( formElementMasterDTO.getActivated() );
        formElementMasterType.setPid( formElementMasterDTO.getPid() );
        formElementMasterType.setName( formElementMasterDTO.getName() );
        formElementMasterType.setAlias( formElementMasterDTO.getAlias() );
        formElementMasterType.setDescription( formElementMasterDTO.getDescription() );

        return formElementMasterType;
    }

    @Override
    public List<FormElementMasterType> formElementMasterDTOsToFormElementMasterTypes(List<FormElementMasterTypeDTO> formElementMasterDTOs) {
        if ( formElementMasterDTOs == null ) {
            return null;
        }

        List<FormElementMasterType> list = new ArrayList<FormElementMasterType>();
        for ( FormElementMasterTypeDTO formElementMasterTypeDTO : formElementMasterDTOs ) {
            list.add( formElementMasterDTOToFormElementMasterType( formElementMasterTypeDTO ) );
        }

        return list;
    }
    private String formElementMasterName(FormElementMasterType formElementMaster ) {
        if(formElementMaster.getDescription()!=null && getCompanyCofig() && !formElementMaster.getDescription().equals("common")) {
        return formElementMaster.getDescription();
        }
       
    return formElementMaster.getName();
    }
}
