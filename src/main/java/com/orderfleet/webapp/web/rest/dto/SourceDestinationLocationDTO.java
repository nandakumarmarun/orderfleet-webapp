package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class SourceDestinationLocationDTO {

	private List<StockLocationDTO> sourceStockLocationDTOs;
	
	private List<StockLocationDTO> destinationStockLocationDTOs;

	public List<StockLocationDTO> getSourceStockLocationDTOs() {
		return sourceStockLocationDTOs;
	}

	public void setSourceStockLocationDTOs(List<StockLocationDTO> sourceStockLocationDTOs) {
		this.sourceStockLocationDTOs = sourceStockLocationDTOs;
	}

	public List<StockLocationDTO> getDestinationStockLocationDTOs() {
		return destinationStockLocationDTOs;
	}

	public void setDestinationStockLocationDTOs(List<StockLocationDTO> destinationStockLocationDTOs) {
		this.destinationStockLocationDTOs = destinationStockLocationDTOs;
	}
	
	
	
}
