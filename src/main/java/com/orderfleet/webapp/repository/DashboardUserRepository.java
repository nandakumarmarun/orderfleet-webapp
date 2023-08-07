package com.orderfleet.webapp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDTO;

/**
 * Spring Data JPA repository for the DashboardUser entity.
 * 
 * @author Muhammed Riyas T
 * @since August 23, 2016
 */
public interface DashboardUserRepository extends JpaRepository<DashboardUser, Long> {

	Optional<DashboardUser> findByUserLogin(String login);
	
	@Query("select dashboardUser.user from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId} order by dashboardUser.sortOrder ASC")
	List<User> findUsersByCompanyId();
	
	@Query("select dashboardUser.user from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId}")
	Page<User> findUsersByCompanyId(Pageable pageable);

	@Modifying
	@Query("delete from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId}")
	void deleteDashboardUsers();

	@Query("select count(dashboardUser) from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId}")
	long countByCompanyId();
	
	@Query(nativeQuery = true , value = "select count(*) from tbl_dashboard_user where user_id in ?1")
	long countByUserIdIn(List<Long> userIds);
	
	@Query("select dashboardUser.user from DashboardUser dashboardUser where dashboardUser.user.id in ?1 order by dashboardUser.sortOrder asc")
	List<User> findUserByUserIdIn(List<Long> userIds);
	
	@Query("select dashboardUser.user.id from DashboardUser dashboardUser where dashboardUser.user.id in ?1 order by dashboardUser.user.firstName")
	Set<Long> findUserIdsByUserIdIn(Collection<Long> userIds);
	
	@Query("select dashboardUser.user.id from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId}")
	Set<Long> findUserIdsByCompanyId();

	@Query("select dashboardUser from DashboardUser dashboardUser where dashboardUser.company.id = ?#{principal.companyId}")
	List<DashboardUser> findDashboardUsersByCompanyIdAndSordOrder();
	
	
	@Query("select dashboardUser.user.id from DashboardUser dashboardUser where  dashboardUser.user.id in ?1 and dashboardUser.company.id = ?#{principal.companyId}")
	List<Long> findDashBoardUserIdsByUserIdInAndCompanyId(List<Long> userIds);

}
