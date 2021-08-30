package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.ClientAppLogFiles;
import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;

/**
 * repository for AccountGroupAccountProfile
 * 
 * @author Prashob Sasidharan
 * @since April 11, 2019
 */
public interface ClientAppLogFilesRepository extends JpaRepository<ClientAppLogFiles, Long> {

	Optional<ClientAppLogFiles> findOneByLogDateAndFileNameAndCompanyId(LocalDate currentDate, String originalFilename,
			Long id);

	@Query("select cl from ClientAppLogFiles cl where cl.company.id = ?#{principal.companyId} order by cl.logDate")
	List<ClientAppLogFiles> findAllByCompanyId();

	@Query("select cl from ClientAppLogFiles cl where cl.company.id = ?#{principal.companyId} and cl.createdDate between ?1 and ?2 order by cl.logDate")
	List<ClientAppLogFiles> findAllByCompanyIdAndCreatedDateDesc(LocalDateTime fromDate, LocalDateTime toDate);

}
