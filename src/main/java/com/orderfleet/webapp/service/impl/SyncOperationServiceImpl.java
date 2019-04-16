package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.web.rest.dto.SyncOperationDTO;
import com.orderfleet.webapp.web.rest.dto.SyncOperationManageDTO;
import com.orderfleet.webapp.web.rest.dto.SyncOperationTimeDTO;

/**
 * Service Implementation for managing SyncOperation.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */
@Service
@Transactional
public class SyncOperationServiceImpl implements SyncOperationService {

	private final Logger log = LoggerFactory.getLogger(SyncOperationServiceImpl.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public SyncOperation save(SyncOperation syncOperation) {
		return syncOperationRepository.save(syncOperation);
	}

	@Override
	public SyncOperation saveOrUpdateSyncOperations(String companyPid, String syncOperationTypes) {
		log.debug("save or update SyncOperation using companyId and syncOperationTypes   :  " + syncOperationTypes);

		Optional<Company> company = companyRepository.findOneByPid(companyPid);
		List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyId(company.get().getId());
		syncOperationRepository.deleteByCompanyId(company.get().getId());
		SyncOperation syncOperation = new SyncOperation();
		if (!syncOperations.isEmpty()) {
			String[] allSyncOperationTypes = syncOperationTypes.split(",");
			Set<SyncOperation> setSOps = new HashSet<>();
			for (String syncOperationType : allSyncOperationTypes) {
				Optional<SyncOperation> OPSyncOperation = syncOperations.stream()
						.filter(pc -> pc.getOperationType().equals(SyncOperationType.valueOf(syncOperationType)))
						.findAny();
				if (!OPSyncOperation.isPresent()) {
					syncOperation = new SyncOperation();
					syncOperation.setCompany(company.get());
					syncOperation.setOperationType(SyncOperationType.valueOf(syncOperationType));
					setSOps.add(syncOperation);
				} else {
					syncOperation = new SyncOperation();
					syncOperation.setCompany(company.get());
					syncOperation.setOperationType(SyncOperationType.valueOf(syncOperationType));
					syncOperation.setCompleted(OPSyncOperation.get().getCompleted());
					syncOperation.setLastSyncCompletedDate(OPSyncOperation.get().getLastSyncCompletedDate());
					syncOperation.setLastSyncStartedDate(OPSyncOperation.get().getLastSyncStartedDate());
					syncOperation.setLastSyncTime(OPSyncOperation.get().getLastSyncTime());
					setSOps.add(syncOperation);
				}
			}
			syncOperationRepository.save(new ArrayList<>(setSOps));

		} else {
			String[] allSyncOperationTypes = syncOperationTypes.split(",");
			for (String syncOperationType : allSyncOperationTypes) {
				syncOperation = new SyncOperation();
				syncOperation.setCompany(company.get());
				syncOperation.setOperationType(SyncOperationType.valueOf(syncOperationType));
				syncOperationRepository.save(syncOperation);
			}
		}
		return syncOperation;
	}

	@Override
	public SyncOperation saveSyncOperationForPartners(String companyPid, String syncOperationTypes) {
		log.debug("save  SyncOperation using companyId and syncOperationTypes   :  " + syncOperationTypes);
		Optional<Company> company = companyRepository.findOneByPid(companyPid);
		syncOperationRepository.deleteByCompanyId(company.get().getId());
		SyncOperation syncOperation = new SyncOperation();
		String[] allSyncOperationTypes = syncOperationTypes.split(",");
		for (String syncOperationType : allSyncOperationTypes) {
			syncOperation = new SyncOperation();
			syncOperation.setCompany(company.get());
			syncOperation.setUser(true);
			syncOperation.setDocument(true);
			syncOperation.setOperationType(SyncOperationType.valueOf(syncOperationType));
			syncOperationRepository.save(syncOperation);
		}

		return syncOperation;
	}

	@Override
	public Page<SyncOperationDTO> findAllSyncOperations(Pageable pageable) {
		log.debug("find all SyncOperations");
		List<SyncOperationDTO> syncOperationDTOs = new ArrayList<>();
		List<Company> companies = companyRepository.findAllCompaniesByActivatedTrue();
		for (Company company : companies) {
			List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyId(company.getId());
			List<String> syncOperationTypes = new ArrayList<>();
			for (SyncOperation syncOperation : syncOperations) {
				syncOperationTypes.add(syncOperation.getOperationType().toString());
			}
			if (!syncOperationTypes.isEmpty()) {
				SyncOperationDTO operationDTO = new SyncOperationDTO();
				operationDTO.setCompanyName(company.getLegalName());
				operationDTO.setOperationTypes(syncOperationTypes);
				syncOperationDTOs.add(operationDTO);
			}
		}

		int start = pageable.getOffset();
		int end = (start + pageable.getPageSize()) > syncOperationDTOs.size() ? syncOperationDTOs.size()
				: (start + pageable.getPageSize());
		Page<SyncOperationDTO> result = new PageImpl<SyncOperationDTO>(syncOperationDTOs.subList(start, end), pageable,
				syncOperationDTOs.size());
		return result;
	}

	@Override
	public List<String> findAllAssignedSyncOperationTypesBycompanyPid(String companyPid) {
		List<SyncOperationType> operationTypes = syncOperationRepository
				.findAllSyncOperationTypeByCompanyId(companyPid);
		List<String> strings = new ArrayList<>();
		for (SyncOperationType syncOperationType : operationTypes) {
			strings.add(syncOperationType.toString());
		}
		return strings;
	}

	@Override
	public List<SyncOperationTimeDTO> findAllSyncOperationTimesBycompanyPid(String companyPid) {
		List<SyncOperation> syncOperations = syncOperationRepository.findAllSyncOperationByCompanyId(companyPid);
		List<SyncOperationTimeDTO> syncOperationTimeDTOs = getSyncOperationTime(syncOperations);
		return syncOperationTimeDTOs;
	}

	public List<SyncOperationTimeDTO> getSyncOperationTime(List<SyncOperation> syncOperations) {
		List<SyncOperationTimeDTO> syncOperationTimeDTOs = new ArrayList<>();
		for (SyncOperation syncOperation : syncOperations) {
			SyncOperationTimeDTO syncOperationTimeDTO = new SyncOperationTimeDTO();
			syncOperationTimeDTO.setCompleted(syncOperation.getCompleted());
			syncOperationTimeDTO.setLastSyncCompletedDate(syncOperation.getLastSyncCompletedDate());
			syncOperationTimeDTO.setLastSyncStartedDate(syncOperation.getLastSyncStartedDate());
			syncOperationTimeDTO.setOperationType(syncOperation.getOperationType());
			syncOperationTimeDTO.setLastSyncTime(syncOperation.getLastSyncTime());
			syncOperationTimeDTOs.add(syncOperationTimeDTO);
		}
		return syncOperationTimeDTOs;
	}

	@Override
	public List<SyncOperationTimeDTO> findAllByCompanyIdAndCompleted(Long companyId, boolean completed) {
		List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyIdAndCompleted(companyId,
				completed);
		List<SyncOperationTimeDTO> syncOperationTimeDTOs = getSyncOperationTime(syncOperations);
		return syncOperationTimeDTOs;
	}

	@Override
	public List<SyncOperationDTO> findAllSyncOperations() {
		List<SyncOperationDTO> syncOperationDTOs = new ArrayList<>();
		List<Company> companies = companyRepository.findAllCompaniesByActivatedTrue();
		for (Company company : companies) {
			List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyId(company.getId());
			List<String> syncOperationTypes = new ArrayList<>();
			for (SyncOperation syncOperation : syncOperations) {
				syncOperationTypes.add(syncOperation.getOperationType().toString());
			}
			if (!syncOperationTypes.isEmpty()) {
				SyncOperationDTO operationDTO = new SyncOperationDTO();
				operationDTO.setCompanyName(company.getLegalName());
				operationDTO.setOperationTypes(syncOperationTypes);
				syncOperationDTOs.add(operationDTO);
			}
		}
		return syncOperationDTOs;
	}

	@Override
	public SyncOperationDTO findAllByCompanyId(Long companyId) {
		List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyId(companyId);
		SyncOperationDTO operationDTO = new SyncOperationDTO();
		List<String> syncOperationTypes = new ArrayList<>();
		for (SyncOperation syncOperation : syncOperations) {
			syncOperationTypes.add(syncOperation.getOperationType().toString());
		}
		if (!syncOperationTypes.isEmpty()) {
			operationDTO.setCompanyName("");
			operationDTO.setOperationTypes(syncOperationTypes);
		}
		return operationDTO;
	}

	@Override
	public List<SyncOperationManageDTO> findAllSyncOperationByCompanyPid(String companyPid) {
		List<SyncOperation> syncOperations = syncOperationRepository.findAllSyncOperationTypeByCompanyPid(companyPid);
		List<SyncOperationManageDTO> result = syncOperations.stream().map(SyncOperationManageDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<SyncOperationManageDTO> saveSyncOperation(List<SyncOperationManageDTO> syncOperationManageDTOs) {
		log.debug("save or update SyncOperation  :  " + syncOperationManageDTOs.size());
		Optional<Company> company = companyRepository.findOneByPid(syncOperationManageDTOs.get(0).getCompanyPid());
		List<SyncOperation> syncOperations = syncOperationRepository.findAllByCompanyId(company.get().getId());
		syncOperationRepository.deleteByCompanyId(company.get().getId());

		if (!syncOperations.isEmpty()) {
			syncOperationManageDTOs.forEach(sync -> {
				SyncOperation syncOperation = new SyncOperation();

				Set<SyncOperation> setSOps = new HashSet<>();
				Optional<SyncOperation> OPSyncOperation = syncOperations.stream()
						.filter(pc -> pc.getOperationType().equals(sync.getOperationType())).findAny();
				if (!OPSyncOperation.isPresent()) {
					syncOperation = new SyncOperation();
					syncOperation.setCompany(company.get());
					syncOperation.setOperationType(sync.getOperationType());
					syncOperation.setReset(sync.isReset());
					syncOperation.setUser(sync.isUser());
					syncOperation.setDocument(sync.isDocument());
					setSOps.add(syncOperation);
				} else {
					syncOperation = new SyncOperation();
					syncOperation.setCompany(company.get());
					syncOperation.setOperationType(sync.getOperationType());
					syncOperation.setCompleted(OPSyncOperation.get().getCompleted());
					syncOperation.setLastSyncCompletedDate(OPSyncOperation.get().getLastSyncCompletedDate());
					syncOperation.setLastSyncStartedDate(OPSyncOperation.get().getLastSyncStartedDate());
					syncOperation.setLastSyncTime(OPSyncOperation.get().getLastSyncTime());
					syncOperation.setReset(sync.isReset());
					syncOperation.setUser(sync.isUser());
					syncOperation.setDocument(sync.isDocument());
					setSOps.add(syncOperation);
				}
				syncOperationRepository.save(new ArrayList<>(setSOps));

			});
		} else {
			syncOperationManageDTOs.forEach(sync -> {
				SyncOperation syncOperation = new SyncOperation();
				syncOperation.setCompany(company.get());
				syncOperation.setOperationType(sync.getOperationType());
				syncOperation.setReset(sync.isReset());
				syncOperation.setUser(sync.isUser());
				syncOperation.setDocument(sync.isDocument());
				syncOperationRepository.save(syncOperation);
			});
		}
		return syncOperationManageDTOs;
	}
}
