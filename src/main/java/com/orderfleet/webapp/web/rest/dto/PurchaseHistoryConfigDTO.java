package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.PurchaseHistoryConfig;

/**
 * A DTO for the PurchaseHistoryConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 06, 2017
 */
public class PurchaseHistoryConfigDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private int startMonth;

	private String startMonthName;

	private int startMonthMinus;

	private int startMonthYearMinus;

	private int endMonth;

	private String endMonthName;

	private int endMonthMinus;

	private int endMonthYearMinus;

	private boolean createDynamicLabel;

	private String description;

	private int sortOrder;

	public PurchaseHistoryConfigDTO() {
		super();
	}

	public PurchaseHistoryConfigDTO(PurchaseHistoryConfig purchaseHistoryConfig) {
		super();
		this.pid = purchaseHistoryConfig.getPid();
		this.name = purchaseHistoryConfig.getName();
		this.startMonth = purchaseHistoryConfig.getStartMonth();
		this.startMonthName = purchaseHistoryConfig.getStartMonthName();
		this.startMonthMinus = purchaseHistoryConfig.getStartMonthMinus();
		this.startMonthYearMinus = purchaseHistoryConfig.getStartMonthYearMinus();
		this.endMonth = purchaseHistoryConfig.getEndMonth();
		this.endMonthName = purchaseHistoryConfig.getEndMonthName();
		this.endMonthMinus = purchaseHistoryConfig.getEndMonthMinus();
		this.endMonthYearMinus = purchaseHistoryConfig.getEndMonthYearMinus();
		this.createDynamicLabel = purchaseHistoryConfig.getCreateDynamicLabel();
		this.description = purchaseHistoryConfig.getDescription();
		this.sortOrder = purchaseHistoryConfig.getSortOrder();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getStartMonthMinus() {
		return startMonthMinus;
	}

	public void setStartMonthMinus(int startMonthMinus) {
		this.startMonthMinus = startMonthMinus;
	}

	public int getStartMonthYearMinus() {
		return startMonthYearMinus;
	}

	public void setStartMonthYearMinus(int startMonthYearMinus) {
		this.startMonthYearMinus = startMonthYearMinus;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public int getEndMonthMinus() {
		return endMonthMinus;
	}

	public void setEndMonthMinus(int endMonthMinus) {
		this.endMonthMinus = endMonthMinus;
	}

	public int getEndMonthYearMinus() {
		return endMonthYearMinus;
	}

	public void setEndMonthYearMinus(int endMonthYearMinus) {
		this.endMonthYearMinus = endMonthYearMinus;
	}

	public boolean getCreateDynamicLabel() {
		return createDynamicLabel;
	}

	public void setCreateDynamicLabel(boolean createDynamicLabel) {
		this.createDynamicLabel = createDynamicLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartMonthName() {
		return startMonthName;
	}

	public void setStartMonthName(String startMonthName) {
		this.startMonthName = startMonthName;
	}

	public String getEndMonthName() {
		return endMonthName;
	}

	public void setEndMonthName(String endMonthName) {
		this.endMonthName = endMonthName;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PurchaseHistoryConfigDTO purchaseHistoryConfigDTO = (PurchaseHistoryConfigDTO) o;

		if (!Objects.equals(pid, purchaseHistoryConfigDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "PurchaseHistoryConfigDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", description='"
				+ description + "'" + '}';
	}
}
