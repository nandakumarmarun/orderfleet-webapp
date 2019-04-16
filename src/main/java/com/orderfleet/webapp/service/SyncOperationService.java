package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.web.rest.dto.SyncOperationDTO;
import com.orderfleet.webapp.web.rest.dto.SyncOperationManageDTO;
import com.orderfleet.webapp.web.rest.dto.SyncOperationTimeDTO;

/**
 * Service Interface for managing SyncOperation.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */
public interface SyncOperationService {

	SyncOperation save(SyncOperation syncOperation);

	SyncOperation saveOrUpdateSyncOperations(String companyPid, String syncOperations);

	Page<SyncOperationDTO> findAllSyncOperations(Pageable pageable);
	
	List<SyncOperationDTO>findAllSyncOperations();

	List<String> findAllAssignedSyncOperationTypesBycompanyPid(String companyPid);

	List<SyncOperationTimeDTO> findAllSyncOperationTimesBycompanyPid(String companyPid);

	List<SyncOperationTimeDTO> findAllByCompanyIdAndCompleted(Long companyId, boolean completed);
	
	SyncOperationDTO findAllByCompanyId(Long companyId);

	SyncOperation saveSyncOperationForPartners(String companyPid, String syncOperationTypes);

	List<SyncOperationManageDTO> findAllSyncOperationByCompanyPid(String companyPid);

	List<SyncOperationManageDTO> saveSyncOperation(List<SyncOperationManageDTO> syncOperationManageDTOs);
}
