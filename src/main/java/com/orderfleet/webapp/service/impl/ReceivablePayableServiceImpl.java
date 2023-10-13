package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.mapper.ReceivablePayableMapper;

/**
 * Service Implementation for managing ReceivablePayable.
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */
@Service
@Transactional
public class ReceivablePayableServiceImpl implements ReceivablePayableService {

	private final Logger log = LoggerFactory.getLogger(ReceivablePayableServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	 @Inject
	 private ReceivablePayableMapper receivablePayableMapper;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	/**
	 * Save a receivablePayable.
	 * 
	 * @param receivablePayableDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ReceivablePayableDTO save(ReceivablePayableDTO receivablePayableDTO) {
		log.debug("Request to save ReceivablePayable : {}", receivablePayableDTO);

		ReceivablePayable receivablePayable = new ReceivablePayable();
		// set pid
		receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());

		receivablePayable.setBillOverDue(Long.valueOf(receivablePayableDTO.getBillOverDue()));
		receivablePayable.setReceivablePayableType(receivablePayableDTO.getReceivablePayableType());
		receivablePayable.setReferenceDocumentAmount(receivablePayableDTO.getReferenceDocumentAmount());
		receivablePayable.setReferenceDocumentBalanceAmount(receivablePayableDTO.getReferenceDocumentBalanceAmount());
		receivablePayable.setReferenceDocumentDate(receivablePayableDTO.getReferenceDocumentDate());
		receivablePayable.setReferenceDocumentNumber(receivablePayableDTO.getReferenceDocumentNumber());
		receivablePayable.setReferenceDocumentType(receivablePayableDTO.getReferenceDocumentType());
		// ReceivablePayable receivablePayable =
		// receivablePayableMapper.receivablePayableDTOToReceivablePayable(receivablePayableDTO);
		// set company
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name Ignore case";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> accountProfile = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
				SecurityUtils.getCurrentUsersCompanyId(), receivablePayableDTO.getAccountName());
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
		if (accountProfile.isPresent()) {
			receivablePayable.setAccountProfile(accountProfile.get());
			receivablePayable.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
			receivablePayable = receivablePayableRepository.save(receivablePayable);
			ReceivablePayableDTO result = new ReceivablePayableDTO(receivablePayable);
			return result;
		} else {
			return receivablePayableDTO;
		}
	}

	/**
	 * Update a receivablePayable.
	 * 
	 * @param receivablePayableDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ReceivablePayableDTO update(ReceivablePayableDTO receivablePayableDTO) {
		log.debug("Request to Update ReceivablePayable : {}", receivablePayableDTO);

		return receivablePayableRepository.findOneByPid(receivablePayableDTO.getPid()).map(receivablePayable -> {
			receivablePayable.setReceivablePayableType(receivablePayableDTO.getReceivablePayableType());
			receivablePayable.setReferenceDocumentAmount(receivablePayableDTO.getReferenceDocumentAmount());
			receivablePayable
					.setReferenceDocumentBalanceAmount(receivablePayableDTO.getReferenceDocumentBalanceAmount());
			receivablePayable.setReferenceDocumentDate(receivablePayableDTO.getReferenceDocumentDate());
			receivablePayable.setReferenceDocumentNumber(receivablePayableDTO.getReferenceDocumentNumber());
			receivablePayable.setReferenceDocumentType(receivablePayableDTO.getReferenceDocumentType());
			receivablePayable.setRemarks(receivablePayableDTO.getRemarks());
			receivablePayable.setBillOverDue(Long.valueOf(receivablePayableDTO.getBillOverDue()));
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by compId and name Ignore case";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			receivablePayable.setAccountProfile(accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
					SecurityUtils.getCurrentUsersCompanyId(), receivablePayableDTO.getAccountName()).get());
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
			receivablePayable = receivablePayableRepository.save(receivablePayable);
			ReceivablePayableDTO result = new ReceivablePayableDTO(receivablePayable);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the receivablePayables.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ReceivablePayable> findAll(Pageable pageable) {
		log.debug("Request to get all ReceivablePayables");
		Page<ReceivablePayable> result = receivablePayableRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the receivablePayables.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ReceivablePayableDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ReceivablePayables");
		Page<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByCompanyId(pageable);
		List<ReceivablePayableDTO> receivablePayableDTOs = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables.getContent()) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			receivablePayableDTOs.add(payableDTO);
		}
		Page<ReceivablePayableDTO> result = new PageImpl<ReceivablePayableDTO>(receivablePayableDTOs, pageable,
				receivablePayables.getTotalElements());
		return result;
	}

	/**
	 * Get one receivablePayable by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ReceivablePayableDTO findOne(Long id) {
		log.debug("Request to get ReceivablePayable : {}", id);
		ReceivablePayable receivablePayable = receivablePayableRepository.findOne(id);
		ReceivablePayableDTO receivablePayableDTO = new ReceivablePayableDTO(receivablePayable);
		return receivablePayableDTO;
	}

	/**
	 * Get one receivablePayable by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ReceivablePayableDTO> findOneByPid(String pid) {
		log.debug("Request to get ReceivablePayable by pid : {}", pid);
		return receivablePayableRepository.findOneByPid(pid).map(receivablePayable -> {
			ReceivablePayableDTO receivablePayableDTO = new ReceivablePayableDTO(receivablePayable);
			return receivablePayableDTO;
		});
	}

	/**
	 * Get one receivablePayable by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ReceivablePayableDTO> findByAccountName(String name) {
		log.debug("Request to get ReceivablePayable by name : {}", name);
		return receivablePayableRepository
				.findByCompanyIdAndAccountProfileNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(receivablePayable -> {
					ReceivablePayableDTO receivablePayableDTO = new ReceivablePayableDTO(receivablePayable);
					return receivablePayableDTO;
				});
	}

	/**
	 * Delete the receivablePayable by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ReceivablePayable : {}", pid);
		receivablePayableRepository.findOneByPid(pid).ifPresent(receivablePayable -> {
			receivablePayableRepository.delete(receivablePayable.getId());
		});
	}

	/**
	 * Get all the receivablePayable.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompany() {
		log.debug("Request to get all receivablePayables");
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByCompanyId();
//		List<ReceivablePayableDTO> result = new ArrayList<>();
//		for (ReceivablePayable receivablePayable : receivablePayables) {
//			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
//			result.add(payableDTO);
//		}
		List<ReceivablePayableDTO> result= receivablePayableMapper.receivablePayablesToReceivablePayableDTOs(receivablePayables);
		return result;
	}

	@Override
	public List<ReceivablePayableDTO> findAllByAccountProfilePid(String accountPid) {
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByAccountProfilePid(accountPid);
		List<ReceivablePayableDTO> result = receivablePayableMapper.receivablePayablesToReceivablePayableDTOs(receivablePayables);
//		for (ReceivablePayable receivablePayable : receivablePayables) {
//			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
//			result.add(payableDTO);
//		}
		return result;
	}

	@Override
	public List<ReceivablePayableDTO> findAllByAccountProfilePidAndReceivablePayableType(String accountPid,
			ReceivablePayableType receivablePayableType) {
		List<ReceivablePayable> receivablePayables = receivablePayableRepository
				.findAllByAccountProfilePidAndReceivablePayableType(accountPid, receivablePayableType);
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			result.add(payableDTO);
		}
		return result;
	}

	/**
	 * Get all the receivablePayable.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all receivablePayables");
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByCompanyId(companyId);
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			result.add(payableDTO);
		}
		return result;
	}

	/**
	 * Save a receivablePayable.
	 * 
	 * @param receivablePayableDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ReceivablePayableDTO saveReceivablePayable(ReceivablePayableDTO receivablePayableDTO, Long companyId) {
		log.debug("Request to save ReceivablePayable : {}", receivablePayableDTO);

		ReceivablePayable receivablePayable = new ReceivablePayable();
		// set pid
		receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
		// ReceivablePayable receivablePayable =
		// receivablePayableMapper.receivablePayableDTOToReceivablePayable(receivablePayableDTO);

		receivablePayable.setBillOverDue(Long.valueOf(receivablePayableDTO.getBillOverDue()));
		receivablePayable.setReceivablePayableType(receivablePayableDTO.getReceivablePayableType());
		receivablePayable.setReferenceDocumentAmount(receivablePayableDTO.getReferenceDocumentAmount());
		receivablePayable.setReferenceDocumentBalanceAmount(receivablePayableDTO.getReferenceDocumentBalanceAmount());
		receivablePayable.setReferenceDocumentDate(receivablePayableDTO.getReferenceDocumentDate());
		receivablePayable.setReferenceDocumentNumber(receivablePayableDTO.getReferenceDocumentNumber());
		receivablePayable.setReferenceDocumentType(receivablePayableDTO.getReferenceDocumentType());
		receivablePayable.setRemarks(receivablePayableDTO.getRemarks());
		// set company
		Optional<AccountProfile> accountProfile = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId,
				receivablePayableDTO.getAccountName());
		ReceivablePayableDTO result = new ReceivablePayableDTO();
		if (accountProfile.isPresent()) {
			receivablePayable.setAccountProfile(accountProfile.get());
			receivablePayable.setCompany(companyRepository.findOne(companyId));
			receivablePayable = receivablePayableRepository.save(receivablePayable);
			result = new ReceivablePayableDTO(receivablePayable);
		}
		return result;
	}

	/**
	 * Get all the receivablePayable.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompanyAndlastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all receivablePayables");
		List<ReceivablePayable> receivablePayables = receivablePayableRepository
				.findAllByCompanyAndlastModifiedDate(lastModifiedDate);
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			result.add(payableDTO);
		}
		return result;
	}

	/**
	 * Get all the receivablePayable by AccountProfile of current Location.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompanyAndAccountProfileIn() {
		log.debug("Request to get all receivablePayables by accountProfile");
		List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
				.findAccountProfilesByCurrentUserLocations();
		List<String> accountProfilePids = new ArrayList<>();
		for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
			accountProfilePids.add(accountProfileDTO.getPid());
		}
		List<ReceivablePayable> receivablePayables = new ArrayList<>();
		if (!accountProfilePids.isEmpty()) {
			receivablePayables = receivablePayableRepository.findAllByCompanyIdAndAccountprofileIn(accountProfilePids);
		}
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			result.add(payableDTO);
		}
		return result;
	}

	/**
	 * Get all the receivablePayable by AccountProfile of current Location and
	 * lastModifiedDate .
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompanyAndlastModifiedDateAndAccountProfileIn(
			LocalDateTime lastModifiedDate) {
		log.debug("Request to get all receivablePayables  by accountProfile", lastModifiedDate);
		List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
				.findAccountProfilesByCurrentUserLocations();
		List<String> accountProfilePids = new ArrayList<>();
		for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
			accountProfilePids.add(accountProfileDTO.getPid());
		}
		List<ReceivablePayable> receivablePayables = new ArrayList<>();
		if (!accountProfilePids.isEmpty()) {
			receivablePayables = receivablePayableRepository
					.findAllByCompanyAndlastModifiedDateAndAccountProfileIn(lastModifiedDate, accountProfilePids);
		}
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
			result.add(payableDTO);
		}
		return result;
	}

	@Override
	public ReceivablePayableDTO updateFromPage(ReceivablePayableDTO receivablePayableDTO) {
		return receivablePayableRepository.findOneByPid(receivablePayableDTO.getPid()).map(receivablePayable -> {
			receivablePayable.setReceivablePayableType(receivablePayableDTO.getReceivablePayableType());
			receivablePayable.setReferenceDocumentAmount(receivablePayableDTO.getReferenceDocumentAmount());
			receivablePayable
					.setReferenceDocumentBalanceAmount(receivablePayableDTO.getReferenceDocumentBalanceAmount());
			receivablePayable.setReferenceDocumentDate(receivablePayableDTO.getReferenceDocumentDate());
			receivablePayable.setReferenceDocumentNumber(receivablePayableDTO.getReferenceDocumentNumber());
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name Ignore case";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			receivablePayable.setAccountProfile(accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
					SecurityUtils.getCurrentUsersCompanyId(), receivablePayableDTO.getAccountName()).get());
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
			receivablePayable = receivablePayableRepository.save(receivablePayable);
			ReceivablePayableDTO result = new ReceivablePayableDTO(receivablePayable);
			return result;
		}).orElse(null);
	}

	@Override
	public Integer dueUpdate() {
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAll();
		receivablePayables.forEach(receivablePayable -> {
			LocalDate currentDate = LocalDate.now();
			Long differenceInDays = Math
					.abs(ChronoUnit.DAYS.between(currentDate, receivablePayable.getReferenceDocumentDate()));
			if (receivablePayable.getAccountProfile().getCreditDays() == null
					|| receivablePayable.getAccountProfile().getCreditDays() == 0) {
				receivablePayable.setBillOverDue(differenceInDays);
			} else {
				receivablePayable.setBillOverDue(
						Math.abs(differenceInDays - receivablePayable.getAccountProfile().getCreditDays()));
				receivablePayableRepository.save(receivablePayable);
			}
		});
		return receivablePayables.size();

	}

	@Override
	@Transactional(readOnly = true)
	public List<ReceivablePayableDTO> findAllByCompanyAndDateBetween(LocalDate fromDate, LocalDate toDate) {
		log.debug("Request to get all receivablePayables");
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByCompanyIdAndDateBetween(fromDate,toDate);
//		List<ReceivablePayableDTO> result = new ArrayList<>();
//		for (ReceivablePayable receivablePayable : receivablePayables) {
//			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
//			result.add(payableDTO);
//		}
		List<ReceivablePayableDTO> receivablePayableDTO = receivablePayableMapper.receivablePayablesToReceivablePayableDTOs(receivablePayables);
		return receivablePayableDTO;
	}

	@Override
	public List<ReceivablePayableDTO> findAllByAccountProfilePidAndDateBetween(String accountPid, LocalDate fromDate,
			LocalDate toDate) {
		List<ReceivablePayable> receivablePayables = receivablePayableRepository.findAllByAccountProfilePidAndDateBetween(accountPid,fromDate,toDate);
//		List<ReceivablePayableDTO> result = new ArrayList<>();
//		for (ReceivablePayable receivablePayable : receivablePayables) {
//			ReceivablePayableDTO payableDTO = new ReceivablePayableDTO(receivablePayable);
//			result.add(payableDTO);
//		}
		List<ReceivablePayableDTO> receivablePayableDTO = receivablePayableMapper.receivablePayablesToReceivablePayableDTOs(receivablePayables);
		return receivablePayableDTO;
	}

	@Override
	public void saveReceivableFromTransaction(List<InventoryVoucherHeader> tsTransactionWrapper) {
		System.out.println("Enter here to save Transaction details");
          List<ReceivablePayable> receivablePayables = new ArrayList<>();
		List<String> pids = tsTransactionWrapper.stream().map(ivh -> ivh.getReceiverAccount().getPid()).collect(Collectors.toList());
		Optional<AccountProfile> accounts = accountProfileRepository.findOneByPid(pids);
		tsTransactionWrapper.forEach(inventoryVoucherHeader -> {

			ReceivablePayable receivablePayable = new ReceivablePayable();
			receivablePayable.setPid(ReceivablePayableService.PID_PREFIX +RandomUtil.generatePid());
			receivablePayable.setAccountProfile(inventoryVoucherHeader.getReceiverAccount());
			receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
			receivablePayable.setReferenceDocumentNumber(inventoryVoucherHeader.getDocumentNumberLocal());
			receivablePayable.setReferenceDocumentDate(inventoryVoucherHeader.getDocumentDate().toLocalDate());
			receivablePayable.setReferenceDocumentType(inventoryVoucherHeader.getReferenceDocumentType());
			receivablePayable.setReferenceDocumentAmount(inventoryVoucherHeader.getDocumentTotal());
			receivablePayable.setReferenceDocumentBalanceAmount(inventoryVoucherHeader.getDocumentTotal());
			receivablePayable.setCompany(inventoryVoucherHeader.getCompany());
			receivablePayable.setSupplierAccountProfile(inventoryVoucherHeader.getSupplierAccount());
			receivablePayable.setBillOverDue(0L);
			accounts.get().setClosingBalance(accounts.get().getClosingBalance() + inventoryVoucherHeader.getDocumentTotal());
			receivablePayables.add(receivablePayable);
		});
		accountProfileRepository.save(accounts.get());
      receivablePayableRepository.save(receivablePayables);
	  System.out.println("Successfully Saved ....");
	}

	@Override
	public void UpdateReceivablesByReceipt(List<AccountingVoucherHeader> accountingVoucherHeaders) {

//		List<String> pidList;
//		accountingVoucherHeaders.forEach(accountingVoucherHeader -> {
//			accountingVoucherHeader.getAccountingVoucherDetails()
//					.forEach(accountingVoucherDetail -> {
//						 pidList = accountingVoucherDetail.getAccountingVoucherAllocations()
//								.stream().map(acc -> acc.getReceivablePayablePid())
//								.collect(Collectors.toList());
//			});
//		});
		List<String> pidList = new ArrayList<>();

		accountingVoucherHeaders.forEach(accountingVoucherHeader -> {
			accountingVoucherHeader.getAccountingVoucherDetails().forEach(accountingVoucherDetail -> {
				accountingVoucherDetail.getAccountingVoucherAllocations().forEach(acc -> {
					pidList.add(acc.getReceivablePayablePid());
				});
			});
		});


		System.out.println("pidList :"+pidList);
		Optional<ReceivablePayable> receivablePayable = receivablePayableRepository.findOneByPid(pidList);
		System.out.println("AMount :"+receivablePayable.get().getReferenceDocumentAmount());
		accountingVoucherHeaders.forEach(accountingVoucherHeader -> {
			if(accountingVoucherHeader.getTotalAmount() == receivablePayable.get().getReferenceDocumentAmount())
			{
				receivablePayableRepository.delete(receivablePayable.get());
			}
			else{
				double amount = receivablePayable.get().getReferenceDocumentAmount() - accountingVoucherHeader.getTotalAmount();
			receivablePayable.get().setReferenceDocumentAmount(amount);
			receivablePayable.get().setReferenceDocumentBalanceAmount(amount);
			receivablePayableRepository.save(receivablePayable.get());

			}
			System.out.println("Updating Receipt amount");
		});

	}
}
