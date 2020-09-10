package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.enums.ContactManagement;

/**
 * A DTO for the Activity entity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
public class StockLocationManagementDTO {

	private String stockLocationPid;
	private String stockLocationName;
	private String userName;
	private LocalDateTime temporaryStockLocationDate;
	private LocalDateTime liveStockLocationDate;

	public String getStockLocationPid() {
		return stockLocationPid;
	}

	public void setStockLocationPid(String stockLocationPid) {
		this.stockLocationPid = stockLocationPid;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getTemporaryStockLocationDate() {
		return temporaryStockLocationDate;
	}

	public void setTemporaryStockLocationDate(LocalDateTime temporaryStockLocationDate) {
		this.temporaryStockLocationDate = temporaryStockLocationDate;
	}

	public LocalDateTime getLiveStockLocationDate() {
		return liveStockLocationDate;
	}

	public void setLiveStockLocationDate(LocalDateTime liveStockLocationDate) {
		this.liveStockLocationDate = liveStockLocationDate;
	}

}
