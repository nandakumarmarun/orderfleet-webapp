
package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.KilometreCalculation;
import com.orderfleet.webapp.domain.UserDistance;

/**
 * @author Anish
 *
 */
public interface KilometreCalculationRepository extends JpaRepository<KilometreCalculation,Long>{

	Optional<KilometreCalculation> findByCompanyIdAndUserPidAndDate(Long companyId, String userPid, LocalDate date);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId}")
	List<KilometreCalculation> findAllByCompany();

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} Order By kilometreCalculation.createdDate desc")
	Page<KilometreCalculation> findAllByCompanyIdOrderByDateDesc(Pageable pageable);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.pid = ?1")
	List<KilometreCalculation> findAllByCompanyIdAndUserPid(String userPid);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.pid = ?1 and kilometreCalculation.date between ?2 and ?3 Order By kilometreCalculation.createdDate desc")
	List<KilometreCalculation> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate, LocalDate toDate);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.pid = ?1 and kilometreCalculation.date =?2 Order By kilometreCalculation.createdDate desc")
	List<KilometreCalculation> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid, LocalDate fromDate);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.date between ?1 and ?2 Order By kilometreCalculation.createdDate desc")
	List<KilometreCalculation> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDate fromDate, LocalDate toDate);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.pid in ?1 and kilometreCalculation.date between ?2 and ?3 Order By kilometreCalculation.createdDate desc")
	List<KilometreCalculation> findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate, LocalDate toDate);

	@Query("select kilometreCalculation from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.pid in ?1 and kilometreCalculation.date =?2 Order By kilometreCalculation.createdDate desc")
	List<KilometreCalculation> findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate);

	@Query("select sum(kilometreCalculation.kilometre),kilometreCalculation.employee.name from KilometreCalculation kilometreCalculation where kilometreCalculation.company.id = ?#{principal.companyId} and kilometreCalculation.user.id in ?1 and kilometreCalculation.createdDate between ?2 and ?3 Group By kilometreCalculation.employee.name")
    List<Object[]> findByUserIdsAndDateBetwewn(List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);
}
