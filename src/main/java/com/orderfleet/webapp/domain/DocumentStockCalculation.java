package com.orderfleet.webapp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DocumentStockCalculation.
 * 
 * @author Muhammed Riyas T
 * @since Sep 30, 2016
 */
@Entity
@Table(name = "tbl_document_stock_calculation")
public class DocumentStockCalculation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_stock_calculation_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_stock_calculation_id") })
	@GeneratedValue(generator = "seq_document_stock_calculation_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_stock_calculation_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Document document;

	@Column(name = "opening", nullable = false)
	private boolean opening;

	@Column(name = "closing_actual", nullable = false)
	private boolean closingActual;

	@Column(name = "closing_logical", nullable = false)
	private boolean closingLogical;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
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
		DocumentStockCalculation documentStockCalculation = (DocumentStockCalculation) o;
		if (documentStockCalculation.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, documentStockCalculation.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
