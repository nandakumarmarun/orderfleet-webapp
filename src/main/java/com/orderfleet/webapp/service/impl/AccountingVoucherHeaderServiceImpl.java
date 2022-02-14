package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	/**
	 * Save a accountingVoucherHeader.
	 * 
	 * @param accountingVoucherHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountingVoucherHeaderDTO save(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		log.debug("Request to save AccountingVoucherHeader : {}", accountingVoucherHeaderDTO);

		AccountingVoucherHeader accountingVoucherHeader = new AccountingVoucherHeader();
		// set pid
		accountingVoucherHeader.setPid(AccountingVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVoucherHeader.setAccountProfile(
				accountProfileRepository.findOneByPid(accountingVoucherHeaderDTO.getAccountProfilePid()).get());
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
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "BANK_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 = "get one by pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				Bank bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid()).get();
				String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
				logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
						+ "," + description1);
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
	 * @param pageable the pagination information
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher by companyid order by created date";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountingVoucherHeaderDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all AccountingVoucherHeaders");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher by companyid order by created date using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
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

		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderList = accountingVoucherHeaders.getContent().stream()
				.map(AccountingVoucherHeaderDTO::new).collect(Collectors.toList());
		Page<AccountingVoucherHeaderDTO> result = new PageImpl<AccountingVoucherHeaderDTO>(accountingVoucherHeaderList,
				pageable, accountingVoucherHeaders.getTotalElements());
		return result;
	}

	/**
	 * Get one accountingVoucherHeader by id.
	 *
	 * @param id the id of the entity
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
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountingVoucherHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountingVoucherHeader by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		return accountingVoucherHeaderRepository.findOneByPid(pid).map(accountingVoucherHeader -> {
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
					accountingVoucherHeader);
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
			accountingVoucherHeaderDTO.setAccountingVoucherDetails(accountingVoucherHeader.getAccountingVoucherDetails()
					.stream().map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
			return accountingVoucherHeaderDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher by companyid Between two created date";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher ByCompanyIdUserPidAccountPidAndDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid, fromDate,
						toDate);
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
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher ByCompanyId,UserPid,AndDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher ByCompanyId  AccountPid AndDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountPid, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	public void updateAccountingVoucherHeaderStatus(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findOneByPid(accountingVoucherHeaderDTO.getPid()).get();
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

		accountingVoucherHeader.setStatus(true);
		accountingVoucherHeader.setTallyDownloadStatus(accountingVoucherHeaderDTO.getTallyDownloadStatus());
		accountingVoucherHeaderRepository.save(accountingVoucherHeader);
	}

	@Override
	public List<AccountingVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String accountingVoucherHeaderPid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = " get all AccVoucher By ExecutiveTask Execution Pid ";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(accountingVoucherHeaderPid);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher By CompanyId DocumentPid And DateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(documentPid, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(String userPid,
			String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_124" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all  AccVoucher By CompanyId  UserPid AccountPid DocumentPid And DateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid,
						documentPid, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;

	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher ByCompanyId UserPid DocumentPid And DateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);
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
		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidDocumentPidAndDateBetween(String accountPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_126" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all AccVoucher ByCompanyId AccountPid DocumentPid AndDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(accountPid, documentPid,
						fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();
		result.stream().forEach(acc -> {
			if (comp) {
				acc.setAccountProfileName(acc.getDescription());
			} else {
				acc.setAccountProfileName(acc.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	public Set<Document> findDocumentsByUserIdIn(List<Long> userIds) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_127" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get  Accvoucher Documents By User Id";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Set<Document> documents = accountingVoucherHeaderRepository.findDocumentsByUserIdIn(userIds);

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
		return documents;
	}

	@Override
	public AccountingVoucherHeaderDTO findAllByCompanyIdAccountPidDocumentPidAndExecutiveTaskExecutionIn(
			String accountPid, String documentPid, List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs) {
		List<String> executiveTaskExecutionPids = new ArrayList<>();
		for (ExecutiveTaskExecutionDTO executiveTaskExecutionDTO : executiveTaskExecutionDTOs) {
			executiveTaskExecutionPids.add(executiveTaskExecutionDTO.getPid());
		}
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_136" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get AccVoucher By DocumentPid And AccountProfilePid And ExecutiveTaskExecutionIn";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.getByDocumentPidAndAccountProfilePidAndExecutiveTaskExecutionIn(documentPid, accountPid,
						executiveTaskExecutionPids);
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
		AccountingVoucherHeaderDTO result = new AccountingVoucherHeaderDTO(accountingVoucherHeader);
		return result;
	}

	@Override
	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(
			List<String> userPids, String accountPid, List<String> documentPids,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_145" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyid ,userPid,accountPid and documentPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPids, accountPid,
						documentPids, tallyDownloadStatus, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(avh -> {
			if (comp) {
				avh.setAccountProfileName(avh.getDescription());
			} else {
				avh.setAccountProfileName(avh.getAccountProfileName());
			}
		});
		return result;
	}

	@Override
	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidDocumentPidAndDateBetween(List<String> userPids,
			List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate,
			LocalDateTime toDate) {

		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyid ,userPid and documentPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.getAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPids, documentPids,
						tallyDownloadStatus, fromDate, toDate);
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

		List<AccountingVoucherHeaderDTO> result = accountingVoucherHeaders.stream().map(AccountingVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(avh -> {
			if (comp) {
				avh.setAccountProfileName(avh.getDescription());
			} else {
				avh.setAccountProfileName(avh.getAccountProfileName());
			}
		});
		return result;
	}

	public boolean getCompanyCofig() {
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if (optconfig.isPresent()) {
			if (Boolean.valueOf(optconfig.get().getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateAccountingVoucherHeaderSalesManagementStatus(
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findOneByPid(accountingVoucherHeaderDTO.getPid()).get();
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

		accountingVoucherHeader.setSalesManagementStatus(accountingVoucherHeaderDTO.getSalesManagementStatus());
		accountingVoucherHeaderRepository.save(accountingVoucherHeader);

	}

}
