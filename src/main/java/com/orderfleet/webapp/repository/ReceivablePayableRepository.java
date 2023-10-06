package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;

/**
 * Spring Data JPA repository for the ReceivablePayable entity.
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */
public interface ReceivablePayableRepository extends JpaRepository<ReceivablePayable, Long> {

	Optional<ReceivablePayable> findByCompanyIdAndAccountProfileNameIgnoreCase(Long id, String name);

	Optional<ReceivablePayable> findOneByPid(String pid);
	Optional<ReceivablePayable> findOneByPid(List<String> pid);
	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId}")
	List<ReceivablePayable> findAllByCompanyId();

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId}")
	Page<ReceivablePayable> findAllByCompanyId(Pageable pageable);

	List<ReceivablePayable> findAllByAccountProfilePid(String accountPid);
	
	List<ReceivablePayable> findAllByAccountProfilePidIn(List<String> accountPids);

	Optional<ReceivablePayable> findOneByAccountProfilePidAndReferenceDocumentNumber(String accountPid,
			String referenceDocumentNumber);

	@Transactional
	Long deleteByCompanyId(Long companyId);

	@Query("select coalesce(sum(receivablePayable.referenceDocumentBalanceAmount),0) from ReceivablePayable receivablePayable where receivablePayable.accountProfile.pid in ?1 and receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.billOverDue > 0")
	Double findDueAmountByAccountProfileInAndCompanyId(List<String> accountProfilePids);

	@Query("select coalesce(sum(receivablePayable.referenceDocumentBalanceAmount),0) from ReceivablePayable receivablePayable where receivablePayable.accountProfile.pid in ?1 and receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.billOverDue > 0")
	Double findDueAmountByAccountProfilePidInAndCompanyId(Set<String> accountProfilePids);

	List<ReceivablePayable> findAllByAccountProfilePidAndReceivablePayableType(String accountPid,
			ReceivablePayableType receivablePayableType);

	List<ReceivablePayable> findAllByCompanyId(Long companyId);

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.lastModifiedDate > ?1")
	List<ReceivablePayable> findAllByCompanyAndlastModifiedDate(LocalDateTime lastModifiedDate);

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.accountProfile.pid in ?1")
	List<ReceivablePayable> findAllByCompanyIdAndAccountprofileIn(List<String> accountProfilePids);

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.lastModifiedDate > ?1 and receivablePayable.accountProfile.pid in ?2")
	List<ReceivablePayable> findAllByCompanyAndlastModifiedDateAndAccountProfileIn(LocalDateTime lastModifiedDate,
			List<String> accountProfilePids);

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.referenceDocumentDate between ?1 and ?2 order by receivablePayable.referenceDocumentDate desc")
	List<ReceivablePayable> findAllByCompanyIdAndDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.accountProfile.pid = ?1 and receivablePayable.referenceDocumentDate between ?2 and ?3 order by receivablePayable.referenceDocumentDate desc")
	List<ReceivablePayable> findAllByAccountProfilePidAndDateBetween(String accountPid, LocalDate fromDate,
			LocalDate toDate);
	@Query("select receivablePayable from ReceivablePayable receivablePayable where receivablePayable.company.id = ?#{principal.companyId} and receivablePayable.referenceDocumentNumber in ?1")
	ReceivablePayable findOneByReferenceDocumentNumber(List<String> docNo);
}
