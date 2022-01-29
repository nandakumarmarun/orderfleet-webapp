package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;
import com.orderfleet.webapp.web.rest.mapper.StaticFormJSCodeMapper;
@Component
public class StaticFormJSCodeMapperImpl extends StaticFormJSCodeMapper{

 @Override
    public StaticFormJSCodeDTO staticFormJSCodeToStaticFormJSCodeDTO(StaticFormJSCode staticFormJSCode) {
        if ( staticFormJSCode == null ) {
            return null;
        }

        StaticFormJSCodeDTO staticFormJSCodeDTO = new StaticFormJSCodeDTO();

        staticFormJSCodeDTO.setDocumentPid( staticFormJSCodeDocumentPid( staticFormJSCode ) );
        staticFormJSCodeDTO.setDocumentName( staticFormJSCodeDocumentName( staticFormJSCode ) );
        staticFormJSCodeDTO.setId( staticFormJSCode.getId() );
        staticFormJSCodeDTO.setJsCode( staticFormJSCode.getJsCode() );
        staticFormJSCodeDTO.setJsCodeName( staticFormJSCode.getJsCodeName() );

        return staticFormJSCodeDTO;
    }

    @Override
    public List<StaticFormJSCodeDTO> staticFormJSCodesToStaticFormJSCodeDTOs(List<StaticFormJSCode> staticFormJSCodes) {
        if ( staticFormJSCodes == null ) {
            return null;
        }

        List<StaticFormJSCodeDTO> list = new ArrayList<StaticFormJSCodeDTO>();
        for ( StaticFormJSCode staticFormJSCode : staticFormJSCodes ) {
            list.add( staticFormJSCodeToStaticFormJSCodeDTO( staticFormJSCode ) );
        }

        return list;
    }

    @Override
    public StaticFormJSCode staticFormJSCodeDTOToStaticFormJSCode(StaticFormJSCodeDTO staticFormJSCodeDTO) {
        if ( staticFormJSCodeDTO == null ) {
            return null;
        }

        StaticFormJSCode staticFormJSCode = new StaticFormJSCode();

        staticFormJSCode.setId( staticFormJSCodeDTO.getId() );
        staticFormJSCode.setJsCode( staticFormJSCodeDTO.getJsCode() );
        staticFormJSCode.setJsCodeName( staticFormJSCodeDTO.getJsCodeName() );

        return staticFormJSCode;
    }

    @Override
    public List<StaticFormJSCode> staticFormJSCodeDTOsToStaticFormJSCodes(List<StaticFormJSCodeDTO> staticFormJSCodeDTOs) {
        if ( staticFormJSCodeDTOs == null ) {
            return null;
        }

        List<StaticFormJSCode> list = new ArrayList<StaticFormJSCode>();
        for ( StaticFormJSCodeDTO staticFormJSCodeDTO : staticFormJSCodeDTOs ) {
            list.add( staticFormJSCodeDTOToStaticFormJSCode( staticFormJSCodeDTO ) );
        }

        return list;
    }

    private String staticFormJSCodeDocumentPid(StaticFormJSCode staticFormJSCode) {

        if ( staticFormJSCode == null ) {
            return null;
        }
        Document document = staticFormJSCode.getDocument();
        if ( document == null ) {
            return null;
        }
        String pid = document.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String staticFormJSCodeDocumentName(StaticFormJSCode staticFormJSCode) {

        if ( staticFormJSCode == null ) {
            return null;
        }
        Document document = staticFormJSCode.getDocument();
        if ( document == null ) {
            return null;
        }
        String name = document.getName();
        if ( name == null ) {
            return null;
        }
        if(document.getDescription()!=null && getCompanyCofig() && !document.getDescription().equals("common")) {
	        return document.getDescription();
	        }
        return name;
    }

}
