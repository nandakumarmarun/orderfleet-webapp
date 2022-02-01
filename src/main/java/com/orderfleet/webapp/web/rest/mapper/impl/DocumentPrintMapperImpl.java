package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPrint;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentPrintMapper;
@Component
public class DocumentPrintMapperImpl extends DocumentPrintMapper{
	
	 @Override
	    public DocumentPrintDTO documentPrintToDocumentPrintDTO(DocumentPrint documentPrint) {
	        if ( documentPrint == null ) {
	            return null;
	        }

	        DocumentPrintDTO documentPrintDTO = new DocumentPrintDTO();

	        documentPrintDTO.setDocumentPid( documentPrintDocumentPid( documentPrint ) );
	        documentPrintDTO.setUserLastName( documentPrintUserLastName( documentPrint ) );
	        documentPrintDTO.setActivityName( documentPrintActivityName( documentPrint ) );
	        documentPrintDTO.setUserLoginName( documentPrintUserLogin( documentPrint ) );
	        documentPrintDTO.setUserPid( documentPrintUserPid( documentPrint ) );
	        documentPrintDTO.setUserFirstName( documentPrintUserFirstName( documentPrint ) );
	        documentPrintDTO.setDocumentName( documentPrintDocumentName( documentPrint ) );
	        documentPrintDTO.setActivityPid( documentPrintActivityPid( documentPrint ) );
	        documentPrintDTO.setPid( documentPrint.getPid() );
	        documentPrintDTO.setPrintStatus( documentPrint.isPrintStatus() );

	        return documentPrintDTO;
	    }
	 public DocumentPrintDTO documentPrintToDocumentPrintDTODescription(DocumentPrint documentPrint) {
	        if ( documentPrint == null ) {
	            return null;
	        }

	        DocumentPrintDTO documentPrintDTO = new DocumentPrintDTO();

	        documentPrintDTO.setDocumentPid( documentPrintDocumentPid( documentPrint ) );
	        documentPrintDTO.setUserLastName( documentPrintUserLastName( documentPrint ) );
	        documentPrintDTO.setActivityName( documentPrintActivityDescription( documentPrint ) );
	        documentPrintDTO.setUserLoginName( documentPrintUserLogin( documentPrint ) );
	        documentPrintDTO.setUserPid( documentPrintUserPid( documentPrint ) );
	        documentPrintDTO.setUserFirstName( documentPrintUserFirstName( documentPrint ) );
	        documentPrintDTO.setDocumentName( documentPrintDocumentDescription( documentPrint ) );
	        documentPrintDTO.setActivityPid( documentPrintActivityPid( documentPrint ) );
	        documentPrintDTO.setPid( documentPrint.getPid() );
	        documentPrintDTO.setPrintStatus( documentPrint.isPrintStatus() );

	        return documentPrintDTO;
	    }

	    @Override
	    public List<DocumentPrintDTO> documentPrintsToDocumentPrintDTOs(List<DocumentPrint> documentPrints) {
	        if ( documentPrints == null ) {
	            return null;
	        }

	        List<DocumentPrintDTO> list = new ArrayList<DocumentPrintDTO>();
	        if(getCompanyCofig())
	        {
	        for ( DocumentPrint documentPrint : documentPrints ) {
	            list.add( documentPrintToDocumentPrintDTODescription( documentPrint ) );
	        }
	        }else
	        {
	        	for ( DocumentPrint documentPrint : documentPrints ) {
		            list.add( documentPrintToDocumentPrintDTO( documentPrint ) );
		        }
	        }
	        return list;
	    }

	    @Override
	    public DocumentPrint documentPrintDTOToDocumentPrint(DocumentPrintDTO documentPrintDTO) {
	        if ( documentPrintDTO == null ) {
	            return null;
	        }

	        DocumentPrint documentPrint = new DocumentPrint();

	        documentPrint.setPid( documentPrintDTO.getPid() );
	        documentPrint.setPrintStatus( documentPrintDTO.isPrintStatus() );

	        return documentPrint;
	    }

	    @Override
	    public List<DocumentPrint> documentPrintDTOsToDocumentPrints(List<DocumentPrintDTO> documentPrintDTOs) {
	        if ( documentPrintDTOs == null ) {
	            return null;
	        }

	        List<DocumentPrint> list = new ArrayList<DocumentPrint>();
	        for ( DocumentPrintDTO documentPrintDTO : documentPrintDTOs ) {
	            list.add( documentPrintDTOToDocumentPrint( documentPrintDTO ) );
	        }

	        return list;
	    }

	    private String documentPrintDocumentPid(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Document document = documentPrint.getDocument();
	        if ( document == null ) {
	            return null;
	        }
	        String pid = document.getPid();
	        if ( pid == null ) {
	            return null;
	        }
	        return pid;
	    }

	    private String documentPrintUserLastName(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        User user = documentPrint.getUser();
	        if ( user == null ) {
	            return null;
	        }
	        String lastName = user.getLastName();
	        if ( lastName == null ) {
	            return null;
	        }
	        return lastName;
	    }

	    private String documentPrintActivityName(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Activity activity = documentPrint.getActivity();
	        if ( activity == null ) {
	            return null;
	        }
	        String name = activity.getName();
	        if ( name == null ) {
	            return null;
	        }
//	        if(activity.getDescription()!=null && getCompanyCofig() && !activity.getDescription().equals("common")) {
//		        return activity.getDescription();
//		        }
	        return name;
	    }

	    private String documentPrintActivityDescription(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Activity activity = documentPrint.getActivity();
	        if ( activity == null ) {
	            return null;
	        }
	        String name = activity.getName();
	        if ( name == null ) {
	            return null;
	        }
	        if(activity.getDescription()!=null && !activity.getDescription().equals("common")) {
		        return activity.getDescription();
		        }
	        return name;
	    }

	    private String documentPrintUserLogin(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        User user = documentPrint.getUser();
	        if ( user == null ) {
	            return null;
	        }
	        String login = user.getLogin();
	        if ( login == null ) {
	            return null;
	        }
	        return login;
	    }

	    private String documentPrintUserPid(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        User user = documentPrint.getUser();
	        if ( user == null ) {
	            return null;
	        }
	        String pid = user.getPid();
	        if ( pid == null ) {
	            return null;
	        }
	        return pid;
	    }

	    private String documentPrintUserFirstName(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        User user = documentPrint.getUser();
	        if ( user == null ) {
	            return null;
	        }
	        String firstName = user.getFirstName();
	        if ( firstName == null ) {
	            return null;
	        }
	        return firstName;
	    }

	    private String documentPrintDocumentName(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Document document = documentPrint.getDocument();
	        if ( document == null ) {
	            return null;
	        }
	        String name = document.getName();
	        if ( name == null ) {
	            return null;
	        }
//	        if(document.getDescription()!=null && getCompanyCofig() && !document.getDescription().equals("common")) {
//		        return document.getDescription();
//		        }
	        return name;
	    }
	    private String documentPrintDocumentDescription(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Document document = documentPrint.getDocument();
	        if ( document == null ) {
	            return null;
	        }
	        String name = document.getName();
	        if ( name == null ) {
	            return null;
	        }
	        if(document.getDescription()!=null && !document.getDescription().equals("common")) {
		        return document.getDescription();
		        }
	        return name;
	    }

	    private String documentPrintActivityPid(DocumentPrint documentPrint) {

	        if ( documentPrint == null ) {
	            return null;
	        }
	        Activity activity = documentPrint.getActivity();
	        if ( activity == null ) {
	            return null;
	        }
	        String pid = activity.getPid();
	        if ( pid == null ) {
	            return null;
	        }
	        return pid;
	    }
}
