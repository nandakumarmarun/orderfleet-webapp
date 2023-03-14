package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.Industry;

/**
 * Spring Data JPA repository for the Company entity.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {

	Optional<Company> findByIdAndLegalName(Long id, String name);
	
	Optional<Company> findByLegalName(String name);

	Optional<Company> findOneByPid(String pid);

	Page<Company> findAll(Pageable pageable);

	List<Company> findAllCompaniesByActivatedTrue();
	
	@Query("select company.legalName from Company company where company.activated = true order by company.legalName asc")
	List<String> findAllCompanyNameByActivatedTrueOrderByCreatedDate();

	@Query("select company.legalName from Company company where company.activated = false order by company.legalName asc")
	List<String> findAllCompanyNameByDeActivatedTrueOrderByCreatedDate();
	
	@Query("select company from Company company order by company.legalName asc")
	List<Company> findAllCompanySortedByName();

	@Query("select company.logo from Company company where company.id = ?#{principal.companyId}")
	byte[] findtCurrentUserEmployeeProfileImage();

	Optional<Company> findByAlias(String alias);
	
	Optional<Company> findByEmail(String email);

	@Query("select company from Company company where company.industry=?1  order by company.legalName asc")
	List<Company> findAllCompanyByIndustrySortedByName(Industry industry);
	
	@Transactional
	@Modifying
	@Query("update Company set apiUrl = ?1 where pid = ?2 ")
	void updateCompanyUrlByPid(String apiUrl, String pid);
	
	@Query("select company.apiUrl from Company company where company.pid =?1")
	String findApiUrlByCompanyPid(String companyPid);
	
	@Query("select company from Company company where company.onPremise = true and company.activated = true")
	List<Company> findAllByOnPremiseTrue();
	
	Optional<Company> findById(Long companyId);
	
    @Query("select count(company) from Company company where company.activated= true")
	Integer findcountOfActiveCompany();
    
    @Query("select count(company) from Company company where company.activated= false")
    Integer findcountOfDeactiveCompany();

    @Query("select company from Company company where company.activated = ?1 order by company.legalName asc")
	List<Company> findAllCompanyByActivated(boolean data);

    @Query("select count(company) from Company company where company.activated= ?1")
      int findCountOfActiveCompany(boolean data);
   
    

}
