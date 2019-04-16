package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.LedgerReportTP;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;

/**
 * Mapper for the entity LedgerReportTP and its DTO LedgerReportTPDTO.
 * 
 * @author Sarath
 * @since Nov 2, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class LedgerReportTPMapper {

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Mapping(source = "division.pid", target = "divisionPid")
	@Mapping(source = "division.name", target = "divisionName")
	@Mapping(source = "division.alias", target = "divisionAlias")
	@Mapping(source = "accountProfile.pid", target = "accountProfilePid")
	@Mapping(source = "accountProfile.name", target = "accountProfileName")
	public abstract LedgerReportTPDTO ledgerReportTPToLedgerReportTPDTO(LedgerReportTP ledgerReportTP);

	public abstract List<LedgerReportTPDTO> ledgerReportTPsToLedgerReportTPDTOs(List<LedgerReportTP> ledgerReportTPs);

	@Mapping(source = "divisionPid", target = "division")
	@Mapping(source = "accountProfilePid", target = "accountProfile")
	@Mapping(target = "company", ignore = true)
	public abstract LedgerReportTP ledgerReportTPDTOToLedgerReportTP(LedgerReportTPDTO ledgerReportTPDTO);

	public abstract List<LedgerReportTP> ledgerReportTPDTOsToLedgerReportTPs(List<LedgerReportTPDTO> ledgerReportTPDTOs);

	public Division divisionFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}

		return divisionRepository.findOneByPid(pid).map(division -> division).orElse(null);
	}

	public AccountProfile accountProfileFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return accountProfileRepository.findOneByPid(pid).map(account -> account).orElse(null);
	}
}
