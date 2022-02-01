package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.mapper.FormMapper;
@Component
public class FormMapperImpl extends FormMapper{

  @Override
    public FormDTO formToFormDTO(Form form) {
        if ( form == null ) {
            return null;
        }

        FormDTO formDTO = new FormDTO();

        formDTO.setActivated( form.getActivated() );
        formDTO.setDescription( form.getDescription() );
        formDTO.setJsCode( form.getJsCode() );
        formDTO.setLastModifiedDate( form.getLastModifiedDate() );
        formDTO.setMultipleRecord( form.getMultipleRecord() );
        formDTO.setName(form.getName());
        formDTO.setPid( form.getPid() );

        return formDTO;
    }
  public FormDTO formToFormDTODescription(Form form) {
      if ( form == null ) {
          return null;
      }

      FormDTO formDTO = new FormDTO();

      formDTO.setActivated( form.getActivated() );
      formDTO.setDescription( form.getDescription() );
      formDTO.setJsCode( form.getJsCode() );
      formDTO.setLastModifiedDate( form.getLastModifiedDate() );
      formDTO.setMultipleRecord( form.getMultipleRecord() );
      formDTO.setName(form.getDescription() != null && !form.getDescription().equalsIgnoreCase("common")
				? form.getDescription()
				: form.getName());
      formDTO.setPid( form.getPid() );

      return formDTO;
  }

    @Override
    public List<FormDTO> formsToFormDTOs(List<Form> forms) {
        if ( forms == null ) {
            return null;
        }

        List<FormDTO> list = new ArrayList<FormDTO>();
        if(getCompanyCofig()) {
        for ( Form form : forms ) {
            list.add( formToFormDTODescription( form ) );
        }
        }
        else
        {for ( Form form : forms ) {
            list.add( formToFormDTO( form ) );
        }
        	
        }
        return list;
    }

    @Override
    public Form formDTOToForm(FormDTO formDTO) {
        if ( formDTO == null ) {
            return null;
        }

        Form form = new Form();

        if ( formDTO.getActivated() != null ) {
            form.setActivated( formDTO.getActivated() );
        }
        form.setDescription( formDTO.getDescription() );
        form.setJsCode( formDTO.getJsCode() );
        form.setMultipleRecord( formDTO.getMultipleRecord() );
        form.setName( formDTO.getName() );
        form.setPid( formDTO.getPid() );

        return form;
    }

    @Override
    public List<Form> formDTOsToForms(List<FormDTO> formDTOs) {
        if ( formDTOs == null ) {
            return null;
        }

        List<Form> list = new ArrayList<Form>();
        for ( FormDTO formDTO : formDTOs ) {
            list.add( formDTOToForm( formDTO ) );
        }

        return list;
    }
    private String formName(Form form) {
        if(form.getDescription()!=null && getCompanyCofig() && !form.getDescription().equals("common")) {
        return form.getDescription();
        }
       
    return form.getName();
    }
}
