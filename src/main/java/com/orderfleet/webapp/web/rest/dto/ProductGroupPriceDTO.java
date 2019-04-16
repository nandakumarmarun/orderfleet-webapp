package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;

import com.orderfleet.webapp.domain.ProductGroupPrice;

/**
 * A DTO for the ProductGroupPrice entity.
 * 
 * @author Shaheer
 * @since December 12, 2016
 */
public class ProductGroupPriceDTO {

	private Long id;
	
	private ProductGroupDTO productGroupDTO;

	private BigDecimal price;

	public ProductGroupPriceDTO() {
	}

	public ProductGroupPriceDTO(ProductGroupPrice productGroupPrice) {
		super();
		this.id = productGroupPrice.getId();
		this.productGroupDTO = new ProductGroupDTO(productGroupPrice.getProductGroup());
		this.price = productGroupPrice.getPrice();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductGroupDTO getProductGroupDTO() {
		return productGroupDTO;
	}

	public void setProductGroupDTO(ProductGroupDTO productGroupDTO) {
		this.productGroupDTO = productGroupDTO;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
