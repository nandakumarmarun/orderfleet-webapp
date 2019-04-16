package com.orderfleet.webapp.web.rest.integration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * upload openingStock and stockLocation.
 *
 * @author Sarath
 * @since Feb 7, 2018
 *
 */

@RestController
@RequestMapping(value = "/api/tp/v1")
public class LamitStockUploadResource {

	private final Logger log = LoggerFactory.getLogger(MasterDataProductResource.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@RequestMapping(value = "/lamit-stock-locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLamitStockLocations(
			@Valid @RequestBody List<StockLocationDTO> stockLocationDTOs) {
		log.debug("REST request to save Stock Locations : {}", stockLocationDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.STOCK_LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					saveUpdateStockLocations(stockLocationDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Stock-location sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/lamit-opening-stock", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLamitOpeningStock(
			@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
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
					saveUpdateOpeningStock(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@Transactional
	public void saveUpdateStockLocations(final List<StockLocationDTO> stockLocationDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();

		List<String> newStaticLocations = Arrays.asList("LOC_ERNAKULAM", "LOC_KOLLAM", "LOC_MANCHERY");
		newStaticLocations.forEach(loc -> {
			StockLocationDTO locationDTO = new StockLocationDTO();
			locationDTO.setName(loc);
			stockLocationDTOs.add(locationDTO);
		});

		Set<StockLocation> saveUpdateStockLocations = new HashSet<>();
		// find all locations
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
		for (StockLocationDTO locDto : stockLocationDTOs) {
			// check exist by name, only one exist with a name
			Optional<StockLocation> optionalLoc = stockLocations.stream()
					.filter(sl -> sl.getName().equals(locDto.getName())).findAny();
			StockLocation stockLocation;
			if (optionalLoc.isPresent()) {
				stockLocation = optionalLoc.get();
			} else {
				stockLocation = new StockLocation();
				stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				stockLocation.setName(locDto.getName());
				stockLocation.setStockLocationType(StockLocationType.ACTUAL);
				stockLocation.setCompany(company);
			}
			stockLocation.setActivated(locDto.getActivated());
			saveUpdateStockLocations.add(stockLocation);
		}
		bulkOperationRepositoryCustom.bulkSaveStockLocations(saveUpdateStockLocations);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	@Transactional
	@Async
	public void saveUpdateOpeningStock(final List<OpeningStockDTO> openingStockDTOs,
			final SyncOperation syncOperation) {
		long start = System.nanoTime();

		/* uploadToErnaklm */
		Company ernklmCompany = companyRepository.findOne(304697L);
		Optional<StockLocation> opStockLocation = stockLocationRepository
				.findByCompanyIdAndNameIgnoreCase(ernklmCompany.getId(), "LOC_ERNAKULAM");
		if (opStockLocation.isPresent()) {
			updateToLamit(ernklmCompany, openingStockDTOs, opStockLocation.get());
		}

		/* uploadToKollam */
		Company kollamCompany = companyRepository.findOne(304698L);
		Optional<StockLocation> opKollamStockLocation = stockLocationRepository
				.findByCompanyIdAndNameIgnoreCase(kollamCompany.getId(), "LOC_KOLLAM");
		if (opKollamStockLocation.isPresent()) {
			updateToLamit(kollamCompany, openingStockDTOs, opKollamStockLocation.get());
		}

		/* uploadToManchery */
		Company MancheryCompany = companyRepository.findOne(304690L);
		Optional<StockLocation> opMancheryStockLocation = stockLocationRepository
				.findByCompanyIdAndNameIgnoreCase(MancheryCompany.getId(), "LOC_MANCHERY");
		if (opMancheryStockLocation.isPresent()) {
			updateToLamit(MancheryCompany, openingStockDTOs, opMancheryStockLocation.get());
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	private void updateToLamit(Company company, List<OpeningStockDTO> openingStockDTOs, StockLocation stockLocation) {

		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());
		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		// delete all opening stock
		openingStockRepository.deleteByCompanyId(company.getId());
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						OpeningStock openingStock = new OpeningStock();
						openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
						openingStock.setOpeningStockDate(LocalDateTime.now());
						openingStock.setCreatedDate(LocalDateTime.now());
						openingStock.setCompany(company);
						openingStock.setProductProfile(pp);
						openingStock.setStockLocation(stockLocation);
						openingStock.setBatchNumber(osDto.getBatchNumber());
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
	}
}
