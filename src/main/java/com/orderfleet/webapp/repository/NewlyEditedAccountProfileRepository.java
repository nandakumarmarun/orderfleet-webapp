package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;

/**
 * Spring Data JPA repository for the NewlyEditedAccountProfile entity.
 * 
 * @author Prashob Sasidharan
 * @since July 14, 2020
 */
public interface NewlyEditedAccountProfileRepository extends JpaRepository<NewlyEditedAccountProfile, Long> {

	Optional<NewlyEditedAccountProfile> findOneByPid(String pid);

	
	
	@Query("select newlyEditedAccountProfile from NewlyEditedAccountProfile newlyEditedAccountProfile where newlyEditedAccountProfile.company.id = ?1 and newlyEditedAccountProfile.user.id in ?2 and newlyEditedAccountProfile.createdDate between ?3 and ?4  and newlyEditedAccountProfile.accountStatus in ?5 and newlyEditedAccountProfile.activated = true order by newlyEditedAccountProfile.createdDate desc")
	List<NewlyEditedAccountProfile> findByCompanyIdAndUserIdInAndCreatefDateBetweenOrderAndAccountStatusByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate, List<AccountStatus> accountStatus);



	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE NewlyEditedAccountProfile ap SET ap.accountStatus = ?1  WHERE  ap.company.id = ?#{principal.companyId}  AND ap.pid = ?2")
	void updateAccountProfileStatus(AccountStatus accountStatus,String pid);
	

}
