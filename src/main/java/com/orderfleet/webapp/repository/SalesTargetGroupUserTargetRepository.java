package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.TargetFrequency;

public interface SalesTargetGroupUserTargetRepository extends JpaRepository<SalesTargetGroupUserTarget, Long> {

	Optional<SalesTargetGroupUserTarget> findOneByPid(String pid);

	@Query("select salesTargetGroupUserTarget from SalesTargetGroupUserTarget salesTargetGroupUserTarget where salesTargetGroupUserTarget.company.id = ?#{principal.companyId} and salesTargetGroupUserTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroupUserTarget> findAllByCompanyId();

	@Query("select salesTargetGroupUserTarget from SalesTargetGroupUserTarget salesTargetGroupUserTarget where salesTargetGroupUserTarget.company.id = ?#{principal.companyId} and salesTargetGroupUserTarget.accountWiseTarget = 'FALSE' ")
	Page<SalesTargetGroupUserTarget> findAllByCompanyId(Pageable pageable);

	@Query("select salesTargetGroupUserTarget from SalesTargetGroupUserTarget salesTargetGroupUserTarget where ?1 between salesTargetGroupUserTarget.fromDate and salesTargetGroupUserTarget.toDate and salesTargetGroupUserTarget.user.login = ?#{principal.username} and salesTargetGroupUserTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroupUserTarget> findByUserAndCurrentDate(LocalDate date);

	@Query("select stguTarget from SalesTargetGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroupUserTarget> findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid,String salesTargetGroupPid, LocalDate startDate,
			LocalDate endDate);

	List<SalesTargetGroupUserTarget> findByUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
			String userPid, LocalDate startDate, LocalDate endDate);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(List<String> salesTargetGroupPids,
			String userPid, LocalDate startDate, LocalDate endDate);
	//All Employee
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidInAndUserPidInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(List<String> salesTargetGroupPids,
			List<String> userid, LocalDate startDate, LocalDate endDate);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid,
			String userPid, LocalDate startDate, LocalDate endDate);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidAndUserIdInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid,
			List<Long> userIds, LocalDate startDate, LocalDate endDate);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);
	
	List<SalesTargetGroupUserTarget> findByAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String accountProfilePid, LocalDate startDate, LocalDate endDate);

	List<SalesTargetGroupUserTarget> findByUserPidAndSalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
			String userPid, String salesTargetPid, LocalDate firstDateMonth, LocalDate lastDateMonth);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidAndUserPidAndAccountWiseTargetFalse(String salesTargetGroupPid,
			String userPid);
	
	
	@Query("select stguTarget from SalesTargetGroupUserTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.pid = ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupIdInAndUserPidAndFromDateBetweenAndAccountWiseTargetFalse(Set<Long> groupIds, String userPid, LocalDate startDate, LocalDate endDate);
	
	@Query("select stguTarget from SalesTargetGroupUserTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.id in ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupIdInAndUserIdInAndFromDateBetweenAndAccountWiseTargetFalse(Set<Long> groupIds, List<Long> userIds, LocalDate startDate, LocalDate endDate);
	
	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesTargetGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroup> findByUserPidAndGroupBySalesTargetGroup(String userPid);
	
	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesTargetGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup in ?2 and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroup> findByUserPidAndSalesTargetGroup(String userPid, List<SalesTargetGroup> salesTargetGroup);
	
	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesTargetGroupUserTarget stguTarget where stguTarget.company.id = ?#{principal.companyId} and stguTarget.accountWiseTarget = 'FALSE' ")
	List<SalesTargetGroup> findAllSalesTargetGroupByCompanyId();

	/** account wise */
	List<SalesTargetGroupUserTarget> findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			String userPid, String salesTArgetPid, String accountProfilePid, LocalDate firstDateMonth,
			LocalDate lastDateMonth);
	
	@Query("select stguTarget.pid, stguTarget.amount, stguTarget.volume from SalesTargetGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.accountProfile.pid = ?3 and stguTarget.fromDate >= ?4 AND stguTarget.toDate <= ?5 ")
	List<Object[]> findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndDateBetween(String userPid, String salesTArgetPid, String accountProfilePid, LocalDate firstDateMonth, LocalDate lastDateMonth);
	
	List<SalesTargetGroupUserTarget> findBySalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String salesTargetPid, String accountProfilePid, LocalDate firstDateMonth,
			LocalDate lastDateMonth);

	@Query("select sum(stguTarget.volume),sum(stguTarget.amount) from SalesTargetGroupUserTarget stguTarget where stguTarget.accountProfile.pid = ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 ")
	Object findSumOfAccountWiseTarget(String accountPid, LocalDate startDate, LocalDate endDate);
	
	@Query("select sum(stguTarget.amount), sum(stguTarget.volume) from SalesTargetGroupUserTarget stguTarget where stguTarget.accountProfile.pid in ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 ")
	Object findSumOfAccountWiseTarget(List<String> accountPids, LocalDate startDate, LocalDate endDate);
	
	@Query("select stguTarget from SalesTargetGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' and stguTarget.targetFrequency=?5")
	List<SalesTargetGroupUserTarget> findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(String userPid,String salesTargetGroupPid, LocalDate startDate,LocalDate endDate,TargetFrequency targetFrequency);
	
	@Query("select salesTargetGroupUserTarget from SalesTargetGroupUserTarget salesTargetGroupUserTarget where salesTargetGroupUserTarget.company.id = ?#{principal.companyId} and salesTargetGroupUserTarget.accountWiseTarget = 'FALSE' and salesTargetGroupUserTarget.targetFrequency=?1 and salesTargetGroupUserTarget.fromDate <= ?3 AND salesTargetGroupUserTarget.toDate >= ?2 ")
	List<SalesTargetGroupUserTarget> findAllByCompanyIdAndTargetFrequencyAndDateBetween(TargetFrequency targetFrequency,LocalDate startDate, LocalDate endDate);
	
	
}
