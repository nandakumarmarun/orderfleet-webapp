package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Implementation for managing AccountingVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class AccountingVoucherHeaderServiceImpl implements AccountingVoucherHeaderService {

	private final Logger log = LoggerFactory.getLogger(AccountingVoucherHeaderServiceImpl.class);

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private BankRepository bankRepository;

	/**
	 * Save a accountingVoucherHeader.
	 * 
	 * @param accountingVoucherHeaderDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountingVoucherHeaderDTO save(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		log.debug("Request to save AccountingVoucherHeader : {}", accountingVoucherHeaderDTO);

		AccountingVoucherHeader accountingVoucherHeader = new AccountingVoucherHeader();
		// set pid
		accountingVoucherHeader.setPid(AccountingVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
		accountingVoucherHeader.setAccountProfile(
				accountProfileRepository.findOneByPid(accountingVoucherHeaderDTO.getAccountProfilePid()).get());
		accountingVoucherHeader.setCreatedBy(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		accountingVoucherHeader.setCreatedDate(accountingVoucherHeaderDTO.getCreatedDate());
		accountingVoucherHeader
				.setDocument(documentRepository.findOneByPid(accountingVoucherHeaderDTO.getDocumentPid()).get());
		accountingVoucherHeader.setEmployee(
				employeeProfileRepository.findEmployeeProfileByUser(accountingVoucherHeader.getCreatedBy()));
		accountingVoucherHeader.setOutstandingAmount(accountingVoucherHeaderDTO.getOutstandingAmount());
		accountingVoucherHeader.setRemarks(accountingVoucherHeaderDTO.getRemarks());
		accountingVoucherHeader.setTotalAmount(accountingVoucherHeaderDTO.getTotalAmount());
		// set company
		accountingVoucherHeader.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));

		accountingVoucherHeader.setDocumentNumberLocal(accountingVoucherHeaderDTO.getDocumentNumberLocal());
		// Set unique server number same as document number local
		accountingVoucherHeader.setDocumentNumberServer(accountingVoucherHeaderDTO.getDocumentNumberLocal());
		// set voucher details
		List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<AccountingVoucherDetail>();
		List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs = accountingVoucherHeaderDTO
				.getAccountingVoucherDetails();
		for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherDetailDTOs) {
			AccountingVoucherDetail accountingVoucherDetail = new AccountingVoucherDetail();
			accountingVoucherDetail.setAmount(accountingVoucherDetailDTO.getAmount());
			accountingVoucherDetail
					.setBy(accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getByAccountPid()).get());
			accountingVoucherDetail
					.setTo(accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getToAccountPid()).get());
			accountingVoucherDetail.setInstrumentDate(accountingVoucherDetailDTO.getInstrumentDate());
			accountingVoucherDetail.setInstrumentNumber(accountingVoucherDetailDTO.getInstrumentNumber());
			accountingVoucherDetail.setMode(accountingVoucherDetailDTO.getMode());
			accountingVoucherDetail.setReferenceNumber(accountingVoucherDetailDTO.getReferenceNumber());
			accountingVoucherDetail.setRemarks(accountingVoucherDetailDTO.getRemarks());
			accountingVoucherDetail.setVoucherDate(accountingVoucherDetailDTO.getVoucherDate());
			accountingVoucherDetail.setVoucherNumber(accountingVoucherDetailDTO.getVoucherNumber());
			// set bank
			if (accountingVoucherDetailDTO.getBankPid() != null) {
				Bank bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid()).get();
				accountingVoucherDetail.setBank(bank);
			} else {
				accountingVoucherDetail.setBankName(accountingVoucherDetailDTO.getBankName());
			}
			accountingVoucherDetails.add(accountingVoucherDetail);
		}
		accountingVoucherHeader.setAccountingVoucherDetails(accountingVoucherDetails);
		accountingVoucherHeader = accountingVoucherHeaderRepository.save(accountingVoucherHeader);
		AccountingVoucherHeaderDTO result = new AccountingVoucherHeaderDTO(accountingVoucherHeader);
		return result;
	}

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountingVoucherHeader> findAll(Pageable pageable) {
		log.debug("Request to get all AccountingVoucherHeaders");
		Page<AccountingVoucherHeader> result = accountingVoucherHeaderRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompany() {
		log.debug("Request to get all AccountingVoucherHeaders");
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountingVoucherHeaderDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all AccountingVoucherHeaders");
		Page<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderList = accountingVoucherHeaders.getContent().stream()
				.map(AccountingVoucherHeaderDTO::new).collect(Collectors.toList());
		Page<AccountingVoucherHeaderDTO> result = new PageImpl<AccountingVoucherHeaderDTO>(accountingVoucherHeaderList,
				pageable, accountingVoucherHeaders.getTotalElements());
		return result;
	}

	/**
	 * Get one accountingVoucherHeader by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public AccountingVoucherHeaderDTO findOne(Long id) {
		log.debug("Request to get AccountingVoucherHeader : {}", id);
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository.findOne(id);
		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(accountingVoucherHeader);
		accountingVoucherHeaderDTO.setAccountingVoucherDetails(accountingVoucherHeader.getAccountingVoucherDetails()
				.stream().map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
		return accountingVoucherHeaderDTO;
	}

	/**
	 * Get one accountingVoucherHeader by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountingVoucherHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountingVoucherHeader by pid : {}", pid);
		return accountingVoucherHeaderRepository.findOneByPid(pid).map(accountingVoucherHeader -> {
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
					accountingVoucherHeader);
			accountingVoucherHeaderDTO.setAccountingVoucherDetails(accountingVoucherHeader.getAccountingVoucherDetails()
					.stream().map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
			return accountingVoucherHeaderDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid, fromDate,
						toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountPid, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateAccountingVoucherHeaderStatus(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findOneByPid(accountingVoucherHeaderDTO.getPid()).get();
		accountingVoucherHeader.setStatus(true);
		accountingVoucherHeader.setTallyDownloadStatus(accountingVoucherHeaderDTO.getTallyDownloadStatus());
		accountingVoucherHeaderRepository.save(accountingVoucherHeader);
	}

	@Override
	public List<AccountingVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String accountingVoucherHeaderPid) {

		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(accountingVoucherHeaderPid);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(documentPid, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(String userPid,
			String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid,
						documentPid, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidDocumentPidAndDateBetween(String accountPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(accountPid, documentPid,
						fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public Set<Document> findDocumentsByUserIdIn(List<Long> userIds) {
		return accountingVoucherHeaderRepository.findDocumentsByUserIdIn(userIds);
	}

	@Override
	public AccountingVoucherHeaderDTO findAllByCompanyIdAccountPidDocumentPidAndExecutiveTaskExecutionIn(String accountPid,
			String documentPid, List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs) {
		List<String>executiveTaskExecutionPids=new ArrayList<>();
		for(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO:executiveTaskExecutionDTOs) {
			executiveTaskExecutionPids.add(executiveTaskExecutionDTO.getPid());
		}
		AccountingVoucherHeader accountingVoucherHeader=accountingVoucherHeaderRepository.getByDocumentPidAndAccountProfilePidAndExecutiveTaskExecutionIn(documentPid, accountPid, executiveTaskExecutionPids);
		AccountingVoucherHeaderDTO result=new AccountingVoucherHeaderDTO(accountingVoucherHeader);
		return result;
	}

	@Override
	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(
			List<String> userPids, String accountPid, List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPids, accountPid,
						documentPids, tallyDownloadStatus, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	} 

	@Override
	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidDocumentPidAndDateBetween(List<String> userPids,
			List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.getAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPids,
						documentPids, tallyDownloadStatus, fromDate, toDate);
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
