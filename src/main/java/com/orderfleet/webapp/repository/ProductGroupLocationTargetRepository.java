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
import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupMonthlyTargetDTO;

public interface ProductGroupLocationTargetRepository extends JpaRepository<ProductGroupLocationTarget, Long> {

	List<ProductGroupLocationTarget> findByLocationPidAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			String locationPid, String pid, LocalDate firstDateMonth, LocalDate lastDateMonth);

	List<ProductGroupLocationTarget> findByLocationPidInAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> locationPids, String productGroupPid, LocalDate fromDate, LocalDate toDate);

	Optional<ProductGroupLocationTarget> findOneByPid(String pid);

	/*
	 * Optional<ProductGroupLocationTarget> findOneByPid(String pid);
	 * 
	 * @Query("select productGroupLocationTarget from ProductGroupLocationTarget productGroupLocationTarget where productGroupLocationTarget.company.id = ?#{principal.companyId} "
	 * ) List<ProductGroupLocationTarget> findAllByCompanyId();
	 * 
	 * @Query("select productGroupLocationTarget from ProductGroupLocationTarget productGroupLocationTarget where productGroupLocationTarget.company.id = ?#{principal.companyId} "
	 * ) Page<ProductGroupLocationTarget> findAllByCompanyId(Pageable pageable);
	 * 
	 * @Query("select productGroupLocationTarget from ProductGroupLocationTarget productGroupLocationTarget where ?1 between productGroupLocationTarget.fromDate and productGroupLocationTarget.toDate"
	 * ) List<ProductGroupLocationTarget> findByUserAndCurrentDate(LocalDate date);
	 * 
	 * @Query("select stguTarget from ProductGroupLocationTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<ProductGroupLocationTarget>
	 * findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid, String
	 * salesTargetGroupPid, LocalDate startDate, LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findByUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * String userPid, LocalDate startDate, LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * List<String> salesTargetGroupPids, String userPid, LocalDate startDate,
	 * LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupPidAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * String salesTargetGroupPid, String userPid, LocalDate startDate, LocalDate
	 * endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupPidAndUserIdInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * String salesTargetGroupPid, List<Long> userIds, LocalDate startDate,
	 * LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findByAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
	 * String accountProfilePid, LocalDate startDate, LocalDate endDate);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findByUserPidAndSalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
	 * String userPid, String salesTargetPid, LocalDate firstDateMonth, LocalDate
	 * lastDateMonth);
	 * 
	 * List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupPidAndUserPidAndAccountWiseTargetFalse( String
	 * salesTargetGroupPid, String userPid);
	 * 
	 * @Query("select stguTarget from ProductGroupLocationTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.pid = ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupIdInAndUserPidAndFromDateBetweenAndAccountWiseTargetFalse(
	 * Set<Long> groupIds, String userPid, LocalDate startDate, LocalDate endDate);
	 * 
	 * @Query("select stguTarget from ProductGroupLocationTarget stguTarget where stguTarget.salesTargetGroup.id in ?1 and stguTarget.user.id in ?2 and stguTarget.fromDate >= ?3 and stguTarget.toDate <= ?4 and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<ProductGroupLocationTarget>
	 * findBySalesTargetGroupIdInAndUserIdInAndFromDateBetweenAndAccountWiseTargetFalse(
	 * Set<Long> groupIds, List<Long> userIds, LocalDate startDate, LocalDate
	 * endDate);
	 * 
	 * @Query("select DISTINCT stguTarget.salesTargetGroup from ProductGroupLocationTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<SalesTargetGroup> findByUserPidAndGroupBySalesTargetGroup(String
	 * userPid);
	 * 
	 * @Query("select DISTINCT stguTarget.salesTargetGroup from ProductGroupLocationTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup in ?2 and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<SalesTargetGroup> findByUserPidAndSalesTargetGroup(String userPid,
	 * List<SalesTargetGroup> salesTargetGroup);
	 * 
	 * @Query("select DISTINCT stguTarget.salesTargetGroup from ProductGroupLocationTarget stguTarget where stguTarget.company.id = ?#{principal.companyId} and stguTarget.accountWiseTarget = 'FALSE' "
	 * ) List<SalesTargetGroup> findAllSalesTargetGroupByCompanyId();
	 * 
	 *//** account wise *//*
							 * List<ProductGroupLocationTarget>
							 * findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
							 * String userPid, String salesTArgetPid, String accountProfilePid, LocalDate
							 * firstDateMonth, LocalDate lastDateMonth);
							 * 
							 * @Query("select stguTarget.pid, stguTarget.amount, stguTarget.volume from ProductGroupLocationTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.accountProfile.pid = ?3 and stguTarget.fromDate >= ?4 AND stguTarget.toDate <= ?5 "
							 * ) List<Object[]>
							 * findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndDateBetween(String
							 * userPid, String salesTArgetPid, String accountProfilePid, LocalDate
							 * firstDateMonth, LocalDate lastDateMonth);
							 * 
							 * List<ProductGroupLocationTarget>
							 * findBySalesTargetGroupPidAndAccountProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
							 * String salesTargetPid, String accountProfilePid, LocalDate firstDateMonth,
							 * LocalDate lastDateMonth);
							 * 
							 * @Query("select sum(stguTarget.volume),sum(stguTarget.amount) from ProductGroupLocationTarget stguTarget where stguTarget.accountProfile.pid = ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 "
							 * ) Object findSumOfAccountWiseTarget(String accountPid, LocalDate startDate,
							 * LocalDate endDate);
							 * 
							 * @Query("select sum(stguTarget.amount), sum(stguTarget.volume) from ProductGroupLocationTarget stguTarget where stguTarget.accountProfile.pid in ?1 and stguTarget.fromDate <= ?3 AND stguTarget.toDate >= ?2 "
							 * ) Object findSumOfAccountWiseTarget(List<String> accountPids, LocalDate
							 * startDate, LocalDate endDate);
							 * 
							 * @Query("select stguTarget from ProductGroupLocationTarget stguTarget where stguTarget.user.pid = ?1 and stguTarget.salesTargetGroup.pid = ?2 and stguTarget.fromDate <= ?4 AND stguTarget.toDate >= ?3 and stguTarget.accountWiseTarget = 'FALSE' and stguTarget.targetFrequency=?5"
							 * ) List<ProductGroupLocationTarget>
							 * findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(String userPid,
							 * String salesTargetGroupPid, LocalDate startDate, LocalDate endDate,
							 * TargetFrequency targetFrequency);
							 * 
							 * @Query("select productGroupLocationTarget from ProductGroupLocationTarget productGroupLocationTarget where productGroupLocationTarget.company.id = ?#{principal.companyId} and productGroupLocationTarget.accountWiseTarget = 'FALSE' and productGroupLocationTarget.targetFrequency=?1 and productGroupLocationTarget.fromDate <= ?3 AND productGroupLocationTarget.toDate >= ?2 "
							 * ) List<ProductGroupLocationTarget>
							 * findAllByCompanyIdAndTargetFrequencyAndDateBetween(TargetFrequency
							 * targetFrequency, LocalDate startDate, LocalDate endDate);
							 */

}
