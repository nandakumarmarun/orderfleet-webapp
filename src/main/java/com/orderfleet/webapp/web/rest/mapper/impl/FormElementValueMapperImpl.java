package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;
import com.orderfleet.webapp.web.rest.mapper.FormElementValueMapper;
@Component
public class FormElementValueMapperImpl extends FormElementValueMapper{

@Override
    public FormElementValueDTO formElementValueToFormElementValueDTO(FormElementValue formElementValue) {
        if ( formElementValue == null ) {
            return null;
        }

        FormElementValueDTO formElementValueDTO = new FormElementValueDTO();

        if ( formElementValue.getId() != null ) {
            formElementValueDTO.setId( String.valueOf( formElementValue.getId() ) );
        }
        formElementValueDTO.setName( formElementValue.getName() );

        return formElementValueDTO;
    }

    @Override
    public List<FormElementValueDTO> formElementValuesToFormElementValueDTOs(List<FormElementValue> formElementValues) {
        if ( formElementValues == null ) {
            return null;
        }

        List<FormElementValueDTO> list = new ArrayList<FormElementValueDTO>();
        for ( FormElementValue formElementValue : formElementValues ) {
            list.add( formElementValueToFormElementValueDTO( formElementValue ) );
        }

        return list;
    }

    @Override
    public FormElementValue formElementValueDTOToFormElementValue(FormElementValueDTO formElementValueDTO) {
        if ( formElementValueDTO == null ) {
            return null;
        }

        FormElementValue formElementValue = new FormElementValue();

        if ( formElementValueDTO.getId() != null ) {
            formElementValue.setId( Long.parseLong( formElementValueDTO.getId() ) );
        }
        formElementValue.setName( formElementValueDTO.getName() );

        return formElementValue;
    }

    @Override
    public Set<FormElementValue> formElementValueDTOsToFormElementValues(Set<FormElementValueDTO> formElementValueDTOs) {
        if ( formElementValueDTOs == null ) {
            return null;
        }

        Set<FormElementValue> set = new HashSet<FormElementValue>();
        for ( FormElementValueDTO formElementValueDTO : formElementValueDTOs ) {
            set.add( formElementValueDTOToFormElementValue( formElementValueDTO ) );
        }

        return set;
    }

	
}
