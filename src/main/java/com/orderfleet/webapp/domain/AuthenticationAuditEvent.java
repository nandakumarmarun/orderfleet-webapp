package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_authentication_audit_event")
public class AuthenticationAuditEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_authentication_audit_event_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_authentication_audit_event_id") })
	@GeneratedValue(generator = "seq_authentication_audit_event_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_authentication_audit_event_id')")
	private Long id;
	
	@NotNull
	@Column(nullable = false)
	private String login;
	
	@NotNull
	@Column(nullable = false)
	private String password;

	@Column(name = "audit_event_date")
	private Instant auditEventDate;
	
	@NotNull
	@Column(nullable = false)
	private String ipAddress;
	
	public AuthenticationAuditEvent() {
	}

	public AuthenticationAuditEvent(String login, String password, String ipAddress, Instant auditEventDate) {
		super();
		this.login = login;
		this.password = password;
		this.ipAddress = ipAddress;
		this.auditEventDate = auditEventDate;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Instant getAuditEventDate() {
		return auditEventDate;
	}

	public void setAuditEventDate(Instant auditEventDate) {
		this.auditEventDate = auditEventDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}