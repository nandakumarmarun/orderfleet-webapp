package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

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
 * A UserStockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
@Entity
@Table(name = "tbl_user_stock_location")
public class UserStockLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_stock_location_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_stock_location_id") })
	@GeneratedValue(generator = "seq_user_stock_location_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_stock_location_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private StockLocation stockLocation;

	@ManyToOne
	@NotNull
	private Company company;

	public UserStockLocation() {
		super();
	}

	public UserStockLocation(User user, StockLocation stockLocation, Company company) {
		super();
		this.user = user;
		this.stockLocation = stockLocation;
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

	public StockLocation getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(StockLocation stockLocation) {
		this.stockLocation = stockLocation;
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
		UserStockLocation userStockLocation = (UserStockLocation) o;
		if (userStockLocation.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userStockLocation.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
