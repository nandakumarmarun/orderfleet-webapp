package com.orderfleet.webapp.web.rest.api;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.DistributorDealerAssociation;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DistributorDealerProfileRepository;
import com.orderfleet.webapp.web.rest.api.dto.DistributorDealerDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class DistributorDealerAssociationController {
	
	private final Logger log = LoggerFactory.getLogger(DistributorDealerAssociationController.class);
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private DistributorDealerProfileRepository distributorDealerProfileRepository;
	
	@RequestMapping(value = "/distributor-dealer-association", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getDistributorDealerByAccountPid(
			@RequestParam String accountProfilePid) {
		log.info("Get Distributor Dealer Association :"+accountProfilePid);
		
		Optional<AccountProfile> opAccp = accountProfileRepository.findOneByPid(accountProfilePid);
		AccountProfile distributor = new AccountProfile();
		if(opAccp.isPresent())
		{
			Optional<DistributorDealerAssociation> association = distributorDealerProfileRepository.findByDistributorPid(opAccp.get().getPid());
			distributor = association.get().getDistributor();
		}
		
		AccountProfileDTO accountProfileDTO = new AccountProfileDTO(distributor);
		
		return new ResponseEntity<AccountProfileDTO>(accountProfileDTO,HttpStatus.OK);
	}

}
