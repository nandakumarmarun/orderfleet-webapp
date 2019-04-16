package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

public class AssignProductSetUpDTO {
	
	private List<UserDTO> userDTOs;
	
	private List<ProductGroupDTO>productGroupDTOs;
	
	private List<ProductCategoryDTO> productCategoryDTOs;

	public AssignProductSetUpDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AssignProductSetUpDTO(List<UserDTO> userDTOs, List<ProductGroupDTO> productGroupDTOs,
			List<ProductCategoryDTO> productCategoryDTOs) {
		super();
		this.userDTOs = userDTOs;
		this.productGroupDTOs = productGroupDTOs;
		this.productCategoryDTOs = productCategoryDTOs;
	}

	public List<UserDTO> getUserDTOs() {
		return userDTOs;
	}

	public void setUserDTOs(List<UserDTO> userDTOs) {
		this.userDTOs = userDTOs;
	}

	public List<ProductGroupDTO> getProductGroupDTOs() {
		return productGroupDTOs;
	}

	public void setProductGroupDTOs(List<ProductGroupDTO> productGroupDTOs) {
		this.productGroupDTOs = productGroupDTOs;
	}

	public List<ProductCategoryDTO> getProductCategoryDTOs() {
		return productCategoryDTOs;
	}

	public void setProductCategoryDTOs(List<ProductCategoryDTO> productCategoryDTOs) {
		this.productCategoryDTOs = productCategoryDTOs;
	}
	
	

}
