package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DocumentPrintCheckDTO.
 *
 * @author Sarath
 * @since Aug 14, 2017
 *
 */
public class DocumentPrintCheckDTO {

	private List<DocumentDTO> allDocuments;
	private List<DocumentDTO> trueDocuments;

	public DocumentPrintCheckDTO() {
		super();
	}

	public DocumentPrintCheckDTO(List<DocumentDTO> allDocuments, List<DocumentDTO> trueDocuments) {
		super();
		this.allDocuments = allDocuments;
		this.trueDocuments = trueDocuments;
	}

	public List<DocumentDTO> getAllDocuments() {
		return allDocuments;
	}

	public void setAllDocuments(List<DocumentDTO> allDocuments) {
		this.allDocuments = allDocuments;
	}

	public List<DocumentDTO> getTrueDocuments() {
		return trueDocuments;
	}

	public void setTrueDocuments(List<DocumentDTO> trueDocuments) {
		this.trueDocuments = trueDocuments;
	}

}
