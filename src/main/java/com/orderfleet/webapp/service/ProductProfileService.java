package com.orderfleet.webapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;

/**
 * Service Interface for managing ProductProfile.
 *
 * @author Muhammed Riyas T
 * @since May 18, 2016
 */
public interface ProductProfileService {

	String PID_PREFIX = "PPro-";

	/**
	 * Save a productProfile.
	 *
	 * @param productProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ProductProfileDTO save(ProductProfileDTO productProfileDTO);

	String saveProductImage(MultipartFile file, String productPid) throws FileManagerException, IOException;

	Set<FileDTO> findProductProfileImages(String productPid);

	/**
	 * Update a productProfile.
	 *
	 * @param productProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ProductProfileDTO update(ProductProfileDTO productProfileDTO);

	/**
	 * Get all the productProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductProfile> findAll(Pageable pageable);

	/**
	 * Get all the productProfiles of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductProfileDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" productProfile.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ProductProfileDTO findOne(Long id);

	/**
	 * Get the productProfile by "pid".
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ProductProfileDTO> findOneByPid(String pid);

	/**
	 * Get the productProfileDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ProductProfileDTO> findByName(String name);

	/**
	 * Delete the "id" productProfile.
	 *
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	boolean deleteProductProfileImage(String productPid, String filePid);

	List<ProductProfileDTO> findAllByCompany();

	List<ProductProfileDTO> findByProductCategoryPid(String categoryPid);

	List<ProductProfileDTO> findByProductCategoryPids(List<String> categoryPids);

	List<ProductProfileDTO> findProductsByUserProductCategoriesIsCurrentUser(int page, int count);

	Page<ProductProfileDTO> findAllByCompanyAndCategoryPid(Pageable pageable, String CategoryPid);

	void updateSize(double size, List<String> productProfiles);

	List<ProductProfileDTO> findProductsByUserProductCategoriesIsCurrentUserAndActivated(int page, int count);

	List<ProductProfileDTO> findProductsAssignedInProductGroup();
	
	List<ProductProfileDTO> searchByName(String name);

	/**
	 * Update the ProductProfile status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param activated
	 *            the activated of the entity
	 * @return the entity
	 */
	ProductProfileDTO updateProductProfileStatus(String pid, boolean activated);

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 *
	 *        find all active company
	 *
	 * @param active
	 *            the active of the entity
	 * @return the list.
	 */
	List<ProductProfileDTO> findAllByCompanyAndActivatedProductProfileOrderByName(boolean active);
	
	List<ProductProfileDTO> findAllByCompanyAndActivatedProductProfileOrderByNameLiits(boolean active);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductCategoryPids And Activated
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	List<ProductProfileDTO> findByProductCategoryPidsAndActivated(List<String> categoryPids);

	Optional<ProductProfileDTO> findByCompanyIdAndName(Long companyId, String name);

	List<ProductProfileDTO> findByProductCategoryInAndActivatedTrueAndLastModifiedDate(int page, int count,
			LocalDateTime lastModifiedDate);

	Optional<ProductProfile> findProductProfileByCompanyIdAndName(Long companyId, String name);

	void saveTaxMaster(List<String> taxmasterPids, String productProfilePid);

	List<TaxMasterDTO> getAssignedTaxMaster(String productProfilePid);

	void updateProductImage(MultipartFile file, String productPid, String imgFilePid)
			throws FileManagerException, IOException;

	List<ProductProfileDTO> findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(
			boolean active, StockAvailabilityStatus status);
	
	List<ProductProfileDTO> findAllProductProfilePidByProductProfilePidAndActivated(List<String> profilePids);




	List<String> findByProductCategoryInAndActivatedFalseAndLastModifiedDate(LocalDateTime lastSyncdate);
}
