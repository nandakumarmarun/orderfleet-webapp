package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * used to calculate distance Api
 *
 * @author Sarath
 * @since May 25, 2017
 *
 */
public class MapDistanceApiDTO {

	private List<String> destination_addresses = new ArrayList<String>();
	private List<String> origin_addresses = new ArrayList<String>();
	private List<MapRowsDTO> rows = new ArrayList<MapRowsDTO>();
	private String status;

	public MapDistanceApiDTO() {
		super();
	}

	public List<String> getDestination_addresses() {
		return destination_addresses;
	}

	public void setDestination_addresses(List<String> destination_addresses) {
		this.destination_addresses = destination_addresses;
	}

	public List<String> getOrigin_addresses() {
		return origin_addresses;
	}

	public void setOrigin_addresses(List<String> origin_addresses) {
		this.origin_addresses = origin_addresses;
	}

	public List<MapRowsDTO> getRows() {
		return rows;
	}

	public void setRows(List<MapRowsDTO> rows) {
		this.rows = rows;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DistanceApiJson [destination_addresses=" + destination_addresses + ", origin_addresses="
				+ origin_addresses + ", rows=" + rows + ", status=" + status + "]";
	}

}
