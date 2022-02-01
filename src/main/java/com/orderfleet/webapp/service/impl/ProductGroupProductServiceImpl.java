package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing ProductGroupProduct.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
@Service
@Transactional
public class ProductGroupProductServiceImpl implements ProductGroupProductService {
	private final Logger log = LoggerFactory.getLogger(ProductGroupProductServiceImpl.class);

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private ProductProfileRepository productRepository;

	@Inject
	private ProductProfileMapper productMapper;

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper productGroupMapper;

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
		ProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
		for (String productPid : products) {
			ProductProfile product = productRepository.findOneByPid(productPid).get();
			productGroupProducts.add(new ProductGroupProduct(product, productGroup, company));
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
	public List<ProductGroupDTO> findProductGroupByProductPid(String productPid) {
		log.debug("Request to get all assigned ProductGroups in a product");
		List<ProductGroup> productList = productGroupProductRepository.findProductGroupByProductPid(productPid);
		List<ProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productList);
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
	public List<ProductGroupProduct> findAllByCompany() {
		log.debug("Request to get all Products");
		List<ProductGroupProduct> productGroupProducts = productGroupProductRepository.findAllByCompanyId();
		return productGroupProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUser() {
		List<ProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		for (ProductGroup productGroup : productGroups) {
			ProductGroupProductDTO productGroupProductDTO = new ProductGroupProductDTO();
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
							productProfileDTO.setStockQty(openingStock.getQuantity());
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

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductGroupPids And Activated
	 * @param groupPids the groupPids of the entity
	 * @return the list
	 */
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
							productProfileDTO.setStockQty(openingStock.getQuantity());
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

	/**
	 * @author Fahad
	 * @since Feb 28, 2017
	 *
	 *        find ProductGroupProduct by CompanyId And Activated
	 * @param active the active of the entity
	 * @return the list of ProductGroupProduct
	 */
	@Override
	public List<ProductGroupProduct> findAllByCompanyAndActivated(boolean active) {
		log.debug("Request to get all Products activated");
		List<ProductGroupProduct> productGroupProducts = productGroupProductRepository
				.findAllByCompanyIdAndActivated(active);
		return productGroupProducts;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductCategoryPids
	 * @param categoryPids the categoryPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductGroupProduct> findProductGroupProductByProductCategoryPidsAndActivated(
			List<String> categoryPids) {
		List<ProductGroupProduct> productProfileProductList = productGroupProductRepository
				.findProductGroupProductByProductCategoryPidInAndActivated(categoryPids, true);
		return productProfileProductList;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductGroupPids
	 * @param groupPids the groupPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductGroupProduct> findProductGroupProductByProductGroupPidsAndActivated(List<String> groupPids) {
		List<ProductGroupProduct> productList = productGroupProductRepository
				.findProductGroupProductByProductGroupPidInAndActivated(groupPids, true);
		return productList;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductGroupPids And CategoryPids
	 * @param groupPids    the groupPids of the entity
	 * @param categoryPids the categoryPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductGroupProduct> findProductGroupProductByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids) {
		List<ProductGroupProduct> productList = productGroupProductRepository
				.findProductGroupProductByProductGroupPidInAndProductCategoryPidIn(groupPids, categoryPids);
		return productList;
	}

	// @Override
	// @Transactional(readOnly = true)
	// public List<ProductGroupProductDTO>
	// findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(
	// boolean activted) {
	// List<ProductGroupProductDTO> productGroupProducts = new ArrayList<>();
	// // get user productGroups
	// List<ProductGroup> productGroups = userProductGroupRepository
	// .findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(activted);
	// for (ProductGroup productGroup : productGroups) {
	// ProductGroupProductDTO productGroupProductDTO = new
	// ProductGroupProductDTO();
	// productGroupProductDTO.setProductGroupDTO(productGroupMapper.productGroupToProductGroupDTO(productGroup));
	// // get Products by productGroup
	// List<ProductProfile> productProfiles = productGroupProductRepository
	// .findProductByProductGroupPid(productGroup.getPid());
	// productGroupProductDTO
	// .setProductProfiles(productProfileMapper.productProfilesToProductProfileDTOs(productProfiles));
	// if (!productProfiles.isEmpty()) {
	// productGroupProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
	// }
	// productGroupProducts.add(productGroupProductDTO);
	// }
	// return productGroupProducts;
	// }

	public boolean getCompanyCofig() {
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if (optconfig.isPresent()) {
			if (Boolean.valueOf(optconfig.get().getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(
			boolean activted) {
		List<ProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<ProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(activted);

		boolean companyconfig = getCompanyCofig();

		for (ProductGroup productGroup : productGroups) {
			ProductGroupProductDTO productGroupProductDTO = new ProductGroupProductDTO();

			if (companyconfig) {
				productGroupProductDTO
						.setProductGroupDTO(productGroupMapper.productGroupToProductGroupDTODescription(productGroup));
			} else {
				productGroupProductDTO
						.setProductGroupDTO(productGroupMapper.productGroupToProductGroupDTO(productGroup));
			}
			// get Products by productGroup
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPidAndActivated(productGroup.getPid(), true);

			List<ProductProfileDTO> productProfileDTOs = productMapper
					.productProfilesToProductProfileDTOs(productProfiles);

//			List<ProductProfileDTO> productProfileDTOs = productProfiles.stream().map(ProductProfileDTO::new)
//					.collect(Collectors.toList());

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
									productProfileDTO.setStockQty(openingStock.getQuantity());
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
								productProfileDTO.setStockQty(sum);
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
	public List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
			boolean activted, LocalDateTime lastModifiedDate) {
		List<ProductGroupProductDTO> productGroupProducts = new ArrayList<>();
		// get user productGroups
		List<ProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(activted);
		if (productGroups.isEmpty()) {
			return productGroupProducts;
		}
		List<String> groupPids = productGroups.stream().map(ProductGroup::getPid).collect(Collectors.toList());
		List<ProductGroupProduct> allProductGroupProducts = productGroupProductRepository
				.findByProductGroupPidInAndActivatedAndLastModifiedDateAndProductGroupLastModified(groupPids, true,
						lastModifiedDate);
		boolean companyconfig = getCompanyCofig();
		Map<String, List<ProductGroupProduct>> productGroupProductMap = allProductGroupProducts.parallelStream()
				.collect(Collectors.groupingBy(pl -> pl.getProductGroup().getName()));
		for (Map.Entry<String, List<ProductGroupProduct>> entry : productGroupProductMap.entrySet()) {
			Set<ProductProfile> productProfiles = new HashSet<>();
			for (ProductGroupProduct productGroupProduct : entry.getValue()) {
				productProfiles.add(productGroupProduct.getProduct());
			}
			ProductGroupProductDTO productGroupProductDTO = new ProductGroupProductDTO();
			if (companyconfig) {
				productGroupProductDTO.setProductGroupDTO(productGroupMapper
						.productGroupToProductGroupDTODescription(entry.getValue().get(0).getProductGroup()));
			} else {
				productGroupProductDTO.setProductGroupDTO(
						productGroupMapper.productGroupToProductGroupDTO(entry.getValue().get(0).getProductGroup()));
			}

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
								productProfileDTO.setStockQty(openingStock.getQuantity());
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
