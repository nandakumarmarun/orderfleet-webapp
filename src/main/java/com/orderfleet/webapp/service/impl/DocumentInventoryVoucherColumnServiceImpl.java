package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentInventoryVoucherColumnRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.InventoryVoucherColumnRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherColumnDTO;

/**
 * Service Implementation for managing DocumentInventoryVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 */
@Service
@Transactional
public class DocumentInventoryVoucherColumnServiceImpl implements DocumentInventoryVoucherColumnService {

	private final Logger log = LoggerFactory.getLogger(DocumentInventoryVoucherColumnServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentInventoryVoucherColumnRepository documentInventoryVoucherColumnRepository;

	@Inject
	private InventoryVoucherColumnRepository inventoryVoucherColumnRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String documentPid, List<InventoryVoucherColumnDTO> assignedInventoryVoucherColumns) {
		log.debug("Request to save DocumentInventory Voucher Columns");

		Document document = documentRepository.findOneByPid(documentPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = new ArrayList<>();

		for (InventoryVoucherColumnDTO inventoryVoucherColumnDTO : assignedInventoryVoucherColumns) {
			InventoryVoucherColumn inventoryVoucherColumn = inventoryVoucherColumnRepository
					.findOneByName(inventoryVoucherColumnDTO.getName());
			documentInventoryVoucherColumns.add(new DocumentInventoryVoucherColumn(document, inventoryVoucherColumn,
					inventoryVoucherColumnDTO.getEnabled(), company, inventoryVoucherColumnDTO.getLabel()));
		}
		documentInventoryVoucherColumnRepository.deleteByDocumentPid(documentPid);
		documentInventoryVoucherColumnRepository.save(documentInventoryVoucherColumns);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherColumn> findInventoryVoucherColumnsByDocumentPid(String documentPid) {
		log.debug("Request to get all DocumentInventoryVoucherColumn under in a documents");
		List<InventoryVoucherColumn> inventoryVoucherColumns = documentInventoryVoucherColumnRepository
				.findInventoryVoucherColumnsByDocumentPid(documentPid);
		return inventoryVoucherColumns;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentInventoryVoucherColumn> findByDocumentPid(String documentPid) {
		return documentInventoryVoucherColumnRepository.findByDocumentPid(documentPid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentInventoryVoucherColumn> findByCompanyId() {
		return documentInventoryVoucherColumnRepository.findAllByCompanyId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		return documentInventoryVoucherColumnRepository.findAllByCompanyIdAndLastModifiedDate(lastModifiedDate);
	}

	@Override
	public List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndDocumentPid(String documentPid) {
		return documentInventoryVoucherColumnRepository.findAllByCompanyIdAndDocumentPid(documentPid);
	}

}
