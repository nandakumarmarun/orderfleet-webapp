package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountNameType;

/**
 * Spring Data JPA repository for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

	Optional<AccountType> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<AccountType> findOneByPid(String pid);

	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId}")
	List<AccountType> findAllByCompanyId();

	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId} and accountType.activated = ?1 order by accountType.name asc")
	Page<AccountType> findAllByCompanyAndActivated(boolean activated, Pageable pageable);

	List<AccountType> findAllByCompanyPid(String companyPid);

	@Query("select accountType from AccountType accountType where accountType.company.id=?#{principal.companyId} and accountType.activated = ?1")
	Page<AccountType> findAllByCompanyIdAndAccountTypeActivated(Pageable pageable, Boolean active);

	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId} and accountType.activated = ?1 order by accountType.name asc")
	List<AccountType> findAllByCompanyIdAndActivated(boolean activated);
	
	List<AccountType> findAllByCompanyId(Long companyId);

	AccountType findFirstByCompanyIdOrderByIdAsc(Long companyId);
	
	@Query("select accountType from AccountType accountType where accountType.company.id=?#{principal.companyId} and accountType.activated = true and accountType.lastModifiedDate > ?1")
	Page<AccountType> findAllByCompanyIdAndAccountTypeActivatedTrueAndLastModifiedDateGreater(LocalDateTime lastSyncdate, Pageable pageable);

	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId} and accountType.activated = true and accountType.pid in ?1  order by accountType.name asc")
	List<AccountType> findAllByAccountTypePidIn(List<String>accountTypePids);
	
	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId} and accountType.name = ?1")
	AccountType findByCompanyIdAndName(String name);
	
	@Query("select accountType from AccountType accountType where accountType.company.id = ?#{principal.companyId} and accountType.accountNameType = ?1")
	AccountType findByCompanyIdAndAccountNameType(AccountNameType accountTypeName);
}
