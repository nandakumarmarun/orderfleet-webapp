package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DistributorDealerAssociation;

public interface DistributorDealerProfileRepository extends JpaRepository<DistributorDealerAssociation,Long>{

	List<DistributorDealerAssociation> findAllByCompanyId(Long companyId);

	@Query("Select association from DistributorDealerAssociation association where association.company.id = ?#{principal.companyId} and association.dealer.pid = ?1")
	Optional<DistributorDealerAssociation> findByDistributorPid(String pid);
	
	
	

}
