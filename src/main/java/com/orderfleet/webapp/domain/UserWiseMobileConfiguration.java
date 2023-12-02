package com.orderfleet.webapp.domain;

import com.orderfleet.webapp.domain.enums.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A User wise MobileConfiguration.
 *
 * @author Resmi T R
 * @since Nov 27, 2023
 *
 */
@Entity
@Table(name = "tbl_user_mobile_configuration")
public class UserWiseMobileConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_mobile_configuration_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_mobile_configuration_id") })
	@GeneratedValue(generator = "seq_user_mobile_configuration_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_mobile_configuration_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "live_routing", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean liveRouting;


	@NotNull
	@ManyToOne
	private Company company;

	@NotNull
	@ManyToOne
	private User user;



	public UserWiseMobileConfiguration() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean isLiveRouting() {
		return liveRouting;
	}

	public void setLiveRouting(boolean liveRouting) {
		this.liveRouting = liveRouting;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserWiseMobileConfiguration{" +
				"id=" + id +
				", pid='" + pid + '\'' +
				", liveRouting=" + liveRouting +
				", company=" + company +
				", user=" + user +
				'}';
	}
}
