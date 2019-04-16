package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CustomerTimeSpent;
/**
 * A Repository for Customer Time Spent .
 * 
 * @author Fahad
 * @since Jan 20, 2018
 */
public interface CustomerTimeSpentRepository extends JpaRepository<CustomerTimeSpent, Long>{

	@Query("select customerTimeSpent from CustomerTimeSpent customerTimeSpent where customerTimeSpent.company.id = ?#{principal.companyId} and customerTimeSpent.user.id = ?1 and customerTimeSpent.active = 'TRUE'")
	List<CustomerTimeSpent>findAllByUserIdAndActiveTrue(Long userId);
	
	Optional<CustomerTimeSpent> findTop1ByCompanyIdAndUserPidAndActiveOrderByStartTimeAsc(Long companyId, String userPid,boolean active);
	
	Optional<CustomerTimeSpent> findOneByUserLoginAndClientTransactionKey(String login,String clientTransactionKey);
}
