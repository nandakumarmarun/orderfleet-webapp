package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;

/**
 * Spring Data JPA repository for the SyncOperation entity.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */
public interface SyncOperationRepository extends JpaRepository<SyncOperation, Long> {

	@Query("select syncOperation from SyncOperation syncOperation where syncOperation.company.id = ?#{principal.companyId}")
	Page<SyncOperation> findAllByCompanyId(Pageable pageable);

	@Query("select syncOperation from SyncOperation syncOperation where syncOperation.company.id = ?1")
	List<SyncOperation> findAllByCompanyId(Long companyId);

	@Transactional
	void deleteByCompanyId(Long id);

	@Query("select syncOperation.operationType from SyncOperation syncOperation where syncOperation.company.pid = ?1")
	List<SyncOperationType> findAllSyncOperationTypeByCompanyId(String companyPid);

	Optional<SyncOperation> findOneByCompanyIdAndOperationType(Long companyId, SyncOperationType operationType);

	@Query("select syncOperation from SyncOperation syncOperation where syncOperation.company.pid = ?1")
	List<SyncOperation> findAllSyncOperationByCompanyId(String companyPid);

	@Query("select syncOperation from SyncOperation syncOperation where syncOperation.company.id = ?1 and syncOperation.completed = ?2")
	List<SyncOperation> findAllByCompanyIdAndCompleted(Long companyId, boolean completed);
	
	@Query("select syncOperation from SyncOperation syncOperation where syncOperation.company.pid = ?1")
	List<SyncOperation> findAllSyncOperationTypeByCompanyPid(String companyPid);
}
