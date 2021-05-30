package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;

/**
 * Spring Data JPA repository for the EmployeeProfile entity.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

	Optional<EmployeeProfile> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	List<EmployeeProfile> findByCompanyIdAndNameIgnoreCaseIn(Long id, List<String> empNames);

	Optional<EmployeeProfile> findOneByPid(String pid);

	EmployeeProfile findEmployeeProfileByPid(String pid);

	EmployeeProfile findEmployeeProfileByUser(User user);

	EmployeeProfile findEmployeeProfileByUserLogin(String userLogin);

	Optional<EmployeeProfile> findByUserPid(String userPid);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId}")
	Page<EmployeeProfile> findAllByCompanyId(Pageable pageable);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.activated = ?1 Order By employeeProfile.name asc")
	List<EmployeeProfile> findAllByCompanyId(boolean activate);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.activated = ?1 and employeeProfile.user IS NOT NULL Order By employeeProfile.name asc")
	List<EmployeeProfile> findAllByCompanyIdAndUser(boolean activate);

	@Query("select employeeProfile.profileImage from EmployeeProfile employeeProfile where employeeProfile.user.login = ?#{principal.username}")
	byte[] findtCurrentUserEmployeeProfileImage();

	List<EmployeeProfile> findAllByCompanyPid(String companyPid);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} Order By employeeProfile.name asc")
	Page<EmployeeProfile> findAllByCompanyIdOrderByEmployeeProfileName(Pageable pageable);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.activated = ?1 Order By employeeProfile.name asc")
	Page<EmployeeProfile> findAllByCompanyAndActivatedEmployeeProfileOrderByName(Pageable pageable, boolean active);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.activated = ?1")
	List<EmployeeProfile> findAllByCompanyAndDeativatedEmployeeProfile(boolean deactive);

	@Query("select employeeProfile.user.id from EmployeeProfile employeeProfile where employeeProfile.id in ?1 and employeeProfile.activated = true")
	List<Long> findUserIdByEmployeeIdIn(List<Long> employeeIds);

	@Query("select employeeProfile.user.id from EmployeeProfile employeeProfile where employeeProfile.pid in ?1")
	List<Long> findUserIdByEmployeePidIn(List<String> employeePids);

	@Query("select employeeProfile.user.id from EmployeeProfile employeeProfile where employeeProfile.activated = ?1")
	List<Long> findAllUserIdsByActivated(boolean activated);
	
	@Query("select employeeProfile.user.id from EmployeeProfile employeeProfile where employeeProfile.activated = ?1 and employeeProfile.pid in ?2")
	List<Long> findAllUserIdsByActivatedAndEmployeePidIn(boolean activated,List<String> employeePids);

	List<EmployeeProfile> findByUserPidIn(List<String> toUserPids);

	List<EmployeeProfile> findByUserPidInOrderByNameAsc(List<String> toUserPids);

	List<EmployeeProfile> findByUserIdIn(List<Long> toUserPids);

	List<EmployeeProfile> findByPidIn(List<String> employeePids);

	@Query("select employeeProfile.pid, employeeProfile.name from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.user.id in ?1 and employeeProfile.activated = ?2 Order By employeeProfile.name asc")
	List<Object[]> findEmployeeByUserIdInAndActivated(List<Long> userIds, boolean activate);

	@Query("select employeeProfile.name from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId}")
	List<String> findEmployeeNamesByCompanyId();

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.user.id in ?1 and employeeProfile.activated = ?2 Order By employeeProfile.name asc")
	List<EmployeeProfile> findByUserIdInAndActivated(List<Long> toUserPids, boolean activate);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.activated = ?1 and employeeProfile.user.id not in ?2 Order By employeeProfile.name asc")
	List<EmployeeProfile> findAllByCompanyIdNotInUserIds(boolean activate, List<Long> toUserIds);

	@Query("select employeeProfile.pid from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId}")
	List<String> findEmployeePidsByCompanyId();

	List<EmployeeProfile> findAllByCompanyId(Long companyId);

	@Query("select employeeProfile.user.pid from EmployeeProfile employeeProfile where employeeProfile.pid = ?1")
	String findUserPidByEmployeePid(String employeePid);

	@Query("select employeeProfile.pid from EmployeeProfile employeeProfile where employeeProfile.id in ?1")
	List<String> findEmployeeByIdsIn(List<Long> employeeIds);

	@Query("select employeeProfile.user.pid from EmployeeProfile employeeProfile where employeeProfile.pid in ?1")
	List<String> findUserPidsByEmployeePids(List<String> employeePid);

	@Query("select employeeProfile from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.id in ?1")
	List<EmployeeProfile> findAllByCompanyIdAndIdsIn(Set<Long> employeeIds);

	@Query("select employeeProfile.user.id from EmployeeProfile employeeProfile where employeeProfile.activated = ?1 and employeeProfile.company.id = ?#{principal.companyId}")
	List<Long> findAllUserIdsByActivatedAndCompanyId(boolean activated);

	@Query("select employeeProfile.id,employeeProfile.name from EmployeeProfile employeeProfile where employeeProfile.company.id = ?#{principal.companyId} and employeeProfile.id IN ?1")
	List<Object[]> findByEmpIdIn(Set<Long> ids);
}
