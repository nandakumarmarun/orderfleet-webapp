package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.domain.UserVehicleAssociation;
import com.orderfleet.webapp.domain.Vehicle;

public interface UserVehicleAssociationRepository extends JpaRepository<UserVehicleAssociation, Long>  {

	@Query("select userVehicleAssociation.vehicle from UserVehicleAssociation userVehicleAssociation where userVehicleAssociation.employeeProfile.pid = ?1 ")
	Vehicle findVehicleByUserPid(String employeePid);
	
	void deleteByemployeeProfilePid(String employeePid);
	
	@Query("select userVehicleAssociation from UserVehicleAssociation userVehicleAssociation where userVehicleAssociation.employeeProfile.pid = ?1 ")
	Optional<UserVehicleAssociation> findByVehicleAndUserPid(String employeePid);
	
}
