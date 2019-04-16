package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import com.orderfleet.webapp.domain.DocumentStockCalculation;

/**
 * A DTO for the Document entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public class DocumentStockCalculationDTO implements Cloneable {

	private String documentPid;

	private String documentName;

	private boolean opening;

	private boolean closingActual;

	private boolean closingLogical;

	public DocumentStockCalculationDTO() {
	}

	public DocumentStockCalculationDTO(DocumentStockCalculation documentStockCalculation) {
		super();
		this.documentPid = documentStockCalculation.getDocument().getPid();
		this.documentName = documentStockCalculation.getDocument().getName();
		this.opening = documentStockCalculation.getOpening();
		this.closingActual = documentStockCalculation.getClosingActual();
		this.closingLogical = documentStockCalculation.getClosingLogical();
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public boolean getOpening() {
		return opening;
	}

	public void setOpening(boolean opening) {
		this.opening = opening;
	}

	public boolean getClosingActual() {
		return closingActual;
	}

	public void setClosingActual(boolean closingActual) {
		this.closingActual = closingActual;
	}

	public boolean getClosingLogical() {
		return closingLogical;
	}

	public void setClosingLogical(boolean closingLogical) {
		this.closingLogical = closingLogical;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DocumentStockCalculationDTO documentDTO = (DocumentStockCalculationDTO) o;

		if (!Objects.equals(documentPid, documentDTO.documentPid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(documentPid);
	}

}
