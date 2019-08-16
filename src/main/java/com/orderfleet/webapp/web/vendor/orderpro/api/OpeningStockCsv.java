package com.orderfleet.webapp.web.vendor.orderpro.api;

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

import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.vendor.excel.service.OpeningStockUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/v1")
public class OpeningStockCsv {

	private final Logger log = LoggerFactory.getLogger(AccountProfileCsv.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private OpeningStockUploadService openingStockUploadService;

	@Inject
	private StockLocationService stockLocationService;
	
	@Inject
	private OpeningStockRepository openingStockRepository;

//	@PostMapping("/opening-stock.json")
//	@ResponseBody
//	public ResponseEntity<String> uploadOpeningStock(@RequestBody List<OpeningStockVendorDTO> openingStockDtos,
//			@RequestHeader("X-COMPANY") String companyId) {
//		
//		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
//		Company company = snrichPartnerCompany.getCompany();
//		if(company != null){
//			masterDataUploadService.saveOrUpdateOpeningStock(openingStockDtos, company);
//			return new ResponseEntity<String>("Success",HttpStatus.OK);	
//		}else{
//			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@RequestMapping(value = "/opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> bulkSaveOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
		log.debug("REST request to save opening stock : {}", openingStockDTOs.size());
		
		
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.OPENING_STOCK).map(so -> {

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
