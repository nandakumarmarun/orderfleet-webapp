package com.orderfleet.webapp.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A FilledFormDetailHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_filled_form_detail_history")
public class FilledFormDetailHistory {

	@Id
	@GenericGenerator(name = "seq_filled_form_detail_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_filled_form_detail_history_id") })
	@GeneratedValue(generator = "seq_filled_form_detail_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_filled_form_detail_history_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "filled_form_history_id")
	private FilledFormHistory filledFormHistory;

	@Column(name = "value")
	private String value;

	@ManyToOne
	@NotNull
	private FormElement formElement;

	public FilledFormDetailHistory() {

	}

	public FilledFormDetailHistory(FilledFormDetail filledFormDetail) {
		super();
		this.value = filledFormDetail.getValue();
		this.formElement = filledFormDetail.getFormElement();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public FilledFormHistory getFilledFormHistory() {
		return filledFormHistory;
	}

	public void setFilledFormHistory(FilledFormHistory filledFormHistory) {
		this.filledFormHistory = filledFormHistory;
	}

}
