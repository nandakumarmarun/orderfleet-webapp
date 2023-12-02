package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.UserTaskListAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserTaskListAssociationRepository extends JpaRepository<UserTaskListAssociation, Long> {

	
	@Query(value="select * from tbl_user_task_list_association where user_id= ?1",nativeQuery = true)
	List<UserTaskListAssociation> findAllByUserId(Long id);

	Optional<UserTaskListAssociation> findOneByPid(String pid);
	
	

	
}
