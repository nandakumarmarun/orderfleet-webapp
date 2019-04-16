package com.orderfleet.webapp.web.rest.dto;

public class PriceView {

	private String priceLabel;

	private double price;

	private String values;

	public PriceView(PriceTrendConfigurationDTO configuration) {
		this.priceLabel = configuration.getValue();
	}

	public PriceView(String priceLabel, double price) {
		super();
		this.priceLabel = priceLabel;
		this.price = price;
	}

	public String getPriceLabel() {
		return priceLabel;
	}

	public void setPriceLabel(String priceLabel) {
		this.priceLabel = priceLabel;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

}
