package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation.KmSlabUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KmSlabRepository extends JpaRepository<KmSlab,Long> {

    void deleteByPid(String pid);

    Optional<KmSlab> findByPid(String pid);

    List<KmSlab> findAllByCompanyId(long comapanyId);

}
