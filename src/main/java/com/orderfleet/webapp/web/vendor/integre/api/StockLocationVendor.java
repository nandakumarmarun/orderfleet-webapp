package com.orderfleet.webapp.web.vendor.integre.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.vendor.integre.dto.StockLocationVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class StockLocationVendor { 
	
	@Inject
	private MasterDataUploadService masterDataUploadService;
	
	@Inject
	private AssignUploadDataService assignUploadDataService;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository; 
	

	@PostMapping("/stock-locations.json")
	@ResponseBody 
	public ResponseEntity<String> uploadStockLocations(@RequestBody List<StockLocationVendorDTO> stockLocationsDtos,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<StockLocation> existingStockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdateStockLocation(stockLocationsDtos, company);
			for(StockLocation stockL : existingStockLocations) {
				stockLocationsDtos.removeIf(stk -> stk.getCode().equals(stockL.getAlias()));
			}
			List<String> newStockLocationAlias = stockLocationsDtos.stream()
					.map(stk -> stk.getCode()).collect(Collectors.toList());
			assignUploadDataService.userStockLocationAssociation(newStockLocationAlias,company);
			assignUploadDataService.documentStockLocationAssociation(newStockLocationAlias,company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);	
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
}
