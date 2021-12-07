package com.orderfleet.webapp.web.rest.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.LedgerReportTP;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;

/**
 * Mapper for the entity LedgerReportTP and its DTO LedgerReportTPDTO.
 * 
 * @author Sarath
 * @since Nov 2, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class LedgerReportTPMapper {
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		 AccountProfile ap= accountProfileRepository.findOneByPid(pid).map(account -> account).orElse(null);
		  String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		return ap;
	}
}
