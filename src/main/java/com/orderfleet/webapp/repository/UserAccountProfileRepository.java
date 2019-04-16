package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserAccountProfile;

/**
 * Spring Data JPA repository for the UserAccountProfile entity.
 *
 * @author Sarath
 * @since Oct 24, 2016
 */
public interface UserAccountProfileRepository extends JpaRepository<UserAccountProfile, Long> {

	@Query("select userAccountProfile from UserAccountProfile userAccountProfile where userAccountProfile.company.id = ?#{principal.companyId}")
	public Page<UserAccountProfile> findAllByCompanyId(Pageable pageable);

	@Query("select userAccountProfile from UserAccountProfile userAccountProfile where userAccountProfile.company.id = ?#{principal.companyId}")
	List<UserAccountProfile> findAllByCompanyId();

	public Optional<UserAccountProfile> findByCompanyIdAndAccountProfilePid(Long id, String accountProfilePid);

	public Optional<UserAccountProfile> findByCompanyIdAndUserPid(Long currentUsersCompanyId, String userPid);
	
	public Optional<UserAccountProfile> findByCompanyIdAndUserLogin(Long companyId, String login);
}
