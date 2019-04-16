package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A PriceTrendProductCompetitor
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
@Entity
@Table(name = "tbl_price_trend_product_competitor")
public class PriceTrendProductCompetitor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_price_trend_product_competitor_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_price_trend_product_competitor_id") })
	@GeneratedValue(generator = "seq_price_trend_product_competitor_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_price_trend_product_competitor_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private PriceTrendProduct priceTrendProduct;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "competitor_profile_id")
	private CompetitorProfile competitor;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public PriceTrendProductCompetitor() {
		super();
	}

	public PriceTrendProductCompetitor(PriceTrendProduct priceTrendProduct, CompetitorProfile competitor) {
		super();
		this.priceTrendProduct = priceTrendProduct;
		this.competitor = competitor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PriceTrendProduct getPriceTrendProduct() {
		return priceTrendProduct;
	}

	public void setPriceTrendProduct(PriceTrendProduct priceTrendProduct) {
		this.priceTrendProduct = priceTrendProduct;
	}

	public CompetitorProfile getCompetitor() {
		return competitor;
	}

	public void setCompetitor(CompetitorProfile competitor) {
		this.competitor = competitor;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

}
