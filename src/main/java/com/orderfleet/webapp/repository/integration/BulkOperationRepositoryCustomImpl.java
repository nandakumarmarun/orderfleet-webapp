package com.orderfleet.webapp.repository.integration;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

@Component
public final class BulkOperationRepositoryCustomImpl implements BulkOperationRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	@Override
	@Transactional
	public void bulkSaveProductCategory(Set<ProductCategory> productCategories) {
		int i = 0;
		for (ProductCategory entity : productCategories) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				// flush a batch of inserts and release memory.
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveProductGroup(Set<ProductGroup> productGroups) {
		int i = 0;
		for (ProductGroup entity : productGroups) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveProductProfile(Set<ProductProfile> productProfiles) {
		int i = 0;
		for (ProductProfile entity : productProfiles) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveProductGroupProductProfile(Set<ProductGroupProduct> productGroupProducts) {
		int i = 0;
		for (ProductGroupProduct entity : productGroupProducts) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveStockLocations(Set<StockLocation> saveUpdateStockLocations) {
		int i = 0;
		for (StockLocation entity : saveUpdateStockLocations) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveOpeningStocks(Set<OpeningStock> openingStocks) {
		int i = 0;
		for (OpeningStock entity : openingStocks) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveUpdatePriceLevels(Set<PriceLevel> priceLevels) {
		int i = 0;
		for (PriceLevel entity : priceLevels) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveUpdatePriceLevelLists(Set<PriceLevelList> priceLevelLists) {
		int i = 0;
		for (PriceLevelList entity : priceLevelLists) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveAccountProfile(Set<AccountProfile> accountProfiles) {
		int i = 0;
		for (AccountProfile entity : accountProfiles) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveLocations(Set<Location> locations) {
		int i = 0;
		for (Location entity : locations) {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveReceivablePayables(Set<ReceivablePayable> receivablePayables) {
		int i = 0;
		for (ReceivablePayable entity : receivablePayables) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveGSTProductGroup(Set<GSTProductGroup> gstpg) {
		int i = 0;
		for (GSTProductGroup entity : gstpg) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveTaxMasters(Set<TaxMaster> saveUpdateTaxMasters) {
		int i = 0;
		for (TaxMaster entity : saveUpdateTaxMasters) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveEcomProductProfiles(Set<EcomProductProfile> saveUpdateEcomProductProfiles) {
		int i = 0;
		for (EcomProductProfile entity : saveUpdateEcomProductProfiles) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}

	}

	@Override
	@Transactional
	public void bulkSaveEcomProductProfileProducts(
			List<EcomProductProfileProduct> saveUpdateEcomProductProfileProducts) {
		int i = 0;
		for (EcomProductProfileProduct entity : saveUpdateEcomProductProfileProducts) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	@Transactional
	public void bulkSaveProductGroupEcomProducts(List<ProductGroupEcomProduct> saveUpdateProductGroupEcomProducts) {
		int i = 0;
		for (ProductGroupEcomProduct entity : saveUpdateProductGroupEcomProducts) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}

	}

	@Override
	@Transactional
	public void bulkPriceLevelAccountProductGroups(
			List<PriceLevelAccountProductGroup> savePriceLevelAccountProductGroup) {
		int i = 0;
		for (PriceLevelAccountProductGroup entity : savePriceLevelAccountProductGroup) {
			em.persist(entity);
			i++;
			if (i % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}

	}
}
