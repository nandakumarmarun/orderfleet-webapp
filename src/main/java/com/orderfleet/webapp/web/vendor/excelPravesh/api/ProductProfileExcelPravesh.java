package com.orderfleet.webapp.web.vendor.excelPravesh.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileProductDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupEcomProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductProfileCustomDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.vendor.CochinDistributorsexcel.service.ProductProfileUploadServiceCochinDistributors;
import com.orderfleet.webapp.web.vendor.excel.service.ProductProfileUploadService;
import com.orderfleet.webapp.web.vendor.excelPravesh.service.ProductProfileUploadServicePravesh;

@RestController
@RequestMapping(value = "/api/pravesh/v1")
public class ProductProfileExcelPravesh {

	private final Logger log = LoggerFactory.getLogger(ProductProfileExcelPravesh.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	// @Inject
	// private ProductProfileUploadService productProfileUploadService;

	@Inject
	private ProductProfileUploadServiceCochinDistributors productProfileUploadService;
	
	@Inject
	private ProductProfileUploadServicePravesh  productProfileUploadServicePravesh;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;

	

	@RequestMapping(value = "/product-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfiles(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) {
		
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTPROFILE).map(so -> {
					// update sync status
					
					log.debug("----------------------------------------", productProfileDTOs.size());
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadServicePravesh.saveUpdateProductProfiles(productProfileDTOs, so);
					productProfileUploadServicePravesh.saveUpdateProductGroupProductExcel(productProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
		
	}
	
	
	
	
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
//		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
//				SyncOperationType.PRODUCTPROFILE).map(so -> {
//
//					Optional<SyncOperation> opSyncCG = syncOperationRepository.findOneByCompanyIdAndOperationType(
//							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTCATEGORY);
//
//					if (opSyncCG.isPresent() && !opSyncCG.get().getCompleted()) {
//						return new ResponseEntity<>("product-category save processing try after some time.",
//								HttpStatus.BAD_REQUEST);
//					}

//					Optional<SyncOperation> opSyncPG = syncOperationRepository.findOneByCompanyIdAndOperationType(
//							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTGROUP);
//
//					if (opSyncPG.isPresent() && !opSyncPG.get().getCompleted()) {
//						return new ResponseEntity<>("product-group save processing try after some time.",
//								HttpStatus.BAD_REQUEST);
//					}

					// update sync status
//					
//					so.setCompleted(false);
//					so.setLastSyncStartedDate(LocalDateTime.now());
//					syncOperationRepository.save(so);
//					// save/update
//					productProfileUploadService.saveUpdateProductProfiles(productProfileDTOs, so);
//					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
//				}).orElse(new ResponseEntity<>("Product-profile sync operation not registered for this company",
//						HttpStatus.BAD_REQUEST));
	// }

	
	
	/*@RequestMapping(value = "/stock-locations.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavestockLocations(@Valid @RequestBody List<StockLocationDTO> stockLocationDTOs) {
		log.debug("REST request to save Stock Locations : {}", stockLocationDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.STOCK_LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateStockLocations(stockLocationDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserStockLocations();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}
					if (so.getDocument()) {
						documentUserWiseUpdateController.assighnStockLocationDestination();
						documentUserWiseUpdateController.assighnStockLocationSource();
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Stock-location sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
		log.debug("REST request to save opening stock : {}", openingStockDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.OPENING_STOCK).map(so -> {

					Optional<SyncOperation> opSyncSL = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.STOCK_LOCATION);

					if (!opSyncSL.get().getCompleted()) {
						return new ResponseEntity<>("stock-location save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);

					if (!opSyncPP.get().getCompleted()) {
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateOpeningStock(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
*/
	


/*	@RequestMapping(value = "/ecom-product-profile-product.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveEcomProductProfileProduct(
			@Valid @RequestBody List<EcomProductProfileProductDTO> ppDTOs) {
		log.debug("REST request to save ecom product profile product: {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ECOM_PRODUCT_PROFILE_PRODUCT).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateEcomProductProfileProducts(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"ECOM_PRODUCT_PROFILE_PRODUCT sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}*/

/*	@RequestMapping(value = "/product-group-ecom-product.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupEcomProductProfile(
			@Valid @RequestBody List<ProductGroupEcomProductDTO> ppDTOs) {
		log.debug("REST request to save product group ecom product profile : {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCT_GROUP_ECOM_PRODUCT).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductGroupEcomProductProfiles(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"PRODUCT_GROUP_ECOM_PRODUCT sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}*/

}
