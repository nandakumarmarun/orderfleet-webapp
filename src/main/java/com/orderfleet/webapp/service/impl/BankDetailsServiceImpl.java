package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.orderfleet.webapp.domain.BankDetails;
import com.orderfleet.webapp.repository.BankDetailsRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BankDetailsService;
import com.orderfleet.webapp.service.BankService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.BankDetailsDTO;
import com.orderfleet.webapp.web.rest.mapper.BankMapper;

/**
 * Service Implementation for managing BankDetails.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class BankDetailsServiceImpl implements BankDetailsService {

	private final Logger log = LoggerFactory.getLogger(BankDetailsServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private BankDetailsRepository bankDetailsRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private BankMapper bankMapper;

	/**
	 * Save a bankDetails.
	 * 
	 * @param bankDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public BankDetailsDTO save(BankDetailsDTO bankDTO) {
		log.debug("Request to save BankDetails : {}", bankDTO);
		bankDTO.setPid(BankDetailsService.PID_PREFIX + RandomUtil.generatePid()); // set
		BankDetails bankDetails = bankDTOToBank(bankDTO);
		bankDetails.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		bankDetails = bankDetailsRepository.save(bankDetails);
		BankDetailsDTO result = bankToBankDetailsDTO(bankDetails);
		return result;
	}

	/**
	 * Update a bankDetails.
	 * 
	 * @param bankDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public BankDetailsDTO update(BankDetailsDTO bankDetailsDTO) {

		log.debug("Request to Update BankDetails : {}", bankDetailsDTO);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		BankDetailsDTO bdDTO = bankDetailsRepository.findOneByPid(bankDetailsDTO.getPid()).map(bankDetails -> {
			bankDetails.setAccountHolderName(bankDetailsDTO.getAccountHolderName());
			bankDetails.setAccountNumber(bankDetailsDTO.getAccountNumber());
			bankDetails.setBankName(bankDetailsDTO.getBankName());
			bankDetails.setIfscCode(bankDetailsDTO.getIfscCode());
			bankDetails.setPhoneNumber(bankDetailsDTO.getPhoneNumber());
			bankDetails.setSwiftCode(bankDetailsDTO.getSwiftCode());
			bankDetails.setBranch(bankDetailsDTO.getBranch());

			bankDetails = bankDetailsRepository.save(bankDetails);
			BankDetailsDTO result = bankToBankDetailsDTO(bankDetails);
			return result;
		}).orElse(null);
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
					return bdDTO;
	}

	/**
	 * Get all the banks.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BankDetailsDTO> findAllByCompany() {
		log.debug("Request to get all Banks");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BD_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<BankDetails> bankList = bankDetailsRepository.findAllByCompanyId();
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

		List<BankDetailsDTO> result = banksToBankDetailsDTOs(bankList);
		return result;
	}

	/**
	 * Get one bankDetails by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public BankDetailsDTO findOne(Long id) {
		log.debug("Request to get BankDetails : {}", id);
		BankDetails bankDetails = bankDetailsRepository.findOne(id);
		BankDetailsDTO bankDTO = bankToBankDetailsDTO(bankDetails);
		return bankDTO;
	}

	/**
	 * Get one bankDetails by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<BankDetailsDTO> findOneByPid(String pid) {
		log.debug("Request to get BankDetails by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<BankDetailsDTO> bankDetail= bankDetailsRepository.findOneByPid(pid).map(bankDetails -> {
			BankDetailsDTO bankDTO = bankToBankDetailsDTO(bankDetails);
			return bankDTO;
		});
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
				return bankDetail;

	}

	/**
	 * Get one bankDetails by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<BankDetailsDTO> findByName(String name) {
		log.debug("Request to get BankDetails by name : {}", name);
//		return bankDetailsRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
//				.map(bankDetails -> {
//					BankDetailsDTO bankDTO = bankToBankDetailsDTO(bankDetails);
//					return bankDTO;
//				});
		return null;
	}

	/**
	 * Delete the bankDetails by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete BankDetails : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "BD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		bankDetailsRepository.findOneByPid(pid).ifPresent(bankDetails -> {
			bankDetailsRepository.delete(bankDetails.getId());
		});
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

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<BankDetailsDTO> findAllByCompanyAndDeactivatedBank(boolean deactive) {
		log.debug("Request to get Deactivated BankDetails ");
//		List<BankDetails> banks = bankDetailsRepository.findAllByCompanyAndDeactivatedBank(deactive);
//		List<BankDetailsDTO> bankDTOs = banksToBankDetailsDTOs(banks);
//		return bankDTOs;
		return null;
	}

	private List<BankDetailsDTO> banksToBankDetailsDTOs(List<BankDetails> bankDetailList) {
		List<BankDetailsDTO> bankDetailsDTO = new ArrayList<>();

		for (BankDetails bankDetails : bankDetailList) {
			bankDetailsDTO.add(bankToBankDetailsDTO(bankDetails));
		}

		return bankDetailsDTO;
	}

	private BankDetails bankDTOToBank(BankDetailsDTO bankDetailsDTO) {

		BankDetails bankDetails = new BankDetails();

		bankDetails.setAccountHolderName(bankDetailsDTO.getAccountHolderName());
		bankDetails.setAccountNumber(bankDetailsDTO.getAccountNumber());
		bankDetails.setBankName(bankDetailsDTO.getBankName());
		bankDetails.setIfscCode(bankDetailsDTO.getIfscCode());
		bankDetails.setPhoneNumber(bankDetailsDTO.getPhoneNumber());
		bankDetails.setPid(bankDetailsDTO.getPid());
		bankDetails.setSwiftCode(bankDetailsDTO.getSwiftCode());
		bankDetails.setBranch(bankDetailsDTO.getBranch());

		return bankDetails;
	}

	private BankDetailsDTO bankToBankDetailsDTO(BankDetails bankDetails) {
		BankDetailsDTO bankDetailsDTO = new BankDetailsDTO();

		bankDetailsDTO.setAccountHolderName(bankDetails.getAccountHolderName());
		bankDetailsDTO.setAccountNumber(bankDetails.getAccountNumber());
		bankDetailsDTO.setBankName(bankDetails.getBankName());
		bankDetailsDTO.setIfscCode(bankDetails.getIfscCode());
		bankDetailsDTO.setPhoneNumber(bankDetails.getPhoneNumber());
		bankDetailsDTO.setPid(bankDetails.getPid());
		bankDetailsDTO.setSwiftCode(bankDetails.getSwiftCode());
		bankDetailsDTO.setBranch(bankDetails.getBranch());

		return bankDetailsDTO;
	}
}
