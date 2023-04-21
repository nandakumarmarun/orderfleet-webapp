package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_distributor_dealer_profile")
public class DistributorDealerAssociation implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_distributor_dealer_profile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_distributor_dealer_profile_id") })
	@GeneratedValue(generator = "seq_distributor_dealer_profile_id_GEN")
	
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_distributor_dealer_profile_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@NotNull
	@ManyToOne
	private AccountProfile distributor;

	@NotNull
	@ManyToOne
	private AccountProfile dealer;

	@NotNull
	@ManyToOne
	private Company company;

	public DistributorDealerAssociation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DistributorDealerAssociation(Long id, AccountProfile distributor, AccountProfile dealer, Company company) {
		super();
		this.id = id;
		this.distributor = distributor;
		this.dealer = dealer;
		this.company = company;
	}

	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountProfile getDistributor() {
		return distributor;
	}

	public void setDistributor(AccountProfile distributor) {
		this.distributor = distributor;
	}

	public AccountProfile getDealer() {
		return dealer;
	}

	public void setDealer(AccountProfile dealer) {
		this.dealer = dealer;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "DistributorDealerAssociation [id=" + id + ", distributor=" + distributor + ", dealer=" + dealer
				+ ", company=" + company + "]";
	}
	
	
	

}
