package com.orderfleet.webapp.web.vendor.excel.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
import com.orderfleet.webapp.web.vendor.integre.dto.OpeningStockVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class OpeningStockExcel {

	private final Logger log = LoggerFactory.getLogger(AccountProfileExcel.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;
	
	@Inject
	private OpeningStockUploadService openingStockUploadService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;
	
	@Inject
	private StockLocationService stockLocationService;
	
	@Inject
	private OpeningStockRepository openingStockRepository;


	/**
	 * REST endpoint to bulk save opening stock data from a JSON request.
	 *
	 * @param openingStockDTOs A list of OpeningStockDTO objects containing the opening stock data to be saved.
	 * @param request          The HTTP servlet request.
	 * @return A ResponseEntity containing a status message indicating the result of the operation.
	 */
	@RequestMapping(value = "/opening-stock.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> bulkSaveOpeningStock(
			@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs,
			HttpServletRequest request) {
		log.debug(request.getRequestedSessionId() +" : "+"REST request to save opening stock : {} "
				+ request.getRequestURI()+"   : size  "
				+ openingStockDTOs.size());

		// Get the current user's company ID from the security context
		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		long stockLocationId = 0L;

		// Retrieve all stock locations for the current company
		List<StockLocation> stockLocations =
				stockLocationService.findAllStockLocationByCompanyId(companyId);

		// Check if the openingStockDTOs list is not empty
		if(!openingStockDTOs.isEmpty()) {
			String stockLocationName = openingStockDTOs.get(0).getStockLocationName();

			// Find the first stock location with a matching alias
			Optional<StockLocation> stockLocation =
					stockLocations
							.stream()
							.filter(sl -> sl.getAlias()!=null?sl.getAlias()
									.equals(stockLocationName):false)
							.findFirst();

			// If a matching stock location is found, set the stockLocationId
			if(stockLocation.isPresent()) {
				stockLocationId = stockLocation.get().getId();
			}
		}

		// If stockLocationId is not 0,
		// delete existing opening stock records for the specified stock location and company
			if(stockLocationId!=0) {
			openingStockRepository
					.deleteByStockLocationIdAndCompanyId(
							stockLocationId,companyId);
			}

		return syncOperationRepository.findOneByCompanyIdAndOperationType(
				SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.OPENING_STOCK)
				.map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					openingStockUploadService.saveUpdateOpeningStock(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

}
