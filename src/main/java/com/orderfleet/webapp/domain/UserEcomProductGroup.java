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

/**
 * A UserProductGroup.
 * 
 * @author Anish
 * @since June 9 2020
 */
@Entity
@Table(name = "tbl_user_ecom_product_group")
public class UserEcomProductGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_user_ecom_product_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_ecom_product_group_id") })
	@GeneratedValue(generator = "seq_user_ecom_product_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_ecom_product_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private EcomProductGroup productGroup;

	@ManyToOne
	@NotNull
	private Company company;

	public UserEcomProductGroup() {
		super();
	}

	public UserEcomProductGroup(User user, EcomProductGroup productGroup, Company company) {
		super();
		this.user = user;
		this.productGroup = productGroup;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public EcomProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(EcomProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "UserProductGroup [id=" + id + ", user=" + user + ", productGroup=" + productGroup + "]";
	}

}
