package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.GSTProductGroup;

/**
 * Spring Data JPA repository for the GSTProductGroup entity.
 *
 * @author Sarath
 * @since Jul 11, 2017
 *
 */
public interface GSTProductGroupRepository extends JpaRepository<GSTProductGroup, Long> {

	@Query("select gstProductGroup from GSTProductGroup gstProductGroup where gstProductGroup.company.id = ?#{principal.companyId}")
	List<GSTProductGroup> findAllByCompanyId();

}
