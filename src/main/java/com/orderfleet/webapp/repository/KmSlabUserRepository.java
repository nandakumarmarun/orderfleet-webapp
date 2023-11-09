package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation.KmSlabUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface KmSlabUserRepository extends JpaRepository<KmSlabUser, Long> {

    @Query("select kmSlabUser from KmSlabUser kmSlabUser where kmSlabUser.company.id = ?1 and kmSlabUser.kmSlab.pid = ?2")
    List<KmSlabUser> findAllByCompanyId(Long companyId,String kmSlabPid);

    @Query("select kmSlabUser.kmSlab from KmSlabUser kmSlabUser where kmSlabUser.company.id = ?1 and kmSlabUser.user.pid = ?2")
    List<KmSlab> findAllKmSlabsByCompanyIdAndUserPidInAndSlabPid(Long companyId, String kmSlabUserPid);


    @Query("select kmSlabUser.id from KmSlabUser kmSlabUser where kmSlabUser.company.id = ?1 and kmSlabUser.kmSlab.pid = ?2")
    List<Long> findAllByCompanyIdAndUserPidInAndSlabPid(Long companyId,String kmSlabUserPid);


    @Transactional
    @Modifying
    @Query("delete from KmSlabUser kmSlabUser where kmSlabUser.company.id = ?1 and kmSlabUser.id in ?2")
    void deleteAllByCompanyIdAndIdIn(Long companyId,List<Long> kmSlabid);

}
