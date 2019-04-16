package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.GuidedSellingConfig;

/**
 * A DTO for the GuidedSellingConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 09, 2017
 */
public class GuidedSellingConfigDTO {

	private boolean guidedSellingFilterItems;

	private String favouriteProductGroupPid;

	private String favouriteProductGroupName;

	private boolean favouriteItemCompulsory;

	private String promotionProductGroupPid;

	private String promotionProductGroupName;

	private boolean promotionItemCompulsory;

	private String guidedSellingInfoDocumentPid;

	private String guidedSellingInfoDocumentName;

	public GuidedSellingConfigDTO() {
		super();
	}

	public GuidedSellingConfigDTO(GuidedSellingConfig guidedSellingConfig) {
		super();
		this.guidedSellingFilterItems = guidedSellingConfig.getGuidedSellingFilterItems();
		if (guidedSellingConfig.getFavouriteProductGroup() != null) {
			this.favouriteProductGroupPid = guidedSellingConfig.getFavouriteProductGroup().getPid();
			this.favouriteProductGroupName = guidedSellingConfig.getFavouriteProductGroup().getName();
		}
		this.favouriteItemCompulsory = guidedSellingConfig.getFavouriteItemCompulsory();
		if (guidedSellingConfig.getPromotionProductGroup() != null) {
			this.promotionProductGroupPid = guidedSellingConfig.getPromotionProductGroup().getPid();
			this.promotionProductGroupName = guidedSellingConfig.getPromotionProductGroup().getName();
		}
		this.promotionItemCompulsory = guidedSellingConfig.getPromotionItemCompulsory();
		if (guidedSellingConfig.getGuidedSellingInfoDocument() != null) {
			this.guidedSellingInfoDocumentPid = guidedSellingConfig.getGuidedSellingInfoDocument().getPid();
			this.guidedSellingInfoDocumentName = guidedSellingConfig.getGuidedSellingInfoDocument().getName();
		}
	}

	public boolean getGuidedSellingFilterItems() {
		return guidedSellingFilterItems;
	}

	public void setGuidedSellingFilterItems(boolean guidedSellingFilterItems) {
		this.guidedSellingFilterItems = guidedSellingFilterItems;
	}

	public String getFavouriteProductGroupPid() {
		return favouriteProductGroupPid;
	}

	public void setFavouriteProductGroupPid(String favouriteProductGroupPid) {
		this.favouriteProductGroupPid = favouriteProductGroupPid;
	}

	public String getFavouriteProductGroupName() {
		return favouriteProductGroupName;
	}

	public void setFavouriteProductGroupName(String favouriteProductGroupName) {
		this.favouriteProductGroupName = favouriteProductGroupName;
	}

	public boolean getFavouriteItemCompulsory() {
		return favouriteItemCompulsory;
	}

	public void setFavouriteItemCompulsory(boolean favouriteItemCompulsory) {
		this.favouriteItemCompulsory = favouriteItemCompulsory;
	}

	public String getPromotionProductGroupPid() {
		return promotionProductGroupPid;
	}

	public void setPromotionProductGroupPid(String promotionProductGroupPid) {
		this.promotionProductGroupPid = promotionProductGroupPid;
	}

	public String getPromotionProductGroupName() {
		return promotionProductGroupName;
	}

	public void setPromotionProductGroupName(String promotionProductGroupName) {
		this.promotionProductGroupName = promotionProductGroupName;
	}

	public boolean getPromotionItemCompulsory() {
		return promotionItemCompulsory;
	}

	public void setPromotionItemCompulsory(boolean promotionItemCompulsory) {
		this.promotionItemCompulsory = promotionItemCompulsory;
	}

	public String getGuidedSellingInfoDocumentPid() {
		return guidedSellingInfoDocumentPid;
	}

	public void setGuidedSellingInfoDocumentPid(String guidedSellingInfoDocumentPid) {
		this.guidedSellingInfoDocumentPid = guidedSellingInfoDocumentPid;
	}

	public String getGuidedSellingInfoDocumentName() {
		return guidedSellingInfoDocumentName;
	}

	public void setGuidedSellingInfoDocumentName(String guidedSellingInfoDocumentName) {
		this.guidedSellingInfoDocumentName = guidedSellingInfoDocumentName;
	}

}
