package com.orderfleet.webapp.service.impl;

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

	@Override
	public LedgerReportTPDTO update(LedgerReportTPDTO ledgerReportTPDTO) {
		log.debug("Request to Update LedgerReportTP : {}", ledgerReportTPDTO);

		return ledgerReportTPRepository.findOneById(ledgerReportTPDTO.getId()).map(ledgerReportTP -> {
			Optional<AccountProfile> accountProfile = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
					SecurityUtils.getCurrentUsersCompanyId(), ledgerReportTPDTO.getAccountProfileName());
			if (accountProfile.isPresent()) {
				ledgerReportTP.setAccountProfile(accountProfile.get());
			}

			Optional<Division> division = divisionRepository.findByCompanyIdAndNameIgnoreCase(
					SecurityUtils.getCurrentUsersCompanyId(), ledgerReportTPDTO.getDivisionName());
			if (division.isPresent()) {
				ledgerReportTP.setDivision(division.get());
			}

			ledgerReportTP.setVoucheNo(ledgerReportTPDTO.getVoucheNo());
			ledgerReportTP.setVoucherDate(ledgerReportTPDTO.getVoucherDate());
			ledgerReportTP.setAmount(ledgerReportTPDTO.getAmount());
			ledgerReportTP.setDebitCredit(ledgerReportTPDTO.getDebitCredit());
			ledgerReportTP.setNarration(ledgerReportTPDTO.getNarration());
			ledgerReportTP.setType(ledgerReportTPDTO.getType());

			ledgerReportTP = ledgerReportTPRepository.save(ledgerReportTP);
			LedgerReportTPDTO result = ledgerReportTPMapper.ledgerReportTPToLedgerReportTPDTO(ledgerReportTP);
			return result;
		}).orElse(null);
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
