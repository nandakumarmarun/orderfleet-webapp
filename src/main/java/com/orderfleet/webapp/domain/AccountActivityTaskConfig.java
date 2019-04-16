package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_account_activity_task_config")
public class AccountActivityTaskConfig implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_activity_account_type_config_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_activity_account_type_config_id") })
	@GeneratedValue(generator = "seq_activity_account_type_config_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_activity_account_type_config_id')")
	private Long id;
	
	@ManyToOne
	private AccountType accountType;
	
	@ManyToOne
	private Activity activity;
	
	@ManyToOne
	private Company company;
	
	@Column(name = "assign_notification", columnDefinition = "boolean DEFAULT 'FALSE'")
	private Boolean assignNotification;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Boolean getAssignNotification() {
		return assignNotification;
	}

	public void setAssignNotification(Boolean assignNotification) {
		this.assignNotification = assignNotification;
	}
	
	
	
}
