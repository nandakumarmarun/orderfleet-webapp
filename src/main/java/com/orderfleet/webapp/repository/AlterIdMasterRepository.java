package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AlterIdMaster;
import com.orderfleet.webapp.domain.Company;

public interface AlterIdMasterRepository extends JpaRepository<AlterIdMaster, Long> {

	@Query("select alterIdMaster from AlterIdMaster alterIdMaster where alterIdMaster.masterName =?1 and alterIdMaster.company.id=?2 ")
	AlterIdMaster findByMasterName(String masterName,Long company_id );

	@Query("select alterIdMaster from AlterIdMaster alterIdMaster where alterIdMaster.masterName =?1 and alterIdMaster.company.id=?2 ")
	Optional<AlterIdMaster> findOnByMasterNameAndCompany(String masterName,Long companyId);

}
