package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A CustomerTimeSpent.
 * 
 * @author Fahad
 * @since Jan 20, 2018
 */
@Entity
@Table(name = "tbl_customer_time_spent")
public class CustomerTimeSpent implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_customer_time_spent_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_customer_time_spent_id") })
	@GeneratedValue(generator = "seq_customer_time_spent_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_customer_time_spent_id')")
	private Long id;
	
	@ManyToOne
	@NotNull
	private AccountProfile accountProfile;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@ManyToOne
	@NotNull
	private EmployeeProfile employeeProfile;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;
	
	@NotNull
	@Column(name = "end_time")
	private LocalDateTime endTime;
	
	@NotNull
	@Column(name="active",nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean active=false;
	
	@Column(name="client_transaction_key")
	private String clientTransactionKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getClientTransactionKey() {
		return clientTransactionKey;
	}

	public void setClientTransactionKey(String clientTransactionKey) {
		this.clientTransactionKey = clientTransactionKey;
	}

	@Override
	public String toString() {
		return "CustomerTimeSpent [id=" + id + ", accountProfile=" + accountProfile + ", user=" + user
				+ ", employeeProfile=" + employeeProfile + ", company=" + company + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}
	
}
