package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserOnPremise;

public interface UserOnPremiseRepository extends JpaRepository<UserOnPremise, UUID> {

	Optional<UserOnPremise> findOneByLogin(String login);

	@Query("select user.fullName from UserOnPremise user where user.companyPid = ?1")
	List<String> findAllfullNamesByCompanyPid(String companyId);

	@Query("select user from UserOnPremise user where user.companyPid = ?1")
	List<UserOnPremise> findAllUserOnPremiseByCompanyPid(String pid);
	
	@Query("select user.login from UserOnPremise user where user.login in ?1 and user.companyPid= ?2")
	List<String> findAllUserByLogin(List<String> logins,String pid);

}
