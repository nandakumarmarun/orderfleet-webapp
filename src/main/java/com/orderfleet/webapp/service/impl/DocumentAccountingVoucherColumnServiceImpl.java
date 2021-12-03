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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.AccountingVoucherColumn;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentAccountingVoucherColumnRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.AccountingVoucherColumnRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherColumnDTO;

/**
 * Service Implementation for managing DocumentAccountingVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
@Service
@Transactional
public class DocumentAccountingVoucherColumnServiceImpl implements DocumentAccountingVoucherColumnService {

	private final Logger log = LoggerFactory.getLogger(DocumentAccountingVoucherColumnServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentAccountingVoucherColumnRepository documentAccountingVoucherColumnRepository;

	@Inject
	private AccountingVoucherColumnRepository accountingVoucherColumnRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String documentPid, List<AccountingVoucherColumnDTO> assignedAccountingVoucherColumns) {
		log.debug("Request to save DocumentAccounting Voucher Columns");

		Document document = documentRepository.findOneByPid(documentPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns = new ArrayList<>();

		for (AccountingVoucherColumnDTO accountingVoucherColumnDTO : assignedAccountingVoucherColumns) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AVC_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			AccountingVoucherColumn accountingVoucherColumn = accountingVoucherColumnRepository
					.findOneByName(accountingVoucherColumnDTO.getName());
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

			documentAccountingVoucherColumns.add(new DocumentAccountingVoucherColumn(document, accountingVoucherColumn,
					accountingVoucherColumnDTO.getEnabled(), company));
		}
		documentAccountingVoucherColumnRepository.deleteByDocumentPid(documentPid);
		documentAccountingVoucherColumnRepository.save(documentAccountingVoucherColumns);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountingVoucherColumn> findAccountingVoucherColumnsByDocumentPid(String documentPid) {
		log.debug("Request to get all DocumentAccountingVoucherColumn under in a documents");
		List<AccountingVoucherColumn> accountingVoucherColumns = documentAccountingVoucherColumnRepository
				.findAccountingVoucherColumnsByDocumentPid(documentPid);
		return accountingVoucherColumns;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentAccountingVoucherColumn> findByDocumentPid(String documentPid) {
		return documentAccountingVoucherColumnRepository.findByDocumentPid(documentPid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentAccountingVoucherColumn> findByCompanyId() {
		return documentAccountingVoucherColumnRepository.findAllByCompanyId();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DocumentAccountingVoucherColumn> findByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		return documentAccountingVoucherColumnRepository.findByCompanyIdAndLastModifiedDate(lastModifiedDate);
	}

	@Override
	public List<DocumentAccountingVoucherColumn> findByCompanyIdAndDocumentPid(String documentPid) {
		return documentAccountingVoucherColumnRepository.findByCompanyIdAndDocumentPid(documentPid);
	}

}
