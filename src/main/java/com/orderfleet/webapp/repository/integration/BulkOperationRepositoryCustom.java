package com.orderfleet.webapp.repository.integration;

import java.util.List;
import java.util.Set;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.domain.GSTProductGroup;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.TaxMaster;

/**
 * Interface for bulk operation that to be implemented manually.
 *
 * @author Shaheer
 * @since March 11, 2017
 *
 */
public interface BulkOperationRepositoryCustom {

	void bulkSaveProductCategory(Set<ProductCategory> productCategories);

	void bulkSaveProductGroup(Set<ProductGroup> productGroups);

	void bulkSaveProductProfile(Set<ProductProfile> productProfiles);

	void bulkSaveProductGroupProductProfile(Set<ProductGroupProduct> productGroupProducts);

	void bulkSaveStockLocations(Set<StockLocation> stockLocations);

	void bulkSaveOpeningStocks(Set<OpeningStock> openingStocks);

	void bulkSaveUpdatePriceLevels(Set<PriceLevel> priceLevels);

	void bulkSaveAccountProfile(Set<AccountProfile> accountProfiles);

	void bulkSaveLocations(Set<Location> locations);

	void bulkSaveReceivablePayables(Set<ReceivablePayable> receivablePayables);

	void bulkSaveUpdatePriceLevelLists(Set<PriceLevelList> priceLevelLists);

	void bulkSaveGSTProductGroup(Set<GSTProductGroup> gstpg);

	void bulkSaveTaxMasters(Set<TaxMaster> saveUpdateTaxMasters);

	void bulkSaveEcomProductProfiles(Set<EcomProductProfile> saveUpdateEcomProductProfiles);

	void bulkSaveEcomProductProfileProducts(List<EcomProductProfileProduct> saveUpdateEcomProductProfileProducts);

	void bulkSaveProductGroupEcomProducts(List<ProductGroupEcomProduct> saveUpdateProductGroupEcomProducts);

	void bulkPriceLevelAccountProductGroups(List<PriceLevelAccountProductGroup> savePriceLevelAccountProductGroup);
}
