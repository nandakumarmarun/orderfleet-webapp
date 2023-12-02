package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesSummaryAchievment;

/**
 * Spring Data JPA repository for the SalesSummaryAchievment entity.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public interface SalesSummaryAchievmentRepository extends JpaRepository<SalesSummaryAchievment, Long> {

	Optional<SalesSummaryAchievment> findOneByPid(String pid);

	@Query("select salesSummaryAchievment from SalesSummaryAchievment salesSummaryAchievment where salesSummaryAchievment.company.id = ?#{principal.companyId}")
	List<SalesSummaryAchievment> findAllByCompanyId();

	SalesSummaryAchievment findByUserPidAndSalesTargetGroupPidAndAchievedDateBetweenAndLocationPid(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate, String LocationPid);

	@Query("select salesSummaryAchievment from SalesSummaryAchievment salesSummaryAchievment where salesSummaryAchievment.location.id in ?1 and salesSummaryAchievment.salesTargetGroup.pid = ?2 and salesSummaryAchievment.achievedDate between ?3 and ?4")
	List<SalesSummaryAchievment> findLocationIdInAndSalesTargetGroupPidAndAchievedDateBetween(List<Long> locationId,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);

	List<SalesSummaryAchievment> findByUserPidAndSalesTargetGroupPidAndAchievedDateBetween(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);

	List<SalesSummaryAchievment> findByUserPidInAndSalesTargetGroupPidAndAchievedDateBetween(List<String> userPids,
			String groupPid, LocalDate start, LocalDate end);

	List<SalesSummaryAchievment> findByUserPidInAndSalesTargetGroupPidInAndAchievedDateBetween(List<String> userPids,
																							List<String> groupPid, LocalDate start,LocalDate end);

}
