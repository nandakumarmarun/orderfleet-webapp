package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BankService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.mapper.BankMapper;

/**
 * Service Implementation for managing Bank.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class BankServiceImpl implements BankService {

	private final Logger log = LoggerFactory.getLogger(BankServiceImpl.class);

	@Inject
	private BankRepository bankRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private BankMapper bankMapper;

	/**
	 * Save a bank.
	 * 
	 * @param bankDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public BankDTO save(BankDTO bankDTO) {
		log.debug("Request to save Bank : {}", bankDTO);
		bankDTO.setPid(BankService.PID_PREFIX + RandomUtil.generatePid()); // set
		Bank bank = bankMapper.bankDTOToBank(bankDTO);
		bank.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		bank = bankRepository.save(bank);
		BankDTO result = bankMapper.bankToBankDTO(bank);
		return result;
	}

	/**
	 * Update a bank.
	 * 
	 * @param bankDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public BankDTO update(BankDTO bankDTO) {
		log.debug("Request to Update Bank : {}", bankDTO);
		return bankRepository.findOneByPid(bankDTO.getPid()).map(bank -> {
			bank.setName(bankDTO.getName());
			bank.setAlias(bankDTO.getAlias());
			bank.setDescription(bankDTO.getDescription());

			bank = bankRepository.save(bank);
			BankDTO result = bankMapper.bankToBankDTO(bank);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the banks.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BankDTO> findAllByCompany() {
		log.debug("Request to get all Banks");
		List<Bank> bankList = bankRepository.findAllByCompanyId();
		List<BankDTO> result = bankMapper.banksToBankDTOs(bankList);
		return result;
	}

	/**
	 * Get all the banks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<BankDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Banks");
		Page<Bank> banks = bankRepository.findAllByCompanyIdOrderByBankName(pageable);
		Page<BankDTO> result = new PageImpl<BankDTO>(bankMapper.banksToBankDTOs(banks.getContent()), pageable,
				banks.getTotalElements());
		return result;
	}

	/**
	 * Get one bank by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public BankDTO findOne(Long id) {
		log.debug("Request to get Bank : {}", id);
		Bank bank = bankRepository.findOne(id);
		BankDTO bankDTO = bankMapper.bankToBankDTO(bank);
		return bankDTO;
	}

	/**
	 * Get one bank by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<BankDTO> findOneByPid(String pid) {
		log.debug("Request to get Bank by pid : {}", pid);
		return bankRepository.findOneByPid(pid).map(bank -> {
			BankDTO bankDTO = bankMapper.bankToBankDTO(bank);
			return bankDTO;
		});
	}

	/**
	 * Get one bank by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<BankDTO> findByName(String name) {
		log.debug("Request to get Bank by name : {}", name);
		return bankRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(bank -> {
					BankDTO bankDTO = bankMapper.bankToBankDTO(bank);
					return bankDTO;
				});
	}

	/**
	 * Delete the bank by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Bank : {}", pid);
		bankRepository.findOneByPid(pid).ifPresent(bank -> {
			bankRepository.delete(bank.getId());
		});
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 * Update the Bank status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public BankDTO updateBankStatus(String pid, boolean activate) {
		log.debug("Request to update bank status: {}");
		return bankRepository.findOneByPid(pid).map(bank -> {
			bank.setActivated(activate);
			bank = bankRepository.save(bank);
			BankDTO result = bankMapper.bankToBankDTO(bank);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 *
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<BankDTO> findAllByCompanyAndActivatedBankOrderByName(Pageable pageable, boolean active) {
		log.debug("Request to get Activated Bank ");
		Page<Bank> pageBanks = bankRepository.findAllByCompanyAndActivatedBankOrderByName(pageable, active);
		Page<BankDTO> pageBankDTOs = new PageImpl<BankDTO>(bankMapper.banksToBankDTOs(pageBanks.getContent()),pageable,pageBanks.getTotalElements());
		return pageBankDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<BankDTO> findAllByCompanyAndDeactivatedBank(boolean deactive) {
		log.debug("Request to get Deactivated Bank ");
		List<Bank> banks = bankRepository.findAllByCompanyAndDeactivatedBank(deactive);
		List<BankDTO> bankDTOs = bankMapper.banksToBankDTOs(banks);
		return bankDTOs;
	}
}
