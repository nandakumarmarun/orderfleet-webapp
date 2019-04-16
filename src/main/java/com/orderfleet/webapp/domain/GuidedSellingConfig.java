package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A GuidedSellingConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 09, 2017
 */
@Entity
@Table(name = "tbl_guided_selling_config")
public class GuidedSellingConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_guided_selling_config_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_guided_selling_config_id") })
	@GeneratedValue(generator = "seq_guided_selling_config_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_guided_selling_config_id')")
	private Long id;

	@Column(name = "guided_selling_filter_items")
	private boolean guidedSellingFilterItems;

	@ManyToOne
	private ProductGroup favouriteProductGroup;

	@Column(name = "favourite_item_compulsory")
	private boolean favouriteItemCompulsory;

	@ManyToOne
	private ProductGroup promotionProductGroup;

	@Column(name = "promotion_item_compulsory")
	private boolean promotionItemCompulsory;

	@ManyToOne
	private Document guidedSellingInfoDocument;

	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getGuidedSellingFilterItems() {
		return guidedSellingFilterItems;
	}

	public void setGuidedSellingFilterItems(boolean guidedSellingFilterItems) {
		this.guidedSellingFilterItems = guidedSellingFilterItems;
	}

	public ProductGroup getFavouriteProductGroup() {
		return favouriteProductGroup;
	}

	public void setFavouriteProductGroup(ProductGroup favouriteProductGroup) {
		this.favouriteProductGroup = favouriteProductGroup;
	}

	public boolean getFavouriteItemCompulsory() {
		return favouriteItemCompulsory;
	}

	public void setFavouriteItemCompulsory(boolean favouriteItemCompulsory) {
		this.favouriteItemCompulsory = favouriteItemCompulsory;
	}

	public ProductGroup getPromotionProductGroup() {
		return promotionProductGroup;
	}

	public void setPromotionProductGroup(ProductGroup promotionProductGroup) {
		this.promotionProductGroup = promotionProductGroup;
	}

	public boolean getPromotionItemCompulsory() {
		return promotionItemCompulsory;
	}

	public void setPromotionItemCompulsory(boolean promotionItemCompulsory) {
		this.promotionItemCompulsory = promotionItemCompulsory;
	}

	public Document getGuidedSellingInfoDocument() {
		return guidedSellingInfoDocument;
	}

	public void setGuidedSellingInfoDocument(Document guidedSellingInfoDocument) {
		this.guidedSellingInfoDocument = guidedSellingInfoDocument;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
