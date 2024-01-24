package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.LedgerReportTP;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.LedgerReportTPRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LedgerReportTPService;
import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;
import com.orderfleet.webapp.web.rest.mapper.LedgerReportTPMapper;

/**
 * Service Implementation for managing LedgerReportTP.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */
@Service
@Transactional
public class LedgerReportTPServiceImpl implements LedgerReportTPService {

	private final Logger log = LoggerFactory.getLogger(LedgerReportTPServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private LedgerReportTPRepository ledgerReportTPRepository;

	@Inject
	private LedgerReportTPMapper ledgerReportTPMapper;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public LedgerReportTPDTO save(LedgerReportTPDTO ledgerReportTPDTO) {
		log.debug("Request to save LedgerReportTP : {}", ledgerReportTPDTO);
		LedgerReportTP ledgerReportTP = ledgerReportTPMapper.ledgerReportTPDTOToLedgerReportTP(ledgerReportTPDTO);
		ledgerReportTP.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		ledgerReportTP = ledgerReportTPRepository.save(ledgerReportTP);
		LedgerReportTPDTO result = ledgerReportTPMapper.ledgerReportTPToLedgerReportTPDTO(ledgerReportTP);
		return result;
	}


	/**
	 * @deprecated
	 * @param ledgerReportTPDTO
	 * @return
	 */
	@Override
	public LedgerReportTPDTO update(LedgerReportTPDTO ledgerReportTPDTO) {
//		log.debug("Request to Update LedgerReportTP : {}", ledgerReportTPDTO);
//
//		return ledgerReportTPRepository.findOneById(ledgerReportTPDTO.getId()).map(ledgerReportTP -> {
//			  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
//				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//				String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
//				String description ="get by compId and name Ignore case";
//				LocalDateTime startLCTime = LocalDateTime.now();
//				String startTime = startLCTime.format(DATE_TIME_FORMAT);
//				String startDate = startLCTime.format(DATE_FORMAT);
//				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
//
//				Optional<AccountProfile> accountProfile =
//						accountProfileRepository
//								.findOneByPid(ledgerReportTPDTO.getAccountProfilePid());
//
//				String flag = "Normal";
//				LocalDateTime endLCTime = LocalDateTime.now();
//				String endTime = endLCTime.format(DATE_TIME_FORMAT);
//				String endDate = startLCTime.format(DATE_FORMAT);
//				Duration duration = Duration.between(startLCTime, endLCTime);
//				long minutes = duration.toMinutes();
//				if (minutes <= 1 && minutes >= 0) {
//					flag = "Fast";
//				}
//				if (minutes > 1 && minutes <= 2) {
//					flag = "Normal";
//				}
//				if (minutes > 2 && minutes <= 10) {
//					flag = "Slow";
//				}
//				if (minutes > 10) {
//					flag = "Dead Slow";
//				}
//		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
//						+ description);
//			if (accountProfile.isPresent()) {
//				ledgerReportTP.setAccountProfile(accountProfile.get());
//			}
//
//			Optional<Division> division = divisionRepository.findByCompanyIdAndNameIgnoreCase(
//					SecurityUtils.getCurrentUsersCompanyId(), ledgerReportTPDTO.getDivisionName());
//			if (division.isPresent()) {
//				ledgerReportTP.setDivision(division.get());
//			}
//
//			ledgerReportTP.setVoucheNo(ledgerReportTPDTO.getVoucheNo());
//			ledgerReportTP.setVoucherDate(ledgerReportTPDTO.getVoucherDate());
//			ledgerReportTP.setAmount(ledgerReportTPDTO.getAmount());
//			ledgerReportTP.setDebitCredit(ledgerReportTPDTO.getDebitCredit());
//			ledgerReportTP.setNarration(ledgerReportTPDTO.getNarration());
//			ledgerReportTP.setType(ledgerReportTPDTO.getType());
//
//			ledgerReportTP = ledgerReportTPRepository.save(ledgerReportTP);
//			LedgerReportTPDTO result = ledgerReportTPMapper.ledgerReportTPToLedgerReportTPDTO(ledgerReportTP);
//			return result;
//		}).orElse(null);
		return null;
	}

	@Override
	public List<LedgerReportTPDTO> findAllByCompany() {
		log.debug("Request to get all LedgerReportTPs");
		List<LedgerReportTP> ledgerReportTPList = ledgerReportTPRepository.findAllByCompanyId();
		List<LedgerReportTPDTO> result = ledgerReportTPMapper.ledgerReportTPsToLedgerReportTPDTOs(ledgerReportTPList);
		return result;
	}

	@Override
	public LedgerReportTPDTO findOneById(Long id) {
		log.debug("Request to get LedgerReportTP : {}", id);
		LedgerReportTP ledgerReportTP = ledgerReportTPRepository.findOne(id);
		LedgerReportTPDTO ledgerReportTPDTO = ledgerReportTPMapper.ledgerReportTPToLedgerReportTPDTO(ledgerReportTP);
		return ledgerReportTPDTO;
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete LedgerReportTP : {}", id);
		ledgerReportTPRepository.findOneById(id).ifPresent(ledgerReportTP -> {
			ledgerReportTPRepository.delete(ledgerReportTP.getId());
		});
	}

	@Override
	public List<LedgerReportTPDTO> findAllByCompanyIdAndTypeAndAccountProfilePid(String narration,
			String accountPid) {
		List<LedgerReportTP> ledgerReportTPList = ledgerReportTPRepository
				.findAllByCompanyIdAndTypeAndAccountProfilePid(narration, accountPid);
		List<LedgerReportTPDTO> result = ledgerReportTPMapper.ledgerReportTPsToLedgerReportTPDTOs(ledgerReportTPList);
		return result;
	}

}
