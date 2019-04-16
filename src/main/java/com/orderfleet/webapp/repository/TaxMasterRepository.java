package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TaxMaster;

/**
 * 
 * Spring Data JPA repository for the TaxMaster entity.
 *
 * @author Sarath
 * @since Aug 8, 2017
 *
 */
public interface TaxMasterRepository extends JpaRepository<TaxMaster, Long>{

	Optional<TaxMaster> findByCompanyIdAndVatNameIgnoreCase(Long id, String name);
	
	Optional<TaxMaster> findOneByPid(String pid);
	
	@Query("select taxMaster from TaxMaster taxMaster where taxMaster.company.id = ?#{principal.companyId}")
	List<TaxMaster> findAllByCompanyId();
	
	@Query("select taxMaster from TaxMaster taxMaster where taxMaster.company.id = ?1")
	List<TaxMaster> findAllByCompanyId(Long companyId);
	
	@Query("select taxMaster from TaxMaster taxMaster where taxMaster.company.id = ?1 and taxMaster.vatClass=?2 and taxMaster.vatPercentage=?3 ")
	TaxMaster findAllByCompanyIdAndVatClassAndVatPercentage(Long companyId,String vatClass ,double vatPercentage);
}
