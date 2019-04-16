package com.orderfleet.webapp.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;

/**
 * Service Implementation for managing ProductProfile.
 *
 * @author Muhammed Riyas T
 * @since May 18, 2016
 */
@Service
@Transactional
public class ProductProfileServiceImpl implements ProductProfileService {

	private final Logger log = LoggerFactory.getLogger(ProductProfileServiceImpl.class);

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductProfileMapper productProfileMapper;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private TaxMasterRepository taxMasterRepository;

	@Inject
	private TaxMasterMapper taxMasterMapper;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	/**
	 * Save a productProfile.
	 *
	 * @param productProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductProfileDTO save(ProductProfileDTO productProfileDTO) {
		log.debug("Request to save ProductProfile : {}", productProfileDTO);
		productProfileDTO.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
		ProductProfile productProfile = productProfileMapper.productProfileDTOToProductProfile(productProfileDTO);
		// set company
		productProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productProfile.setLastModifiedDate(LocalDateTime.now());
		productProfile = productProfileRepository.save(productProfile);
		ProductProfileDTO result = productProfileMapper.productProfileToProductProfileDTO(productProfile);
		return result;
	}

	public String saveProductImage(MultipartFile file, String productPid) throws FileManagerException, IOException {
		log.debug("Request to save image : {}", file.getOriginalFilename());
		// save file
		File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(), file.getOriginalFilename(),
				file.getContentType());
		productProfileRepository.findOneByPid(productPid).ifPresent(productProfile -> {
			productProfile.getFiles().add(uploadedFile);
			productProfileRepository.save(productProfile);
		});
		return uploadedFile.getPid();
	}

	/**
	 * Update a productProfile.
	 *
	 * @param productProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ProductProfileDTO update(ProductProfileDTO productProfileDTO) {
		log.debug("Request to Update ProductProfile : {}", productProfileDTO);
		return productProfileRepository.findOneByPid(productProfileDTO.getPid()).map(productProfile -> {
			productProfile.setName(productProfileDTO.getName());
			productProfile.setAlias(productProfileDTO.getAlias());
			productProfile.setProductCategory(
					productCategoryRepository.findOneByPid(productProfileDTO.getProductCategoryPid()).get());
			productProfile.setDivision(divisionRepository.findOneByPid(productProfileDTO.getDivisionPid()).get());
			productProfile.setSku(productProfileDTO.getSku());
			productProfile.setUnitQty(productProfileDTO.getUnitQty());
			productProfile.setPrice(productProfileDTO.getPrice());
			productProfile.setMrp(productProfileDTO.getMrp());
			productProfile.setDescription(productProfileDTO.getDescription());
			productProfile.setTaxRate(productProfileDTO.getTaxRate());
			productProfile.setSize(
					productProfileDTO.getSize() == null || productProfileDTO.getSize().equalsIgnoreCase("") ? null
							: productProfileDTO.getSize());
			productProfile.setColorImage(productProfileDTO.getColorImage());
			productProfile.setColorImageContentType(productProfileDTO.getColorImageContentType());
			productProfile.setStockAvailabilityStatus(productProfileDTO.getStockAvailabilityStatus());
			productProfile = productProfileRepository.save(productProfile);
			ProductProfileDTO result = productProfileMapper.productProfileToProductProfileDTO(productProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the categorys.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductProfile> findAll(Pageable pageable) {
		log.debug("Request to get all EmployeeProfiles");
		Page<ProductProfile> result = productProfileRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the categorys.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductProfileDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all EmployeeProfiles");
		Page<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(pageable);
		Page<ProductProfileDTO> result = new PageImpl<ProductProfileDTO>(
				productProfileMapper.productProfilesToProductProfileDTOs(productProfiles.getContent()), pageable,
				productProfiles.getTotalElements());
		return result;
	}

	/**
	 * Get one productProfile by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ProductProfileDTO findOne(Long id) {
		log.debug("Request to get ProductProfile : {}", id);
		ProductProfile productProfile = productProfileRepository.findOne(id);
		ProductProfileDTO productProfileDTO = productProfileMapper.productProfileToProductProfileDTO(productProfile);
		return productProfileDTO;
	}

	/**
	 * Get one productProfile by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductProfileDTO> findOneByPid(String pid) {
		log.debug("Request to get ProductProfile by pid : {}", pid);
		return productProfileRepository.findOneByPid(pid).map(productProfile -> {
			ProductProfileDTO productProfileDTO = productProfileMapper
					.productProfileToProductProfileDTO(productProfile);
			return productProfileDTO;
		});
	}

	/**
	 * Get one productProfile by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductProfileDTO> findByName(String name) {
		log.debug("Request to get ProductProfile by name : {}", name);
		return productProfileRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productProfile -> {
					ProductProfileDTO productProfileDTO = productProfileMapper
							.productProfileToProductProfileDTO(productProfile);
					return productProfileDTO;
				});
	}

	/**
	 * Delete the productProfile by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ProductProfile : {}", pid);
		productProfileRepository.findOneByPid(pid).ifPresent(productProfile -> {
			productProfileRepository.delete(productProfile.getId());
		});
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findAllByCompany() {
		log.debug("Request to get all StockLocations");
		//
		List<ProductProfile> productProfileList = productProfileRepository.findAllByCompanyIdActivatedTrue();
		List<ProductProfileDTO> result = productProfileMapper.productProfilesToProductProfileDTOs(productProfileList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findByProductCategoryPid(String categoryPid) {
		List<ProductProfile> productProfileList = productProfileRepository.findByProductCategoryPid(categoryPid);
		List<ProductProfileDTO> result = productProfileMapper.productProfilesToProductProfileDTOs(productProfileList);
		return result;
	}

	@Override
	public List<ProductProfileDTO> findByProductCategoryPids(List<String> categoryPids) {
		List<ProductProfile> productProfileList = productProfileRepository.findByProductCategoryPidIn(categoryPids);
		List<ProductProfileDTO> result = productProfileMapper.productProfilesToProductProfileDTOs(productProfileList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductsByUserProductCategoriesIsCurrentUser(int page, int count) {
		// get user productCategories
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUser();
		// get productProfiles by user productCategories
		Page<ProductProfile> productProfiles = productProfileRepository.findByProductCategoryIn(productCategories,
				new PageRequest(page, count));
		List<ProductProfileDTO> result = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles.getContent());

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
	 * Get all the categorys.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductProfileDTO> findAllByCompanyAndCategoryPid(Pageable pageable, String categoryPid) {
		log.debug("Request to get all EmployeeProfiles");
		Page<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyIdAndCategoryPid(pageable,
				categoryPid);
		Page<ProductProfileDTO> result = new PageImpl<ProductProfileDTO>(
				productProfileMapper.productProfilesToProductProfileDTOs(productProfiles.getContent()), pageable,
				productProfiles.getTotalElements());
		return result;
	}

	@Override
	public Set<FileDTO> findProductProfileImages(String productPid) {
		Set<File> files = productProfileRepository.findProductProfileImages(productPid);
		Set<FileDTO> fileDTOs = new HashSet<>();
		files.forEach(file -> {
			FileDTO fileDTO = new FileDTO();
			fileDTO.setFilePid(file.getPid());
			fileDTO.setFileName(file.getFileName());
			fileDTO.setMimeType(file.getMimeType());
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
			if (physicalFile.exists()) {
				try {
					fileDTO.setContent(Files.toByteArray(physicalFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fileDTOs.add(fileDTO);
		});
		return fileDTOs;
	}

	@Override
	public boolean deleteProductProfileImage(String productPid, String filePid) {
		productProfileRepository.findOneByPid(productPid).map(productProfile -> {
			boolean status = false;
			for (File file : productProfile.getFiles()) {
				if (file.getPid().equals(filePid)) {
					productProfile.getFiles().remove(file);
					productProfileRepository.save(productProfile);
					fileManagerService.deleteFile(file);
					status = true;
					break;
				}
			}
			return status;
		});
		return true;
	}

	@Override
	public void updateSize(double size, List<String> productProfiles) {
		productProfileRepository.updateSize(size, productProfiles);
	}

	/*
	 * find products activated true
	 *
	 * @author Sarath T
	 *
	 * @since Jan 20, 2017
	 */

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductsByUserProductCategoriesIsCurrentUserAndActivated(int page, int count) {
		// get user productCategories
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUser();
		// get productProfiles by user productCategories
		Page<ProductProfile> productProfiles = productProfileRepository
				.findByProductCategoryInAndActivatedTrue(productCategories, new PageRequest(page, count));
		List<ProductProfileDTO> result = productProfiles.getContent().stream().map(ProductProfileDTO::new)
				.collect(Collectors.toList());
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
	public List<ProductProfileDTO> findProductsAssignedInProductGroup() {
		List<ProductProfile> productProfiles = productGroupProductRepository.findAssignedProductProfiles();
		return productProfileMapper.productProfilesToProductProfileDTOs(productProfiles);
	}

	/**
	 * Update the ProductProfile status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param activated
	 *            the activated of the entity
	 * @return the entity
	 */
	@Override
	public ProductProfileDTO updateProductProfileStatus(String pid, boolean activated) {
		log.debug("Change Activate status of ProductProfile", pid);
		return productProfileRepository.findOneByPid(pid).map(productProfile -> {
			productProfile.setActivated(activated);
			productProfile = productProfileRepository.save(productProfile);
			ProductProfileDTO res = productProfileMapper.productProfileToProductProfileDTO(productProfile);
			return res;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 *
	 *        find all active company
	 *
	 * @param active
	 *            the active of the entity
	 * @return the list
	 */
	@Override
	public List<ProductProfileDTO> findAllByCompanyAndActivatedProductProfileOrderByName(boolean active) {
		log.debug("Request to get Activated ProductProfile");
		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyIdAndActivatedOrDeactivatedProductProfileOrderByName(active);
		List<ProductProfileDTO> productProfileDTOs = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles);
		List<String> productNameTextSettings = productNameTextSettingsRepository
				.findNameByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		Set<Long> sLocationIds = documentStockLocationSourceRepository.findStockLocationIdsByCompanyId();

		if (!productNameTextSettings.isEmpty()) {
			Boolean descriptionExist = Boolean.FALSE;
			Boolean mrpExist = Boolean.FALSE;
			Boolean sellingRateExist = Boolean.FALSE;
			Boolean stockExist = Boolean.FALSE;
			for (String productNameText : productNameTextSettings) {
				switch (productNameText) {
				case "DESCRIPTION":
					descriptionExist = Boolean.TRUE;
					break;
				case "MRP":
					mrpExist = Boolean.TRUE;
					break;
				case "SELLING RATE":
					sellingRateExist = Boolean.TRUE;
					break;
				case "STOCK":
					stockExist = Boolean.TRUE;
					break;
				default:
					break;
				}
			}
			for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
				StringBuilder name = new StringBuilder(" (");
				if (descriptionExist && productProfileDTO.getDescription() != null
						&& !productProfileDTO.getDescription().isEmpty()) {
					name.append(productProfileDTO.getDescription());
				}
				if (mrpExist) {
					name.append(",").append(productProfileDTO.getMrp());
				}
				if (sellingRateExist) {
					name.append(",").append(productProfileDTO.getPrice());
				}
				if (stockExist) {
					if (sLocationIds.isEmpty()) {
						OpeningStock openingStock = openingStockRepository
								.findTop1ByProductProfilePidOrderByCreatedDateDesc(productProfileDTO.getPid());
						if (openingStock != null) {
							name.append(",").append(openingStock.getQuantity());
						}
					} else {
						Double sum = openingStockRepository.findSumOpeningStockByProductPidAndStockLocationIdIn(
								productProfileDTO.getPid(), sLocationIds);
						name.append(",").append(sum);
					}
				}
				name.append(")");
				if (name.length() > 3) {
					productProfileDTO.setName(productProfileDTO.getName() + name.toString());
				} else {
					productProfileDTO.setName(productProfileDTO.getName());
				}
			}
		}
		return productProfileDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductCategoryPids And Activated
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	@Override
	public List<ProductProfileDTO> findByProductCategoryPidsAndActivated(List<String> categoryPids) {
		List<ProductProfile> productProfileList = productProfileRepository
				.findByProductCategoryPidInAndActivated(categoryPids, true);
		List<ProductProfileDTO> result = productProfileMapper.productProfilesToProductProfileDTOs(productProfileList);
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
	 * Get one productProfile by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductProfileDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get ProductProfile by name : {}", name);
		return productProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(productProfile -> {
			ProductProfileDTO productProfileDTO = productProfileMapper
					.productProfileToProductProfileDTO(productProfile);
			return productProfileDTO;
		});
	}

	/*
	 * find products activated true
	 *
	 * @author Sarath T
	 *
	 * @since Jan 20, 2017
	 */

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findByProductCategoryInAndActivatedTrueAndLastModifiedDate(int page, int count,
			LocalDateTime lastModifiedDate) {
		// get user productCategories
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUser();
		if (productCategories.isEmpty()) {
			return Collections.emptyList();
		}
		// get productProfiles by user productCategories
		Page<ProductProfile> productProfiles = productProfileRepository
				.findByProductCategoryInAndActivatedTrueAndLastModifiedDate(productCategories, true, lastModifiedDate,
						new PageRequest(page, count));
		List<ProductProfileDTO> result = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles.getContent());
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
	 * Get one productProfile by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductProfile> findProductProfileByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get ProductProfile by name : {}", name);
		return productProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId, name);
	}

	@Override
	public void saveTaxMaster(List<String> taxmasterPids, String productProfilePid) {
		log.debug("Request to save ProductProfile by taxmasterPids : {}", taxmasterPids);
		List<TaxMaster> taxMasters = new ArrayList<>();
		for (String taxPid : taxmasterPids) {
			TaxMaster taxMaster = taxMasterRepository.findOneByPid(taxPid).get();
			taxMasters.add(taxMaster);
		}
		ProductProfile productProfile = productProfileRepository.findOneByPid(productProfilePid).get();
		productProfile.setTaxMastersList(taxMasters);
		productProfileRepository.save(productProfile);
	}

	@Override
	public List<TaxMasterDTO> getAssignedTaxMaster(String productProfilePid) {
		List<TaxMasterDTO> taxMasterDTOs = new ArrayList<>();
		ProductProfile productProfiles = productProfileRepository.findOneByPid(productProfilePid).get();
		if (productProfiles != null) {
			List<TaxMaster> taxMasters = productProfiles.getTaxMastersList();
			taxMasterDTOs = taxMasterMapper.taxMastersToTaxMasterDTOs(taxMasters);
		}
		return taxMasterDTOs;
	}

	@Override
	public void updateProductImage(MultipartFile file, String productPid, String filePid)
			throws FileManagerException, IOException {
		log.debug("Request to update image : {}", file.getOriginalFilename());
		// save file
		File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(), file.getOriginalFilename(),
				file.getContentType());
		Optional<ProductProfile> opProductProfile = productProfileRepository.findOneByPid(productPid);
		if (opProductProfile.isPresent()) {
			Set<File> imgFiles = new HashSet<>();
			ProductProfile productProfile = opProductProfile.get();
			for (File imgFile : productProfile.getFiles()) {
				if (imgFile.getPid().equalsIgnoreCase(filePid)) {
					imgFile.setPersistentFile(uploadedFile.getPersistentFile());
					imgFile.setDescription(uploadedFile.getDescription());
					imgFile.setFileName(uploadedFile.getFileName());
					imgFile.setMimeType(uploadedFile.getMimeType());
					imgFile.setUploadedDate(uploadedFile.getUploadedDate());
				}
				imgFiles.add(imgFile);
			}
			productProfile.setFiles(imgFiles);
			productProfileRepository.save(productProfile);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(
			boolean active, StockAvailabilityStatus status) {
		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(active, status);
		return productProfileMapper.productProfilesToProductProfileDTOs(productProfiles);
	}

	@Override
	public List<ProductProfileDTO> findAllProductProfilePidByProductProfilePidAndActivated(List<String> profilePids) {
		List<ProductProfile> productProfileList = productProfileRepository
				.findByPidInAndActivated(profilePids, true);
		List<ProductProfileDTO> result = productProfileMapper.productProfilesToProductProfileDTOs(productProfileList);
		return result;
	}
}
