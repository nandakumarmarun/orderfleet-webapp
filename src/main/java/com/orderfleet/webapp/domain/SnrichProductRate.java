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

import com.orderfleet.webapp.domain.enums.OrderProPaymentMode;

@Entity
@Table(name = "tbl_snrich_product_rate")
public class SnrichProductRate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_account_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_group_id") })
	@GeneratedValue(generator = "seq_account_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_group_id')")
	private Long id;
	
	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@ManyToOne
	private SnrichProduct snrichProduct;
	
	//rate per user
	@Column(name = "rate")
	private Double rate; 
	
	@Enumerated(EnumType.STRING)
	@Column(name = "orderpro_payment_mode")
	private OrderProPaymentMode orderProPaymentMode;

	
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

	public SnrichProduct getSnrichProduct() {
		return snrichProduct;
	}

	public void setSnrichProduct(SnrichProduct snrichProduct) {
		this.snrichProduct = snrichProduct;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public OrderProPaymentMode getOrderProPaymentMode() {
		return orderProPaymentMode;
	}

	public void setOrderProPaymentMode(OrderProPaymentMode orderProPaymentMode) {
		this.orderProPaymentMode = orderProPaymentMode;
	}
	
	

}
