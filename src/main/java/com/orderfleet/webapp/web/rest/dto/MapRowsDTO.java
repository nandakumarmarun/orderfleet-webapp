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
public class MapRowsDTO {
	private List<MapElementsDTO> elements = new ArrayList<MapElementsDTO>();

	public MapRowsDTO() {
		super();
	}

	public List<MapElementsDTO> getElements() {
		return elements;
	}

	public void setElements(List<MapElementsDTO> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "Rows [elements=" + elements + "]";
	}

}
