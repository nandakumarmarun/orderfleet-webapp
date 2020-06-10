package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroupProduct;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EcomProductGroupProductRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserEcomProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductGroupProductService;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing ProductGroupProduct.
 *
 * @author Anish
 * @since June 9, 2020
 */
@Service
@Transactional
public class EcomProductGroupProductServiceImpl implements EcomProductGroupProductService {
	private final Logger log = LoggerFactory.getLogger(EcomProductGroupProductServiceImpl.class);

	@Inject
	private EcomProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductGroupProductRepository productGroupProductRepository;

	@Inject
	private ProductProfileRepository productRepository;

	@Inject
	private ProductProfileMapper productMapper;

	@Inject
	private UserEcomProductGroupRepository userProductGroupRepository;

	@Inject
	private EcomProductGroupMapper productGroupMapper;

	@Inject
	private ProductProfileMapper productProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductsRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Override
	public void save(String productGroupPid, String assignedProducts) {
		log.debug("Request to save ProductGroup Products");
		EcomProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<EcomProductGroupProduct> productGroupProducts = new ArrayList<>();
		for (String productPid : products) {
			ProductProfile product = productRepository.findOneByPid(productPid).get();
			productGroupProducts.add(new EcomProductGroupProduct(product, productGroup, company));
		}
		productGroupProductRepository.deleteByProductGroupPid(productGroupPid);
		productGroupProductRepository.save(productGroupProducts);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductByProductGroupPid(String productGroupPid) {
		log.debug("Request to get all Products under in a productGroups");
		List<ProductProfile> productList = new ArrayList<>();
		productList = productGroupProductRepository.findProductByProductGroupPid(productGroupPid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupDTO> findProductGroupByProductPid(String productPid) {
		log.debug("Request to get all assigned ProductGroups in a product");
		List<EcomProductGroup> productList = productGroupProductRepository.findProductGroupByProductPid(productPid);
		List<EcomProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findByProductGroupPids(List<String> groupPids) {
		List<ProductProfile> productList = productGroupProductRepository.findProductByProductGroupPidIn(groupPids);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findByProductGroupPidsAndCategoryPids(List<String> groupPids,
			List<String> categoryPids) {
		List<ProductProfile> productList = productGroupProductRepository
				.findProductByProductGroupPidInAndProductCategoryPidIn(groupPids, categoryPids);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	/**
	 * Get all the products.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupProduct> findAllByCompany() {
		log.debug("Request to get all Products");
		List<EcomProductGroupProduct> productGroupProducts = productGroupProductRepository.findAllByCompanyId();
		return productGroupProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUser() {
		List<EcomProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<EcomProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		for (EcomProductGroup productGroup : productGroups) {
			EcomProductGroupProductDTO productGroupProductDTO = new EcomProductGroupProductDTO();
			productGroupProductDTO.setProductGroupDTO(productGroupMapper.productGroupToProductGroupDTO(productGroup));
			// get Products by productGroup
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPid(productGroup.getPid());
			productGroupProductDTO
					.setProductProfiles(productProfileMapper.productProfilesToProductProfileDTOs(productProfiles));
			if (!productProfiles.isEmpty()) {
				productGroupProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
			}
			productGroupProducts.add(productGroupProductDTO);
		}
		return productGroupProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findAllByProductProfilePid(String productProfilePid) {
		List<ProductProfile> productList = productGroupProductRepository
				.findAllProductProfileByProductProfilePid(productProfilePid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductGroupPids, CategoryPids And
	 *        ActivatedProductProfile.
	 * @param categoryPids the categoryPids of the entity
	 * @param groupPids    the groupPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductProfileDTO> findByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids) {
		List<ProductProfile> productList = productGroupProductRepository
				.findProductByProductGroupPidInAndProductCategoryPidInAndActivated(groupPids, categoryPids, true);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		if (productNameTextSettings.size() > 0) {
			for (ProductProfileDTO productProfileDTO : result) {
				String name = " (";
				for (ProductNameTextSettings productNameText : productNameTextSettings) {
					if (productNameText.getName().equals("DESCRIPTION")) {
						if (productProfileDTO.getDescription() != null && !productProfileDTO.getDescription().isEmpty())
							name += productProfileDTO.getDescription() + ",";
					} else if (productNameText.getName().equals("MRP")) {
						name += productProfileDTO.getMrp() + ",";
					} else if (productNameText.getName().equals("SELLING RATE")) {
						name += productProfileDTO.getPrice() + ",";
					} else if (productNameText.getName().equals("STOCK")) {
						OpeningStock openingStock = openingStockRepository
								.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
						if (openingStock != null) {
							name += openingStock.getQuantity() + ",";
						}
					} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
						if (productProfileDTO.getProductDescription() != null
								&& !productProfileDTO.getProductDescription().isEmpty())
							name += productProfileDTO.getProductDescription() + ",";
					} else if (productNameText.getName().equals("BARCODE")) {
						if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
							name += productProfileDTO.getBarcode() + ",";
					} else if (productNameText.getName().equals("REMARKS")) {
						if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
							name += productProfileDTO.getRemarks() + ",";
					}
				}
				name = name.substring(0, name.length() - 1);
				if (name.length() > 1) {
					name += ")";
				}
				productProfileDTO.setName(productProfileDTO.getName() + name);
			}
		}
		return result;
	}


	@Override
	public List<ProductProfileDTO> findByProductGroupPidsAndActivated(List<String> groupPids) {
		List<ProductProfile> productList = productGroupProductRepository
				.findProductByProductGroupPidInAndActivated(groupPids, true);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		if (productNameTextSettings.size() > 0) {
			for (ProductProfileDTO productProfileDTO : result) {
				String name = " (";
				for (ProductNameTextSettings productNameText : productNameTextSettings) {
					if (productNameText.getName().equals("DESCRIPTION")) {
						if (productProfileDTO.getDescription() != null && !productProfileDTO.getDescription().isEmpty())
							name += productProfileDTO.getDescription() + ",";
					} else if (productNameText.getName().equals("MRP")) {
						name += productProfileDTO.getMrp() + ",";
					} else if (productNameText.getName().equals("SELLING RATE")) {
						name += productProfileDTO.getPrice() + ",";
					} else if (productNameText.getName().equals("STOCK")) {
						OpeningStock openingStock = openingStockRepository
								.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
						if (openingStock != null) {
							name += openingStock.getQuantity() + ",";
						}
					} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
						if (productProfileDTO.getProductDescription() != null
								&& !productProfileDTO.getProductDescription().isEmpty())
							name += productProfileDTO.getProductDescription() + ",";
					} else if (productNameText.getName().equals("BARCODE")) {
						if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
							name += productProfileDTO.getBarcode() + ",";
					} else if (productNameText.getName().equals("REMARKS")) {
						if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
							name += productProfileDTO.getRemarks() + ",";
					}
				}
				name = name.substring(0, name.length() - 1);
				if (name.length() > 1) {
					name += ")";
				}
				productProfileDTO.setName(productProfileDTO.getName() + name);
			}
		}
		return result;
	}


	@Override
	public List<EcomProductGroupProduct> findAllByCompanyAndActivated(boolean active) {
		log.debug("Request to get all Products activated");
		List<EcomProductGroupProduct> productGroupProducts = productGroupProductRepository
				.findAllByCompanyIdAndActivated(active);
		return productGroupProducts;
	}


	@Override
	public List<EcomProductGroupProduct> findProductGroupProductByProductCategoryPidsAndActivated(
			List<String> categoryPids) {
		List<EcomProductGroupProduct> productProfileProductList = productGroupProductRepository
				.findProductGroupProductByProductCategoryPidInAndActivated(categoryPids, true);
		return productProfileProductList;
	}


	@Override
	public List<EcomProductGroupProduct> findProductGroupProductByProductGroupPidsAndActivated(List<String> groupPids) {
		List<EcomProductGroupProduct> productList = productGroupProductRepository
				.findProductGroupProductByProductGroupPidInAndActivated(groupPids, true);
		return productList;
	}


	@Override
	public List<EcomProductGroupProduct> findProductGroupProductByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids) {
		List<EcomProductGroupProduct> productList = productGroupProductRepository
				.findProductGroupProductByProductGroupPidInAndProductCategoryPidIn(groupPids, categoryPids);
		return productList;
	}


	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(
			boolean activted) {
		List<EcomProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<EcomProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(activted);
		for (EcomProductGroup productGroup : productGroups) {
			EcomProductGroupProductDTO productGroupProductDTO = new EcomProductGroupProductDTO();
			productGroupProductDTO.setProductGroupDTO(productGroupMapper.productGroupToProductGroupDTO(productGroup));
			// get Products by productGroup
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPidAndActivated(productGroup.getPid(), true);

			List<ProductProfileDTO> productProfileDTOs = productProfiles.stream().map(ProductProfileDTO::new)
					.collect(Collectors.toList());

			// set description to productProfile
			List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
					.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
			Set<Long> sLocationIds = documentStockLocationSourceRepository.findStockLocationIdsByCompanyId();
			if (sLocationIds == null || sLocationIds.size() == 0) {
				throw new IllegalArgumentException("Document Stock Location not assigned");
			}
			List<OpeningStock> openingStocks = openingStockRepository
					.findOpeningStocksAndStockLocationIdIn(sLocationIds);
			// log.info("Caluclationg op Stock");
			if (productNameTextSettings.size() > 0) {
				for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
					String name = " (";
					for (ProductNameTextSettings productNameText : productNameTextSettings) {
						if (productNameText.getName().equals("DESCRIPTION")) {
							if (productProfileDTO.getDescription() != null
									&& !productProfileDTO.getDescription().isEmpty())
								name += "DES:" + productProfileDTO.getDescription() + ",";
						} else if (productNameText.getName().equals("MRP")) {
							name += "MRP:" + productProfileDTO.getMrp() + ",";
						} else if (productNameText.getName().equals("SELLING RATE")) {
							name += "SRATE:" + productProfileDTO.getPrice() + ",";
						} else if (productNameText.getName().equals("STOCK")) {
//							OpeningStock openingStock = openingStockRepository
//									.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
//							if (openingStock != null) {
//								name +="STK:"+ openingStock.getQuantity() + ",";
//							}
							/*
							 * List<OpeningStock> openingStocks = new ArrayList<>(); openingStocks =
							 * openingStockRepository
							 * .findByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
							 */
							// double quantity = 0.0;

							if (sLocationIds.isEmpty()) {
								OpeningStock openingStock = openingStockRepository
										.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
								if (openingStock != null) {
									name += "STK:" + openingStock.getQuantity() + ",";
								}
							} else {
								Double sum = openingStocks.stream().filter(
										op -> op.getProductProfile().getPid().equals(productProfileDTO.getPid()))
										.mapToDouble(OpeningStock::getQuantity).sum();
								/*
								 * Double sum =
								 * openingStockRepository.findSumOpeningStockByProductPidAndStockLocationIdIn(
								 * productProfileDTO.getPid(), sLocationIds);
								 */
								name += "STK:" + sum + ",";
							}

//							
//							
//							
//							if(!openingStocks.isEmpty() ) {
//								quantity = openingStocks.stream().mapToDouble(op -> op.getQuantity()).sum();
//							}
//							
//							
//							if (quantity != 0.0) {
//								name +="STK:"+ quantity + ",";
//							}
						} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
							if (productProfileDTO.getProductDescription() != null
									&& !productProfileDTO.getProductDescription().isEmpty())
								name += productProfileDTO.getProductDescription() + ",";
						} else if (productNameText.getName().equals("BARCODE")) {
							if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
								name += productProfileDTO.getBarcode() + ",";
						} else if (productNameText.getName().equals("REMARKS")) {
							if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
								name += productProfileDTO.getRemarks() + ",";
						}
					}
					name = name.substring(0, name.length() - 1);
					if (name.length() > 1) {
						name += ")";
					}
					productProfileDTO.setName(productProfileDTO.getName() + name);
				}
			}

			// log.info("Caluclationg op Stock Completed");

			productGroupProductDTO.setProductProfiles(productProfileDTOs);
			if (!productProfiles.isEmpty()) {
				productGroupProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
			}
			productGroupProducts.add(productGroupProductDTO);
		}
		return productGroupProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
			boolean activted, LocalDateTime lastModifiedDate) {
		List<EcomProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<EcomProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(activted);
		if (productGroups.isEmpty()) {
			return productGroupProducts;
		}
		List<String> groupPids = productGroups.stream().map(EcomProductGroup::getPid).collect(Collectors.toList());
		List<EcomProductGroupProduct> allProductGroupProducts = productGroupProductRepository
				.findByProductGroupPidInAndActivatedAndLastModifiedDateAndProductGroupLastModified(groupPids, true,
						lastModifiedDate);
		Map<String, List<EcomProductGroupProduct>> productGroupProductMap = allProductGroupProducts.parallelStream()
				.collect(Collectors.groupingBy(pl -> pl.getProductGroup().getName()));
		for (Map.Entry<String, List<EcomProductGroupProduct>> entry : productGroupProductMap.entrySet()) {
			Set<ProductProfile> productProfiles = new HashSet<>();
			for (EcomProductGroupProduct productGroupProduct : entry.getValue()) {
				productProfiles.add(productGroupProduct.getProduct());
			}
			EcomProductGroupProductDTO productGroupProductDTO = new EcomProductGroupProductDTO();
			productGroupProductDTO.setProductGroupDTO(
					productGroupMapper.productGroupToProductGroupDTO(entry.getValue().get(0).getProductGroup()));
			// get Products by productGroup
			List<ProductProfileDTO> productProfileDTOs = productProfileMapper
					.productProfilesToProductProfileDTOs(new ArrayList<>(productProfiles));
			// set description to productProfile
			List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
					.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
			if (productNameTextSettings.size() > 0) {
				for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
					String name = " (";
					for (ProductNameTextSettings productNameText : productNameTextSettings) {
						if (productNameText.getName().equals("DESCRIPTION")) {
							if (productProfileDTO.getDescription() != null
									&& !productProfileDTO.getDescription().isEmpty())
								name += productProfileDTO.getDescription() + ",";
						} else if (productNameText.getName().equals("MRP")) {
							name += productProfileDTO.getMrp() + ",";
						} else if (productNameText.getName().equals("SELLING RATE")) {
							name += productProfileDTO.getPrice() + ",";
						} else if (productNameText.getName().equals("STOCK")) {
							OpeningStock openingStock = openingStockRepository
									.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
							if (openingStock != null) {
								name += openingStock.getQuantity() + ",";
							}
						} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
							if (productProfileDTO.getProductDescription() != null
									&& !productProfileDTO.getProductDescription().isEmpty())
								name += productProfileDTO.getProductDescription() + ",";
						} else if (productNameText.getName().equals("BARCODE")) {
							if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
								name += productProfileDTO.getBarcode() + ",";
						} else if (productNameText.getName().equals("REMARKS")) {
							if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
								name += productProfileDTO.getRemarks() + ",";
						}
					}
					name = name.substring(0, name.length() - 1);
					if (name.length() > 1) {
						name += ")";
					}
					productProfileDTO.setName(productProfileDTO.getName() + name);
				}
			}
			productGroupProductDTO.setProductProfiles(productProfileDTOs);
			productGroupProducts.add(productGroupProductDTO);
		}
		return productGroupProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(
			String productGroupPid, boolean activated, StockAvailabilityStatus status) {
		log.debug("Request to get all Products by productGroupPid and activated and StockAvailabilityStatus");

		List<ProductProfile> productList = new ArrayList<>();
		List<EcomProductProfile> ecomProductProfiles = productGroupEcomProductsRepository
				.findEcomProductByProductGroupPid(productGroupPid);
		if (ecomProductProfiles.isEmpty()) {
			productList = productGroupProductRepository
					.findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(productGroupPid, activated,
							status);
		} else {
			// if ecom productgroup, then find productprofiles from
			// ecomProductProfileProduct
			Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid)
					.collect(Collectors.toSet());
			productList = ecomProductProfileProductRepository.findProductByEcomProductProfilePidInAndActivatedAndStatus(
					ecomProductProfilesPids, activated, status);
		}

		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status) {
		log.debug("Request to get all Products activated and StockAvailabilityStatus");
		List<ProductProfile> productList = productGroupProductRepository
				.findProductByActivatedAndStockAvailabilityStatus(activated, status);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findEcomProductProfileByProductGroupPid(String productGroupPid) {
		List<ProductProfile> productList = new ArrayList<>();
		List<EcomProductProfile> ecomProductProfiles = productGroupEcomProductsRepository
				.findEcomProductByProductGroupPid(productGroupPid);
		if (ecomProductProfiles.isEmpty()) {
			productList = productGroupProductRepository.findProductByProductGroupPid(productGroupPid);
		} else {
			// if ecom productgroup, then find productprofiles from
			// ecomProductProfileProduct
			Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid)
					.collect(Collectors.toSet());
			productList = ecomProductProfileProductRepository
					.findProductByEcomProductProfilePidIn(ecomProductProfilesPids);
		}
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<String> findProductByProductProfilePidAndProductGroupPidAndProductCategoryPidAndActivated(
			List<String> productProfilePids, List<String> productGroupPids, List<String> productCategoryPids) {
		List<String> productList = productGroupProductRepository
				.findProductByProductPidInAndProductGroupPidInAndProductProductCategoryPidInAndActivated(
						productProfilePids, productGroupPids, productCategoryPids, true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

	@Override
	public List<String> findProductByProductProfilePidAndProductCategoryPidAndActivated(List<String> productProfilePids,
			List<String> productCategoryPids) {
		List<String> productList = productGroupProductRepository
				.findProductByProductPidInAndProductProductCategoryPidInAndActivated(productProfilePids,
						productCategoryPids, true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

	@Override
	public List<String> findProductByProductProfilePidAndProductGroupPidAndActivated(List<String> productProfilePids,
			List<String> productGroupPids) {
		List<String> productList = productGroupProductRepository
				.findProductByProductPidInAndProductGroupPidInAndActivated(productProfilePids, productGroupPids, true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

}
