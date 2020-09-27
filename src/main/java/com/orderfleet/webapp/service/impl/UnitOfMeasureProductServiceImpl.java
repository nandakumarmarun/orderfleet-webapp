package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UnitOfMeasureProductService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureProductDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing UnitOfMeasureProduct.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
@Service
@Transactional
public class UnitOfMeasureProductServiceImpl implements UnitOfMeasureProductService {
	private final Logger log = LoggerFactory.getLogger(UnitOfMeasureProductServiceImpl.class);

	@Inject
	private UnitOfMeasureRepository unitOfMeasureRepository;

	@Inject
	private UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	@Inject
	private ProductProfileRepository productRepository;

	@Inject
	private ProductProfileMapper productMapper;

	@Inject
	private ProductProfileMapper productProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Override
	public void save(String unitOfMeasurePid, String assignedProducts) {
		log.debug("Request to save UnitOfMeasure Products");
		UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findOneByPid(unitOfMeasurePid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<UnitOfMeasureProduct> unitOfMeasureProducts = new ArrayList<>();
		for (String productPid : products) {
			ProductProfile product = productRepository.findOneByPid(productPid).get();
			unitOfMeasureProducts.add(new UnitOfMeasureProduct(product, unitOfMeasure, company));
		}
		unitOfMeasureProductRepository.deleteByUnitOfMeasurePid(unitOfMeasurePid);
		unitOfMeasureProductRepository.save(unitOfMeasureProducts);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductByUnitOfMeasurePid(String unitOfMeasurePid) {
		log.debug("Request to get all Products under in a unitOfMeasures");
		List<ProductProfile> productList = new ArrayList<>();
		productList = unitOfMeasureProductRepository.findProductByUnitOfMeasurePid(unitOfMeasurePid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitOfMeasureDTO> findUnitOfMeasureByProductPid(String productPid) {
		log.debug("Request to get all assigned UnitOfMeasures in a product");
		List<UnitOfMeasure> productList = unitOfMeasureProductRepository.findUnitOfMeasureByProductPid(productPid);
		List<UnitOfMeasureDTO> result = unitOfMeasuresToUnitOfMeasureDTOs(productList);
		return result;
	}

	private UnitOfMeasure unitOfMeasureDTOToUnitOfMeasure(UnitOfMeasureDTO unitOfMeasureDTO) {
		if (unitOfMeasureDTO == null) {
			return null;
		}

		UnitOfMeasure unitOfMeasure = new UnitOfMeasure();

		unitOfMeasure.setPid(unitOfMeasureDTO.getPid());
		unitOfMeasure.setName(unitOfMeasureDTO.getName());
		unitOfMeasure.setAlias(unitOfMeasureDTO.getAlias());
		unitOfMeasure.setDescription(unitOfMeasureDTO.getDescription());
		unitOfMeasure.setUomId(unitOfMeasureDTO.getUomId());

		unitOfMeasure.setActivated(unitOfMeasureDTO.getActivated());

		return unitOfMeasure;
	}

	private List<UnitOfMeasureDTO> unitOfMeasuresToUnitOfMeasureDTOs(List<UnitOfMeasure> unitOfMeasures) {
		if (unitOfMeasures == null) {
			return null;
		}

		List<UnitOfMeasureDTO> list = new ArrayList<UnitOfMeasureDTO>();
		for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
			list.add(unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure));
		}

		return list;
	}

	private UnitOfMeasureDTO unitOfMeasureToUnitOfMeasureDTO(UnitOfMeasure unitOfMeasure) {
		UnitOfMeasureDTO unitOfMeasureDTO = new UnitOfMeasureDTO();

		unitOfMeasureDTO.setPid(unitOfMeasure.getPid());
		unitOfMeasureDTO.setName(unitOfMeasure.getName());
		unitOfMeasureDTO.setAlias(unitOfMeasure.getAlias());
		unitOfMeasureDTO.setDescription(unitOfMeasure.getDescription());
		unitOfMeasureDTO.setLastModifiedDate(unitOfMeasure.getLastModifiedDate());
		unitOfMeasureDTO.setActivated(unitOfMeasure.getActivated());
		unitOfMeasureDTO.setUomId(unitOfMeasure.getUomId());

		return unitOfMeasureDTO;
	}

	@Override
	public List<ProductProfileDTO> findByUnitOfMeasurePids(List<String> groupPids) {
		List<ProductProfile> productList = unitOfMeasureProductRepository.findProductByUnitOfMeasurePidIn(groupPids);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findByUnitOfMeasurePidsAndCategoryPids(List<String> groupPids,
			List<String> categoryPids) {
		List<ProductProfile> productList = unitOfMeasureProductRepository
				.findProductByUnitOfMeasurePidInAndProductCategoryPidIn(groupPids, categoryPids);
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
	public List<UnitOfMeasureProduct> findAllByCompany() {
		log.debug("Request to get all Products");
		List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();
		return unitOfMeasureProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUser() {
		List<UnitOfMeasureProductDTO> unitOfMeasureProducts = new ArrayList<>();
		// get user unitOfMeasures
//		List<UnitOfMeasure> unitOfMeasures = userUnitOfMeasureRepository.findUnitOfMeasuresByUserIsCurrentUser();
//		for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
//			UnitOfMeasureProductDTO unitOfMeasureProductDTO = new UnitOfMeasureProductDTO();
//			unitOfMeasureProductDTO
//					.setUnitOfMeasureDTO(unitOfMeasureMapper.unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure));
//			// get Products by unitOfMeasure
//			List<ProductProfile> productProfiles = unitOfMeasureProductRepository
//					.findProductByUnitOfMeasurePid(unitOfMeasure.getPid());
//			unitOfMeasureProductDTO
//					.setProductProfiles(productProfileMapper.productProfilesToProductProfileDTOs(productProfiles));
//			if (!productProfiles.isEmpty()) {
//				unitOfMeasureProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
//			}
//			unitOfMeasureProducts.add(unitOfMeasureProductDTO);
//		}
		return unitOfMeasureProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findAllByProductProfilePid(String productProfilePid) {
		List<ProductProfile> productList = unitOfMeasureProductRepository
				.findAllProductProfileByProductProfilePid(productProfilePid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by UnitOfMeasurePids, CategoryPids And
	 *        ActivatedProductProfile.
	 * @param categoryPids the categoryPids of the entity
	 * @param groupPids    the groupPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductProfileDTO> findByUnitOfMeasurePidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids) {
		List<ProductProfile> productList = unitOfMeasureProductRepository
				.findProductByUnitOfMeasurePidInAndProductCategoryPidInAndActivated(groupPids, categoryPids, true);
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

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by UnitOfMeasurePids And Activated
	 * @param groupPids the groupPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductProfileDTO> findByUnitOfMeasurePidsAndActivated(List<String> groupPids) {
		List<ProductProfile> productList = unitOfMeasureProductRepository
				.findProductByUnitOfMeasurePidInAndActivated(groupPids, true);
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

	/**
	 * @author Fahad
	 * @since Feb 28, 2017
	 *
	 *        find UnitOfMeasureProduct by CompanyId And Activated
	 * @param active the active of the entity
	 * @return the list of UnitOfMeasureProduct
	 */
	@Override
	public List<UnitOfMeasureProduct> findAllByCompanyAndActivated(boolean active) {
		log.debug("Request to get all Products activated");
		List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository
				.findAllByCompanyIdAndActivated(active);
		return unitOfMeasureProducts;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by ProductCategoryPids
	 * @param categoryPids the categoryPids of the entity
	 * @return the list
	 */
	@Override
	public List<UnitOfMeasureProduct> findUnitOfMeasureProductByProductCategoryPidsAndActivated(
			List<String> categoryPids) {
		List<UnitOfMeasureProduct> productProfileProductList = unitOfMeasureProductRepository
				.findUnitOfMeasureProductByProductCategoryPidInAndActivated(categoryPids, true);
		return productProfileProductList;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by UnitOfMeasurePids
	 * @param groupPids the groupPids of the entity
	 * @return the list
	 */
	@Override
	public List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidsAndActivated(List<String> groupPids) {
		List<UnitOfMeasureProduct> productList = unitOfMeasureProductRepository
				.findUnitOfMeasureProductByUnitOfMeasurePidInAndActivated(groupPids, true);
		return productList;
	}

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by UnitOfMeasurePids And CategoryPids
	 * @param groupPids    the groupPids of the entity
	 * @param categoryPids the categoryPids of the entity
	 * @return the list
	 */
	@Override
	public List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids) {
		List<UnitOfMeasureProduct> productList = unitOfMeasureProductRepository
				.findUnitOfMeasureProductByUnitOfMeasurePidInAndProductCategoryPidIn(groupPids, categoryPids);
		return productList;
	}

	// @Override
	// @Transactional(readOnly = true)
	// public List<UnitOfMeasureProductDTO>
	// findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUserAndUnitOfMeasureActivated(
	// boolean activted) {
	// List<UnitOfMeasureProductDTO> unitOfMeasureProducts = new ArrayList<>();
	// // get user unitOfMeasures
	// List<UnitOfMeasure> unitOfMeasures = userUnitOfMeasureRepository
	// .findUnitOfMeasuresByUserIsCurrentUserAndUnitOfMeasuresActivated(activted);
	// for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
	// UnitOfMeasureProductDTO unitOfMeasureProductDTO = new
	// UnitOfMeasureProductDTO();
	// unitOfMeasureProductDTO.setUnitOfMeasureDTO(unitOfMeasureMapper.unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure));
	// // get Products by unitOfMeasure
	// List<ProductProfile> productProfiles = unitOfMeasureProductRepository
	// .findProductByUnitOfMeasurePid(unitOfMeasure.getPid());
	// unitOfMeasureProductDTO
	// .setProductProfiles(productProfileMapper.productProfilesToProductProfileDTOs(productProfiles));
	// if (!productProfiles.isEmpty()) {
	// unitOfMeasureProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
	// }
	// unitOfMeasureProducts.add(unitOfMeasureProductDTO);
	// }
	// return unitOfMeasureProducts;
	// }

	@Override
	@Transactional(readOnly = true)
	public List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUserAndUnitOfMeasureActivated(
			boolean activted) {
		List<UnitOfMeasureProductDTO> unitOfMeasureProducts = new ArrayList<>();
		// get user unitOfMeasures
//		List<UnitOfMeasure> unitOfMeasures = userUnitOfMeasureRepository
//				.findUnitOfMeasuresByUserIsCurrentUserAndUnitOfMeasuresActivated(activted);
//		for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
//			UnitOfMeasureProductDTO unitOfMeasureProductDTO = new UnitOfMeasureProductDTO();
//			unitOfMeasureProductDTO.setUnitOfMeasureDTO(unitOfMeasureMapper.unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure));
//			// get Products by unitOfMeasure
//			List<ProductProfile> productProfiles = unitOfMeasureProductRepository
//					.findProductByUnitOfMeasurePidAndActivated(unitOfMeasure.getPid(), true);
//
//			List<ProductProfileDTO> productProfileDTOs = productProfiles.stream().map(ProductProfileDTO::new)
//					.collect(Collectors.toList());
//
//			// set description to productProfile
//			List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
//					.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
//			Set<Long> sLocationIds = documentStockLocationSourceRepository.findStockLocationIdsByCompanyId();
//			if (sLocationIds == null || sLocationIds.size() == 0) {
//				throw new IllegalArgumentException("Document Stock Location not assigned");
//			}
//			List<OpeningStock> openingStocks = openingStockRepository
//					.findOpeningStocksAndStockLocationIdIn(sLocationIds);
//			// log.info("Caluclationg op Stock");
//			if (productNameTextSettings.size() > 0) {
//				for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
//					String name = " (";
//					for (ProductNameTextSettings productNameText : productNameTextSettings) {
//						if (productNameText.getName().equals("DESCRIPTION")) {
//							if (productProfileDTO.getDescription() != null
//									&& !productProfileDTO.getDescription().isEmpty())
//								name += "DES:" + productProfileDTO.getDescription() + ",";
//						} else if (productNameText.getName().equals("MRP")) {
//							name += "MRP:" + productProfileDTO.getMrp() + ",";
//						} else if (productNameText.getName().equals("SELLING RATE")) {
//							name += "SRATE:" + productProfileDTO.getPrice() + ",";
//						} else if (productNameText.getName().equals("STOCK")) {
////							OpeningStock openingStock = openingStockRepository
////									.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
////							if (openingStock != null) {
////								name +="STK:"+ openingStock.getQuantity() + ",";
////							}
//							/*
//							 * List<OpeningStock> openingStocks = new ArrayList<>(); openingStocks =
//							 * openingStockRepository
//							 * .findByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
//							 */
//							// double quantity = 0.0;
//
//							if (sLocationIds.isEmpty()) {
//								OpeningStock openingStock = openingStockRepository
//										.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
//								if (openingStock != null) {
//									name += "STK:" + openingStock.getQuantity() + ",";
//								}
//							} else {
//								Double sum = openingStocks.stream().filter(
//										op -> op.getProductProfile().getPid().equals(productProfileDTO.getPid()))
//										.mapToDouble(OpeningStock::getQuantity).sum();
//								/*
//								 * Double sum =
//								 * openingStockRepository.findSumOpeningStockByProductPidAndStockLocationIdIn(
//								 * productProfileDTO.getPid(), sLocationIds);
//								 */
//								name += "STK:" + sum + ",";
//							}
//
////							
////							
////							
////							if(!openingStocks.isEmpty() ) {
////								quantity = openingStocks.stream().mapToDouble(op -> op.getQuantity()).sum();
////							}
////							
////							
////							if (quantity != 0.0) {
////								name +="STK:"+ quantity + ",";
////							}
//						} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
//							if (productProfileDTO.getProductDescription() != null
//									&& !productProfileDTO.getProductDescription().isEmpty())
//								name += productProfileDTO.getProductDescription() + ",";
//						} else if (productNameText.getName().equals("BARCODE")) {
//							if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
//								name += productProfileDTO.getBarcode() + ",";
//						} else if (productNameText.getName().equals("REMARKS")) {
//							if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
//								name += productProfileDTO.getRemarks() + ",";
//						}
//					}
//					name = name.substring(0, name.length() - 1);
//					if (name.length() > 1) {
//						name += ")";
//					}
//					productProfileDTO.setName(productProfileDTO.getName() + name);
//				}
//			}
//
//			// log.info("Caluclationg op Stock Completed");
//
//			unitOfMeasureProductDTO.setProductProfiles(productProfileDTOs);
//			if (!productProfiles.isEmpty()) {
//				unitOfMeasureProductDTO.setLastModifiedDate(productProfiles.get(0).getLastModifiedDate());
//			}
//			unitOfMeasureProducts.add(unitOfMeasureProductDTO);
//		}
		return unitOfMeasureProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUserAndUnitOfMeasureActivatedAndModifiedDate(
			boolean activted, LocalDateTime lastModifiedDate) {
		List<UnitOfMeasureProductDTO> unitOfMeasureProducts = new ArrayList<>();
		// get user unitOfMeasures
//		List<UnitOfMeasure> unitOfMeasures = userUnitOfMeasureRepository
//				.findUnitOfMeasuresByUserIsCurrentUserAndUnitOfMeasuresActivated(activted);
//		if (unitOfMeasures.isEmpty()) {
//			return unitOfMeasureProducts;
//		}
//		List<String> groupPids = unitOfMeasures.stream().map(UnitOfMeasure::getPid).collect(Collectors.toList());
//		List<UnitOfMeasureProduct> allUnitOfMeasureProducts = unitOfMeasureProductRepository
//				.findByUnitOfMeasurePidInAndActivatedAndLastModifiedDateAndUnitOfMeasureLastModified(groupPids, true,
//						lastModifiedDate);
//		Map<String, List<UnitOfMeasureProduct>> unitOfMeasureProductMap = allUnitOfMeasureProducts.parallelStream()
//				.collect(Collectors.groupingBy(pl -> pl.getUnitOfMeasure().getName()));
//		for (Map.Entry<String, List<UnitOfMeasureProduct>> entry : unitOfMeasureProductMap.entrySet()) {
//			Set<ProductProfile> productProfiles = new HashSet<>();
//			for (UnitOfMeasureProduct unitOfMeasureProduct : entry.getValue()) {
//				productProfiles.add(unitOfMeasureProduct.getProduct());
//			}
//			UnitOfMeasureProductDTO unitOfMeasureProductDTO = new UnitOfMeasureProductDTO();
//			unitOfMeasureProductDTO.setUnitOfMeasureDTO(
//					unitOfMeasureMapper.unitOfMeasureToUnitOfMeasureDTO(entry.getValue().get(0).getUnitOfMeasure()));
//			// get Products by unitOfMeasure
//			List<ProductProfileDTO> productProfileDTOs = productProfileMapper
//					.productProfilesToProductProfileDTOs(new ArrayList<>(productProfiles));
//			// set description to productProfile
//			List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
//					.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
//			if (productNameTextSettings.size() > 0) {
//				for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
//					String name = " (";
//					for (ProductNameTextSettings productNameText : productNameTextSettings) {
//						if (productNameText.getName().equals("DESCRIPTION")) {
//							if (productProfileDTO.getDescription() != null
//									&& !productProfileDTO.getDescription().isEmpty())
//								name += productProfileDTO.getDescription() + ",";
//						} else if (productNameText.getName().equals("MRP")) {
//							name += productProfileDTO.getMrp() + ",";
//						} else if (productNameText.getName().equals("SELLING RATE")) {
//							name += productProfileDTO.getPrice() + ",";
//						} else if (productNameText.getName().equals("STOCK")) {
//							OpeningStock openingStock = openingStockRepository
//									.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
//							if (openingStock != null) {
//								name += openingStock.getQuantity() + ",";
//							}
//						} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
//							if (productProfileDTO.getProductDescription() != null
//									&& !productProfileDTO.getProductDescription().isEmpty())
//								name += productProfileDTO.getProductDescription() + ",";
//						} else if (productNameText.getName().equals("BARCODE")) {
//							if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
//								name += productProfileDTO.getBarcode() + ",";
//						} else if (productNameText.getName().equals("REMARKS")) {
//							if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
//								name += productProfileDTO.getRemarks() + ",";
//						}
//					}
//					name = name.substring(0, name.length() - 1);
//					if (name.length() > 1) {
//						name += ")";
//					}
//					productProfileDTO.setName(productProfileDTO.getName() + name);
//				}
//			}
//			unitOfMeasureProductDTO.setProductProfiles(productProfileDTOs);
//			unitOfMeasureProducts.add(unitOfMeasureProductDTO);
//		}
		return unitOfMeasureProducts;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductByUnitOfMeasurePidAndActivatedAndStockAvailabilityStatus(
			String unitOfMeasurePid, boolean activated, StockAvailabilityStatus status) {
		log.debug("Request to get all Products by unitOfMeasurePid and activated and StockAvailabilityStatus");

		List<ProductProfile> productList = new ArrayList<>();
//		List<EcomProductProfile> ecomProductProfiles = unitOfMeasureEcomProductsRepository
//				.findEcomProductByUnitOfMeasurePid(unitOfMeasurePid);
//		if (ecomProductProfiles.isEmpty()) {
//			productList = unitOfMeasureProductRepository
//					.findProductByUnitOfMeasurePidAndActivatedAndStockAvailabilityStatus(unitOfMeasurePid, activated,
//							status);
//		} else {
//			// if ecom productgroup, then find productprofiles from
//			// ecomProductProfileProduct
//			Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid)
//					.collect(Collectors.toSet());
////			productList = ecomProductProfileProductRepository.findProductByEcomProductProfilePidInAndActivatedAndStatus(
////					ecomProductProfilesPids, activated, status);
//		}

		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status) {
		log.debug("Request to get all Products activated and StockAvailabilityStatus");
		List<ProductProfile> productList = unitOfMeasureProductRepository
				.findProductByActivatedAndStockAvailabilityStatus(activated, status);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findEcomProductProfileByUnitOfMeasurePid(String unitOfMeasurePid) {
		List<ProductProfile> productList = new ArrayList<>();
//		List<EcomProductProfile> ecomProductProfiles = unitOfMeasureEcomProductsRepository
//				.findEcomProductByUnitOfMeasurePid(unitOfMeasurePid);
//		if (ecomProductProfiles.isEmpty()) {
//			productList = unitOfMeasureProductRepository.findProductByUnitOfMeasurePid(unitOfMeasurePid);
//		} else {
//			// if ecom productgroup, then find productprofiles from
//			// ecomProductProfileProduct
//			Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid)
//					.collect(Collectors.toSet());
//			productList = ecomProductProfileProductRepository
//					.findProductByEcomProductProfilePidIn(ecomProductProfilesPids);
//		}
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public List<String> findProductByProductProfilePidAndUnitOfMeasurePidAndProductCategoryPidAndActivated(
			List<String> productProfilePids, List<String> unitOfMeasurePids, List<String> productCategoryPids) {
		List<String> productList = unitOfMeasureProductRepository
				.findProductByProductPidInAndUnitOfMeasurePidInAndProductProductCategoryPidInAndActivated(
						productProfilePids, unitOfMeasurePids, productCategoryPids, true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

	@Override
	public List<String> findProductByProductProfilePidAndProductCategoryPidAndActivated(List<String> productProfilePids,
			List<String> productCategoryPids) {
		List<String> productList = unitOfMeasureProductRepository
				.findProductByProductPidInAndProductProductCategoryPidInAndActivated(productProfilePids,
						productCategoryPids, true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

	@Override
	public List<String> findProductByProductProfilePidAndUnitOfMeasurePidAndActivated(List<String> productProfilePids,
			List<String> unitOfMeasurePids) {
		List<String> productList = unitOfMeasureProductRepository
				.findProductByProductPidInAndUnitOfMeasurePidInAndActivated(productProfilePids, unitOfMeasurePids,
						true);
		// List<ProductProfileDTO> result =
		// productMapper.productProfilesToProductProfileDTOs(productList);
		return productList;
	}

}
