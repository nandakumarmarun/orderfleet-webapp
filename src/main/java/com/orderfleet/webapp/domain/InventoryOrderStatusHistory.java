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

@Entity
@Table(name = "tbl_inventory_order_status_history")
public class InventoryOrderStatusHistory implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_inventory_order_status_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_order_status_history_id") })
	@GeneratedValue(generator = "seq_inventory_order_status_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_order_status_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@NotNull
	@Column(name = "status_updated_date", nullable = false)
	private LocalDateTime statusUpdatedDate;
	
	@NotNull
	@ManyToOne
	private User statusUpdatedUser;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@ManyToOne
	@NotNull
	private InventoryVoucherHeader inventoryVoucherHeader;
	
	@NotNull
	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;
	
	@NotNull
	@ManyToOne
	private AccountProfile accountProfile;
	
	@Column(name = "order_amount", nullable = false)
	private double orderAmount;
	
	@NotNull
	@ManyToOne
	private OrderStatus orderStatus;

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

	public LocalDateTime getStatusUpdatedDate() {
		return statusUpdatedDate;
	}

	public void setStatusUpdatedDate(LocalDateTime statusUpdatedDate) {
		this.statusUpdatedDate = statusUpdatedDate;
	}

	public User getStatusUpdatedUser() {
		return statusUpdatedUser;
	}

	public void setStatusUpdatedUser(User statusUpdatedUser) {
		this.statusUpdatedUser = statusUpdatedUser;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public InventoryVoucherHeader getInventoryVoucherHeader() {
		return inventoryVoucherHeader;
	}

	public void setInventoryVoucherHeader(InventoryVoucherHeader inventoryVoucherHeader) {
		this.inventoryVoucherHeader = inventoryVoucherHeader;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "InventoryOrderStatusHistory [id=" + id + ", pid=" + pid + ", statusUpdatedDate=" + statusUpdatedDate
				+ ", statusUpdatedUser=" + statusUpdatedUser + ", company=" + company + ", inventoryVoucherHeader="
				+ inventoryVoucherHeader + ", orderDate=" + orderDate + ", accountProfile=" + accountProfile
				+ ", orderAmount=" + orderAmount + ", orderStatus=" + orderStatus + "]";
	}
	
	
	
}
