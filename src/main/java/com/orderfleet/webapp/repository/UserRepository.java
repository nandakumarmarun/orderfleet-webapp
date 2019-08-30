package com.orderfleet.webapp.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.User;

/**
 * Spring Data JPA repository for the User entity.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findOneByActivationKey(String activationKey);

	Optional<User> findOneByDeviceKey(String deviceKey);

	List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

	List<User> findAllByActivated(Boolean activated);

	Optional<User> findOneByResetKey(String resetKey);

	Optional<User> findOneByEmail(String email);

	Optional<User> findOneByFirstName(String name);

	Optional<User> findOneByLogin(String login);

	@EntityGraph(attributePaths = "authorities")
	User findOneWithAuthoritiesById(Long id);

	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByLogin(String login);

	Optional<User> findOneByPid(String pid);

	List<User> findByPidIn(List<String> pids);

	@Query(value = "select distinct user from User user join fetch user.authorities", countQuery = "select count(user) from User user")
	Page<User> findAllWithAuthorities(Pageable pageable);

	@Query("select distinct user from User user join fetch user.authorities where user.company.pid =:pid")
	List<User> findAllWithAuthoritiesAndCompanyPid(@Param("pid") String companyPid);

	@Query("select user from User user where user.company.id = ?#{principal.companyId} and user.activated = 'TRUE'")
	List<User> findAllByCompanyId();

	@Query("select user from User user where user.company.id = ?#{principal.companyId} and user.activated = 'TRUE'")
	Page<User> findAllByCompanyId(Pageable pageable);

	@Query("select user from User user where user.company.pid = ?1 and user.activated = 'TRUE' order by user.firstName asc")
	List<User> findAllByCompanyPidSortedByName(String companyPid);

	List<User> findAllByCompanyPid(String companyPid);

	User findByCompanyIdAndFirstNameAndLastName(Long companyId, String firstName, String lastName);

	@Query("select user from User user where user.id in ?1")
	List<User> findByUserIdIn(List<Long> userIds);

	@Query("select user from User user where user.pid in ?1")
	List<User> findByUserPidIn(List<String> userIds);

	List<User> findByAuthoritiesIn(Set<Authority> authorities);

	// @Query("select user from User user where user.authorities in ?1 and
	// user.activated = ?2 order by user.firstName asc")
	List<User> findByAuthoritiesInAndActivated(Set<Authority> authorities, boolean activated);

	@Query("select user from User user where user.company.pid = ?1 order by user.id desc")
	List<User> findAllByCompanyPidSortedById(String companyPid);

	List<User> findAllByCompanyIdAndAuthoritiesInAndActivated(Long companyId, Set<Authority> authorities,
			boolean activated);

	@Query("select user from User user where user.company.pid = ?1 and user.pid = ?2")
	Optional<User> findAllUserByCompanyPidAndUserPid(String companyPid, String userPid);

	@Query("select count(user) from User user where user.company.legalName = ?1 and user.createdDate between ?2 and ?3")
	Long getCountofUserByCompanyNameAndCreatedDateBetween(String companyName, ZonedDateTime startDate,
			ZonedDateTime toDate);

	@Query("select count(user) from User user where user.company.legalName in ?1 and user.createdDate between ?2 and ?3")
	Long getCountofUserByCompanyNameInAndCreatedDateBetween(List<String> companyName, ZonedDateTime startDate,
			ZonedDateTime toDate);

	@Query("select user.id from User user where user.login = ?1")
	Long getIdByLogin(String login);

	@Query("select user.pid from User user where user.login = ?1")
	String getPidByLogin(String login);

	List<Long> findUserIdByActivated(Boolean activated);

	@Query("select user.pid, user.firstName, user.lastName from User user where user.company.id = ?#{principal.companyId} and user.activated = 'TRUE' order by user.firstName")
	List<Object[]> findByUserPropertyCompanyId();

	User findTop1ByCompanyId(Long companyId);

	@Query("select user.login, user.firstName, user.lastName, user.company.pid, user.company.legalName from User user where user.company.pid = ?1 order by user.firstName")
	List<Object[]> findUsersByCompanyPid(String companyPid);

	Long countByCompanyPidAndAuthoritiesIn(String companyPid, Set<Authority> authorities);

	@Query("select user.login from User user where user.login in ?1 and user.company.id=?#{principal.companyId}")
	List<String> findAllUserByLogin(List<String> logins);

	@Query("select user from User user where user.id in ?1 and user.company.id=?#{principal.companyId}")
	List<User> findAllByCompanyIdAndIdsIn(Set<Long> userIds);

}
