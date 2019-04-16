package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DocumentPrintEmail;

public interface DocumentPrintEmailRepository extends JpaRepository<DocumentPrintEmail, Long> {

	@Query("select documentPrintEmail from DocumentPrintEmail documentPrintEmail where documentPrintEmail.company.pid = ?1")
	List<DocumentPrintEmail> findAllDocumentPrintEmailByCompanyId(String companyPid);
	
	Optional<DocumentPrintEmail> findOneByDocumentPidAndNameIgnoreCase(String pid,String name);
	
	List<DocumentPrintEmail> findByDocumentPid(String documentPid);
	
	@Query("select documentPrintEmail from DocumentPrintEmail documentPrintEmail where documentPrintEmail.id=?1")
	Optional<DocumentPrintEmail> findOneId(Long id);
	
	Optional<DocumentPrintEmail> findOneByDocumentPidAndPrintFilePathIgnoreCase(String pid,String printFilePath);
	
	@Query("select documentPrintEmail from DocumentPrintEmail documentPrintEmail where documentPrintEmail.company.id = ?#{principal.companyId} and documentPrintEmail.document.pid = ?1 and documentPrintEmail.name = ?2")
	Optional<DocumentPrintEmail> findOneByDocumentPidAndNamesIgnoreCase(String pid,String name);
	
}
