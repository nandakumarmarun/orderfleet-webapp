package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
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
 * A CompetitorProfile.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Entity
@Table(name = "tbl_competitor_price_trend")
public class CompetitorPriceTrend implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_competitor_price_trend_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_competitor_price_trend_id") })
	@GeneratedValue(generator = "seq_competitor_price_trend_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_competitor_price_trend_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	private PriceTrendProductGroup priceTrendProductGroup;

	@ManyToOne
	private PriceTrendProduct priceTrendProduct;

	@ManyToOne
	private CompetitorProfile competitorProfile;

	@Column(name = "price1")
	private double price1;

	@Column(name = "price2")
	private double price2;

	@Column(name = "price3")
	private double price3;

	@Column(name = "price4")
	private double price4;

	@Column(name = "price5")
	private double price5;

	@Column(name = "remarks")
	private String remarks;

	@NotNull
	@ManyToOne
	private Company company;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@ManyToOne
	@NotNull
	private User user;

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

	public PriceTrendProductGroup getPriceTrendProductGroup() {
		return priceTrendProductGroup;
	}

	public void setPriceTrendProductGroup(PriceTrendProductGroup priceTrendProductGroup) {
		this.priceTrendProductGroup = priceTrendProductGroup;
	}

	public PriceTrendProduct getPriceTrendProduct() {
		return priceTrendProduct;
	}

	public void setPriceTrendProduct(PriceTrendProduct priceTrendProduct) {
		this.priceTrendProduct = priceTrendProduct;
	}

	public CompetitorProfile getCompetitorProfile() {
		return competitorProfile;
	}

	public void setCompetitorProfile(CompetitorProfile competitorProfile) {
		this.competitorProfile = competitorProfile;
	}

	public double getPrice1() {
		return price1;
	}

	public void setPrice1(double price1) {
		this.price1 = price1;
	}

	public double getPrice2() {
		return price2;
	}

	public void setPrice2(double price2) {
		this.price2 = price2;
	}

	public double getPrice3() {
		return price3;
	}

	public void setPrice3(double price3) {
		this.price3 = price3;
	}

	public double getPrice4() {
		return price4;
	}

	public void setPrice4(double price4) {
		this.price4 = price4;
	}

	public double getPrice5() {
		return price5;
	}

	public void setPrice5(double price5) {
		this.price5 = price5;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CompetitorPriceTrend competitorProfile = (CompetitorPriceTrend) o;
		if (competitorProfile.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, competitorProfile.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
