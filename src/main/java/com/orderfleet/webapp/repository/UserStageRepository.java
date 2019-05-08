package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.UserStage;

/**
 * Spring Data JPA repository for the UserStage entity.
 * 
 * @author Muhammed Riyas T
 * @since July 01, 2016
 */
public interface UserStageRepository extends JpaRepository<UserStage, Long> {

	@Transactional
	@Modifying
	@Query("delete from UserStage u where u.user.pid = ?1")
	void deleteUserStagesByUserPid(String userPid);

	@Query("select userStage from UserStage userStage where userStage.user.pid = ?1 ")
	List<UserStage> findUserStagesByUserPid(String userPid);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_user_stage userStage where userStage.user.pid = ?1", nativeQuery = true)
	void deleteByUserPid(String userPid);
	
	@Transactional
	@Modifying
	@Query("delete from UserStage userStatage where userStatage.id in ?1")
	void deleteByIdIn(List<Long> ids);

}
