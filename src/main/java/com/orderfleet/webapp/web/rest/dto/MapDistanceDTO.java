package com.orderfleet.webapp.web.rest.dto;

/**
 * used to calculate distance Api.
 * 
 * @author Sarath
 * @since May 25, 2017
 *
 */
public class MapDistanceDTO {

	private String text;
	private Integer value;

	public MapDistanceDTO() {
		super();
	}

	public MapDistanceDTO(String text, Integer value) {
		super();
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Distance [text=" + text + ", value=" + value + "]";
	}

}
