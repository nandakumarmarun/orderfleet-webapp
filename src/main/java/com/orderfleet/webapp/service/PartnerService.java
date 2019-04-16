package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.PartnerDTO;

/**
 * Service Interface for managing Partners.
 *
 * @author Sarath
 * @since Feb 22, 2018
 *
 */
public interface PartnerService {

	PartnerDTO save(PartnerDTO partnerDTO);

	PartnerDTO update(PartnerDTO partnerDTO);

	List<PartnerDTO> GetAllActivatedPartners(boolean activated);

	List<PartnerDTO> GetAllPartners();

	Optional<PartnerDTO> findOneByPid(String pid);

	PartnerDTO updatePartnerStatus(String pid, Boolean activated);

}
