package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.UserStockLocation;

/**
 * Spring Data JPA repository for the UserStockLocation entity.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public interface UserStockLocationRepository extends JpaRepository<UserStockLocation, Long> {

	@Query("select userStockLocation.stockLocation from UserStockLocation userStockLocation where userStockLocation.user.login = ?#{principal.username}")
	List<StockLocation> findStockLocationsByUserIsCurrentUser();

	@Query("select userStockLocation.stockLocation from UserStockLocation userStockLocation where userStockLocation.user.pid = ?1 ")
	List<StockLocation> findStockLocationsByUserPid(String userPid);

	void deleteByUserPid(String userPid);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all stockLocationDTOs from StockLocation by status and company
	 *        user.
	 * 
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Query("select userStockLocation.stockLocation from UserStockLocation userStockLocation where userStockLocation.user.login = ?#{principal.username} and userStockLocation.stockLocation.activated = ?1")
	List<StockLocation> findStockLocationsByUserIsCurrentUserAndStockLocationActivated(boolean active);

	@Query("select userStockLocation.stockLocation from UserStockLocation userStockLocation where userStockLocation.user.login = ?#{principal.username} and userStockLocation.stockLocation.activated = ?1 and userStockLocation.stockLocation.lastModifiedDate > ?2")
	List<StockLocation> findStockLocationsByUserIsCurrentUserAndStockLocationActivatedAndLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> toUserPids);

	List<UserStockLocation> findByUserPid(String fromUserPid);

	List<String> findUserPidByCompanyPid(String pid);

	List<String> findPidByCompanyId(Long id);

	void deleteByCompanyIdAndStockLocationPid(Long companyId, String pid);

	List<UserStockLocation> findByUserPidAndStockLocationPid(String fromUserPid, String stockLocationPid);

	List<UserStockLocation> findAllByCompanyPid(String pid);

	@Transactional
	@Modifying
	@Query("delete from UserStockLocation userStockLocation where userStockLocation.company.id = ?1 and userStockLocation.id in ?2")
	void deleteByIdIn(Long companyId, List<Long> userStockLocationIds);

	@Query("select userStockLocation.stockLocation.pid from UserStockLocation userStockLocation where userStockLocation.user.pid = ?1")
	List<String> findStockLocationPidsByUserPid(String userPid);
}
