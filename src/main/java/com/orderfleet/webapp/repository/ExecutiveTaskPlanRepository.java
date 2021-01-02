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
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;

/**
 * Spring Data JPA repository for the ExecutiveTaskPlan entity.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface ExecutiveTaskPlanRepository extends JpaRepository<ExecutiveTaskPlan, Long> {

	Optional<ExecutiveTaskPlan> findOneByPid(String pid);

	@Query("select executiveTaskPlan from ExecutiveTaskPlan executiveTaskPlan where executiveTaskPlan.company.id = ?#{principal.companyId}")
	List<ExecutiveTaskPlan> findAllByCompanyId();

	@Query("select executiveTaskPlan from ExecutiveTaskPlan executiveTaskPlan where executiveTaskPlan.company.id = ?#{principal.companyId}")
	Page<ExecutiveTaskPlan> findAllByCompanyId(Pageable pageable);

	@Query("select executiveTaskPlan from ExecutiveTaskPlan executiveTaskPlan where executiveTaskPlan.user.login = ?#{principal.username} order by executiveTaskPlan.id asc")
	List<ExecutiveTaskPlan> findByUserIsCurrentUser();

	@Query("select executiveTaskPlan from ExecutiveTaskPlan executiveTaskPlan where executiveTaskPlan.plannedDate <= ?1 and executiveTaskPlan.user.login = ?#{principal.username} order by executiveTaskPlan.id asc")
	List<ExecutiveTaskPlan> findTillCurrentDateAndUserIsCurrentUser(LocalDateTime currentDate);

	ExecutiveTaskPlan findTop1ByUserIdAndActivityIdAndAccountProfileIdAndTaskPlanStatusAndPlannedDateBetweenOrderByIdDesc(
			Long userId, Long activityId, Long accountId, TaskPlanStatus TaskPlanStatus, LocalDateTime startDateTime,
			LocalDateTime endDateTime);

	Long countByActivityPidAndUserPidAndPlannedDateBetween(String activityPid, String userPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	Long countByActivityPidAndPlannedDateBetween(String activityPid, LocalDateTime startDateTime,
			LocalDateTime endDateTime);

	List<ExecutiveTaskPlan> findByUserPidAndTaskPlanStatus(String userPid, TaskPlanStatus taskPlanStatus);

	List<ExecutiveTaskPlan> findByUserPidAndTaskPlanStatusAndPlannedDateBetween(String userPid,
			TaskPlanStatus taskPlanStatus, LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<ExecutiveTaskPlan> findByPlannedDateBetweenAndCompanyIdOrderByIdAsc(LocalDateTime startDateTime,
			LocalDateTime endDateTime, Long companyId);

	List<ExecutiveTaskPlan> findByUserLoginAndPlannedDateBetweenOrderByIdAsc(String login, LocalDateTime startDateTime,
			LocalDateTime endDateTime);

	List<ExecutiveTaskPlan> findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(String userPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime, Long companyId);

	List<ExecutiveTaskPlan> findByUserPidInAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(List<String> userPids,
			LocalDateTime startDateTime, LocalDateTime endDateTime, Long companyId);

	List<ExecutiveTaskPlan> findByUserPidAndPlannedDateBetweenOrderByPlannedDateAsc(String userPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<ExecutiveTaskPlan> findByUserPidAndPlannedDateBetweenAndTaskPlanStatusNot(String userPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime, TaskPlanStatus taskPlanStatus);

	void deleteByUserPidAndTaskPlanStatusAndPlannedDateBetween(String userPid, TaskPlanStatus taskPlanStatus,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	Long countByPlannedDateBetweenAndActivityIn(LocalDateTime startDateTime, LocalDateTime endDateTime,
			List<Activity> activities);

	List<ExecutiveTaskPlan> findByUserPidAndPlannedDateGreaterThanEqualOrderByPlannedDateAsc(String userPid,
			LocalDateTime plannedDateTime);

	Long countByPlannedDateBetweenAndActivityInAndUserPid(LocalDateTime startDateTime, LocalDateTime endDateTime,
			List<Activity> activities, String userPid);

	List<ExecutiveTaskPlan> findByUserPidAndPidIn(String userPid, List<String> executiveTaskPlanPids);

	List<ExecutiveTaskPlan> findByUserPidAndPlannedDateBetweenOrderByPlannedDateDesc(String userPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<ExecutiveTaskPlan> findByUserPidInAndPlannedDateBetweenOrderByIdAsc(List<String> userPids,
			LocalDateTime startDateTime, LocalDateTime endDateTime);

	// ################## start User wise #######################################
	Long countByPlannedDateBetweenAndActivityInAndUserIdIn(LocalDateTime startDateTime, LocalDateTime endDateTime,
			List<Activity> activities, List<Long> userIds);

	// ################## end User wise #########################################

	// ################## start Account wise #######################################
	Long countByPlannedDateBetweenAndActivityInAndAccountProfileIn(LocalDateTime startDateTime,
			LocalDateTime endDateTime, List<Activity> activities, List<AccountProfile> accountProfiles);

	// ################## end Account wise #########################################

	Long countByUserPidAndPlannedDateBetweenAndActivityPid(String userPid, LocalDateTime startDateTime,
			LocalDateTime endDateTime, String activityPid);

	void deleteByUserPidAndTaskListPidAndPlannedDateBetween(String userPid, String taskListPid,
			LocalDateTime startDateTime, LocalDateTime endDateTime);
}
