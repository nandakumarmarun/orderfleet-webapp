package com.orderfleet.webapp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_inventory_voucher_update_history")
public class InventoryVoucherUpdateHistory {
	@Id
	@GenericGenerator(name = "seq_inventory_voucher_update_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_update_history_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_update_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_update_history_id')")
	private Long id;
	
	private String pid;
	
	@NotNull
	@ManyToOne
	private User updateBy;
	
	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime updatedDate;
	
	@Column(name = "quantity")
	private double quantity;
	
	@Column(name = "Updated")
	private boolean isUpdated;
	
	@ManyToOne
	@JoinColumn(name = "inventory_voucher_header_id")
	private InventoryVoucherHeader inventoryVoucherHeader;

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


	public User getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(User updateBy) {
		this.updateBy = updateBy;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public InventoryVoucherHeader getInventoryVoucherHeader() {
		return inventoryVoucherHeader;
	}

	public void setInventoryVoucherHeader(InventoryVoucherHeader inventoryVoucherHeader) {
		this.inventoryVoucherHeader = inventoryVoucherHeader;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
}
