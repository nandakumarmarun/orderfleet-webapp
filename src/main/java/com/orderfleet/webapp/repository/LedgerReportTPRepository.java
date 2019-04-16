package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.LedgerReportTP;

/**
 * Spring Data JPA repository for the LedgerReportTP entity.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */
public interface LedgerReportTPRepository extends JpaRepository<LedgerReportTP, Long> {

	Optional<LedgerReportTP> findOneById(Long id);

	@Query("select ledgerReportTP from LedgerReportTP ledgerReportTP where ledgerReportTP.company.id = ?#{principal.companyId}")
	List<LedgerReportTP> findAllByCompanyId();

	@Query("select ledgerReportTP from LedgerReportTP ledgerReportTP where ledgerReportTP.company.id = ?#{principal.companyId} and type=?1 and accountProfile.pid=?2")
	List<LedgerReportTP> findAllByCompanyIdAndTypeAndAccountProfilePid(String narration, String accountPid);

	
	@Transactional
	void deleteByCompanyIdAndType(Long companyId,String type);
}
