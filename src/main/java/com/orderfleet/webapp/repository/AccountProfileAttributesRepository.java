package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.AccountProfileAttributes;

import com.orderfleet.webapp.web.rest.dto.AccountProfileAttributesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountProfileAttributesRepository extends JpaRepository<AccountProfileAttributes, Long> {
    @Query("SELECT attr FROM AccountProfileAttributes attr WHERE attr.accountProfile.pid = ?1 order by attr.sortOrder asc")
    List<AccountProfileAttributes> findAccountProfileAttributesByAccountProfilePid(String accountProfilePid);
    @Query("SELECT attr FROM AccountProfileAttributes attr WHERE attr.accountProfile.pid in ?1 order by attr.sortOrder asc")
    List<AccountProfileAttributes> findAccountProfileAttributesByAccountProfilePidIn(List<String> accountProfilePids);
    @Query("SELECT attr FROM AccountProfileAttributes attr WHERE attr.attributesPid in ?1")
    List<AccountProfileAttributes> findAccountProfileAttributesByAttributeIdIn(List<String> attributeIds);
}
