package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.VoucherNumberGenerator;

public interface VoucherNumberGeneratorRepository extends JpaRepository<VoucherNumberGenerator, Long> {

	Optional<VoucherNumberGenerator> findByCompanyIdAndPrefixIgnoreCase(Long id, String prefix);

	@Query("select voucherNumberGenerator from VoucherNumberGenerator voucherNumberGenerator where voucherNumberGenerator.company.id = ?#{principal.companyId}")
	List<VoucherNumberGenerator> findAllByCompanyId();

	Optional<VoucherNumberGenerator> findById(Long id);

	@Query("select vng from VoucherNumberGenerator vng where vng.user.pid = ?1 and vng.company.pid = ?2")
	List<VoucherNumberGenerator> findAllByUserAndCompany(String userPid, String companyPid);

	@Query("select vng from VoucherNumberGenerator vng where vng.document.pid = ?1 and vng.user.pid = ?2 and vng.company.pid = ?3")
	VoucherNumberGenerator findByDocumentAndUserAndCompany(String documentPid, String userPid, String companyPid);
	
	@Query("select vng from VoucherNumberGenerator vng where vng.user.pid = ?1 and vng.company.pid = ?2 and vng.document.pid = ?3")
	List<VoucherNumberGenerator> findAllByUserAndCompanyAndDocument(String userPid, String companyPid,String documentPid);
}
