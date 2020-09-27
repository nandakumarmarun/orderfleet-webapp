package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.UnitOfMeasure;

/**
 * Spring Data JPA repository for the UnitOfMeasure entity.
 *
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {

	Optional<UnitOfMeasure> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<UnitOfMeasure> findOneByPid(String pid);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId} and unitOfMeasure.activated=?1 ORDER BY unitOfMeasure.name ASC")
	List<UnitOfMeasure> findAllByCompanyId(boolean status);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId} and unitOfMeasure.activated=?1")
	Page<UnitOfMeasure> findAllByCompanyId(Pageable pageable, boolean status);

	@Query("select unitOfMeasure.name from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId}")
	List<String> findUnitOfMeasureNamesByCompanyId();

	List<UnitOfMeasure> findAllByCompanyPid(String companyPid);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId} Order By unitOfMeasure.name asc")
	List<UnitOfMeasure> findAllByCompanyOrderByName();

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId} and unitOfMeasure.activated = ?1 order By unitOfMeasure.name asc")
	Page<UnitOfMeasure> findAllByCompanyIdAndActivatedUnitOfMeasureOrderByName(boolean active, Pageable pageable);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?#{principal.companyId} and unitOfMeasure.activated = ?1 order By unitOfMeasure.name asc")
	List<UnitOfMeasure> findAllByCompanyIdAndDeactivatedUnitOfMeasure(boolean deactive);

	List<UnitOfMeasure> findByCompanyId(Long companyId);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.pid = ?1 order By unitOfMeasure.id asc")
	List<UnitOfMeasure> findAllByCompanyPidOrderByProductId(String companyPid);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.pid = ?1 and unitOfMeasure.pid in ?2 order By unitOfMeasure.id asc")
	List<UnitOfMeasure> findAllByCompanyPidAndUnitOfMeasurePidIn(String companyPid, List<String> unitOfMeasurePids);

	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?1 and UPPER(unitOfMeasure.name) in ?2")
	List<UnitOfMeasure> findByCompanyIdAndNameIgnoreCaseIn(Long id, List<String> names);

	UnitOfMeasure findFirstByCompanyId(Long id);
	
//	@Query("select unitOfMeasure from UnitOfMeasure unitOfMeasure where unitOfMeasure.company.id = ?1 order by unitOfMeasure.id asc LIMIT 1")
//	UnitOfMeasure findFirstByCompanyIdAsc(Long id);
	
	UnitOfMeasure findFirstByCompanyIdOrderById(Long id);

	List<UnitOfMeasure> findByCompanyIdAndNameIgnoreCaseIn(Long id, Set<String> uomNames);
	
}
