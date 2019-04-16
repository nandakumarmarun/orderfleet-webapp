package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.UserPriceLevel;

/**
 * Spring Data JPA repository for the UserPriceLevel entity.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
public interface UserPriceLevelRepository extends
		JpaRepository<UserPriceLevel, Long> {

	@Query("select userPriceLevel.priceLevel from UserPriceLevel userPriceLevel where userPriceLevel.user.login = ?#{principal.username} and userPriceLevel.priceLevel.activated = true")
	List<PriceLevel> findPriceLevelsByUserIsCurrentUser();

	@Query("select userPriceLevel.priceLevel from UserPriceLevel userPriceLevel where userPriceLevel.user.pid = ?1 ")
	List<PriceLevel> findPriceLevelsByUserPid(String userPid);

	void deleteByUserPid(String userPid);

	@Query("select  userPriceLevel.priceLevel from UserPriceLevel userPriceLevel where userPriceLevel.user.login = ?#{principal.username} and userPriceLevel.priceLevel.activated = ?1 ")
	List<PriceLevel> findPriceLevelsByUserIsCurrentUserAndPriceLevelActivated(
			boolean active);
	
	@Query("select userPriceLevel.priceLevel from UserPriceLevel userPriceLevel where userPriceLevel.user.login = ?#{principal.username} and userPriceLevel.priceLevel.activated = true and userPriceLevel.priceLevel.lastModifiedDate > ?1")
	List<PriceLevel> findPriceLevelsByUserIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> toUserPids);

	List<UserPriceLevel> findByUserPid(String fromUserPid);
	
	void deleteByCompanyId(Long companyId);

	List<String> findUserPidByCompanyPid(String pid);
	
	//Optional<UserPriceLevel> findByCompanyIdAndNameIgnoreCase(Long id, String name);
}
