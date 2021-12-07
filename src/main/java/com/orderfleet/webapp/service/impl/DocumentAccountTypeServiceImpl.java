package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;

/**
 * Service Implementation for managing DocumentAccountType.
 * 
 * @author Muhammed Riyas T
 * @since August 11, 2016
 */
@Service
@Transactional
public class DocumentAccountTypeServiceImpl implements DocumentAccountTypeService {
	private final Logger log = LoggerFactory.getLogger(DocumentAccountTypeServiceImpl.class);

	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentAccountTypeRepository documentAccountTypeRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountTypeMapper accountTypeMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String documentPid, String assignedAccountTypes, AccountTypeColumn accountTypeColumn) {
		log.debug("Request to save Document AccountTypes");

		Document document = documentRepository.findOneByPid(documentPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] accountTypes = assignedAccountTypes.split(",");
		List<DocumentAccountType> documentAccountTypes = new ArrayList<>();
		for (String accountTypePid : accountTypes) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			AccountType accountType = accountTypeRepository.findOneByPid(accountTypePid).get();
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

			documentAccountTypes.add(new DocumentAccountType(document, accountType, accountTypeColumn, company));
		}
		documentAccountTypeRepository.deleteByDocumentPidAndAccountTypeColumn(documentPid, accountTypeColumn);
		documentAccountTypeRepository.save(documentAccountTypes);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findAccountTypesByDocumentPidAndAccountTypeColumn(String documentPid,
			AccountTypeColumn accountTypeColumn) {
		log.debug("Request to get all AccountTypes under in a documents");
		List<AccountType> accountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, accountTypeColumn);
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes);
		return result;
	}

	/**
	 * Get all the DocumentAccountType.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentAccountType> findAllByCompany() {
		log.debug("Request to get all Document AccountType");
		List<DocumentAccountType> documentAccountTypes = documentAccountTypeRepository.findAllByCompanyId();
		return documentAccountTypes;
	}
	
	/**
	 * Get all the DocumentAccountType.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentAccountType> findAllByCompanyLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all Document AccountType");
		List<DocumentAccountType> documentAccountTypes = documentAccountTypeRepository.findAllByCompanyIdLastModifiedDate(lastModifiedDate);
		return documentAccountTypes;
	}
}
