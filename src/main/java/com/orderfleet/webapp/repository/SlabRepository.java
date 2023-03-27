package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.Slab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SlabRepository extends JpaRepository<Slab, Long> {

    @Query("select slab from Slab slab where slab.maximumUser = ?1 and slab.minimumUser =?2  and slab.company.id = ?3")
    Optional<Slab> findByMinimumUserAndMaximUserAndCompanyId(int maxUser, int minUser, Long companyId);

    @Query("select slab from Slab slab where lower(slab.company.legalName) lIKE lower(concat('%', :legalName,'%'))")
    List<Slab> findByCompanyName(@Param("legalName") String legalName);

    @Query("select slab from Slab slab where slab.company.id = ?1")
    List<Slab> findAllByCompanyId(long companyid);

    @Transactional
    @Modifying
    @Query("delete from Slab slab where slab.pid = ?1")
    void deleteByPid(String pid);

    @Query("select slab from Slab slab where slab.company.id in ?1")
   List<Slab> findAllByCompanyIdIn(List<Long> companyId);



}
