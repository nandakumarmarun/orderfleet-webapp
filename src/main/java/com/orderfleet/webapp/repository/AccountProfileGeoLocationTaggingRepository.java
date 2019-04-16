package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountProfileGeoLocationTagging;

public interface AccountProfileGeoLocationTaggingRepository extends JpaRepository<AccountProfileGeoLocationTagging, Long>{

	@Query("select accountProfileGeoLocationTagging from AccountProfileGeoLocationTagging accountProfileGeoLocationTagging where accountProfileGeoLocationTagging.company.id = ?#{principal.companyId} and  accountProfileGeoLocationTagging.accountProfile.pid = ?1")
	List<AccountProfileGeoLocationTagging> findAllAccountProfileGeoLocationTaggingByAccountProfilePid(String pid);
	
	Optional<AccountProfileGeoLocationTagging> findOneByPid(String pid);
	
	@Query("select apGeoLocationTagging from AccountProfileGeoLocationTagging apGeoLocationTagging where apGeoLocationTagging.company.id = ?#{principal.companyId} and apGeoLocationTagging.accountProfile.pid in ?1 and apGeoLocationTagging.sendDate between ?2 and ?3 order by apGeoLocationTagging.sendDate DESC")
	List<AccountProfileGeoLocationTagging> findGeoLocationTaggingByAccountProfilePidInAndDateBetweenOrdeByDateDesc(List<String> accountPids, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select apGeoLocationTagging from AccountProfileGeoLocationTagging apGeoLocationTagging where apGeoLocationTagging.company.id = ?#{principal.companyId} and apGeoLocationTagging.user.id in ?1 and apGeoLocationTagging.accountProfile.pid in ?2 and apGeoLocationTagging.sendDate between ?3 and ?4 order by apGeoLocationTagging.sendDate DESC")
	List<AccountProfileGeoLocationTagging> findGeoLocationTaggingByUserIdInAccountProfilePidInAndDateBetweenOrdeByDateDesc(List<Long> userIds, List<String> accountPids, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select distinct apglt.pid, apglt.sendDate, apglt.user.id, apglt.user.firstName, ap.pid, ap.name, ap.latitude, ap.longitude, ap.location from AccountProfileGeoLocationTagging apglt JOIN AccountProfile ap ON apglt.accountProfile.id=ap.id where ap.activated = true and apglt.company.id = ?#{principal.companyId} and apglt.sendDate between ?1 and ?2 order by apglt.sendDate desc")
	List<Object[]> findByActivatedAndCompanyWithAccountProfileAndSentDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
	
}
