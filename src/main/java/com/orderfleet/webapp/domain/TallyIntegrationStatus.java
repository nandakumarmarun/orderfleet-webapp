package com.orderfleet.webapp.domain;

import java.io.Serializable;

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

import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;

/**
 * A TallyIntegrationStatus.
 *
 * @author Sarath
 * @since Mar 4, 2017
 */
@Entity
@Table(name = "tbl_tally_integration_status")
public class TallyIntegrationStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_tally_integration_status_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_tally_integration_status_id") })
	@GeneratedValue(generator = "seq_tally_integration_status_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tally_integration_status_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Company company;

	@Enumerated(EnumType.STRING)
	@Column(name = "tally_integration_status_type", nullable = false)
	private TallyIntegrationStatusType tallyIntegrationStatusType;

	@Column(name = "integrated", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean integrated = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public TallyIntegrationStatusType getTallyIntegrationStatusType() {
		return tallyIntegrationStatusType;
	}

	public void setTallyIntegrationStatusType(TallyIntegrationStatusType tallyIntegrationStatusType) {
		this.tallyIntegrationStatusType = tallyIntegrationStatusType;
	}

	public boolean isIntegrated() {
		return integrated;
	}

	public void setIntegrated(boolean integrated) {
		this.integrated = integrated;
	}

}
