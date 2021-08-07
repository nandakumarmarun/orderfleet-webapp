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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileUploadService;
import com.orderfleet.webapp.web.vendor.excel.service.OpeningStockUploadService;
import com.orderfleet.webapp.web.vendor.excelPravesh.service.OpeningStockUploadServicePravesh;
import com.orderfleet.webapp.web.vendor.integre.dto.OpeningStockVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/pravesh/v1")
public class OpeningStockExcelPravesh {

	private final Logger log = LoggerFactory.getLogger(AccountProfileExcelPravesh.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;

	@Inject
	private OpeningStockUploadService openingStockUploadService;

	@Inject
	private OpeningStockUploadServicePravesh OpeningStockUploadServicePravesh;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@RequestMapping(value = "/opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> bulkSaveOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
		log.debug("REST request to save opening stock : {}", openingStockDTOs.size());

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		long stockLocationId = 0L;
		List<StockLocation> stockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);
		if (!openingStockDTOs.isEmpty()) {

			String stockLocationName = openingStockDTOs.get(0).getStockLocationName();

			Optional<StockLocation> stockLocation = stockLocations.stream()
					.filter(sl -> sl.getName() != null ? sl.getName().equals(stockLocationName) : false).findFirst();
			if (stockLocation.isPresent()) {
				stockLocationId = stockLocation.get().getId();
			}
		}
		if (stockLocationId != 0) {
			openingStockRepository.deleteByStockLocationIdAndCompanyId(stockLocationId, companyId);
		}

		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.OPENING_STOCK).map(so -> {

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					OpeningStockUploadServicePravesh.saveUpdateOpeningStock(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

}
