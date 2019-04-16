package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicDocumentReportDetail;
import com.orderfleet.webapp.domain.DynamicDocumentSettingsColumns;
import com.orderfleet.webapp.domain.DynamicDocumentSettingsHeader;
import com.orderfleet.webapp.domain.DynamicDocumentSettingsRowColour;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentReportDetailRepository;
import com.orderfleet.webapp.repository.DynamicDocumentSettingsColumnsRepository;
import com.orderfleet.webapp.repository.DynamicDocumentSettingsHeaderRepository;
import com.orderfleet.webapp.repository.DynamicDocumentSettingsRowColourRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementValueRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DynamicDocumentSettingsHeaderService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentReportDetailDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsColumnsDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsRowColourDTO;

/**
 * Service Implementation for managing DynamicDocumentSettingsHeader.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
@Service
@Transactional
public class DynamicDocumentSettingsHeaderServiceImpl implements DynamicDocumentSettingsHeaderService {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentSettingsHeaderServiceImpl.class);

	@Inject
	private DynamicDocumentSettingsHeaderRepository documentSettingsHeaderRepository;

	@Inject
	private DynamicDocumentSettingsColumnsRepository documentSettingsColumnsRepository;

	@Inject
	private DynamicDocumentSettingsRowColourRepository documentSettingsRowColourRepository;

	@Inject
	private DynamicDocumentReportDetailRepository dynamicDocumentReportDetailRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormElementValueRepository formElementValueRepository;

	/**
	 * save dynamicDocumentSettingsHeader
	 * 
	 * @param dynamicDocumentSettingsHeaderDTO
	 */
	@Override
	public DynamicDocumentSettingsHeaderDTO save(DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO) {
		log.debug("Request to save DynamicDocumentSettingsHeader : {}", documentSettingsHeaderDTO);
		DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader = new DynamicDocumentSettingsHeader();
		dynamicDocumentSettingsHeader
				.setPid(DynamicDocumentSettingsHeaderService.PID_PREFIX + RandomUtil.generatePid());
		dynamicDocumentSettingsHeader.setName(documentSettingsHeaderDTO.getName());
		dynamicDocumentSettingsHeader.setTitle(documentSettingsHeaderDTO.getTitle());
		dynamicDocumentSettingsHeader
				.setDocument(documentRepository.findOneByPid(documentSettingsHeaderDTO.getDocumentPid()).get());
		dynamicDocumentSettingsHeader.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		dynamicDocumentSettingsHeader = documentSettingsHeaderRepository.save(dynamicDocumentSettingsHeader);

		if (!documentSettingsHeaderDTO.getDocumentSettingsColumnsDTOs().isEmpty()) {
			saveDocumentSettingsColumnsDTOs(dynamicDocumentSettingsHeader,
					documentSettingsHeaderDTO.getDocumentSettingsColumnsDTOs());
		}

		if (!documentSettingsHeaderDTO.getDocumentSettingsRowColourDTOs().isEmpty()) {
			saveDocumentSettingsRowColourDTOs(dynamicDocumentSettingsHeader,
					documentSettingsHeaderDTO.getDocumentSettingsRowColourDTOs());
		}

		if (!documentSettingsHeaderDTO.getDynamicDocumentReportDetailDTOs().isEmpty()) {
			saveDynamicDocumentReportDetailDTOs(dynamicDocumentSettingsHeader,
					documentSettingsHeaderDTO.getDynamicDocumentReportDetailDTOs());
		}

		DynamicDocumentSettingsHeaderDTO result = new DynamicDocumentSettingsHeaderDTO(dynamicDocumentSettingsHeader);
		return result;
	}

	/**
	 * save dynamicDocumentReportDetailDTOs
	 * 
	 * @param dynamicDocumentSettingsHeader
	 * @param dynamicDocumentReportDetailDTOs
	 */
	private void saveDynamicDocumentReportDetailDTOs(DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader,
			List<DynamicDocumentReportDetailDTO> dynamicDocumentReportDetailDTOs) {

		List<DynamicDocumentReportDetail> documentReportDetails = new ArrayList<>();
		
		if(dynamicDocumentReportDetailDTOs!=null&&dynamicDocumentReportDetailDTOs.size()>0)
		{
//			dynamicDocumentReportDetailRepository.deleteByDynamicDocumentSettingsHeaderId(dynamicDocumentSettingsHeader.getId());
		}
		for (DynamicDocumentReportDetailDTO dynamicDocumentReportDetailDTO : dynamicDocumentReportDetailDTOs) {
			Optional<FormElement> formElement = formElementRepository.findOneByPid(dynamicDocumentReportDetailDTO.getFormElementPid());
			if (formElement.isPresent()) {
				DynamicDocumentReportDetail documentReportDetail = new DynamicDocumentReportDetail();
				documentReportDetail.setCompany(dynamicDocumentSettingsHeader.getCompany());
				documentReportDetail.setDynamicDocumentSettingsHeader(dynamicDocumentSettingsHeader);
				documentReportDetail.setFormElement(formElement.get());
				documentReportDetail.setColumnName(dynamicDocumentReportDetailDTO.getColumnName());
				documentReportDetail.setDisplayLabel(dynamicDocumentReportDetailDTO.getDisplayLabel());
				documentReportDetail.setSortOrder(dynamicDocumentReportDetailDTO.getSortOrder());
				documentReportDetail.setTableName(dynamicDocumentReportDetailDTO.getTableName());
				documentReportDetails.add(documentReportDetail);
			}
		}
		dynamicDocumentReportDetailRepository.save(documentReportDetails);
	}

	/**
	 * save dynamicDocumentSettingsHeader
	 * 
	 * @param dynamicDocumentSettingsHeaderDTO
	 */
	@Override
	public DynamicDocumentSettingsHeaderDTO update(DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO) {
		log.debug("Request to update DynamicDocumentSettingsHeader : {}", documentSettingsHeaderDTO);
		return documentSettingsHeaderRepository.findOneByPid(documentSettingsHeaderDTO.getPid())
				.map(dynamicDocumentSettingsHeader -> {
					dynamicDocumentSettingsHeader.setName(documentSettingsHeaderDTO.getName());
					dynamicDocumentSettingsHeader.setTitle(documentSettingsHeaderDTO.getTitle());
					dynamicDocumentSettingsHeader = documentSettingsHeaderRepository
							.save(dynamicDocumentSettingsHeader);
					DynamicDocumentSettingsHeaderDTO result = new DynamicDocumentSettingsHeaderDTO(
							dynamicDocumentSettingsHeader);
					return result;
				}).orElse(null);
	}

	/**
	 * save DocumentSettingsRowColour.
	 * 
	 * @param dynamicDocumentSettingsHeader
	 * @param documentSettingsRowColourDTOs
	 */
	private void saveDocumentSettingsRowColourDTOs(DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader,
			List<DynamicDocumentSettingsRowColourDTO> documentSettingsRowColourDTOs) {
		List<DynamicDocumentSettingsRowColour> documentSettingsRowColours = new ArrayList<>();
		for (DynamicDocumentSettingsRowColourDTO dynamicDocumentSettingsRowColourDTO : documentSettingsRowColourDTOs) {
			DynamicDocumentSettingsRowColour documentSettingsRowColour = new DynamicDocumentSettingsRowColour();
			documentSettingsRowColour.setColour(dynamicDocumentSettingsRowColourDTO.getColour());
			documentSettingsRowColour.setCompany(dynamicDocumentSettingsHeader.getCompany());
			documentSettingsRowColour.setDynamicDocumentSettingsHeader(dynamicDocumentSettingsHeader);
			documentSettingsRowColour.setFormElement(
					formElementRepository.findOneByPid(dynamicDocumentSettingsRowColourDTO.getFormElementPid()).get());
			documentSettingsRowColour.setFormElementValue(
					formElementValueRepository.getOne(dynamicDocumentSettingsRowColourDTO.getFormElementValueId()));
			documentSettingsRowColours.add(documentSettingsRowColour);
		}
		documentSettingsRowColourRepository.save(documentSettingsRowColours);
	}

	/**
	 * save DocumentSettingsColumns.
	 * 
	 * @param dynamicDocumentSettingsHeader
	 * @param documentSettingsColumnsDTOs
	 */
	private void saveDocumentSettingsColumnsDTOs(DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader,
			List<DynamicDocumentSettingsColumnsDTO> documentSettingsColumnsDTOs) {
		log.debug("Request to save documentSettingsColumnsDTOs : {}", documentSettingsColumnsDTOs.size());
		List<DynamicDocumentSettingsColumns> saveList = new ArrayList<>();
		for (DynamicDocumentSettingsColumnsDTO dynamicDocumentSettingsColumnsDTO : documentSettingsColumnsDTOs) {
			DynamicDocumentSettingsColumns columns = new DynamicDocumentSettingsColumns();
			columns.setCompany(dynamicDocumentSettingsHeader.getCompany());
			columns.setDynamicDocumentSettingsHeader(dynamicDocumentSettingsHeader);
			columns.setFormElement(
					formElementRepository.findOneByPid(dynamicDocumentSettingsColumnsDTO.getFormElementPid()).get());
			columns.setSortOrder(dynamicDocumentSettingsColumnsDTO.getSortOrder());
			saveList.add(columns);
		}
		documentSettingsColumnsRepository.save(saveList);
	}

	/**
	 * Get all the DynamicDocumentSettingsHeaderDTO.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentSettingsHeaderDTO> findAllByCompanyId() {
		log.debug("Request to get all dynamic document settings headers");
		List<DynamicDocumentSettingsHeader> documentSettingsHeaders = documentSettingsHeaderRepository
				.findAllByCompanyId();
		List<DynamicDocumentSettingsHeaderDTO> result = documentSettingsHeaders.stream()
				.map(DynamicDocumentSettingsHeaderDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the DynamicDocumentSettingsHeaderDTO.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DynamicDocumentSettingsHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get dynamic document settings headers by pid");
		return documentSettingsHeaderRepository.findOneByPid(pid).map(head -> {
			DynamicDocumentSettingsHeaderDTO headerDTO = new DynamicDocumentSettingsHeaderDTO(head);
			return headerDTO;
		});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete dynamic document settings headers by pid");
		documentSettingsColumnsRepository.deleteByDynamicDocumentSettingsHeaderPid(pid);
		documentSettingsRowColourRepository.deleteByDynamicDocumentSettingsHeaderPid(pid);
		dynamicDocumentReportDetailRepository.deleteByDynamicDocumentSettingsHeaderPid(pid);
		documentSettingsHeaderRepository.deleteByPid(pid);
	}

	@Override
	public void updateDynamicDocumentSettingsColumns(
			List<DynamicDocumentSettingsColumnsDTO> dynamicDocumentSettingsColumnsDTOs) {
		log.debug("Request to save documentSettingsColumnsDTOs : {}", dynamicDocumentSettingsColumnsDTOs.size());
		List<DynamicDocumentSettingsColumns> saveList = new ArrayList<>();
		Optional<DynamicDocumentSettingsHeader> opDynamicDocumentSettingsHeader = documentSettingsHeaderRepository
				.findOneByPid(dynamicDocumentSettingsColumnsDTOs.get(0).getDynamicDocumentSettingsHeaderPid());
		if (opDynamicDocumentSettingsHeader.isPresent()) {
			DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader = opDynamicDocumentSettingsHeader.get();
			dynamicDocumentSettingsHeader.setDocumentSettingsColumns(new ArrayList<>());
			documentSettingsColumnsRepository
					.deleteByDynamicDocumentSettingsHeaderPid(dynamicDocumentSettingsHeader.getPid());

			for (DynamicDocumentSettingsColumnsDTO dynamicDocumentSettingsColumnsDTO : dynamicDocumentSettingsColumnsDTOs) {
				DynamicDocumentSettingsColumns columns = new DynamicDocumentSettingsColumns();
				columns.setCompany(dynamicDocumentSettingsHeader.getCompany());
				columns.setDynamicDocumentSettingsHeader(dynamicDocumentSettingsHeader);
				columns.setFormElement(formElementRepository
						.findOneByPid(dynamicDocumentSettingsColumnsDTO.getFormElementPid()).get());
				columns.setSortOrder(dynamicDocumentSettingsColumnsDTO.getSortOrder());
				saveList.add(columns);
			}
			documentSettingsColumnsRepository.save(saveList);
		}
	}

	@Override
	public void saveDynamicDocumentSettingsRowColour(
			List<DynamicDocumentSettingsRowColourDTO> dynamicDocumentSettingsRowColourDTOs) {
		log.debug("Request to save dynamicDocumentSettingsRowColourDTOs : {}",
				dynamicDocumentSettingsRowColourDTOs.size());
		List<DynamicDocumentSettingsRowColour> saveList = new ArrayList<>();
		Optional<DynamicDocumentSettingsHeader> opDynamicDocumentSettingsHeader = documentSettingsHeaderRepository
				.findOneByPid(dynamicDocumentSettingsRowColourDTOs.get(0).getDynamicDocumentSettingsHeaderPid());
		if (opDynamicDocumentSettingsHeader.isPresent()) {
			DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader = opDynamicDocumentSettingsHeader.get();
			dynamicDocumentSettingsHeader.setDocumentSettingsRowColours(new ArrayList<>());
			documentSettingsRowColourRepository
					.deleteByDynamicDocumentSettingsHeaderPid(dynamicDocumentSettingsHeader.getPid());
			for (DynamicDocumentSettingsRowColourDTO dynamicDocumentSettingsRowColourDTO : dynamicDocumentSettingsRowColourDTOs) {
				DynamicDocumentSettingsRowColour documentSettingsRowColour = new DynamicDocumentSettingsRowColour();
				documentSettingsRowColour.setColour(dynamicDocumentSettingsRowColourDTO.getColour());
				documentSettingsRowColour.setCompany(dynamicDocumentSettingsHeader.getCompany());
				documentSettingsRowColour.setDynamicDocumentSettingsHeader(dynamicDocumentSettingsHeader);
				documentSettingsRowColour.setFormElement(formElementRepository
						.findOneByPid(dynamicDocumentSettingsRowColourDTO.getFormElementPid()).get());
				documentSettingsRowColour.setFormElementValue(
						formElementValueRepository.getOne(dynamicDocumentSettingsRowColourDTO.getFormElementValueId()));
				saveList.add(documentSettingsRowColour);
			}
			documentSettingsRowColourRepository.save(saveList);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<DynamicDocumentSettingsHeaderDTO> findOneByName(String name) {
		log.debug("Request to get dynamic document settings headers by name");
		return documentSettingsHeaderRepository.findOneByName(name).map(head -> {
			DynamicDocumentSettingsHeaderDTO headerDTO = new DynamicDocumentSettingsHeaderDTO(head);
			return headerDTO;
		});
	}

	/**
	 * save dynamicDocumentSettingsHeader
	 * 
	 * @param dynamicDocumentSettingsHeaderDTO
	 */
	@Override
	public DynamicDocumentSettingsHeaderDTO updateDynamicDocumentSettingsHeader(DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO) {
		log.debug("Request to update DynamicDocumentSettingsHeader : {}", documentSettingsHeaderDTO);
		return documentSettingsHeaderRepository.findOneByPid(documentSettingsHeaderDTO.getPid())
				.map(dynamicDocumentSettingsHeader -> {
					dynamicDocumentSettingsHeader.setName(documentSettingsHeaderDTO.getName());
					dynamicDocumentSettingsHeader.setTitle(documentSettingsHeaderDTO.getTitle());
					dynamicDocumentSettingsHeader = documentSettingsHeaderRepository.save(dynamicDocumentSettingsHeader);
					
					saveDynamicDocumentReportDetailDTOs(dynamicDocumentSettingsHeader, documentSettingsHeaderDTO.getDynamicDocumentReportDetailDTOs());
					
					DynamicDocumentSettingsHeaderDTO result = new DynamicDocumentSettingsHeaderDTO(dynamicDocumentSettingsHeader);
					return result;
				}).orElse(null);
	}
}
