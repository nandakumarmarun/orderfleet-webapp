package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.orderfleet.webapp.domain.RouteCode;

@Repository
public interface RouteCodeRepository extends JpaRepository<RouteCode, Long>{
	
	@Query("select routeCode from RouteCode routeCode where company.id = ?#{principal.companyId} and routeCode.masterCode = ?1")
	Optional<RouteCode> findOneByRouteCode(String pid);
	
	@Query("select routeCode from RouteCode routeCode where company.id = ?#{principal.companyId}")
	List<RouteCode> findAllByCompanyId();
	
	@Query(value="select * from tbl_route_code where company_id=?1",nativeQuery = true)
	List<RouteCode> findAllByCompanyId(long compnayId);
	
	@Query(value="select * from tbl_route_code where account_profile_id=?1",nativeQuery = true)
	List<RouteCode> findAllByAccountId(long accountId);
	

}   
