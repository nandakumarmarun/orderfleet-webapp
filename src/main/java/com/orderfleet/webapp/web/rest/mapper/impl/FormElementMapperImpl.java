package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementType;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;
import com.orderfleet.webapp.web.rest.mapper.FormElementMapper;
import com.orderfleet.webapp.web.rest.mapper.FormElementValueMapper;
@Component
public class FormElementMapperImpl extends FormElementMapper{

  @Autowired
    private FormElementValueMapper formElementValueMapper;

    @Override
    public FormElementDTO formElementToFormElementDTO(FormElement formElement) {
        if ( formElement == null ) {
            return null;
        }

        FormElementDTO formElementDTO = new FormElementDTO();

        formElementDTO.setFormElementTypeName( formElementFormElementTypeName( formElement ) );
        formElementDTO.setFormElementTypeId( formElementFormElementTypeId( formElement ) );
        formElementDTO.setPid( formElement.getPid() );
        formElementDTO.setName( formElement.getName() );
        List<FormElementValueDTO> list = formElementValueSetToFormElementValueDTOList( formElement.getFormElementValues() );
        if ( list != null ) {
            formElementDTO.setFormElementValues( list );
        }
        formElementDTO.setActivated( formElement.getActivated() );
        formElementDTO.setDefaultValue( formElement.getDefaultValue() );
        formElementDTO.setLastModifiedDate( formElement.getLastModifiedDate() );
        formElementDTO.setFormLoadFromMobile( formElement.getFormLoadFromMobile() );
        formElementDTO.setFormLoadMobileData( formElement.getFormLoadMobileData() );

        return formElementDTO;
    }

    @Override
    public List<FormElementDTO> formElementsToFormElementDTOs(List<FormElement> formElements) {
        if ( formElements == null ) {
            return null;
        }

        List<FormElementDTO> list = new ArrayList<FormElementDTO>();
        for ( FormElement formElement : formElements ) {
            list.add( formElementToFormElementDTO( formElement ) );
        }

        return list;
    }

    @Override
    public FormElement formElementDTOToFormElement(FormElementDTO formElementDTO) {
        if ( formElementDTO == null ) {
            return null;
        }

        FormElement formElement = new FormElement();

        formElement.setFormElementType( formElementTypeFromId( formElementDTO.getFormElementTypeId() ) );
        formElement.setPid( formElementDTO.getPid() );
        formElement.setName( formElementDTO.getName() );
        Set<FormElementValue> set = formElementValueDTOListToFormElementValueSet( formElementDTO.getFormElementValues() );
        if ( set != null ) {
            formElement.setFormElementValues( set );
        }
        formElement.setActivated( formElementDTO.getActivated() );
        formElement.setDefaultValue( formElementDTO.getDefaultValue() );
        formElement.setFormLoadFromMobile( formElementDTO.getFormLoadFromMobile() );
        formElement.setFormLoadMobileData( formElementDTO.getFormLoadMobileData() );

        return formElement;
    }

    @Override
    public List<FormElement> formElementDTOsToFormElements(List<FormElementDTO> formElementDTOs) {
        if ( formElementDTOs == null ) {
            return null;
        }

        List<FormElement> list = new ArrayList<FormElement>();
        for ( FormElementDTO formElementDTO : formElementDTOs ) {
            list.add( formElementDTOToFormElement( formElementDTO ) );
        }

        return list;
    }

    private String formElementFormElementTypeName(FormElement formElement) {

        if ( formElement == null ) {
            return null;
        }
        FormElementType formElementType = formElement.getFormElementType();
        if ( formElementType == null ) {
            return null;
        }
        String name = formElementType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long formElementFormElementTypeId(FormElement formElement) {

        if ( formElement == null ) {
            return null;
        }
        FormElementType formElementType = formElement.getFormElementType();
        if ( formElementType == null ) {
            return null;
        }
        Long id = formElementType.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<FormElementValueDTO> formElementValueSetToFormElementValueDTOList(Set<FormElementValue> set) {
        if ( set == null ) {
            return null;
        }

        List<FormElementValueDTO> list = new ArrayList<FormElementValueDTO>();
        for ( FormElementValue formElementValue : set ) {
            list.add( formElementValueMapper.formElementValueToFormElementValueDTO( formElementValue ) );
        }

        return list;
    }

    protected Set<FormElementValue> formElementValueDTOListToFormElementValueSet(List<FormElementValueDTO> list) {
        if ( list == null ) {
            return null;
        }

        Set<FormElementValue> set = new HashSet<FormElementValue>();
        for ( FormElementValueDTO formElementValueDTO : list ) {
            set.add( formElementValueMapper.formElementValueDTOToFormElementValue( formElementValueDTO ) );
        }

        return set;
    }

}
