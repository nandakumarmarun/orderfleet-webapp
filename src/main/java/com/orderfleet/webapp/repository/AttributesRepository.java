package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AttributesRepository extends JpaRepository<Attributes, Long> {
       Optional<Attributes> findOneByPid(String pid);

       @Query("Select attr from Attributes attr where attr.pid in ?1")
       List<Attributes> findByAttributePidIn(List<String> questionPid);

       @Query("Select attr from Attributes attr where attr.pid in ?1")
       List<Attributes> findByAttributePidIn(Set<String> questionPid);


}
