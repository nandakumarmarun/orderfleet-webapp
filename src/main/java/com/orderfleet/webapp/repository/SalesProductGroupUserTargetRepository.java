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
import com.orderfleet.webapp.domain.SalesProductGroupUserTarget;
import com.orderfleet.webapp.domain.enums.TargetFrequency;

public interface SalesProductGroupUserTargetRepository extends JpaRepository<SalesProductGroupUserTarget, Long> {

	Optional<SalesProductGroupUserTarget> findOneByPid(String pid);
//
//	@Query("select SalesProductGroupUserTarget from SalesProductGroupUserTarget SalesProductGroupUserTarget where SalesProductGroupUserTarget.company.id = ?#{principal.companyId} and SalesProductGroupUserTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesProductGroupUserTarget> findAllByCompanyId();
//
//	@Query("select SalesProductGroupUserTarget from SalesProductGroupUserTarget SalesProductGroupUserTarget where SalesProductGroupUserTarget.company.id = ?#{principal.companyId} and SalesProductGroupUserTarget.accountWiseTarget = 'FALSE' ")
//	Page<SalesProductGroupUserTarget> findAllByCompanyId(Pageable pageable);
//
//	@Query("select SalesProductGroupUserTarget from SalesProductGroupUserTarget SalesProductGroupUserTarget where ?1 between SalesProductGroupUserTarget.fromDate and SalesProductGroupUserTarget.toDate and SalesProductGroupUserTarget.user.login = ?#{principal.username} and SalesProductGroupUserTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesProductGroupUserTarget> findByUserAndCurrentDate(LocalDate date);
//
//	@Query("select stguTarget from SalesProductGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesProductGroupUserTarget> findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid,String salesTargetGroupPid, LocalDate startDate,
//			LocalDate endDate);
//
//	List<SalesProductGroupUserTarget> findByUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
//			String userPid, LocalDate startDate, LocalDate endDate);
//	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(List<String> salesTargetGroupPids,
//			String userPid, LocalDate startDate, LocalDate endDate);
//	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid,
//			String userPid, LocalDate startDate, LocalDate endDate);
//	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidAndUserIdInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid,
//			List<Long> userIds, LocalDate startDate, LocalDate endDate);
//	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);
//	
//	List<SalesProductGroupUserTarget> findByAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String accountProfilePid, LocalDate startDate, LocalDate endDate);

	List<SalesProductGroupUserTarget> findByUserPidAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			String userPid, String productPid, LocalDate firstDateMonth, LocalDate lastDateMonth);

	List<SalesProductGroupUserTarget> findByProductGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> allProductGroupPids, String userPid, LocalDate fromDate, LocalDate toDate);
	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidAndUserPidAndAccountWiseTargetFalse(String salesTargetGroupPid,
//			String userPid);
//	
//	
//	@Query("select stguTarget from SalesProductGroupUserTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.pid = ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupIdInAndUserPidAndFromDateBetweenAndAccountWiseTargetFalse(Set<Long> groupIds, String userPid, LocalDate startDate, LocalDate endDate);
//	
//	@Query("select stguTarget from SalesProductGroupUserTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.id in ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupIdInAndUserIdInAndFromDateBetweenAndAccountWiseTargetFalse(Set<Long> groupIds, List<Long> userIds, LocalDate startDate, LocalDate endDate);
//	
//	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesProductGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesTargetGroup> findByUserPidAndGroupBySalesTargetGroup(String userPid);
//	
//	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesProductGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup in ?2 and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesTargetGroup> findByUserPidAndSalesTargetGroup(String userPid, List<SalesTargetGroup> salesTargetGroup);
//	
//	@Query("select DISTINCT stguTarget.salesTargetGroup from SalesProductGroupUserTarget stguTarget where stguTarget.company.id = ?#{principal.companyId} and stguTarget.accountWiseTarget = 'FALSE' ")
//	List<SalesTargetGroup> findAllSalesTargetGroupByCompanyId();
//
//	/** account wise */
//	List<SalesProductGroupUserTarget> findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
//			String userPid, String salesTArgetPid, String accountProfilePid, LocalDate firstDateMonth,
//			LocalDate lastDateMonth);
//	
//	@Query("select stguTarget.pid, stguTarget.amount, stguTarget.volume from SalesProductGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.accountProfile.pid = ?3 and stguTarget.fromDate >= ?4 AND stguTarget.toDate <= ?5 ")
//	List<Object[]> findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndDateBetween(String userPid, String salesTArgetPid, String accountProfilePid, LocalDate firstDateMonth, LocalDate lastDateMonth);
//	
//	List<SalesProductGroupUserTarget> findBySalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String salesTargetPid, String accountProfilePid, LocalDate firstDateMonth,
//			LocalDate lastDateMonth);
//
//	@Query("select sum(stguTarget.volume),sum(stguTarget.amount) from SalesProductGroupUserTarget stguTarget where stguTarget.accountProfile.pid = ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 ")
//	Object findSumOfAccountWiseTarget(String accountPid, LocalDate startDate, LocalDate endDate);
//	
//	@Query("select sum(stguTarget.amount), sum(stguTarget.volume) from SalesProductGroupUserTarget stguTarget where stguTarget.accountProfile.pid in ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 ")
//	Object findSumOfAccountWiseTarget(List<String> accountPids, LocalDate startDate, LocalDate endDate);
//	
//	@Query("select stguTarget from SalesProductGroupUserTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' and stguTarget.targetFrequency=?5")
//	List<SalesProductGroupUserTarget> findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(String userPid,String salesTargetGroupPid, LocalDate startDate,LocalDate endDate,TargetFrequency targetFrequency);
//	
//	@Query("select SalesProductGroupUserTarget from SalesProductGroupUserTarget SalesProductGroupUserTarget where SalesProductGroupUserTarget.company.id = ?#{principal.companyId} and SalesProductGroupUserTarget.accountWiseTarget = 'FALSE' and SalesProductGroupUserTarget.targetFrequency=?1 and SalesProductGroupUserTarget.fromDate <= ?3 AND SalesProductGroupUserTarget.toDate >= ?2 ")
//	List<SalesProductGroupUserTarget> findAllByCompanyIdAndTargetFrequencyAndDateBetween(TargetFrequency targetFrequency,LocalDate startDate, LocalDate endDate);
	
	
}
