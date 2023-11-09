package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * Spring Data JPA repository for the Task entity.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

	Optional<Task> findOneByPid(String pid);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activated = 'TRUE'")
	List<Task> findAllByCompanyId();
	
	@Query("select task.pid, task.activity.pid, task.activity.name,task.accountProfile.pid, task.accountProfile.name, task.remarks from Task task where task.company.id = ?#{principal.companyId} and task.activated = 'TRUE' order by task.accountProfile.name ASC")
	List<Object[]> findTaskPropertyByCompanyId();

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activated = 'TRUE'")
	Page<Task> findAllByCompanyId(Pageable pageable);

	@Query("select task from Task task where task.activity.pid in ?1 and task.activated = 'TRUE'")
	List<Task> findTaskByActivityPidIn(List<String> activityPids);

	@Query("select task from Task task where task.accountProfile.pid in ?1 and task.activated = 'TRUE'")
	List<Task> findTaskByAccountProfilePidIn(List<String> accProfilePids);

	@Query("select task from Task task where task.activity.pid in ?1 and task.accountProfile.pid in ?2 and task.activated = 'TRUE'")
	List<Task> findTaskByActivityPidInAndAccountProfilePidIn(List<String> activityPids, List<String> accProfilePids);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activity.pid = ?1 and task.activated = 'TRUE'")
	List<Task> findTaskByActivityPid(String activityPid);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activity.pid= ?1 and task.accountProfile.pid = ?2 and task.activated = 'TRUE'")
	List<Task> findTaskByActivityPidAndAccountPid(String activityPid, String accountPid);
	
	@Query("select task from Task task where task.company.id = ?1 and task.activity.pid= ?2 and task.accountProfile.pid = ?3 and task.activated = 'TRUE'")
	List<Task> findTaskByCompanyIdActivityPidAndAccountPid(long companyId ,String activityPid, String accountPid);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activated = ?1 ")
	List<Task> findAllByCompanyIdAndActivated(boolean active);

	@Query("select task from Task task where task.activity.pid in ?1 and task.activated = ?2")
	List<Task> findByActivityPidInAndActivated(List<String> activityPids, boolean active);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.accountType.pid in ?1 and  task.activated = ?2")
	List<Task> findByAccountTypePidInAndActivated(List<String> accountTypePids, boolean active);

	@Query("select task from Task task where task.activity.pid in ?1 and task.accountType.pid in ?2 and task.activated = ?3")
	List<Task> findByActivityPidInAndAccountTypePidInAndActivated(List<String> activityPids,
			List<String> accountTypePids, boolean active);
	
	@Query("select task.accountProfile from Task task where task.company.id = ?#{principal.companyId} and task.accountType.pid= ?1 and task.activity.pid = ?2")
	List<AccountProfile> findAllAccountProfileByCompanyAndAccountTypePid(String accountTypePid, String activityPid);

	@Query("select task from Task task where task.accountProfile.id in ?1 and task.activated = 'TRUE'")
	List<TaskDTO> findTaskByAccountProfileIdIn(List<Long> accProfileIds);

	@Query("select task from Task task where task.company.id = ?#{principal.companyId} and task.activated = 'TRUE'")
	List<Task> findAllPidsCompanyId();

}
