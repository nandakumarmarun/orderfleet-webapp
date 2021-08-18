package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.ProductGroup;

/**
 * A DTO for the ProductGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public class ProductGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private byte[] image;

	private String imageContentType;

	private String productGroupId;

	@NotNull
	private boolean activated = true;

	private boolean thirdpartyUpdate;

	private LocalDateTime lastModifiedDate;

	private List<TaxMasterDTO> productGroupTaxMasterDTOs;

	public ProductGroupDTO() {
		super();
	}

//	public ProductGroupDTO(ProductGroup productGroup) {
//		this(productGroup.getPid(), productGroup.getName(), productGroup.getAlias(), productGroup.getDescription(),
//				productGroup.getLastModifiedDate());
//
//	}

	public ProductGroupDTO(ProductGroup productGroup) {
		super();
		this.pid = productGroup.getPid();
		this.name = productGroup.getName();
		this.alias = productGroup.getAlias();
		this.description = productGroup.getDescription();
		this.lastModifiedDate = productGroup.getLastModifiedDate();
		List<TaxMasterDTO> taxMasterDTOs = new ArrayList<>();
		if (productGroup.getTaxMastersList().isEmpty()) {
			this.productGroupTaxMasterDTOs = taxMasterDTOs;
		} else {
			this.productGroupTaxMasterDTOs = productGroup.getTaxMastersList().stream().map(TaxMasterDTO::new)
					.collect(Collectors.toList());
		}
		this.productGroupId = productGroup.getProductGroupId();
	}

	public ProductGroupDTO(String pid, String name, String alias, String description, LocalDateTime lastModifiedDate) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.lastModifiedDate = lastModifiedDate;
	}

	public ProductGroupDTO(String pid, String name, String alias, String description, byte[] image,
			String imageContentType, boolean activated, boolean thirdpartyUpdate, LocalDateTime lastModifiedDate) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.image = image;
		this.imageContentType = imageContentType;
		this.activated = activated;
		this.thirdpartyUpdate = thirdpartyUpdate;
		this.lastModifiedDate = lastModifiedDate;

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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public List<TaxMasterDTO> getProductGroupTaxMasterDTOs() {
		return productGroupTaxMasterDTOs;
	}

	public void setProductGroupTaxMasterDTOs(List<TaxMasterDTO> productGroupTaxMasterDTOs) {
		this.productGroupTaxMasterDTOs = productGroupTaxMasterDTOs;
	}

	public String getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(String productGroupId) {
		this.productGroupId = productGroupId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ProductGroupDTO productGroupDTO = (ProductGroupDTO) o;

		if (!Objects.equals(pid, productGroupDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean getThirdpartyUpdate() {
		return thirdpartyUpdate;
	}

	public void setThirdpartyUpdate(boolean thirdpartyUpdate) {
		this.thirdpartyUpdate = thirdpartyUpdate;
	}

	@Override
	public String toString() {
		return "ProductGroupDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", image=" + Arrays.toString(image) + ", imageContentType=" + imageContentType + ", activated="
				+ activated + ", thirdpartyUpdate=" + thirdpartyUpdate + ", lastModifiedDate=" + lastModifiedDate
				+ ", productGroupTaxMasterDTOs=" + productGroupTaxMasterDTOs + "]";
	}

}