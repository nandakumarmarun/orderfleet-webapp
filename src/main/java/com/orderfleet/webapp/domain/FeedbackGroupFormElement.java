package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.FeedbackElementType;

/**
 * A FeedbackGroupFormElement.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
@Entity
@Table(name = "tbl_feedback_group_form_element")
public class FeedbackGroupFormElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_feedback_group_form_element_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_feedback_group_form_element_id") })
	@GeneratedValue(generator = "seq_feedback_group_form_element_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_feedback_group_form_element_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private FeedbackGroup feedbackGroup;

	@NotNull
	@ManyToOne
	private FormElement formElement;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "feedback_element_type", nullable = false)
	private FeedbackElementType feedbackElementType;

	@NotNull
	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FeedbackGroup getFeedbackGroup() {
		return feedbackGroup;
	}

	public void setFeedbackGroup(FeedbackGroup feedbackGroup) {
		this.feedbackGroup = feedbackGroup;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public FeedbackElementType getFeedbackElementType() {
		return feedbackElementType;
	}

	public void setFeedbackElementType(FeedbackElementType feedbackElementType) {
		this.feedbackElementType = feedbackElementType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FeedbackGroupFormElement feedbackGroup = (FeedbackGroupFormElement) o;
		if (feedbackGroup.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, feedbackGroup.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
