package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * Spring Data JPA repository for the ExecutiveTaskExecution entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public interface ExecutiveTaskExecutionRepository extends JpaRepository<ExecutiveTaskExecution, Long> {

	Optional<ExecutiveTaskExecution> findOneByPid(String pid);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdOrderByDateDesc();

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} Order By exeTaskExecution.date desc")
	Page<ExecutiveTaskExecution> findAllByCompanyIdOrderByDateDesc(Pageable pageable);

	Long countByUserIdAndActivityIdAndActivityStatusNotAndDateBetween(Long userId, Long activityId,
			ActivityStatus activityStatus, LocalDateTime startDate, LocalDateTime endDate);

	Long countByActivityPidAndUserPidAndActivityStatusNotAndDateBetween(String activityPid, String userPid,
			ActivityStatus activityStatus, LocalDateTime startDateTime, LocalDateTime endDateTime);

	Long countByActivityPidAndActivityStatusNotAndDateBetween(String activityPid, ActivityStatus activityStatus,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdAndDateBetweenOrderByDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.id in ?1 and exeTaskExecution.date between ?2 and ?3 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByUserIdInAndDateBetweenOrderByDateDesc(List<Long> userIds,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.user.pid = ?1 and exeTaskExecution.activity.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(String userPid,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.user.pid = ?1 and exeTaskExecution.date between ?2 and ?3 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.activity.pid = ?1 and exeTaskExecution.date between ?2 and ?3 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(String activityPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.id in ?1 and exeTaskExecution.activity.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByUserIdInActivityPidAndDateBetweenOrderByDateDesc(List<Long> userIds,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate);

	ExecutiveTaskExecution findTop1ByUserPidAndDateBetweenOrderByDateDesc(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	ExecutiveTaskExecution findTop1ByAccountProfileInAndDateBetweenOrderByDateDesc(List<AccountProfile> accountProfiles,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2")
	Long countByDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.user.login = ?3")
	Long countByDateBetweenAndUserLogin(LocalDateTime fromDate, LocalDateTime toDate, String login);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 ")
	Long countByDateBetweenAndActivities(LocalDateTime fromDate, LocalDateTime toDate, List<Activity> activities);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.pid = ?4 ")
	Long countByDateBetweenAndActivities(LocalDateTime fromDate, LocalDateTime toDate, List<Activity> activities,
			String userPid);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.executiveTaskPlan IS NULL ")
	Long countByDateBetweenAndActivitiesAndTaskPlanIsNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.pid = ?4 and exeTaskExecution.executiveTaskPlan IS NULL ")
	Long countByDateBetweenAndActivitiesAndUserAndTaskPlanIsNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, String userPid);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.executiveTaskPlan IS NOT NULL ")
	Long countByDateBetweenAndActivitiesAndTaskPlanIsNotNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.pid = ?4 and exeTaskExecution.executiveTaskPlan IS NOT NULL ")
	Long countByDateBetweenAndActivitiesAndUserAndTaskPlanIsNotNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, String userPid);

	Long countByUserPidAndDateBetween(String userPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.login = ?#{principal.username} and exeTaskExecution.activity.pid = ?1 and exeTaskExecution.accountProfile.pid = ?2 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findByUserAndActivityPidAndAccountPid(String activityPid, String accountPid);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.login = ?#{principal.username} and exeTaskExecution.activity.pid = ?1 and exeTaskExecution.accountProfile.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findByUserAndActivityPidAndAccountPidAndDateBetween(String activityPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.user.pid = ?1 and exeTaskExecution.activity.pid = ?2 and exeTaskExecution.accountProfile.pid = ?3 and exeTaskExecution.date between ?4 and ?5 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdUserPidActivityPidAccountPidAndDateBetweenOrderByDateDesc(
			String userPid, String activityPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.accountProfile.pid = ?1 and exeTaskExecution.date between ?2 and ?3 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.id in ?1 and exeTaskExecution.accountProfile.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByUserIdInAccountPidAndDateBetweenOrderByDateDesc(List<Long> userIds,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.user.pid = ?1 and exeTaskExecution.accountProfile.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByDateDesc(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.activity.pid = ?1 and exeTaskExecution.accountProfile.pid = ?2 and exeTaskExecution.date between ?3 and ?4 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdActivityPidAccountPidAndDateBetweenOrderByDateDesc(
			String activityPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.id in ?1 and exeTaskExecution.activity.pid = ?2 and exeTaskExecution.accountProfile.pid = ?3 and exeTaskExecution.date between ?4 and ?5 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByUserIdInActivityPidAccountPidAndDateBetweenOrderByDateDesc(List<Long> userIds,
			String activityPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and status=false Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> findAllByCompanyIdAndStatusFalse();

	@Query("select exeTaskExecution.executiveTaskPlan from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.executiveTaskPlan in ?1 Order By exeTaskExecution.id asc")
	List<ExecutiveTaskPlan> findExecutiveTaskPlanByExecutiveTaskPlanIn(List<ExecutiveTaskPlan> executiveTaskPlans);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.user.pid = ?1 and exeTaskExecution.date between ?2 and ?3 Order By exeTaskExecution.date asc")
	List<ExecutiveTaskExecution> findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	List<ExecutiveTaskExecution> findTop2ByUserPidAndDateBetweenAndLocationTypeInOrderByDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<LocationType> locationTypes);

	// User wise - start
	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.id in ?4")
	Long countByDateBetweenAndActivitiesAndUserIdIn(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, List<Long> userIds);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.id in ?4 and exeTaskExecution.executiveTaskPlan IS NOT NULL ")
	Long countByDateBetweenAndActivitiesAndUserIdInAndTaskPlanIsNotNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, List<Long> userIds);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.user.id in ?4 and exeTaskExecution.executiveTaskPlan IS NULL ")
	Long countByDateBetweenAndActivitiesAndUserIdInAndTaskPlanIsNull(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, List<Long> userIds);

	// User wise - end

	// Account wise - start
	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.accountProfile in ?4 ")
	Long countByDateBetweenAndActivitiesAndAccountProfileIn(LocalDateTime fromDate, LocalDateTime toDate,
			List<Activity> activities, List<AccountProfile> accountProfiles);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.accountProfile in ?4 and exeTaskExecution.executiveTaskPlan IS NOT NULL ")
	Long countByDateBetweenAndActivitiesAndAccountProfileInAndTaskPlanIsNotNull(LocalDateTime fromDate,
			LocalDateTime toDate, List<Activity> activities, List<AccountProfile> accountProfiles);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity in ?3 and exeTaskExecution.accountProfile in ?4 and exeTaskExecution.executiveTaskPlan IS NULL ")
	Long countByDateBetweenAndActivitiesAndAccountProfileInAndTaskPlanIsNull(LocalDateTime fromDate,
			LocalDateTime toDate, List<Activity> activities, List<AccountProfile> accountProfiles);

	// Account wise - end

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.activity.pid = ?1 and exeTaskExecution.accountProfile.pid = ?2")
	List<ExecutiveTaskExecution> findAllByCompanyIdActivityPidAccountPid(String activityPid, String accountPid);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.clientTransactionKey = ?1 ")
	Optional<ExecutiveTaskExecution> findByCompanyIdAndClientTransactionKey(String clientTransactionKey);

	@Query("select distinct exeTaskExecution.user from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?1")
	List<User> findAllUniqueUsersFromExecutiveTaskExecution(String companyPid);

	@Query("select distinct exeTaskExecution.user from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?1 and exeTaskExecution.createdDate between ?2 and ?3")
	List<User> getCountUniqueUsersFromExecutiveTaskExecutionAndCreateDateBetween(String companyPid,
			LocalDateTime monthStartDate, LocalDateTime monthEndDate);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?1 and exeTaskExecution.user.pid = ?2 and exeTaskExecution.createdDate between ?3 and ?4")
	Long getCountFromExecutiveTaskExecutionByUserPidAndCreateDateBetween(String companyPid, String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	// billing report
	@Query("select exeTaskExecution.user.login from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.user.id in ?1 and exeTaskExecution.date between ?2 and ?3")
	List<String> findUserByCompanyPidAndUserIdInAndDateBetweenOrderByDateDesc(List<Long> userIds,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity.pid in ?3 and exeTaskExecution.user.id in ?4 and exeTaskExecution.accountProfile.pid in ?5 Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(LocalDateTime fromDate,
			LocalDateTime toDate, List<String> activityPids, List<Long> userIds, List<String> accountPids);
	
	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.activity.pid in ?3 and exeTaskExecution.user.id in ?4  Order By exeTaskExecution.date desc")
	List<ExecutiveTaskExecution> getByDateBetweenAndActivityPidInAndUserIdIn(LocalDateTime fromDate,
			LocalDateTime toDate, List<String> activityPids, List<Long> userIds);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.id = ?#{principal.companyId} and exeTaskExecution.date between ?1 and ?2 and exeTaskExecution.user.pid = ?3 and exeTaskExecution.accountProfile.pid in ?4")
	List<ExecutiveTaskExecution> findAllByDateBetweenAndUserPidAndAccountProfilePidIn(LocalDateTime fromDate,
			LocalDateTime toDate, String userPid, List<String> accountProfilePids);

	@Query("select count(exeTaskExecution) from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?2 and exeTaskExecution.location in ?1 and exeTaskExecution.createdDate between ?3 and ?4 and exeTaskExecution.locationType = ?5")
	Long countAndUserNameByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate,LocationType locationType);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?2 and exeTaskExecution.location in ?1 and exeTaskExecution.createdDate between ?3 and ?4")
	List<ExecutiveTaskExecution> getByCompanyPidAndLocationInAndDateBetween(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.pid in ?1")
	List<ExecutiveTaskExecution> findAllByExecutiveTaskExecutionPidIn(List<String>pids);
	
	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.company.pid = ?2 and exeTaskExecution.location in ?1 and exeTaskExecution.createdDate between ?3 and ?4 and exeTaskExecution.locationType = ?5")
	List<ExecutiveTaskExecution> getByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate,LocationType locationType);
	
	@Query("select ete.id, ep.name from ExecutiveTaskExecution ete inner join EmployeeProfile ep on ete.user.id=ep.user.id where ete.user.id in ?1 and ete.date between ?2 and ?3 Order By ep.name ASC")
	List<Object[]> findByUserIdInAndDateBetween(List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select exeTaskExecution from ExecutiveTaskExecution exeTaskExecution where exeTaskExecution.pid = ?1")
	ExecutiveTaskExecution findByExecutionPid(String executionPid);

}
