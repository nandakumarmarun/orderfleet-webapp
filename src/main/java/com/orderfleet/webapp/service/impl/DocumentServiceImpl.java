package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentStockCalculation;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	private final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private DocumentStockCalculationRepository documentStockCalculationRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	/**
	 * Save a document.
	 * 
	 * @param documentDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DocumentDTO save(DocumentDTO documentDTO) {
		log.debug("Request to save Document : {}", documentDTO);

		// set pid
		documentDTO.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
		Document document = documentMapper.documentDTOToDocument(documentDTO);

		if (documentDTO.getHeaderImage() != null) {
			document.setHeaderImage(documentDTO.getHeaderImage());
			document.setHeaderImageContentType(documentDTO.getHeaderImageContentType());
		}
		if (documentDTO.getFooterImage() != null) {
			document.setFooterImage(documentDTO.getFooterImage());
			document.setFooterImageContentType(documentDTO.getFooterImageContentType());
		}
		// set company
		document.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		document = documentRepository.save(document);
		DocumentDTO result = documentMapper.documentToDocumentDTO(document);
		return result;
	}

	@Override
	public DocumentStockCalculationDTO saveDocumentStockCalculation(
			DocumentStockCalculationDTO documentStockCalculationDTO) {
		DocumentStockCalculation documentStockCalculation = null;
		Optional<DocumentStockCalculation> stockCalculation = documentStockCalculationRepository
				.findOneByDocumentPid(documentStockCalculationDTO.getDocumentPid());
		if (stockCalculation.isPresent()) {
			documentStockCalculation = stockCalculation.get();
			documentStockCalculation.setClosingActual(documentStockCalculationDTO.getClosingActual());
			documentStockCalculation.setClosingLogical(documentStockCalculationDTO.getClosingLogical());
			documentStockCalculation.setOpening(documentStockCalculationDTO.getOpening());
		} else {
			documentStockCalculation = new DocumentStockCalculation();
			documentStockCalculation
					.setDocument(documentRepository.findOneByPid(documentStockCalculationDTO.getDocumentPid()).get());
			documentStockCalculation.setClosingActual(documentStockCalculationDTO.getClosingActual());
			documentStockCalculation.setClosingLogical(documentStockCalculationDTO.getClosingLogical());
			documentStockCalculation.setOpening(documentStockCalculationDTO.getOpening());
		}
		documentStockCalculationRepository.save(documentStockCalculation);
		return new DocumentStockCalculationDTO(documentStockCalculation);

	}

	/**
	 * Update a document.
	 * 
	 * @param documentDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DocumentDTO update(DocumentDTO documentDTO) {
		log.debug("Request to Update Document : {}", documentDTO);

		return documentRepository.findOneByPid(documentDTO.getPid()).map(document -> {
			document.setName(documentDTO.getName());
			document.setDocumentPrefix(documentDTO.getDocumentPrefix());
			document.setAlias(documentDTO.getAlias());
			document.setDescription(documentDTO.getDescription());
			document.setTermsAndConditions(documentDTO.getTermsAndConditions());
			document.setDiscountPercentage(documentDTO.getDiscountPercentage());
			document.setDocumentType(documentDTO.getDocumentType());
			document.setActivityAccount(documentDTO.getActivityAccount());
			document.setSave(documentDTO.getSave());
			document.setEditable(documentDTO.getEditable());
			document.setBatchEnabled(documentDTO.getBatchEnabled());
			document.setPromptStockLocation(documentDTO.isPromptStockLocation());
			document.setSingleVoucherMode(documentDTO.getSingleVoucherMode());
			document.setPhotoMandatory(documentDTO.getPhotoMandatory());
			document.setIsTakeImageFromGallery(documentDTO.getIsTakeImageFromGallery());
			document.setMode(documentDTO.getMode());
			document.setStockFlow(documentDTO.getStockFlow());
			document.setQrCodeEnabled(documentDTO.getQrCodeEnabled());
			document.setOrderNoEnabled(documentDTO.getOrderNoEnabled());
			document.setVoucherNumberGenerationType(documentDTO.getVoucherNumberGenerationType());
			document.setAddNewCustomer(documentDTO.getAddNewCustomer());
			document.setTermsAndConditionsColumn(documentDTO.isTermsAndConditionsColumn());
			document.setHasTelephonicOrder(documentDTO.getHasTelephonicOrder());
			document.setRateWithTax(documentDTO.getRateWithTax());
			document.setDiscountScaleBar(documentDTO.getDiscountScaleBar());
			document.setSmsApiEnable(documentDTO.getSmsApiEnable());
			document.setPreventNegativeStock(documentDTO.getPreventNegativeStock());
			document.setEnableHeaderPrintOut(documentDTO.getEnableHeaderPrintOut());
			if (documentDTO.getHeaderImage() != null) {
				document.setHeaderImage(documentDTO.getHeaderImage());
				document.setHeaderImageContentType(documentDTO.getHeaderImageContentType());
			}
			if (documentDTO.getFooterImage() != null) {
				document.setFooterImage(documentDTO.getFooterImage());
				document.setFooterImageContentType(documentDTO.getFooterImageContentType());
			}

			document = documentRepository.save(document);
			DocumentDTO result = documentMapper.documentToDocumentDTO(document);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the documents.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Document> findAll(Pageable pageable) {
		log.debug("Request to get all Documents");
		Page<Document> result = documentRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the documents.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllByCompany() {
		log.debug("Request to get all Documents");
		List<Document> documents = documentRepository.findAllByCompanyId();
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	/**
	 * Get all the documents.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DocumentDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Documents");
		Page<Document> documents = documentRepository.findAllByCompanyId(pageable);
		Page<DocumentDTO> result = new PageImpl<DocumentDTO>(
				documentMapper.documentsToDocumentDTOs(documents.getContent()), pageable, documents.getTotalElements());
		return result;
	}

	/**
	 * Get all the documents.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DocumentDTO> findAllByCompanyAndDocumentType(Pageable pageable, DocumentType documentType) {
		log.debug("Request to get all Documents");
		Page<Document> documents = documentRepository.findAllByCompanyIdAndDocumentType(pageable, documentType);
		Page<DocumentDTO> result = new PageImpl<DocumentDTO>(
				documentMapper.documentsToDocumentDTOs(documents.getContent()), pageable, documents.getTotalElements());
		return result;
	}

	/**
	 * Get one document by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DocumentDTO findOne(Long id) {
		log.debug("Request to get Document : {}", id);
		Document document = documentRepository.findOne(id);
		DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);
		return documentDTO;
	}

	/**
	 * Get one document by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentDTO> findOneByPid(String pid) {
		log.debug("Request to get Document by pid : {}", pid);
		return documentRepository.findOneByPid(pid).map(document -> {
			DocumentDTO documentDTO = new DocumentDTO(document);
			return documentDTO;
		});

	}

	/**
	 * Get one document by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentDTO> findByName(String name) {
		log.debug("Request to get DocumentGroup by name : {}", name);
		return documentRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(document -> {
					DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);
					return documentDTO;
				});
	}

	/**
	 * Get one document by documentPrefix.
	 *
	 * @param documentPrefix the documentPrefix of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentDTO> findByDocumentPrefix(String documentPrefix) {
		log.debug("Request to get DocumentGroup by documentPrefix : {}", documentPrefix);
		return documentRepository
				.findByCompanyIdAndDocumentPrefixIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), documentPrefix)
				.map(document -> {
					DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);
					return documentDTO;
				});
	}

	/**
	 * Delete the document by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Document : {}", pid);
		documentRepository.findOneByPid(pid).ifPresent(document -> {
			documentRepository.delete(document.getId());
		});
	}

	/**
	 * Get all the documents.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllUnderInventoryAndAccountingVoucher() {
		log.debug("Request to get all Documents");
		List<Document> accountingVouchers = documentRepository
				.findAllDocumentsByDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		List<Document> invaentoryVouchers = documentRepository
				.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		List<Document> documents = new ArrayList<Document>();
		documents.addAll(invaentoryVouchers);
		documents.addAll(accountingVouchers);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	/**
	 * Get all the all Documents Under Inventory Voucher.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllByDocumentType(DocumentType documentType) {
		log.debug("Request to get all Documents by documentType");
		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(documentType);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	public List<DocumentDTO> findOneByPidIn(List<String> pids) {
		List<Document> documents = documentRepository.findOneByPidIn(pids);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	public List<DocumentDTO> findAllByCompanyPidAndDocumentType(String companyPid, DocumentType documentType) {
		log.debug("Request to get all Documents");
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPidAndDocumentType(documentType,
				companyPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	public List<DocumentDTO> findAllByActivityPid(String activityPid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by activityPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityDocument> activityDocuments = activityDocumentRepository.findByActivityPid(activityPid);
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

		List<Document> documents = new ArrayList<>();
		for (ActivityDocument activityDocument : activityDocuments) {
			documents.add(activityDocument.getDocument());
		}
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	public List<DocumentDTO> findAllByCompanyPid(String companyPid) {
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(companyPid);
		List<DocumentDTO> result = documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Optional<DocumentDTO> findByNameAndCompanyPid(String name, String companyPid) {
		return documentRepository.findByCompanyPidAndNameIgnoreCase(companyPid, name).map(activity -> {
			DocumentDTO documentDTO = new DocumentDTO(activity);
			return documentDTO;
		});
	}

	@Override
	public DocumentDTO saveFormSAdmin(DocumentDTO documentDTO) {
		log.debug("Request to save Document : {}", documentDTO);
		Optional<Company> opCompany = companyRepository.findOneByPid(documentDTO.getCompanyPid());
		if (opCompany.isPresent()) {
			log.debug("Request to save Document : {}", documentDTO);
			// set pid
			documentDTO.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
			Document document = documentMapper.documentDTOToDocument(documentDTO);
			// set company
			document.setCompany(opCompany.get());
			document.setMode(documentDTO.getMode());
			document.setStockFlow(documentDTO.getStockFlow());
			document.setVoucherNumberGenerationType(documentDTO.getVoucherNumberGenerationType());
			if (documentDTO.getHeaderImage() != null) {
				document.setHeaderImage(documentDTO.getHeaderImage());
				document.setHeaderImageContentType(documentDTO.getHeaderImageContentType());
			}
			if (documentDTO.getFooterImage() != null) {
				document.setFooterImage(documentDTO.getFooterImage());
				document.setFooterImageContentType(documentDTO.getFooterImageContentType());
			}
			document = documentRepository.save(document);
			DocumentDTO result = documentMapper.documentToDocumentDTO(document);
			return result;
		}
		return null;
	}

}
