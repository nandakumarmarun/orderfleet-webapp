package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserDevice;

/**
 * Spring Data JPA repository for the UserDevice entity.
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

	Optional<UserDevice> findByCompanyIdAndDeviceNameIgnoreCase(Long id, String name);

	Optional<UserDevice> findOneByPid(String pid);

	Optional<UserDevice> findByUserLoginAndActivatedTrue(String login);

	Optional<UserDevice> findBy(String pid);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and userDevice.user.pid in ?1 and activated=true")
	List<UserDevice> findAllByUserPidINAndActivatedTrue(List<String> userPids);
	
	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and userDevice.user.id in ?1 and activated=true")
	Set<Long> findByUserIdInAndActivatedTrue(List<Long> userIds);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?1 and userDevice.user.pid in ?2 and activated=true")
	List<UserDevice> findAllByCompanyAndUserPidINAndActivatedTrue(Long companyId, List<String> userPids);

	@Query("select userDevice from UserDevice userDevice where userDevice.user.id in ?1 and activated=true")
	List<UserDevice> findByUserIdInAndActivatedTrue(Set<Long> userIds);

	Optional<UserDevice> findByDeviceKeyAndActivatedTrue(String deviceKey);

	Optional<UserDevice> findOneByDeviceKeyAndUserLoginAndActivatedTrue(String deviceKey, String login);

	List<UserDevice> findByDeviceKeyAndUserPid(String deviceKey, String userPid);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId}")
	List<UserDevice> findAllByCompanyId();

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId}")
	Page<UserDevice> findAllByCompanyId(Pageable pageable);

	Page<UserDevice> findAllByActivatedTrue(Pageable pageable);

	List<UserDevice> findAllByActivatedTrue();

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and activated=true")
	List<UserDevice> findAllByCompanyIdAndActivatedTrue();

	@Query("select userDevice from UserDevice userDevice where userDevice.company.pid = ?1 Order By userDevice.activated desc,userDevice.lastModifiedDate desc")
	List<UserDevice> findAllUserDeviceByCompanyAndLastModifiedDate(String companyPid);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.pid = ?1 and userDevice.user.pid = ?2 Order By userDevice.activated desc,userDevice.lastModifiedDate desc")
	List<UserDevice> findAllUserDeviceByCompanyAndUserAndLastModifiedDate(String companyPid, String userPid);

	@Query("select userDevice from UserDevice userDevice  Order By userDevice.activated desc,userDevice.lastModifiedDate desc")
	List<UserDevice> findAllUserDeviceOrderByActivatedAndLastModifiedDate();

	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and activated=true and userDevice.user.id in ?1")
	List<UserDevice> findAllByCompanyIdAndActivatedTrueAndUserPidIn(List<Long> userPids);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.pid = ?1 and userDevice.activated = ?2")
	List<UserDevice> findAllUserDeviceByCompanyPidAndActivated(String companyPid, boolean activated);

	@Query("select userDevice from UserDevice userDevice where userDevice.company.pid = ?1 and userDevice.user.pid = ?2 and userDevice.activated = ?3 ")
	List<UserDevice> findAllUserDeviceByCompanyPidAndUserPidAndActivated(String companyPid, String userPid,
			boolean activated);

	@Query("select userDevice from UserDevice userDevice where userDevice.activated= ?1")
	List<UserDevice> findAllActivated(boolean activated);
	
	@Query("select userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and activated=true and userDevice.user.pid in ?1")
	List<UserDevice> findAllByCompanyIdAndActivatedAndUserPidIn(List<String> userPids);
	
	@Query("select distinct userDevice from UserDevice userDevice where userDevice.company.id = ?#{principal.companyId} and activated=true and userDevice.pid in ?1")
	List<UserDevice> findByCompanyIdAndActivatedAndUserDevicePidIn(List<String> userDevicePids);
}