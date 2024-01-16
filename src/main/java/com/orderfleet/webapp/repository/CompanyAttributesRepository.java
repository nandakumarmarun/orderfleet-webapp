package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyAttributes;
import com.orderfleet.webapp.web.rest.dto.CustomerAttributesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CompanyAttributesRepository extends JpaRepository<CompanyAttributes, Long> {
    List<CompanyAttributes> findByCompany(Company company);
    void deleteByCompanyPid(String companyPid);
    void deleteByDocumentPid(String documentPid);
    @Query("SELECT ca FROM CompanyAttributes ca WHERE ca.documentPid = :documentPid")
    List<CompanyAttributes> findAttributesByDocumentPid(@Param("documentPid") String documentPid);
    @Query("SELECT ca FROM CompanyAttributes ca WHERE ca.company.pid = :companyPid")
    List<CompanyAttributes> findAttributesByCompanyPid(@Param("companyPid") String companyPid);
    @Query("SELECT ca FROM CompanyAttributes ca WHERE ca.company.id = :companyId AND ca.documentPid = :documentPid")
    List<CompanyAttributes> findAttributesByCompanyIdAndDocumentPid(@Param("companyId") Long companyId, @Param("documentPid") String documentPid);

    @Query("select comp from CompanyAttributes comp where comp.attributes.pid in ?1 order by sort_order asc")
    List<CompanyAttributes> findCompanyAttributesByAttributesPidIn(Set<String> attributesPid);
@Query("select distinct comattr from CompanyAttributes comattr where comattr.company.id = ?#{principal.companyId} order by sort_order asc")
    List<CompanyAttributes> findAllByCompanyId();

    List<CompanyAttributes> findByCompanyId(Long id);

}
