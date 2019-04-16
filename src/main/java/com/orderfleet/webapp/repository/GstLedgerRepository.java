package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.GstLedger;

public interface GstLedgerRepository extends JpaRepository<GstLedger, Long> {
	
	@Query("select gstLedger from GstLedger gstLedger where gstLedger.company.id = ?1")
	List<GstLedger> findAllByCompanyId(Long companyId);
	
	@Query("select gstLedger from GstLedger gstLedger where gstLedger.company.id = ?1 and gstLedger.activated = ?2")
	List<GstLedger> findAllByCompanyIdAndActivated(Long companyId, boolean status);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE GstLedger gstLedger SET gstLedger.activated = ?1  WHERE  gstLedger.id in ?2")
	void updateByid(boolean activated, List<Long> ids);
	
	void deleteByCompanyId(Long companyId);

}
